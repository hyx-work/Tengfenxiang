package com.android.tengfenxiang.activity;

import java.util.HashMap;
import java.util.Map;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.util.ResponseTools;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        RequestManager.init(getApplication());
        login(getApplication());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void login(final Context context){
        StringRequest stringRequest = new StringRequest(Method.POST, Constant.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            	System.err.println(response);
            	MainApplication application = ((MainApplication) getApplication());
        		User user = (User) ResponseTools.handleResponse(getApplication(), response, User.class);
        		application.setCurrentUser(user);
        		Intent intent = new Intent(MainActivity.this, MyProfitActivity.class);
                startActivity(intent);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // responseText.setText(error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("phone", "13826473672");
				map.put("password", "testtest");
				map.put("deviceId", "1");
				map.put("deviceInfo", "1");
				map.put("pushToken", "22");
				map.put("appVersion", "1.0.0");
				map.put("os", "android");
				map.put("osVersion", "4.4.4");
				map.put("model", "model");
                return map;
            }
        };
        RequestManager.getRequestQueue().add(stringRequest);
    }
}