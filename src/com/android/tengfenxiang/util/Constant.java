package com.android.tengfenxiang.util;

/**
 * 定义系统中的常量
 * @author ccz
 *
 */
public class Constant {

	/**
	 * 服务器的IP地址
	 */
	public final static String SERVER_ADDRESS = "http://112.124.29.147:8080/TengShare_AppInterface";

	/**
	 * 联系QQ
	 */
	public final static String QQ_GROUP = "111111111";

	/**
	 * 联系电话
	 */
	public final static String CONTACT_PHONE = "020-11111111";

	/**
	 * 登陆的URL
	 */
	public final static String LOGIN_URL = SERVER_ADDRESS + "/user/login/";

	/**
	 * 修改密码的URL
	 */
	public final static String MODIFY_PASSWORD_URL = SERVER_ADDRESS + "/user/modifyPassword/";

	/**
	 * 注册的URL
	 */
	public final static String REGISTER_URL = SERVER_ADDRESS + "/user/register/";

	/**
	 * 获取文章列表的URL
	 */
	public final static String ARTICLE_LIST_URL = SERVER_ADDRESS + "/article/list/";

	/**
	 * 文章分享记录的URL
	 */
	public final static String ARTICLE_RETWEET_URL = SERVER_ADDRESS + "/article/log/retweet/";

	/**
	 * 获取任务列表的URL
	 */
	public final static String TASK_LIST_URL = SERVER_ADDRESS + "/task/list/";

	/**
	 * 任务分享记录的URL
	 */
	public final static String TASK_RETWEET_URL = SERVER_ADDRESS + "/task/log/retweet/";

	/**
	 * 获取收益概览的URL
	 */
	public final static String SUMMARY_URL = SERVER_ADDRESS + "/profit/summary/";

	/**
	 * 获取收益详情的URL
	 */
	public final static String PROFIT_DETAIL_URL = SERVER_ADDRESS + "/profit/detail/";

	/**
	 * 获取提现记录的URL
	 */
	public final static String WITHDRAW_LIST_URL = SERVER_ADDRESS + "/profit/withdraw/list/";

	/**
	 * 提现申请的URL
	 */
	public final static String WITHDRAW_URL = SERVER_ADDRESS + "/profit/withdraw/";

	/**
	 * 修改资料的URL
	 */
	public final static String MODIFY_INFO_URL = SERVER_ADDRESS + "/user/modifyInfo/";

	/**
	 * 获取签到状态的URL
	 */
	public final static String SIGNIN_STATUS_URL = SERVER_ADDRESS + "/user/signin/status/";

	/**
	 * 签到的URL
	 */
	public final static String SIGNIN_URL = SERVER_ADDRESS + "/user/signin/";

	/**
	 * 我的下级的URL
	 */
	public final static String SUBORDINATE_URL = SERVER_ADDRESS + "/user/subordinate/";

	/**
	 * 获取系统消息的URL
	 */
	public final static String MESSAGE_URL = SERVER_ADDRESS + "/system/message/list/";
}