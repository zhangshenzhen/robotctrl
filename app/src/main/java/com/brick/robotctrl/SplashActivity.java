package com.brick.robotctrl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRouter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.VideoView;

import com.presentation.SamplePresentation;
import com.rg2.activity.BaseActivity;
import com.rg2.utils.WifiAdmin;

/**
 * Created by shenzhen on 2017/1/7.
 */

public class SplashActivity extends BaseActivity {

    private static final String TAG ="SplashActivity" ;
    public  String OPPOR9sk = '"'+"OPPO R9sk"+'"';
    private SamplePresentation mPresentation;
    private VideoView vv;
    private  Uri mUri;
    public WifiAdmin wifiAdmin;


    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);//主屏幕;
        vv = (VideoView) findViewById(R.id.vv);

//        Intent startIntent = new Intent(SplashActivity.this, ZIMEAVDemoService.class);
//        startService(startIntent); // 启动服务

    }

    @Override
    protected void initData() {
         mUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.red);
         vv.setVideoURI(Uri.parse(String.valueOf(mUri)));
         vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                  @Override
               public void onPrepared(MediaPlayer mediaPlayer) {
                   if(vv != null) {
                    Log.i(TAG, "initData:VideoView "+vv);
                      vv.start();
                    }
                  }
              });
    }
    @Override
    protected void initViewData() {
     /* new Thread(new Runnable() {
            @Override
            public void run() {
         String[] str = new String[]{"edge -a 192.168.10.8 -c test -k 123456 -l 222.190.128.98:8080 &",
           "sleep 1",
         "busybox ip route delete 192.168.10.0/24",
          "busybox ip route add 192.168.10.0/24 via 192.168.10.8 dev edge0 table local"};
                CommandExecution.execCommand(str,true);
              */
        /* String[] str = new String[]{"edge -a 192.168.100.34 -c test -k 123456 -l 222.190.128.98:8080 &","sleep 1",
                        "busybox ip route delete 192.168.100.0/24","busybox ip route add 192.168.100.0/24 via 192.168.100.34 dev edge0 table local"};
                CommandExecution.execCommand(str,true);/*
            }
        }).start();*/
    }

    @Override
    protected void initEvent() {

        //视频播放的监听事件;
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //获取最大音乐量值
                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                int  current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
                Log.e(TAG, "设置前媒体音量 ："+current);

                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,13,0);
                int  current2 = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
                Log.e(TAG, "当前媒体音量 ："+current2);

            startActivityForResult( new Intent(SplashActivity.this, MainActivity.class),1);
            }
        });
    }



    @Override
    protected void onResume() {
        // Log.d(TAG, "onResume: ");
        super.onResume();
        autoConnectWifi();
        // Register a callback for all events related to live video devices
       // mMediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, mMediaRouterCallback);
        // Update the displays based on the currently active routes
       // updatePresentation();//在父类中调用
    }

    @Override
    protected void onPause() {
        // Log.d(TAG, "onPause: ");
        super.onPause();

    }

    @Override
    protected void onStop() {
         super.onStop();
         if(vv != null){
             vv=null;
         }
        if (mPresentation != null) {
            mPresentation.dismiss();
            mPresentation = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void updatePresentation() {
        // Log.d(TAG, "updatePresentation: ");
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;

        // 注释 : Dismiss the current presentation if the display has changed.
        if (mPresentation != null && mPresentation.getDisplay() != presentationDisplay) {
            mPresentation.dismiss();
            mPresentation = null;
        }
        if (mPresentation == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mPresentation = new SamplePresentation(this, presentationDisplay);
            // Log.d(TAG, "updatePresentation: this: "+ this.toString());
            mPresentation.setOnDismissListener(mOnDismissListener);
            // Try to show the presentation, this might fail if the display has
            // gone away in the mean time
            try {
                   mPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                // Couldn't show presentation - display was already removed
                // Log.d(TAG, "updatePresentation: failed");
                mPresentation = null;
            }
        }
    }
    //代码连接无线网
    public void autoConnectWifi(){
           wifiAdmin = new WifiAdmin(SplashActivity.this);
        if(!OPPOR9sk.equals(wifiAdmin.getSSID())) {//如果已经里连接默认网络不要重复连接；
            wifiAdmin.openWifi();
            try {
                //防止wifi 是关闭的，打开后要等待一段时间才能创建连接，否者会出现空指针
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo("OPPO R9sk", "12345678", 3));
            Toast.makeText(SplashActivity.this, "准备连接网络", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "默认网络:");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 &&resultCode== Activity.RESULT_OK)
        {
            finish();
        }
    }
}
