package com.android.tengfenxiang.util;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.android.tengfenxiang.bean.ItemInfo;

import android.content.Context;
import android.util.SparseArray;

public class ItemUtil {

	private SparseArray<ItemInfo> itemInfos;

	public ItemUtil(Context context, String fileName) {
		String json = FileUtil.readAssets(context, fileName);
		parseJsonObject(json);
	}

	private void parseJsonObject(String json) {
		if (null == json || json.equals("")) {
			return;
		}
		List<ItemInfo> infos = new ArrayList<ItemInfo>();
		infos = JSON.parseArray(json, ItemInfo.class);		

		itemInfos = new SparseArray<ItemInfo>();
		for (ItemInfo info : infos) {
			itemInfos.put(info.getCode(), info);
		}
	}

	public SparseArray<ItemInfo> getItemInfos() {
		return itemInfos;
	}

	public void setItemInfos(SparseArray<ItemInfo> itemInfos) {
		this.itemInfos = itemInfos;
	}

}