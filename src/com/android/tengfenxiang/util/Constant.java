package com.android.tengfenxiang.util;

/**
 * 定义系统中的常量
 * 
 * @author ccz
 * 
 */
public class Constant {

	/**
	 * 服务器的IP地址
	 */
	// public final static String SERVER_ADDRESS = "http://api.wanzhuan9527.com/TengShare_AppInterface";

	/**
	 * 测试服务器的IP地址
	 */
	public final static String SERVER_ADDRESS = "http://112.124.29.147:8080/TengShare_AppInterface";

	/**
	 * 图片缓存的相对路径，现在设置为：sdcard/9527/Cache
	 */
	public final static String IMAGE_CACHE_PATH = "9527/Cache";

	/**
	 * 微信的APP_ID
	 */
	public final static String WX_APP_ID = "wxe3da6f4746e58d27";

	/**
	 * 登陆的URL
	 */
	public final static String LOGIN_URL = SERVER_ADDRESS + "/user/login/";

	/**
	 * 注销的URL
	 */
	public final static String LOGOUT_URL = SERVER_ADDRESS + "/user/logout/";

	/**
	 * 修改密码的URL
	 */
	public final static String MODIFY_PASSWORD_URL = SERVER_ADDRESS
			+ "/user/modifyPassword/";

	/**
	 * 注册的URL
	 */
	public final static String REGISTER_URL = SERVER_ADDRESS
			+ "/static/html/register.html";

	/**
	 * 获取文章列表的URL
	 */
	public final static String ARTICLE_LIST_URL = SERVER_ADDRESS
			+ "/article/list/";

	/**
	 * 文章分享记录的URL
	 */
	public final static String ARTICLE_RETWEET_URL = SERVER_ADDRESS
			+ "/article/log/retweet/";

	/**
	 * 获取任务列表的URL
	 */
	public final static String TASK_LIST_URL = SERVER_ADDRESS + "/task/list/";

	/**
	 * 任务分享记录的URL
	 */
	public final static String TASK_RETWEET_URL = SERVER_ADDRESS
			+ "/task/log/retweet/";

	/**
	 * 获取收益概览的URL
	 */
	public final static String SUMMARY_URL = SERVER_ADDRESS
			+ "/profit/summary/";

	/**
	 * 获取收益详情的URL
	 */
	public final static String PROFIT_DETAIL_URL = SERVER_ADDRESS
			+ "/profit/detail/";

	/**
	 * 获取提现记录的URL
	 */
	public final static String WITHDRAW_LIST_URL = SERVER_ADDRESS
			+ "/profit/withdraw/list/";

	/**
	 * 提现申请的URL
	 */
	public final static String WITHDRAW_URL = SERVER_ADDRESS
			+ "/profit/withdraw/";

	/**
	 * 修改资料的URL
	 */
	public final static String MODIFY_INFO_URL = SERVER_ADDRESS
			+ "/user/modifyInfo/";

	/**
	 * 获取签到状态的URL
	 */
	public final static String SIGNIN_STATUS_URL = SERVER_ADDRESS
			+ "/user/signin/status/";

	/**
	 * 签到的URL
	 */
	public final static String SIGNIN_URL = SERVER_ADDRESS + "/user/signin/";

	/**
	 * 我的下级的URL
	 */
	public final static String SUBORDINATE_URL = SERVER_ADDRESS
			+ "/user/subordinate/";

	/**
	 * 获取系统消息的URL
	 */
	public final static String MESSAGE_URL = SERVER_ADDRESS
			+ "/system/message/list/";

	/**
	 * 获取QQ群和客服电话的URL
	 */
	public final static String SETTING_URL = SERVER_ADDRESS
			+ "/static/config.json";

	/**
	 * 功能介绍的URL
	 */
	public final static String INTRODUCTION_URL = SERVER_ADDRESS
			+ "/static/html/aboutApp.html";

	/**
	 * 帮助中心的URL
	 */
	public final static String HELP_CENTER_URL = SERVER_ADDRESS
			+ "/static/html/FAQ.html";

	/**
	 * 意见反馈的URL
	 */
	public final static String FEEDBACK_URL = SERVER_ADDRESS
			+ "/static/html/feedback.html";

	/**
	 * 注销登录发送的广播文字
	 */
	public final static String LOGOUT_BROADCAST = "logout_current_account";

	/**
	 * 保存分享记录发送的广播文字
	 */
	public final static String SAVE_RETWEET_RECORD = "save_reweet_record";
}