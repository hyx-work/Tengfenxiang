package com.android.tengfenxiang.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

public class DeviceInfoUtil {

	private static DeviceInfoUtil deviceInfoUtil;

	/**
	 * 设备ID
	 */
	private String deviceId;

	private String deviceInfo;

	private String pushToken;

	/**
	 * 软件的版本号
	 */
	private String appVersion;

	/**
	 * 系统类型，Android
	 */
	private String os;

	/**
	 * 系统版本号
	 */
	private String osVersion;

	/**
	 * 手机型号
	 */
	private String model;

	private DeviceInfoUtil(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		deviceId = tm.getDeviceId();
		deviceInfo = android.os.Build.MODEL;
		if (null != pi) {
			appVersion = pi.versionName;
		}
		os = "Android";
		osVersion = android.os.Build.VERSION.RELEASE;
		model = android.os.Build.MODEL;
	}

	/**
	 * 单例模式
	 * 
	 * @param context
	 * @return
	 */
	public static DeviceInfoUtil getInstance(Context context) {
		if (null == deviceInfoUtil) {
			deviceInfoUtil = new DeviceInfoUtil(context);
		}
		return deviceInfoUtil;
	}

	public String getDeviceId() {
		// 读取不到信息时置为null
		if (null == deviceId || deviceId.equals("")) {
			deviceId = "null";
		}
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceInfo() {
		// 读取不到信息时置为null
		if (null == deviceInfo || deviceInfo.equals("")) {
			deviceInfo = "null";
		}
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getPushToken() {
		return pushToken;
	}

	public void setPushToken(String pushToken) {
		this.pushToken = pushToken;
	}

	public String getAppVersion() {
		// 读取不到信息时置为null
		if (null == appVersion || appVersion.equals("")) {
			appVersion = "null";
		}
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsVersion() {
		// 读取不到信息时置为null
		if (null == osVersion || osVersion.equals("")) {
			osVersion = "null";
		}
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getModel() {
		// 读取不到信息时置为null
		if (null == model || model.equals("")) {
			model = "null";
		}
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

}