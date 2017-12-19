package com.kjn.videoview;

import android.util.Log;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by kjnijk on 2016-08-13.
 */
public class HttpAsk {
    private static final String TAG = "HttpAsk";
    public static String posturl(String url){
        InputStream is = null;
        String result = "";

        try{
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        }catch(Exception e){
            return "Fail to establish http connection!"+e.toString();
        }

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + " ");
            }
            is.close();
            result=sb.toString();
//            Log.d(TAG, "posturl: " + result);
        }catch(Exception e){
            return "Fail to convert net stream!";
        }
        return result;
    }
}
