package com.android.tengfenxiang.util;

import java.io.File;

import android.content.Context;
import android.widget.Toast;

import com.android.tengfenxiang.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * 图片缓存工具类
 * @author ccz
 *
 */
public class ImageLoadTools {
	/**
	 * 初始化ImageLoader对象
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		// 缓存文件的目录为：sdcard/tengfenxiang/Cache
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "tengfenxiang/Cache");
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				// 保存的每个缓存文件的最大长宽 
				.memoryCacheExtraOptions(480, 800)
				// 线程池大小
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				// 最多支持缓存100Mb到内存卡中
				.diskCacheSize(100 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.diskCache(new UnlimitedDiscCache(cacheDir))
				.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
				.writeDebugLogs()
				.build();
		//全局初始化此配置  
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 清除内存缓存
	 */
	public void clearMemoryCache() {
    	ImageLoader.getInstance().clearMemoryCache();
	}

	/**
	 * 清除本地缓存
	 * @param context
	 */
	public static void clearDiskCache(Context context) {
		Toast.makeText(context, context.getString(R.string.clear_cache_success), Toast.LENGTH_SHORT).show();
		ImageLoader.getInstance().clearDiskCache();
    }

	/**
	 * 获取本地缓存的大小
	 * @param context
	 * @return
	 */
	public static String getCacheSize(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "tengfenxiang/Cache");
		return FileSizeTools.getFileSize(cacheDir.getPath());
	}
}
