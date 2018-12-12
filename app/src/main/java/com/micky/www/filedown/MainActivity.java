package com.micky.www.filedown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,DownloadManager.ProgressListener{

    private ProgressBar mPrb;
    private TextView mPercent;
    private TextView mStart;
    private TextView mPause;
    private TextView mResume;
    private DownloadManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrb = findViewById(R.id.progress);
        mPercent = findViewById(R.id.percent);
        mStart = findViewById(R.id.start);
        mPause = findViewById(R.id.pause);
        mResume = findViewById(R.id.resume);

        mStart.setOnClickListener(this);
        mPause.setOnClickListener(this);
        mResume.setOnClickListener(this);

        manager = DownloadManager.getInstance();
        manager.setProgressListener(this);
    }

    @Override
    public void onClick(View view) {
       switch (view.getId())
       {
           case R.id.start:
               manager.start("https://test2015data.oss-cn-hangzhou.aliyuncs.com/audio-hroot/year201810/audio-201810100/Audio/file_181015101215435595.mp3");
               break;
           case R.id.pause:
               manager.pause();
               break;
           case R.id.resume:
               manager.resume();
               break;
       }
    }

    @Override
    public void progressChanger(long read, long contentLength, boolean done) {
        int progress = (int) (100 * read / contentLength);
        mPrb.setProgress(progress);
        mPercent.setText(progress+"%");
    }
}
