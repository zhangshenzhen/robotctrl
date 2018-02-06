package com.ftp;

import android.util.Log;

import java.io.File;

/**
 * Created by lx on 2018-02-02.
 */

public class FtpDownLoad {
    public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功";
    public static final String FTP_CONNECT_FAIL = "ftp连接失败";
    public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接";
    public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在";

    public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";
    public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败";
    public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";

    public static final String FTP_DOWN_LOADING = "ftp文件正在下载";
    public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";
    public static final String FTP_DOWN_FAIL = "ftp文件下载失败";

    public static final String FTP_DELETEFILE_SUCCESS = "ftp文件删除成功";
    public static final String FTP_DELETEFILE_FAIL = "ftp文件删除失败";
    private static final String TAG ="FtpDownLoad.class" ;

    public static void downLoad(final String serverpath, final String Localpath){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Log.d(TAG, "-----xiazai---"+"%"+ Thread.currentThread());

                    new FTP().downloadSingleFile(serverpath, Localpath, new FTP.DownLoadProgressListener() {
                        @Override
                        public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                            if(currentStep.equals(FtpDownLoad.FTP_DOWN_SUCCESS)){
                                Log.d(TAG, "-----xiazai--successful");
                            } else if(currentStep.equals(FtpDownLoad.FTP_DOWN_LOADING)){
                                Log.d(TAG, "-----xiazai---"+downProcess + "%");
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();



    }
}
