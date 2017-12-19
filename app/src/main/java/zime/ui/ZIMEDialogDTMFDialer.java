package zime.ui;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.brick.robotctrl.R;

import zime.media.ZIMEClientJni;
import zime.media.ZIMEVideoClientJNI;

public class ZIMEDialogDTMFDialer extends Dialog {

	private static ZIMEClientJni mAClient  = null;
	private static ZIMEVideoClientJNI mAVClient = null;
	private static ZIMEConfig mConfig = null;
	public static Button mButton_0 ;
	public static Button mButton_1 ;
	public static Button mButton_2 ;
	public static Button mButton_3 ;
	public static Button mButton_4 ;
	public static Button mButton_5 ;
	public static Button mButton_6 ;
	public static Button mButton_7 ;
	public static Button mButton_8 ;
	public static Button mButton_9 ;
	public static Button mButton_star ;
	public static Button mButton_well ;
	public static Button mButton_A ;
	public static Button mButton_B ;
	public static Button mButton_C ;
	public static Button mButton_D ;


	public ZIMEDialogDTMFDialer(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	// 引擎
	private final static String TAG = "ZIME.ui";

	public static class DTMFDialerBuilder{

		private Context context;

		public DTMFDialerBuilder(Context context)
		{
			this.context = context;
		}

		public int SetZIMESDKClient(ZIMEVideoClientJNI iAVClient, ZIMEClientJni iAClient, ZIMEConfig iConfig)
		{
			if(iAVClient == null && iAClient == null)
			{
				//Log.e(TAG, "Don't Create ZIMEVideoClientJNI & ZIMEClientJni the same time");
				Log.e(TAG, "ZIMEVideoClientJNI & ZIMEClientJni are null");
				return -1;
			}

			mAClient  = iAClient;
			mAVClient = iAVClient;
			mConfig = iConfig;

			return 0;
		}

		public ZIMEDialogDTMFDialer create()
		{
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final ZIMEDialogDTMFDialer dialog = new ZIMEDialogDTMFDialer(context, R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog_dtmfdialer, null);

			//按键初始化
			mButton_0 = (Button)layout.findViewById(R.id.btnDtmf0);
			mButton_1 = (Button)layout.findViewById(R.id.btnDtmf1);
			mButton_2 = (Button)layout.findViewById(R.id.btnDtmf2);
			mButton_3 = (Button)layout.findViewById(R.id.btnDtmf3);
			mButton_4 = (Button)layout.findViewById(R.id.btnDtmf4);
			mButton_5 = (Button)layout.findViewById(R.id.btnDtmf5);
			mButton_6 = (Button)layout.findViewById(R.id.btnDtmf6);
			mButton_7 = (Button)layout.findViewById(R.id.btnDtmf7);
			mButton_8 = (Button)layout.findViewById(R.id.btnDtmf8);
			mButton_9 = (Button)layout.findViewById(R.id.btnDtmf9);
			mButton_star = (Button)layout.findViewById(R.id.btnDtmfXing);
			mButton_well = (Button)layout.findViewById(R.id.btnDtmfJing);
			mButton_A = (Button)layout.findViewById(R.id.btnDtmfA);
			mButton_B = (Button)layout.findViewById(R.id.btnDtmfB);
			mButton_C = (Button)layout.findViewById(R.id.btnDtmfC);
			mButton_D = (Button)layout.findViewById(R.id.btnDtmfD);

			//设置回调
			mButton_0.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 0, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 0, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_0.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.i(TAG, "lj mConfig.mInbandDTMF = " + ZIMEConfig.mInbandDTMF);
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 0, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 0, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_1.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 1, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 1, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_2.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 2, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 2, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_3.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 3, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 3, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_4.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 4, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 4, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_5.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 5, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 5, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_6.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 6, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 6, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_7.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 7, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 7, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_8.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 8, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 8, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_9.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 9, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 9, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_star.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 10, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 10, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_well.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 11, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 11, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_A.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 12, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 12, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_B.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 13, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 13, !ZIMEConfig.mInbandDTMF, 500, 10);
						}
					}
				}
			});

			mButton_C.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 14, !ZIMEConfig.mInbandDTMF, 1000, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 14, !ZIMEConfig.mInbandDTMF, 1000, 10);
						}
					}
				}
			});

			mButton_D.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mConfig.mChannelId)
						{
							ZIMEVideoClientJNI.SendDTMF(mConfig.mChannelId, 15, !ZIMEConfig.mInbandDTMF, 100, 10);
						}
					}
					else
					{
						if (null != mAClient && -1 != mConfig.mChannelId)
						{
							ZIMEClientJni.SendDTMF(mConfig.mChannelId, 15, !ZIMEConfig.mInbandDTMF, 100, 10);
						}
					}
				}
			});


			dialog.setContentView(layout);
			return dialog;
		}
	}

}
