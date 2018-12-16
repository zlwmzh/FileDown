package com.micky.www.filedown;

/**
 * Created by Micky on 2018/12/14.
 */

public class ListBean {
    private String cover;
    private String title;
    private String url;
    private int percent;
    private String speed;
    private int downStatus;

    public ListBean(String cover, String title, String url, int percent, String speed, int downStatus) {
        this.cover = cover;
        this.title = title;
        this.url = url;
        this.percent = percent;
        this.speed = speed;
        this.downStatus = downStatus;
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

    public String getSpeed() {
        return speed;
    }

    public void setKbSpeed(String speed) {
        this.speed = speed;
    }

    public int getDownStatus() {
        return downStatus;
    }

    public void setDownStatus(int downStatus) {
        this.downStatus = downStatus;
    }

    @Override
    public String toString() {
        return "ListBean{" +
                "cover='" + cover + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", percent='" + percent + '\'' +
                ", kbSpeed='" + speed + '\'' +
                ", downStatus=" + downStatus +
                '}';
    }
}
