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
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceInfo() {
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
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

}