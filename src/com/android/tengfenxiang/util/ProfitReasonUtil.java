package com.android.tengfenxiang.util;

import android.content.Context;
import android.util.SparseArray;

import com.android.tengfenxiang.bean.ItemInfo;

public class ProfitReasonUtil {
	private static ProfitReasonUtil instance;
	private final static String FILE_NAME = "ProfitReasonUtil.json";
	private SparseArray<ItemInfo> profitReasons;

	private ProfitReasonUtil(Context context) {
		ItemUtil util = new ItemUtil(context, FILE_NAME);
		profitReasons = util.getItemInfos();
	}

	public static ProfitReasonUtil getInstance(Context context) {
		if (null == instance) {
			instance = new ProfitReasonUtil(context);
		}
		return instance;
	}

	public SparseArray<ItemInfo> getProfitReasons() {
		return profitReasons;
	}

	public void setProfitReasons(SparseArray<ItemInfo> profitReasons) {
		this.profitReasons = profitReasons;
	}

}