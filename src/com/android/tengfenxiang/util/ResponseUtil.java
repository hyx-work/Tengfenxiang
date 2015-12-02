package com.android.tengfenxiang.util;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.tengfenxiang.bean.ResponseResult;

/**
 * 处理通用响应
 * 
 * @author ccz
 * 
 */
public class ResponseUtil {

	/**
	 * 处理服务器返回的响应信息
	 * 
	 * @param context
	 * @param response
	 *            响应的json字符串
	 * @param bean
	 *            返回结果的类类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object handleResponse(Context context, String response,
			@SuppressWarnings("rawtypes") Class bean) {

		// 解析返回结果，取出code和data字段
		ResponseResult result = JSON
				.parseObject(response, ResponseResult.class);

		// 如果data字段为空则直接返回
		if (null == result.getData()) {
			return null;
		}

		String data = result.getData().toString();
		// 如果返回的code=200，说明没有错误，需要取出data信息
		if (200 == result.getCode()) {
			if (null == bean)
				return new Object();
			else
				return JSON.parseObject(data, bean);
		}

		// 提示错误信息
		Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
		return null;
	}
}