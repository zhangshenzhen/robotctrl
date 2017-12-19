package com.rg2.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class APPUtils
{

    /**
     * 〈获得本地MAC地址〉
     *
     * @return String
     * @throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
     * @since [起始版本]
     */
    @SuppressLint(
            {"NewApi", "DefaultLocale"})
    public static String getMacAddress()
    {
        String mac = "";
        try
        {
            byte[] m = NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()))
                    .getHardwareAddress();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < m.length; i++)
            {
                if (i != 0)
                {
                    sb.append("-");
                }
                String s = Integer.toHexString(m[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            mac = sb.toString().toUpperCase();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return mac;
    }

    /**
     * <获取本机IP>
     *
     * @return String
     */
    public static String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); )
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); )
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex)
        {
            LogUtil.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    /**
     * <获取当前应用的版本号>
     *
     * @param mContext
     * @return String
     */
    public static String getVersionName(Context mContext)
    {
        // 获取packagemanager的实例
        String version = "1.0.0";
        try
        {
            PackageManager packageManager = mContext.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (NameNotFoundException e)
        {
            e.printStackTrace();
            return version;
        }

        return version;
    }

    /**
     * 〈获取手机设备号〉
     *
     * @return String
     * @throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
     * @since [起始版本]
     */
    public static String getDeviceId(Context mContext)
    {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder sb = new StringBuilder();
        String mDeviceId = tm.getDeviceId();
        if (StringUtils.stringIsEmpty(mDeviceId))
        {
            mDeviceId = StringUtils.getUUid().replace("-", "").substring(0, 15);
        }
        return mDeviceId;
    }

    /**
     * 〈获取手机分辨率〉
     *
     * @param mContext
     * @return String
     * @throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
     * @since [起始版本]
     */
    public static String getResolution(Activity mContext)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        return heightPixels + "*" + widthPixels;
    }

    /**
     * 获取屏幕分辨率宽
     */
    public static int getScreenWidth(Context context)
    {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕分辨率高
     */
    public static int getScreenHeight(Context context)
    {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取屏幕分辨率宽计算dialog的宽度
     */
    public static int dip2px(Context context, float dipValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static boolean isAppInstalled(Context context, String packageName)
    {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        if (pinfo != null)
        {
            for (int i = 0; i < pinfo.size(); i++)
            {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    /*获取当前系统的android版本号*/
    public static int getCurrentapiVersion()

    {
        return android.os.Build.VERSION.SDK_INT;

    }







}
