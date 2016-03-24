package com.android.tengfenxiang.view.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * 
 * @author Github大神：https://github.com/antonyt，我增加了moveToNext方法用于移动到下一个ViewPager
 * 
 */
public class InfiniteViewPager extends ViewPager {

	public InfiniteViewPager(Context context) {
		super(context);
	}

	public InfiniteViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setAdapter(PagerAdapter adapter) {
		super.setAdapter(adapter);
		// offset first element so that we can scroll to the left
		setCurrentItem(0);
	}

	@Override
	public void setCurrentItem(int item) {
		// offset the current item to ensure there is space to scroll
		setCurrentItem(item, false);
	}

	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		if (getAdapter().getCount() == 0) {
			super.setCurrentItem(item, smoothScroll);
			return;
		}
		item = getOffsetAmount() + (item % getAdapter().getCount());
		super.setCurrentItem(item, smoothScroll);
	}

	@Override
	public int getCurrentItem() {
		if (getAdapter().getCount() == 0) {
			return super.getCurrentItem();
		}
		int position = super.getCurrentItem();
		if (getAdapter() instanceof InfinitePagerAdapter) {
			InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) getAdapter();
			// Return the actual item position in the data backing
			// InfinitePagerAdapter
			return (position % infAdapter.getRealCount());
		} else {
			return super.getCurrentItem();
		}
	}

	/**
	 * 自己添加的方法，用于移动到下一个ViewPager，算是对原控件增加的一个小功能
	 */
	public void moveToNext() {
		// 没有数据时或者数据量为1的时候，不需要移动
		if (getAdapter().getCount() == 0 || getAdapter().getCount() == 1) {
			return;
		}
		// 取出当前真正的位置
		int realPosition = super.getCurrentItem();
		// 如果当前的adapter是InfinitePagerAdapter的话，就移动到下一个
		if (getAdapter() instanceof InfinitePagerAdapter) {
			super.setCurrentItem(realPosition + 1, true);
		}
	}

	private int getOffsetAmount() {
		if (getAdapter().getCount() == 0) {
			return 0;
		}
		if (getAdapter() instanceof InfinitePagerAdapter) {
			InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) getAdapter();
			// allow for 100 back cycles from the beginning
			// should be enough to create an illusion of infinity
			// warning: scrolling to very high values (1,000,000+) results in
			// strange drawing behaviour
			return infAdapter.getRealCount() * 100;
		} else {
			return 0;
		}
	}
}