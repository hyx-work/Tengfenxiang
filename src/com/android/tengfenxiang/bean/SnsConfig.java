package com.android.tengfenxiang.bean;

/**
 * 分享到各类社交平台的key
 * 
 * @author 陈楚昭
 * 
 */
public class SnsConfig {

	private String wechatKey;

	private String qqKey;

	private String weiboKey;

	public SnsConfig() {
		super();
	}

	public SnsConfig(String wechatKey, String qqKey, String weiboKey) {
		super();
		this.wechatKey = wechatKey;
		this.qqKey = qqKey;
		this.weiboKey = weiboKey;
	}

	public String getWechatKey() {
		return wechatKey;
	}

	public void setWechatKey(String wechatKey) {
		this.wechatKey = wechatKey;
	}

	public String getQqKey() {
		return qqKey;
	}

	public void setQqKey(String qqKey) {
		this.qqKey = qqKey;
	}

	public String getWeiboKey() {
		return weiboKey;
	}

	public void setWeiboKey(String weiboKey) {
		this.weiboKey = weiboKey;
	}

}