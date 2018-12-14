package com.micky.www.filedownlibrary;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Micky on 2018/12/12.
 * 文件下载的实体类
 */
@Entity
public class DownloadInfo {
    @Id
    private Long id;
    // 本地存储路径
    @Property(nameInDb = "local_path")
    private String localPath;
    // 文件总长度
    @Property(nameInDb = "content_length")
    private long contentLength;
    // 已下载长度
    @Property(nameInDb = "read_length")
    private long readLength;
    // 文件下载链接
    @Property(nameInDb = "url")
    private String url;
    @Property(nameInDb = "is_complete")
    private boolean isComplete;
    // 绑定下载服务
    @Transient
    private DownLoadService service;

    @Generated(hash = 1329093281)
    public DownloadInfo(Long id, String localPath, long contentLength,
            long readLength, String url, boolean isComplete) {
        this.id = id;
        this.localPath = localPath;
        this.contentLength = contentLength;
        this.readLength = readLength;
        this.url = url;
        this.isComplete = isComplete;
    }

    @Generated(hash = 327086747)
    public DownloadInfo() {
    }

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

    public boolean getIsComplete() {
        return this.isComplete;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    
}
