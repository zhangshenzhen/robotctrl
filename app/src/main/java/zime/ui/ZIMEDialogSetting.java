package zime.ui;

import android.app.Dialog;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.brick.robotctrl.R;

import java.util.ArrayList;
import java.util.List;

import zime.media.ZIMEClientJni;
import zime.media.ZIMEVideoClientJNI;
import zime.media.ZIMEVideoClientJNI.StrPtr;
import zime.media.ZIMEVideoClientJNI.T_ZIMEVideoSwitchLevel;

public class ZIMEDialogSetting extends Dialog {

	// 引擎
	private final static String TAG = "ZIME.ui";
	private static ZIMEClientJni mAClient  = null;
	private static ZIMEVideoClientJNI mAVClient = null;
	private static ZIMEConfig mConfig;
	//private final static String QVGAStr = "";
	private final static String QVGAStr = "qvga:10/150-15/200-15/280";
	private final static String VGAStr = "vga:15/350-15/500-20/600";
	private final static String _720pStr = "720p:10/900-15/1200-20/1500";

	// 通道
	private static int mChannelId = -1;

	// 控件
	private static EditText mRevTxtIP = null;
	private static EditText mEditNalNum = null;
	private static EditText mTxtMaxBitRate = null;

	private static EditText mEditVideoCodecLevel_qvga = null;
	private static EditText mEditVideoCodecLevel_vga = null;
	private static EditText mEditVideoCodecLevel_720p = null;

	private static Spinner mSpinnerAudioCodec = null;
	private static Spinner mSpinnerVideoSizeIdx  = null;
	private static Spinner mSpinnerVideoCodec = null;
	private static Spinner mSpinnerSetLogLevel = null;

	private static CheckBox mCheckboxAECTV = null;
	private static CheckBox mCheckboxFEC = null;
	private static CheckBox mCheckboxNACK = null;
	private static CheckBox mCheckboxAudio = null;
	private static CheckBox mCheckboxDisplay = null;
	private static CheckBox mCheckboxInbandDTMF = null;
	private static CheckBox mCheckboxMute = null;
	private static CheckBox mCheckboxVBR = null;
	private static CheckBox mCheckBoxCamera = null;
	private static CheckBox mCheckBoxNS = null;
	private static CheckBox mCheckBoxSourceFilter = null;
	private static CheckBox mCheckBoxSync = null;
	private static CheckBox mCheckboxEncodeProfile = null;
	private static CheckBox mCheckBoxConstantBps = null;
	private static CheckBox mCheckBoxCallSelf = null;
	private static CheckBox mCheckBoxExTrans = null;
	private static CheckBox mCheckBoxQVGA = null;
	private static CheckBox mCheckBoxVGA = null;
	private static CheckBox mCheckBox720p = null;
	private static CheckBox mCheckBoxSpeaker = null;
	private static CheckBox mCheckBoxLogCallBack = null;
	private static CheckBox mCheckBoxSaveH26X = null;
	private static Button mButtonOK = null;
	private static Button mButtonUpdateLevel = null;


	private static String[] mVideDevCapSizeStrArray;
	private static int [] mVideoResWidthArr;
	private static int [] mVideoResHeightArr;
	private static Size[] mVideoDevCapSize = null;
	private static boolean FirstLoadPreviewSize = false;
	private static boolean mStarted = false;
	private static boolean mStopped = true;
	private static AudioManager mAudioManager = null;

	public static View.OnClickListener mClickListenerUpdateLevel = null;
	public static View.OnClickListener mClickListenerOK = null;
	public static TextWatcher mTextWatcherQVGA = null;
	public static TextWatcher mTextWatcherVGA = null;
	public static TextWatcher mTextWatcher720p = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerFEC = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerNACK = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerAECTV = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerDisplay = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerInbandDTMF = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerAudio = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerMute = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerVBR = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerCamera = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerNS = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerSourceFilter = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerConstantBps = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerCallSelf = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerExTrans = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerLogCallBack = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerSync = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerSaveH26X = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerQVGA = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerVGA = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListener720p = null;
	public static OnItemSelectedListener mItemSelectedListenerCodec = null;
	public static OnItemSelectedListener mItemSelectedListenerLevel = null;
	public static OnItemSelectedListener mItemSelectedListenerSetLogLevel = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerEncodeProfile = null;
	public static CompoundButton.OnCheckedChangeListener mCheckedChangeListenerSpeaker = null;

	static List<String> mVideoQualityLevellist;

	public static Handler mHandler;
	private StrPtr mPeerIP;

	public ZIMEDialogSetting(Context context, int theme){
		super(context, theme);
		init();
	}

	//更新档位
	void updateVideoCodecLevel(){
		List<String> li = ChangeLevelTolist();
		mVideoQualityLevellist.clear();
		mVideoQualityLevellist.addAll(li);

		ArrayAdapter<String> apt = (ArrayAdapter<String>) mSpinnerVideoSizeIdx.getAdapter();
		apt.notifyDataSetChanged();
	};

	private void init()
	{
		//mDialog = this;
		mClickListenerOK = new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mConfig.mRecvIP = mRevTxtIP.getText().toString();

				mConfig.mMaxBitRate = 1000 * Integer.valueOf(mTxtMaxBitRate.getText().toString()).intValue();
				mConfig.mNaluNum = Integer.valueOf(mEditNalNum.getText().toString()).intValue();


				mConfig.mBitrate = -1;
				if(mConfig.mBitrate != -1)
				{
					mConfig.mBitrate *= 1000;
				}

				int idx = (int)mSpinnerAudioCodec.getSelectedItemId();
				if(3 == idx)
				{
					idx = 4;
				}
				else if(4 == idx)
				{
					idx = 14;
				}
				mConfig.mAudioCodecId = idx;

				Log.e("", "Init Level: w = " + mConfig.mWidth + ", fps = " + mConfig.mFrameRate + ", initbps = " + mConfig.mInitBitRate);

				dismiss();
			}
		};



		//点击刷新按钮
		mClickListenerUpdateLevel = new  View.OnClickListener(){

			@Override
			public void onClick(View v) {
				updateVideoCodecLevel();
			}};

		// FEC
		mCheckedChangeListenerFEC = new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked)
			{
				ZIMEConfig.mFEC = isChecked;
				mChannelId = mConfig.mChannelId;
				Log.i(TAG, "-------------SetFECStatus------channel id = " + mChannelId);

				if(ZIMEConfig.mFEC)
				{
					mCheckboxNACK.setEnabled(false);
				}
				else
				{
					mCheckboxNACK.setEnabled(true);
				}

				if(!(mStarted && !mStopped))
				{
					return;
				}

				if (null != mAVClient && -1 != mChannelId)
				{
					ZIMEVideoClientJNI.SetNACKStatus(mChannelId, false);
					ZIMEVideoClientJNI.SetFECStatus(mChannelId, ZIMEConfig.mFEC);
				}
			}

		};

		mCheckedChangeListenerNACK = new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked)
			{
				ZIMEConfig.mNACK = isChecked;
				mChannelId = mConfig.mChannelId;
				Log.i(TAG, "-------------SetNACKStatus------channel id = " + mChannelId);

				if(ZIMEConfig.mNACK)
				{
					mCheckboxFEC.setEnabled(false);
				}
				else
				{
					mCheckboxFEC.setEnabled(true);
				}

				if(!(mStarted && !mStopped))
				{
					return;
				}

				if (null != mAVClient && -1 != mChannelId)
				{
					ZIMEVideoClientJNI.SetFECStatus(mChannelId, false);
					ZIMEVideoClientJNI.SetNACKStatus(mChannelId, ZIMEConfig.mNACK);
				}
			}

		};

		// AECScene
		mCheckedChangeListenerAECTV = new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked)
			{
				if (isChecked)
				{
					ZIMEConfig.mVQEScene = 1;
				}
				else
				{
					ZIMEConfig.mVQEScene = 0;
				}

				mChannelId = mConfig.mChannelId;
				Log.i(TAG, "-------------SetAECSceneStatus-------------- channel id = " + mChannelId);

				if(!(mStarted && !mStopped))
				{
					return;
				}

				if(!ZIMEConfig.mIsOnlyAudio)
				{
					if (null != mAVClient && -1 != mChannelId)
					{
						ZIMEVideoClientJNI.SetVQEScene(ZIMEConfig.mVQEScene);
					}
				}
				else
				{
					if (null != mAClient && -1 != mChannelId)
					{
						ZIMEClientJni.SetVQEScene(ZIMEConfig.mVQEScene);
					}
				}

			}
		};

		// Mute
		mCheckedChangeListenerMute = new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked)
			{
				ZIMEConfig.mMute = isChecked;
				mChannelId = mConfig.mChannelId;
				Log.i(TAG, "-------------SetMute-------------- channel id = " + mChannelId);

				if(!(mStarted && !mStopped))
				{
					return;
				}

				if(!ZIMEConfig.mIsOnlyAudio)
				{
					if (null != mAVClient && -1 != mChannelId)
					{
						ZIMEVideoClientJNI.SetInputMute(mChannelId, ZIMEConfig.mMute);
					}
				}
				else
				{
					if (null != mAClient && -1 != mChannelId)
					{
						ZIMEClientJni.SetInputMute(mChannelId, ZIMEConfig.mMute);
					}
				}

			}

		};

		// Pause
		mCheckedChangeListenerDisplay = new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked)
			{
				ZIMEConfig.mPauseVideo = isChecked;
				mChannelId = mConfig.mChannelId;
				Log.i(TAG, "-------------StartCapFileAsCamera-------------- channel id = " + mChannelId);

				if(!(mStarted && !mStopped))
				{
					return;
				}

				if (null != mAVClient && -1 != mChannelId)
				{
					if(ZIMEConfig.mPauseVideo)
					{
						ZIMEVideoClientJNI.StartCapFileAsCamera(mChannelId, ZIMEConfig.mPicPath);
					}
					else
					{
						ZIMEVideoClientJNI.StopCapFileAsCamera(mChannelId);
					}
				}
			}
		};

		// set InbandDTMF
		mCheckedChangeListenerInbandDTMF = new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked)
			{
				ZIMEConfig.mInbandDTMF = isChecked;
				mChannelId = mConfig.mChannelId;
				Log.i(TAG, "-------------set InbandDTMF or not-------------- channel id = " + mChannelId);

				if(!(mStarted && !mStopped))
				{
					return;
				}
			}
		};

		// Audio
		mCheckedChangeListenerAudio = new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked)
			{
				ZIMEConfig.mIsOnlyAudio = isChecked;

				if(isChecked)
				{
					mCheckboxFEC.setChecked(false);
					mCheckboxNACK.setChecked(false);
					mCheckboxDisplay.setChecked(false);
					mCheckBoxCamera.setChecked(false);
					mCheckBoxSync.setChecked(false);
					mCheckBoxConstantBps.setChecked(false);
					mCheckboxVBR.setChecked(false);

					mCheckboxFEC.setEnabled(false);
					mCheckboxNACK.setEnabled(false);
					mCheckboxDisplay.setEnabled(false);
					mCheckBoxCamera.setEnabled(false);
					mCheckBoxSync.setEnabled(false);
					mCheckBoxConstantBps.setEnabled(false);
					mCheckboxVBR.setEnabled(false);
				}
				else
				{
					mCheckboxFEC.setEnabled(true);
					mCheckboxNACK.setEnabled(true);
					mCheckboxDisplay.setEnabled(true);
					mCheckBoxCamera.setEnabled(true);
					mCheckBoxSync.setEnabled(true);
					mCheckBoxConstantBps.setEnabled(true);
					mCheckboxVBR.setEnabled(true);
				}

				Message msg = mHandler.obtainMessage(1, mCheckboxAudio);
				mHandler.sendMessage(msg);
			}
		};

		// VBR
		mCheckedChangeListenerVBR = new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked)
			{
				ZIMEConfig.mVBR = isChecked;
				Log.i(TAG, "-------------SetVBR: " + ZIMEConfig.mVBR);
			}
		};

		// Camera
		mCheckedChangeListenerCamera = new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked)
			{
				mChannelId = mConfig.mChannelId;
				ZIMEConfig.mCameraId = isChecked ? 1:0;
				Log.i(TAG, "-------------SetVideoDevices-------------- channel id = " + mChannelId);

				if(!(mStarted && !mStopped))
				{
					return;
				}

				if (null != mAVClient && -1 != mChannelId)
				{
					ZIMEVideoClientJNI.SetVideoDevices(ZIMEConfig.mCameraId);
				}
			}
		};

		// NS
		mCheckedChangeListenerNS = new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked)
			{
				ZIMEConfig.mNS = isChecked;
				mChannelId = mConfig.mChannelId;
				Log.i(TAG, "-------------SetAECStatus-------------- channel id = " + mChannelId);

				if(!(mStarted && !mStopped))
				{
					return;
				}

				if(!ZIMEConfig.mIsOnlyAudio)
				{
					if (null != mAVClient && -1 != mChannelId)
					{
						ZIMEVideoClientJNI.SetNSStatus(ZIMEConfig.mNS);
					}
				}
				else
				{
					if (null != mAClient && -1 != mChannelId)
					{
						ZIMEClientJni.SetNSStatus(ZIMEConfig.mNS);
					}
				}
			}

		};

		// 端口和ip过滤
		mCheckedChangeListenerSourceFilter = new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ZIMEConfig.mSourceFilter = isChecked;
				mChannelId = mConfig.mChannelId;
				Log.i(TAG, "-------------Set Source Filter -------------- channel id = " + mChannelId);

				mPeerIP = mAVClient.new StrPtr();
				mPeerIP.retuStr = mConfig.mRecvIP;

				if(!(mStarted && !mStopped))
				{
					return;
				}

				if(ZIMEConfig.mSourceFilter)
				{
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mChannelId)
						{
							ZIMEVideoClientJNI.SetSourceFilter(mChannelId, mConfig.mAudioRTCPport, mConfig.mVideoRTPport, mPeerIP);
						}
					}
					else
					{
						if (null != mAClient && -1 != mChannelId)
						{
							ZIMEClientJni.SetSourceFilter(mChannelId, mConfig.mAudioRTCPport, mConfig.mRecvIP);
						}
					}
				}
				else
				{
					mPeerIP.retuStr = "0.0.0.0";
					if(!ZIMEConfig.mIsOnlyAudio)
					{
						if (null != mAVClient && -1 != mChannelId)
						{
							ZIMEVideoClientJNI.SetSourceFilter(mChannelId, 0, 0, mPeerIP);
						}
					}
					else
					{
						if (null != mAClient && -1 != mChannelId)
						{
							ZIMEClientJni.SetSourceFilter(mChannelId, 0, "0.0.0.0");
						}
					}
				}

			}
		};


		// 固定码率ConstantBps
		mCheckedChangeListenerConstantBps = new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked)
			{
				if(mStarted || !mStopped)
				{
					return;
				}
				mConfig.mbConstantBps = isChecked;
				Log.i(TAG, "-------------Config：One Level--------------  " + isChecked);
			}
		};

		// 编码模式
		mCheckedChangeListenerEncodeProfile = new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked)
			{
				if(isChecked)
				{
					ZIMEConfig.mEncodeProfile = 1;
				}
				else
				{
					ZIMEConfig.mEncodeProfile = 0;
				}
			}

		};

		//设置视频编码类型
		mItemSelectedListenerCodec = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				Log.i(TAG, "-------------Config：Set video codec-------------- mStarted = " + mStarted + " mStopped = " + mStopped);
				if(mStarted || !mStopped)
				{
					return;
				}
				mChannelId = mConfig.mChannelId;
				ZIMEConfig.mUsingEncodeCAllback = false;
				ZIMEConfig.mVendorType = ZIMEConfig.enumZIME_GENERA_VENDORTYPE;

				String codecType = mSpinnerVideoCodec.getAdapter().getItem(arg2).toString();


				if (0 == codecType.compareToIgnoreCase("InnerSoft (H265)")) {
					ZIMEConfig.mIsH265 = true;
				}
				else
				{
					ZIMEConfig.mIsH265 = false;
				}

				if (0 == codecType.compareToIgnoreCase("InnerSoft (H264)")) {
				}
				else if (0 == codecType.compareToIgnoreCase("InnerSoft (Callback)")) {
					ZIMEConfig.mCodecType = ZIMEConfig.enumZIME_GOTAEXTERNENCODER;
					ZIMEConfig.mUsingEncodeCAllback = true;
				}
				else if (0 == codecType.compareToIgnoreCase("Amlogic (Callback)")) {
					ZIMEConfig.mCodecType = ZIMEConfig.enumZIME_AMLOGICHARDWEAR;
					ZIMEConfig.mUsingEncodeCAllback = true;
				}
				else if (0 == codecType.compareToIgnoreCase("MediaCodec (Callback)")) {
					if(ZIMEAVDemoActivity.mSupportMediaCodec){
						ZIMEConfig.mCodecType = ZIMEConfig.enumZIME_MediaCodec;
					}
				}
				else {

				}


				//aml回调编码器不能使用pause功能
				if((ZIMEConfig.mUsingEncodeCAllback == true) || (ZIMEConfig.mCodecType == ZIMEConfig.enumZIME_AMLOGICHARDWEAR))
				{
					mCheckboxDisplay.setEnabled(false);
					mCheckboxDisplay.setChecked(false);
					ZIMEConfig.mPauseVideo = false;
				}
				else
				{
					mCheckboxDisplay.setEnabled(true);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		};

		//设置档位
		mItemSelectedListenerLevel= new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				Log.i(TAG, "-------------Config：Set video Level-------------- mStarted = " + mStarted + " mStopped = " + mStopped);
				if(mStarted || !mStopped)
				{
					return;
				}

				String Level = mSpinnerVideoSizeIdx.getAdapter().getItem(arg2).toString();
				String aLine[] = Level.split("/");
				mConfig.mWidth = Integer.parseInt(aLine[0]);
				mConfig.mHeight = Integer.parseInt(aLine[1]);
				mConfig.mFrameRate = Integer.parseInt(aLine[2]);
				mConfig.mInitBitRate = Integer.parseInt(aLine[3]) * 1000;

				Log.i(TAG, "-------------Select Video Initial Level = " + Level);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		};

		//动态设置日志级别
		mItemSelectedListenerSetLogLevel=new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {

				int pos = (int)adapter.getSelectedItemPosition();
				int logLevel = (pos+1)*10000;

				Log.i(TAG, "Select Log Level = " + logLevel);

				if(mAVClient != null && mAClient == null){
					ZIMEVideoClientJNI.SetLogLevel(logLevel);
				}
				if(mAClient != null){
					ZIMEClientJni.SetLogLevel(logLevel);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		};

		// 自呼
		mCheckedChangeListenerCallSelf = new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked){

				Log.i(TAG, "-------------Config：CallSelf-------------- mStarted = " + mStarted + " mStopped = " + mStopped);
				if(mStarted || !mStopped) {
					return;
				}

				if(isChecked){
					mConfig.mRecvIP = mConfig.getLocalIpAddress();
					mRevTxtIP.setText(mConfig.mRecvIP);

					Log.i(TAG, "-------------Config：CallSelf-------------- ip = " + mConfig.mRecvIP);
				}
			}
		};

		//设置外部收发
		mCheckedChangeListenerExTrans = new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(mStarted || !mStopped)
				{
					return;
				}
				ZIMEConfig.mIsExTrans = isChecked;
				mChannelId = mConfig.mChannelId;
				Log.i(TAG, "-------------SetExTrans-------------- channel id = " +
						"" + mChannelId + ", isChecked = " + isChecked);

			}
		};

		//设置日志回调  
		mCheckedChangeListenerLogCallBack = new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCheckBoxLogCallBack.setEnabled(false);
				ZIMEConfig.mIsLogCallBack = isChecked;

				mChannelId = mConfig.mChannelId;
				Log.i(TAG, "-------------SetLogCallBack-------------- channel id = " +
						"" + mChannelId + ", isChecked = " + isChecked);

				if (isChecked)
				{
					ZIMEVideoClientJNI.SetLogCallBack();
				}
			}
		};

		//同步
		mCheckedChangeListenerSync = new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				ZIMEConfig.mSync = isChecked;
				mChannelId = mConfig.mChannelId;
				Log.i(TAG, "-------------SetSynchronization -------------- channel id = " + mChannelId);

				if(mStarted || !mStopped)
				{
					return;
				}

				if (null != mAVClient && -1 != mChannelId)
				{
					ZIMEVideoClientJNI.SetSynchronization(mChannelId, true);
				}
			}
		};

		//设置保存H26X文件  
		mCheckedChangeListenerSaveH26X = new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				ZIMEConfig.mSaveH26XFile = isChecked;
				Log.i(TAG, "-------------SetSaveH26XFile -------------- ");
			}
		};

		// 清空/还原qvga档位
		// 1.当复选框勾选上时，使帧率码率表编辑框为空；
		// 2.当复选框没有勾选上时，帧率码率的长度不为0，就使帧率码率表编辑框为以前内容。
		mCheckedChangeListenerQVGA = new CompoundButton.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mEditVideoCodecLevel_qvga.setText("");
				}
				else
				{
					if(mEditVideoCodecLevel_qvga.length() == 0){
						mEditVideoCodecLevel_qvga.setText(QVGAStr);
					}
				}
			}
		};

		// 清空/还原vga档位
		mCheckedChangeListenerVGA = new CompoundButton.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mEditVideoCodecLevel_vga.setText("");
				}
				else
				{
					if(mEditVideoCodecLevel_vga.length() == 0){
						mEditVideoCodecLevel_vga.setText(VGAStr);
					}
				}
			}
		};

		// 清空/还原720p档位
		mCheckedChangeListener720p = new CompoundButton.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mEditVideoCodecLevel_720p.setText("");
				}
				else
				{
					if(mEditVideoCodecLevel_720p.length() == 0){
						mEditVideoCodecLevel_720p.setText(_720pStr);
					}
				}
			}
		};

		mCheckedChangeListenerSpeaker = new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub

				if(mAudioManager != null)
				{
					mAudioManager.setSpeakerphoneOn(isChecked);
					mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
				}

				if(ZIMEConfig.mIsOnlyAudio)
				{
					if (null != mAClient )
					{

						ZIMEClientJni.SetSpeakerMode(isChecked);
					}
				}
				else
				{
					if (null != mAVClient)
					{
						ZIMEVideoClientJNI.SetSpeakerMode(isChecked);
					}
				}
			}
		};


		// QVGA档位 文本监视事件
		// 1.当编辑框修改后，且编辑框的长度不为空时，则使复选框不勾选上。
		// 2.当编辑框修改后，且编辑框的长度为空时，则使复选框勾选上。
		mTextWatcherQVGA = new TextWatcher(){
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(mEditVideoCodecLevel_qvga.length() != 0){
					mCheckBoxQVGA.setChecked(false);
				}
				if(mEditVideoCodecLevel_qvga.length() == 0){
					mCheckBoxQVGA.setChecked(true);
				}
			}

		};

		// VGA档位 文本监视事件
		mTextWatcherVGA = new TextWatcher(){
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(mEditVideoCodecLevel_vga.length() != 0){
					mCheckBoxVGA.setChecked(false);
				}
				if(mEditVideoCodecLevel_vga.length() == 0){
					mCheckBoxVGA.setChecked(true);
				}
			}

		};

		// 720P档位 文本监视事件
		mTextWatcher720p = new TextWatcher(){
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(mEditVideoCodecLevel_720p.length() != 0){
					mCheckBox720p.setChecked(false);
				}
				if(mEditVideoCodecLevel_720p.length() == 0){
					mCheckBox720p.setChecked(true);
				}
			}
		};

	}


	public void reset()
	{
		mCheckboxAECTV.setChecked(false);
		mCheckboxFEC.setChecked(false);
		mCheckboxNACK.setChecked(false);
		mCheckboxAudio.setChecked(false);
		mCheckboxDisplay.setChecked(false);
		mCheckboxMute.setChecked(false);
		mCheckboxVBR.setChecked(false);
		mCheckBoxCamera.setChecked(false);
		mCheckBoxNS.setChecked(false);
		mCheckBoxSourceFilter.setChecked(false);
		mCheckBoxConstantBps.setChecked(false);
		mCheckBoxCallSelf.setChecked(false);
	}

	public void enableAudio(boolean bEnabled)
	{
		mCheckboxAudio.setEnabled(bEnabled);
	}

	public void enableSaveH26X(boolean bEnabled)
	{
		mCheckBoxSaveH26X.setEnabled(bEnabled);
	}

	public void enableExTrans(boolean bEnabled)
	{
		mCheckBoxExTrans.setEnabled(bEnabled);
	}

	public void enableSync(boolean bEnabled)
	{
		mCheckBoxSync.setEnabled(bEnabled);
	}

	public void setStatus(boolean bStarted, boolean bStopped)
	{
		mStarted = bStarted;
		mStopped = bStopped;

		if(mStarted || !mStopped)
		{
			mCheckBoxConstantBps.setEnabled(false);
			mCheckBoxCallSelf.setEnabled(false);
			mCheckboxAudio.setEnabled(false);
			mCheckboxVBR.setEnabled(false);
		}
		else
		{
			mCheckBoxConstantBps.setEnabled(true);
			mCheckBoxCallSelf.setEnabled(true);
			mCheckboxAudio.setEnabled(true);
			mCheckboxVBR.setEnabled(true);
		}
	}


	private static List<String> ChangeLevelTolist()
	{
		List<String> liststr = new ArrayList<String>();

		mConfig.mVideoCodecLevel_qvga = mEditVideoCodecLevel_qvga.getText().toString();
		mConfig.mVideoCodecLevel_vga = mEditVideoCodecLevel_vga.getText().toString();
		mConfig.mVideoCodecLevel_720p = mEditVideoCodecLevel_720p.getText().toString();

		T_ZIMEVideoSwitchLevel[] levels = mConfig.GetAllResOfQualitySet();


		int i;
		for( i = 0;i < levels.length; i++)
		{
			String str = String.format("%d/%d/%d/%d", levels[i].s32Width, levels[i].s32Height, levels[i].s32Framerate, levels[i].s32Bitrate);
			liststr.add(str);
		}

		// 初始化
		mConfig.mWidth = levels[0].s32Width;
		mConfig.mHeight = levels[0].s32Height;
		mConfig.mFrameRate = levels[0].s32Framerate;
		mConfig.mInitBitRate = levels[0].s32Bitrate * 1000;

		return liststr;
	}

	public static class Builder{
		private Context context;
		public Builder(Context context)
		{
			this.context = context;
		}

		private int LoadCameraSize()
		{
			/*Test:不调用release，open捕获异常
			 * 抛出的异常：
			 * 1. Fail to connect to camera service
			 * 2. Camera initialization failed
			 * 3. Can't find android/hardware/Camera
			 */


			if(!FirstLoadPreviewSize)
			{

				Camera mCamera = null;
				try {
					mCamera = Camera.open(0);
				} catch (Exception e) {
					Log.e(TAG, "Can't open Camera[0]" + "Reason:" + e.toString());
					mCamera = null;
					return -1;
				}

				if (mCamera == null)
				{
					return -1;
				}

				Camera.Parameters params =  null;
				try {
					params = mCamera.getParameters();
				} catch (Exception e) {
					Log.e(TAG, "mCamera getParameters failed,Reason:" + e.toString());
					mCamera.release();
					mCamera = null;
					return -1;
				}

				List<Size> camSizeList = params.getSupportedPreviewSizes();
				int iCameraSize = camSizeList.size();

				mVideoDevCapSize = new Size[iCameraSize];
				mVideDevCapSizeStrArray = new String[iCameraSize+4];
				mVideoResWidthArr = new int[iCameraSize+4];
				mVideoResHeightArr = new int[iCameraSize+4];

				int Num = 0;
				for(int i = 0; i < iCameraSize; i++)
				{
					//if(camSizeList.get(i).width == 640 && camSizeList.get(i).height == 480){
					mVideoDevCapSize[Num] = camSizeList.get(i);
					mVideDevCapSizeStrArray[Num] = camSizeList.get(i).width + "*" + camSizeList.get(i).height;
					mVideoResWidthArr[Num] = camSizeList.get(i).width;
					mVideoResHeightArr[Num] = camSizeList.get(i).height;
					Num++;
					Log.i(TAG, "VideoProducer open-------width:" + camSizeList.get(i).width + ",height:" + camSizeList.get(i).height);
					//}

				}

				mVideDevCapSizeStrArray[Num] = "336*192";
				mVideoResWidthArr[Num] = 336;
				mVideoResHeightArr[Num]	= 192;
				Num++;
				mVideDevCapSizeStrArray[Num] = "512*288";
				mVideoResWidthArr[Num] 	= 512;
				mVideoResHeightArr[Num]	= 288;
				Num++;
				mVideDevCapSizeStrArray[Num] = "768*448";
				mVideoResWidthArr[Num] 	= 768;
				mVideoResHeightArr[Num]	= 448;
				Num++;	
				/*mVideDevCapSizeStrArray[Num] = "704*576";
				mVideoResWidthArr[Num] 	= 704;
				mVideoResHeightArr[Num]	= 576;				
				Num++;*/
				mVideDevCapSizeStrArray[Num] = "1024*576";
				mVideoResWidthArr[Num] 	= 1024;
				mVideoResHeightArr[Num]	= 576;
				Num++;


				for(int i = 0; i < Num; i++)
				{
					Log.i(TAG, "VideoProducer open-------width:" + mVideoResWidthArr[i] + ",height:" + mVideoResHeightArr[i]);
				}

				mCamera.release();
				FirstLoadPreviewSize = true;
			}

			ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mVideDevCapSizeStrArray);
			array_adapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
			mSpinnerVideoSizeIdx.setAdapter(array_adapter);
			mSpinnerVideoSizeIdx.setSelection(0);

			return 0;

		}

		public int SetZIMESDKClient(ZIMEVideoClientJNI iAVClient, ZIMEClientJni iAClient, ZIMEConfig iConfig)
		{
			if(iAVClient == null && iAClient == null)
			{
				Log.e(TAG, "ZIMEVideoClientJNI & ZIMEClientJni are null");
				return -1;
			}

			mAClient  = iAClient;
			mAVClient = iAVClient;
			mConfig = iConfig;

			return 0;
		}

		public ZIMEDialogSetting create(/*Handler handler*/)
		{
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final ZIMEDialogSetting dialog = new ZIMEDialogSetting(context, R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog_setting, null);

			//mHandler = handler;

			// 接收IP
			mRevTxtIP = (EditText)layout.findViewById(R.id.editTextRevIP);
			//mRevTxtIP.setText(mConfig.mRecvIP);
			mRevTxtIP.setText("192.168.1.103");

			// 设置音频编解码类型
			mSpinnerAudioCodec  = (Spinner)layout.findViewById(R.id.spinnerAudioCodecType);
			String[ ] itemsACodec = {"PCMA", "PCMU", "G729", "SILK", "AMR-WB"};
			ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, itemsACodec);
			array_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpinnerAudioCodec.setAdapter(array_adapter );
			mSpinnerAudioCodec.setSelection(0, true);

			// 设置视频编解码器类型
			mSpinnerVideoCodec = (Spinner)layout.findViewById(R.id.spinnerVideoCodecType);
			mSpinnerVideoCodec.setSelection(0, true);
			mSpinnerVideoCodec.setOnItemSelectedListener(mItemSelectedListenerCodec);

			// 设置日志级别
			mSpinnerSetLogLevel = (Spinner)layout.findViewById(R.id.spinnerSetLogLevel);
			mSpinnerSetLogLevel.setSelection(0, true);
			mSpinnerSetLogLevel.setOnItemSelectedListener(mItemSelectedListenerSetLogLevel);

			// 初始最大码率
			mTxtMaxBitRate = (EditText)layout.findViewById(R.id.editTextMaxBitRate);
			mTxtMaxBitRate.setText("8000");

			//nal个数
			mEditNalNum  = (EditText)layout.findViewById(R.id.editTextNaluNum);
			mEditNalNum.setText("2");

			// 帧率码率表
			// 对帧率码率表编辑框添加文本修改监视函数，
			// 让编辑框后的一键删除/还原复选框与编辑框内容保持同步：
			// 1.帧率码率表有内容时，复选框没有被勾选；
			// 2.帧率码率表没有内容时，复选框被勾选；
			mEditVideoCodecLevel_qvga = (EditText)layout.findViewById(R.id.editTextVideoCodecLevel_QVGA);
			mEditVideoCodecLevel_qvga.setText(QVGAStr);
			mEditVideoCodecLevel_qvga.addTextChangedListener(mTextWatcherQVGA);
			mEditVideoCodecLevel_vga = (EditText)layout.findViewById(R.id.editTextVideoCodecLevel_VGA);
			mEditVideoCodecLevel_vga.setText(VGAStr);
			mEditVideoCodecLevel_vga.addTextChangedListener(mTextWatcherVGA);
			mEditVideoCodecLevel_720p = (EditText)layout.findViewById(R.id.editTextVideoCodecLevel_720p);
			mEditVideoCodecLevel_720p.setText(_720pStr);
			mEditVideoCodecLevel_720p.addTextChangedListener(mTextWatcher720p);

			// AEC
			mCheckboxAECTV = (CheckBox)layout.findViewById(R.id.CheckBoxAECTV);
			mCheckboxAECTV.setChecked(false);
			mCheckboxAECTV.setOnCheckedChangeListener(mCheckedChangeListenerAECTV);

			// FEC
			mCheckboxFEC = (CheckBox)layout.findViewById(R.id.CheckBoxFEC);
			mCheckboxFEC.setChecked(true);
			mCheckboxFEC.setOnCheckedChangeListener(mCheckedChangeListenerFEC);

			// NACK
			mCheckboxNACK = (CheckBox)layout.findViewById(R.id.CheckBoxNACK);
			mCheckboxNACK.setChecked(false);
			mCheckboxNACK.setEnabled(false);
			mCheckboxNACK.setOnCheckedChangeListener(mCheckedChangeListenerNACK);

			// Audio
			mCheckboxAudio = (CheckBox)layout.findViewById(R.id.CheckBoxAudio);
			mCheckboxAudio.setChecked(false);
			mCheckboxAudio.setOnCheckedChangeListener(mCheckedChangeListenerAudio);

			// pause
			mCheckboxDisplay = (CheckBox)layout.findViewById(R.id.CheckBoxPause);
			mCheckboxDisplay.setChecked(false);
			mCheckboxDisplay.setOnCheckedChangeListener(mCheckedChangeListenerDisplay);

			// 发送DTMF
			mCheckboxInbandDTMF = (CheckBox)layout.findViewById(R.id.CheckBoxInbandDTMF);
			mCheckboxInbandDTMF.setOnCheckedChangeListener(mCheckedChangeListenerInbandDTMF);
			mCheckboxInbandDTMF.setChecked(false);

			// 静音
			mCheckboxMute = (CheckBox)layout.findViewById(R.id.CheckBoxMute);
			mCheckboxMute.setChecked(false);
			mCheckboxMute.setOnCheckedChangeListener(mCheckedChangeListenerMute);

			// CBR/VBR切换
			mCheckboxVBR = (CheckBox)layout.findViewById(R.id.CheckBoxVBR);
			mCheckboxVBR.setChecked(mConfig.mVBR);
			mCheckboxVBR.setOnCheckedChangeListener(mCheckedChangeListenerVBR);
			mCheckboxVBR.setEnabled(true);

			// 摄像头切换
			mCheckBoxCamera = (CheckBox)layout.findViewById(R.id.CheckBoxCamera);
			mCheckBoxCamera.setChecked(false);
			mCheckBoxCamera.setOnCheckedChangeListener(mCheckedChangeListenerCamera);

			// NS
			mCheckBoxNS = (CheckBox)layout.findViewById(R.id.CheckBoxNS);
			mCheckBoxNS.setChecked(true);
			mCheckBoxNS.setOnCheckedChangeListener(mCheckedChangeListenerNS);

			// 端口和IP过滤
			mCheckBoxSourceFilter = (CheckBox)layout.findViewById(R.id.CheckBoxSourceFilter);
			mCheckBoxSourceFilter.setChecked(true);
			mCheckBoxSourceFilter.setOnCheckedChangeListener(mCheckedChangeListenerSourceFilter);

			// 图像分辨率
			mSpinnerVideoSizeIdx  = (Spinner)layout.findViewById(R.id.SizeIndx);
			mSpinnerVideoSizeIdx.setOnItemSelectedListener(mItemSelectedListenerLevel);
			// LoadCameraSize();

			// 设置固定档位
			mCheckBoxConstantBps = (CheckBox)layout.findViewById(R.id.CheckBoxConstantBps);
			mCheckBoxConstantBps.setChecked(false);
			mCheckBoxConstantBps.setOnCheckedChangeListener(mCheckedChangeListenerConstantBps);

			// 一键删除档位
			mCheckBoxQVGA = (CheckBox)layout.findViewById(R.id.checkBoxQVGA);
			mCheckBoxQVGA.setChecked(false);
			mCheckBoxQVGA.setOnCheckedChangeListener(mCheckedChangeListenerQVGA);
			mCheckBoxVGA = (CheckBox)layout.findViewById(R.id.checkBoxVGA);
			mCheckBoxVGA.setChecked(false);
			mCheckBoxVGA.setOnCheckedChangeListener(mCheckedChangeListenerVGA);
			mCheckBox720p = (CheckBox)layout.findViewById(R.id.checkBox720p);
			mCheckBox720p.setChecked(false);
			mCheckBox720p.setOnCheckedChangeListener(mCheckedChangeListener720p);

			// 设置编码模式
			mCheckboxEncodeProfile = (CheckBox)layout.findViewById(R.id.CheckBoxHP);
			mCheckboxEncodeProfile.setChecked(true);
			mCheckboxEncodeProfile.setOnCheckedChangeListener(mCheckedChangeListenerEncodeProfile);

			// 设置自呼
			mCheckBoxCallSelf = (CheckBox)layout.findViewById(R.id.checkCallSelf);
			mCheckBoxCallSelf.setChecked(false);
			mCheckBoxCallSelf.setOnCheckedChangeListener(mCheckedChangeListenerCallSelf);

			// 设置ExTrans
			mCheckBoxExTrans = (CheckBox)layout.findViewById(R.id.CheckBoxExTrans);
			mCheckBoxExTrans.setChecked(false);
			mCheckBoxExTrans.setOnCheckedChangeListener(mCheckedChangeListenerExTrans);

			//设置日志回调
			mCheckBoxLogCallBack = (CheckBox)layout.findViewById(R.id.CheckBoxLogCallBack);
			mCheckBoxLogCallBack.setChecked(false);
			mCheckBoxLogCallBack.setOnCheckedChangeListener(mCheckedChangeListenerLogCallBack);

			//保存H26X文件
			mCheckBoxSaveH26X = (CheckBox)layout.findViewById(R.id.CheckBoxSaveH264File);
			mCheckBoxSaveH26X.setChecked(false);
			mCheckBoxSaveH26X.setOnCheckedChangeListener(mCheckedChangeListenerSaveH26X);

			// 同步
			mCheckBoxSync = (CheckBox)layout.findViewById(R.id.CheckBoxSync);
			mCheckBoxSync.setChecked(true);
			mCheckBoxSync.setOnCheckedChangeListener(mCheckedChangeListenerSync);

			// 确认并退出
			mButtonOK =(Button)layout.findViewById(R.id.buttonOK);
			mButtonOK.setOnClickListener(mClickListenerOK);
			//设置免提和听筒
			mCheckBoxSpeaker = (CheckBox) layout.findViewById(R.id.CheckBoxSpeaker);
			mCheckBoxSpeaker.setChecked(false);
			mCheckBoxSpeaker.setOnCheckedChangeListener(mCheckedChangeListenerSpeaker);
			// 更新档位
			mButtonUpdateLevel =(Button)layout.findViewById(R.id.buttonUpdateLevel);
			mButtonUpdateLevel.setOnClickListener(mClickListenerUpdateLevel);

			// 手机置为不可见,手机不需要主动调用刷新level等级，有事件可触发
			// VISIBLE:0 意思是可见的
			// INVISIBLE:4 意思是不可见的，但还占着原来的空间
			// GONE:8 意思是不可见的，不占用原来的布局空间
			mButtonUpdateLevel.setVisibility(View.VISIBLE);

			// 帧率码率表
			mConfig.mVideoCodecLevel_qvga = mEditVideoCodecLevel_qvga.getText().toString();
			mConfig.mVideoCodecLevel_vga  = mEditVideoCodecLevel_vga.getText().toString();
			mConfig.mVideoCodecLevel_720p = mEditVideoCodecLevel_720p.getText().toString();

			mVideoQualityLevellist = dialog.ChangeLevelTolist();
			ArrayAdapter<String> array_adapter_level = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mVideoQualityLevellist);
			array_adapter_level.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
			mSpinnerVideoSizeIdx.setAdapter(array_adapter_level);
			mSpinnerVideoSizeIdx.setSelection(0);

			dialog.setContentView(layout);
			return dialog;
		}
	}

	public void setAudioManager(AudioManager i_AudioManager)
	{
		mAudioManager = i_AudioManager;
		mAudioManager.setSpeakerphoneOn(false);
		mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
	}

}
