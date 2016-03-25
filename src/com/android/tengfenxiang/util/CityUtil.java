package com.android.tengfenxiang.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;

import com.android.tengfenxiang.bean.City;

public class CityUtil {

	private static CityUtil util;

	private List<City> cities;

	private final static String FILE_NAME = "city.txt";

	private CityUtil(Context context) {
		cities = new ArrayList<City>();
		decodeFile(context);
	}

	public static CityUtil getInstance(Context context) {
		if (null == util) {
			util = new CityUtil(context);
		}
		return util;
	}

	private void decodeFile(Context context) {
		cities.clear();
		String content = FileUtil.readAssets(context, FILE_NAME);
		// 读出所有的行
		String[] lines = content.split("\n");
		// 将每一行转为一个City实例
		for (int i = 0; i < lines.length; i++) {
			String[] infos = lines[i].split("\t");
			City city = new City(infos[0], infos[1], infos[2], infos[3],
					infos[4], infos[5], infos[6], infos[7]);
			cities.add(city);
		}
	}

	public String getProvinceName(Context context, int code) {
		if (null == cities || cities.isEmpty()) {
			decodeFile(context);
		}
		String province = "";
		for (City city : cities) {
			if (city.getProvinceId().equals(code + "")) {
				province = city.getProvinceName();
				break;
			}
		}
		if (province.equals("")) {
			for (City city : cities) {
				if (city.getOldProvinceId().equals(code + "")) {
					province = city.getProvinceName();
					break;
				}
			}
		}
		return province;
	}

	public String getCityName(Context context, int code) {
		if (null == cities || cities.isEmpty()) {
			decodeFile(context);
		}
		String cityName = "";
		for (City city : cities) {
			if (city.getCityId().equals(code + "")) {
				cityName = city.getCityName();
				break;
			}
		}
		if (cityName.equals("")) {
			for (City city : cities) {
				if (city.getOldCityId().equals(code + "")) {
					cityName = city.getCityName();
					break;
				}
			}
		}
		return cityName;
	}

	public String getDistrictName(Context context, int code) {
		if (null == cities || cities.isEmpty()) {
			decodeFile(context);
		}
		String district = "";
		for (City city : cities) {
			if (city.getCountyId().equals(code + "")) {
				district = city.getCountyName();
				break;
			}
		}
		return district;
	}

	public List<City> getProvinces(Context context) {
		if (null == cities || cities.isEmpty()) {
			decodeFile(context);
		}
		Map<String, City> maps = new HashMap<String, City>();
		for (City city : cities) {
			if (!maps.containsKey(city.getProvinceId())) {
				maps.put(city.getProvinceId(), city);
			}
		}
		List<City> tmp = new ArrayList<City>();
		Iterator<Entry<String, City>> iter = maps.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, City> entry = (Entry<String, City>) iter.next();
			tmp.add(entry.getValue());
		}
		return tmp;
	}

	public List<City> getCities(Context context, String code) {
		if (null == cities || cities.isEmpty()) {
			decodeFile(context);
		}
		Map<String, City> maps = new HashMap<String, City>();
		for (City city : cities) {
			if (!maps.containsKey(city.getCityId())
					&& city.getProvinceId().equals(code)) {
				maps.put(city.getCityId(), city);
			}
		}
		Iterator<Entry<String, City>> iter = maps.entrySet().iterator();
		List<City> tmp = new ArrayList<City>();
		while (iter.hasNext()) {
			Entry<String, City> entry = (Entry<String, City>) iter.next();
			tmp.add(entry.getValue());
		}
		return tmp;
	}

	public List<City> getDistricts(Context context, String code) {
		if (null == cities || cities.isEmpty()) {
			decodeFile(context);
		}
		List<City> tmp = new ArrayList<City>();
		for (City city : cities) {
			if (city.getCityId().equals(code)) {
				tmp.add(city);
			}
		}
		return tmp;
	}
}