package com.brick.robotctrl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjnijk on 2016-09-10.
 */
public class ImageActivity extends Activity{
    private final String TAG = "ImageActivity";
    public static String fileName = null;
    private String mode = null;
    private ImageView imageView;
    private List<String> videoList;
    private final int singleOver = 1010;
    private int index;
    private String imagefilePath;
    private static Handler contextHandler2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image);
        Intent intent = getIntent();
       fileName = intent.getStringExtra("fileName");
        Log.d(TAG, "onCreate: filename" + fileName);
        mode = intent.getStringExtra("mode");
        imageView = (ImageView)findViewById(R.id.imageView);
        imagefilePath = Environment.getExternalStorageDirectory()
                .getPath()+"/Movies";
        switch (mode) {
            case "Single":
                showsingle(fileName);
                break;
            case "Cycle":
                showcycle(fileName);
                break;
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagefilePath = Environment.getExternalStorageDirectory()
                        .getPath()+"/Movies";
                if (++index >= videoList.size()) {
                    index = 0;
                }
                String imagefileName=videoList.get(index);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bm = BitmapFactory.decodeFile(imagefileName, options);
                imageView.setImageBitmap(bm);
            }
        });
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case singleOver:
                    String imagefileName = videoList.get(index);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bm = BitmapFactory.decodeFile(imagefileName, options);
                    imageView.setImageBitmap(bm);
                    if (++index >= videoList.size()) {
                        index = 0;
                    }
                    Log.d(TAG, "handleMessage: 正在显示++");
                    ActivityManager dm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);//获得运行activity
                    ComponentName dn = dm.getRunningTasks(1).get(0).topActivity;//得到某一活动
                    if ( dn.getClassName().equals("com.brick.robotctrl.ImageActivity")) {
                        handler.sendEmptyMessageDelayed(singleOver, 3000);
                    }
                    break;
            }
        }
    };

    public void showsingle(String fileName){
        String imagefileName = Environment.getExternalStorageDirectory()
                    .getPath()+"/Movies/"+fileName;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bm = BitmapFactory.decodeFile(imagefileName, options);
        imageView.setImageBitmap(bm);
    }

    public void showcycle(String fileName){
        if(getFiles(imagefilePath)) {
            index = findIndexOfStringInvideoList(fileName);
            String imagefileName = videoList.get(index);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bm = BitmapFactory.decodeFile(imagefileName, options);
            imageView.setImageBitmap(bm);
            if (++index >= videoList.size()) {
                index = 0;
            }
            handler.sendEmptyMessageDelayed(singleOver,3000);
        }
    }

    public static void startAction(Context context, String mode, String str) {
        Intent startIntent = new Intent();
        startIntent.setClass(context, ImageActivity.class);
        startIntent.putExtra("mode", mode);
        startIntent.putExtra("fileName", str);
        context.startActivity(startIntent);
    }

    public boolean getFiles(String url) {
        boolean flag = true;
        videoList = new ArrayList<String>();
        try {
            File file = new File(url);
            File[] files = file.listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    Log.i(TAG, "getFiles find Directory");
                    getFiles(files[i].getAbsolutePath());
                } else {
                    if (files[i].getAbsolutePath().endsWith(".jpg")
                            ) {
                        videoList.add(files[i].toString());
                    }
                }
            }
            if (videoList.isEmpty()){
                flag = false;
            }
        } catch (Exception e) {
            Log.d("getfile", "查找异常!");
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
//        for(int i = 0; i<videoList.size(); i++) {
//            Log.d(TAG, "getFiles: " + videoList.get(i));
//        }
        return flag;
    }
    public int findIndexOfStringInvideoList(String str)
    {
        for(int i=0;i<videoList.size();i++) {
            if(videoList.get(i).endsWith(str)) {
                return i;
            }
        }
        return -1;
    }
}
