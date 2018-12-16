package com.micky.www.filedownlibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import java.io.File;

/**
 * @ClassName DownloadHelper
 * @Description 下载帮助类
 * @Author Micky
 * @Date 2018/12/15 20:10
 * @Version 1.0
 */
public class DownloadHelper {
    // 帮助类对象
    private static DownloadHelper mInstance;
    // 监听Listener
    protected DownloadListener progressListener;

    private DownloadHelper()
    {

    }

    public static DownloadHelper getInstance()
    {
       if (mInstance == null)
       {
           synchronized (DownloadHelper.class)
           {
               mInstance = new DownloadHelper();
           }
       }
       return mInstance;
    }

    /**
     * 注册监听
     * @param listener  回掉监听
     */
    public void registerListener(DownloadListener listener)
    {
        this.progressListener = listener;
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadConfig.RECEIVER_START);
        filter.addAction(DownloadConfig.RECEIVER_PASUE);
        filter.addAction(DownloadConfig.RECEIVER_RESUME);
        filter.addAction(DownloadConfig.RECEIVER_WAIT);
        filter.addAction(DownloadConfig.RECEIVER_COMPLETE);
        filter.addAction(DownloadConfig.RECEIVER_DOWNNING);
        filter.addAction(DownloadConfig.RECEIVER_ERROR);
        filter.addAction(DownloadConfig.RECEIVER_DELETE);
        LocalBroadcastManager.getInstance(FileDown.getInstances()).registerReceiver(receiver,filter);
    }

    /**
     * 解除监听，页面销毁时一定要调用
     */
    public void  unRegisterListener()
    {
        this.progressListener = null;
        // 解除广播
        LocalBroadcastManager.getInstance(FileDown.getInstances()).unregisterReceiver(receiver);
    }

    /**
     *  下载的广播接收器
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取action，根据不同的状态处理相关操作
            String action = intent.getAction();
            // 获取下载链接
            String url = intent.getStringExtra(DownloadConfig.URL);
            if (action.equals(DownloadConfig.RECEIVER_START))
            {
                // 下载开始
                progressListener.start(url);
            }else if (action.equals(DownloadConfig.RECEIVER_PASUE))
            {
                // 下载暂停
                progressListener.pause(url);

            }else if (action.equals(DownloadConfig.RECEIVER_RESUME))
            {
                // 恢复下载

            }else if (action.equals(DownloadConfig.RECEIVER_DOWNNING))
            {
                // 下载中
                long read = intent.getLongExtra(DownloadConfig.READ,0);
                long total = intent.getLongExtra(DownloadConfig.TOTAL,0);
                long speed = intent.getLongExtra(DownloadConfig.SPEED,0);
                boolean isComplete = intent.getBooleanExtra(DownloadConfig.IS_COMPLETE,false);
                progressListener.progress(read,total,isComplete,url,speed);
            }else if (action.equals(DownloadConfig.RECEIVER_COMPLETE))
            {
                // 下载完成
                String localPath = intent.getStringExtra(DownloadConfig.LOCAL_PATH);
                File file;
                if (TextUtils.isEmpty(localPath))
                {
                    file = null;
                }else
                {
                    file = new File(localPath);
                }
                progressListener.complete(url,file);
            }else if (action.equals(DownloadConfig.RECEIVER_WAIT))
            {
                // 下载等待
                progressListener.wait(url);
            }else if (action.equals(DownloadConfig.RECEIVER_ERROR))
            {
                // 下载错误
                String errorMsg = intent.getStringExtra(DownloadConfig.ERROR_MESSAGE);
                progressListener.error(url,errorMsg);
            }else if (action.equals(DownloadConfig.RECEIVER_DELETE))
            {
                // 删除下载
                progressListener.delete(url);
            }


        }
    };

    /**
     * 开始下载
     * @param url
     */
    public void start(String url)
    {
       startService(url,DownloadConfig.ACTION_START);
    }

    /**
     * 暂停下载
     * @param url 请求连接
     */
    public void pause(String url)
    {
        startService(url,DownloadConfig.ACTION_PAUSE);
    }

    /**
     *  恢复下载
     * @param url
     */
    public void resume(String url)
    {
        startService(url,DownloadConfig.ACTION_RESUME);
    }

    /**
     * 重新下载
     * @param url
     */
    public void restart(String url)
    {
        startService(url,DownloadConfig.ACTION_RESTART);
    }

    /**
     * 删除下载
     * @param url
     */
    public void delete(String url)
    {
        startService(url,DownloadConfig.ACTION_DELETE);
    }




    /**
     * 启动服务
     * @param url
     * @param action
     */
    protected void startService(String url,int action)
    {
        Intent intent = new Intent(FileDown.getInstances(),DownloadManngerService.class);
        intent.putExtra(DownloadConfig.ACTION,action);
        intent.putExtra(DownloadConfig.URL,url);
        FileDown.getInstances().startService(intent);
    }

}
