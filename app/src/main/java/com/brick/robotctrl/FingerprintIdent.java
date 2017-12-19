package com.brick.robotctrl;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FingerprintIdent {
    public static String TAG = "FingerprintIdent";

    public static boolean addFingerprint() {
        String resultMsg = "";
        resultMsg = do_exec("/system/bin/echo high > /sys/class/gpio/gpio205/direction\n");
        if (resultMsg == "") {
            return false;
        }
        return true;
    }

    // end == reset
    public static boolean endFingerprint() {
        String resultMsg = "";
        resultMsg = do_exec("/system/bin/echo high > /sys/class/gpio/gpio36/direction\n");
        if (resultMsg == "") {
            return false;
        }
        return true;
    }

    public static boolean delFingerprint() {
        String resultMsg = "";
        resultMsg = do_exec("/system/bin/echo high > /sys/class/gpio/gpio101/direction");
        if (resultMsg == "") {
            return false;
        }
        return true;
    }

    public static boolean fingerIdentSuccess() {
        String resultMsg = "";
        resultMsg = do_exec("/system/bin/cat /sys/class/gpio/gpio34/value");
        if (resultMsg == "") {
            return false;
        } else {
            String[] result = resultMsg.split("|");
            if (result[0].equals("0")) {
                return true;
            } else {
                return false;
            }
        }
    }


    public static String[] shellCommand = {

//            // touch out
//            "/system/bin/echo 34 > /sys/class/gpio/export",
//            "/system/bin/echo in > /sys/class/gpio/gpio34/direction",
    };

    public static void exeShellCommand() {
        List<String> commands = new ArrayList<String>();


        for(String str: shellCommand) {
            commands.add(str);
//            do_exec(str);
        }
//        Log.d(TAG, "exeShellCommand: " + ShellUtils.execCommand(commands, false, true));
    }

    public static String do_exec(String cmd) {
        Log.d(TAG, "do_exec: " + cmd);
        String resultMsg = "";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                resultMsg += line + "|";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultMsg;
    }
}
