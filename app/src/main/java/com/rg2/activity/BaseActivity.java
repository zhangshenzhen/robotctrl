package com.rg2.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.io.IOException;


/**
 * 作者：王先云 on 2016/7/1 10:23
 * 邮箱：wangxianyun1@163.com
 * 描述：一句话简单描述
 */
public abstract class BaseActivity extends Activity implements View.OnClickListener {
    private static final String TAG ="BaseActivity" ;
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

    public Context mContext;
       // 媒体路由器
    public MediaRouter mMediaRouter;
    //屏幕管理器
    public DisplayManager mDisplayManager;
    public int mTotalDisplays = 0;//屏幕个数
    public String mSecondaryTouch;

    public Object mPresentation;
   // public UserInfo instance ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        //单例
       // instance = UserInfo.getInstance();
        super.onCreate(savedInstanceState);
        //启动时隐藏软键盘,但EditText的光标还在，点击编辑框才弹出软键盘；
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        decorView = getWindow().getDecorView();

       /* int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);*/

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());

        //设置为横屏幕;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        initViews(savedInstanceState);//主屏幕
        Log.e("Baseactivity","....1");
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
        mSecondaryTouch = System.getProperty("persist.secondary.touch", "ft5x06");

        //intent中获取数据
        initData();
        // 代码和初始化数据
         initViewData();
        //事件;
        initEvent();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i(TAG,"。。系统获取了焦点");
        if (hasFocus){
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)  {

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

    //回调函数;
    public final MediaRouter.SimpleCallback mMediaRouterCallback =
            new MediaRouter.SimpleCallback() {
                @Override
                public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {

                    updatePresentation();
                }
                @Override
                public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
                    updatePresentation();
                }
                @Override
                public void onRoutePresentationDisplayChanged(MediaRouter router, MediaRouter.RouteInfo info) {
                   // Log.d(TAG, "onRoutePresentationDisplayChanged: info=" + info);

                    updatePresentation();
                }
            };

        protected abstract void updatePresentation();

    public final DialogInterface.OnDismissListener mOnDismissListener =
            new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    // Log.d(TAG, "onDismiss: ");
                    if (dialog == mPresentation) {
                        Log.e("mOnDismissListener","...........666");
                        mPresentation = null;
                    }
                }
            };


    /**
     * 从intent中获取数据
     */
    protected abstract void initData();

    /**
     * 存放从xml中获取ui,例如 findViewById
     */
    protected abstract void initViews(Bundle savedInstanceState);


    /**
     * 初始化页面UI事件,例如 setOnClickListener
     */
    protected abstract void initEvent();

    /**
     * 存放刷新页面的代码和初始化数据
     */
    protected abstract void initViewData();

    @Override
    public void onClick(View v){
    }

    @Override
    protected void onPause() {
        super.onPause();
         updatePresentation();
     mMediaRouter.removeCallback(mMediaRouterCallback);
    }




    @Override
    protected void onResume() {

        super.onResume();
        // Listen for changes to media routes.
        mMediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, mMediaRouterCallback);

        updatePresentation();
    }

    //@Override
    /*public boolean dispatchTouchEvent(MotionEvent event) /*{
        float x,y;
        x = event.getX();
        //250
        y=event.getY();
        Log.d("TAG", "dispatchTouchEvent: "+x+":"+" "+y);
        event.setLocation(x*1280/1024,y*750/768);

        return super.dispatchTouchEvent(event);
    }*/
}
