package zime.media;

import java.nio.ByteBuffer;

import zime.media.ZIMEVideoClientJNI.T_ZIMEAudioDownlinkStat;
import zime.media.ZIMEVideoClientJNI.T_ZIMEAudioUplinkStat;

public class ZIMEClientJni
{

	public class StrParam
	{
		public String retuStr;
	}

	public class IntParam
	{
		public int  retuInt;
	}

	public class BoolParam
	{
		public boolean  retuBool;
	}


	public  class ZIMECodeInfo
	{
		public int  s32PT;             /**< PT值 */
	public int  s32SampleRate;     /**< 采样率 */
	public int  s32PacketSize;     /**< 每包采样数 */
	public int  s32ChannelNum;     /**< 通道个数 */
	public int  s32BitRate;        /**< 码率 Bits/S (optional) */
	public String aPTName;         /**< 负载名 */
	public int  eExtnType;
		public int  codecExInfo;
		public ZIMECodeInfo(int i_s32PT, int i_s32SampleRate, int i_s32PacketSize, int i_s32ChannelNum, int i_s32BitRate,String i_aPTName)
		{
			s32PT = i_s32PT;
			s32SampleRate = i_s32SampleRate;
			s32PacketSize = i_s32PacketSize;
			s32ChannelNum = i_s32ChannelNum;
			s32BitRate = i_s32BitRate;
			aPTName = i_aPTName;
			eExtnType = 0;
			codecExInfo = 0;
		}
	}

	// 此处只封装了部分接接口，请根据需要自行封装
	public static  native int Create();

	public static  native int Destroy();

	public static  native int Init();

	public static  native int Terminate();

	public static native int Exit();

	public static  native int SetLogLevel(int Level);

	public static  native int CreateChannel();

	public static  native int DeleteChannel(int ChId);

	public static  native int MaxNumOfChannels();

	public static  native int SetLocalReceiver(int ChId, int VoiceRTPPort,
											   int VoiceRTCPPort);

	public static  native int GetLocalReceiver(int ChId,  IntParam VoiceRTPPort,
					 IntParam VoiceRTCPPort,IntParam VideoRTPPort,IntParam VideoRTCPPort, StrParam LocalAddr, int AddrLen );

	public static  native int SetSendDestination(int ChId, int VoiceRTPPort,
												 String DstAddr, int VoiceRTCPPort);

	public static  native int GetSendDestination(int ChId,  IntParam VoiceRTPPort,
												 IntParam VoiceRTCPPort,IntParam VideoRTPPort,IntParam VideoRTCPPort, StrParam LocalAddr, int AddrLen );

	public static  native int StartListen(int ChId);

	public static  native int StopListen(int ChId);

	public static  native int StartPlayout(int ChId);

	public static  native int StopPlayout(int ChId);

	public static  native int StartSend(int ChId);

	public static  native int StopSend(int ChId);

	public static  native int GetNumOfCodecs();

	public static  native int GetCodec(int CodecIdx, ZIMECodeInfo VoiceCodecInfo);

	public static  native int SetSendCodec(int ChId, ZIMECodeInfo VoiceCodecInfo);

	public static  native int GetSendCodec(int ChId, ZIMECodeInfo VoiceCodecInfo);

	public static  native int SetRecPayloadType(int ChId, ZIMECodeInfo VoiceCodecInfo);

	public static  native int GetRecCodec(int ChId, ZIMECodeInfo VoiceCodecInfo);
	public static  native int SetModeSubset(int i_s32ChId, byte []i_aSubset, int i_s32Size, boolean i_b8UseMin);
	public static  native int SetECStatus(boolean Enable);
	public static  native int SetVQEScene(int VQEScene);

	public static  native int GetECStatus(BoolParam Enable);

	public static  native int SetNSStatus(boolean Enable);

	public static  native int SetAGCStatus(boolean Enable, int Mode);

	public static  native int GetNSStatus(boolean Enable);

	public static  native int SetSpeakerMode(boolean Enable);

	public static  native int SetAudioCallBack(AudioDeviceCallBack t_audioDevice);
	public static  native int PutAudioInFrame(ByteBuffer RecordData, int i_DataLenth);
	public static  native int GetAudioOutFrame(ByteBuffer PlayData,  int i_DataLenth);

	public static native int SetSampleRate(int i_SampleRate);

	public static  native int SetInputMute(int iChannel, boolean Enable);
	public static  native int DisconnectDevice(int i_s32ChId, int i_nDeviceType);
	public static  native int ConnectDevice(int i_s32ChId, int i_nDeviceType);
	public static  native int SetSendDTMFPayloadType(int i_s32ChId, byte i_u8PT);
	public static  native int SendDTMF(int i_s32ChId, int i_s32EvtNum, boolean i_bOutBand, int i_s32LenMs, int i_s32Level);
	public static  native int SetDTMFFeedbackStatus(boolean i_bEnable, boolean i_bDirectFeedback);
	public static  native int GetAudioQosStat(int i_s32ChId, T_ZIMEAudioUplinkStat o_tUplinkQosStat, T_ZIMEAudioDownlinkStat o_tDownlinkQosStat);

	public static  native int SetSourceFilter(int ChId, int VoiceRTPPort, String DstAddr);
}