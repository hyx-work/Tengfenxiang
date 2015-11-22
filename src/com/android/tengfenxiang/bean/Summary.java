package com.android.tengfenxiang.bean;

import java.util.List;

/**
 * 收益概况实体类
 * @author ccz
 *
 */
public class Summary {

	private float realtimePoints;

	private float yesterdayPoints;

	private float totalPoints;

	private float withdrawPoints;

	private float withdrawablePoints;

	private List<ProfitPoint> recent;

	public float getRealtimePoints() {
		return realtimePoints;
	}

	public void setRealtimePoints(float realtimePoints) {
		this.realtimePoints = realtimePoints;
	}

	public float getYesterdayPoints() {
		return yesterdayPoints;
	}

	public void setYesterdayPoints(float yesterdayPoints) {
		this.yesterdayPoints = yesterdayPoints;
	}

	public float getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(float totalPoints) {
		this.totalPoints = totalPoints;
	}

	public float getWithdrawPoints() {
		return withdrawPoints;
	}

	public void setWithdrawPoints(float withdrawPoints) {
		this.withdrawPoints = withdrawPoints;
	}

	public float getWithdrawablePoints() {
		return withdrawablePoints;
	}

	public void setWithdrawablePoints(float withdrawablePoints) {
		this.withdrawablePoints = withdrawablePoints;
	}

	public List<ProfitPoint> getRecent() {
		return recent;
	}

	public void setRecent(List<ProfitPoint> recent) {
		this.recent = recent;
	}

	/**
	 * 内部类表示曲线上的某个点
	 * @author ccz
	 *
	 */
	public static class ProfitPoint {

		/**
		 * 横坐标，日期
		 */
		private String profitDate;

		/**
		 * 纵坐标，收益值
		 */
		private float points;

		public String getProfitDate() {
			return profitDate;
		}

		public void setProfitDate(String profitDate) {
			this.profitDate = profitDate;
		}

		public float getPoints() {
			return points;
		}

		public void setPoints(float points) {
			this.points = points;
		}

	}
}