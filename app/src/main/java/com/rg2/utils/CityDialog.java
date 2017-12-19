package com.rg2.utils;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.rg2.listener.MyOnClickListener;
import com.rg2.wheel.OnWheelChangedListener;
import com.rg2.wheel.WheelView;
import com.rg2.wheel.adapters.ArrayWheelAdapter;


/**
 * 作者：王先云 on 2016/9/1 14:09
 * 邮箱：wangxianyun1@163.com
 * 描述：一句话简单描述
 */
public class CityDialog extends BaseCity
{

    private Activity mContext;
    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;

    public Dialog showCityDialog(Activity mContext, final MyOnClickListener mClickListener)
    {
        this.mContext = mContext;
        final Dialog dialog = new Dialog(mContext, R.style.dialogNoAnimation);
        dialog.setCancelable(false);
        final View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_city, null);
        dialog.setContentView(view);

        mViewProvince = (WheelView) view.findViewById(R.id.id_province);
        mViewCity = (WheelView) view.findViewById(R.id.id_city);
        mViewDistrict = (WheelView) view.findViewById(R.id.id_district);

        TextView mCancelTv = (TextView) view.findViewById(R.id.tv_cancel);
        TextView mSubmitTv = (TextView) view.findViewById(R.id.tv_submit);

        mCancelTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                dialog.dismiss();
            }
        });

        mSubmitTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mClickListener.onClicked(mCurrentProviceName + "/" + mCurrentCityName + "/"
                        + mCurrentDistrictName);
                dialog.dismiss();
            }
        });


        // 添加change事件
        mViewProvince.addChangingListener(new OnWheelChangedListener()
        {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue)
            {
                updateCities();
            }
        });
        // 添加change事件
        mViewCity.addChangingListener(new OnWheelChangedListener()
        {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue)
            {
                updateAreas();
            }
        });
        // 添加change事件
        mViewDistrict.addChangingListener(new OnWheelChangedListener()
        {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue)
            {
                mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
                mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
            }
        });


        initProvinceDatas(mContext);
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(mContext,mProvinceDatas));
        // 设置可见条目数量
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
        mViewProvince.setCurrentItem(4);
        updateCities();
        updateAreas();

        //Dialog部分
        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels;
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setAttributes(lp);
        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas()
    {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null)
        {
            areas = new String[]{""};
        }
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(mContext, areas));
        mViewDistrict.setCurrentItem(0);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities()
    {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null)
        {
            cities = new String[]{""};
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(mContext, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }


}
