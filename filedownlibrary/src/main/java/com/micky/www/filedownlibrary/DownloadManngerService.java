package com.micky.www.filedownlibrary;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Micky on 2018/12/14.
 * 下载管理服务类
 */

public class DownloadManngerService extends Service{



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
