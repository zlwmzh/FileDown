package com.micky.www.filedown;

/**
 * Created by Micky on 2018/12/12.
 * 请求链接处理工具类
 * 创建Retrofit需要有一个baseURL，并且结尾必须是以“/”结尾，否则会报异常
 */

public class UrlUtils {
    /**
     * 读取baseurl
     *
     * @param url
     * @return
     */
    public static String getBasUrl(String url) {
        String head = "";
        int index = url.indexOf("://");
        if (index != -1) {
            head = url.substring(0, index + 3);
            url = url.substring(index + 3);
        }
        index = url.indexOf("/");
        if (index != -1) {
            url = url.substring(0, index + 1);
        }
        return head + url;
    }
}
