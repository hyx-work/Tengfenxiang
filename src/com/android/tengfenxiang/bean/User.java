package com.android.tengfenxiang.bean;

/**
 * 用户实体类
 * @author ccz
 *
 */
public class User {

	/**
	 * 用户的id
	 */
	private int userId;

	/**
	 * 用户昵称
	 */
	private String nickname;

	/**
	 * 手机号码
	 */
	private String phone;

	/**
	 * 支付宝账号
	 */
	private String alipay;

	private String avatar;

	/**
	 * 性别
	 */
	private int gender;

	/**
	 * 省份
	 */
	private int province;

	/**
	 * 城市
	 */
	private int city;

	/**
	 * 微信账号
	 */
	private String wechat;

	/**
	 * QQ账号
	 */
	private String qq;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 邀请码
	 */
	private String inviteCode;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAlipay() {
		return alipay;
	}

	public void setAlipay(String alipay) {
		this.alipay = alipay;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getProvince() {
		return province;
	}

	public void setProvince(int province) {
		this.province = province;
	}

	public int getCity() {
		return city;
	}

	public void setCity(int city) {
		this.city = city;
	}

	public String getWechat() {
		return wechat;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

}