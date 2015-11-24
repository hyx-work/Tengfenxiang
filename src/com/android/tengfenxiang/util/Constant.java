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
	 * 登陆的URL
	 */
	public final static String LOGIN_URL = SERVER_ADDRESS + "/user/login/";

	/**
	 * 注册的URL
	 */
	public final static String REGISTER_URL = SERVER_ADDRESS + "/user/register/";

	/**
	 * 收益概览的URL
	 */
	public final static String SUMMARY_URL = SERVER_ADDRESS + "/profit/summary/";
}