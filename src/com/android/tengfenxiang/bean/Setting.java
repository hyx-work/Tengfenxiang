package com.android.tengfenxiang.bean;

import java.util.List;

public class Setting {

	private AboutInfo aboutViewSettings;

	private double pointsToCashRate;

	private List<ArticleType> articleTypes;

	private SnsConfig snsConfig;

	public AboutInfo getAboutViewSettings() {
		return aboutViewSettings;
	}

	public void setAboutViewSettings(AboutInfo aboutViewSettings) {
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

	public SnsConfig getSnsConfig() {
		return snsConfig;
	}

	public void setSnsConfig(SnsConfig snsConfig) {
		this.snsConfig = snsConfig;
	}

}