package zime.ui;


import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.rg2.utils.LogUtil;

import zime.media.ZIMEClientJni;
import zime.media.ZIMEVideoClientJNI;
import zime.media.ZIMEVideoClientJNI.T_ZIMEAudioDownlinkStat;
import zime.media.ZIMEVideoClientJNI.T_ZIMEAudioUplinkStat;
import zime.media.ZIMEVideoClientJNI.T_ZIMEVideoDownlinkStat;
import zime.media.ZIMEVideoClientJNI.T_ZIMEVideoUplinkStat;

public class ZIMEJniThread extends Thread
{
	private static final String ZIMETAG = ZIMEJniThread.class.getCanonicalName();
	public static ZIMEMedia mZIMEMedia = null;
  private Handler mMsgHandler  = null;
	private ZIMEAudio mZIMEAudio = null;
	private ZIMEConfig mConf = null;
	private ZIMEVideoClientJNI mVJNI = null;
	private ZIMEClientJni mAJNI = null;
	private static final int ifGetQos = 1;
	private Object mActivity;
	private AudioManager am = null;

	public ZIMEJniThread(ZIMEVideoClientJNI i_VClientJni, ZIMEClientJni i_AClientJni) {
		mVJNI = i_VClientJni;
		mAJNI = i_AClientJni;


		mZIMEMedia = new ZIMEMedia(mVJNI);
		/*mZIMEMedia.Exit();
		mZIMEMedia = new ZIMEMedia(mVJNI);

		Log.d("1111111111111111111111","111111111111111111111111111111111111111111111111111111111111111111");*/
		mZIMEMedia.SetLogCallBack();

		// 创建引擎	
		int resultCode = ZIMEVideoClientJNI.SetLogPath("/sdcard");   // = 0;
		Log.e(ZIMETAG, "ZIMEVideoClient set log path: resultCode = " + resultCode);

		resultCode = ZIMEVideoClientJNI.Create();
		if(0 != resultCode){                                       // = -1;
			Log.e(ZIMETAG, "ZIMEVideoClient Create failed!!, Ret = " + resultCode);
			return;
		}

		//设置初始默认的日志级别
		ZIMEVideoClientJNI.SetLogLevel(ZIMEMedia.ZIME_L_DEBUG);

		resultCode = ZIMEClientJni.Create();
		if(0 != resultCode){
			Log.e(ZIMETAG, "ZIMEAudioClient Create failed!!, Ret = " + resultCode);
			return;
		}
		mZIMEAudio = new ZIMEAudio(mAJNI);

		Log.e(ZIMETAG, "ZIMEJniThread Constructor--------- ");
	}

	public void SetActivity(Object activity)
	{
		mActivity = activity;
	}

	public int Input(final int msgWhat, final Object msgObj){
		Log.d(ZIMETAG, "Thread.currentThread=0"+Thread.currentThread());
				while(null == mMsgHandler) {
				}
				Log.d(ZIMETAG, "Thread.currentThread=1"+Thread.currentThread());
				Message msg = mMsgHandler.obtainMessage();
			 	//Message msg = Message.obtain();
				Log.d(ZIMETAG, "msgObj="+msgObj  +"--msgWhat="+msgWhat + "--mMsgHandler----"+mMsgHandler);
				msg.what = msgWhat;
				msg.obj = msgObj;
				mMsgHandler.sendMessage(msg);
		return 0;
	}

	private void SetConf(ZIMEConfig i_ZIMEConfig){
		Log.e(ZIMETAG, "SetConf, ip = " + i_ZIMEConfig.mRecvIP);

				  if(!ZIMEConfig.mIsOnlyAudio)
				  {
					  mZIMEMedia.init(mVJNI);
					  mZIMEMedia.SetConf(i_ZIMEConfig);
					  ZIMEVideoClientJNI.SetActivity(mActivity);
				  }
				  else
				  {
					  mZIMEAudio.SetConf(i_ZIMEConfig);
				  }

		return;
	}

	private void SetVideoConf(ZIMEConfig i_ZIMEConfig){
		Log.e(ZIMETAG, "SetVideoConf, ip = " + i_ZIMEConfig.mRecvIP);
		if(!ZIMEConfig.mIsOnlyAudio)
		{
			mZIMEMedia.SetVideoConf(i_ZIMEConfig);
		}
		return;
	}

	private void SetAudioConf(ZIMEConfig i_ZIMEConfig){
		Log.e(ZIMETAG, "SetAudioConf, ip = " + i_ZIMEConfig.mRecvIP);
		if(!ZIMEConfig.mIsOnlyAudio)
		{
			mZIMEMedia.init(mVJNI);
			mZIMEMedia.SetAudioConf(i_ZIMEConfig);
			ZIMEVideoClientJNI.SetActivity(mActivity);
		}
		return;
	}

	private void Start(){
		Log.e(ZIMETAG, "Start" + "--" + mConf.mRecvIP);

		while(null == mZIMEMedia) {
			Log.d(ZIMETAG,"WAIT......");
		}

		if(mConf.mSaveH26XFile)
		{
			mZIMEMedia.SetVideoModeSet();
		}

		if(!ZIMEConfig.mIsOnlyAudio)
		{
			//mZIMEMedia.init(mVJNI);
			mZIMEMedia.setAudioMan(am);
			mZIMEMedia.Start();
		}
		else
		{
			//mZIMEAudio.init(mAJNI);
			mZIMEAudio.setAudioMan(am);
			mZIMEAudio.Start();
		}

		//handler.postDelayed(runnable, 4000);
		return;
	}

	private void Stop(){
		Log.e(ZIMETAG, "Stop");

		if (mZIMEAudio == null && mZIMEMedia == null) {
			return;
		}
		//handler.removeCallbacks(runnable);
		if(!ZIMEConfig.mIsOnlyAudio)
		{
			mZIMEMedia.Stop();
		}
		else
		{
			mZIMEAudio.Stop();
		}

		Log.d(ZIMETAG, "hangup OK");

		return;
	}

	private void Exit(){
		Log.e(ZIMETAG, "Exit");
		mZIMEMedia.LogExit();
		if (mZIMEAudio == null && mZIMEMedia == null) {
			return;
		}
		if(!ZIMEConfig.mIsOnlyAudio)
		{
			mZIMEMedia.Exit();

			mZIMEMedia = null;

		}
		else
		{
			mZIMEAudio.Exit();
			mZIMEMedia = null;
		}

		return;
	}
	private void StartSend(){
		Log.e(ZIMETAG, "StartSend");

		if(!ZIMEConfig.mIsOnlyAudio)
		{
			mZIMEMedia.StartSend();
		}

		//handler.postDelayed(runnable, 4000);

		return;
	}

	private void StartRecv(){
		Log.e(ZIMETAG, "StartRecv");

		if(mConf.mSaveH26XFile)
		{
			mZIMEMedia.SetVideoModeSet();
		}

		if(!ZIMEConfig.mIsOnlyAudio)
		{
			mZIMEMedia.StartRecv();
		}

		//handler.postDelayed(runnable, 4000);

		return;
	}

	public void setAudioMan(AudioManager amm){
		am = amm;

		return;
	}

	public void StartAByAVClientInterface(){
		Log.e(ZIMETAG, "Start Audio by ClientInterface" + "--" + mConf.mRecvIP);
		if(!ZIMEConfig.mIsOnlyAudio)
		{
			//mZIMEMedia.init(mVJNI);
			mZIMEMedia.setAudioMan(am);
			mZIMEMedia.StartAudio();
		}

		//handler.postDelayed(runnable, 4000);
		return;
	}

	public void toa(){
		mZIMEMedia.ToAudio();
		return;
	}

	public void toAV(){
		mZIMEMedia.toAV();
		return;
	}

	// @Override
	public void run(){
		Looper.prepare();
		Log.d(ZIMETAG, "Thread.currentThread=2"+Thread.currentThread());
		  mMsgHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				Log.e("ZIMETAG",mMsgHandler+"--handle Message---" + msg.what);
				switch (msg.what)
				{
					case ZIMEConfig.SET_PARAM:
					{
						if(null != (ZIMEConfig)msg.obj)
						{
							mConf = (ZIMEConfig) msg.obj;
							SetConf(mConf);
							LogUtil.d("ZIMETAG","--mConf ="+mConf );
						}

						break;
					}
					case ZIMEConfig.SET_VIDEOPARAM:
					{
						if(null != (ZIMEConfig)msg.obj)
						{
							mConf = (ZIMEConfig)msg.obj;
							SetVideoConf(mConf);
							LogUtil.d("ZIMETAG","--mConf ="+mConf );
						}

						break;
					}
					case ZIMEConfig.SET_AUDIOPARAM_BEFORSTART:
					{
						if(null != (ZIMEConfig)msg.obj)
						{
							mConf = (ZIMEConfig)msg.obj;
							SetAudioConf(mConf);
							LogUtil.d("ZIMETAG","-SET_AUDIOPARAM_BEFORSTART-mConf ="+mConf );
						}

						break;
					}
					case ZIMEConfig.START:
						Start();
						LogUtil.d("ZIMETAG","-START ="+ZIMEConfig.START );
						break;
					case ZIMEConfig.STOP:
						Stop();
						LogUtil.d("ZIMETAG","-STOP ="+ZIMEConfig.STOP );
						break;
					case ZIMEConfig.EXIT:
						Exit();
						LogUtil.d("ZIMETAG","-EXIT ="+ZIMEConfig.EXIT );
						break;
					case ZIMEConfig.STARTSEND:
						StartSend();
						LogUtil.d("ZIMETAG","-STARTSEND ="+ZIMEConfig.STARTSEND );
						break;
					case ZIMEConfig.STARTRECV:
						StartRecv();
						LogUtil.d("ZIMETAG","-STARTRECV ="+ZIMEConfig.STARTRECV );
						break;

					case ZIMEConfig.ASTART_BY_AVCLIENTINTERFACE:
						StartAByAVClientInterface();
						LogUtil.d("ZIMETAG","-ASTART_BY_AVCLIENTINTERFACE ="+ZIMEConfig.ASTART_BY_AVCLIENTINTERFACE );
						break;
					case ZIMEConfig.TOA:
						toa();
						LogUtil.d("ZIMETAG","-TOA ="+ZIMEConfig.TOA );
						break;
					case ZIMEConfig.TOAV:
						toAV();
						LogUtil.d("ZIMETAG","-TOAV ="+ZIMEConfig.TOAV );
						break;
					default:
						break;
				}
			}
		};
		Looper.loop();
	}

}
