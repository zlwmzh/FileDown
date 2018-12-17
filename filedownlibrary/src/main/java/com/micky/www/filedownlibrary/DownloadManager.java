package com.micky.www.filedownlibrary;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.greendao.gen.DownloadInfoDao;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Micky on 2018/12/12.
 *  下载管理类
 */

public class DownloadManager implements DownloadProgressListener{
    private static final String TAG = "DownloadManager";

    protected DownloadListener progressObserver;
    protected DownloadInfo info;
    protected DownLoadService service;

    // 下载任务的网络请求操作对象
    private Disposable disposable;
    // 轮询操作的对象
    private Disposable mResearchProgress;
    // 当前状态
    private int mDownStatus = DownloadConfig.STATUS_DEFAULT;

    protected int mSpeedRefreshUiTime = DownloadConfig.SPEED_REFRESH_UI_TIME ;
    // 文件存储路径
    protected String mSaveFilePath = Environment.getExternalStorageDirectory() +
            File.separator +"mickydown"+File.separator;
    private DownloadManager()
    {
       // delayProgress();
    }

    /**
     * 获取实例
     * @return
     */
    public static DownloadManager getInstance()
    {
        return new DownloadManager();
    }

    @Override
    public void progress(long read, long contentLength, final boolean done) {
       // Log.d(TAG, "progress : " + "read = " + read + "contentLength = " + contentLength);
        // 该方法仍然是在子线程，如果想要调用进度回调，需要切换到主线程，否则的话，会在子线程更新UI，直接错误
        // 如果断点续传，重新请求的文件大小是从断点处到最后的大小，不是整个文件的大小，info中的存储的总长度是
        // 整个文件的大小，所以某一时刻总文件的大小可能会大于从某个断点处请求的文件的总大小。此时read的大小为
        // 之前读取的加上现在读取的
        if (info.getContentLength() > contentLength) {
            // 继续上次断点续传
            read = read + (info.getContentLength() - contentLength);
        } else {
            //  重新开始一个下载
            info.setContentLength(contentLength);
        }
        // 设置已经下载的大小
        info.setReadLength(read);
        // 设置是否完成
        info.setIsComplete(done);
        // 设置状态下载中
        mDownStatus = DownloadConfig.STATUS_DOWNNING;
        // 通过RxJava的方法，将回掉结果转接到主线程，防止刷新UI时崩溃
        /*Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (progressObserver != null)
                {
                    progressObserver.progress(info.getReadLength(), info.getContentLength(), done, info.getUrl(),speed);
                }
            }
        });*/
    }

    /**
     * 设置保存的位置
     * @param path 本地路径
     */
    public void setSavePath(String path)
    {
        if (!TextUtils.isEmpty(path))
        {
            this.mSaveFilePath = path;
        }
    }

    /**
     * 开始下载
     * @param url
     */
    public void start(String url)
    {
        // 判断链接是否已经在下载
        if (mDownStatus == DownloadConfig.STATUS_DOWNNING )
        {
            // 正在下载中
            Log.d(TAG,"正在下载中，请勿重复添加");
            return;
        }
        // 首先查找此链接数据库中是否已经存在对应的下载
        info = FileDown.getFileDown().getDaoSession().getDownloadInfoDao().queryBuilder().where(DownloadInfoDao.Properties.Url.eq(url)).unique();
        if (info == null)
        {
            info = new DownloadInfo();
        }

        // 判断是否已经下载完成
        boolean isComplete = info.getIsComplete();
        if (isComplete)
        {
            mDownStatus = DownloadConfig.STATUS_COMPLETE;
            // 回掉下载完成 不在继续下载
            if (progressObserver != null)
            {
                progressObserver.complete(url, new File(mSaveFilePath+getFileName(url)));
            }
            return;
        }
        // 设置本地保存路径
        info.setLocalPath(mSaveFilePath+getFileName(url));
        // 设置下载路径
        info.setUrl(url);
        // 拦截器
        DownloadInterceptor interceptor = new DownloadInterceptor(this);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置连接超时时间为8s
        builder.connectTimeout(8 , TimeUnit.SECONDS);
        // 添加拦截器
        builder.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(UrlUtils.getBasUrl(url))
                .build();
        if (service == null)
        {
            service = retrofit.create(DownLoadService.class);
            info.setService(service);
        }else
        {
            // 保存的实例
            info.setService(service);
        }
        // 开启轮询
        delayProgress();
        // 开始下载
        download();
    }

    /**
     *  开始下载
     */
    protected void download()
    {
        Log.d(TAG,"info："+info);
        service.download("bytes=" + info.getReadLength() + "-",info.getUrl())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenNetworkException())
                .map(new Function<ResponseBody, DownloadInfo>() {
                    @Override
                    public DownloadInfo apply(@NonNull ResponseBody responseBody) throws Exception {
                        //写入文件
                        FileUtil.writeCache(responseBody, new File(info.getLocalPath()), info);
                        return info;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<DownloadInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                        mDownStatus = DownloadConfig.STATUS_START;
                        // 初始化开始下载的时间和已经下载的数据，计算速度
                        lastTimeStamp = System.currentTimeMillis();
                        lastRead = info.getReadLength();

                        progressObserver.start(info.getUrl());
                    }

                    @Override
                    public void onNext(@NonNull DownloadInfo downloadInfo) {
                        Log.d(TAG,"onNext："+downloadInfo);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG,"onError："+e.getMessage());
                        // 下载错误，保存当前的info
                        saveInfoToDb();
                        mDownStatus = DownloadConfig.STATUS_ERROR;
                        progressObserver.error(info.getUrl(),e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete");
                        // 下载完成，设置标识为已完成状态
                        mDownStatus= DownloadConfig.STATUS_COMPLETE;
                        if (progressObserver != null)
                        {
                            progressObserver.complete(info.getUrl(),new File(info.getLocalPath()));
                        }
                        info.setIsComplete(true);
                        saveInfoToDb();
                    }
                });

    }

    /**
     * 暂停下载
     */
    public void pause()
    {
        if (mDownStatus == DownloadConfig.STATUS_PAUSE)
        {
            return;
        }

        if (disposable != null)
        {
            disposable.dispose();
            // 暂停时需要保持相关信息
            saveInfoToDb();
            mDownStatus = DownloadConfig.STATUS_PAUSE;
            progressObserver.pause(info.getUrl());
        }
    }

    /**
     *  恢复下载
     */
    public void resume()
    {
        if (mDownStatus != DownloadConfig.STATUS_PAUSE)
        {
            return;
        }
        // 开启轮询
        delayProgress();
        download();
        //mDownStatus = DownloadConfig.STATUS_RESUME;
    }

    public void delete()
    {
        if (mDownStatus == DownloadConfig.STATUS_DELETE)
        {
            return;
        }
        // 首先暂停下载
        pause();
        // 删除下载信息
        deleteDownInfo();
        // 删除已经下载的文件
        FileUtil.deleteFile(info.getLocalPath());
        mDownStatus = DownloadConfig.STATUS_DELETE;
        progressObserver.delete(info.getUrl());
    }

    /**
     * 获取文件名
     * @param pathandname
     * @return
     */
    public String getFileName(String pathandname){
        int start=pathandname.lastIndexOf("/");
        if(start!=-1){
            return pathandname.substring(start+1,pathandname.length());
        }else{
            return null;
        }
    }

    /**
     *  保存当前信息到数据库
     */
    protected void saveInfoToDb()
    {
        FileDown.getFileDown().getDaoSession().getDownloadInfoDao().save(info);
    }


    /**
     * 删除下载信息
     */
    protected void deleteDownInfo()
    {
        FileDown.getFileDown().getDaoSession().getDownloadInfoDao().delete(info);
    }

    /**
     * 清楚所有的下载数据数据
     */
    public void cleanDataBase()
    {
        FileDown.getFileDown().getDaoSession().getDownloadInfoDao().deleteAll();
      //  Log.d(TAG,"DataBase："+FileDown.getInstances().getDaoSession().getDownloadInfoDao().)
    }

    public String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 延迟回掉进度
     */
    protected void  delayProgress()
    {
        mResearchProgress = Observable.interval(mSpeedRefreshUiTime,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (mDownStatus == DownloadConfig.STATUS_DOWNNING)
                        {
                            // 如果是正在下载的话，回掉进度：
                            if (progressObserver != null)
                            {
                                // 计算下载速度
                                getSpeed();
                                progressObserver.progress(info.getReadLength(), info.getContentLength(), info.getIsComplete(), info.getUrl(),speed);
                            }
                        }else
                        {
                            // 其他情况关闭轮询
                            if (mResearchProgress != null)
                            {
                                mResearchProgress.dispose();
                            }
                        }
                    }
                });
    }


    // 最近一次计算时间
    long lastTimeStamp;
    // 上次读写的数据
    long lastRead;
    // 下载速度
    long speed;

    /**
     * long类型的下载速度
     * @return
     */
    protected long getSpeed()
    {
       long nowTimeStamp = System.currentTimeMillis();
       if (nowTimeStamp - lastTimeStamp < mSpeedRefreshUiTime) return speed;
       speed = ((info.getReadLength() - lastRead) / ((nowTimeStamp - lastTimeStamp)/1000));
       Log.d(TAG,"瞬时下载量："+(info.getReadLength() - lastRead)+"；speed："+speed);
        lastTimeStamp = nowTimeStamp;
       lastRead = info.getReadLength();
       return speed;
    }



    public void setProgressListener(DownloadListener progressObserver)
    {
        this.progressObserver = progressObserver;
    }

}

