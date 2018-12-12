package com.micky.www.filedown;

/**
 * Created by Micky on 2018/12/12.
 * 文件下载的实体类
 */

public class DownloadInfo {
    // 本地存储路径
    private String localPath;
    // 文件总长度
    private long contentLength;
    // 已下载长度
    private long readLength;
    // 文件下载链接
    private String url;
    // 绑定下载服务
    private DownLoadService service;

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getReadLength() {
        return readLength;
    }

    public void setReadLength(long readLength) {
        this.readLength = readLength;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DownLoadService getService() {
        return service;
    }

    public void setService(DownLoadService service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "localPath='" + localPath + '\'' +
                ", contentLength=" + contentLength +
                ", readLength=" + readLength +
                ", url='" + url + '\'' +
                ", service=" + service +
                '}';
    }
}
