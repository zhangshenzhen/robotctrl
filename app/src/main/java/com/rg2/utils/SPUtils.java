package com.rg2.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SharedPreferences的工具类
 */

public class SPUtils {
    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "share_data";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *  @param context 上下文
     * @param key     键
     * @param object  值
     */
    public static int put(Context context, String key, Object object) {

        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
        return 0;
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context       上下文
     * @param key           键
     * @param defaultObject 默认值
     * @return 保存的数据
     */
    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }


    /**
     * 移除某个key值已经对应的值
     *
     * @param context 上下文
     * @param key     键
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     *
     * @param context 上下文
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context 上下文
     * @param key     键
     * @return true代表存在，false代表不存在
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context 上下文
     * @return 所有的键值对
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 将一个字符串添加进key中原本value值得末尾,用","连接
     *
     * @param context
     * @param key
     * @param value
     * @param values
     */
    public static void add(Context context, String key, String value, List<String> values) {
        if (values != null && !values.contains(value)) {
          //  LogUtils.e("sp中add添加前", values.toString() + values.size());
            values.add(value);
          //  LogUtils.e("sp中add,集合添加之后", values.toString());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < values.size(); i++) {
                if (i == values.size()-1) {
                    sb.append(values.get(i));
                }else {
                    sb.append(values.get(i) + ",");
                }
            }
            put(context,key,sb.toString());
        }
    }

    public static List<String> getList(Context context, String key) {
        List<String> values = new ArrayList<>();
        String value = (String) get(context,key,"");
       // LogUtils.e("sp中获取value", value.length() + "");
        if (value.length() == 0) {
            return values;
        }
        String[] strings = value.split(",");
      //  LogUtils.e("sp中获取集合", strings.length + "");
        for (String s : strings) {
            values.add(s);
        }
        return values;
    }

    /**
     * 获取集合
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
   /* public static <T> ArrayList<T> getObj(String key,Class<T> clazz) {
        List<String> values = new ArrayList<>();
     //   SharedPreferences sp = UIUtils.getContext().getSharedPreferences(FILE_NAME,
      //          Context.MODE_PRIVATE);
      //  String value = sp.getString(key, null);
        ArrayList<T> listOfT = new ArrayList<>();
        if (value != null) {
            Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();
            ArrayList<JsonObject> jsonObjs = new Gson().fromJson(value, type);
            for (JsonObject jsonObj : jsonObjs) {
                listOfT.add(new Gson().fromJson(jsonObj, clazz));
            }
        }
       // LogUtils.e(listOfT.toString()+"test");
        return listOfT;
    }*/

    /**
     * 存入对象
     * @param key
     * @param obj
     * @return
     */
//    public static void putObj(String key,Object obj) {
//       SharedPreferences sp = UIUtils.getContext().getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//            String s = new Gson().toJson(obj);
//           editor.putString(key,s).commit();
//      //  LogUtils.e(s+"test");
//    }


    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return apply的方法
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor Editor对象
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            editor.commit();
        }
    }

}

