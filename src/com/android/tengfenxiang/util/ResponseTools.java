package com.android.tengfenxiang.util;

import android.content.Context;
import android.widget.Toast;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.ResponseResult;
import com.google.gson.Gson;

/**
 * 处理通用响应
 * 
 * @author ccz
 * 
 */
public class ResponseTools {

	/**
	 * 处理通用响应
	 * 
	 * @param context
	 * @param response
	 */
	public static void handleCommonResponse(Context context, String response) {
		Gson gson = new Gson();
		ResponseResult result = gson.fromJson(response, ResponseResult.class);

		// 如果不包含data属性则提示未知错误
		if (null == result.getData()) {
			Toast.makeText(context,
					context.getResources().getString(R.string.unknown_error),
					Toast.LENGTH_SHORT).show();
		}
		// 直接显示服务器返回的错误信息
		else {
			Toast.makeText(context, result.getData().toString(),
					Toast.LENGTH_SHORT).show();
		}
	}

}