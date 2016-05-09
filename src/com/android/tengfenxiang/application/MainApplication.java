package com.android.tengfenxiang.application;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.widget.Toast;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.activity.GoldCoinActivity;
import com.android.tengfenxiang.bean.Setting;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.receiver.ConnectionChangeReceiver;
import com.android.tengfenxiang.receiver.ConnectionChangeReceiver.OnNetworkChangedListener;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.ImageLoadUtil;
import com.android.tengfenxiang.util.LoginUtil;
import com.android.tengfenxiang.util.LoginUtil.OnLoginListener;
import com.android.volley.VolleyError;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.BuglyStrategy;
import com.tencent.bugly.beta.Beta;
import com.tencent.smtt.sdk.QbSdk;

/**
 * 复写application，初始化参数
 * 
 * @author ccz
 * 
 */
public class MainApplication extends Application {

	private static User currentUser;
	private static Setting setting;
	private ConnectionChangeReceiver myReceiver;
	private static LoginUtil loginUtil;
	private SharedPreferences preferences;

	public void onCreate() {
		super.onCreate();
		// 初始化账户登录工具类
		loginUtil = LoginUtil.getInstance(this);
		// 注册广播
		registerReceiver();
		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		// X5内核尚未初始化，则需要初始化内核
		if (!QbSdk.isTbsCoreInited()) {
			QbSdk.preInit(this);
		}

		// 初始化bugly
		initBuglyStrategy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		// 内存不足时清除缓存
		ImageLoadUtil.clearMemoryCache(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		// 销毁时注销广播
		unregisterReceiver();
	}

	/**
	 * 注册广播监听器
	 */
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		myReceiver = new ConnectionChangeReceiver();
		myReceiver.setOnNetworkChangedListener(new OnNetworkChangedListener() {

			@Override
			public void onUsable() {
				String phone = preferences.getString("phone", "");
				String password = preferences.getString("password", "");
				// 当网络从断开到连接上时，且本机上已经登录过，需要进行登录操作
				if (!phone.equals("") && !password.equals("")
						&& null != getCurrentUser()
						&& null == getCurrentUser().getToken()) {
					Toast.makeText(MainApplication.this, R.string.try_to_login,
							Toast.LENGTH_SHORT).show();
					loginUtil.setOnLoginListener(onLoginListener);
					loginUtil.login(phone, password);
				}
			}

			@Override
			public void onUnusable() {
				// 当网络从连接到断开，需要给出提示
				Toast.makeText(MainApplication.this, R.string.network_error,
						Toast.LENGTH_SHORT).show();
			}
		});
		registerReceiver(myReceiver, filter);
	}

	private OnLoginListener onLoginListener = new OnLoginListener() {

		@Override
		public void onLoginSuccess() {
			// TODO Auto-generated method stub
			Toast.makeText(MainApplication.this, R.string.login_success,
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onLoginFail() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLoginError(VolleyError error) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 注销广播监听器
	 */
	private void unregisterReceiver() {
		unregisterReceiver(myReceiver);
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		MainApplication.currentUser = currentUser;
	}

	public Setting getSetting() {
		return setting;
	}

	public void setSetting(Setting setting) {
		MainApplication.setting = setting;
	}

	public LoginUtil getLoginUtil() {
		return loginUtil;
	}

	public void setLoginUtil(LoginUtil loginUtil) {
		MainApplication.loginUtil = loginUtil;
	}

	/**
	 * 初始化自动升级配置
	 */
	private void initBuglyStrategy() {
		/***** Beta高级设置 *****/
		/**
		 * true表示app启动自动初始化升级模块; false不会自动初始化;
		 * 开发者如果担心sdk初始化影响app启动速度，可以设置为false，
		 * 在后面某个时刻手动调用Beta.init(getApplicationContext(),false);
		 */
		Beta.autoInit = true;

		/**
		 * true表示初始化时自动检查升级; false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
		 */
		Beta.autoCheckUpgrade = false;

		/**
		 * 设置启动延时为1s（默认延时3s），APP启动1s后初始化SDK，避免影响APP启动速度;
		 */
		Beta.initDelay = 1 * 1000;
		/**
		 * 设置通知栏大图标，largeIconId为项目中的图片资源;
		 */
		Beta.largeIconId = R.drawable.ic_launcher;
		/**
		 * 设置状态栏小图标，smallIconId为项目中的图片资源Id;
		 */
		Beta.smallIconId = R.drawable.ic_launcher;
		/**
		 * 设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;
		 * 当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
		 */
		Beta.defaultBannerId = R.drawable.ic_launcher;
		/**
		 * 设置sd卡的Download为更新资源保存目录;
		 * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
		 */
		Beta.storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		/**
		 * 已经确认过的弹窗在APP下次启动自动检查更新时会再次显示;
		 */
		Beta.showInterruptedStrategy = true;
		/**
		 * 只允许在GoldCoinActivity上显示更新弹窗，其他activity上不显示弹窗;
		 * 不设置会默认所有activity都可以显示弹窗;
		 */
		Beta.canShowUpgradeActs.add(GoldCoinActivity.class);

		/***** Bugly高级设置 *****/
		BuglyStrategy strategy = new BuglyStrategy();

		/***** 统一初始化Bugly产品，包含Beta *****/
		Bugly.init(this, Constant.BUGLY_APP_ID, true, strategy);
	}
}