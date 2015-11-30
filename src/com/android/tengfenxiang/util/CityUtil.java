package com.android.tengfenxiang.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;

import com.android.tengfenxiang.bean.CityInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 从文件中解析省份和城市的工具类
 * 
 * @author ccz
 * 
 */
public class CityUtil {

	private HashMap<String, List<CityInfo>> city_map = new HashMap<String, List<CityInfo>>();
	private List<CityInfo> province_list = new ArrayList<CityInfo>();
	private ArrayList<String> province_list_code = new ArrayList<String>();
	private ArrayList<String> city_list_code = new ArrayList<String>();

	public static CityUtil instance;

	private CityUtil(Context context) {
		String area_str = FileUtil.readAssets(context, "city.json");
		province_list = getJSONParserResult(area_str, "province");
		city_map = getJSONParserResultArray(area_str, "city");
	}

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static CityUtil getInstance(Context context) {
		if (null == instance) {
			instance = new CityUtil(context);
		}
		return instance;
	}

	private List<CityInfo> getJSONParserResult(String JSONString, String key) {
		List<CityInfo> list = new ArrayList<CityInfo>();
		JsonObject result = new JsonParser().parse(JSONString)
				.getAsJsonObject().getAsJsonObject(key);

		Iterator<Entry<String, JsonElement>> iterator = result.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, JsonElement> entry = (Entry<String, JsonElement>) iterator
					.next();
			CityInfo cityinfo = new CityInfo();

			cityinfo.setCity_name(entry.getValue().getAsString());
			cityinfo.setId(entry.getKey());
			province_list_code.add(entry.getKey());
			list.add(cityinfo);
		}
		return list;
	}

	private HashMap<String, List<CityInfo>> getJSONParserResultArray(
			String JSONString, String key) {
		HashMap<String, List<CityInfo>> hashMap = new HashMap<String, List<CityInfo>>();
		JsonObject result = new JsonParser().parse(JSONString)
				.getAsJsonObject().getAsJsonObject(key);

		Iterator<Entry<String, JsonElement>> iterator = result.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, JsonElement> entry = (Entry<String, JsonElement>) iterator
					.next();
			List<CityInfo> list = new ArrayList<CityInfo>();
			JsonArray array = entry.getValue().getAsJsonArray();
			for (int i = 0; i < array.size(); i++) {
				CityInfo cityinfo = new CityInfo();
				cityinfo.setCity_name(array.get(i).getAsJsonArray().get(0)
						.getAsString());
				cityinfo.setId(array.get(i).getAsJsonArray().get(1)
						.getAsString());
				city_list_code.add(array.get(i).getAsJsonArray().get(1)
						.getAsString());
				list.add(cityinfo);
			}
			hashMap.put(entry.getKey(), list);
		}
		return hashMap;
	}

	public HashMap<String, List<CityInfo>> getCity_map() {
		return city_map;
	}

	public void setCity_map(HashMap<String, List<CityInfo>> city_map) {
		this.city_map = city_map;
	}

	public List<CityInfo> getProvince_list() {
		return province_list;
	}

	public void setProvince_list(List<CityInfo> province_list) {
		this.province_list = province_list;
	}

	public ArrayList<String> getProvince_list_code() {
		return province_list_code;
	}

	public void setProvince_list_code(ArrayList<String> province_list_code) {
		this.province_list_code = province_list_code;
	}

	public ArrayList<String> getCity_list_code() {
		return city_list_code;
	}

	public void setCity_list_code(ArrayList<String> city_list_code) {
		this.city_list_code = city_list_code;
	}

}