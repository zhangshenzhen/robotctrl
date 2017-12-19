package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.brick.robotctrl.SplashActivity;


/**
 * Created by lx on 2017/5/18.
 */

public class BootReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"开机啦");
//     if(intent.getAction().equals(ACTION)) {     // boot
        Intent intent2 = new Intent(context, SplashActivity.class);
        intent2.setAction("android.intent.action.MAIN");
        intent2.addCategory("android.intent.category.LAUNCHER");
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);//关闭自启动
        // }
    }
}
