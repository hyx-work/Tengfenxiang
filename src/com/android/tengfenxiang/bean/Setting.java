package com.android.tengfenxiang.bean;

import java.util.List;

public class Setting {

	private aboutInfo aboutViewSettings;

	private double pointsToCashRate;

	private List<ItemInfo> articleTypes;

	public aboutInfo getAboutViewSettings() {
		return aboutViewSettings;
	}

	public void setAboutViewSettings(aboutInfo aboutViewSettings) {
		this.aboutViewSettings = aboutViewSettings;
	}

	public double getPointsToCashRate() {
		return pointsToCashRate;
	}

	public void setPointsToCashRate(double pointsToCashRate) {
		this.pointsToCashRate = pointsToCashRate;
	}

	public List<ItemInfo> getArticleTypes() {
		return articleTypes;
	}

	public void setArticleTypes(List<ItemInfo> articleTypes) {
		this.articleTypes = articleTypes;
	}

}