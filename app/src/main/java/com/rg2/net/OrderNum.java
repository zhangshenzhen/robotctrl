package com.rg2.net;

import android.util.Log;

import com.rg2.utils.InputStreamTools;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lx on 2017/12/7.
 */

public class OrderNum {

    private static final String TAG ="OrderNum.class" ;
    public static String stream;

    public static String getDate(){
    /*  new Thread(){
           @Override
           public void run() {
               super.run();*/
               String url = "http://192.168.3.115:8080/mm/Gson.json";
               //1,创建okheepclient对象
               OkHttpClient okHttpClient = new OkHttpClient();
               //2, 构建请求对象，请求体
               Request request = new Request.Builder().url(url).build();
               //3,创建发送请求
               okHttpClient.newCall(request).enqueue(new Callback() {
                   @Override
                   public void onFailure(Call call, IOException e) {
                   }
                   @Override
                   public void onResponse(Call call, Response response) throws IOException {
                       stream = InputStreamTools.readStream(response.body().byteStream());
                       Log.d(TAG, "print4  : "+stream);
                   }
               });
      /*    }
          }.start();*/
        return stream;
     }


}
