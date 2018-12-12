package com.micky.www.filedown;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

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

    protected ProgressListener progressObserver;
    protected DownloadInfo info;
    protected DownLoadService service;

    private Disposable disposable;
    // 文件存储路径
    protected String mSaveFilePath = Environment.getExternalStorageDirectory() +
            File.separator +"mickydown"+File.separator;
    private DownloadManager()
    {
       // 信息下载类
       info = new DownloadInfo();
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
        Log.d(TAG, "progress : " + "read = " + read + "contentLength = " + contentLength);
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

        // 通过RxJava的方法，将回掉结果转接到主线程，防止刷新UI时崩溃
        Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (progressObserver != null)
                {
                    progressObserver.progressChanger(info.getReadLength(), info.getContentLength(), done);
                }
            }
        });
    }

    /**
     * 开始下载
     * @param url
     */
    public void start(String url)
    {
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
            // 获取保存的实例
            service = info.getService();
        }
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
           }

           @Override
           public void onNext(@NonNull DownloadInfo downloadInfo) {
              Log.d(TAG,"onNext："+downloadInfo);
           }

           @Override
           public void onError(@NonNull Throwable e) {
               Log.d(TAG,"onError："+e.getMessage());
           }

           @Override
           public void onComplete() {
               Log.d(TAG,"onComplete");
           }
       });

    }

    /**
     * 暂停下载
     */
    public void pause()
    {
        if (disposable != null && disposable.isDisposed())
        {
            disposable.dispose();
        }
    }

    /**
     *  恢复下载
     */
    public void resume()
    {
        download();
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
     * 进度回掉接口
     */
    public interface ProgressListener
    {
        void progressChanger(long read, long contentLength, boolean done);
    }

    public void setProgressListener(ProgressListener progressObserver)
    {
        this.progressObserver = progressObserver;
    }

}
