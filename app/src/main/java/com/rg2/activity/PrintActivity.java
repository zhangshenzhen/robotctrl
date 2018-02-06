package com.rg2.activity;

import android.media.MediaRouter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.brick.robotctrl.SerialCtrl;
import com.presentation.PrintPresentation;
import com.rg2.utils.LogUtil;
import com.rg2.utils.SPUtils;
import com.rg2.utils.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brick on 2016/12/10.
 */
public class PrintActivity extends BaseActivity{
    private static final String TAG = "PrintActivity";
    private TextView   mBackTv;
    private  Button   mSubmitBnt1, mSubmitBnt2, mSubmitBnt3;
    SerialCtrl serialCtrlPrinter = null;
    //副屏
    private PrintPresentation mPrintPresentation;



    @Override
    protected void initData() {
    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_print);

        LogUtil.e(TAG, "..initViews.currentTimeMillis()"+System.currentTimeMillis());

        mBackTv = (TextView) findViewById(R.id.tv_back);
        mSubmitBnt1 = (Button) findViewById(R.id.btn_submit1);
        mSubmitBnt2 = (Button) findViewById(R.id.btn_submit2);
        mSubmitBnt3 = (Button) findViewById(R.id.btn_submit3);


    }

    @Override
    protected void initEvent()
    {
        mSubmitBnt1.setOnClickListener(this);
        mSubmitBnt2.setOnClickListener(this);
        mSubmitBnt3.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
    }

    @Override
    protected void initViewData()
    {
    // serialCtrlPrinter = new SerialCtrl(PrintActivity.this, new Handler(), "ttyUSB1", 9600, "printer");
       serialCtrlPrinter = new SerialCtrl(PrintActivity.this, new Handler(), "ttyS1", 9600, "printer");

    }


    @Override
    protected void updatePresentation() {
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay =  route  !=  null ? route.getPresentationDisplay() : null;

        // 注释 : Dismiss the current presentation if the display has changed.
        if (mPrintPresentation != null && mPrintPresentation.getDisplay() !=  presentationDisplay) {
            mPrintPresentation.dismiss();
            mPrintPresentation = null;
        }
        if (mPrintPresentation == null &&  presentationDisplay != null) {
            mPrintPresentation = new PrintPresentation(this,  presentationDisplay);
            mPrintPresentation.setOnDismissListener(mOnDismissListener);
            // Try to show the presentation, this might fail if the display has
            // gone away in the mean time
            try {
                Log.e("SamplePresentation","........PrintActivity........28");
                if(countNum >=10){
                    mPrintPresentation.initViewData(false);
                    Log.d("PrintActivity","..提示没有打印纸。。。");
                }
                mPrintPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                // Couldn't show presentation - display was already removed
                mPrintPresentation = null;
            }
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_submit1:
                CountPtint();
                print1();
                break;
            case R.id.btn_submit2:
                CountPtint();
                print2();
                break;
            case R.id.btn_submit3:
                CountPtint();
               // print3();
                print4();
                break;
            case R.id.tv_back:
                finish();
                break;
        }
    }


    //计数器
     private  int countNum ;

    public void CountPtint(){
        //采用sp进行物理存储，打开退出关闭程序不影响计数器的数值;
        countNum = (int) SPUtils.get(mContext, "countNum",0);
         Log.d(TAG,"countNum= 取出"+countNum);
        if (countNum<9){
            ++countNum;
            Log.d(TAG,"countNum=  打印"+countNum);
            SPUtils.put(mContext,"countNum", countNum);//存储变量
            mPrintPresentation.initViewData(true);
          }else {
            ++countNum;
            Log.d(TAG,"countNum="+countNum+"还剩"+(10-countNum)+"张纸");
            countNum = 0;//复原变量到初始值；
            SPUtils.put(mContext,"countNum", countNum);
            Log.d(TAG,"countNum="+countNum);
          mPrintPresentation.initViewData(false);
        }
        mPrintPresentation.show();
    }

    private void print1(){
        //  String str ="1234567890ABCDEFGHIJ中华人民共和";
        //  String str="中华人民共和";
        String time = StringUtils.getDateToString(new Date());
           //每行24个字符
        String str0 = "                        ";
        String str1 = "              2016-12-09";
        String str2 = "                        ";
        String str3 = "                        ";
        String str4 = "               8号      ";
        String str5 = "                        ";
        String str6 = "                        ";
        String str7 = "对公业务    柜台032     ";
        String str8 = "                        ";
        String str9 = "                        ";

        sendPortText(str9);
        sendPortText(str8);
        sendPortText(str7);
        sendPortText(str6);
        sendPortText(str5);
        sendPortText(str4);
        sendPortText(str3);
        sendPortText(str2);
        sendPortText(str1);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);

    }

    private void print2()
    {
        //  String str ="1234567890ABCDEFGHIJ中华人民共和";
        String time = StringUtils.getDateToString(new Date());

        String str0 = "                                ";
        String str1 = "                      2016-12-12";
        String str2 = "                                ";
        String str3 = "                                ";
        String str4 = "               9号              ";
        String str5 = "                                ";
        String str6 = "                                ";
        String str7 = "对私业务    柜台032             ";
        String str8 = "                                ";
        String str9 = "                                ";
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str8);
        sendPortText(str7);
        sendPortText(str6);
        sendPortText(str5);
        sendPortText(str4);
        sendPortText(str3);
        sendPortText(str2);
        sendPortText(str1);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
    }

    private void print3()
    {
        //  String str ="1234567890ABCDEFGHIJ中华人民共和";
        String time = StringUtils.getDateToString(new Date());

        String str0 = "                                ";
        String str1 = "                      2016-12-13";
        String str2 = "                                ";
        String str3 = "                                ";
        String str4 = "               16号             ";
        String str5 = "                                ";
        String str6 = "                                ";
        String str7 = "理财业务    柜台032             ";
        String str8 = "                                ";
        String str9 = "                                ";
       sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str8);
        sendPortText(str7);
        sendPortText(str6);
        sendPortText(str5);
        sendPortText(str4);
        sendPortText(str3);
        sendPortText(str2);
        sendPortText(str1);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
    }

    private void print4() {
        String url = "http://192.168.43.28:8080/mm/Gson.json";
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
                // String   stream = InputStreamTools.readStream(response.body().byteStream());
                String str0 = "                                ";
               // String str1 = "  "+R.drawable.ic_gf_logo+" "+"\n";
                String   stream = response.body().string();
                        Log.d(TAG, "print4  : "+stream);
                   if (stream ==null){
                     print3();
                    }else {
                     sendPortText(str0);
                     sendPortText(str0);
                     sendPortText(str0);
                     sendPortText(stream+"\n");
                     sendPortText(stream+"\n");
                     sendPortText(stream+"\n");
                     sendPortText(stream+"\n");
                     sendPortText(str0);
                     sendPortText(str0);
                     sendPortText(str0);
                     sendPortText(str0);
                    }
            }
        });
       // String streamData = OrderNum.getDate();
       //Log.d(TAG, "print4  : "+streamData);
    }

    private void sendPortText(String content)
    {
        byte[] temp = null;
        try
        {
            temp = content.getBytes("gbk");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        serialCtrlPrinter.sendPortText(serialCtrlPrinter.ComA, temp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"打印的Activity");
        LogUtil.e(TAG, "..System.currentTimeMillis()"+System.currentTimeMillis());
       // updatePresentation();//在父类中已经被调用了
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPrintPresentation != null){
            mPrintPresentation.dismiss();
            mPrintPresentation = null;
        }
        Log.i(TAG, "onStop: 停止了么？");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
