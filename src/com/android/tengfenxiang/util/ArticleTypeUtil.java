package com.android.tengfenxiang.util;

import com.android.tengfenxiang.bean.ItemInfo;

import android.content.Context;
import android.util.SparseArray;

public class ArticleTypeUtil {

	private static ArticleTypeUtil instance;
	private final static String FILE_NAME = "ArticleType.json";
	private SparseArray<ItemInfo> typeInfos;

	private ArticleTypeUtil(Context context) {
		ItemUtil util = new ItemUtil(context, FILE_NAME);
		typeInfos = util.getItemInfos();
	}

	public static ArticleTypeUtil getInstance(Context context) {
		if (null == instance) {
			instance = new ArticleTypeUtil(context);
		}
		return instance;
	}

	public SparseArray<ItemInfo> getTypeInfos() {
		return typeInfos;
	}

	public void setTypeInfos(SparseArray<ItemInfo> typeInfos) {
		this.typeInfos = typeInfos;
	}

}