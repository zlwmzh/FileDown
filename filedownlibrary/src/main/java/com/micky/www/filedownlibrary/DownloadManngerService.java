package com.micky.www.filedownlibrary;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Micky on 2018/12/14.
 * 下载管理服务类
 */

public class DownloadManngerService extends Service{
    private static final String TAG = "DownloadManngerService";

    // 同时下载的最大线程数量
    protected int maxThreadCount = DownloadConfig.MAX_DOWN_THREAD_COUNT;
    // 当前正在下载的线程数量
    protected int mCurrentThreadCount = 0;
    // 存放处理进行中的任务的map集合,url做为键
    protected Map<String,DownloadManager> mapThread;
    // 存放代下载任务的url集合
    protected Map<String,String> mWaitThread;

    @Override
    public void onCreate() {
        super.onCreate();
        // 执行初始化操作
        init();
        Log.d(TAG,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
        {
           return super.onStartCommand(intent, flags, startId);
        }
        // 获取相关动作
        int action = intent.getIntExtra(DownloadConfig.ACTION,DownloadConfig.ACTION_DEFAULT);
        // 获取相关连接
        String url = intent.getStringExtra(DownloadConfig.URL);
        // 处理相关响应
        respondAction(action,url);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *  初始化操作
     */
    protected void init()
    {
       // 初始化话map
        mapThread = new HashMap<>(maxThreadCount);
        mWaitThread = new HashMap<>();
    }

    /**
     *  处理相关操作：开始下载、暂停下载、恢复下载、重新下载、删除下载
     * @param action 执行动作
     * @param url 下载连接
     */
    protected void respondAction(int action, String url)
    {
        switch (action)
        {
            case DownloadConfig.ACTION_START:   // 开始下载
                start(url);
                break;
            case DownloadConfig.ACTION_PAUSE:   // 暂停下载
                pause(url);
                break;
             case DownloadConfig.ACTION_RESUME: // 恢复下载
                resume(url);
                break;
             case DownloadConfig.ACTION_RESTART:// 重新下载

                break;
             case DownloadConfig.ACTION_DELETE: // 删除下载
                delete(url);
                break;
             default:

                break;
        }
    }

    /**
     * 开始下载
     * @param url 下载连接
     */
    protected void start(String url)
    {
        mCurrentThreadCount = mapThread.size();
       if (mCurrentThreadCount >= maxThreadCount)
       {
           // 当前进行的线程数量 大于 最大允许进行的最大线程
           // 下载任务需要等待
           listener.wait(url);
           // 加入等待队列
           mWaitThread.put(url,url);
           return;
       }
        DownloadManager downloadManager = null;
        // 实例化DownloadManager对象
        if (mapThread.containsKey(url))
        {
            downloadManager = mapThread.get(url);
        }else
        {
            downloadManager = DownloadManager.getInstance();
            // 加入到下载队列
            mapThread.put(url,downloadManager);
        }
       // 开始下载
       downloadManager.setProgressListener(listener);
       downloadManager.start(url);

       // 当前任务数+1
       mCurrentThreadCount ++;
    }

    /**
     * 暂停任务
     * @param url
     */
    protected void pause(String url)
    {
        // 获取当前任务的下载管理类
        DownloadManager downloadManager = mapThread.get(url);
        if (downloadManager != null)
        {
            downloadManager.pause();
        }
    }

    /**
     * 恢复下载
     * @param url 下载链接
     */
    protected void resume(String url)
    {
        // 获取当前任务的下载管理类
        DownloadManager downloadManager = mapThread.get(url);
        if (downloadManager != null)
        {
            downloadManager.resume();
        }
    }


    /**
     * 删除下载
     * @param url 下载链接
     */
    public void delete(String url)
    {
        // 获取当前任务的下载管理类
        DownloadManager downloadManager = mapThread.get(url);
        if (downloadManager != null)
        {
            // 执行删除操作
            downloadManager.delete();
        }
    }

    DownloadListener listener = new DownloadListener() {
        @Override
        public void start(String url) {
            // 开始下载
            sendMessage(DownloadConfig.STATUS_START,url,0,0,false,"","",0);
        }

        @Override
        public void pause(String url) {
           // 暂停下载
           nextTask(url,DownloadConfig.STATUS_PAUSE);
           sendMessage(DownloadConfig.STATUS_PAUSE,url,0,0,false,"","",0);

        }

        @Override
        public void progress(long read, long contentLength, boolean done, String url, long speed) {
           // 回掉进度
          //  Log.d(TAG,"已下载："+read+"；总大小："+contentLength);
            sendMessage(DownloadConfig.STATUS_DOWNNING,url,read,contentLength,false,"","",speed);

        }

        @Override
        public void wait(String url) {
           // 等待下载
            sendMessage(DownloadConfig.STATUS_WAIT,url,0,0,false,"","",0);


        }

        @Override
        public void complete(String url, File file) {
           // 下载完成
           nextTask(url,DownloadConfig.STATUS_COMPLETE);
           sendMessage(DownloadConfig.STATUS_COMPLETE,url,0,0,true,file.getAbsolutePath(),"",0);

        }

        @Override
        public void error(String url, String msg) {
          //  下载错误
          nextTask(url,DownloadConfig.STATUS_ERROR);
          sendMessage(DownloadConfig.STATUS_ERROR,url,0,0,false,"",msg,0);

        }

        @Override
        public void delete(String url) {
            nextTask(url,DownloadConfig.STATUS_DELETE);
            sendMessage(DownloadConfig.STATUS_DELETE,url,0,0,false,"","",0);
        }
    };

    /**
     * 发送消息
     * @param downStatus  下载状态
     * @param url 下载连接
     * @param read 已下载
     * @param contentLength 总大小
     * @param done 是否下载完成
     * @param localPath
     * @param errMsg 错误消息
     * @param speed 下载速度 kb/s
     */
    protected void sendMessage(int downStatus, String url,long read, long contentLength, boolean done, String localPath, String errMsg ,
                               long speed)
    {
        Intent intent = new Intent();
        intent.putExtra(DownloadConfig.URL,url);
        switch (downStatus)
        {
            case DownloadConfig.STATUS_START:   // 开始下载
                intent.setAction(DownloadConfig.RECEIVER_START);
                break;
            case DownloadConfig.STATUS_PAUSE:   // 暂停下载
                intent.setAction(DownloadConfig.RECEIVER_PASUE);
                break;
            case DownloadConfig.STATUS_RESUME:  // 恢复下载
                intent.setAction(DownloadConfig.RECEIVER_RESUME);
                break;
            case DownloadConfig.STATUS_WAIT:    // 等待下载
                intent.setAction(DownloadConfig.RECEIVER_WAIT);
                break;
            case DownloadConfig.STATUS_DOWNNING:  // 下载中
                intent.setAction(DownloadConfig.RECEIVER_DOWNNING);
                // 传递已下载的进度
                intent.putExtra(DownloadConfig.READ,read);
                // 传递总大小
                intent.putExtra(DownloadConfig.TOTAL,contentLength);
                // 传输速度
                intent.putExtra(DownloadConfig.SPEED,speed);
                // 是否下载完成
                intent.putExtra(DownloadConfig.IS_COMPLETE,done);
                break;
            case DownloadConfig.STATUS_COMPLETE:  // 下载完成
                intent.setAction(DownloadConfig.RECEIVER_COMPLETE);
                intent.putExtra(DownloadConfig.LOCAL_PATH,localPath);
                break;
            case DownloadConfig.STATUS_ERROR:   // 下载错误
                intent.setAction(DownloadConfig.RECEIVER_ERROR);
                intent.putExtra(DownloadConfig.ERROR_MESSAGE,errMsg);
                break;
            case DownloadConfig.STATUS_DELETE:
                intent.setAction(DownloadConfig.RECEIVER_DELETE);
                break;
            default:

                break;
        }
        // 发送本地广播
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
       // sendBroadcast(intent);
    }


    /**
     * 执行下一个任务
     * @param url
     * @param downStatus 下载状态
     */
    protected void nextTask(String url, int downStatus)
    {
        // 如果是下载完成或者是下载错误，从已下载集合中移除暂停或者下载完成的任务
        if (downStatus == DownloadConfig.STATUS_COMPLETE || downStatus == DownloadConfig.STATUS_ERROR
                || downStatus == DownloadConfig.STATUS_DELETE)
        {
            mapThread.remove(url);
        }
        // 处理排队的任务
        String nextUrl = getKeyOrNull(mWaitThread);
        if (TextUtils.isEmpty(nextUrl))
        {
            // 未取出第一个元素
            // 全部任务完成，或只有三个任务
            // TODO
            return;
        }
        // 如果是暂停状态，且后面有排队的任务，从进行中任务移除暂停的任务
        if (downStatus == DownloadConfig.STATUS_PAUSE)
        {
            mapThread.remove(url);
        }
        // 从待下载集合中移除
        mWaitThread.remove(nextUrl);
        // 开始执行下一个任务
        start(nextUrl);
    }


    /**
     * 获取第一个待下载的请求
     * @param map
     * @return
     */
    private String getKeyOrNull(Map<String, String> map) {
        String obj = null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            obj = entry.getKey();
            if (obj != null) {
                break;
            }
        }
        return  obj;
    }

}
