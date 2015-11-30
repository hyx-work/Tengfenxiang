package com.android.tengfenxiang.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.SimpleListAdapter;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class AboutActivity extends Activity {

	private ListView linkListView;
	private ListView simpleListView;
	private TitleBar titleBar;

	private List<String> qqGroup = new ArrayList<String>();
	private String phone = "";

	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		dialog = new LoadingDialog(this);
		dialog.showDialog();
		getInfo();
	}

	private void initView() {
		linkListView = (ListView) findViewById(R.id.link_list);
		simpleListView = (ListView) findViewById(R.id.simple_list);
		fillLinkList();
		fillSimpleList();

		titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setOnClickListener(new OnTitleClickListener() {

			@Override
			public void OnClickLeft() {
				finish();
			}

			@Override
			public void OnClickRight() {

			}
		});
	}

	private void fillSimpleList() {
		ArrayList<String> infos = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		infos.add(getString(R.string.qq_group));
		infos.add(getString(R.string.contact_phone));
		values.add(getQQGroupInfo());
		values.add(phone);
		SimpleListAdapter adapter = new SimpleListAdapter(AboutActivity.this,
				infos, values);
		simpleListView.setAdapter(adapter);

		simpleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (0 == arg2) {
					showDialog();
				} else {
					Uri uri = Uri.parse("tel:" + phone);
					Intent intent = new Intent(Intent.ACTION_DIAL, uri);
					startActivity(intent);
				}
			}
		});
	}

	private void showDialog() {
		Builder builder = new Builder(AboutActivity.this);
		builder.setTitle(R.string.select_qq_number);

		int size = qqGroup.size();
		final String[] cities = (String[]) qqGroup.toArray(new String[size]);

		builder.setItems(cities, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				cmb.setPrimaryClip(ClipData.newPlainText(null, cities[which]));
				Toast.makeText(getApplication(), R.string.copy_success,
						Toast.LENGTH_SHORT).show();
			}
		});
		builder.show();
	}

	private void fillLinkList() {
		ArrayList<String> infos = new ArrayList<String>();
		infos.add(getString(R.string.function_introduction));
		infos.add(getString(R.string.help_center));
		infos.add(getString(R.string.feedback));
		infos.add(getString(R.string.score));
		SimpleListAdapter adapter = new SimpleListAdapter(AboutActivity.this,
				infos);
		linkListView.setAdapter(adapter);

		linkListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent;
				switch (arg2) {
				case 0:
					intent = new Intent(AboutActivity.this, WebActivity.class);
					intent.putExtra("title",
							getString(R.string.function_introduction));
					break;
				case 1:
					intent = new Intent(AboutActivity.this, WebActivity.class);
					intent.putExtra("title", getString(R.string.help_center));
					break;
				case 2:
					intent = new Intent(AboutActivity.this, WebActivity.class);
					intent.putExtra("title", getString(R.string.feedback));
					break;
				default:
					Uri uri = Uri.parse("market://details?id="
							+ getPackageName());
					intent = new Intent(Intent.ACTION_VIEW, uri);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					break;
				}
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(getApplication(), R.string.no_app_market,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private String getQQGroupInfo() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < qqGroup.size(); i++) {
			buffer.append(qqGroup.get(i)).append("  ");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		return buffer.toString();
	}

	private void getInfo() {
		String url = Constant.SETTING_URL;

		// 请求成功的回调函数
		Listener<JSONObject> listener = new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				try {
					JSONObject tmp = response
							.getJSONObject("aboutViewSettings");
					phone = tmp.getString("phone");
					JSONArray array = tmp.getJSONArray("QQGroups");
					for (int i = 0; i < array.length(); i++) {
						qqGroup.add(array.getString(i));
					}
				} catch (JSONException e) {
					Toast.makeText(getApplication(), R.string.unknow_error,
							Toast.LENGTH_SHORT).show();
				}
				initView();
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				Toast.makeText(getApplication(), R.string.unknow_error,
						Toast.LENGTH_SHORT).show();
			}
		};
		JsonObjectRequest stringRequest = new JsonObjectRequest(url, null,
				listener, errorListener);
		RequestManager.getRequestQueue().add(stringRequest);
	}
}