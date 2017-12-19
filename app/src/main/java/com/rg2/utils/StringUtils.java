package com.rg2.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils
{

    /**
     * 判断是否正整数
     *
     * @param string
     * @return
     */
    public static boolean isNumeric(String string)
    {

        boolean flag = false;
        if (!stringIsEmpty(string))
        {
            Pattern p = Pattern.compile("[1-9]{1}[0-9]{0,1}");
            Matcher m = p.matcher(string);
            flag = m.matches();
        }
        return flag;
    }

    public static boolean isAllNumeric(String str)
    {
        Pattern pattern = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断字符串是否为�?
     *
     * @param str
     * @return
     */
    public static boolean stringIsEmpty(String str)
    {
        if ("".equals(str) || null == str || "null".equals(str))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String getFormTime(long time)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        return df.format(time);// new Date()为获取当前系统时间
    }

    public static String getDateToString(Date date)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        return df.format(date);// new Date()为获取当前系统时间
    }

    SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date();
    String str  = sdf.format(date);

    public static String formatTime(long ms)
    {
        if (ms == 0)
        {
            return "00:00:00";
        }
        // 将毫秒数换算成x天x时x分x秒x毫秒
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day;
        String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;
        return strHour + ":" + strMinute + ":" + strSecond + " ";
    }

    public static String getCurrentTime(String format)
    {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String getCurrentTime()
    {
        return getCurrentTime("yyyy-MM-dd");
    }

    public static int getIndex(String str, String[] codeArr)
    {
        int mIndex = 0;

        if (!stringIsEmpty(str))
        {
            for (int i = 0; i < codeArr.length; i++)
            {
                if (str.equals(codeArr[i]))
                {
                    mIndex = i;
                }
            }
        }

        return mIndex;
    }

    /**
     * 〈比较时间〉
     *
     * @param DATE1
     * @param DATE2
     * @return int
     * @throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
     * @since [起始版本]
     */
    public static int compareDate(String DATE1, String DATE2)
    {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime())
            {
                System.out.println("dt1 在dt2前");
                return 1;
            }
            else if (dt1.getTime() < dt2.getTime())
            {
                System.out.println("dt1在dt2后");
                return -1;
            }
            else
            {
                return 0;
            }
        } catch (Exception exception)
        {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 〈判断邮箱格式〉
     *
     * @param email
     * @return boolean
     * @throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
     * @since [起始版本]
     */
    public static boolean checkEmail(String email)
    {
        Pattern pattern = Pattern
                .compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 〈手机号码正则〉
     *
     * @param mobiles
     * @return boolean
     * @throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
     * @since [起始版本]
     */
    public static boolean isMobileNO(String mobiles)
    {
        Pattern p = Pattern.compile("^((13[0-9])|(17[0-9])|(15[^4,\\D])|(176)|(18[0-9]))\\d{8}$");

        Matcher m = p.matcher(mobiles);

        return m.matches();
    }

    // 前一天
    public static String getBeforeDay(String time)
    {
        String day = null;
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(time);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            date1 = calendar.getTime();
            day = getDateToString(date1);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return day;
    }

    // 后一天
    public static String getNextDay(String time)
    {
        String day = null;
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(time);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date1 = calendar.getTime();
            day = getDateToString(date1);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return day;
    }


    public static String getFormatTime(String time)
    {

        String day = null;
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(time);
            day = getDateToString(date1);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return day;
    }


    public static String getUUid()
    {
        return UUID.randomUUID().toString();

    }

    /**
     * 〈获取明天时间〉
     *
     * @return String
     * @throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
     * @since [起始版本]
     */
    public static String getTomorrowTime()
    {
        Date date = new Date();// 取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);

        return dateString;
    }


    public static float getRating(String starLevel)
    {
        float mRating = 3;

        if ("1".equals(starLevel))
        {
            mRating = 1;
        }
        else if ("2".equals(starLevel))
        {
            mRating = (float) 1.5;
        }
        else if ("3".equals(starLevel))
        {
            mRating = (float) 2;
        }
        else if ("4".equals(starLevel))
        {
            mRating = (float) 2.5;
        }
        else if ("5".equals(starLevel))
        {
            mRating = (float) 3;
        }
        else if ("6".equals(starLevel))
        {
            mRating = (float) 3.5;
        }
        else if ("7".equals(starLevel))
        {
            mRating = (float) 4;
        }
        else if ("8".equals(starLevel))
        {
            mRating = (float) 4.5;
        }
        else if ("9".equals(starLevel))
        {
            mRating = (float) 5;
        }

        return mRating;
    }

    public static String formatString(String str)
    {
        if (stringIsEmpty(str))
        {
            return "";
        }

        return str;
    }




    // 特殊字符过滤
    public static String stringFilter(String str)
    {
        String regEx = "[`~!@#$^&*()=|{}':;',\"\\[\\].<>~！@#￥……&*（）&;—|{}【】《》‘；：”“'。，、？]"; // 要过滤掉的字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick()
    {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800)
        {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 〈身份证中间显示****〉
     *
     * @param str
     * @return String
     * @throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
     * @since [起始版本]
     */
    public static String certificateFormat(String str)
    {
        String a = "";
        if (!stringIsEmpty(str) && str.length() > 8)
        {
            a = str.substring(0, 4) + "****" + str.substring(str.length() - 4, str.length());
        }

        return a;
    }

    //获取当前年龄
    public static String getAge(String time)
    {
        try
        {
            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            Date mydate = myFormatter.parse(time);
            long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000) + 1;
            String year = new java.text.DecimalFormat("#").format(day / 365f);
            return year;
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return "保密";
    }


    //获取工作年限
    public static String getWorkYears(String time)
    {
        try
        {
            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            Date mydate = myFormatter.parse(time);
            long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000) + 1;
            String year = new java.text.DecimalFormat("#").format(day / 365f);
            return year;
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return "未知";
    }





}
