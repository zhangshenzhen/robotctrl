package zime.ui;

import android.media.AudioManager;
import android.util.Log;

import zime.media.AudioDeviceCallBack;
import zime.media.ZIMEClientJni;
import zime.media.ZIMEClientJni.ZIMECodeInfo;
import zime.media.ZIMEVideoClientJNI.T_ZIMEAudioDownlinkStat;
import zime.media.ZIMEVideoClientJNI.T_ZIMEAudioUplinkStat;

public class ZIMEAudio
{
	protected static final int ZIME_L_DEBUG = 10000;         /**< 调试级别日志 */
protected static final int ZIME_L_INFO  = 20000;         /**< 信息级别日志 */
protected static final int ZIME_L_WARN  = 30000;         /**< 警告级别日志 */
protected static final int ZIME_L_ERROR = 40000;         /**< 错误级别日志 */
protected static final int ZIME_L_FATAL = 50000;

	protected static final float ZIME_MIN_VOL_RATE  = (float)0.3;
	protected static final float ZIME_VOL_DOWN_RATE = (float)0.7;

	private static final String TAG = ZIMEAudio.class.getCanonicalName();
	private int mChannelID = -1;
	private ZIMEClientJni mAClient;
	private AudioDeviceCallBack m_tAudioDevice = null;
	private ZIMEConfig mAConf;

	boolean mbHangup = false;

	private AudioManager am = null;
	private int mLastVol = 0;
	private boolean mbVolDown = false;
	private EchoProcThd mThread = null;

	public ZIMEAudio(ZIMEClientJni i_AClientJNI) {
		Log.e(TAG, "ZIMEAudio --------- ");
		mAClient = i_AClientJNI;
	}

	public void SetConf(ZIMEConfig i_ZIMEConfig){
		Log.e("TAG", "ZIMEAudio SetConf");
		mAConf = i_ZIMEConfig;
	}

	public void Start()
	{
		Log.e("TAG", "ZIMEAudioCall mAudioCodecId" + mAConf.mAudioCodecId);
		Log.e("TAG", "ZIMEAudioCall SendIP " + mAConf.mRecvIP);

		int nRet = ZIMEClientJni.SetLogLevel(ZIME_L_DEBUG);
		nRet = ZIMEClientJni.Init();
		if (nRet != 0) {
			Log.d(TAG, "ZIMEClientJni.Init failed:" + nRet);
		}


		mChannelID = ZIMEClientJni.CreateChannel();
		Log.d(TAG, "EngineClient.CreateChannel = " + mChannelID);

		// 设置免提类型
		nRet = ZIMEClientJni.SetSpeakerMode(false);
		Log.d(TAG, "EngineClient.SetSpeakerMode = " + nRet);
		//ZIMEClientJni.SetAGCStatus(false);

		//  设置相关的端口和地址
		nRet = ZIMEClientJni.SetLocalReceiver(mChannelID, mAConf.mAudioRTPport, -1);
		Log.d(TAG, "EngineClient.SetLocalReceiver = " + nRet);

		nRet = ZIMEClientJni.SetSendDestination(mChannelID, mAConf.mAudioRTPport, mAConf.mRecvIP, -1);
		Log.d(TAG, "EngineClient.SetSendDestination = " + nRet);

		if(ZIMEConfig.mSourceFilter)
		{
			int resultCode = 0;
			resultCode = ZIMEClientJni.SetSourceFilter(mChannelID, mAConf.mAudioRTPport, mAConf.mRecvIP);
			if (resultCode != 0) {
				Log.e(TAG, "ZIME_SetSourceFilter failed!!, ret = " + resultCode);
				return;
			}
		}

		//  设置编解码
		ZIMECodeInfo codeAudioInfo = mAClient.new ZIMECodeInfo(0, 0, 0, 0, 0, "123");
		int resultCode = ZIMEClientJni.GetCodec(mAConf.mAudioCodecId, codeAudioInfo);
		if (resultCode != 0) {
			Log.e(TAG, "ZIME_GetAudioCodec failed!!, ret = " + resultCode);;
			return;
		}

		Log.d(TAG, "EngineClient.SetAudioCodecs = " + codeAudioInfo.aPTName);
		nRet = ZIMEClientJni.SetSendCodec(mChannelID, codeAudioInfo);
		Log.d(TAG, "EngineClient.SetRecCodec = " + nRet);
		nRet = ZIMEClientJni.SetRecPayloadType(mChannelID, codeAudioInfo);
		Log.d(TAG, "EngineClient.SetSendCodec = " + nRet);

		//  设置声卡回调
		nRet = ZIMEClientJni.SetAudioCallBack(m_tAudioDevice);
		Log.d(TAG, "EngineClient.SetAudioCallBack = " + nRet);

		//设置免提类型
		//ZIMEClientJni.SetSpeakerMode(true);

		// 设置NS状态
		nRet = ZIMEClientJni.SetNSStatus(ZIMEConfig.mNS);
		Log.d(TAG, "EngineClient.SetNSStatus = " + nRet);

		//设置AEC状态
		nRet = ZIMEClientJni.SetECStatus(ZIMEConfig.mAEC);
		Log.d(TAG, "EngineClient.SetECStatus= " + ZIMEConfig.mAEC + "nRet=" + nRet);
		nRet = ZIMEClientJni.SetVQEScene(ZIMEConfig.mVQEScene);
		Log.d(TAG, "EngineClient.SetECScene= " + ZIMEConfig.mVQEScene + "nRet=" + nRet);

		//设置带外DTMF PT值
		nRet = ZIMEClientJni.SetSendDTMFPayloadType(mChannelID, (byte) 127);
		Log.d(TAG, "EngineClient.SetSendDTMFPayloadType = " + nRet);
		//设置DTMF发送的反馈
		nRet = ZIMEClientJni.SetDTMFFeedbackStatus(true, true);
		Log.d(TAG, "EngineClient.SetDTMFFeedbackStatus = " + nRet);

		//  启动发送、监听和播放
		nRet = ZIMEClientJni.StartSend(mChannelID);
		Log.d(TAG, "EngineClient.StartSend = " + nRet);
		nRet = ZIMEClientJni.StartListen(mChannelID);
		Log.d(TAG, "EngineClient.Startlisten = " + nRet);
		nRet = ZIMEClientJni.StartPlayout(mChannelID);
		Log.d(TAG, "EngineClient.StartPlayOut = " + nRet);

		// 设置静音
		nRet = ZIMEClientJni.SetInputMute(mChannelID, ZIMEConfig.mMute);
		Log.d(TAG, "EngineClient.SetInputMute = " + nRet);

		// 破音检测
//		    ZIMEClientJni.SetEchoCheck(1);

//			mThread = new EchoProcThd();
//			mThread.start();

		mbHangup = false;

		mAConf.mChannelId = mChannelID;
	}

	public int GetAudioQosStat(T_ZIMEAudioUplinkStat o_tUplinkQosStat, T_ZIMEAudioDownlinkStat o_tDownlinkQosStat){

		return ZIMEClientJni.GetAudioQosStat(mChannelID, o_tUplinkQosStat, o_tDownlinkQosStat);
	}

	public void Stop()
	{
		if(mThread != null && mThread.isAlive()){
			mThread.interrupt();
		}

		int nRet = 0;
		nRet = ZIMEClientJni.StopListen(mChannelID);
		Log.d(TAG, "EngineClient.Stoplisten = " + nRet);
		nRet = ZIMEClientJni.StopPlayout(mChannelID);
		Log.d(TAG, "EngineClient.StopPlayOut = " + nRet);
		nRet = ZIMEClientJni.StopSend(mChannelID);
		Log.d(TAG, "EngineClient.StopSend = " + nRet);
		nRet = ZIMEClientJni.DeleteChannel(mChannelID);
		Log.d(TAG, "EngineClient.DeleteChannel = " + nRet);
		mChannelID = -1;

		mbHangup = true;
	}

	public void Exit()
	{
		//mThread.stop();
		//if(mThread != null && mThread.isAlive()){
		//	mThread.interrupt();
		//	mThread = null;
		//}

		if (!mbHangup)
		{
			// 1. 停止监听、播放和采集
			int nRet = 0;
			nRet = ZIMEClientJni.StopListen(mChannelID);
			Log.d(TAG, "EngineClient.Stoplisten = " + nRet);
			nRet = ZIMEClientJni.StopPlayout(mChannelID);
			Log.d(TAG, "EngineClient.StopPlayOut = " + nRet);
			nRet = ZIMEClientJni.StopSend(mChannelID);
			Log.d(TAG, "EngineClient.StopSend = " + nRet);
			nRet = ZIMEClientJni.DeleteChannel(mChannelID);
			Log.d(TAG, "EngineClient.DeleteChannel = " + nRet);
			mChannelID = -1;
		}

		// 2. 销毁引擎	
		ZIMEClientJni.Terminate();
		ZIMEClientJni.Destroy();
		ZIMEClientJni.Exit();

		mbHangup = true;
	}

	public class EchoProcThd extends Thread {
		public void run() {
			while (true) {
				int s32MaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				int s32CurVol    = am.getStreamVolume(AudioManager.STREAM_MUSIC );
				////Log.e(TAG, "Echo Proc-init:s32CurVol/maxVolume = " + s32CurVol+ "/" + s32MaxVolume);

				boolean bHasEcho = false;
				//ZIMEClientJni.BoolPtr retBool = null;
				float fRate = (float)0;
				if(s32CurVol > (int)(s32MaxVolume * ZIME_MIN_VOL_RATE))
				{
					//sZIMEClientJni.SetEchoCheck(1);
					//bHasEcho = ZIMEClientJni.GetEchoStatus(mChannelId, bHasEcho, fRate);
//	        		   bHasEcho = ZIMEClientJni.GetEchoStatus(mChannelID);
//	        		   if(bHasEcho){
//	        			   fRate= (float)0.3;
//	        		   }

					////Log.e(TAG, "Echo Proc-check:bHasEcho = " + bHasEcho + " ,fRate= " + fRate);
					if(bHasEcho && !mbVolDown)
					{
						mLastVol = s32CurVol;

						int s32NewVol = (int)(s32CurVol * (1 - fRate) + 0.5);
						am.setStreamVolume(AudioManager.STREAM_MUSIC, s32NewVol, 0);
						////Log.e(TAG, "Echo Proc-down:s32NewVol = " + s32NewVol);

//	        			   ZIMEClientJni.SetEchoCheck(0);
						mbVolDown = true;
					}

					s32CurVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
					if(mLastVol > s32CurVol)
					{
						////Log.e(TAG, "Echo Proc-set check:mLastVol = " + mLastVol + ";s32CurVol="+s32CurVol);
//	        			   ZIMEClientJni.SetEchoCheck(1);
						mbVolDown = false;
					}
				}

				//mLastVol = s32CurVol;
				try
				{
					Thread.sleep(15);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void setAudioMan(AudioManager amm){
		am = amm;

		return;
	}
}

