package zime.media;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioDeviceCallBack
{
	private static final String TAG = AudioDeviceCallBack.class.getCanonicalName();
	// 采集
	private AudioRecord mAudioRecord = null;
	private int mRecordSampleLen = 0;
	private boolean mbStereoRecord = false;
	private boolean mbRightChannel = false;
	byte[] mreadbuf = null;

	// 播放
	private AudioTrack mAudioTrack = null;
	private boolean mConsumerPlaying = false;
	/*************************************************************************************************************************
	 * 采集相关的部分
	 * @return
	 */
	public int ProducerOpen(int nSampleRate)
	{
		Log.d(TAG, "Audio ProducerOpen enter");
		int ptime = 10;
		int rate = nSampleRate;
		// 1. 创建采集的声卡	
		int minBufferSize = 0;

		try {
			minBufferSize = AudioRecord.getMinBufferSize(rate, mbStereoRecord ? AudioFormat.CHANNEL_IN_STEREO : AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT);

		} catch (Exception e) {
			Log.e(TAG, "ProducerOpen:getMinBufferSize failed,reason:" + e.toString());
			return -1;
		}

		final int shortsPerNotif = (rate * ptime)/1000; //480

		int bufferSize = Math.max((int) ((float) minBufferSize), shortsPerNotif * 2);
		Log.d(TAG, "buffersize:" + bufferSize + ",minBuffersize:" + minBufferSize);

		mRecordSampleLen = mbStereoRecord ? shortsPerNotif * 2 * 2 : shortsPerNotif * 2;
		mreadbuf = new byte[mRecordSampleLen];

		// 外部buf大小也改为200
		int iTmp = mRecordSampleLen * 200;
		if (bufferSize < iTmp)
		{
			bufferSize = iTmp;
		}

		try
		{
			mAudioRecord = new AudioRecord( MediaRecorder.AudioSource.VOICE_RECOGNITION, rate, mbStereoRecord ? AudioFormat.CHANNEL_IN_STEREO: AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSize);
		}
		catch (Exception e)
		{
			mAudioRecord.release();
			mAudioRecord = null;
			Log.d(TAG, "ProducerOpen use VOICE_COMMUNICATION to new audioRecord failed, Reason:" + e.toString());
		}
		if (mAudioRecord == null || mAudioRecord.getState() == 0)
		{
			Log.e(TAG, "------careful:the VOICE_COMMUNICATION  is not ok! we use the MIC!");
			if (mAudioRecord != null) {
				mAudioRecord.release();
				mAudioRecord = null;
			}
			try {
				mAudioRecord = new AudioRecord( MediaRecorder.AudioSource.MIC, rate, mbStereoRecord ? AudioFormat.CHANNEL_IN_STEREO: AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT, bufferSize);
			} catch (Exception e) {
				mAudioRecord.release();
				mAudioRecord = null;
				Log.d(TAG, "ProducerOpen use MIC to new audioRecord failed, Reason:" + e.toString());
			}

		}
		if (mAudioRecord == null) {
			Log.d(TAG, "mAudioRecord is null.");
			return -1;
		}

		// Check if it was initialized properly.
		// This ensures that the appropriate hardware resources have been acquired.
		int iState = mAudioRecord.getState();
		if(iState != AudioRecord.STATE_INITIALIZED){
			Log.d(TAG, "ProducerOpen failed,state error:" + iState);
			return -1;
		}

		Log.d(TAG, "Audio ProducerOpen succeed.VOICE_RECOGNITION");
		return 0;
	}

	public int ProducerClose()
	{
		Log.d(TAG, "Audio ProducerClose enter.");
		if(mAudioRecord != null)
		{
			mAudioRecord.stop();
			mAudioRecord.release();
			mAudioRecord = null;
		}
		mreadbuf = null;
		Log.d(TAG, "Audio ProducerClose succeed.");
		return 0;
	}

	public int ProducerStart()
	{
		Log.d(TAG, "Audio ProducerStart enter.");
	   /*
	    * 1.startRecording() called on an uninitialized AudioRecord.
	    * 2.IllegalStateException
	   */
		try {
			mAudioRecord.startRecording();
		} catch (Exception e) {
			Log.e(TAG, "mAudioRecord startRecording failed,Reason:" + e.toString());
			return -1;
		}
		// startRecording调用成功，状态置为RECORDSTATE_RECORDING，避免在其他录音设备已经打开的情况下，函数依然返回1	
		int iState = mAudioRecord.getRecordingState();
		if (iState != AudioRecord.RECORDSTATE_RECORDING) {
			Log.e(TAG, "ProducerStart************************failed:" + iState);
			return -1;
		}

		Log.d(TAG, "Audio ProducerStart succeed.");
		return 0;
	}

	public int ProducerStop()
	{
		Log.d(TAG, "Audio ProducerStop enter.");
		if (mAudioRecord != null) {
			mAudioRecord.stop(); //if new AudioRecord failed will throw exception.
		}
		Log.d(TAG, "Audio ProducerStop succeed.");
		return 0;
	}

	public void GetLeftData(byte[] i_SrcData, int i_SrcLen, byte[] i_DstData, int i_dstLen)
	{
		int i = 0, j = 0;
		for (i = 0; i < i_SrcLen; i=i+4,j=j+2)
		{
			i_DstData[j]   = i_SrcData[i];
			i_DstData[j+1] = i_SrcData[i+1];
		}
		return ;
	}

	public void GetRightData(byte[] i_SrcData, int i_SrcLen, byte[] i_DstData, int i_dstLen)
	{
		int i = 2, j = 0;

		for (i = 2; i < i_SrcLen; i=i+4,j=j+2) {
			i_DstData[j]   = i_SrcData[i];
			i_DstData[j+1] = i_SrcData[i+1];
		}
		return ;
	}

	public int GetFrame(byte[] i_pFrameBuf, int i_iBufLen)
	{
		int iReadLen = 0;
		if(null == mAudioRecord)
		{
			return 0;
		}

		int iTmp = mbStereoRecord ? mRecordSampleLen/2 : mRecordSampleLen;
		if (i_iBufLen < iTmp) {

			Log.e(TAG, "Audo GetFrame buffer too small:" + i_iBufLen + "need lenth:" + mRecordSampleLen);
			return 0;
		}


		if(mbStereoRecord)
		{
			iReadLen = mAudioRecord.read(mreadbuf, 0, mRecordSampleLen);
			if (mbRightChannel) {
				GetRightData(mreadbuf, iReadLen, i_pFrameBuf, iReadLen/2);
			}
			else {
				GetLeftData(mreadbuf, iReadLen, i_pFrameBuf, iReadLen/2);
			}
			return iReadLen/2;
		}

		iReadLen = mAudioRecord.read(i_pFrameBuf, 0, mRecordSampleLen);
		return iReadLen;
	}

	/*************************************************************************************************************************
	 * 播放相关的部分
	 * @return
	 */
	public int ConsumerOpen(int nSampleRate)
	{
		Log.d(TAG, "Audio ConsumerOpen enter.");
		int ptime = 10;
		int rate = nSampleRate;
		int BufferSize = 0;
		// 创建播放的声卡		
		int minBufferSize = 0;
		try {
			minBufferSize = AudioTrack.getMinBufferSize(rate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "AudioTrack getMinBufferSize failed,reason:" + e.toString());
			return -1;
		}

		final int shortsPerNotif = (rate * ptime)/1000;
		BufferSize = Math.max(minBufferSize, shortsPerNotif * 2);

		try
		{
			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, rate, AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT, BufferSize, AudioTrack.MODE_STREAM);
		}
		catch (Exception e)
		{
			mAudioTrack = null;
			Log.e(TAG, "ConsumerOpen new AudioTrack failed,reason:" + e.toString());
			return -1;
		}
		if (mAudioTrack == null) {
			Log.e(TAG, "mAudioTrack is null");
			return -1;
		}
		// Check if it was initialized properly. 
		// This ensures that the appropriate hardware resources have been acquired.
		int iState = mAudioTrack.getState();
		if(iState != AudioTrack.STATE_INITIALIZED){
			Log.d(TAG, "ConsumerOpen failed,state error:" + iState);
			return -1;
		}
		Log.d(TAG, "Audio ConsumerOpen succeed.");
		return 0;
	}

	public int ConsumerClose()
	{
		Log.d(TAG, "Audio ConsumerClose enter.");
		if(mAudioTrack != null)
		{
			mAudioTrack.stop();
			mAudioTrack.release();
			mAudioTrack = null;
		}
		Log.d(TAG, "Audio ConsumerClose succeed.");
		return 0;
	}

	public int ConsumerStart()
	{
		Log.d(TAG, "Audio ConsumerStart enter.");
		Log.d(TAG, "Audio ConsumerStart succeed.");
		return 0;
	}

	public int ConsumerStop()
	{
		Log.d(TAG, "Audio ConsumerStop enter.");
		if (mAudioTrack != null) {
			mAudioTrack.stop();
		}
		mConsumerPlaying = false;
		Log.d(TAG, "Audio ConsumerStop succeed.");
		return 0;
	}

	public int WriteFrame(byte[] i_pFrameBuf, int i_iBufLen)
	{
		if(null == mAudioTrack)
		{
			return 0;
		}
		int nRet = mAudioTrack.write(i_pFrameBuf, 0, i_iBufLen);
		if (!mConsumerPlaying) {
			/*
			 * 1.play() called on uninitialized AudioTrack.
			 * 2.IllegalStateException ,Unable to retrieve AudioTrack pointer for start()
			 */
			try {
				mAudioTrack.play();
			} catch (Exception e) {
				Log.e(TAG, "mAudioTrack.play failed, Reason:" + e.toString());
				return -1;
			}

			mConsumerPlaying = true;
		}
		return nRet;
	}
}


