package com.android.tengfenxiang.adapter;

import java.util.ArrayList;
import java.util.List;

import com.android.tengfenxiang.bean.ItemInfo;
import com.android.tengfenxiang.fragment.ArticleFragment;
import com.android.tengfenxiang.util.ArticleTypeUtil;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

public class ArticlePagerAdapter extends FragmentStatePagerAdapter {

	private final List<String> catalogs = new ArrayList<String>();

	public ArticlePagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		SparseArray<ItemInfo> infos = ArticleTypeUtil.getInstance(context)
				.getTypeInfos();
		for (int i = 0; i < infos.size(); i++) {
			catalogs.add(infos.get(i).getName());
		}
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return catalogs.get(position);
	}

	@Override
	public int getCount() {
		return catalogs.size();
	}

	@Override
	public Fragment getItem(int position) {
		return ArticleFragment.newInstance(position);
	}

}