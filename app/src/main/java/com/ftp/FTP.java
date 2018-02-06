package com.ftp;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lx on 2017/11/30.
 */

public class FTP {
    private static final String TAG ="FTP.class" ;
     // 服务器名.

    private String hostName;
  //端口号
    private int serverPort;
    // * 用户名.
    private String userName;
     //* 密码.
    private String password;
     //* FTP连接.
    private FTPClient ftpClient;
    public long mLocalSize;


    public FTP() {
      /*  this.hostName = "192.168.2.150";
        this.serverPort = 2121;
        this.userName = "hongshi";
        this.password = "1234qwer";*/
        this.hostName = "222.190.128.98";
        this.serverPort = 121;
        this.userName = "RobotFtp";
        this.password = "1234qwer";
        this.ftpClient = new FTPClient();

    }
    /**
     * 打开FTP服务.
     *
     * @throws IOException
     */
    public void openConnect() throws IOException {
        // 中文转码
        ftpClient.setControlEncoding("gbk");
        int reply; // 服务器响应值
        // 连接至服务器
        ftpClient.connect(hostName, serverPort);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        }
        // 登录到服务器
        ftpClient.login(userName, password);

        // 获取响应值
        reply = ftpClient.getReplyCode();
        Log.d(TAG, "openConnect: " + reply);
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        } else {
            // 获取登录信息
            FTPClientConfig config = new FTPClientConfig(ftpClient
                    .getSystemType().split(" ")[0]);
            config.setServerLanguageCode("zh");
            ftpClient.configure(config);
            // 使用被动模式设为默认
            ftpClient.enterLocalPassiveMode();
            // 二进制文件支持
            ftpClient
                    .setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);


        }
    }

    /**
     * 关闭FTP服务.
     *
     * @throws IOException
     */
    public void closeConnect() throws IOException {
        if (ftpClient != null) {
            // 退出FTP
            ftpClient.logout();
            // 断开连接
            ftpClient.disconnect();
        }
    }
     //服务器上的路径
    public String serverPathDir;

       /*  获取远程服务器上的文件资源
       * */
     public String ServerFile(String remoteFile, String LocalFile){
         // 先判断服务器文件是否存在
         try {
             FTPFile[] refiles = ftpClient.listFiles(remoteFile);

             File mfile = new File(LocalFile);//存放音视频的文件夹对象
             File[] mfiles = mfile.listFiles();//文件夹中集合
             //创建一个本地文件名的集合
             List<String> lis = new ArrayList<>();
             //遍历本地Movies文件夹中的文件获取文件名称,并且添加到存储文件名的集合;
              for (int j = 0; j <mfiles.length ; j++) {
                 lis.add(mfiles[j].getName());
              }
              Log.d(TAG, "-----本地文件---"+lis);

            for (int i = 0; i <refiles.length ; i++) {//遍历远程服务器上指定文件夹，
              Log.d(TAG, "---y--"+refiles[i].getName());
                /*获取远程文件的大小*/
                FTPFile[] files = ftpClient.listFiles(remoteFile+refiles[i].getName());
                long serverSize = files[0].getSize();
                //如果本地有这个文件,获取此文件的大小,因为文件名称相同
                File mLocalfile = new File(LocalFile+refiles[i].getName());
                if (mLocalfile.exists()){
                  mLocalSize = mLocalfile.length();
                //判断本地文件名的集合是否包含服务器上的文件,获取已经下载的文件是否是完整文件
                if (Math.abs(serverSize-mLocalSize)>0){
                    mLocalfile.delete();//删除本地文件 重新下载;
                    Log.d(TAG, "文件需要更新或删除本地后重新下载 : ");
                    lis.clear();
                    return refiles[i].getName();
                    //直接用绝对值求解;
                }else if (Math.abs(serverSize-mLocalSize)<10 ||serverSize ==mLocalSize /*lis.contains(refiles[i].getName())*/){ //本地文件名的集合包含服务器上的文件就删除掉
                     Log.d(TAG, "本地已经包含这个完整文件 可以删除 : "+refiles[i].getName());
                                 ///*+"/"*/
                  /* deleteSingleFile(serverPathDir+refiles[i].getName(), new DeleteFileProgressListener() {
                      @Override
                      public void onDeleteProgress(String currentStep) {
                          if(currentStep.equals(FtpDownLoad.FTP_DELETEFILE_SUCCESS)){
                              Log.d(TAG, "-----删除--success");
                          } else if(currentStep.equals(FtpDownLoad.FTP_DELETEFILE_FAIL)){
                              Log.d(TAG, "-----删除--fail");
                          }
                       }
                  });*/
                 } //不存在就返回开始下载
                }else if (!lis.contains(refiles[i].getName())){
                        lis.clear();
                    Log.d(TAG, "----不包含--");
                        return refiles[i].getName();
                 }else {
                    lis.clear();
                    Log.d(TAG, "----不存在--");
                    return refiles[i].getName();
                }

            }
         } catch (Exception e) {
             e.printStackTrace();
         }
           return null;
     }

    /**
     * 下载单个文件，可实现断点下载.
     * @param serverPath
     *            Ftp目录及文件路径
     * @param localPath
     *            本地目录
     * @param fileName
     *            下载之后的文件名称
     * @param listener
     *            监听器
     * @throws IOException
     */
    public  void downloadSingleFile(String serverPathfile, String localPath, DownLoadProgressListener listener)
            throws Exception {
            serverPathDir = serverPathfile;

        // 打开FTP服务
        try {
              this.openConnect();
             listener.onDownLoadProgress(FtpDownLoad.FTP_CONNECT_SUCCESSS, 0, null);
        } catch (Exception e1) {
            e1.printStackTrace();
            listener.onDownLoadProgress(FtpDownLoad.FTP_CONNECT_FAIL, 0, null);
            return;
        }
           //获取要下载远程路径下的文件名  //"test1/",localPath /*"/mnt/sdcard/Movies"*/
            String serverPath  =  ServerFile(serverPathfile,localPath /*"/mnt/sdcard/Movies"*/);
            String fileName =serverPath;//把本地下载后的文件名修改为何远程的一样;

             if(serverPath==null){
               Log.d(TAG, "-----从服务器上获取的文件名 : " +serverPath);
                 // 下载完成之后关闭连接
                 this.closeConnect();
                 listener.onDownLoadProgress(FtpDownLoad.FTP_DISCONNECT_SUCCESS, 0, null);
                  return;
             }


        // 先判断服务器文件是否存在                  //路径+文件名 serverPathfile+"/"+serverPath
        FTPFile[] files = ftpClient.listFiles(serverPathfile+serverPath);
        Log.d(TAG, "---判断服务器文件"+serverPathfile+serverPath );
        for (int i = 0; i <files.length ; i++) {
            Log.d(TAG, "---远程传递过来的--"+files[i].getName());
        }
        if (files.length == 0) {
            listener.onDownLoadProgress(FtpDownLoad.FTP_FILE_NOTEXISTS, 0, null);
            return;
        }

        //创建本地文件夹
        File mkFile = new File(localPath);
        if (!mkFile.exists()) {
            mkFile.mkdirs();
        }

        localPath = localPath + fileName;
        // 接着判断下载的文件是否能断点下载
        long serverSize = files[0].getSize(); // 获取远程文件的长度
        File localFile = new File(localPath);
        long localSize = 0;
        if (localFile.exists()) {
            localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
            Log.d(TAG, "---本地存在的残缺文件大小--"+localSize);
            if (localSize >= serverSize) {
                File file = new File(localPath);
                file.delete();
                //如果把文件删除了，就归零了
                localSize = 0;
            }
        }

        // 进度
        long step = serverSize / 100;
        long process = 0;
        long currentSize = 0;
        // 开始准备下载文件 创建要下载的空文件
        OutputStream out = new FileOutputStream(localFile, true);
        ftpClient.setRestartOffset(localSize);//serverPathfile+"/"+serverPath
        InputStream input = ftpClient.retrieveFileStream(serverPathfile+serverPath);
        byte[] b = new byte[1024];
        int length = 0;
        while ((length = input.read(b)) != -1) {
            out.write(b, 0, length);
            currentSize = currentSize + length;
            if (currentSize / step != process) {
                process = currentSize / step;
                if (process % 5 == 0) {  //每隔%5的进度返回一次
                    listener.onDownLoadProgress(FtpDownLoad.FTP_DOWN_LOADING, process, null);
                }
            }
        }
        out.flush();
        out.close();
        input.close();

        // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
        if (ftpClient.completePendingCommand()) {
            listener.onDownLoadProgress(FtpDownLoad.FTP_DOWN_SUCCESS, 0, new File(localPath));
        } else {
            listener.onDownLoadProgress(FtpDownLoad.FTP_DOWN_FAIL, 0, null);
        }

        // 下载完成之后关闭连接
        this.closeConnect();
        listener.onDownLoadProgress(FtpDownLoad.FTP_DISCONNECT_SUCCESS, 0, null);

        return;
    }

/*    public void downloadSingleFile(String serverPath, String localPath, String fileName, DownLoadProgressListener listener)
            throws Exception {
        // 打开FTP服务
        try {
            this.openConnect();
            listener.onDownLoadProgress(MainActivity.FTP_CONNECT_SUCCESSS, 0, null);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onDownLoadProgress(MainActivity.FTP_CONNECT_FAIL, 0, null);
            return;
        }
        // 先判断服务器文件是否存在
        FTPFile[] files = ftpClient.listFiles(serverPath);
        for (int i = 0; i <files.length ; i++) {
            Log.d(TAG, "-----"+files[i].getName());
        }
        if (files.length == 0) {
            listener.onDownLoadProgress(MainActivity.FTP_FILE_NOTEXISTS, 0, null);
            return;
        }
        //创建本地文件夹
        File mkFile = new File(localPath);
        if (!mkFile.exists()) {
            mkFile.mkdirs();
        }

        localPath = localPath + fileName;
        // 接着判断下载的文件是否能断点下载
        long serverSize = files[0].getSize(); // 获取远程文件的长度
        File localFile = new File(localPath);
        long localSize = 0;
        if (localFile.exists()) {
            localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
            if (localSize >= serverSize) {
                File file = new File(localPath);
                file.delete();
            }
        }
        // 进度
        long step = serverSize / 100;
        long process = 0;
        long currentSize = 0;
        // 开始准备下载文件
        OutputStream out = new FileOutputStream(localFile, true);
        ftpClient.setRestartOffset(localSize);
        InputStream input = ftpClient.retrieveFileStream(serverPath);
        byte[] b = new byte[1024];
        int length = 0;
        while ((length = input.read(b)) != -1) {
            out.write(b, 0, length);
            currentSize = currentSize + length;
            if (currentSize / step != process) {
                process = currentSize / step;
                if (process % 5 == 0) {  //每隔%5的进度返回一次
                    listener.onDownLoadProgress(MainActivity.FTP_DOWN_LOADING, process, null);
                }
            }
        }
        out.flush();
        out.close();
        input.close();
        // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
        if (ftpClient.completePendingCommand()) {
            listener.onDownLoadProgress(MainActivity.FTP_DOWN_SUCCESS, 0, new File(localPath));
        } else {
            listener.onDownLoadProgress(MainActivity.FTP_DOWN_FAIL, 0, null);
        }

        // 下载完成之后关闭连接
        this.closeConnect();
        listener.onDownLoadProgress(MainActivity.FTP_DISCONNECT_SUCCESS, 0, null);
        return;
    }*/

    /**
     * 删除Ftp下的文件.
     *
     * @param serverPath
     *            Ftp目录及文件路径
     * @param listener
     *            监听器
     * @throws IOException
     */
    public void deleteSingleFile(String serverPath, DeleteFileProgressListener listener)
            throws Exception {
        // 打开FTP服务
        try {
           // this.openConnect();  不用重复打开
            listener.onDeleteProgress(FtpDownLoad.FTP_CONNECT_SUCCESSS);
        } catch (Exception e1) {
            e1.printStackTrace();
            listener.onDeleteProgress(FtpDownLoad.FTP_CONNECT_FAIL);
            return;
        }
        // 先判断服务器文件是否存在
        Log.d(TAG, "-----从服务器要删除的路径文件名 : " +serverPath);
        FTPFile[] files = ftpClient.listFiles(serverPath);
        if (files.length == 0) {
            listener.onDeleteProgress(FtpDownLoad.FTP_FILE_NOTEXISTS);
            return;
        }
        //进行删除操作
        boolean flag = true;
        flag = ftpClient.deleteFile(serverPath);
        if (flag) {
            listener.onDeleteProgress(FtpDownLoad.FTP_DELETEFILE_SUCCESS);
        } else {
            listener.onDeleteProgress(FtpDownLoad.FTP_DELETEFILE_FAIL);
        }
        // 删除完成之后关闭连接
        //this.closeConnect(); //不用重复关闭
        listener.onDeleteProgress(FtpDownLoad.FTP_DISCONNECT_SUCCESS);

        return;
    }

    /*
 * 上传进度监听
 */
    public interface UploadProgressListener {
        public void onUploadProgress(String currentStep, long uploadSize, File file);
    }

    /*
     * 下载进度监听
     */
    public interface DownLoadProgressListener {
        public void onDownLoadProgress(String currentStep, long downProcess, File file);
    }

    /*
     * 文件删除监听
     */
    public interface DeleteFileProgressListener {
        public void onDeleteProgress(String currentStep);
    }

}
