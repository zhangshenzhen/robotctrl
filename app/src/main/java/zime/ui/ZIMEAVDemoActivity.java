package zime.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaCodecInfo;
import android.opengl.GLES11Ext;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.brick.robotctrl.R;

import java.util.ArrayList;
import java.util.Locale;

import zime.media.VideoDecodeCallBack;
import zime.media.VideoDeviceCallBack;
import zime.media.ZIMEClientJni;
import zime.media.ZIMEVideoClientJNI;
import zime.media.ZMCEVideoGLRender;
import zime.ui.ZIMEDialogDTMFDialer.DTMFDialerBuilder;
import zime.ui.ZIMEDialogSetting.Builder;

@SuppressLint("ShowToast")
public class ZIMEAVDemoActivity extends Activity {

	private final static String ZIMETAG = "ZIMEAVDemoActivity";

	public static SurfaceTexture surfaceTexture;

	private Context mContext = null;
	private Button mButtonStart;
	private Button mButtonStop;
	private Button mButtonConf;
	private Button mButtonExit;
	private Button mButtonStartSend;
	private Button mButtonStartRecv;
	private Button mButtonSendDTMF;
	private Button mButtonSendOneIFrame;
	private Button mButtonAstart;
	private Button mButtonSwtich;
	private TextView mTextViewDownQoS;
	private SurfaceView mSurfaceLocalView = null;
	private GLSurfaceView mSurfaceRemoteView = null;
	private ZIMEDialogSetting.Builder mDiaglogBuilder = null;
	private boolean mSurfaceExist = false;
	private ZIMEDialogSetting mDialogSetting = null;
	private ZIMEDialogDTMFDialer.DTMFDialerBuilder mDialogDTMFDialerBuilder= null;
	private ZIMEDialogDTMFDialer mDialogDTMFDialer = null;

	private ZIMEClientJni mAudioClientJNI = null;
	private ZIMEVideoClientJNI mVideoClientJNI = null;
	private ZIMEConfig mZIMEConfig = null;
	public ZIMEJniThread mZIMEJniThread = null;
	private static WakeLock mWakelock = null;
	private static boolean mStarted = false;
	private static boolean mStopped = true;
	private static boolean m_bSetParam = false;

	private static int DEVTYPE_DEFAULT_VALUE = 2;
	private static int m_iDeviceType = DEVTYPE_DEFAULT_VALUE;  // 默认home行为

	// opengl 
	private ZMCEVideoGLRender mVideoGLRender = null;
	private AudioManager am = null;
	public String mStringDecodeTypeName = "";
	public  int  mYUVType = 19;
	public  static boolean  mSupportMediaCodec = false;
	Handler mHandler = null;
	String mStrResSwitchInfo = "";
	public int mVResWidth_S = 0;
	public int mVResWidth_R = 0;

	// Handler
	private Handler mViewHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 1)
			{
				CheckBox cboAudio = (CheckBox)msg.obj;
				mButtonAstart.setEnabled(!cboAudio.isChecked());
			}

			super.handleMessage(msg);
		}

	};

	public void ToastShow(String i_text)
	{
		mStrResSwitchInfo = new String(i_text);
		Message msg = new Message();
		msg.what = 1;
		mHandler.sendMessage(msg);
	}

	public void GetDownLinkQosToShow(String i_DownLinkQos)
	{
		if(mStarted)
		{
			mTextViewDownQoS.setTextColor(android.graphics.Color.RED);
			mTextViewDownQoS.setText(i_DownLinkQos);
		}
	}

	@SuppressWarnings("deprecation")
	public static void keepScreenOn(Context context, boolean on) {
		if (on) {
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			mWakelock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
			mWakelock.acquire();
		}else {
			mWakelock.release();
			mWakelock = null;
		}
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		mSurfaceRemoteView.onResume();
		if(ZIMEConfig.mIsOnlyAudio)
		{
			int eRet = ZIMEClientJni.ConnectDevice(mZIMEConfig.mChannelId, 1);
			String logString = "onResume---connect Device:" + 1 + "---ret: " + eRet;
			Toast.makeText(mContext, logString, Toast.LENGTH_LONG).show();
		}
		else if (mSurfaceExist)
		{
			//锁屏时走这个分支恢复画面			
			int eRet = ZIMEVideoClientJNI.ConnectDevice(mZIMEConfig.mChannelId, m_iDeviceType);

			//如果发“狗”图片，暂停时码率波动比较大
			String logString = "onResume---connect Device:" + m_iDeviceType + "---ret: " + eRet;
			Toast.makeText(mContext, logString, Toast.LENGTH_LONG).show();
			m_iDeviceType = DEVTYPE_DEFAULT_VALUE;
		}

	}

	@Override
	protected void onPause()
	{
		super.onPause();
		mSurfaceRemoteView.onPause();
		String logString;
		if(ZIMEConfig.mIsOnlyAudio)
		{
			ZIMEClientJni.DisconnectDevice(mZIMEConfig.mChannelId, 1);
			logString = "onpause---Distconnnect Device:" + 1;
		}
		else
		{
			ZIMEVideoClientJNI.DisconnectDevice(mZIMEConfig.mChannelId, m_iDeviceType);
			logString = "onpause---Distconnnect Device:" + m_iDeviceType;
		}

		Toast.makeText(mContext, logString, Toast.LENGTH_LONG).show();

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置为无标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//隐去状态栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mContext = this;
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.zime);
		//keepScreenOn(getApplicationContext(), true);

		Log.e(ZIMETAG, "OnCreate start--------- ");
		am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

		mButtonStart 		= (Button)findViewById(R.id.buttonStart);
		mButtonStop 		= (Button)findViewById(R.id.buttonStop);
		mButtonConf 		= (Button)findViewById(R.id.buttonConf);
		mButtonExit 		= (Button)findViewById(R.id.buttonExit);
		mButtonStartSend 	= (Button)findViewById(R.id.buttonStartSend);
		mButtonStartRecv 	= (Button)findViewById(R.id.buttonStartRecv);
		mButtonSendDTMF     = (Button)findViewById(R.id.buttonSendDTMF);
		mButtonAstart		= (Button)findViewById(R.id.buttonAStart);
		mButtonSwtich		= (Button)findViewById(R.id.buttonSwitch);
		mButtonSendOneIFrame = (Button)findViewById(R.id.buttonConfigVideoIntraFrameRefresh);

		mButtonSwtich.setEnabled(false);
		mButtonStop.setEnabled(false);
		mButtonSendDTMF.setEnabled(false);
		mButtonSendOneIFrame.setEnabled(false);

		mSurfaceLocalView 	= (SurfaceView)findViewById(R.id.LocalView);
		mSurfaceRemoteView 	= (GLSurfaceView)findViewById(R.id.RemoteView);

		mTextViewDownQoS    = (TextView)findViewById(R.id.textViewDownQoS);

		surfaceTexture = new SurfaceTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);

		mSurfaceLocalView.getHolder().setFixedSize(160, 120);
		VideoDeviceCallBack.SetCurActivity(this);

		// 设置来电监听
		// 1、取得TelephonyManagement对象
		TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		ArrayList<MediaCodecInfo> arrayList = VideoDecodeCallBack.GetSupportDecoders();
		for (MediaCodecInfo decoderInfo : arrayList) {
			String str = decoderInfo.getName().toUpperCase(Locale.getDefault());
			if(!(str.contains("GOOGLE"))){
				mStringDecodeTypeName = decoderInfo.getName();
				int[] colorFormats = decoderInfo.getCapabilitiesForType(mStringDecodeTypeName).colorFormats;
				mYUVType = colorFormats[0];
				mSupportMediaCodec = true;
				break;
			}
		}


		// 2、创建通话状态监听器
		PhoneStateListener listener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
					case TelephonyManager.CALL_STATE_IDLE:
						Toast.makeText(mContext, "callstate is idle", Toast.LENGTH_LONG).show();
						break;
					case TelephonyManager.CALL_STATE_OFFHOOK:
						Toast.makeText(mContext, "callstate is offhook", Toast.LENGTH_LONG).show();
						break;
					case TelephonyManager.CALL_STATE_RINGING:
						m_iDeviceType = 2;
						Toast.makeText(mContext, "callstate is ringring", Toast.LENGTH_LONG).show();
						break;

					default:
						break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}
		};

		//3、监听电话通话状态改变
		tManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);


		// 设置显示回调
		mSurfaceLocalView.getHolder().addCallback(new SurfaceHolder.Callback(){

			@Override
			public void surfaceCreated(SurfaceHolder holder) {

				// TODO Auto-generated method stub
				Log.i(ZIMETAG, "1. surfaceCreated , holder = " + holder);
				mSurfaceExist = true;
				//按home键时走这个分支恢复画面
				Log.e(ZIMETAG,"x");
				int eRet = ZIMEVideoClientJNI.ConnectDevice(mZIMEConfig.mChannelId, m_iDeviceType);
				String logString = "surfaceCreated---ConnectDevice Device:" + m_iDeviceType + "----ret: " + eRet;
				Toast.makeText(mContext, logString, Toast.LENGTH_LONG).show();
				m_iDeviceType = DEVTYPE_DEFAULT_VALUE;
			}

			private boolean mbHaveStart = false;
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				// TODO Auto-generated method stub
				Log.i(ZIMETAG, "2. video client surfaceChanged ,width = " + width + ", height = " + height);

				mZIMEConfig.mRemoteGLSurface = mSurfaceRemoteView;
				mZIMEConfig.mLocalSurfaceHolder  = holder;
				if(mbHaveStart == false)
				{
					Log.e(ZIMETAG,"y");
					mZIMEJniThread.start();
					mbHaveStart = true;
				}
			}
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				Log.i(ZIMETAG, "3. surfaceDestroyed ");
				mSurfaceExist = false;
			}

		});

		// 设置显示回调
		mSurfaceRemoteView.getHolder().addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				Log.i(ZIMETAG, "1. Remote surfaceCreated , holder = " + holder);
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
									   int width, int height) {
				// TODO Auto-generated method stub
				Log.i(ZIMETAG, "2. Remote surfaceChanged");
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				Log.i(ZIMETAG, "3. Remote Destroyed");
			}

		});


		mSurfaceLocalView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		ZIMEVideoClientJNI.ZIMELoadLibrary();
		Log.e(ZIMETAG,"1");
		mVideoClientJNI = new ZIMEVideoClientJNI();
		mAudioClientJNI = new ZIMEClientJni();
		Log.e(ZIMETAG,"2");
		mZIMEConfig = new ZIMEConfig();
		Log.e(ZIMETAG,"3");

		mDiaglogBuilder 	= new Builder(mContext);
		mDiaglogBuilder.SetZIMESDKClient(mVideoClientJNI, mAudioClientJNI, mZIMEConfig);
		mDialogSetting = mDiaglogBuilder.create(/*mViewHandler*/);
		mDialogSetting.setAudioManager(am);
		Log.e(ZIMETAG,"4");

		mDialogDTMFDialerBuilder = new DTMFDialerBuilder(mContext);
		mDialogDTMFDialerBuilder.SetZIMESDKClient(mVideoClientJNI, mAudioClientJNI, mZIMEConfig);
		mDialogDTMFDialer  = mDialogDTMFDialerBuilder.create();
		Log.e(ZIMETAG,"5");

		mZIMEJniThread = new ZIMEJniThread(mVideoClientJNI, mAudioClientJNI);
		Log.e(ZIMETAG,"6");
		mZIMEJniThread.SetActivity(ZIMEAVDemoActivity.this);
		VideoDeviceCallBack.SetCurActivity(this);
		Log.e(ZIMETAG,"7");
		mZIMEJniThread.setAudioMan(am);

		// opengl 
		mVideoGLRender = new ZMCEVideoGLRender();
		mVideoGLRender.SetGLSurface(mSurfaceRemoteView);
		Log.e(ZIMETAG,"8");

		//start AV
		mButtonStart.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Log.i(ZIMETAG, "-------------Start AV Button--------------");
				mZIMEJniThread.Input(ZIMEConfig.SET_PARAM, mZIMEConfig);
				Log.e(ZIMETAG,"9");
				mZIMEJniThread.Input(ZIMEConfig.START, null);
				Log.e(ZIMETAG,"10");
				//mVResWidth_S = mZIMEConfig.mWidth;

				VideoDeviceCallBack.SetCodecType(ZIMEConfig.mCodecType);

				if(ZIMEConfig.mCodecType == ZIMEConfig.enumZIME_AMLOGICHARDWEAR)
				{
					Log.e(ZIMETAG,"11");
					mVideoGLRender.useMediaCodecInfo(false, 0);

					mVideoGLRender.setAmlogicEnable(true);
				}
				else if(ZIMEConfig.mCodecType == ZIMEConfig.enumZIME_MediaCodec)
				{
					mVideoGLRender.useMediaCodecInfo(true, mYUVType);

					mVideoGLRender.setAmlogicEnable(false);
				}
				else
				{
					mVideoGLRender.useMediaCodecInfo(false, 0);
					mVideoGLRender.setAmlogicEnable(false);
				}

				mButtonStart.setEnabled(false);

				mButtonStop.setEnabled(true);
				mButtonStartSend.setEnabled(false);
				mButtonStartRecv.setEnabled(false);
				mButtonSendDTMF.setEnabled(true);
				mButtonAstart.setEnabled(false);

				mDialogSetting.enableAudio(false);
				mDialogSetting.enableExTrans(false);
				mDialogSetting.enableSync(false);
				mDialogSetting.enableSaveH26X(false);

				if(ZIMEConfig.mIsOnlyAudio)
				{
					mButtonSwtich.setEnabled(false);
					mButtonSendOneIFrame.setEnabled(false);
				}
				else
				{
					mButtonSwtich.setEnabled(true);
					mButtonSwtich.setText("SwitchA");
					mButtonSendOneIFrame.setEnabled(true);
				}

				mStarted = true;
				mStopped = false;
				mDialogSetting.setStatus(mStarted, mStopped);

			}
		});

		//start audio
		mButtonAstart.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Log.i(ZIMETAG, "-------------Start Astart Button--------------");
				mZIMEJniThread.Input(ZIMEConfig.SET_AUDIOPARAM_BEFORSTART, mZIMEConfig);
				mZIMEJniThread.Input(ZIMEConfig.ASTART_BY_AVCLIENTINTERFACE, null);


				mButtonStart.setEnabled(false);
				mButtonStop.setEnabled(true);
				mButtonStartSend.setEnabled(false);
				mButtonStartRecv.setEnabled(false);
				mButtonSendDTMF.setEnabled(true);
				mButtonAstart.setEnabled(false);
				mButtonSwtich.setEnabled(true);
				mButtonSwtich.setText("SwitchAV");

				mDialogSetting.enableExTrans(false);
				mDialogSetting.enableAudio(false);
				mDialogSetting.enableSync(true);

				mStarted = true;
				mStopped = false;
				mDialogSetting.setStatus(mStarted, mStopped);

			}
		});

		//switch
		mButtonSwtich.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String ButtonSwtichName = mButtonSwtich.getText().toString();
				if (ButtonSwtichName.equals("SwitchAV"))
				{
					Log.i(ZIMETAG, "-------------Switch Audio and Video--------------");
					mZIMEJniThread.Input(ZIMEConfig.SET_VIDEOPARAM, mZIMEConfig);
					mZIMEJniThread.Input(ZIMEConfig.TOAV, null);
					mButtonSwtich.setText("SwitchA");
					mButtonSendOneIFrame.setEnabled(true);
					mDialogSetting.enableSync(false);
				}
				else
				{
					Log.i(ZIMETAG, "-------------Switch Audio --------------");
					mZIMEJniThread.Input(ZIMEConfig.TOA, null);
					mButtonSwtich.setText("SwitchAV");
					mButtonSendOneIFrame.setEnabled(false);
					mDialogSetting.enableSync(true);
				}

				mButtonStart.setEnabled(false);
				mButtonStop.setEnabled(true);
				mButtonStartSend.setEnabled(false);
				mButtonStartRecv.setEnabled(false);
				mButtonSendDTMF.setEnabled(true);
				mButtonAstart.setEnabled(false);
				mButtonSwtich.setEnabled(true);

				mDialogSetting.enableExTrans(false);
				mDialogSetting.enableAudio(false);

			}
		});

		//stop
		mButtonStop.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i(ZIMETAG, "-------------Stop Button--------------");


				if(mStarted)
				{
					mZIMEJniThread.Input(ZIMEConfig.STOP, null);
				}
				m_bSetParam = false;
				mButtonStart.setEnabled(true);
				mButtonStop.setEnabled(false);
				mButtonStartSend.setEnabled(true);
				mButtonStartRecv.setEnabled(true);
				mButtonSwtich.setEnabled(false);
				mButtonSwtich.setText("Switch");
				mButtonSendDTMF.setEnabled(false);
				mButtonSendOneIFrame.setEnabled(false);

				mDialogSetting.enableExTrans(true);
				mDialogSetting.enableAudio(true);
				mDialogSetting.enableSync(true);

				if(!ZIMEConfig.mSaveH26XFile)
				{
					mDialogSetting.enableSaveH26X(true);
				}

				if(!ZIMEConfig.mIsOnlyAudio)
				{
					mButtonAstart.setEnabled(true);
				}

				mStarted = false;
				mStopped = true;
				mDialogSetting.setStatus(mStarted, mStopped);

				resetTextViewDownQoS();

			}
		});

		// Config
		mButtonConf.setOnClickListener(new OnClickListener(){
			public void onClick(View arg)
			{
				Log.i(ZIMETAG, "-------------Config Button--------------1");
				mDialogSetting.show();
			}
		});

		// Exit
		mButtonExit.setOnClickListener(new OnClickListener(){
			public void onClick(View arg)
			{
				Log.i(ZIMETAG, "-------------Exit Button--------------1");

				mZIMEJniThread.Input(ZIMEConfig.EXIT, null);

				ToastUtil.cancelToast();

				mStarted = false;
				mDialogSetting.setStatus(mStarted, mStopped);

				Log.i(ZIMETAG, "-------------Exit Button--------------3");

				System.exit(0);

			}
		});

		//StartSend
		mButtonStartSend.setOnClickListener(new OnClickListener(){
			public void onClick(View arg)
			{
				Log.i(ZIMETAG, "-------------Start Send Button--------------");
				if(!m_bSetParam)
				{
					mZIMEJniThread.Input(ZIMEConfig.SET_PARAM, mZIMEConfig);
					m_bSetParam = true;
				}
				mZIMEJniThread.Input(ZIMEConfig.STARTSEND, null);
				VideoDeviceCallBack.SetCodecType(mZIMEConfig.mCodecType);

				mButtonStart.setEnabled(false);
				mButtonStartSend.setEnabled(false);
				mButtonStop.setEnabled(true);
				mButtonSendDTMF.setEnabled(true);
				mButtonSendOneIFrame.setEnabled(true);
				mButtonAstart.setEnabled(false);
				mDialogSetting.enableExTrans(true);
				mDialogSetting.enableAudio(false);

				if(mButtonStartRecv.isEnabled() == false)
				{
					mButtonSwtich.setEnabled(true);
				}

				mStarted = true;
				mStopped = false;
				mDialogSetting.setStatus(mStarted, mStopped);

			}
		});

		//StartRecv
		mButtonStartRecv.setOnClickListener(new OnClickListener(){
			public void onClick(View arg)
			{
				Log.i(ZIMETAG, "-------------Start Recv Button--------------");
				if(!m_bSetParam)
				{
					mZIMEJniThread.Input(ZIMEConfig.SET_PARAM, mZIMEConfig);
					m_bSetParam = true;
				}
				mZIMEJniThread.Input(ZIMEConfig.STARTRECV, null);
				if(mZIMEConfig.mCodecType == ZIMEConfig.enumZIME_AMLOGICHARDWEAR)
				{
					mVideoGLRender.setAmlogicEnable(true);
				}
				else
				{
					mVideoGLRender.setAmlogicEnable(false);
				}

				mButtonStart.setEnabled(false);
				mButtonStartRecv.setEnabled(false);
				mButtonStop.setEnabled(true);
				mButtonAstart.setEnabled(false);
				mDialogSetting.enableExTrans(true);
				mDialogSetting.enableAudio(false);
				mDialogSetting.enableSync(false);
				mDialogSetting.enableSaveH26X(false);
				if(mButtonStartSend.isEnabled() == false)
				{
					mButtonSwtich.setEnabled(true);
				}

				mStarted = true;
				mStopped = false;
				mDialogSetting.setStatus(mStarted, mStopped);

			}
		});


		mButtonSendOneIFrame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int ret = ZIMEVideoClientJNI.ConfigVideoIntraFrameRefresh(mZIMEConfig.mChannelId);
				if (ret != 0) {
					Toast.makeText(ZIMEAVDemoActivity.this, "send one I Frame failed", Toast.LENGTH_LONG).show();
				}
			}
		});


		//SendDTMF
		mButtonSendDTMF.setOnClickListener(new OnClickListener(){
			public void onClick(View arg)
			{
				Log.i(ZIMETAG, "-------------Start SendDTMF Button--------------");
				mDialogDTMFDialer.show();

			}
		});


		mHandler = new Handler() {
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
					case 1:
						Toast.makeText(ZIMEAVDemoActivity.this, mStrResSwitchInfo, Toast.LENGTH_LONG).show();
						break;
				}
				super.handleMessage(msg);
			}
		};


	}

	protected void resetTextViewDownQoS() {

		String DownLinkQos = "";
		if(!ZIMEConfig.mIsOnlyAudio)
		{

			DownLinkQos = "DownQoS" + "\n" +
					"Jitter A/V:" +  -1 + "/" + -1 + "\n" +
					"Lost A/V:" + -1 +"/" + -1 + "\n" +
					"RTT A/V:" +  -1 +"/" + -1 + "\n" +
					"Audio \n" +
					"kbps S/R:" + -1 + "/" +  -1 + "\n" +
					"Video \n" +
					"FPS C/S/R/D:" + -1 + "/" + -1 + "/" + -1 + "/" +  -1 + "\n" +
					"Res S/R:" + -1 + "/" +  -1 + "\n" +
					"Trigger S/R:" + -1 + "/" +  -1 + "\n" +
					"Total kbps S/R:" + -1 + "/" +  -1 + "\n" +
					"Encode kbps S:" + -1  + "\n" +
					"ES kbps S/R:" + -1 + "/" +  -1 + "\n" +
					"Red kbps S/R:" + -1 + "/" +  -1 + "\n" +
					"Network Lost:" + -1 + "%" + "\n" +
					"Real    Lost:" + -1 + "%" + "\n" ;
		}
		else {


			DownLinkQos = "DownQoS" + "\n" +
					"Jitter :" +  -1  + "\n" +
					"Lost :" + -1  + "\n" +
					"RTT :" +  -1  + "\n" +
					"kbps S/R:" + -1 + "/" +  -1 + "\n" ;
		}

		mTextViewDownQoS.setTextColor(android.graphics.Color.RED);
		mTextViewDownQoS.setText(DownLinkQos);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下键盘上返回按钮
		Log.e(ZIMETAG, "KeyCode=" + keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_HOME) {
			new AlertDialog.Builder(this)
					.setTitle("Message Box")
					.setMessage("是否退出?")
					.setNegativeButton("否",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
								}
							})

					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									if(mStarted)
									{
										Log.i(ZIMETAG, "-------------Exit Button--------------2");
										mZIMEJniThread.Input(ZIMEConfig.EXIT, null);

										mStarted = false;
										mDialogSetting.setStatus(mStarted, mStopped);
									}

									System.exit(0);

								}
							}).show();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

}


