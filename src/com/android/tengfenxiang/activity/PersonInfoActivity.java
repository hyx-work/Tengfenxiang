package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.PersonInfoListAdapter;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class PersonInfoActivity extends Activity {

	private ListView userInfos;

	private User currentUser;

	private PersonInfoListAdapter adapter;

	private ImageView headImageView;
	private RelativeLayout headLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_info);

		MainApplication application = ((MainApplication) getApplication());
		currentUser = application.getCurrentUser();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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

		loadHead();
		ArrayList<String> infos = new ArrayList<String>();
		infos.add(getString(R.string.phone_number));
		infos.add(getString(R.string.wechat_account));
		infos.add(getString(R.string.nickname));
		infos.add(getString(R.string.gender));
		infos.add(getString(R.string.place));
		infos.add(getString(R.string.alipay_account));
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