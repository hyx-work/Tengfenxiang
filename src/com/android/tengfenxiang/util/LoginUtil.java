package com.android.tengfenxiang.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.tengfenxiang.R;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.ResponseResult;
import com.android.tengfenxiang.bean.Setting;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.db.UserDao;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
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
	private SharedPreferences preferences;
	private static UserDao userDao;
	private ChannelUtil channelUtil;

	private final int HANDLE_LOGIN_SUCCESS = 0;
	private final int HANDLE_LOGIN_FAIL = 1;
	private final int HANDLE_LOGIN_ERROR = 2;

	private VolleyError volleyError;

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_LOGIN_SUCCESS:
				if (null != onLoginListener) {
					onLoginListener.onLoginSuccess();
				}
				break;

			case HANDLE_LOGIN_FAIL:
				if (null != onLoginListener) {
					onLoginListener.onLoginFail();
				}
				break;

			case HANDLE_LOGIN_ERROR:
				if (null != onLoginListener) {
					onLoginListener.onLoginError(volleyError);
				}
				break;

			default:
				break;
			}
		};
	};

	private LoginUtil(Context context) {
		this.context = context;
		preferences = context.getSharedPreferences(context.getPackageName(),
				Context.MODE_PRIVATE);
		channelUtil = ChannelUtil.getInstance(context);
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

					// 缓存完数据去获取静态配置数据
					getConfig();
				} else {
					Toast.makeText(context, result.getData().toString(),
							Toast.LENGTH_SHORT).show();
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							// 登录失败，则用null初始化文章频道
							channelUtil.initData(null);
							handler.sendEmptyMessage(HANDLE_LOGIN_FAIL);
						}
					}).start();
				}
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(final VolleyError error) {
				VolleyErrorUtil.handleVolleyError(context, error);
				new Thread(new Runnable() {
					public void run() {
						// 登录失败，则用null初始化文章频道
						channelUtil.initData(null);
						volleyError = error;
						handler.sendEmptyMessage(HANDLE_LOGIN_ERROR);
					}
				}).start();
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

	/**
	 * 获取静态配置文件
	 */
	private void getConfig() {
		String url = Constant.CONFIG_URL;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				final Setting setting = JSON.parseObject(response,
						Setting.class);
				if (null == setting) {
					Toast.makeText(context, R.string.fail_to_get_config,
							Toast.LENGTH_SHORT).show();
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							// 利用获取到的静态配置信息，初始化本地的文章类型
							channelUtil.initData(setting);
							// 初始完本地配置数据再回调监听
							// 如果没有成功获取到配置文件，也视为登录失败
							handler.sendEmptyMessage(HANDLE_LOGIN_FAIL);
						}
					}).start();
				} else {
					// 缓存本次获取到的转换率
					Editor editor = preferences.edit();
					editor.putFloat("pointsToCashRate",
							(float) setting.getPointsToCashRate());
					editor.commit();
					((MainApplication) context).setSetting(setting);

					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							// 利用获取到的静态配置信息，初始化本地的文章类型
							channelUtil.initData(setting);
							// 初始完本地配置数据再回调监听
							handler.sendEmptyMessage(HANDLE_LOGIN_SUCCESS);
						}
					}).start();

				}
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyErrorUtil.handleVolleyError(context, error);
				// 调用登录失败的回调函数
				if (null != onLoginListener) {
					onLoginListener.onLoginError(error);
				}
			}
		};
		StringRequest stringRequest = new StringRequest(url, listener,
				errorListener) {
			@Override
			protected Response<String> parseNetworkResponse(
					NetworkResponse response) {
				// TODO Auto-generated method stub
				// 重写parseNetworkResponse方法，解决中文乱码问题
				String str = null;
				try {
					str = new String(response.data, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return Response.success(str,
						HttpHeaderParser.parseCacheHeaders(response));
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