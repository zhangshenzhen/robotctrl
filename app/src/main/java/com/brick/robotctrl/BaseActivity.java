package com.brick.robotctrl;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;

import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "BaseActivity";
    //    UserTimer userTimer = null;
    private static int timerOutCount = 0;
    private int screenWidth;
    private int screenHeight;
    protected AudioManager mAudioManager;
    /**
     * 最大声音
     */
    private int mMaxVolume;
    /**
     * 当前声音
     */
    private int mVolume = -1;
    private GestureDetector mGestureDetector;
    private View decorView;

    public static void clearTimerCount() {
        timerOutCount = 0;
    }

    public static void addTimerCount() {
        timerOutCount++;
    }

    public static int getTimerCount() {
        return timerOutCount;
    }

    //gr2中的
    public Context mContext;
    // 媒体路由器
    public MediaRouter mMediaRouter;
    //屏幕管理器
    public DisplayManager mDisplayManager;
    public int mTotalDisplays = 0;//屏幕个数
    public String mSecondaryTouch;
    //传入的对象
    public Object mPresentation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        //设置为横屏幕;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();      // 屏幕高（像素，如：800p）

        decorView = getWindow().getDecorView();
//        Hide both the navigation bar and the status bar.
//        SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
//        a general rule, you should design your app to hide the status bar whenever you
//        hide the navigation bar.
        //隐藏虚拟按键。导航栏的属性
        /*SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏状态栏
          SYSTEM_UI_FLAG_FULLSCREEN     //全屏;
          SYSTEM_UI_FLAG_IMMERSIVE      //可以获取焦点
          SYSTEM_UI_FLAG_IMMERSIVE_STICKY  //一段时间后自动隐藏
          SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION和SYSTEM_UI_FLAG_LAYOUT_STABLE)来防止系统栏隐藏时内容区域大小发生变化
        * */
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
//        decorView.setSystemUiVisibility(uiOptions);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());


      //添加了gr2包下的BaseActivity,
        //设置为横屏幕;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        //获取媒体路由器
        mMediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);         // 控制和管理路由的媒体服务
        //获取双屏异显的设备
        mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);        // 与显示设备交互服务
        //     列出可用的屏幕
        while (true) {
            Display display = mDisplayManager.getDisplay(mTotalDisplays);   // Gets information about a logical display.
            if (display == null)
                break;
            mTotalDisplays++;//  mTotalDisplays = 1;
        }
      //   mSecondaryTouch = System.getProperty("persist.secondary.touch", "ft5x06");
        ActivityCollerctor.AddActivity(this);//把所有Activity存入集合
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i(TAG,"系统获取了焦点");
        if (hasFocus){
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }

    //回调函数;
    public final MediaRouter.SimpleCallback mMediaRouterCallback =
            new MediaRouter.SimpleCallback() {
                @Override
                public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
                      Log.d(TAG, "onRouteSelected: type=" + type + ", info ...1=" + info);
                    updatePresentation();
                }
                @Override
                public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
                    Log.d(TAG, "onRouteUnselected: type=" + type + ", info,,2=" + info);
                    updatePresentation();
                }
                @Override
                public void onRoutePresentationDisplayChanged(MediaRouter router, MediaRouter.RouteInfo info) {
                    Log.d(TAG, "onRoutePresentationDisplayChanged  ..3: info =" + info);
                    updatePresentation();
                }
            };

       protected abstract void updatePresentation() ;


    public final DialogInterface.OnDismissListener mOnDismissListener =
            new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    // Log.d(TAG, "onDismiss: ");
                    if (dialog == mPresentation) {
                        mPresentation = null;
                    }
                }
            };


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();

    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: ");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        clearTimerCount();
        Log.d(TAG, "onResume: ");
        View decorView = getWindow().getDecorView();
//        Hide both the navigation bar and the status bar.
//        SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
//        a general rule, you should design your app to hide the status bar whenever you
//        hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        super.onResume();
        updatePresentation();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        ActivityCollerctor.RemoveActivity(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mGestureDetector.onTouchEvent(event))
            return true;

        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
    }

   @Override
    public void onClick(View view) {

    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        long[] mHitsL = new long[5];
        long[] mHitsR = new long[5];

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            Log.d(TAG, "onTouch: x:" + x + "y:" + y);

            if (y < screenHeight / 2) {
                if (x < screenWidth / 2) {
                    System.arraycopy(mHitsL, 1, mHitsL, 0, mHitsL.length - 1);
                    mHitsL[mHitsL.length - 1] = SystemClock.uptimeMillis();
                    //Log.d(TAG, "onPreferenceClick:mHits" + mHits[4]+ ","+mHits[3]+"," + mHits[2]+"," + mHits[1]+"," + mHits[0]);
                    if (mHitsL[0] >= (SystemClock.uptimeMillis() - 3000)) {
                        Log.d(TAG,"onPreferenceClick:shutdown");
                        onShutdown();
                    }
                } else {
                    System.arraycopy(mHitsR, 1, mHitsR, 0, mHitsR.length - 1);
                    mHitsR[mHitsR.length - 1] = SystemClock.uptimeMillis();
                    //Log.d(TAG, "onPreferenceClick:mHits" + mHits[4]+ ","+mHits[3]+"," + mHits[2]+"," + mHits[1]+"," + mHits[0]);
                    if (mHitsR[0] >= (SystemClock.uptimeMillis() - 3000)) {
                        Log.d(TAG,"onPreferenceClick:reboot");
                        onReboot();
                    }
                }
            }
            return true;

        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            Display disp = getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();

//            if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
            onVolumeSlide((mOldY - y) / windowHeight);
//            else if (mOldX < windowWidth / 5.0)// 左边滑动
//                onBrightnessSlide((mOldY - y) / windowHeight);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;
            // 显示
//            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
//            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        int nextVolume = (int) (percent * mMaxVolume) + mVolume;
        if (nextVolume > mMaxVolume) {
            nextVolume = mMaxVolume;
        } else if (nextVolume < 0) {
            nextVolume = 0;
        }
        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, nextVolume, 0);

        // 变更进度条
//        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
//        lp.width = findViewById(R.id.operation_full).getLayoutParams().width
//                * nextVolume / mMaxVolume;
//        mOperationPercent.setLayoutParams(lp);
    }

    public void onReboot() {
        try {
            Runtime.getRuntime().exec("su -c \"/system/bin/reboot\"");
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Log.d(TAG, "onReboot: start to reboot");
//        PowerManager pManager=(PowerManager) getSystemService(Context.POWER_SERVICE);
//        pManager.reboot("");
    }

    public void onShutdown() {
//        try {
//            Runtime.getRuntime().exec("su -c \"/system/bin/reboot -p\"");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
//        intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
//        //其中false换成true,会弹出是否关机的确认窗口
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

//        Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
//        intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }
}
