package zime.ui;
import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;

import zime.media.ZIMEVideoClientJNI;
import zime.media.ZIMEVideoClientJNI.StrPtr;
import zime.media.ZIMEVideoClientJNI.T_ZIMEAudioDownlinkStat;
import zime.media.ZIMEVideoClientJNI.T_ZIMEAudioUplinkStat;
import zime.media.ZIMEVideoClientJNI.T_ZIMEVideoDownlinkStat;
import zime.media.ZIMEVideoClientJNI.T_ZIMEVideoSwitchLevel;
import zime.media.ZIMEVideoClientJNI.T_ZIMEVideoUplinkStat;
import zime.media.ZIMEVideoClientJNI.ZIMEAudioCodeInfo;
import zime.media.ZIMEVideoClientJNI.ZIMEVideoCodeInfo;

@SuppressLint("SdCardPath")
public class ZIMEMedia {
	protected static final int ZIME_L_DEBUG = 10000;         /**< 调试级别日志 */
	protected static final int ZIME_L_INFO  = 20000;         /**< 信息级别日志 */
	protected static final int ZIME_L_WARN  = 30000;         /**< 警告级别日志 */
	protected static final int ZIME_L_ERROR = 40000;         /**< 错误级别日志 */
	protected static final int ZIME_L_FATAL = 50000;

	private static final String ZIMETAG = ZIMEMedia.class.getCanonicalName();

	protected static final float ZIME_MIN_VOL_RATE  = (float)0.3;
	protected static final float ZIME_VOL_DOWN_RATE = (float)0.7;

	private ZIMEVideoClientJNI mVideoClient;
	private ZIMEAudioCodeInfo m_AudioCodecInfo;
	private ZIMEAudioCodeInfo m_DTMFCodecInfo;
	private ZIMEVideoCodeInfo m_VideoCodecInfo;
	private StrPtr mPeerAddr;
	private int mChannelId = -1;
	boolean mbHangup = false;

	private AudioManager am = null;
	private int mLastVol = 0;
	private boolean mbVolDown = false;
	private EchoProcThd mThread = null;
	public  ZIMEConfig mZIMEConfig  = null;

	private boolean mbStartSend = false;
	private boolean mbStartRecv = false;


	public ZIMEMedia(ZIMEVideoClientJNI i_VideoClientJNI) {

		Log.e(ZIMETAG, "ZIMEMedia --------- ");
		mVideoClient = i_VideoClientJNI;

		m_AudioCodecInfo = mVideoClient.new ZIMEAudioCodeInfo(8, 8000, 160, 1, 64000, "PCMA", 0, 0);
		m_DTMFCodecInfo  = mVideoClient.new ZIMEAudioCodeInfo(127, 8000, 80, 1, 8000, "telephone-event", 0, 0);
		m_VideoCodecInfo = mVideoClient.new ZIMEVideoCodeInfo(114, 90000, 90000/10, 1, 300000, -1, "H264", 640, 480, 10, 2, 0);
		mPeerAddr = mVideoClient.new StrPtr();
	}

	public void init(ZIMEVideoClientJNI i_VideoClientJNI) {

		Log.e(ZIMETAG, "ZIMEMedia --------- ");
		mVideoClient = i_VideoClientJNI;

		m_AudioCodecInfo = mVideoClient.new ZIMEAudioCodeInfo(8, 8000, 160, 1, 64000, "PCMA", 0, 0);
		m_DTMFCodecInfo  = mVideoClient.new ZIMEAudioCodeInfo(127, 8000, 80, 1, 8000, "telephone-event", 0, 0);
		m_VideoCodecInfo = mVideoClient.new ZIMEVideoCodeInfo(114, 90000, 90000/10, 1, 300000, -1, "H264", 640, 480, 10, 2, 0);
		mPeerAddr = mVideoClient.new StrPtr();

		int resultCode = ZIMEVideoClientJNI.Init();
		if(0 != resultCode){
			Log.e(ZIMETAG, "ZIMEVideoClient Init failed!!, resultCode = " + resultCode);
			return;
		}
	}


	private void SetOtherParamExceptTransportParam(boolean i_bIsSupportAudio, boolean i_bIsSupportVideo)
	{
		int resultCode = 0;
		ZIMEAudioCodeInfo AudioCodecInfo = null;
		ZIMEVideoCodeInfo VideoCodecInfo = m_VideoCodecInfo;

		ZIMEVideoClientJNI.GetVideoCodec(0, m_VideoCodecInfo);

		if (i_bIsSupportVideo)
		{

			ZIMEVideoClientJNI.SetFECStatus(mChannelId, ZIMEConfig.mFEC);
			ZIMEVideoClientJNI.SetNACKStatus(mChannelId, ZIMEConfig.mNACK);
			resultCode = ZIMEVideoClientJNI.SetVendorType(mChannelId, ZIMEConfig.mVendorType);
			if(0 != resultCode){
				Log.e(ZIMETAG, "ZIMEVideoClient SetCodecType failed!!, mChannelId = " + mChannelId);
				return;
			}

			if(true == ZIMEConfig.mIsH265)
			{
				m_VideoCodecInfo.aPTName = "H265";
				m_VideoCodecInfo.s32PT   = 116;
			}
			else
			{
				m_VideoCodecInfo.aPTName = "H264";
				m_VideoCodecInfo.s32PT   = 114;
			}

			Log.e(ZIMETAG, "codec: " + m_VideoCodecInfo.aPTName + m_AudioCodecInfo.aPTName);
			m_VideoCodecInfo.s32Height = mZIMEConfig.mHeight;
			m_VideoCodecInfo.s32Width  = mZIMEConfig.mWidth;
			m_VideoCodecInfo.s32InitBitRate = mZIMEConfig.mInitBitRate;
			m_VideoCodecInfo.s32BitRate = mZIMEConfig.mBitrate;
			m_VideoCodecInfo.u32FrameRate = mZIMEConfig.mFrameRate;
			m_VideoCodecInfo.s32MaxBitRate = mZIMEConfig.mMaxBitRate;
			m_VideoCodecInfo.s32PacketSize = 90000 / mZIMEConfig.mFrameRate;
			m_VideoCodecInfo.eSceneType = ZIMEConfig.enumZIME_MOVE_SCENE;
			m_VideoCodecInfo.s32VBREnable = ZIMEConfig.mVBR? 1: 0;
			VideoCodecInfo = m_VideoCodecInfo;
			Log.i(ZIMETAG, "sleceted size------width:" + mZIMEConfig.mWidth + ",height:" + mZIMEConfig.mHeight);
			ZIMEVideoClientJNI.SetVideoDevices(ZIMEConfig.mCameraId);
			if(ZIMEConfig.mSourceFilter) {
				ZIMEVideoClientJNI.SetVideoDevCapSize(mZIMEConfig.mHeight, mZIMEConfig.mWidth);
			}
			else {
				ZIMEVideoClientJNI.SetVideoDevCapSize(mZIMEConfig.mWidth, mZIMEConfig.mHeight);
			}

			resultCode = ZIMEVideoClientJNI.SetVideoDisplayWnd((SurfaceHolder) mZIMEConfig.mLocalSurfaceHolder,
					(GLSurfaceView) mZIMEConfig.mRemoteGLSurface);
			if(resultCode != 0){
				Log.e(ZIMETAG, "ZIMEVideoClient SetVideoDisplayWnd failed!!, ret = " + resultCode);
				return;
			}

			//设置视频编解码回调
			if(ZIMEConfig.mUsingEncodeCAllback)
			{
				resultCode = ZIMEVideoClientJNI.SetVideoEncodeFun(mChannelId);
				if(resultCode != 0){
					Log.e(ZIMETAG, "ZIMEVideoClient SetVideoEncodeFun failed!!, ret = " + resultCode);
					return;
				}


			}

			if(ZIMEConfig.mCodecType == ZIMEConfig.enumZIME_AMLOGICHARDWEAR ||
					ZIMEConfig.mCodecType == ZIMEConfig.enumZIME_MediaCodec )
			{
				resultCode = ZIMEVideoClientJNI.SetVideoDecodeFun(mChannelId);
				if(resultCode != 0){
					Log.e(ZIMETAG, "ZIMEVideoClient SetVideoDecodeFun failed!!, ret = " + resultCode);
					return;
				}
			}

			T_ZIMEVideoSwitchLevel[] levels = mZIMEConfig.GetAllResOfQualitySet();
			resultCode = ZIMEVideoClientJNI.SetVideoQualityLevelSet(mChannelId, levels.length, levels);
			if(resultCode != 0){
				Log.e(ZIMETAG, "SetVideoQualityLevelSet failed!, ret = " + resultCode);
				return;
			}
		}

		if (i_bIsSupportAudio)
		{
			resultCode = ZIMEVideoClientJNI.GetAudioCodec(mZIMEConfig.mAudioCodecId, m_AudioCodecInfo);
			if (resultCode != 0) {
				Log.e(ZIMETAG, "ZIME_GetAudioCodec failed!!, ret = " + resultCode);
				return;
			}
			resultCode = ZIMEVideoClientJNI.SetSampleRate(m_AudioCodecInfo.s32SampleRate / 1000);
			if (resultCode != 0) {
				Log.e(ZIMETAG, "SetSampleRate failed!!, ret = " + resultCode);
				return;
			}

			AudioCodecInfo = m_AudioCodecInfo;

			ZIMEVideoClientJNI.SetSpeakerMode(false);
			// 设置NS状态
			resultCode = ZIMEVideoClientJNI.SetNSStatus(ZIMEConfig.mNS);
			if (resultCode != 0) {
				Log.e(ZIMETAG, "ZIMEVideoClient SetNSStatus failed!!, ret = " + resultCode);
				//return;
			}

			// 设置AEC状态
			resultCode = ZIMEVideoClientJNI.SetECStatus(ZIMEConfig.mAEC);
			if (resultCode != 0) {
				Log.e(ZIMETAG, "ZIMEVideoClient SetECStatus failed!!, ret = " + resultCode);
				//return;
			}

			resultCode = ZIMEVideoClientJNI.SetVQEScene(ZIMEConfig.mVQEScene);
			Log.e(ZIMETAG, "ZIMEVideoClient SetVQEScene[" + ZIMEConfig.mVQEScene + "] !!, ret = " + resultCode);
			if(resultCode != 0){
				Log.e(ZIMETAG, "ZIMEVideoClient SetVQEScene failed!!, ret = " + resultCode);
				return;
			}

		}

		//VideoCodecInfo.s32PT = 115;
		//AudioCodecInfo.s32PT = 97;
		resultCode = ZIMEVideoClientJNI.SetSendCodec(mChannelId, AudioCodecInfo, VideoCodecInfo);
		if(resultCode != 0){
			Log.e(ZIMETAG, "ZIMEVideoClient SetSendCodec failed!!, ret = " + resultCode);
			return;
		}

		//VideoCodecInfo.s32PT = 114;
		//AudioCodecInfo.s32PT = 125;
		resultCode = ZIMEVideoClientJNI.SetRecPayloadType(mChannelId, AudioCodecInfo, VideoCodecInfo);
		if(resultCode != 0){
			Log.e(ZIMETAG, "ZIMEVideoClient SetRecPayloadType failed!!, ret = " + resultCode);
			return;
		}


		//设置DTMF
		Log.i(ZIMETAG, "SetInbandDTMF");


		int s32SendDTMFPT = 127;
		resultCode = ZIMEVideoClientJNI.SetSendDTMFPayloadType(mChannelId, (byte) s32SendDTMFPT);
		if (resultCode != 0) {
			Log.i(ZIMETAG, "SetSendDTMFPayloadType failed return = %d " + resultCode);
		}

		Log.i(ZIMETAG, "SetInbandDTMF");
		resultCode = ZIMEVideoClientJNI.SetDTMFFeedbackStatus(true, true);
		if (resultCode != 0) {
			Log.i(ZIMETAG, "SetDTMFFeedbackStatus failed return = %d " + resultCode);
		}


		AudioCodecInfo = m_DTMFCodecInfo;
		AudioCodecInfo.aPTName = "telephone-event";
		AudioCodecInfo.s32PT   = 127;
		resultCode = ZIMEVideoClientJNI.SetRecPayloadType(mChannelId, AudioCodecInfo, VideoCodecInfo);
		if(resultCode != 0){
			Log.e(ZIMETAG, "ZIMEVideoClient DTMF SetRecPayloadType failed!!, ret = " + resultCode);
			return;
		}



		// 设置网络质量回调
		Log.e(ZIMETAG, "before SetNetworkQualityNotify!!");
		ZIMEVideoClientJNI.SetNetworkQualityNotify(mChannelId);
		Log.e(ZIMETAG, "after SetNetworkQualityNotify!!");


	}

	private void SetTransportParam(boolean i_bIsSupportAudio, boolean i_bIsSupportVideo)
	{
		mPeerAddr.retuStr = mZIMEConfig.mRecvIP;
		int resultCode = 0;
		int audioRTPPort = -1;
		int audioRTCPPort = -1;
		int videoRTPPort = -1;
		int VideoRTCPPort = -1;

		if (i_bIsSupportAudio)
		{
			audioRTPPort = mZIMEConfig.mAudioRTPport;
			audioRTCPPort = mZIMEConfig.mAudioRTCPport;
		}
		if (i_bIsSupportVideo) {
			videoRTPPort = mZIMEConfig.mVideoRTPport;
			VideoRTCPPort = mZIMEConfig.mVideoRTCPport;
		}

		if (ZIMEConfig.mIsExTrans) {

			if (!mbStartSend) {
				ZIMEVideoClientJNI.SetExternalTransport(mChannelId, audioRTPPort,
						mPeerAddr, audioRTPPort, i_bIsSupportVideo);
			}
		}
		else
		{
			resultCode = ZIMEVideoClientJNI.SetLocalReceiver(mChannelId, audioRTPPort,
					audioRTCPPort, videoRTPPort, VideoRTCPPort, null);
			if (resultCode != 0) {
				Log.e(ZIMETAG, "ZIME_SetLocalReceiver failed!!, ret = " + resultCode);
				return;
			}
			resultCode = ZIMEVideoClientJNI.SetSendDestination(mChannelId, audioRTPPort,
					audioRTCPPort, videoRTPPort, VideoRTCPPort, mPeerAddr);
			if (resultCode != 0) {
				Log.e(ZIMETAG, "ZIME_SetSendDestination failed!!, ret = " + resultCode);
				return;
			}
		}

		//ip和端口过滤
		if(ZIMEConfig.mSourceFilter)
		{
			resultCode = ZIMEVideoClientJNI.SetSourceFilter(mChannelId, audioRTCPPort, videoRTPPort, mPeerAddr);
			if (resultCode != 0) {
				Log.e(ZIMETAG, "ZIME_SetSourceFilter failed!!, ret = " + resultCode);
				return;
			}
		}

	}

	public void SetConf(ZIMEConfig i_ZIMEConfig){
		Log.i(ZIMETAG, "AV SetConf");
		// 创建通道
		mChannelId = ZIMEVideoClientJNI.CreateChannel(ZIMEConfig.mUsingEncodeCAllback, ZIMEConfig.mCodecType);
		if(-1 == mChannelId){
			Log.e(ZIMETAG, "ZIMEVideoClient CreateChannel failed!!, mChannelId = " + mChannelId);
			return;
		}

		i_ZIMEConfig.mChannelId = mChannelId;
		mZIMEConfig = i_ZIMEConfig;

		SetTransportParam(true, true);
		SetOtherParamExceptTransportParam(true, true);

		if(!mbStartSend && !mbStartRecv)
		{
			ZIMEVideoClientJNI.SetCallBack();
		}

		i_ZIMEConfig.WriteConfigToSDFile();
		return;
	}

	public void SetVideoModeSet(){
		Log.i(ZIMETAG, "SetVideoModeSet");
		ZIMEVideoClientJNI.SetVideoModeSet();
	}

	public void SetVideoConf(ZIMEConfig i_ZIMEConfig){
		Log.i(ZIMETAG, "SetVideoConf");

		mZIMEConfig = i_ZIMEConfig;
		SetTransportParam(false, true);
		SetOtherParamExceptTransportParam(false, true);
		ZIMEVideoClientJNI.SetVideoCallBack();
		return;
	}

	public void SetAudioConf(ZIMEConfig i_ZIMEConfig){
		Log.i(ZIMETAG, "SetAudioConf");

		// 创建通道
		mChannelId = ZIMEVideoClientJNI.CreateChannel(ZIMEConfig.mUsingEncodeCAllback, ZIMEConfig.mCodecType);
		if(-1 == mChannelId){
			Log.e(ZIMETAG, "ZIMEVideoClient CreateChannel failed!!, mChannelId = " + mChannelId);
			return;
		}
		i_ZIMEConfig.mChannelId = mChannelId;
		mZIMEConfig = i_ZIMEConfig;

		Log.e(ZIMETAG, "i_ZIMEConfig.mVideoCodecLevel_qvga = " + i_ZIMEConfig.mVideoCodecLevel_qvga);
		//校验档位，当某档位或多个档位设置为空时也可以设置成功
		int destpos = 0;
		int nNum    = 0;
		T_ZIMEVideoSwitchLevel[] levels = new T_ZIMEVideoSwitchLevel[25];
		for(int i = 0; i < 25; i++)
			SetTransportParam(true, false);
		SetOtherParamExceptTransportParam(true, false);
		if(!mbStartSend && !mbStartRecv)
		{
			ZIMEVideoClientJNI.SetCallBack();
		}

		i_ZIMEConfig.WriteConfigToSDFile();

		return;
	}

	public void Start(){
		Log.i(ZIMETAG, "AV Start");

		//设置编码器
		int resultCode = ZIMEVideoClientJNI.SetVideoEncoderAbility(mChannelId, mZIMEConfig.mNaluNum, ZIMEConfig.mEncodeProfile);
		if (resultCode != 0 && !ZIMEConfig.mUsingEncodeCAllback) {
			Log.e(ZIMETAG, "ZIMEVideoClient SetVideoEncoderAbility failed!!, ret = " + resultCode);
			return;
		}

		ZIMEVideoClientJNI.SetSpeakerMode(false);
		StartSend();
		StartRecv();
		mbHangup = false;
		return;
	}

	public void StartAudio(){

		Log.i(ZIMETAG, "Audio Start");
		int resultCode = 0;
		ZIMEVideoClientJNI.StartAudio(mChannelId);

		// 设置静音
		resultCode = ZIMEVideoClientJNI.SetInputMute(mChannelId, ZIMEConfig.mMute);
		if (resultCode != 0) {
			Log.e(ZIMETAG, "ZIMEVideoClient SetInputMute failed!!, ret = " + resultCode);
		}

		if(ZIMEConfig.mIsExTrans)
		{
			ZIMEVideoClientJNI.StartRTPExternalTransport(true, false);
			ZIMEVideoClientJNI.StartRTCPExternalTransport(true, false);
		}

	}

	public void ToAudio(){
		Log.i(ZIMETAG, "To Audio");
		ZIMEVideoClientJNI.ToAudio(mChannelId);


	}

	public void toAV(){
		Log.i(ZIMETAG, "To AV");

		//设置编码器
		int resultCode = ZIMEVideoClientJNI.SetVideoEncoderAbility(mChannelId, mZIMEConfig.mNaluNum, ZIMEConfig.mEncodeProfile);
		if (resultCode != 0 && !ZIMEConfig.mUsingEncodeCAllback) {
			Log.e(ZIMETAG, "ZIMEVideoClient SetVideoEncoderAbility failed!!, ret = " + resultCode);
			return;
		}

		//设置同步
		resultCode = ZIMEVideoClientJNI.SetSynchronization(mChannelId, ZIMEConfig.mSync);
		if(resultCode != 0)
		{
			Log.e(ZIMETAG, "ZIMEVideoClient SetSynchronization failed!!, ret = " + resultCode);
		}

		ZIMEVideoClientJNI.ToAudioAndVideo(mChannelId, mZIMEConfig.mVideoRTPport, mZIMEConfig.mVideoRTPport, mPeerAddr);
		ConfigParamAfterSend();

	}

	public int GetAudioQosStat(T_ZIMEAudioUplinkStat o_tUplinkQosStat, T_ZIMEAudioDownlinkStat o_tDownlinkQosStat){

		return ZIMEVideoClientJNI.GetAudioQosStat(mChannelId, o_tUplinkQosStat, o_tDownlinkQosStat);
	}

	public int GetVideoQosStat(T_ZIMEVideoUplinkStat o_tUplinkQosStat, T_ZIMEVideoDownlinkStat o_tDownlinkQosStat){
		return ZIMEVideoClientJNI.GetVideoQosStat(mChannelId, o_tUplinkQosStat, o_tDownlinkQosStat);
	}

	public void Stop(){
		Log.e(ZIMETAG, "Stop");

		//mThread.stop();
		//if(mThread != null && mThread.isAlive()){
		//mThread.interrupt();
		//mThread = null;
		//}

		int resultCode = -1;
		if(ZIMEConfig.mIsExTrans)
		{
			ZIMEVideoClientJNI.StopExternalTransportRecv();
		}
		resultCode = ZIMEVideoClientJNI.StopPlayout(mChannelId);
		if (resultCode != 0) {
			Log.e(ZIMETAG, "ZIME_StopPlayout failed!!");
			//return;
		}

		resultCode = ZIMEVideoClientJNI.StopSend(mChannelId);
		if (resultCode != 0) {
			Log.e(ZIMETAG, "ZIME_StopSend failed!!");
			//return;
		}

		resultCode = ZIMEVideoClientJNI.StopListen(mChannelId);
		if (resultCode != 0) {
			Log.e(ZIMETAG, "ZIME_StopListen failed!!");
			//return;
		}



		resultCode = ZIMEVideoClientJNI.DeleteChannel(mChannelId);
		if (resultCode != 0) {
			Log.e(ZIMETAG, "ZIME_DeleteChannel failed!!");
			//return;
		}

		if(ZIMEConfig.mIsExTrans)
		{
			ZIMEVideoClientJNI.StopExternalTransportSend();
		}

		m_AudioCodecInfo = null;
		m_VideoCodecInfo = null;
		mPeerAddr = null;

		mChannelId = -1;
		mbHangup = true;

		mbStartSend = false;
		mbStartRecv = false;

		Log.d(ZIMETAG, "hangup OK");

		return;
	}

	public void Exit(){
		Log.e(ZIMETAG, "Exit");
		if (!mbHangup)
		{
			Stop();
		}

		ZIMEVideoClientJNI.Terminate();
		ZIMEVideoClientJNI.Destroy();
		ZIMEVideoClientJNI.Exit();
		mbHangup = true;

		return;
	}

	public void StartSend(){
		Log.e(ZIMETAG, "StartSend");
		int resultCode = 0;

		//设置编码器
		resultCode = ZIMEVideoClientJNI.SetVideoEncoderAbility(mChannelId, mZIMEConfig.mNaluNum, ZIMEConfig.mEncodeProfile);
		if (resultCode != 0 && !ZIMEConfig.mUsingEncodeCAllback) {
			Log.e(ZIMETAG, "ZIMEVideoClient SetVideoEncoderAbility failed!!, ret = " + resultCode);
			return;
		}

		//ZIMEVideoClientJNI.SetAGCStatus(true, enumZIME_AGC_SOFTWARE);

		// 启动发送
		resultCode = ZIMEVideoClientJNI.StartSend(mChannelId);
		if (resultCode != 0) {
			Log.e(ZIMETAG, "ZIMEVideoClient StartSend failed!!, ret = " + resultCode);
			return;
		}

		ConfigParamAfterSend();

		mbStartSend = true;

		if(!mbStartRecv)
		{
			if(ZIMEConfig.mIsExTrans)
			{
				ZIMEVideoClientJNI.StartRTCPExternalTransport(true, true);
			}
		}

		return;
	}


	public void ConfigParamAfterSend()
	{
		Log.e(ZIMETAG, "ConfigParamAfterSend");
		int resultCode = 0;

		//pause
		if(ZIMEConfig.mPauseVideo)
		{
			ZIMEVideoClientJNI.StartCapFileAsCamera(mChannelId, ZIMEConfig.mPicPath);
		}
		else
		{
			ZIMEVideoClientJNI.StopCapFileAsCamera(mChannelId);
		}

		//设置I帧间隔
		if(mZIMEConfig.ms32IframeInterval >= 1000)
		{
			resultCode = ZIMEVideoClientJNI.SetIframeInterval(mChannelId, mZIMEConfig.ms32IframeInterval);
			Log.e(ZIMETAG, "ZIMEVideoClient SetIframeInterval[" + mZIMEConfig.ms32IframeInterval + "] !!, ret = " + resultCode);

			if(resultCode != 0){
				Log.e(ZIMETAG, "ZIMEVideoClient SetIframeInterval failed!!, ret = " + resultCode);
				return;
			}
		}

		// 设置静音
		resultCode = ZIMEVideoClientJNI.SetInputMute(mChannelId, ZIMEConfig.mMute);
		if (resultCode != 0) {
			Log.e(ZIMETAG, "ZIMEVideoClient SetInputMute failed!!, ret = " + resultCode);
		}


	}


	public void StartRecv(){
		Log.e(ZIMETAG, "StartRecv");
		int resultCode = 0;

		// 启动监听和播放
		resultCode = ZIMEVideoClientJNI.StartListen(mChannelId);
		if (resultCode != 0) {
			Log.e(ZIMETAG, "ZIMEVideoClient StartListen failed!!, ret = " + resultCode);
			return;
		}

		//设置同步
		resultCode = ZIMEVideoClientJNI.SetSynchronization(mChannelId, ZIMEConfig.mSync);
		if(resultCode != 0)
		{
			Log.e(ZIMETAG, "ZIMEVideoClient SetSynchronization failed!!, ret = " + resultCode);
		}

		resultCode = ZIMEVideoClientJNI.StartPlayout(mChannelId);
		if (resultCode != 0) {
			Log.e(ZIMETAG, "ZIMEVideoClient StartPlayout failed!!, ret = " + resultCode);
			return;
		}

		mbStartRecv = true;

		if(!mbStartSend)
		{
			if(ZIMEConfig.mIsExTrans)
			{
				ZIMEVideoClientJNI.StartRTCPExternalTransport(true, true);
			}
		}

		if(ZIMEConfig.mIsExTrans)
		{
			ZIMEVideoClientJNI.StartRTPExternalTransport(true, true);
		}

		return;
	}

	public void SetLogCallBack(){
		if(ZIMEConfig.mIsLogCallBack)
		{
			ZIMEVideoClientJNI.SetLogCallBack();
		}
	}


	public void LogExit(){
		if(ZIMEConfig.mIsLogCallBack)
		{
			ZIMEVideoClientJNI.LogExit();
		}
	}

	public class EchoProcThd extends Thread {
		public void run() {
			while (true) {
				int s32MaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				int s32CurVol    = am.getStreamVolume(AudioManager.STREAM_MUSIC );
				////Log.e(ZIMETAG, "Echo Proc1-init:s32CurVol/maxVolume = " + s32CurVol+ "/" + s32MaxVolume);

				boolean bHasEcho = false;
				//ZIMEVideoClientJNI.BoolPtr retBool = null;
				float fRate = (float)0;
				if(s32CurVol > (int)(s32MaxVolume * ZIME_MIN_VOL_RATE))
				{
					//sZIMEVideoClientJNI.SetEchoCheck(1);
					//bHasEcho = ZIMEVideoClientJNI.GetEchoStatus(mChannelId, bHasEcho, fRate);
					//bHasEcho = ZIMEVideoClientJNI.GetEchoStatus(mChannelId);
//        		   if(bHasEcho){
//        			   fRate= (float)0.3;
//        		   }

					////Log.e(ZIMETAG, "Echo Proc1-check:bHasEcho = " + bHasEcho + " ,fRate= " + fRate);
					if(bHasEcho && !mbVolDown)
					{
						mLastVol = s32CurVol;

						int s32NewVol = (int)(s32CurVol * (1 - fRate) + 0.5);
						am.setStreamVolume(AudioManager.STREAM_MUSIC, s32NewVol, 0);
						////Log.e(ZIMETAG, "Echo Proc1-down:s32NewVol = " + s32NewVol);

//        			   ZIMEVideoClientJNI.SetEchoCheck(0);
						mbVolDown = true;
					}

					s32CurVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
					if(mLastVol > s32CurVol)
					{
						////Log.e(ZIMETAG, "Echo Proc1-set check:mLastVol = " + mLastVol + ";s32CurVol="+s32CurVol);
//        			   ZIMEVideoClientJNI.SetEchoCheck(1);
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
