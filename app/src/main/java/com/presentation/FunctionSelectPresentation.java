package com.presentation;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.brick.robotctrl.R;

/**
 * Created by lx on 2017/7/6.
 */

public class FunctionSelectPresentation extends BasePresentation {

    public FunctionSelectPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.persentation_function);

    }
}
