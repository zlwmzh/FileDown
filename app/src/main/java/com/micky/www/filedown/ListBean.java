package com.micky.www.filedown;

/**
 * Created by Micky on 2018/12/14.
 */

public class ListBean {
    private String cover;
    private String title;
    private String url;
    private int percent;

    public ListBean(String cover, String title, String url, int percent) {
        this.cover = cover;
        this.title = title;
        this.url = url;
        this.percent = percent;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "ListBean{" +
                "cover='" + cover + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", percent=" + percent +
                '}';
    }
}
