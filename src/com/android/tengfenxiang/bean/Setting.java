package com.android.tengfenxiang.bean;

import java.util.List;

public class Setting {

	private aboutInfo aboutViewSettings;

	public aboutInfo getAboutViewSettings() {
		return aboutViewSettings;
	}

	public void setAboutViewSettings(aboutInfo aboutViewSettings) {
		this.aboutViewSettings = aboutViewSettings;
	}

	public static class aboutInfo {
		private List<String> QQGroups;

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

	}
}