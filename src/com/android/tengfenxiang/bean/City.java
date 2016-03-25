package com.android.tengfenxiang.bean;

public class City {

	private String provinceName;

	private String provinceId;

	private String cityName;

	private String cityId;

	private String countyName;

	private String countyId;

	private String oldCityId;

	private String oldProvinceId;

	public City() {
		super();
	}

	public City(String provinceName, String provinceId, String cityName,
			String cityId, String countyName, String countyId,
			String oldCityId, String oldProvinceId) {
		super();
		this.provinceName = provinceName;
		this.provinceId = provinceId;
		this.cityName = cityName;
		this.cityId = cityId;
		this.countyName = countyName;
		this.countyId = countyId;
		this.oldCityId = oldCityId;
		this.oldProvinceId = oldProvinceId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getCountyId() {
		return countyId;
	}

	public void setCountyId(String countyId) {
		this.countyId = countyId;
	}

	public String getOldCityId() {
		return oldCityId;
	}

	public void setOldCityId(String oldCityId) {
		this.oldCityId = oldCityId;
	}

	public String getOldProvinceId() {
		return oldProvinceId;
	}

	public void setOldProvinceId(String oldProvinceId) {
		this.oldProvinceId = oldProvinceId;
	}

}