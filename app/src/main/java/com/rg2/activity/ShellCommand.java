package com.rg2.activity;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brick on 2016/12/13.
 */

public class ShellCommand
{
    public static String TAG = "ShellCommand";

    public static String[] shellCommand = {
            // for test
//            "mkdir /sdcard/1111",
//            // touch add
            "echo low > /sys/class/gpio/gpio205/direction\n",
            // touch end/reset
//            "/system/bin/echo high > /sys/class/gpio/gpio36/direction\n",
//            // touch delete
//            "/system/bin/echo high > /sys/class/gpio/gpio101/direction",
//            // touch out
//            "/system/bin/cat /sys/class/gpio/gpio34/value",
    };

    public static void exeShellCommand() {
        List<String> commands = new ArrayList<String>();


        for(String str: shellCommand) {
            commands.add(str);
//            do_exec(str);
        }
        Log.d(TAG, "exeShellCommand: " + ShellUtils.execCommand(commands, false, true));
    }

    public static String do_exec(String cmd) {
        Log.d(TAG, "do_exec: " + cmd);
        String s = "";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                s += line + "    |     ";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "do_exec: return: " + s);
        return cmd;
    }
}
