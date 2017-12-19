package com.rg2.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.rg2.listener.MyOnClickListener;


/**
 * Created by Administrator on 2016/9/21.
 */
public class DialogUtils
{

    public static void showListDialog(String title, final String[] arr, Context mContext, final MyOnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(title)
                .setItems(arr, new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.onClicked(arr[which]);
                    }
                }).show();
    }



    /**
     *
     * @return
     */
    public static Dialog showNoticeDialog(Context mContext, String str, final View.OnClickListener onClickListener)
    {
        final Dialog dialog = new Dialog(mContext, R.style.dialogNoAnimation);
        dialog.setCancelable(false);
        View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm, null);
        dialog.setContentView(v);
        TextView mTitle = (TextView) v.findViewById(R.id.tv_title);
        mTitle.setText(str);
        ((Button) v.findViewById(R.id.btn_submit)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                onClickListener.onClick(v);
            }
        });


        //Dialog部分
        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = APPUtils.getScreenWidth(mContext) * 7 / 8;
        mWindow.setAttributes(lp);

        return  dialog;
    }
}
