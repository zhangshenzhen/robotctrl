package zime.ui;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import zime.media.ZIMEVideoClientJNI;
import zime.media.ZIMEVideoClientJNI.T_ZIMEVideoSwitchLevel;

public class ZIMEConfig
{
	public String mFileName = "/sdcard/Config.txt";
	public String mRecvIP = "127.0.0.1";
	public int mAudioCodecId = 0;
	public int mAudioRTPport = 8500 + 0;
	public int mAudioRTCPport = mAudioRTPport + 1;
	public int mVideoRTPport = mAudioRTPport + 2;
	public int mVideoRTCPport = mAudioRTPport + 3;
	public int mWidth = 640;
	public int mHeight = 480;

	public int mFrameRate = 10;
	public int mInitBitRate = 300000;
	public int mMaxBitRate	= 8000000;
	public int mBitrate = -1;
	public int mNaluNum = 2;
	public int mChannelId = -1;
	public boolean mbConstantBps = false;	 // video 
	public int ms32IframeInterval = 0;

	public String mVideoCodecLevel_qvga = null;
	public String mVideoCodecLevel_vga = null;
	public String mVideoCodecLevel_720p = null;


	private T_ZIMEVideoSwitchLevel[] ParseStringofOneQualitySet(String line)
	{
		int w = 320;
		int h = 240;

		String aLine[] = line.split(":");
		if (aLine[0].compareToIgnoreCase("qvag") == 0)
		{
			w = 320;
			h = 240;
		}
		else if(aLine[0].compareToIgnoreCase("cif") == 0)
		{
			w = 352;
			h = 288;
		}
		else if(aLine[0].compareToIgnoreCase("4cif") == 0)
		{
			w = 704;
			h = 576;
		}
		else if (aLine[0].compareToIgnoreCase("vga") == 0)
		{
			w = 640;
			h = 480;
		}
		else if (aLine[0].compareToIgnoreCase("192p") == 0)
		{
			w = 336;
			h = 192;
		}
		else if (aLine[0].compareToIgnoreCase("288p") == 0)
		{
			w = 512;
			h = 288;
		}
		else if (aLine[0].compareToIgnoreCase("448p") == 0)
		{
			w = 768;
			h = 448;
		}
		else if (aLine[0].compareToIgnoreCase("576p") == 0)
		{
			w = 1024;
			h = 576;
		}
		else if (aLine[0].compareToIgnoreCase("1024*768") == 0)
		{
			w = 1024;
			h = 768;
		}
		else if (aLine[0].compareToIgnoreCase("720p") == 0)
		{
			w = 1280;
			h = 720;
		}
		else if (aLine[0].compareToIgnoreCase("1080p") == 0)
		{
			w = 1920;
			h = 1080;
		}

		String strFrameBpsSet[] = aLine[1].split("-");

		ZIMEVideoClientJNI.T_ZIMEVideoSwitchLevel[] levels = new ZIMEVideoClientJNI.T_ZIMEVideoSwitchLevel[strFrameBpsSet.length];
		if(strFrameBpsSet.length == 0)
		{
			return null;
		}
		int i = 0;
		for(i = 0; i < strFrameBpsSet.length; i++)
		{
			String aFrameBps[] = strFrameBpsSet[i].split("/");
			Log.e("ZIMEConfig", "ZIMEConfig w = " + w + " frame  = " + Integer.parseInt(aFrameBps[0]) + " bps = " + Integer.parseInt(aFrameBps[1]));
			levels[i] = new ZIMEVideoClientJNI.T_ZIMEVideoSwitchLevel(w, h, Integer.parseInt(aFrameBps[0]), Integer.parseInt(aFrameBps[1]));

		}

		return levels;
	}

	public T_ZIMEVideoSwitchLevel[] GetAllResOfQualitySet()
	{
		//校验档位，当某档位或多个档位设置为空时也可以设置成功
		int destpos = 0;
		T_ZIMEVideoSwitchLevel[] AllResofQulityLevels = null;
		if(mbConstantBps)
		{
			AllResofQulityLevels    = new T_ZIMEVideoSwitchLevel[1];
			AllResofQulityLevels[0] = new T_ZIMEVideoSwitchLevel(0, 0, 0, 0);
			AllResofQulityLevels[0].s32Width   = mWidth;
			AllResofQulityLevels[0].s32Height  = mHeight;
			AllResofQulityLevels[0].s32Bitrate = mInitBitRate / 1000;
			AllResofQulityLevels[0].s32Framerate = mFrameRate;

			Log.e("ZIMECONFIG", "lw---------width=" + mWidth + ",height=" + mHeight);
		}
		else
		{
			T_ZIMEVideoSwitchLevel[] levels_qvga = null;
			T_ZIMEVideoSwitchLevel[] levels_vga = null;
			T_ZIMEVideoSwitchLevel[] levels_720p = null;
			int nLen_Levels_qvga = 0;
			int nLen_Levels_vga = 0;
			int nLen_Levels_720p = 0;
			if(!mVideoCodecLevel_qvga.isEmpty())
			{
				levels_qvga = ParseStringofOneQualitySet(mVideoCodecLevel_qvga);
				nLen_Levels_qvga = levels_qvga.length;
			}

			if(!mVideoCodecLevel_vga.isEmpty())
			{
				levels_vga = ParseStringofOneQualitySet(mVideoCodecLevel_vga);
				nLen_Levels_vga = levels_vga.length;
			}

			if(!mVideoCodecLevel_720p.isEmpty())
			{
				levels_720p = ParseStringofOneQualitySet(mVideoCodecLevel_720p);
				nLen_Levels_720p = levels_720p.length;
			}

			int nNum = nLen_Levels_qvga + nLen_Levels_vga + nLen_Levels_720p;
			AllResofQulityLevels = new T_ZIMEVideoSwitchLevel[nNum];
			for(int i = 0; i < nNum; i++)
			{
				AllResofQulityLevels[i] = new T_ZIMEVideoSwitchLevel(0, 0, 0, 0);
			}

			if(nLen_Levels_qvga != 0)
			{
				System.arraycopy(levels_qvga, 0, AllResofQulityLevels, destpos, nLen_Levels_qvga);
			}
			destpos += nLen_Levels_qvga;

			if(nLen_Levels_vga != 0)
			{
				System.arraycopy(levels_vga, 0, AllResofQulityLevels, destpos, nLen_Levels_vga);
			}
			destpos += nLen_Levels_vga;

			if(nLen_Levels_720p != 0)
			{
				System.arraycopy(levels_720p, 0, AllResofQulityLevels, destpos, nLen_Levels_720p);
			}
			destpos += nLen_Levels_720p;
		}
		return AllResofQulityLevels;

	}

	public static final int enumZIME_VQE_SCENE_MOBILEPHONE = 0x0;
	public static final int enumZIME_VQE_SCENE_TV = 0x1;
	public static int mVQEScene = enumZIME_VQE_SCENE_MOBILEPHONE;	// AEC

	// control
	@SuppressLint("SdCardPath")
	public final static String mPicPath = "/sdcard/PicAsCam.bmp";
	public static boolean mIsOnlyAudio = false;
	public static int mCameraId = 0;

	public static  int mEncodeProfile = 1;
	public static boolean mPauseVideo = false;
	public static boolean mMute = false;
	public static boolean mAEC 	= true;
	public static boolean mFEC  = true;
	public static boolean mNACK = false;
	public static boolean mNS   = true;
	public static boolean mSourceFilter = false;
	public static int mCodecType = 0;
	public static int mVendorType = 0;
	public static boolean mUsingEncodeCAllback = false;
	public static boolean mIsH265 = false;
	public static boolean mInbandDTMF = true;
	public static boolean mIsExTrans = false;
	public static boolean mIsLogCallBack = false;
	public static boolean mVBR = false;
	public static boolean mSync = true;
	public static boolean mSaveH26XFile = false;

	public Object mRemoteGLSurface = null;
	public Object mLocalSurfaceHolder  = null;

	public static final int SET_PARAM = 0x1;
	public static final int SET_PARAM_RUNTIME = 0x2;
	public static final int START = 0x3;
	public static final int STOP = 0x4;
	public static final int EXIT = 0x5;

	public static final int UPDATE_CONF = 0x6;
	public static final int STARTSEND   = 0x7;
	public static final int STARTRECV   = 0x8;
	public static final int SENDDTMF    = 0x9;

	public static final int ASTART_BY_AVCLIENTINTERFACE    = 0xA;
	public static final int TOA     = 0xB;
	public static final int TOAV    = 0xC;
	public static final int SET_VIDEOPARAM = 0xD;
	public static final int SET_AUDIOPARAM_BEFORSTART = 0xE;

	//codectype 用于demo区别注册编解码回调
	public static final int enumZIME_GOTAEXTERNENCODER  = 0x0;
	public static final int enumZIME_AMLOGICHARDWEAR    = 0x1;
	public static final int enumZIME_MediaCodec = 0x2;

	//VendorType 用于告诉引擎是否需要给当前的设备商做特殊流程处理
	public static final int enumZIME_GENERA_VENDORTYPE = 0x0;


	public static final int ZIME_DISABLE_VBR = 0x0;
	public static final int ZIME_ENABLE_VBR  = 0x1;

	public static final int enumZIME_MOBILECALL_SCENE = 0x1;
	public static final int enumZIME_MOVE_SCENE  = 0x2;



	public ZIMEConfig()
	{
		mRecvIP = getLocalIpAddress();
		ReadConfigFromSDFile(mFileName);
	}


	private boolean isValidIpAddr(byte i_abyte[])
	{
		if(i_abyte.length == 4)
		{
			return true;
		}

		return false;
	}

	public String getLocalIpAddress() {
		String localIp = "127.0.0.1";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr =
					 intf.getInetAddresses();
					 enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						// Set the remote ip address the same as
						// the local ip address of the last netif
						byte aByte[] = inetAddress.getAddress();
						if(isValidIpAddr(aByte))
						{
							localIp = inetAddress.getHostAddress().toString();
							return localIp;
						}
					}
				}
			}
		} catch (SocketException e) {
			Log.e("ZIMEConfig", "Unable to get local IP address. Not the end of the world", e);
		}
		return localIp;
	}



	public void WriteConfigToSDFile() {
		try{

			FileOutputStream fout = new FileOutputStream(mFileName);

			String line;
			line = "peer IP:" + mRecvIP + "\n";
			line += "port:" + Integer.toString(mAudioRTPport) + "\n";
			line += "IframeInterval:" + Integer.toString(ms32IframeInterval) + "\n";

			byte [] bytes = line.getBytes();
			fout.write(bytes);
			fout.close();
		}

		catch(Exception e){
			e.printStackTrace();
		}
	}



	public void ReadConfigFromSDFile(String fileName){

		try{
			FileInputStream fin = new FileInputStream(fileName);
			BufferedReader dataIO = new BufferedReader(new InputStreamReader(fin));
			String line = null;
			while((line =  dataIO.readLine()) != null) {
				String aLine[] = line.split(":");
				if (aLine[0].compareToIgnoreCase("peer IP") == 0)
				{
					mRecvIP = aLine[1];
					mRecvIP = "192.168.100.9";
					if(aLine[1].compareToIgnoreCase("127.0.0.1") == 0){
						mRecvIP = getLocalIpAddress();
					}
				}
				else if (aLine[0].compareToIgnoreCase("port") == 0)
				{
					mAudioRTPport = Integer.parseInt(aLine[1]);
					mAudioRTCPport = mAudioRTPport + 1;
					mVideoRTPport = mAudioRTPport + 2;
					mVideoRTCPport = mAudioRTPport + 3;
				}
				else if (aLine[0].compareToIgnoreCase("IframeInterval") == 0)
				{
					ms32IframeInterval = Integer.parseInt(aLine[1]);
				}

			}

			dataIO.close();
			fin.close();
		}

		catch(Exception e){
			e.printStackTrace();
		}
		return ;
	}


}
