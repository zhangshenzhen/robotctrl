package com.presentation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import com.brick.robotctrl.R;

/**
 * Created by shenzhen on 2017/1/13.
 */

public class MainPresentation extends  BasePresentation {
    public MainPresentation(Context outerContext, Display display) {
        super(outerContext, display);


    }


    @Override
      protected void initView(Bundle savedInstanceState) {
        Log.d("MainPresentation", "MainPresentation............main ..2");
        setContentView(R.layout.persentation_main);
     }


}
