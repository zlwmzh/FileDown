package com.micky.www.filedownlibrary;

/**
 * Created by Micky on 2018/12/14.
 * 进度回掉接口
 */

public interface DownloadProgressListener {
    /**
     * @param read 已下载长度
     * @param contentLength 总长度
     * @param done 是否下载完毕
     */
    void progress(long read, long contentLength, boolean done);
}
