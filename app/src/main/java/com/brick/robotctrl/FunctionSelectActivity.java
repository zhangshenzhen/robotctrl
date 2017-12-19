package com.brick.robotctrl;

import android.content.Intent;
import android.media.MediaRouter;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.presentation.FunctionSelectPresentation;
import com.rg2.activity.PrintActivity;

public class FunctionSelectActivity extends com.rg2.activity.BaseActivity {
    private static final String TAG ="FunctionSelectActivity" ;
    private FunctionSelectPresentation mfunctionSelectPresentation;
    private Button jiaohao;
    private Button fuwu;
    private TextView back;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_function_select);
     }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initData() {
        jiaohao = (Button) findViewById(R.id.btn_jiaohao);
        fuwu = (Button) findViewById(R.id.btn_fuwu);
        back = (TextView) findViewById(R.id.tv_back);
    }

    @Override
    protected void initViewData() {
        jiaohao.setOnClickListener(this);
        fuwu.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_jiaohao:
                //进入叫号界面;
                startActivity(new Intent(FunctionSelectActivity.this, PrintActivity.class));
                break;
            case R.id.btn_fuwu:
                //进入表情界面;
               ExpressionActivity.startAction(FunctionSelectActivity.this,1);
                break;
            case R.id.tv_back:
                finish();
                Log.i(TAG, "onClick: 返回了");
                break;
        }
    }

    @Override
    protected void updatePresentation() {
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay =  route  !=  null ? route.getPresentationDisplay() : null;
        if (mfunctionSelectPresentation != null && mfunctionSelectPresentation.getDisplay() !=  presentationDisplay) {
            mfunctionSelectPresentation.dismiss();
            mfunctionSelectPresentation = null;
        }
        if (mfunctionSelectPresentation == null &&  presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            Log.d(TAG, "MainPresentation............main ..2");
            mfunctionSelectPresentation = new FunctionSelectPresentation(this,  presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;
            // Log.d(TAG, "updatePresentation: this: "+ this.toString());
            mfunctionSelectPresentation.setOnDismissListener(mOnDismissListener);

            // Try to show the presentation, this might fail if the display has
            // gone away in the mean time
            try {
                mfunctionSelectPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                // Couldn't show presentation - display was already removed
                // Log.d(TAG, "updatePresentation: failed");
                mfunctionSelectPresentation = null;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mfunctionSelectPresentation != null){
            mfunctionSelectPresentation.dismiss();
            mfunctionSelectPresentation = null;
        }
        Log.i(TAG, "onStop: 停止了么？");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
