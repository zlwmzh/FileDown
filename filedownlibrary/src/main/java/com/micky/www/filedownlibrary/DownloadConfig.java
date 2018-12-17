package com.micky.www.filedownlibrary;

import android.content.BroadcastReceiver;

/**
 * @ClassName DownloadConfig
 * @Description  下载工具类一些参数配置
 * @Author Micky
 * @Date 2018/12/15 13:57
 * @Version 1.0
 */
public class DownloadConfig {

    // 默认下载的最大线程数
    public static final int MAX_DOWN_THREAD_COUNT = 3;
    // 更新速度的时间
    public static final int SPEED_REFRESH_UI_TIME = 1 * 1000;

    // 下载Action
    public static final int ACTION_START = 0x00;
    public static final int ACTION_PAUSE = 0x01;
    public static final int ACTION_RESUME = 0x02;
    public static final int ACTION_RESTART = 0x03;
    public static final int ACTION_DELETE = 0x04;
    public static final int ACTION_DEFAULT = 0x05;
    public static final int ACTION_START_ALL = 0x14;
    public static final int ACTION_PAUSE_ALL = 0x15;
    public static final int ACTION_DELETE_ALL =0x16;

    // 相关取值关键字
    public static final String ACTION = "action";
    public static final String URL = "url";
    public static final String URL_ARRAY = "url_array";
    public static final String READ = "read";
    public static final String TOTAL = "total";
    public static final String SPEED = "speed";
    public static final String MAX_THREAD_COUNT = "max_thread_count";
    public static final String SAVE_PATH = "save_path";
    public static final String IS_COMPLETE = "is_complete";
    public static final String LOCAL_PATH = "local_path";
    public static final String ERROR_MESSAGE = "error_message";

    // 广播通知
    public static final String RECEIVER_START ="RECEIVER_START";
    public static final String RECEIVER_PASUE = "RECEIVER_PASUE";
    public static final String RECEIVER_RESUME = "RECEIVER_RESUME";
    public static final String RECEIVER_WAIT = "RECEIVER_WAIT";
    public static final String RECEIVER_DOWNNING = "RECEIVER_DOWNNING";
    public static final String RECEIVER_COMPLETE = "RECEIVER_COMPLETE";
    public static final String RECEIVER_ERROR = "RECEIVER_ERROR";
    public static final String RECEIVER_DELETE = "RECEIVER_DELETE";

    // 当前状态
    public static final int STATUS_START = 0x06;
    public static final int STATUS_PAUSE = 0x07;
    public static final int STATUS_RESUME = 0x08;
    public static final int STATUS_WAIT =0x09;
    public static final int STATUS_DOWNNING = 0x10;
    public static final int STATUS_COMPLETE = 0x11;
    public static final int STATUS_ERROR = 0x12;
    public static final int STATUS_DELETE = 0x13;
    public static final int STATUS_DEFAULT = 0x00;
}
