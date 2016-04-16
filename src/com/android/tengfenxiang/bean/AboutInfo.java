package com.android.tengfenxiang.bean;

import java.util.List;

public class AboutInfo {

	private List<String> QQGroups;

	private List<String> BusinessQQ;

	private String phone;

	public List<String> getQQGroups() {
		return QQGroups;
	}

	public void setQQGroups(List<String> qQGroups) {
		QQGroups = qQGroups;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public List<String> getBusinessQQ() {
		return BusinessQQ;
	}

	public void setBusinessQQ(List<String> businessQQ) {
		BusinessQQ = businessQQ;
	}

}