package com.android.tengfenxiang.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.PersonInfoListAdapter;
import com.android.tengfenxiang.bean.CityInfo;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.db.UserDao;
import com.android.tengfenxiang.util.CityUtil;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.ImageLoadUtil;
import com.android.tengfenxiang.util.MultipartEntity;
import com.android.tengfenxiang.util.MultipartRequest;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.util.VolleyErrorUtil;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

public class PersonInfoActivity extends BaseActivity {

	private ListView userInfos;
	private PersonInfoListAdapter adapter;
	private ImageView headImageView;
	private RelativeLayout headLayout;
	private TitleBar titleBar;
	private UserDao userDao;

	/**
	 * 长和宽设置为320，设置为480时某些机型会出错
	 */
	private static int output_X = 320;
	private static int output_Y = 320;

	private static final int CODE_GALLERY_REQUEST = 0xa0;
	private static final int CODE_CAMERA_REQUEST = 0xa1;
	private static final int CODE_RESULT_REQUEST = 0xa2;

	private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_info);
		userDao = UserDao.getInstance(getApplication());
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
				showDialog();
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
					intent.putExtra("attributeName", "email");
					intent.putExtra("attributeValue", application
							.getCurrentUser().getEmail());
					intent.putExtra("title",
							getString(R.string.edit_email_title));
					break;
				case 2:
					intent = new Intent(PersonInfoActivity.this,
							EditActivity.class);
					intent.putExtra("attributeName", "qq");
					intent.putExtra("attributeValue", application
							.getCurrentUser().getQq());
					intent.putExtra("title", getString(R.string.edit_qq_title));
					break;
				case 3:
					intent = new Intent(PersonInfoActivity.this,
							EditActivity.class);
					intent.putExtra("attributeName", "wechat");
					intent.putExtra("attributeValue", application
							.getCurrentUser().getWechat());
					intent.putExtra("title",
							getString(R.string.edit_wechat_title));
					break;
				case 4:
					intent = new Intent(PersonInfoActivity.this,
							EditActivity.class);
					intent.putExtra("attributeName", "nickName");
					intent.putExtra("attributeValue", application
							.getCurrentUser().getNickName());
					intent.putExtra("title",
							getString(R.string.edit_nickname_title));
					break;
				case 5:
					intent = new Intent(PersonInfoActivity.this,
							GenderActivity.class);
					break;
				case 6:
					intent = new Intent(PersonInfoActivity.this,
							ProvinceActivity.class);
					break;
				case 7:
					intent = new Intent(PersonInfoActivity.this,
							EditActivity.class);
					intent.putExtra("attributeName", "alipay");
					intent.putExtra("attributeValue", application
							.getCurrentUser().getAlipay());
					intent.putExtra("title", getString(R.string.alipay_account));
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
		User currentUser = application.getCurrentUser();
		ArrayList<String> infos = new ArrayList<String>();
		infos.add(getString(R.string.phone_number));
		infos.add(getString(R.string.email_account));
		infos.add(getString(R.string.qq_account));
		infos.add(getString(R.string.wechat_account));
		infos.add(getString(R.string.nickname));
		infos.add(getString(R.string.gender));
		infos.add(getString(R.string.place));
		infos.add(getString(R.string.alipay));

		ArrayList<String> values = new ArrayList<String>();
		values.add(application.getCurrentUser().getPhone());
		values.add(currentUser.getEmail());
		values.add(currentUser.getQq());
		values.add(currentUser.getWechat());
		values.add(currentUser.getNickName());
		if (currentUser.getGender() == 0) {
			values.add(getString(R.string.male));
		} else {
			values.add(getString(R.string.female));
		}

		values.add(getArea(currentUser.getProvince(), currentUser.getCity()));
		values.add(currentUser.getAlipay());

		adapter = new PersonInfoListAdapter(this, infos, values);
		userInfos.setAdapter(adapter);
	}

	/**
	 * 加载头像
	 */
	private void loadHead() {
		String imageUrl = application.getCurrentUser().getAvatar();
		Glide.with(this.getApplicationContext()).load(imageUrl).centerCrop()
				.crossFade().into(headImageView);
	}

	/**
	 * 根据省份和城市代码获取地区信息
	 * 
	 * @param province
	 * @param city
	 * @return
	 */
	private String getArea(int province, int city) {
		CityUtil cityUtil = CityUtil.getInstance(getApplication());
		List<CityInfo> provinces = cityUtil.getProvince_list();
		String result = "";
		for (CityInfo cityInfo : provinces) {
			if (cityInfo.getId().equals(province + "")) {
				result = cityInfo.getCity_name() + " ";
				break;
			}
		}

		List<CityInfo> citys = cityUtil.getCity_map().get(province + "");
		for (CityInfo cityInfo : citys) {
			if (cityInfo.getId().equals(city + "")) {
				result += cityInfo.getCity_name();
				break;
			}
		}

		return result;
	}

	/**
	 * 从本地相册选取图片作为头像
	 */
	private void choseHeadImageFromGallery() {
		Intent intentFromGallery = new Intent();
		intentFromGallery.setType("image/*");
		intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
		try {
			startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
		} catch (ActivityNotFoundException e) {
			// TODO: handle exception
			Toast.makeText(getApplication(), R.string.launch_camera_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 启动手机相机拍摄照片作为头像
	 */
	private void choseHeadImageFromCameraCapture() {
		Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (ImageLoadUtil.hasSdcard()) {
			intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Environment
							.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
		}

		try {
			startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
		} catch (ActivityNotFoundException e) {
			// TODO: handle exception
			Toast.makeText(getApplication(), R.string.launch_camera_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode,
			final Intent intent) {

		if (resultCode == RESULT_CANCELED) {
			return;
		}

		switch (requestCode) {
		case CODE_GALLERY_REQUEST:
			cropRawPhoto(intent.getData());
			break;

		case CODE_CAMERA_REQUEST:
			if (ImageLoadUtil.hasSdcard()) {
				File tempFile = new File(
						Environment.getExternalStorageDirectory(),
						IMAGE_FILE_NAME);
				cropRawPhoto(Uri.fromFile(tempFile));
			} else {
				Toast.makeText(getApplication(), R.string.no_exist_sdcard,
						Toast.LENGTH_SHORT).show();
			}
			break;

		case CODE_RESULT_REQUEST:
			if (intent != null) {
				// 修改为在线程中读取返回的bitmap数据，否则界面会出现闪屏
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Bundle extras = intent.getExtras();
						if (extras != null) {
							Bitmap photo = extras.getParcelable("data");
							uploadImage(application.getCurrentUser().getId(),
									photo);
						}
					}
				}).start();
			}
			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, intent);
	}

	/**
	 * 裁剪原始的图片
	 */
	private void cropRawPhoto(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");

		intent.putExtra("crop", "true");

		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		intent.putExtra("outputX", output_X);
		intent.putExtra("outputY", output_Y);
		intent.putExtra("return-data", true);
		try {
			startActivityForResult(intent, CODE_RESULT_REQUEST);
		} catch (ActivityNotFoundException e) {
			// TODO: handle exception
			Toast.makeText(getApplication(), R.string.launch_camera_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 选择对话框
	 */
	private void showDialog() {
		Builder builder = new Builder(PersonInfoActivity.this);
		builder.setTitle(R.string.change_head);

		final String[] cities = { getString(R.string.select_from_gallery),
				getString(R.string.select_by_take_photo) };

		builder.setItems(cities, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					choseHeadImageFromGallery();
					break;
				case 1:
					choseHeadImageFromCameraCapture();
				default:
					break;
				}
			}
		});
		builder.show();
	}

	/**
	 * 上传头像文件
	 * 
	 * @param userId
	 * @param bitmap
	 */
	private void uploadImage(final int userId, Bitmap bitmap) {
		String url = Constant.MODIFY_INFO_URL;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				User result = (User) ResponseUtil.handleResponse(
						getApplication(), response, User.class);
				if (null != result) {
					Toast.makeText(getApplication(), R.string.modify_success,
							Toast.LENGTH_SHORT).show();
					if (null == result.getToken()) {
						result.setToken(application.getCurrentUser().getToken());
					}
					// 更新内存中的用户对象
					application.setCurrentUser(result);
					// 更新数据库中缓存的用户对象
					userDao.update(result);
					loadHead();
				}
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyErrorUtil.handleVolleyError(getApplication(), error);
			}
		};
		MultipartRequest request = new MultipartRequest(url, listener,
				errorListener);
		// 通过MultipartEntity来设置参数
		MultipartEntity multi = request.getMultiPartEntity();
		// 文本参数
		multi.addStringPart("userId", userId + "");
		multi.addBinaryPart("avatar", bitmap2Bytes(bitmap));
		RequestUtil.getRequestQueue(getApplication()).add(request);
	}

	/**
	 * 将位图转化为二进制数据
	 * 
	 * @param bitmap
	 * @return
	 */
	private byte[] bitmap2Bytes(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
}