package com.android.tengfenxiang.util;

import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.Glide;

/**
 * 图片缓存工具类
 * 
 * @author ccz
 * 
 */
public class ImageLoadUtil {

	/**
	 * 清除内存缓存
	 */
	public static void clearMemoryCache(Context context) {
		Glide.get(context).clearMemory();
	}

	/**
	 * 清除本地缓存，必须在子线程中执行
	 * 
	 * @param context
	 */
	public static void clearDiskCache(final Context context) {
		Glide.get(context).clearDiskCache();
	}

	/**
	 * 获取本地缓存的大小
	 * 
	 * @param context
	 * @return
	 */
	public static String getCacheSize(Context context) {
		String path = Glide.getPhotoCacheDir(context).getAbsolutePath();
		return FileSizeUtil.getFileSize(path);
	}

	/**
	 * 检查设备是否存在SDCard
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

}