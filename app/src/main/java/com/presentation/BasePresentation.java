package com.presentation;

import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;



/**
 * Created by shenzhen on 2017/1/13.
 */

public abstract class BasePresentation extends Presentation {
    ViewGroup mDesktop;
    Context mContext;
    public BasePresentation(Context outerContext, Display display) {
        super(outerContext, display);
        mContext = outerContext;
    }
    public BasePresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Basepresentation","....1");
        //初始化 副屏幕的方法;
        initView(savedInstanceState);
        initViewData();
    }

    //抽象方法 初始化副屏幕的布局;
    protected abstract void initView(Bundle savedInstanceState);
    //初始化数据的方法, 由其子类根据情况啊选择是否实现;
    public void initViewData(){};

     public Okienko setApp(Intent intent) {
        return new Okienko(mContext, mDesktop, intent);
    }
}
