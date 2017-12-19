package com.presentation;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.brick.robotctrl.R;
import com.kjn.videoview.ADVideo;

/**
 * Created by shenzhen on 2017/1/13.
 */

public class VideoPresentation extends  BasePresentation {

    private static final String TAG = "VideoPresentation.class";
    public VideoView videopresent;
    public String adviopath;
    public ADVideo avideo;
    public RelativeLayout retv;

    public VideoPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
      protected void initView(Bundle savedInstanceState) {
        Log.d("MainPresentation", "MainPresentation............main ..2");
        setContentView(R.layout.persentation_video);
        retv = (RelativeLayout) findViewById(R.id.retv);
         //开始隐藏video控件
        videopresent = (VideoView) findViewById(R.id.videopresent);
        videopresent.setVisibility(View.GONE);//隐藏控件

     }

    public void initViewVideoData(boolean isvideo,   final String filename) {
        avideo = new ADVideo(videopresent, new Handler());
        if(isvideo){

            adviopath = Environment.getExternalStorageDirectory().getPath()+"/Movies";
            new Thread() {
                @Override
                public void run() {
                    try {
                        videopresent.setVisibility(View.VISIBLE);//显示控件
                        retv.setBackgroundColor(Color.BLACK);
                        if (avideo.getFiles(adviopath)) {
                            avideo.playSingleWhat(filename);
                            Log.d(TAG, "videoPlayTargetSingle:");
                        }
                    } catch (Exception e) {
                        Log.d("getfile", "查找异常!");
                        System.out.println(e.toString());
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
               }.start();
          }

    }
}
