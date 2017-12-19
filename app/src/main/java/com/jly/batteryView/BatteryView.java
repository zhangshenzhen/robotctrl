package com.jly.batteryView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by jiangly on 2016/7/9.
 */
public class BatteryView extends View {

    private static int mPower =35;

    private short batteryFlag = 0;

    public BatteryView(Context context) {
        super(context);
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int battery_left = 440;
        int battery_top = 20;
        int battery_width = 25;
        int battery_height = 15;

        int battery_head_width = 3;
        int battery_head_height = 3;

        int battery_inside_margin = 3;


        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        Rect rect = new Rect(battery_left, battery_top,
                battery_left + battery_width, battery_top + battery_height);
        canvas.drawRect(rect, paint);

        float power_percent = mPower/35.0f;

        Paint paint2 = new Paint(paint);

        paint2.setStyle(Paint.Style.FILL);
        if(power_percent>0.15f&&power_percent<0.5f) {
            paint2.setColor(Color.YELLOW);
            batteryFlag = 1;
        } else if(power_percent>=0.5f) {
            paint2.setColor(Color.GREEN);
            batteryFlag = 1;
        } else {
            paint2.setColor(Color.RED);
            paint2.setStrokeWidth(5.0f);
            canvas.drawLine(448,22,458,33,paint2);
        }
        //画电量
        if(batteryFlag == 1) {
            int p_left = battery_left + battery_inside_margin;
            int p_top = battery_top + battery_inside_margin;
            int p_right = p_left - battery_inside_margin + (int)((battery_width - battery_inside_margin) * power_percent);
            int p_bottom = p_top + battery_height - battery_inside_margin * 2;
//            Log.d("get", "p_left "+p_left+"p_top "+p_top+"p_right "+p_right+"p_bottom "+p_bottom);
            Rect rect2 = new Rect(p_left, p_top, p_right , p_bottom);
            canvas.drawRect(rect2, paint2);
            batteryFlag = 0;
        }
        Paint paint3 = new Paint(paint);
        paint2.setColor(Color.GREEN);
        paint2.setStyle(Paint.Style.FILL);
        int h_left = battery_left + battery_width;
        int h_top = battery_top + battery_height / 2 - battery_head_height / 2;
        int h_right = h_left + battery_head_width;
        int h_bottom = h_top + battery_head_height;
        Rect rect3 = new Rect(h_left, h_top, h_right, h_bottom);
        canvas.drawRect(rect3, paint3);
    }
    public void setPower(int power) {
        mPower =power-200;
        if(mPower < 0) {
            mPower = 0;
        }
        invalidate();
    }
}

