package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.SimpleListAdapter;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UserInfoActivity extends BaseActivity {

	private ListView userInfoListView;
	private TextView nicknameTextView;
	private ImageView headImageView;
	private LinearLayout headLayout;

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
	protected void onSaveInstanceState(Bundle outState) {

	}

	private void initView() {
		headLayout = (LinearLayout) findViewById(R.id.head_layout);
		headLayout.setBackground(getResources().getDrawable(
				R.drawable.user_head_bg));

		// 初始化头像的显示
		headImageView = (ImageView) findViewById(R.id.head);
		loadHead();

		// 初始化昵称的显示
		nicknameTextView = (TextView) findViewById(R.id.nickname);
		nicknameTextView.setText(currentUser.getNickName());

		// 初始化用户信息列表
		userInfoListView = (ListView) findViewById(R.id.user_info_list);
		ArrayList<Integer> icons = new ArrayList<Integer>();
		icons.add(R.drawable.ic_data);
		icons.add(R.drawable.ic_signin);
		icons.add(R.drawable.ic_subordinate);
		icons.add(R.drawable.ic_message);
		icons.add(R.drawable.ic_invitation);
		icons.add(R.drawable.ic_set);
		icons.add(R.drawable.ic_info);
		ArrayList<String> infos = new ArrayList<String>();
		infos.add(getString(R.string.person_info));
		infos.add(getString(R.string.today_signin));
		infos.add(getString(R.string.my_subordinate));
		infos.add(getString(R.string.system_message));
		infos.add(getString(R.string.invite_others));
		infos.add(getString(R.string.system_setting));
		infos.add(getString(R.string.about_app));
		SimpleListAdapter adapter = new SimpleListAdapter(icons, infos,
				UserInfoActivity.this);
		userInfoListView.setAdapter(adapter);
		// 设置用户信息列表的点击监听
		addClickListener();
	}

	/**
	 * 设置用户信息列表的点击监听
	 */
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
							MessageActivity.class);
					break;
				case 4:
					intent.setClass(UserInfoActivity.this,
							InviteCodeActivity.class);
					break;
				case 5:
					intent.setClass(UserInfoActivity.this,
							SettingActivity.class);
					break;
				default:
					intent.setClass(UserInfoActivity.this, AboutActivity.class);
					break;
				}
				startActivityForResult(intent, 0);
			}
		});
	}

	/**
	 * 加载用户头像
	 */
	private void loadHead() {
		String imageUrl = currentUser.getAvatar();

		// 显示图片的配置
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				// 由于服务器修改头像后返回的url不变，所以这里不设置本地缓存
				// 如果后续修改为url可变，这将这行代码反注销即可
				// .cacheOnDisk(true)
				.cacheInMemory(true)
				.showImageOnLoading(R.drawable.default_head)
				.showImageForEmptyUri(R.drawable.default_head)
				.showImageOnFail(R.drawable.default_head).build();

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