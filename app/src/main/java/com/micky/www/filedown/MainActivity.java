package com.micky.www.filedown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.micky.www.filedownlibrary.DownloadConfig;
import com.micky.www.filedownlibrary.DownloadHelper;
import com.micky.www.filedownlibrary.DownloadListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,DownloadListener {

    private static final String TAG = "MainActivity";
    private ProgressBar mPrb;
    private TextView mPercent;
    private TextView mStart;
    private TextView mPause;
    private TextView mResume;
    private TextView mDelete;
    private Button mClean;
    private RecyclerView mRecyclerView;
    private DownloadHelper mHelper;
    private String url = "http://upos-hz-mirrorwcsu.acgvideo.com/upgcxcode/90/64/67206490/67206490-1-208.mp4?ua=tvproj&deadline=1544946636&gen=playurl&nbs=1&oi=2501663261&os=wcsu&platform=tvproj&trid=02ffb31b9a9144778a121dab721e31db&uipk=5&upsig=0af04bbb1ebae1aae0c2fb22fda140ec";

    private LinearLayoutManager layoutManager;
    private List<ListBean> mList;
    private FileListAdapter mAdapter;
    // 存储每个view的位置
    private Map<String,Integer> map ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrb = findViewById(R.id.progress);
        mPercent = findViewById(R.id.percent);
        mStart = findViewById(R.id.start);
        mPause = findViewById(R.id.pause);
        mResume = findViewById(R.id.resume);
        mDelete = findViewById(R.id.delete);
        mClean = findViewById(R.id.clean);
        mRecyclerView = findViewById(R.id.recyclerview);

        mStart.setOnClickListener(this);
        mPause.setOnClickListener(this);
        mResume.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        mClean.setOnClickListener(this);

        mHelper = DownloadHelper.getInstance();
        mHelper.registerListener(this);

        mList = new ArrayList<>();
        map = new HashMap<>();
        mAdapter = new FileListAdapter(mList,this)
        {
            @Override
            public void onBindViewHolder(FileListViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                String url = mList.get(position).getUrl();
                Log.d(TAG,"onBindViewHolder："+url + "; 位置:"+position);
                map.put(url,position);
            }
        };
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setListener(new ListItemOnClickerListener() {
            @Override
            public void onItemClicker(View view, int position) {
                String url = mList.get(position).getUrl();
                int downStatus = mList.get(position).getDownStatus();
                if (downStatus == DownloadConfig.STATUS_PAUSE )
                {
                   // 恢复下载
                   mHelper.resume(url);
                   return;
                }
                if (downStatus == DownloadConfig.STATUS_DOWNNING)
                {
                    // 暂停下载
                    mHelper.pause(url);
                    return;
                }
                mHelper.start(url);
            }

            @Override
            public void onItemLongClicker(View view, int position) {

            }
        });

        addTest();
    }

    @Override
    public void onClick(View view) {
       switch (view.getId())
       {
           case R.id.start:
               mHelper.start(url);
               //startService(new Intent(FileDown.getInstances(),DownloadManngerService.class));
               break;
           case R.id.pause:
               mHelper.pause(url);
               break;
           case R.id.resume:
               mHelper.resume(url);
               break;
           case R.id.delete:
               mHelper.delete(url);
               break;
           case R.id.clean:
               //mHelper.cleanDataBase();
               break;
       }
    }

    /**
     * 添加测试数据
     */
    private void addTest()
    {
       ListBean l1 = new ListBean("https://image-hroot.oss-cn-hangzhou.aliyuncs.com/year201811/image-201811151103153554568397/image/file_181120154517203444mp4temp_(00001).jpg",
               "战略落地，人才先行-营销分总战略预备队实","https://video-hroot.oss-cn-hangzhou.aliyuncs.com/year201811/video-201811151103153554568397/video/file_181120154517203444.mp4",
               0,"0kb/s",DownloadConfig.STATUS_DEFAULT);
        ListBean l2 = new ListBean("https://image-hroot.oss-cn-hangzhou.aliyuncs.com/year201811/image-201811151103153554568397/image/file_181114142849697263mp4temp_(00001).jpg",
                "新技术如何赋能未来超大型组织","https://video-hroot.oss-cn-hangzhou.aliyuncs.com/year201811/video-201811151103153554568397/video/file_181114142849697263.mp4",
                0,"0kb/s",DownloadConfig.STATUS_DEFAULT);
        ListBean l3 = new ListBean("https://image-hroot.oss-cn-hangzhou.aliyuncs.com/year201811/image-201811151103153554568397/image/file_181114111345707015mp4temp_(00001).jpg",
                "现在布局，打造未来的员工团队","https://video-hroot.oss-cn-hangzhou.aliyuncs.com/year201811/video-201811151103153554568397/video/file_181114111345707015.mp4",
                0,"0kb/s",DownloadConfig.STATUS_DEFAULT);
        ListBean l4 = new ListBean("https://image-hroot.oss-cn-hangzhou.aliyuncs.com/year201811/image-201811151103153554568397/image/file_181113111247242667mp4temp_(00001).jpg",
                "新税改 薪未来","https://video-hroot.oss-cn-hangzhou.aliyuncs.com/year201811/video-201811151103153554568397/video/file_181113111247242667.mp4",
                0,"0kb/s",DownloadConfig.STATUS_DEFAULT);

        mList.add(l1);
        mList.add(l2);
        mList.add(l3);
        mList.add(l4);
        mAdapter.notifyDataSetChanged();
    }



    @Override
    public void start(String url) {
        Log.d(TAG,"开始下载："+url);
        findItemBean(url,DownloadConfig.STATUS_START,0,0+"");
    }

    @Override
    public void pause(String url) {
        Log.d(TAG,"暂停下载："+url);
        findItemBean(url,DownloadConfig.STATUS_PAUSE,0,0+"");
    }

    @Override
    public void progress(long read, long contentLength, boolean done, String url, long speed) {
         int progress = (int) (100 * read / contentLength);
       // mPrb.setProgress(progress);
       // mPercent.setText(progress+"%   speed:"+((speed > 1024)?(speed / 1024*1.0)+"m/s":speed+"kb/s")+"；kb速度："+speed);
        findItemBean(url,DownloadConfig.STATUS_START,progress,speed+"kb/s");
    }

    @Override
    public void wait(String url) {
        Log.d(TAG,"等待下载："+url);
        findItemBean(url,DownloadConfig.STATUS_WAIT,0,0+"");
    }

    @Override
    public void complete(String url, File file) {
        mPercent.setText("已完成");
        findItemBean(url,DownloadConfig.STATUS_COMPLETE,100,0+"");
    }

    @Override
    public void error(String url, String msg) {
        Log.d(TAG,"下载错误："+url+"；msg:"+msg);
        findItemBean(url,DownloadConfig.STATUS_ERROR,0,0+"");
    }

    @Override
    public void delete(String url) {
        Log.d(TAG,"下载删除："+url);
        findItemBean(url,DownloadConfig.STATUS_DELETE,0,0+"");
    }

    private void findItemBean(String url,int status,int percent,String speed )
    {
        int position = findPosition(url);
        ListBean listBean= mList.get(position);
        listBean.setDownStatus(status);
        listBean.setKbSpeed(speed);
        listBean.setPercent(percent == 0 ? listBean.getPercent(): percent);
        mAdapter.notifyItemChanged(position);
       // mList.indexOf()
    }

    private int findPosition(String url)
    {
        return map.get(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper.unRegisterListener();
    }
}
