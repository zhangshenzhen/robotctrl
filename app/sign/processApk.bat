echo off

del RobotCtrl_master_*.apk
copy ..\build\outputs\apk\RobotCtrl_master_*.apk .\
java -jar %~dp0signapk.jar "platform.x509.pem" "platform.pk8" RobotCtrl_master_*.apk RobotCtrl_master.apk

adb install RobotCtrl_master.apk
pause