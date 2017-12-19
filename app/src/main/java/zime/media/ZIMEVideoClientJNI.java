package zime.media;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ZIMEVideoClientJNI {

	private static boolean CpuHasNeon() {

		boolean bHasneon = false;
		String str1 = "/proc/cpuinfo";
		String str2 = "";

		FileReader fr;
		try {
			fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);

			while (true) {
				str2 = localBufferedReader.readLine();
				//arrayOfString = str2.split("\\s+");		

				if (str2 == null) {
					break;
				}

				Log.i("ZIMEVideoClientJNI", "CPUINFO------- " + str2);

				int nStart = str2.toLowerCase().indexOf("neon");
				if (nStart >= 0) {
					bHasneon = true;
				}
			}

			localBufferedReader.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bHasneon;
	}

	public static void ZIMELoadLibrary() {

		boolean bHasneon = CpuHasNeon();
		try {
			System.loadLibrary("CommonUtilitiesLib");
			System.loadLibrary("LightLog");
			System.loadLibrary("RTCPLib");
			System.loadLibrary("RTPPackerLib");
			System.loadLibrary("RTPParser");
			System.loadLibrary("AEC_ARM_ANDROID");
			System.loadLibrary("zteH265EncARMv7Android");
			System.loadLibrary("zteH265DecARMv7Android");

			if (bHasneon) {
				Log.i("ZIMEVideoClientJNI", "loadLibrary Encode and VideoEhance and Dec NEON Android ------- ");
				System.loadLibrary("zteH264EncARMv7Android");
				System.loadLibrary("zteVideoEnhanceNEON_Android");
				System.loadLibrary("zteH264DecARMv7Android");
			} else {
				Log.i("ZIMEVideoClientJNI", "loadLibrary Encode and VideoEhanceARM  and Dec Android ------- ");
				System.loadLibrary("zteH264EncARMv5Android");
				System.loadLibrary("zteVideoEnhanceARM11Android");
				System.loadLibrary("zteH264Dec_ARM11");
			}

			System.loadLibrary("RTPFECLib");
			System.loadLibrary("yuv_static");
			System.loadLibrary("ZIMEClientSDKAndroid");
			System.loadLibrary("ZIMECodecDevCallBack");
			System.loadLibrary("ZIMEClientSDKJNI");
			System.out.println("The load succ----");
		} catch (Throwable e) {
			System.out.println("The load problem----");
		}
	}

	public class StrPtr {
		public String retuStr;
	}

	public class IntPtr {
		public int retuInt;
	}

	public class BoolPtr {
		public boolean retuBool;

		BoolPtr() {
			retuBool = false;
		}
	}

	public class FloatPtr {
		public float retuRate;

		FloatPtr() {
			retuRate = 0;
		}
	}

	public static class T_ZIMEAudioCommonStat {
		public int iCurFractionLost;
		/**
		 * < 音频网络通道丢包
		 */
		public int iTotalFractionLost;
		/**
		 * < 音频网络通道总丢包数（个）
		 */
		public int iCurJitter;
		/**
		 * < 音频网络通道抖动（ms）
		 */
		public int iCurBitrate;
		public int iAvgBitrate;
		public int iCurRTT;

		public T_ZIMEAudioCommonStat(int i_CurFractionLost, int i_TotalFractionLost, int i_CurJitter, int i_CurBitrate, int i_CurRTT, int i_AvgBitrate) {
			iCurFractionLost = i_CurFractionLost;
			iTotalFractionLost = i_TotalFractionLost;
			iCurJitter = i_CurJitter;
			iCurBitrate = i_CurBitrate;
			iAvgBitrate = i_AvgBitrate;
			iCurRTT = i_CurRTT;
		}

		public T_ZIMEAudioCommonStat() {
			iCurFractionLost = -1;
			iTotalFractionLost = -1;
			iCurJitter = -1;
			iCurBitrate = -1;
			iAvgBitrate = -1;
			iCurRTT = -1;
		}
	}

	public static class T_ZIMEAudioUplinkStat extends T_ZIMEAudioCommonStat {
		public T_ZIMEAudioUplinkStat(int i_CurFractionLost, int i_TotalFractionLost, int i_CurJitter, int i_CurBitrate, int i_CurRTT, int i_AvgBitrate) {
			iCurFractionLost = i_CurFractionLost;
			iTotalFractionLost = i_TotalFractionLost;
			iCurJitter = i_CurJitter;
			iCurBitrate = i_CurBitrate;
			iAvgBitrate = i_AvgBitrate;
			iCurRTT = i_CurRTT;
		}
	}

	public static class T_ZIMEAudioDownlinkStat extends T_ZIMEAudioCommonStat {
		public T_ZIMEAudioDownlinkStat(int i_CurFractionLost, int i_TotalFractionLost, int i_CurJitter, int i_CurBitrate, int i_CurRTT, int i_AvgBitrate) {
			iCurFractionLost = i_CurFractionLost;
			iTotalFractionLost = i_TotalFractionLost;
			iCurJitter = i_CurJitter;
			iCurBitrate = i_CurBitrate;
			iAvgBitrate = i_AvgBitrate;
			iCurRTT = i_CurRTT;
		}
	}


	public static class T_ZIMEVideoCommonStat {
		public int iCurFractionLost;
		/**
		 * < 音频网络通道丢包
		 */
		public int iTotalFractionLost;
		/**
		 * < 音频网络通道总丢包数（个）
		 */
		public int iCurJitter;
		/**
		 * < 音频网络通道抖动（ms）
		 */
		public int iCurBitrate;
		public int iAvgBitrate;
		public int iCurRTT;

		public T_ZIMEVideoCommonStat(int i_CurFractionLost, int i_TotalFractionLost, int i_CurJitter, int i_CurBitrate, int i_CurRTT, int i_AvgBitrate) {
			iCurFractionLost = i_CurFractionLost;
			iTotalFractionLost = i_TotalFractionLost;
			iCurJitter = i_CurJitter;
			iCurBitrate = i_CurBitrate;
			iAvgBitrate = i_AvgBitrate;
			iCurRTT = i_CurRTT;
		}

		public T_ZIMEVideoCommonStat() {
			iCurFractionLost = -1;
			iTotalFractionLost = -1;
			iCurJitter = -1;
			iCurBitrate = -1;
			iAvgBitrate = -1;
			iCurRTT = -1;
		}
	}

	public static class T_ZIMEVideoUplinkStat extends T_ZIMEVideoCommonStat {
		public int iRealCapFrameRate;
		public int iExpectedFrameRate;
		public int iRealFrameRate;
		public int iWidth;
		public int iResSwitchTrigger;
		public int iExpectedESBitRate;
		public int iRealESBitRate_Cur;
		public int iRealESBitRate_Avg;
		public int iRedundantBitRate_Cur;
		public int iRedundantBitRate_Avg;

		public T_ZIMEVideoUplinkStat(int i_CurFractionLost, int i_TotalFractionLost, int i_CurJitter, int i_CurBitrate,
									 int i_CurRTT, int i_AvgBitrate, int i_ExpectedFrameRate,
									 int i_RealFrameRate, int i_Width, int i_ResSwitchTrigger,
									 int i_EncodeBitrate, int i_ESBitrate, int i_ESAvgBitrate, int i_RedBitrate, int i_RedAvgBitrate) {
			iCurFractionLost = i_CurFractionLost;
			iTotalFractionLost = i_TotalFractionLost;
			iCurJitter = i_CurJitter;
			iCurBitrate = i_CurBitrate;
			iAvgBitrate = i_AvgBitrate;
			iCurRTT = i_CurRTT;
			iExpectedESBitRate = i_EncodeBitrate;
			iRealESBitRate_Avg = i_ESBitrate;
			iRealESBitRate_Avg = i_ESAvgBitrate;
			iRedundantBitRate_Cur = i_RedBitrate;
			iRedundantBitRate_Avg = i_RedAvgBitrate;

			iRealCapFrameRate = 0;
			iExpectedFrameRate = i_ExpectedFrameRate;
			iRealFrameRate = i_RealFrameRate;
		}
	}

	public static class T_ZIMEVideoDownlinkStat extends T_ZIMEVideoCommonStat {
		public int iRecvFrameRate;
		public int iDisplayFrameRate;
		public int iWidth;
		public int iResSwitchTrigger;

		public int iESBitRate_Cur;
		public int iESBitRate_Avg;
		public int iRedundantBitRate_Cur;
		public int iRedundantBitRate_Avg;
		public int iRealPktLostRate;

		public T_ZIMEVideoDownlinkStat(int i_CurFractionLost, int i_TotalFractionLost, int i_CurJitter, int i_CurBitrate,
									   int i_CurRTT, int i_AvgBitrate, int i_RecvFrameRate,
									   int i_DisplayFrameRate, int i_Width, int i_ResSwitchTrigger,
									   int i_ESBitrate, int i_ESAvgBitrate, int i_RedBitrate, int i_RedAvgBitrate, int i_iRealPktLostRate) {
			iCurFractionLost = i_CurFractionLost;
			iTotalFractionLost = i_TotalFractionLost;
			iCurJitter = i_CurJitter;
			iCurBitrate = i_CurBitrate;
			iAvgBitrate = i_AvgBitrate;
			iCurRTT = i_CurRTT;
			iRecvFrameRate = i_RecvFrameRate;
			iDisplayFrameRate = i_DisplayFrameRate;
			iWidth = i_Width;
			iResSwitchTrigger = i_ResSwitchTrigger;

			iESBitRate_Cur = i_ESBitrate;
			iESBitRate_Avg = i_ESAvgBitrate;
			iRedundantBitRate_Cur = i_RedBitrate;
			iRedundantBitRate_Avg = i_RedAvgBitrate;
			iRealPktLostRate = i_iRealPktLostRate;
		}
	}

	public class ZIMEAudioCodeInfo {
		public int s32PT;
		/**
		 * < PT值
		 */
		public int s32SampleRate;
		/**
		 * < 采样率
		 */
		public int s32PacketSize;
		/**
		 * < 每包采样数
		 */
		public int s32ChannelNum;
		/**
		 * < 通道个数
		 */
		public int s32BitRate;
		/**
		 * < 码率 Bits/S (optional)
		 */
		public String aPTName;
		/**
		 * < 负载名
		 */
		public int eExtnType;
		/**
		 * < enum类型，是否进行RTP头的扩展，以及扩展类型
		 */
		public int codecExInfo;

		public ZIMEAudioCodeInfo(int i_s32PT, int i_s32SampleRate, int i_s32PacketSize, int i_s32ChannelNum, int i_s32BitRate, String i_aPTName, int i_ExtnType, int i_codecExInfo) {
			s32PT = i_s32PT;
			s32SampleRate = i_s32SampleRate;
			s32PacketSize = i_s32PacketSize;
			s32ChannelNum = i_s32ChannelNum;
			s32BitRate = i_s32BitRate;
			aPTName = i_aPTName;
			eExtnType = i_ExtnType;
			codecExInfo = i_codecExInfo;
		}
	}

	public class ZIMEVideoCodeInfo {
		public int s32PT;
		/**
		 * < PT值
		 */
		public int s32SampleRate;
		/**
		 * < 采样率
		 */
		public int s32PacketSize;
		/**
		 * < 每包采样数
		 */
		public int s32ChannelNum;
		/**
		 * < 通道个数
		 */
		public int s32InitBitRate;
		/**
		 * < 起始码率
		 */
		public int s32BitRate;
		/**
		 * < 码率 Bits/S (optional)
		 */
		public String aPTName;
		/**
		 * < 负载名
		 */
		public int s32Width;
		/**
		 * < 宽
		 */
		public int s32Height;
		/**
		 * < 高
		 */
		public int u32FrameRate;
		/**
		 * < 帧率
		 */
		public int s32MaxBitRate;
		public int s32VBREnable;
		public int eExtnType;
		/**
		 * < enum类型，是否进行RTP头的扩展，以及扩展类型
		 */
		public int eSceneType;

		/**
		 * < enum类型，场景模式设定
		 */

		public ZIMEVideoCodeInfo(int i_s32PT, int i_s32SampleRate, int i_s32PacketSize, int i_s32ChannelNum, int i_s32InitBitRate, int i_s32BitRate,
								 String i_aPTName, int i_s32Width, int i_s32Height, int i_u32FrameRate, int i_eExtnType, int i_eSceneType) {
			s32PT = i_s32PT;
			s32SampleRate = i_s32SampleRate;
			s32PacketSize = i_s32PacketSize;
			s32ChannelNum = i_s32ChannelNum;
			s32InitBitRate = i_s32InitBitRate;
			s32BitRate = i_s32BitRate;
			aPTName = i_aPTName;
			s32Width = i_s32Width;
			s32Height = i_s32Height;
			eExtnType = i_eExtnType;
			eSceneType = i_eSceneType;

			if (i_u32FrameRate < 0)
				u32FrameRate = 10;
			else
				u32FrameRate = i_u32FrameRate;

			s32MaxBitRate = 8000000;
			s32VBREnable = 0;
		}
	}

	;

	public static class T_ZIMEVideoSwitchLevel {
		public int s32Width;
		public int s32Height;
		public int s32Framerate;
		public int s32Bitrate;

		public T_ZIMEVideoSwitchLevel(int i_s32Width, int i_s32Height, int i_s32Framerate, int i_s32Bitrate) {
			s32Width = i_s32Width;
			s32Height = i_s32Height;
			s32Framerate = i_s32Framerate;
			s32Bitrate = i_s32Bitrate;
		}

	}


	// 此处只封装了部分接接口，请根据需要自行封装
	public static native int Create();

	public static native int Destroy();

	public static native int Init();

	public static native int Terminate();

	public static native int Exit();

	public static native int SetLogLevel(int Level);

	public static native int CreateChannel(boolean UsingEncodeCAllback, int CodecType);

	public static native int DeleteChannel(int ChId);

	public static native int MaxNumOfChannels();

	public static native int SetLocalReceiver(int ChId, int AudioRTPPort, int AudioRTCPPort, int VideoRTPPort,
											  int VideoRTCPPort, StrPtr pLocalAddr);

	public static native int GetLocalReceiver(int ChId, IntPtr AudioRTPPort,
											  IntPtr AudioRTCPPort, IntPtr VideoRTPPort, IntPtr VideoRTCPPort, StrPtr LocalAddr, int AddrLen);

	public static native int SetSendDestination(int ChId, int AudioRTPPort, int AudioRTCPPort, int VideoRTPPort,
												int VideoRTCPPort, StrPtr pLocalAddr);

	public static native int GetSendDestination(int ChId, StrPtr LocalAddr, int AddrLen,
												IntPtr AudioRTPPort, IntPtr AudioRTCPPort, IntPtr VideoRTPPort, IntPtr VideoRTCPPort);

	public static native int StartListen(int ChId);

	public static native int StopListen(int ChId);

	public static native int StartPlayout(int ChId);

	public static native int StopPlayout(int ChId);

	public static native int StartSend(int ChId);

	public static native int StopSend(int ChId);

	public static native int GetNumOfAudioCodecs();

	public static native int GetNumOfVideoCodecs();

	public static native int GetAudioCodec(int CodecIdx, ZIMEAudioCodeInfo AudioCodecInfo);

	public static native int GetVideoCodec(int CodecIdx, ZIMEVideoCodeInfo VideoCodecInfo);

	public static native int SetSendCodec(int ChId, ZIMEAudioCodeInfo VoiceCodecInfo, ZIMEVideoCodeInfo VideoCodecInfo);

	public static native int GetSendCodec(int ChId, ZIMEAudioCodeInfo VoiceCodecInfo, ZIMEVideoCodeInfo VideoCodecInfo);

	public static native int SetRecPayloadType(int ChId, ZIMEAudioCodeInfo VoiceCodecInfo, ZIMEVideoCodeInfo VideoCodecInfo);

	public static native int GetRecCodec(int ChId, ZIMEAudioCodeInfo VoiceCodecInfo, ZIMEVideoCodeInfo VideoCodecInfo);

	public static native int SetSourceFilter(int ChId, int s32SrcAudioPort, int s32SrcVideoPort, StrPtr pSrcIP);

	public static native int SetSendSSRC(int ChId, int i_u32SSRC);

	public static native int GetSendSSRC(int ChId, IntPtr o_u32SSRC);

	public static native int SetAGCStatus(boolean Enable, int i_eMode);

	public static native int GetAGCStatus(BoolPtr Enable, IntPtr o_eMode);

	public static native int SetECStatus(boolean Enable);

	public static native int GetECStatus(BoolPtr Enable);

	public static native int SetNSStatus(boolean Enable);

	public static native int GetNSStatus(BoolPtr Enable);

	public static native int SetSpeakerMode(boolean Enable);

	public static native int SetSampleRate(int i_SampleRate);

	public static native int SetFECStatus(int i_s32ChId, boolean i_bDoFEC);

	public static native int SetNACKStatus(int i_s32ChId, boolean i_bDoNACK);

	public static native int SetIframeInterval(int i_s32ChId, int i_s32IframeInterval);

	public static native int ConfigVideoIntraFrameRefresh(int i_s32ChId);

	public static native int SetCCE(int i_s32ChId, boolean i_bDoCCE);

	public static native int SetDEE(int i_s32ChId, boolean i_bDoDEE);

	public static native int SetDeBlock(int i_s32ChId, boolean i_bIsDeBlock);

	public static native int SetBLE(int i_s32ChId, boolean i_bDoBLE);

	public static native int SetDNO(int i_s32ChId, boolean i_bDoDNO);

	public static native int SetVideoDisplayWnd(Object i_pLocalWnd, Object i_pRemoteWnd);

	public static native int SetCallBack();

	public static native int SetVideoCallBack();

	public static native int SetVideoEncodeFun(int i_s32ChId);

	public static native int SetVideoDecodeFun(int i_s32ChId);

	public static native int SetVendorType(int i_s32ChId, int i_nVendorType);

	public static native int SetModeSubset(int i_s32ChId, byte[] i_aSubset, int i_s32Size, boolean i_b8UseMin);

	public static native int SetLogPath(String path);

	public static native int SetVideoDevCapSize(int i_s32Width, int i_s32Height);

	public static native int SetVideoDevices(int i_nCameraID);

	public static native int GetAudioQosStat(int i_s32ChId, T_ZIMEAudioUplinkStat o_tUplinkQosStat, T_ZIMEAudioDownlinkStat o_tDownlinkQosStat);

	public static native int GetVideoQosStat(int i_s32ChId, T_ZIMEVideoUplinkStat o_tUplinkQosStat, T_ZIMEVideoDownlinkStat o_tDownlinkQosStat);

	public static native int SetInputMute(int i_s32ChId, boolean i_bMute);

	public static native int StartCapFileAsCamera(int i_s32ChId, String i_pFileName);

	public static native int StopCapFileAsCamera(int i_s32ChId);

	public static native int SetNetworkQualityNotify(int i_s32ChId);

	public static native int SetActivity(Object i_Activity);

	public static native int DisconnectDevice(int i_s32ChId, int i_nDeviceType);

	public static native int ConnectDevice(int i_s32ChId, int i_nDeviceType);

	public static native int SetVQEScene(int VQEScene);

	public static native int SetSendDTMFPayloadType(int i_s32ChId, byte i_u8PT);

	public static native int SendDTMF(int i_s32ChId, int i_s32EvtNum, boolean i_bOutBand, int i_s32LenMs, int i_s32Level);

	public static native int SetDTMFFeedbackStatus(boolean i_bEnable, boolean i_bDirectFeedback);

	public static native int SetExternalTransport(int i_s32ChId, int i_s32LocalRTPPort,
												  StrPtr jPeerAddr, int i_s32PeerRTPPort, boolean i_bIsHaveVideo);

	public static native int StartRTPExternalTransport(boolean i_bIsStartAudio, boolean i_bIsStartVideo);

	public static native int StartRTCPExternalTransport(boolean i_bIsStartAudio, boolean i_bIsStartVideo);

	//public static  native int StopExternalTransport(boolean i_bIsStopAudio, boolean i_bIsStopVideo);
	public static native int StopExternalTransportRecv();

	public static native int StopExternalTransportSend();

	public static native int ToAudio(int ChId);

	public static native int ToAudioAndVideo(int ChId, int i_s32LocalRTPPort, int i_s32VideoPeerRTPPort, StrPtr jPeerAddr);

	public static native int StartAudio(int ChId);

	public static native int SetVideoQualityLevelSet(int i_s32ChId, int nNum, T_ZIMEVideoSwitchLevel[] levels);

	public static native int SetSynchronization(int i_s32ChId, boolean i_bSetSync);

	public static native int SetVideoEncoderAbility(int i_s32ChId, int i_s32NaluNum, int i_s32EncodeProfile);

	public static native int SetLogCallBack();

	public static native int LogExit();

	public static native int SetVideoModeSet();

}


