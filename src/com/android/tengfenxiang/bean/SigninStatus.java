package com.android.tengfenxiang.bean;

import java.util.List;

/**
 * 签到状态实体类
 * @author ccz
 *
 */
public class SigninStatus {

	private String signinDate;

	private int status;

	private float points;

	private List<SigninStatus> recent;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public float getPoints() {
		return points;
	}

	public void setPoints(float points) {
		this.points = points;
	}

	public List<SigninStatus> getRecent() {
		return recent;
	}

	public void setRecent(List<SigninStatus> recent) {
		this.recent = recent;
	}

	public String getSigninDate() {
		return signinDate;
	}

	public void setSigninDate(String signinDate) {
		this.signinDate = signinDate;
	}

}