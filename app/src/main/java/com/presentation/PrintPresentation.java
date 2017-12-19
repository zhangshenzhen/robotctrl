package com.presentation;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.brick.robotctrl.PlayerService;
import com.brick.robotctrl.R;

/**
 * Created by shenzhen on 2017/1/13.
 */

public class PrintPresentation extends BasePresentation {

    private TextView mprint;
    private TextView mpaper;
    String path;
  boolean  ispaper = true;//模拟判断机器内部是否有纸质小票;
    public PrintPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
      protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.persentation_print);
         initViewData(ispaper);
      }

    public void initViewData(boolean ispaper){
         mprint = (TextView) findViewById(R.id.print);
         mpaper = (TextView) findViewById(R.id.paper);
        // path = "/data/data/com.brick.robotctrl/dream.mp3";
//        String path2 = "/data/data/dream.mp3";//需要在目录下放入资源文件；
         String path = Environment.getExternalStorageDirectory()
                .getPath()+"/Music/we_are_diffent.mp3";
         if(ispaper ){
          mpaper.setVisibility(View.GONE);
          mprint.setVisibility(View.VISIBLE);
          Log.d("PrintActivity","还有打印纸");
          PlayerService.stopAction(mContext); //关闭音乐
          }else {
           mprint.setVisibility(View.GONE);
           mpaper.setVisibility(View.VISIBLE);
           Log.d("PrintActivity","已经没有打印纸了，急需更换。。。");
           PlayerService.startAction(mContext, path);  //调用开启服务的方法,开启音乐；
          }
    };
}
