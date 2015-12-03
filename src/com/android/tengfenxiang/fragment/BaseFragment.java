package com.android.tengfenxiang.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * 懒加载的Fragment
 * 
 * @author ccz
 * 
 */
public abstract class BaseFragment extends Fragment {

	protected boolean isVisible;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if (getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInvisible();
		}
	}

	/**
	 * 可见
	 */
	protected void onVisible() {
		lazyLoad();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

	}

	/**
	 * 不可见
	 */
	protected void onInvisible() {

	}

	/**
	 * 延迟加载 子类必须重写此方法
	 */
	protected abstract void lazyLoad();
}