package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.SimpleListAdapter;
import com.android.tengfenxiang.util.ImageLoadUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UserInfoActivity extends BaseActivity {

	private ListView userInfoListView;
	private TextView phoneTextView;
	private ImageView headImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initView();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ImageLoadUtil.clearMemoryCache();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}

	private void initView() {
		headImageView = (ImageView) findViewById(R.id.head);
		loadHead();

		phoneTextView = (TextView) findViewById(R.id.phone);
		phoneTextView.setText(currentUser.getPhone());

		userInfoListView = (ListView) findViewById(R.id.user_info_list);
		ArrayList<String> infos = new ArrayList<String>();
		infos.add(getString(R.string.person_info));
		infos.add(getString(R.string.today_signin));
		infos.add(getString(R.string.my_subordinate));
		infos.add(getString(R.string.system_setting));
		infos.add(getString(R.string.system_message));
		infos.add(getString(R.string.about_app));
		SimpleListAdapter adapter = new SimpleListAdapter(
				UserInfoActivity.this, infos);
		userInfoListView.setAdapter(adapter);
		addClickListener();
	}

	private void addClickListener() {
		userInfoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				switch (arg2) {
				case 0:
					intent.setClass(UserInfoActivity.this,
							PersonInfoActivity.class);
					break;
				case 1:
					intent.setClass(UserInfoActivity.this, SigninActivity.class);
					break;
				case 2:
					intent.setClass(UserInfoActivity.this,
							SubordinateActivity.class);
					break;
				case 3:
					intent.setClass(UserInfoActivity.this,
							SettingActivity.class);
					break;
				case 4:
					intent.setClass(UserInfoActivity.this,
							MessageActivity.class);
					break;
				default:
					intent.setClass(UserInfoActivity.this, AboutActivity.class);
					break;
				}
				startActivity(intent);
			}
		});
	}

	private void loadHead() {
		String imageUrl = currentUser.getAvatar();

		// 显示图片的配置
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		ImageLoader.getInstance().loadImage(imageUrl, null, options,
				new SimpleImageLoadingListener() {

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						super.onLoadingComplete(imageUri, view, loadedImage);
						headImageView.setImageBitmap(loadedImage);
					}
				});
	}
}