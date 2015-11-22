package com.android.tengfenxiang.bean;

import java.util.List;

/**
 * 提现记录实体类
 * @author ccz
 *
 */
public class Withdraw {

	private List<record> records;

	public List<record> getRecords() {
		return records;
	}

	public void setRecords(List<record> records) {
		this.records = records;
	}

	public static class record {
		/**
		 * 记录id
		 */
		private int id;

		/**
		 * 请求的时间
		 */
		private String requestTime;

		/**
		 * 提现的金额
		 */
		private float requestPoints;

		/**
		 * 审核状态
		 * isPermitted为0表示未审核
		 * isPermitted为-1为审核不通过
		 * isPermitted为1表示审核通过
		 */
		private int isPermitted;

		/**
		 * isPermitted为1的时候isWithdraw代表是否提现
		 */
		private int isWithdraw;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getRequestTime() {
			return requestTime;
		}

		public void setRequestTime(String requestTime) {
			this.requestTime = requestTime;
		}

		public float getRequestPoints() {
			return requestPoints;
		}

		public void setRequestPoints(float requestPoints) {
			this.requestPoints = requestPoints;
		}

		public int getIsPermitted() {
			return isPermitted;
		}

		public void setIsPermitted(int isPermitted) {
			this.isPermitted = isPermitted;
		}

		public int getIsWithdraw() {
			return isWithdraw;
		}

		public void setIsWithdraw(int isWithdraw) {
			this.isWithdraw = isWithdraw;
		}
	}

}