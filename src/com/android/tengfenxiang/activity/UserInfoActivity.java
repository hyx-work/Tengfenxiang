package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.SimpleListAdapter;
import com.android.tengfenxiang.bean.Setting;
import com.android.tengfenxiang.util.ListViewUtil;
import com.bumptech.glide.Glide;

public class UserInfoActivity extends BaseActivity {

	private ListView userInfoListView;
	private TextView nicknameTextView;
	private TextView pointTextView;
	private ImageView headImageView;

	private Setting setting;
	private int cash;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);

		setting = application.getSetting();
		cash = application.getCurrentUser().getWithdrawableCash();
		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
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
		// 初始化头像的显示
		headImageView = (ImageView) findViewById(R.id.head);
		loadHead();

		// 初始化昵称的显示
		nicknameTextView = (TextView) findViewById(R.id.nickname);
		nicknameTextView.setText(application.getCurrentUser().getNickName());

		// 初始化银两的显示
		pointTextView = (TextView) findViewById(R.id.points);
		float pointsToCashRate = 0.1f;
		if (null != setting) {
			pointsToCashRate = (float) setting.getPointsToCashRate();
		} else {
			pointsToCashRate = preferences.getFloat("pointsToCashRate", 0.02f);
		}
		int point = (int) (cash / pointsToCashRate);
		pointTextView.setText(getString(R.string.current_point) + point);

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
		ListViewUtil.setListViewHeightBasedOnChildren(userInfoListView);
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
		String imageUrl = application.getCurrentUser().getAvatar();
		Glide.with(this.getApplicationContext()).load(imageUrl).centerCrop()
				.crossFade().into(headImageView);
	}

	/**
	 * 复写返回键，如果是点击了返回键则用父activity处理点击事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return getParent().onKeyDown(keyCode, event);
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

}