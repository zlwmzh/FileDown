package com.micky.www.filedownlibrary;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Micky on 2018/12/12.
 * 进度拦截器
 */

public class DownloadInterceptor implements Interceptor{
    protected DownloadProgressListener listener;

    public DownloadInterceptor(DownloadProgressListener listener)
    {
       this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder().body(new DownloadResponseBody(response.body(), listener)).build();
    }
}
