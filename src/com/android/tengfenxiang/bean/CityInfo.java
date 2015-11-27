package com.android.tengfenxiang.bean;

import java.io.Serializable;

public class CityInfo implements Serializable {

	private static final long serialVersionUID = 2852621825459542514L;

	private String id;
	private String city_name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

}