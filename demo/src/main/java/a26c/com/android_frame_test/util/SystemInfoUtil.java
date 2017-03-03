package a26c.com.android_frame_test.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.UUID;

/**
 * android 系统信息工具类
 * 
 * @author jie.li
 * 
 */
public class SystemInfoUtil {

	private static final String TAG = SystemInfoUtil.class.getSimpleName();

	/**
	 * SD卡是否已经绑定上
	 * 
	 * @return
	 */
	public static boolean isSDCardMounted() {
		boolean isSDCard = false;
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			isSDCard = true;
		}
		return isSDCard;
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		if (context == null) {
			return false;
		}

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}

		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return false;
		}

		return networkInfo.isAvailable();
	}

	/**
	 * 
	 * 获取设备的唯一识别号
	 * 
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String wifiMac = info.getMacAddress();
		if (!TextUtils.isEmpty(wifiMac)) {
			return wifiMac;
		}
		String myIMSI = tm.getSubscriberId();
		if (!TextUtils.isEmpty(myIMSI)) {
			return myIMSI;
		}
		String deviceID = tm.getDeviceId();
		if (!TextUtils.isEmpty(deviceID)) {
			return deviceID;
		}
		return UUID.randomUUID().toString().replace("-", "");

	}

	/**
	 * 判断是否在WIFI网络中
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiNetWork(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}

		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}

		if (networkinfo.getType() != ConnectivityManager.TYPE_WIFI) {
			return false;
		}
		return true;
	}

	/**
	 * get the device mode
	 * 
	 * @return
	 */
	public static String getDeviceMode() {
		Build bd = new Build();
		String model = Build.MODEL;
		return model;
	}

	/**
	 * get the current phone number.
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getPhoneNumber(Context ctx) {
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getLine1Number();
	}


	/**
	 * 获取手机的MAC地址
	 * @return
	 */
	public static String getMac(){
		String str="";
		String macSerial="";
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (macSerial == null || "".equals(macSerial)) {
			try {
				return loadFileAsString("/sys/class/net/eth0/address")
						.toUpperCase().substring(0, 17);
			} catch (Exception e) {
				e.printStackTrace();

			}

		}
		return macSerial;
	}
	public static String loadFileAsString(String fileName) throws Exception {
		FileReader reader = new FileReader(fileName);
		String text = loadReaderAsString(reader);
		reader.close();
		return text;
	}
	public static String loadReaderAsString(Reader reader) throws Exception {
		StringBuilder builder = new StringBuilder();
		char[] buffer = new char[4096];
		int readLength = reader.read(buffer);
		while (readLength >= 0) {
			builder.append(buffer, 0, readLength);
			readLength = reader.read(buffer);
		}
		return builder.toString();
	}
}
