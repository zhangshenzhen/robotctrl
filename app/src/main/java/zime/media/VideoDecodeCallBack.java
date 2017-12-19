package zime.media;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Locale;

public class VideoDecodeCallBack {
	private static final String TAG = VideoDecodeCallBack.class
			.getCanonicalName();
	private MediaCodec mDecoder = null;
	private BufferInfo mBufInfo = null;

	private ByteBuffer[] mInputBuffers = null;
	private ByteBuffer[] mOutputBuffers = null;
	boolean mIsEOS = false;
	private int mWidth = 0;
	private int mHeight = 0;
	public  long mRtpTs = 0;

	private final int timeoutWaitForStartDecoderInMicroseconds = 300000;
	private final int timeoutWaitForDequeueInputBufferInMicroseconds = 100000;
	private final int timeoutWaitForDequeueOutputBufferInMicroseconds = 5000;

	private boolean mStartOK = false;
	private boolean mFirst = true;
	private MediaFormat mdecoderformat;

	public static ArrayList<MediaCodecInfo> GetSupportDecoders() {
		ArrayList<MediaCodecInfo> supportDecoders = new ArrayList<MediaCodecInfo>();
		int totalCodecCount = MediaCodecList.getCodecCount();
		for (int i = 0; i < totalCodecCount; i++) {
			MediaCodecInfo mediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
			if (!mediaCodecInfo.isEncoder()
					&& (mediaCodecInfo.getName().toUpperCase(Locale.getDefault()).contains("AVC") || mediaCodecInfo.getName().toUpperCase(Locale.getDefault()).contains("H264"))) {
				String[] types = mediaCodecInfo.getSupportedTypes();
				for (String type : types) {
					if (type.contains("avc")) {
						int[] colorFormats = mediaCodecInfo.getCapabilitiesForType(type).colorFormats;
						if (colorFormats[0] == CodecCapabilities.COLOR_FormatYUV420Planar
								|| colorFormats[0] == CodecCapabilities.COLOR_FormatYUV420SemiPlanar) {
							supportDecoders.add(mediaCodecInfo);
							Log.e(TAG, "name=" + mediaCodecInfo.getName() + ";colorFormat=" + mediaCodecInfo.getCapabilitiesForType("video/avc").colorFormats[0]);
						}
					}
				}
			}
		}

		return supportDecoders;
	}

	public int StartDecoder(int width, int height)
	{
		synchronized (this) {
			if (mDecoder != null) {
				Log.e(TAG, "StopDecoder+");
				mDecoder.flush();
			}
		}
		if (mFirst) {

			String mimeType = "video/avc";
			try {
				mDecoder = MediaCodec.createDecoderByType(mimeType);
			}
			catch(Exception e){
				e.getStackTrace();
				e.toString();
				e.getMessage();
			}

			mdecoderformat = MediaFormat.createVideoFormat(mimeType, 1280, 720);
			if(null == mDecoder)
			{
				return -1;
			}
			mBufInfo = new BufferInfo();

			mDecoder.configure(mdecoderformat, null, null, 0);
			mDecoder.start();
			mInputBuffers = mDecoder.getInputBuffers();
			mOutputBuffers = mDecoder.getOutputBuffers();
			int startDecoderIdx = mDecoder.dequeueInputBuffer(timeoutWaitForStartDecoderInMicroseconds);

			Log.e(TAG, "startDecoderIdx+" + startDecoderIdx);
			if (startDecoderIdx < 0) {
				StopDecoder(0);
				return -1;
			}
			mDecoder.flush();
			mFirst = false;
		}

		Log.e(TAG, "StartDecoder+");
		mStartOK = true;
		mWidth = width;
		mHeight = height;
		return 0;
	}

	public int emptyThisBuffer(byte[] i_pInFrame, int i_s32InFrameLen,long i_Timets)
	{
		if (!mStartOK) {
			return -1;
		}

		if (mIsEOS) {
			return -1;
		}
		int inputBufferIndex = mDecoder.dequeueInputBuffer(timeoutWaitForDequeueInputBufferInMicroseconds);
		if (inputBufferIndex < 0) {
			return -1;
		}
		if (i_s32InFrameLen < 0) {
			mDecoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
			mIsEOS = true;
			return -1;
		}
		ByteBuffer buffer = mInputBuffers[inputBufferIndex];
		int samplesize = i_s32InFrameLen;
		buffer.clear();
		buffer.put(i_pInFrame, 0, samplesize);

		mDecoder.queueInputBuffer(inputBufferIndex, 0,
				samplesize, i_Timets, 0);
		return 0;
	}

	public int fillThisBuffer(byte[] o_pOutFrame)
	{
		if (!mStartOK) {
			return -1;
		}

		if (mIsEOS) {
			return -1;
		}

		int nLen = 0;
		int outputBufferIndex = mDecoder.dequeueOutputBuffer(mBufInfo, timeoutWaitForDequeueOutputBufferInMicroseconds);
		if (outputBufferIndex >= 0) {

			ByteBuffer buf = mOutputBuffers[outputBufferIndex];
			mRtpTs = mBufInfo.presentationTimeUs ;
			if (mBufInfo.size > 0 && mBufInfo.size >= mWidth * mHeight *3 /2) {
				mBufInfo.size = mWidth * mHeight *3 /2;
				buf.position(mBufInfo.offset);
				buf.limit(mBufInfo.offset + mBufInfo.size);
				buf.get(o_pOutFrame, 0, mBufInfo.size);
				nLen = mBufInfo.size;
			}

			mDecoder.releaseOutputBuffer(outputBufferIndex, true);
			if ((mBufInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {

				return 0;
			}

		} else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
			mOutputBuffers = mDecoder.getOutputBuffers();

		} else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
			return -2;
		}
		else {
			return -1;
		}

		return nLen;
	}

	public int Decode(byte[] i_pInFrame, int i_s32InFrameLen, byte[] o_pOutFrame ,long i_Timets)
	{
		synchronized (this) {
			emptyThisBuffer(i_pInFrame ,i_s32InFrameLen,i_Timets);
			return fillThisBuffer(o_pOutFrame);
		}
	}


	public int GetFrame( byte[] o_pOutFrame )
	{
		synchronized (this) {
			return fillThisBuffer(o_pOutFrame);
		}
	}


	public int StopDecoder(int i_ReleaseRS)
	{
		synchronized (this) {
			if(i_ReleaseRS == 0)
			{
				if (mDecoder != null) {
					Log.e(TAG, "StopDecoder+");
					mDecoder.flush();
				}
			}
			else {
				if (mDecoder != null) {
					mDecoder.flush();
					mDecoder.stop();
					mDecoder.release();
				}
			}
			mStartOK = false;
		}

		return 0;
	}

}
	

