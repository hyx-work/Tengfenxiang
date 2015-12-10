package com.android.tengfenxiang.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 位图压缩工具类，用于压缩缩略图，因为微信分享时缩略图大于32kb会报错
 * 
 * @author ccz
 * 
 */
public class BitmapCompressUtil {

	public static Bitmap compressImage(Bitmap bitmap, double maxSize) {
		double mid = bitmap.getByteCount() / 1024;
		if (mid > maxSize) {
			double i = mid / maxSize;
			bitmap = zoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i),
					bitmap.getHeight() / Math.sqrt(i));
		}
		return bitmap;
	}

	private static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}

}