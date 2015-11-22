package com.android.tengfenxiang.bean;

import java.util.List;

/**
 * 签到状态实体类
 * @author ccz
 *
 */
public class SigninStatus {

	private boolean status;

	private float points;

	private List<SigninPoint> recent;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public float getPoints() {
		return points;
	}

	public void setPoints(float points) {
		this.points = points;
	}

	public List<SigninPoint> getRecent() {
		return recent;
	}

	public void setRecent(List<SigninPoint> recent) {
		this.recent = recent;
	}

}