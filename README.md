# RobotCtrl - 南大电子服务型机器人端控制器

## BUGS记录

- [x] 客户端启动时上报位置不是设置xml中记录的内容，而是变量默认选项。
- [ ] 检测网络连接应该采用广播接收器，然后再判断ssdb服务器是否在。
- [ ] 机器人属性应该统一到一个类中，方便属性的管理。
- [ ] ssdb命令字应该统一到一个结构当中（类/枚举等）,方便控制字的管理。
- [ ] 电量获取中问题需要解决。
- [ ] ssdb连接问题需要优化。

2016-08-17

- [x] 去除和设置位置相关的程序。

## 功能添加

- [ ] 授时服务器同步时间。
- [x] 添加关于界面介绍控制器与南大电子信息。
- [x] 设置里添加“开发者选项”选项。
- [x] 媒体文件远程更新。
- [x] 添加开机/修改地理信息/收到Location事件上报地理位置信息。

2016-08-17

- [x] 向SSDB写入视频播放进度信息。
- [ ] 写入SSDB的汉子转为GBK编码。
- [ ] Android软关机。
- [ ] 打开浏览器自动进入路由器设置界面。
- [ ] 媒体播放的格式是哪些？需要确定下(mp4/3gp)
- [x] APK加入出厂设置
- [x] 文档说明：替换开机画面/将APK变为launcher
- [x] APK更新/视频推送

2016-08-23

- [x] 播放进度提交
- [x] 网络状态检测
- [ ] 电量值写入SSDB
- [ ] 变声系统（双平台无缝衔接）

## 版本说明

版本规则：

RobotCtrl_vNUM1.NUM2.NUM3.NUM4_alpha（commit_id）

    1. 功能1添加NUM2++
    2. 功能2添加NUM2++
    1. BUG1修复NUM3++
    2. BUG2修复NUM3++

- NUM1代表大幅度框架升级
- NUM2代表新功能添加
- NUM3代表BUG修复个数
- NUM4代表发布月日
- commit_id代表发布时commit的id号

**注意首先填写功能添加列表和BUG修复列表，最后相应更新版本号。**
**NUM4为发布日期，不要存在太多的版本号，只有在发布，或者有必要更新版本号时才产生新的版本号**
**新产生的apk版本要备份到ftp中**

## 版本更新

1. RobotCtrl_master_v1.15.9.0815_alpha(fa0c466)

    1. 左上1/4屏点击5次进入设置界面
    2. 右上1/4屏点击5次进入关于界面
    3. 屏蔽音乐自动播放
    4. PC端指定视频文件进行播放
    5. PC端可进行单曲播放，单曲循环播放，从头多曲循环播放和从指定文件多曲循环播放
    6. 通过访问管理服务器，获得位置信息，并上报PC端

2. RobotCtrl_master_v1.15.10.0816_alpha

    1. 修复由于协议问题导致的电机控制失败

3. RobotCtrl_master_v1.17.11.0819_alpha

    1. 智能语音
    2. 开机自启动
    3. 修复速度调控（串口测试）

4. RobotCtrl_master_v1.18.11.0820_alpha

    1. Single模式播放结束发送消息

5. RobotCtrl_master_v1.19.13.0825_alpha

    1. 修复上传视频列表最后多一个空格
    2. 添加视频播放进度提交
    3. 0~100按比例映射到0~20

6. RobotCtrl_master_v1.20.16.0828_alpha

    1. 网络状态接收器，网络恢复重连ssdb
    2. 修改apk更新下载到Download
    3. 修复视频播放暂停，停止以及进度提交的bug
    4. 去除settingActivity中多余的的robotIDkey

7. RobotCtrl_master_v1.21.17.0829_alpha

    1. 添加ftp同步更新功能
    2. 检查ssdb发送请求

8. RobotCtrl_master_v1.22.20.0831_alpha

    1. 修改机器人速度调节
    2. 更改电池电压值显示范围
    3. 修复电池bug，for循环不起作用
    4. 增加发生异常自动将错误log信息发送到指定邮箱，在setting中添加email设置

9. RobotCtrl_master_v1.22.21.0901_alpha

    1. 调整电池低电量和充满电量的范围
    
10. RobotCtrl_master_v1.23.22.0903_alpha

    1. 添加智能语音editxt输入
    2. 修改智能语音相关服务器IP

11. RobotCtrl_master_v1.24.23.0904_alpha

    1. 修改灵云智能语音部分
    2. 添加FTP断点续传功能

12. RobotCtrl_master_v1.24.25.0905_alpha

    1. 智能语音提问按钮5s逻辑
    2. 退出界面打断TTS

13. RobotCtrl_master_v1.25.25.0912_alpha

    1. 循环播放图片

14. RobotCtrl_master_v1.26.25.0913_alpha

    1. 添加同时更新两个apk

15. RobotCtrl_master_v1.27.30.0922_alpha

    1. 修复以前版本中存在的bug(开机自动更新apk，报空指针的错)
    2. 修改安装APK的顺序，解决pcm APK安装问题
    3. ftp中增加以robotName为命名的目录，并以此为目录作为更新，如果该目录不存在，则更新/东南/更新目录
    4. 连接SSDB连接失败给出提示，并在5s后自动重连，无限次重复直到连接上(修复SSDB连接问题)
    5. 更改ftp目录为：1.robotName; 2. common； 修改SSDB默认地址：60.171.108.192
    6. 修复开机启动时更新无法获取robotName问题

16. RobotCtrl_master_v1.27.33.0923_alpha

    1. 修复安装完成后不会显示完成打开问题
    2. 优化ftp下载问题
    3. 优化apk安装问题

17. RobotCtrl_master_v1.29.36.0926_alpha

    1. 修复说话和害怕表情互换的问题
    2. 优化表情的调用函数
    3. 添加开机上报appVersion功能
    4. 解决开机无法上传地理位置的bug
    5. 加入SSDB重启功能（event:reboot）和右上1/4重启（连续五次点击）

18. RobotCtrl_master_v1.30.36.0927_alpha

    1. 添加读取SSDB Message键位并TTS读出