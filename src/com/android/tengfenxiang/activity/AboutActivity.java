package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.LinkListAdapter;
import com.android.tengfenxiang.adapter.SimpleListAdapter;
import com.android.tengfenxiang.util.Constant;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		initView();
	}

	private void initView() {
		linkListView = (ListView) findViewById(R.id.link_list);
		simpleListView = (ListView) findViewById(R.id.simple_list);
		fillLinkList();
		fillSimpleList();
	}

	private void fillSimpleList() {
		ArrayList<String> infos = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		infos.add(getString(R.string.qq_group));
		infos.add(getString(R.string.contact_phone));
		values.add(Constant.QQ_GROUP);
		values.add(Constant.CONTACT_PHONE);
		SimpleListAdapter adapter = new SimpleListAdapter(AboutActivity.this,
				infos, values);
		simpleListView.setAdapter(adapter);

		simpleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (0 == arg2) {
					ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					cmb.setPrimaryClip(ClipData.newPlainText(null,
							Constant.QQ_GROUP));
					Toast.makeText(getApplication(), R.string.copy_success,
							Toast.LENGTH_SHORT).show();
				} else {
					Uri uri = Uri.parse("tel:" + Constant.CONTACT_PHONE);
					Intent intent = new Intent(Intent.ACTION_DIAL, uri);
					startActivity(intent);
				}
			}
		});
	}

	private void fillLinkList() {
		ArrayList<String> infos = new ArrayList<String>();
		infos.add(getString(R.string.function_introduction));
		infos.add(getString(R.string.help_center));
		infos.add(getString(R.string.feedback));
		infos.add(getString(R.string.score));
		LinkListAdapter adapter = new LinkListAdapter(AboutActivity.this, infos);
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
}