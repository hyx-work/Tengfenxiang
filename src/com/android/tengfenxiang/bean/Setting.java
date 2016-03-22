package com.android.tengfenxiang.bean;

import java.util.List;

public class Setting {

	private aboutInfo aboutViewSettings;

	private double pointsToCashRate;

	private List<ArticleType> articleTypes;

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

	public List<ArticleType> getArticleTypes() {
		return articleTypes;
	}

	public void setArticleTypes(List<ArticleType> articleTypes) {
		this.articleTypes = articleTypes;
	}

}