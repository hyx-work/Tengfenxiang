package com.android.tengfenxiang.util;

import com.android.tengfenxiang.R;
import com.android.volley.NetworkError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import android.content.Context;
import android.widget.Toast;

/**
 * 处理网络请求的错误
 * 
 * @author ccz
 * 
 */
public class VolleyErrorUtil {

	public static void handleVolleyError(Context context, VolleyError error) {
		if (error instanceof NetworkError) {
			Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT)
					.show();
		} else if (error instanceof TimeoutError) {
			Toast.makeText(context, R.string.timeout_error, Toast.LENGTH_SHORT)
					.show();
		} else if (error instanceof ServerError) {
			Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(context, R.string.unknow_error, Toast.LENGTH_SHORT)
					.show();
		}
	}

}