package com.micky.www.filedownlibrary;

import java.io.File;

/**
 * @ClassName DownloadListener
 * @Description 下载监听
 * @Author Micky
 * @Date 2018/12/15 15:05
 * @Version 1.0
 */
public interface DownloadListener {

    /**
     * 开始下载
     * @param url
     */
    void start(String url);

    /**
     * 暂停下载
     * @param url
     */
    void pause(String url);

    /**
     * 下载进度
     * @param read   已下载大小
     * @param contentLength   总大小
     * @param done 是否完成
     * @param url 下载连接
     * @param kbs 下载速度 kb/s
     */
    void progress(long read, long contentLength, boolean done, String url,long kbs);

    /**
     * 等待下载
     * @param url 下载连接
     */
    void wait(String url);

    /**
     * 下载完成
     * @param url 下载连接
     */
    void complete(String url, File file);

    /**
     *  下载完成
     * @param url
     */
    void error(String url, String msg);

    /**
     *  删除任务
     * @param url
     */
    void delete(String url);
}
