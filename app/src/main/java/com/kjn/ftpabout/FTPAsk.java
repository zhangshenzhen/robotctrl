package com.kjn.ftpabout;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kjnijk on 2016-08-13.
 */
public class FTPAsk {
    private final String TAG = "FTPAsk";
    private String hostName;
    private String userName;
    private String password;
    private FTPClient ftpClient;
    private List<FTPFile> list;
    private String currentPath = "";
    private double response;

    /**
     * 构造函数.
     * @param host hostName 服务器名
     * @param user userName 用户名
     * @param pass password 密码
     */
    public FTPAsk(String host, String user, String pass) {

        this.hostName = host;
        this.userName = user;
        this.password = pass;
        this.ftpClient = new FTPClient();
        this.list = new ArrayList<FTPFile>();
    }

    /**
     * 打开FTP服务.
     * @throws IOException
     */
    public void openConnect() throws IOException {
        // 中文转码
        ftpClient.setControlEncoding("UTF-8");
        int reply; // 服务器响应值
        // 连接至服务器
        ftpClient.connect(hostName,2121);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        Log.d(TAG, "openConnect: " + reply);
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        }
        // 登录到服务器
        ftpClient.login(userName, password);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        } else {
            // 获取登录信息
            FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
            config.setServerLanguageCode("zh");
            ftpClient.configure(config);
            // 使用被动模式设为默认
            ftpClient.enterLocalPassiveMode();
            // 二进制文件支持
            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
//            ftpClient.setControlEncoding("GBK");
            System.out.println("login");
        }
    }
    /**
     * 关闭FTP服务.
     * @throws IOException
     */
    public void closeConnect() throws IOException {
        if (ftpClient != null) {
            // 登出FTP
            ftpClient.logout();
            // 断开连接
            ftpClient.disconnect();
            System.out.println("logout");
        }
    }
    /**
     * 下载单个文件.
     * @param localFile 本地目录
     * @param ftpFile FTP目录
     * @return true下载成功, false下载失败
     * @throws IOException
     */
    public boolean downloadSingle(File localFile, FTPFile ftpFile) throws IOException {
        boolean flag = false;
        // 创建输出流
        Log.d(TAG, "downloadSingle: 1");
        // 统计流量
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(localFile));
        response += ftpFile.getSize();
        // 下载单个文件
        Log.d(TAG, "downloadSingle: 2");
        flag = ftpClient.retrieveFile(localFile.getName(), out);
        // 关闭文件流
        Log.d(TAG, "downloadSingle: 3");
        out.close();
        return flag;
    }

    public List<FTPFile> listFiles(String remotePath) throws IOException {
        // 获取文件
        FTPFile[] files = ftpClient.listFiles(remotePath);
        // 遍历并且添加到集合
        for (FTPFile file : files) {
            Log.d(TAG, "listFiles: " + files);
            list.add(file);
        }
        return list;
    }
    public boolean download(String remotePath, String fileName, String localPath) throws IOException {
        boolean flag = false;
        Result result = null;
        // 初始化FTP当前目录
        currentPath = remotePath;
        // 初始化当前流量
        response = 0;
        // 更改FTP目录
        ftpClient.changeWorkingDirectory(remotePath);
        // 得到FTP当前目录下所有文件
        FTPFile[] ftpFiles = ftpClient.listFiles();
        // 循环遍历
        for (FTPFile ftpFile : ftpFiles) {
            // 找到需要下载的文件
            if (ftpFile.getName().equals(fileName)) {
                System.out.println("download..." + fileName);
                // 创建本地目录
                File file = new File(localPath + "/" + fileName);
                // 下载前时间

                    Date startTime = new Date();
                if(!file.exists()) {
                    flag = downloadSingle(file, ftpFile);
                    // 下载完时间
                }else{
                    long lRemoteSize = ftpFiles[0].getSize();
                    long localSize = file.length();
                    if(localSize>=lRemoteSize){
                        Log.d(TAG, "download: 文件已存在" + fileName);
                    }
                    else {
                        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file, true));
                        ftpClient.setRestartOffset(localSize);
                        flag = ftpClient.retrieveFile(file.getName(), out);
                        out.close();

                    }
                }
                    Date endTime = new Date();
                    // 返回值
//                    result = new Result(flag, Util.getFormatTime(endTime.getTime() - startTime.getTime()), Util.getFormatSize(response));

            }
        }
        return flag;
    }


}
