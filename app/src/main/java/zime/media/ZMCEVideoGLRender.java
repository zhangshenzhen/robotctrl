package zime.media;


import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ZMCEVideoGLRender implements GLSurfaceView.Renderer
{
	private static final String TAG = ZMCEVideoGLRender.class.getCanonicalName();
	private int mProgram;
	private int maPositionHandle;
	private int maTextureHandle;
	private int muSamplerYHandle;
	private int muSamplerUHandle;
	private int muSamplerVHandle;
	private int muSamplerUVHandle;
	private int mMVPMatrixHandle;
	private float[] mRotationMatrix = new float[16];
	private float mRotateDegree = 0;
	private GLSurfaceView mGlSurfaceView = null;
	private boolean mYUV420P = true;
	private boolean mSetupOpengl = false;
	private int initCnt = 0;
	private boolean mAmlogicEnable = false;
	private boolean mUserMediaCodec = false;
	private int mYUVType = 0;


	private int[] mTextureY = new int[1];
	private int[] mTextureU = new int[1];
	private int[] mTextureV = new int[1];
	private int[] mTextureUV = new int[1];

	@SuppressWarnings("unused")
	private Context mContext;

	private int mViewWidth = 320, mViewHeight = 240, mViewX = 0, mViewY = 0;

	int mBufferWidthY = 320, mBufferHeightY = 240,  mBufferWidthUV = 160, mBufferHeightUV = 120;
	ByteBuffer mBuffer = null;
	int mBufferPositionY, mBufferPositionU, mBufferPositionV,mBufferPositionUV;
	private FloatBuffer mTriangleVertices;
	private ShortBuffer mIndices;
	private int mWidthStrideY, mWidthStrideUV;

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int SHORT_SIZE_BYTES = 2;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
	private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
	private static final float[] TRIANFLE_VERTICES_DATA = {
			1, -1, 0, 1, 1,
			1, 1, 0, 1, 0,
			-1, 1, 0, 0, 0,
			-1, -1, 0, 0, 1
	};
	private static final short[] INDICES_DATA = {
			0, 1, 2,
			2, 3, 0};

	private FloatBuffer mVertexArray;
	private FloatBuffer mCoordsArray;

	private static  float[] vertexPositions = {
			1.0f, -1.0f, 0.0f,
			1.0f, 1.0f, 0.0f,
			-1.0f,  1.0f, 0.0f,
			-1.0f,  -1.0f, 0.0f
	};

	private static  float[] textureCoords = {
			1.0f, 1.0f,
			1.0f, 0.0f,
			0.0f, 0.0f,
			0.0f, 1.0f
	};

	private static final String VERTEX_SHADER_SOURCE =
			"uniform mat4 u_MVPMatrix;\n"+
					"attribute vec4 aPosition;\n" +
					"attribute vec2 aTextureCoord;\n" +
					"varying vec2 vTextureCoord;\n" +
					"void main() {\n" +
					"  gl_Position = u_MVPMatrix*aPosition;\n" +
					"  vTextureCoord = aTextureCoord;\n" +
					"}\n";

	private static final String FRAGMENT_SHADER_SOURCE_420P = "precision mediump float;" +
			"varying vec2 vTextureCoord;" +
			"" +
			"uniform sampler2D SamplerY; " +
			"uniform sampler2D SamplerU;" +
			"uniform sampler2D SamplerV;" +
			"" +
			"const mat3 yuv2rgb = mat3(1, 0, 1.2802,1, -0.214821, -0.380589,1, 2.127982, 0);" +
			"" +
			"void main() {    " +
			"    vec3 yuv = vec3(1.1643 * (texture2D(SamplerY, vTextureCoord).r - 0.0625)," +
			"                    texture2D(SamplerU, vTextureCoord).r - 0.5," +
			"                    texture2D(SamplerV, vTextureCoord).r - 0.5);" +
			"    vec3 rgb = yuv * yuv2rgb;    " +
			"    gl_FragColor = vec4(rgb, 1.0);" +
			"} ";

	private static final String FRAGMENT_SHADER_SOURCE_420SP = "precision mediump float;" +
			"varying vec2 vTextureCoord;" +
			"" +
			"uniform sampler2D SamplerY; " +
			"uniform sampler2D SamplerUV;" +
			"" +
			"const mat3 yuv2rgb = mat3(1, 0, 1.2802,1, -0.214821, -0.380589,1, 2.127982, 0);" +
			"" +
			"void main() {    " +
			"    vec3 yuv = vec3(1.1643 * (texture2D(SamplerY, vTextureCoord).r - 0.0625)," +
			"                    texture2D(SamplerUV, vTextureCoord).a - 0.5," +
			"                    texture2D(SamplerUV, vTextureCoord).r - 0.5);" +
			"    vec3 rgb = yuv * yuv2rgb;    " +
			"    gl_FragColor = vec4(rgb, 1.0);" +
			"} ";

	public ZMCEVideoGLRender()
	{
		mTriangleVertices = ByteBuffer.allocateDirect(TRIANFLE_VERTICES_DATA.length
				* FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangleVertices.put(TRIANFLE_VERTICES_DATA).position(0);

		mVertexArray = ByteBuffer.allocateDirect(vertexPositions.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();

		mCoordsArray = ByteBuffer.allocateDirect(textureCoords.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();

		mIndices = ByteBuffer.allocateDirect(INDICES_DATA.length
				* SHORT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asShortBuffer();
		mIndices.put(INDICES_DATA).position(0);
		VideoDeviceCallBack.SetRender(this);
	}
	@SuppressWarnings("deprecation")
	public void SetGLSurface(GLSurfaceView i_glSurfaceView)
	{
		mGlSurfaceView = i_glSurfaceView;
		mGlSurfaceView.setEGLContextClientVersion(2);
		mGlSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

		mGlSurfaceView.setRenderer(this);
		mGlSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		mGlSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_GPU);
		mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	public void SetRotateDegree(float RotateDegree)
	{
		if((int)mRotateDegree != (RotateDegree))
		{
			mRotateDegree = RotateDegree;
			setViewport();
		}
	}

	public void SetWidthStride(int i_iWidthStriedeY, int i_iWidthStrideUV)
	{
		mWidthStrideY = i_iWidthStriedeY;
		mWidthStrideUV = i_iWidthStrideUV;
	}

	// 外部调用，并传入YUV的宽和高
	public void setBuffer(int bufferWidth, int bufferHeight, int ImgStrideY, int ImgStrideUV){
		synchronized (this){
			if(ImgStrideY == mWidthStrideY &&  ImgStrideUV == mWidthStrideUV)
			{
				return;
			}

			if(null != mBuffer)
			{
				mBuffer.clear();
				mBuffer = null;
			}

			mWidthStrideY = ImgStrideY;
			mWidthStrideUV = ImgStrideUV;

			mBufferWidthY = bufferWidth;
			mBufferHeightY = bufferHeight;

			mBufferWidthUV = (mBufferWidthY >> 1);
			mBufferHeightUV = (mBufferHeightY >> 1);

			mBufferPositionY = 0;
			mBufferPositionU = (mWidthStrideY * mBufferHeightY);
			mBufferPositionV = (mBufferPositionU + (mWidthStrideUV * mBufferHeightUV));
			mBufferPositionUV = (mWidthStrideY * mBufferHeightY);

			setViewport();	// 根据图像大小，重新计算比率
		}
	}

	public void CopyTheRenderData(byte[] i_pFrameBuf, int i_iBufLen)
	{
		synchronized (this) {

			if(null == mBuffer){
				mBuffer = ByteBuffer.allocateDirect(i_iBufLen);
			}

			mBuffer.clear();
			try {
				mBuffer.put(i_pFrameBuf, 0, i_iBufLen);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return;
		}
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub

		if(!mUserMediaCodec)
		{
			if( (mAmlogicEnable ) && mYUV420P)
			{
				GLES20.glDeleteProgram(mProgram);
				setupOpengl(false);
			}
			else if((!mAmlogicEnable )&& !mYUV420P)
			{
				GLES20.glDeleteProgram(mProgram);
				setupOpengl(true);
			}
		}
		else {

			if( (mUserMediaCodec && mYUVType == 21) && mYUV420P)
			{
				GLES20.glDeleteProgram(mProgram);
				setupOpengl(false);
			}
			else if((mUserMediaCodec && mYUVType == 19)&& !mYUV420P)
			{
				GLES20.glDeleteProgram(mProgram);
				setupOpengl(true);
			}
		}

		// 1. 设置旋转的旋转		
		android.opengl.Matrix.setRotateM(mRotationMatrix, 0, mRotateDegree, 0, 0, 1.0f);
		// 2. 设置显示的大小，内部自动进行缩放
		GLES20.glViewport(mViewX, mViewY, mViewWidth, mViewHeight);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// 3. 对这着色器中的变量进行赋值
		if(mBuffer != null)
		{
			GLES20.glUseProgram(mProgram);
			synchronized(this){
				GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureY[0]);
				GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, mWidthStrideY, mBufferHeightY, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, mBuffer.position(mBufferPositionY));
				GLES20.glUniform1i(muSamplerYHandle, 0);

				if(mYUV420P)
				{
					GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
					GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureU[0]);
					GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, mWidthStrideUV, mBufferHeightUV, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, mBuffer.position(mBufferPositionU));
					GLES20.glUniform1i(muSamplerUHandle, 1);

					GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
					GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureV[0]);
					GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, mWidthStrideUV, mBufferHeightUV, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, mBuffer.position(mBufferPositionV));
					GLES20.glUniform1i(muSamplerVHandle, 2);
				}
				else
				{
					GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
					GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureUV[0]);
					GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE_ALPHA, mWidthStrideUV, mBufferHeightUV, 0, GLES20.GL_LUMINANCE_ALPHA, GLES20.GL_UNSIGNED_BYTE, mBuffer.position(mBufferPositionUV));
					GLES20.glUniform1i(muSamplerUVHandle, 1);
				}

				// 应用投影和视口变换
				GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mRotationMatrix, 0);
			}
		}
		// 4. 进行显示
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDICES_DATA.length, GLES20.GL_UNSIGNED_SHORT, mIndices);

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub		
		initCnt = 0;
		mSetupOpengl = false;

		setViewport();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		setupOpengl(mYUV420P);
		mSetupOpengl = true;
	}

	public void setAmlogicEnable(boolean amlogicEnable)
	{
		mAmlogicEnable = amlogicEnable;
	}
	public void useMediaCodecInfo(boolean useMediaCodec,int YuvType)
	{
		mUserMediaCodec = useMediaCodec;
		mYUVType =  YuvType;
	}

	private void setupOpengl(boolean yuv420p)
	{

		if(mYUV420P == yuv420p && mSetupOpengl && initCnt >= 3)
		{
			initCnt = 0;
			return;
		}

		initCnt ++;

		mYUV420P = yuv420p;

		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glDisable(GLES20.GL_DITHER);
		GLES20.glDisable(GLES20.GL_STENCIL_TEST);
		GLES20.glDisable(GL10.GL_DITHER);

		String extensions = GLES20.glGetString(GL10.GL_EXTENSIONS);
		Log.d(TAG, "OpenGL extensions=" + extensions);

		// Ignore the passed-in GL10 interface, and use the GLES20
		// class's static methods instead.

		if(mYUV420P)
		{
			mProgram = createProgram(VERTEX_SHADER_SOURCE, FRAGMENT_SHADER_SOURCE_420P);
		}
		else
		{
			mProgram = createProgram(VERTEX_SHADER_SOURCE, FRAGMENT_SHADER_SOURCE_420SP);
		}

		if (mProgram == 0) {
			return;
		}
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		checkGlError("glGetAttribLocation aPosition");
		if (maPositionHandle == -1) {
			throw new RuntimeException("Could not get attrib location for aPosition");
		}
		maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
		checkGlError("glGetAttribLocation aTextureCoord");
		if (maTextureHandle == -1) {
			throw new RuntimeException("Could not get attrib location for aTextureCoord");
		}

		muSamplerYHandle = GLES20.glGetUniformLocation(mProgram, "SamplerY");
		if (muSamplerYHandle == -1) {
			throw new RuntimeException("Could not get uniform location for SamplerY");
		}

		if(mYUV420P)
		{
			muSamplerUHandle = GLES20.glGetUniformLocation(mProgram, "SamplerU");
			if (muSamplerUHandle == -1) {
				throw new RuntimeException("Could not get uniform location for SamplerU");
			}


			muSamplerVHandle = GLES20.glGetUniformLocation(mProgram, "SamplerV");
			if (muSamplerVHandle == -1) {
				throw new RuntimeException("Could not get uniform location for SamplerV");
			}
		}
		else
		{
			muSamplerUVHandle = GLES20.glGetUniformLocation(mProgram, "SamplerUV");
			if (muSamplerUVHandle == -1) {
				throw new RuntimeException("Could not get uniform location for SamplerUV 2");
			}
		}

		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
		if (mMVPMatrixHandle == -1) {
			throw new RuntimeException("Could not get uniform location for SamplerV");
		}


		//enable
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		checkGlError("glEnableVertexAttribArray maPositionHandle");

		GLES20.glEnableVertexAttribArray(maTextureHandle);
		checkGlError("glEnableVertexAttribArray maTextureHandle");



		GLES20.glGenTextures(1, mTextureY, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureY[0]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

		if(mYUV420P)
		{
			GLES20.glGenTextures(1, mTextureU, 0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureU[0]);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

			GLES20.glGenTextures(1, mTextureV, 0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureV[0]);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		}
		else
		{
			GLES20.glGenTextures(1, mTextureUV, 0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureUV[0]);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		}

	}

	private void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, op + "ZMCEVideoGLPreview---Error: glError " + error);
		}
	}

	private void setViewport()
	{
		int TmpBufferWidth = mBufferWidthY;
		int TmpBufferHeight = mBufferHeightY;

		// 获取画布的宽和高
		int CanvaWidth = mGlSurfaceView.getWidth();
		int CanvaHeight = mGlSurfaceView.getHeight();

		//调整视口
		if (mRotateDegree % 180 != 0)
		{
			int itmp = TmpBufferWidth;
			TmpBufferWidth = TmpBufferHeight;
			TmpBufferHeight = itmp;
		}
		float fRatio = ((float) TmpBufferWidth / (float) TmpBufferHeight);
		mViewWidth = (int) ((float) CanvaWidth / fRatio) > CanvaHeight ? (int) ((float) CanvaHeight * fRatio) : CanvaWidth;
		mViewHeight = (int) (CanvaWidth / fRatio) > CanvaHeight ? CanvaHeight : (int) (CanvaWidth / fRatio);
		mViewX = ((CanvaWidth - mViewWidth) >> 1);
		mViewY = ((CanvaHeight - mViewHeight) >> 1);

		//修正纹理coordinate
		float ftextureCoord = ((float)mBufferWidthY/(float)mWidthStrideY)*0.998f;
		textureCoords[0] = textureCoords[2] = ftextureCoord;
		mVertexArray.put(vertexPositions);
		mCoordsArray.put(textureCoords);

		mVertexArray.position(0);
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexArray);

		mCoordsArray.position(0);
		GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mCoordsArray);
	}

	private int loadShader(int shaderType, String source) {
		int shader = GLES20.glCreateShader(shaderType);
		if (shader != 0) {
			GLES20.glShaderSource(shader, source);
			GLES20.glCompileShader(shader);
			int[] compiled = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
			if (compiled[0] == 0) {
				Log.e(TAG, "Could not compile shader " + shaderType + ":");
				Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
				GLES20.glDeleteShader(shader);
				shader = 0;
			}
		}
		return shader;
	}
	private int createProgram(String vertexSource, String fragmentSource) {
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
		if (vertexShader == 0) {
			return 0;
		}

		int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
		if (pixelShader == 0) {
			return 0;
		}

		int program = GLES20.glCreateProgram();
		if (program != 0) {
			GLES20.glAttachShader(program, vertexShader);
			checkGlError("glAttachShader");
			GLES20.glAttachShader(program, pixelShader);
			checkGlError("glAttachShader");
			GLES20.glLinkProgram(program);
			int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
			if (linkStatus[0] != GLES20.GL_TRUE) {
				Log.e(TAG, "Could not link program: ");
				Log.e(TAG, GLES20.glGetProgramInfoLog(program));
				GLES20.glDeleteProgram(program);
				program = 0;
			}
		}
		return program;
	}
}