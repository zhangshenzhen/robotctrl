package zime.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.opengl.GLES11Ext;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import zime.media.VideoDeviceCallBack;
import zime.media.ZIMEClientJni;
import zime.media.ZIMEVideoClientJNI;
import zime.media.ZMCEVideoGLRender;

/**
 * Created by li on 2016/10/10.
 */
public class ZIMEAVDemoService extends Service {
    private final static String ZIMETAG = "ZIMEAVDemoService";

    public static SurfaceTexture surfaceTexture;
    private Context mContext = null;
    private ZIMEDialogSetting.Builder mDiaglogBuilder = null;
    private ZIMEDialogSetting mDialogSetting = null;
    private ZIMEDialogDTMFDialer.DTMFDialerBuilder mDialogDTMFDialerBuilder= null;
    private ZIMEDialogDTMFDialer mDialogDTMFDialer = null;

    private ZIMEClientJni mAudioClientJNI = null;
    private ZIMEVideoClientJNI mVideoClientJNI = null;
    private ZIMEConfig mZIMEConfig = null;
    public ZIMEJniThread mZIMEJniThread = null;
    private static boolean mStarted = false;
    private static boolean mStopped = true;

    private static int DEVTYPE_DEFAULT_VALUE = 2;
    private static int m_iDeviceType = DEVTYPE_DEFAULT_VALUE;  // 默认home行为

    // opengl
    private ZMCEVideoGLRender mVideoGLRender = null;
    private AudioManager am = null;
    public  int  mYUVType = 19;

    public static int proId;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(ZIMETAG,"onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(ZIMETAG,"onStartCommand");
      new Thread(new Runnable() {
            @Override
            public void run() {
                String[] str = new String[]{"edge -a 192.168.100.8 -c test -k 123456 -l 222.190.128.98:8080 &","sleep 1",
                        "busybox ip route delete 192.168.100.0/24","busybox ip route add 192.168.100.0/24 via 192.168.100.8 dev edge0 table local"};
                CommandExecution.execCommand(str,true);
                /*String[] str = new String[]{"edge -a 192.168.100.34 -c test -k 123456 -l 222.190.128.98:8080 &","sleep 1",
                        "busybox ip route delete 192.168.100.0/24","busybox ip route add 192.168.100.0/24 via 192.168.100.34 dev edge0 table local"};
                CommandExecution.execCommand(str,true);*/
            }
        }).start();


        mContext = this;
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        surfaceTexture = new SurfaceTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);

        ZIMEVideoClientJNI.ZIMELoadLibrary();
        mVideoClientJNI = new ZIMEVideoClientJNI();
        mVideoClientJNI = new ZIMEVideoClientJNI();
        mAudioClientJNI = new ZIMEClientJni();
        mZIMEConfig = new ZIMEConfig();
        Log.i(ZIMETAG, "-------------Start AV Button------------1--");

        mDiaglogBuilder = new ZIMEDialogSetting.Builder(mContext);
        mDiaglogBuilder.SetZIMESDKClient(mVideoClientJNI, mAudioClientJNI, mZIMEConfig);
        mDialogSetting = mDiaglogBuilder.create(/*mViewHandler*/);
        mDialogSetting.setAudioManager(am);

        mDialogDTMFDialerBuilder = new ZIMEDialogDTMFDialer.DTMFDialerBuilder(mContext);
        mDialogDTMFDialerBuilder.SetZIMESDKClient(mVideoClientJNI, mAudioClientJNI, mZIMEConfig);
        mDialogDTMFDialer  = mDialogDTMFDialerBuilder.create();

        mZIMEJniThread = new ZIMEJniThread(mVideoClientJNI, mAudioClientJNI);
        Log.i(ZIMETAG, "-------------Start AV Button------------2--");

        // opengl
        mVideoGLRender = new ZMCEVideoGLRender();
        int eRet = ZIMEVideoClientJNI.ConnectDevice(mZIMEConfig.mChannelId, m_iDeviceType);
        String logString = "surfaceCreated---ConnectDevice Device:" + m_iDeviceType + "----ret: " + eRet;
        Toast.makeText(mContext, logString, Toast.LENGTH_LONG).show();
        m_iDeviceType = DEVTYPE_DEFAULT_VALUE;
        Log.i(ZIMETAG, "-------------Start AV Button------------3--");
        mZIMEJniThread.setAudioMan(am);
        mZIMEJniThread.start();
        SystemClock.sleep(80);//保证线程开启后在执行下面的代码;
        Log.i(ZIMETAG, "-------------Start AV Button------------4--");
        mZIMEJniThread.Input(ZIMEConfig.SET_PARAM, mZIMEConfig);
        mZIMEJniThread.Input(ZIMEConfig.START, null);

        VideoDeviceCallBack.SetCodecType(ZIMEConfig.mCodecType);
        Log.i(ZIMETAG, "-------------Start AV Button------------5--");
        if(ZIMEConfig.mCodecType == ZIMEConfig.enumZIME_AMLOGICHARDWEAR)
        {
            mVideoGLRender.useMediaCodecInfo(false, 0);
        }
        else if(ZIMEConfig.mCodecType == ZIMEConfig.enumZIME_MediaCodec)
        {
            mVideoGLRender.useMediaCodecInfo(true, mYUVType);
        }
        else
        {
            mVideoGLRender.useMediaCodecInfo(false, 0);
        }

        mStarted = true;
        mStopped = false;
        mDialogSetting.setStatus(mStarted, mStopped);

        Log.i(ZIMETAG, "-------------Start AV Button------------6--");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        Log.i(ZIMETAG, "-------------Exit Button--------------1");

        mZIMEJniThread.Input(ZIMEConfig.EXIT, null);

        ToastUtil.cancelToast();

        mStarted = false;
        mDialogSetting.setStatus(mStarted, mStopped);

        Log.i(ZIMETAG, "-------------Exit Button--------------3");
        Log.d(ZIMETAG,"service destroy");
        super.onDestroy();
    }

}
