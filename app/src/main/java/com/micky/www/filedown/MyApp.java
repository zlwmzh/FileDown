package com.micky.www.filedown;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.micky.www.filedownlibrary.FileDown;

/**
 * @ClassName FileDown
 * @Description TODO
 * @Author Micky
 * @Date 2018/12/16 14:47
 * @Version 1.0
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        FileDown.init(this);
    }
}
