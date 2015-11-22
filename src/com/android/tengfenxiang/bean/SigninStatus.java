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

	/**
	 * 内部类
	 * @author ccz
	 *
	 */
	public class SigninPoint {

		private String signinDate;

		private int status;

		private float points;

		public String getSigninDate() {
			return signinDate;
		}

		public void setSigninDate(String signinDate) {
			this.signinDate = signinDate;
		}

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

	}
}