package zime.media;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

import zime.ui.ZIMEAVDemoService;
import zime.ui.ZIMEConfig;

public class VideoDeviceCallBack {
	private static final String TAG = VideoDeviceCallBack.class
			.getCanonicalName();
	private static boolean bOnlyAudio = false;
	private static int sSensorOrientation;

	private static ZMCEVideoGLRender mRender;
	private static int s_Degree = 0;
	private static int s_CurOrientation = -1;
	private static Activity s_CurActivity = null;


	private Camera mCamera = null;
	private int mCameraNum = 0;
	private Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
	private byte[] mRawData;
	private byte[] mFrameYUV;
	private int mFrameLen;
	private boolean mCapReady = false;

	private SurfaceHolder mSurfaceHolder = null;
	private SurfaceTexture surfaceTexture =null;
	private static int mPlayWidth = 0;
	private int mPlayHeight = 0;
	private GLSurfaceView mRemote = null;
	private int mRemoteDisplayEnable = -1;
	private boolean mUseMetaMode = false;  // AML brian
	private int mMetaDataLen = 0; // AML brian
	private String KeyPreviewCallBackInMetaData = "preview-callback-in-metadata-enable";
	private String KeyPreviewCallBackInMetaDataLenth = "preview-callback-in-metadata-length";
	Object mDecodecallback = null;
	Matrix mMatrix = null;
	Paint mPaint = null;

	private static int mCodecType = ZIMEConfig.enumZIME_GOTAEXTERNENCODER;

	public static int GetRecvWidth()
	{
		return mPlayWidth;
	}
	public static void SetCodecType(int iCodecType)
	{
		mCodecType = iCodecType;
	}

	public static void SetRender(ZMCEVideoGLRender iBuffer) {
		mRender = iBuffer;
	}

	public static int GetNumOfVideoDevices() {
		int cameraNum = Camera.getNumberOfCameras();
		Log.d(TAG, "Video GetNumOfVideoDevices, Num = " + cameraNum);
		return cameraNum;
	}

	public static void DoAudioTalk(boolean iOnlyAudio) {
		bOnlyAudio = iOnlyAudio;
		Log.e(TAG, "Video DoAudioTalk:" + bOnlyAudio);
	}

	public static void SetCurActivity(Activity curActivity) {
		s_CurActivity = curActivity;
		s_CurOrientation = curActivity.getRequestedOrientation();

		OrientationEventListener orientationEventListener = new OrientationEventListener(s_CurActivity, SensorManager.SENSOR_DELAY_NORMAL) {

			@Override
			public void onOrientationChanged(int orientation) {
				// TODO Auto-generated method stub
				synchronized (this) {
					if ((orientation <= 45 && orientation >=0) || (orientation <= 360 && orientation > 315)) {
						sSensorOrientation = 0;
					}
					else if ((orientation <= 135 && orientation > 45)) {
						sSensorOrientation = 90;
					}
					else if((orientation <= 225 && orientation > 135)){
						sSensorOrientation = 180;
					}
					else if ((orientation <= 315 && orientation > 225)) {
						sSensorOrientation = 270;
					}

					VideoDeviceCallBack.s_Degree = getSurfaceRotation(s_CurActivity)
							+ sSensorOrientation;
				}
			}};
		if (orientationEventListener.canDetectOrientation()) {
			orientationEventListener.enable();
		}

	}

	public int ProducerOpen(int i_nCameraId) {
		if (bOnlyAudio) {
			Log.d(TAG, "Video ProducerOpen only audio");
			return 0;
		}
		Log.d(TAG, "Video ProducerOpen enter");

		this.mCamera = openCamera(i_nCameraId);
		if (this.mCamera == null) {
			return -1;
		}
		Camera.getCameraInfo(i_nCameraId, this.mCameraInfo);

		/* 3.获取分辨率,显示在屏幕中
		 *
		 * 抛出的异常：
		 * 1.Method called after release()
		 * 2.getParameters failed (empty parameters)
		 */
		Camera.Parameters params = null;
		try {
			params = this.mCamera.getParameters();
		} catch (Exception e) {
			Log.e(TAG, "mCamera getParameters failed,Reason:" + e.toString());
			this.mCamera.release();
			this.mCamera = null;
			return -1;
		}

		logCameraParams(params);
		Log.d(TAG, "Video ProducerOpen succeed.");
		return 0;
	}

	private Camera openCamera(int i_nCameraId) {
		if (!validCameraId(i_nCameraId)) {
			return null;
		}

		/*打开摄像头
		 *
		 * Test:不调用release，open捕获异常 抛出的异常：
		 * 1. Fail to connect to camera service
		 * 2. Camera initialization failed
		 * 3. Can't find android/hardware/Camera
		 */
		Camera camera = null;
		try {
			camera = Camera.open(i_nCameraId);
		} catch (Exception e) {
			Log.e(TAG, "Can't open Camera[" + i_nCameraId + "]," + "Reason:" + e.toString());
			return null;
		}

		return camera;
	}

	private boolean validCameraId(int i_nCameraId) {
		if (mCameraNum <= 0) {
			mCameraNum = GetNumOfVideoDevices();
		}
		if (mCameraNum <= i_nCameraId) {
			Log.d(TAG, "CameraID [" + i_nCameraId + "] is wrong.ProducerOpen failed!");
			return false;
		}
		return true;
	}

	private void logCameraParams(Camera.Parameters params) {
		for (Size size : params.getSupportedPreviewSizes()) {
			Log.i(TAG, "VideoProducer open-------width:" + size.width
					+ ",height:" + size.height);
		}

		int pictureformat = params.getPictureFormat();
		int previewformat = params.getPreviewFormat();
		Log.i(TAG, "pictureformat:" + pictureformat + ",previewformat"
				+ previewformat);
	}

	public int ProducerClose() {
		if (bOnlyAudio) {
			Log.d(TAG, "Video ProducerClose only audio");
			return 0;
		}
		Log.d(TAG, "Video ProducerClose enter.");

		synchronized (this) {
			if (this.mCamera != null) {
				this.mCamera.release();
				this.mCamera = null;
			}
		}
		this.mRawData = null;
		this.mFrameYUV = null;
		this.mCameraNum = 0;
		Log.d(TAG, "Video ProducerClose succeed.");
		return 0;
	}

	private void setPreview() throws IOException {
		Log.i(TAG, "1. sdk " + Build.VERSION.SDK_INT);

		this.mCamera.setPreviewDisplay(this.mSurfaceHolder);
	}

	public int ProducerStart(byte[] i_Rawbuf, int i_nWidth, int i_nHeight, Object i_surfaceholder) {
		if (bOnlyAudio) {
			Log.d(TAG, "Video ProducerStart only audio");
			return 0;
		}
		Log.d(TAG, "Video ProducerStart enter.");
		mRawData = i_Rawbuf;

		this.surfaceTexture = ZIMEAVDemoService.surfaceTexture;
		this.mSurfaceHolder = ((SurfaceHolder) i_surfaceholder);
		//Log.d(TAG, "In Video ProducerStart, mSurfaceHolder = " + this.mSurfaceHolder + ", getSurface = " + this.mSurfaceHolder.getSurface());

		return startCamera(i_nWidth, i_nHeight);
	}

	private int startCamera(int i_nWidth, int i_nHeight) {
		synchronized (this) {
			if (this.mCamera != null) {
				if (s_CurActivity != null) {
					setCameraDisplayOrientation(s_CurActivity, this.mCameraInfo);
				}

				Camera.Parameters params = initCameraParameters(i_nWidth, i_nHeight, Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				if (null == params) {
					return -1;
				}

				int rawDataLen = params.getPreviewSize().width
						* params.getPreviewSize().height
						* ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
				if(mUseMetaMode == true)
					rawDataLen = mMetaDataLen;

				int result = setPreviewParameters(rawDataLen);
				if (result < 0) {
					return result;
				}

				try {
					this.mCamera.startPreview();
				} catch (Exception e) {
					Log.e(TAG, "mCamera.startPreview failed, Reason:" + e.toString());
					return -1;
				}
			}
		}

		Log.d(TAG, "Video ProducerStart succeed.");
		return 0;
	}

	private int setPreviewParameters(int rawDataLen) {
		this.mCamera.addCallbackBuffer(this.mRawData);
		this.mFrameYUV = new byte[rawDataLen];
		this.mFrameLen = rawDataLen;
		this.mCapReady = false;

		/*try {
			setPreview();
		} catch (Exception e) {
			Log.e(TAG, "mCamera.setPreviewDisplay failed, Reason:" + e);
			return -1;
		}*/
		try {
			mCamera.setPreviewTexture(surfaceTexture);
		}catch (Exception e) {
			Log.e(TAG, "mCamera.setPreviewTexture failed, Reason:" + e);
			return -1;
		}

		this.mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
			public void onPreviewFrame(byte[] data, Camera camera) {
				synchronized (this) {
					if ((VideoDeviceCallBack.this.mFrameYUV != null)
							&& (data != null)
							&& (data.length > 0)) {
                        //Log.e("12","12");
						VideoDeviceCallBack.this.mCapReady = true;
					}
				}
			}
		});
		return 0;
	}

	private Parameters initCameraParameters(int i_nWidth, int i_nHeight, String focusModeContinuousVideo) {
		Camera.Parameters params = null;
		String metadata = null;
		try {
			params = this.mCamera.getParameters();
		} catch (Exception e) {
			Log.e(TAG, "mCamera getParameters failed,Reason:" + e.toString());
			return null;
		}
		metadata = params.get(KeyPreviewCallBackInMetaData);
		if(metadata != null) {
			if(mCodecType == ZIMEConfig.enumZIME_AMLOGICHARDWEAR)
			{
				params.set(KeyPreviewCallBackInMetaData, 1);
				Log.d(TAG, "mCamera set preview in meta data mode :" + params.get(KeyPreviewCallBackInMetaData));
				mMetaDataLen = params.getInt(KeyPreviewCallBackInMetaDataLenth);
				Log.d(TAG, "mCamera meta data length :" + mMetaDataLen);
				mUseMetaMode = true;
			}
			else
			{
				params.set(KeyPreviewCallBackInMetaData, 0);
				mUseMetaMode = false;
			}
		}
		else
		{
			mMetaDataLen = 0;
			mUseMetaMode = false;

		}

		setupParameters(i_nWidth, i_nHeight, focusModeContinuousVideo, params);
		try {
			this.mCamera.setParameters(params);
		} catch (Exception e) {
			Log.e(TAG, "mCamera setParameters failed,Reason:" + e.toString());
			return null;
		}

		Camera.Parameters actualParams = null;
		try {
			actualParams = this.mCamera.getParameters();
		} catch (Exception e) {
			Log.e(TAG, "mCamera getParameters failed,Reason:" + e.toString());
			return null;
		}
		compareParams(actualParams, params);

		return actualParams;
	}

	private void compareParams(Parameters actualParams, Camera.Parameters params) {
		if ((actualParams.getPreviewSize().width != params.getPreviewSize().width)
				|| (actualParams.getPreviewSize().height != params.getPreviewSize().height)) {
			Log.e(TAG, "warning:ProducerStart userd set size:"
					+ params.getPreviewSize().width + "x" + params.getPreviewSize().height
					+ ",camera not supported and changed to:"
					+ actualParams.getPreviewSize().width + "x"
					+ actualParams.getPreviewSize().height);
		}
	}
	private void setupParameters(int i_nWidth, int i_nHeight,
								 String focusModeContinuousVideo, Camera.Parameters params) {
		params.setPreviewSize(i_nWidth, i_nHeight);
		Log.i(TAG, "ProducerStart:used set width=" + params.getPreviewSize().width + ";height=" + params.getPreviewSize().height);

		List<Integer> formatModes = params.getSupportedPreviewFormats();
		for(Integer i=0; i<formatModes.size(); i++)
		{
			Log.i(TAG, "Get the camera support PreviewFormats:" + formatModes.get(i));
		}
		Log.i(TAG, "android.graphics.ImageFormat.YV12=" + android.graphics.ImageFormat.YV12 + ",android.graphics.ImageFormat.NV21=" + android.graphics.ImageFormat.NV21);

		if (mCodecType != ZIMEConfig.enumZIME_AMLOGICHARDWEAR)
		{
			params.setPreviewFormat( android.graphics.ImageFormat.YV12);
		}

		//	params.setPreviewFpsRange(20000, 30000);  //AML使用会采集失败

		List<String> focusModes = params.getSupportedFocusModes();
		if (focusModes.contains(focusModeContinuousVideo)) {
			Log.i(TAG, "setFocusMode=continuous-video");
			params.setFocusMode(focusModeContinuousVideo);
		}
	}

	public void setCameraDisplayOrientation(Activity activity, Camera.CameraInfo cameraInfo) {
		int surfaceRotation = getSurfaceRotation(activity);
		int localPreviewOrientation = 0;
		if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
			localPreviewOrientation = (cameraInfo.orientation + surfaceRotation) % 360;
			localPreviewOrientation = (360 - localPreviewOrientation) % 360;
		} else {
			localPreviewOrientation = (cameraInfo.orientation - surfaceRotation + 360) % 360;
		}

		Log.i(TAG, "camera orientation=" + cameraInfo.orientation + "; surface rotation="
				+ surfaceRotation);

		this.mCamera.setDisplayOrientation(localPreviewOrientation);
	}

	private static int getSurfaceRotation(Activity activity) {
		int degrees = 0;
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
		}
		return degrees;
	}

	public int ProducerStop() {
		if (bOnlyAudio) {
			Log.d(TAG, "Video ProducerStop only audio");
			bOnlyAudio = false;
			return 0;
		}
		Log.d(TAG, "Video ProducerStop enter.");

		synchronized (this) {
			if (this.mCamera != null) {
				this.mCamera.stopPreview();
				this.mCamera.setPreviewCallbackWithBuffer(null);
				this.mCapReady = false;
			}
		}

		this.mRawData = null;
		this.mFrameYUV = null;

		Log.d(TAG, "Video ProducerStop succeed.");
		return 0;
	}

	public int GetFrame(byte[] i_pFrameBuf, int i_iBufLen) {
		if (bOnlyAudio) {
			return 0;
		}

		if (this.mFrameLen > i_iBufLen) {
			Log.e(TAG, "video GetFrame buffer too small : " + i_iBufLen);
			return -1;
		}
		synchronized (this) {
			if (this.mCamera == null) {
				Log.e(TAG, "Camera not Open, GetFrame Failed. ");
				return -1;
			}

			if (!this.mCapReady) {
				return -1;
			}

			this.mCapReady = false;

			if (s_CurOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR) {
				this.setCameraDisplayOrientation(s_CurActivity, this.mCameraInfo);
			}
		}

		return this.mFrameLen;
	}

	public int AddBufferCallback()
	{
		this.mCamera.addCallbackBuffer(this.mRawData);
		return 0;
	}

	public int GetLocalDegree() {
		int Degree = 0;
		synchronized (this) {
			Degree = s_Degree;
		}
		return Degree;
	}

	public int GetDegree() {
		int Degree = 0;
		synchronized (this) {
			if (this.mCameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
				Degree = (360 - (mCameraInfo.orientation - VideoDeviceCallBack.sSensorOrientation) + 360) % 360;
			}
			else {
				Degree = (this.mCameraInfo.orientation + VideoDeviceCallBack.sSensorOrientation + 360) % 360;
				Degree = 0 - Degree < 0 ? (360 - Degree + 360) % 360 : 0;
			}
		}
		return Degree;
	}

	public int ConsumerOpen() {
		if (bOnlyAudio) {
			Log.d(TAG, "Video ConsumerOpen only audio");
			return 0;
		}

		Log.d(TAG, "Video ConsumerOpen succeed.");
		return 0;
	}

	public int ConsumerStart(int i_iWidth, int i_iHeight, Object Surfaceholder) {
		if (bOnlyAudio) {
			Log.d(TAG, "Video ConsumerStart only audio");
			return 0;
		}
		Log.d(TAG, "Video ConsumerStart enter");

		this.mPlayWidth = i_iWidth;
		this.mPlayHeight = i_iHeight;
		Log.i(TAG, "ConsumerStart:width=" + this.mPlayWidth + ";height=" + this.mPlayHeight);

		this.mRemote = ((GLSurfaceView) Surfaceholder);
		Log.d(TAG, "Video ConsumerStart succeed.");
		return 0;
	}

	public int ConsumerStop() {
		if (bOnlyAudio) {
			Log.d(TAG, "Video ConsumerStop only audio");
			bOnlyAudio = false;
			return 0;
		}
		Log.d(TAG, "Video ConsumerStop enter");

		if (this.mMatrix != null) {
			this.mMatrix = null;
		}
		if (this.mPaint != null) {
			this.mPaint = null;
		}


		Log.d(TAG, "Video ConsumerStop succeed");
		return 0;
	}

	public int ConsumerClose() {
		if (bOnlyAudio) {
			Log.d(TAG, "Video ConsumerClose only audio");
			return 0;
		}
		Log.d(TAG, "Video ConsumerClose enter.");
		this.mRemote = null;
		this.mDecodecallback = null;

		Log.d(TAG, "Video ConsumerClose succeed.");
		return 0;
	}

	public int SetRotateDegreeToRender(int i_iRemoteDegree) {
		int localRotateDegree = GetLocalDegree();

		if (this.mRemoteDisplayEnable == 0) {
			i_iRemoteDegree = 0;
		}
		mRender.SetRotateDegree(i_iRemoteDegree + localRotateDegree);
		return 0;
	}

	public int WriteFrame(byte[] i_pFrameBuf, int i_iBufLen, int i_iDegree, int i_iWidthStrideY, int i_iWidthStrideUV) {
		if (bOnlyAudio) {
			return 0;
		}


//		mRender.setBuffer(this.mPlayWidth, this.mPlayHeight, i_iWidthStrideY, i_iWidthStrideUV);
		try {
			mRender.CopyTheRenderData(i_pFrameBuf, i_iBufLen);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SetRotateDegreeToRender(i_iDegree);
//		this.mRemote.requestRender();

		return 0;
	}

	public int ConfigSurfaceView(int i_iwidth, int i_iHeight){

		return 0;
	}
}

