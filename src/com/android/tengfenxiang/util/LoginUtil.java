package com.android.tengfenxiang.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.ResponseResult;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.db.UserDao;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

/**
 * 账户登录工具类
 * 
 * @author ccz
 * 
 */
public class LoginUtil {

	private static LoginUtil loginUtil;
	private Context context;
	private OnLoginListener onLoginListener;

	private static UserDao userDao;

	private LoginUtil(Context context) {
		this.context = context;
	}

	public static LoginUtil getInstance(Context context) {
		if (null == loginUtil) {
			loginUtil = new LoginUtil(context);
			userDao = UserDao.getInstance(context);
		}
		return loginUtil;
	}

	public void login(final String phone, final String password) {
		String url = Constant.LOGIN_URL;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				ResponseResult result = JSON.parseObject(response,
						ResponseResult.class);

				// 如果返回code=200说明登录成功
				if (null != result && result.getCode() == 200) {
					User user = (User) ResponseUtil.handleResponse(context,
							response, User.class);
					// 登录成功设置User对象
					((MainApplication) context).setCurrentUser(user);

					// 将登录的用户信息缓存到本地数据库
					User tmp = userDao.findUser(phone);
					if (null == tmp) {
						userDao.insert(user);
					} else {
						userDao.update(user);
					}

					if (null != onLoginListener) {
						onLoginListener.onLoginSuccess();
					}
				} else {
					Toast.makeText(context, result.getData().toString(),
							Toast.LENGTH_SHORT).show();
					if (null != onLoginListener) {
						onLoginListener.onLoginFail();
					}
				}
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyErrorUtil.handleVolleyError(context, error);
				if (null != onLoginListener) {
					onLoginListener.onLoginError(error);
				}
			}
		};
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				DeviceInfoUtil util = DeviceInfoUtil.getInstance(context);
				map.put("phone", phone);
				map.put("password", password);
				map.put("deviceId", util.getDeviceId());
				map.put("deviceInfo", util.getDeviceInfo());
				if (util.getPushToken() != null
						&& !util.getPushToken().equals("")) {
					map.put("pushToken", util.getPushToken());
				}
				map.put("appVersion", util.getAppVersion());
				map.put("os", util.getOs());
				map.put("osVersion", util.getOsVersion());
				map.put("model", util.getModel());
				return map;
			}
		};
		RequestUtil.getRequestQueue(context).add(stringRequest);
	}

	public OnLoginListener getOnLoginListener() {
		return onLoginListener;
	}

	public void setOnLoginListener(OnLoginListener onLoginListener) {
		this.onLoginListener = onLoginListener;
	}

	/**
	 * 登录结果回调接口
	 * 
	 * @author ccz
	 * 
	 */
	public static interface OnLoginListener {

		/**
		 * 登录成功的回调接口
		 */
		public void onLoginSuccess();

		/**
		 * 登录失败的回调接口
		 */
		public void onLoginFail();

		/**
		 * 登录出错的回调接口
		 */
		public void onLoginError(VolleyError error);
	}
}