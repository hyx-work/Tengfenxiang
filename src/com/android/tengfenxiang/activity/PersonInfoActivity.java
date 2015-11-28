package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.PersonInfoListAdapter;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class PersonInfoActivity extends Activity {

	private ListView userInfos;

	private User currentUser;

	private PersonInfoListAdapter adapter;
	private MainApplication application;

	private ImageView headImageView;
	private RelativeLayout headLayout;
	private TitleBar titleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_info);

		application = ((MainApplication) getApplication());
		currentUser = application.getCurrentUser();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		currentUser = application.getCurrentUser();
		initView();
	}

	private void initView() {
		userInfos = (ListView) findViewById(R.id.person_info);
		headImageView = (ImageView) findViewById(R.id.head);
		headLayout = (RelativeLayout) findViewById(R.id.user_head);
		headLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});

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

		loadHead();
		fillList();
		setItemClick();
	}

	private void setItemClick() {
		userInfos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = null;
				switch (arg2) {
				case 1:
					intent = new Intent(PersonInfoActivity.this,
							EditActivity.class);
					intent.putExtra("attributeName", "wechat");
					intent.putExtra("attributeValue", currentUser.getWechat());
					intent.putExtra("title",
							getString(R.string.edit_wechat_title));
					break;
				case 2:
					break;
				case 3:
					intent = new Intent(PersonInfoActivity.this,
							GenderActivity.class);
					break;
				case 4:
					intent = new Intent(PersonInfoActivity.this,
							ProvinceActivity.class);
					break;
				case 5:
					break;
				case 6:
					break;
				case 7:
					intent = new Intent(PersonInfoActivity.this,
							InviteCodeActivity.class);
					break;
				default:
					break;
				}
				if (null != intent) {
					startActivity(intent);
				}
			}
		});
	}

	private void fillList() {
		ArrayList<String> infos = new ArrayList<String>();
		infos.add(getString(R.string.phone_number));
		infos.add(getString(R.string.wechat_account));
		infos.add(getString(R.string.nickname));
		infos.add(getString(R.string.gender));
		infos.add(getString(R.string.place));
		infos.add(getString(R.string.alipay));
		infos.add(getString(R.string.invite_code));
		infos.add(getString(R.string.invite_others));

		ArrayList<String> values = new ArrayList<String>();
		values.add(currentUser.getPhone());
		values.add(currentUser.getWechat());
		values.add(currentUser.getNickName());
		// values.add(currentUser.getGender());
		// values.add(currentUser.getProvince());
		values.add(currentUser.getAlipay());

		adapter = new PersonInfoListAdapter(this, infos, values);
		userInfos.setAdapter(adapter);
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