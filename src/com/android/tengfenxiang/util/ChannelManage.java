package com.android.tengfenxiang.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.android.tengfenxiang.bean.ChannelItem;
import com.android.tengfenxiang.bean.ItemInfo;
import com.android.tengfenxiang.db.ChannelDao;
import com.android.tengfenxiang.db.DBHelper;

import android.content.Context;
import android.database.SQLException;
import android.util.SparseArray;

public class ChannelManage {

	public static ChannelManage channelManage;

	/**
	 * 默认的用户选择频道列表
	 * */
	private List<ChannelItem> defaultUserChannels;

	/**
	 * 默认的其他频道列表
	 * */
	private List<ChannelItem> defaultOtherChannels;

	private ChannelDao channelDao;

	private boolean userExist = false;

	/**
	 * 默认版块的code值
	 */
	private int[] defaultUserCodes = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11 };

	private ChannelManage(DBHelper paramDBHelper) throws SQLException {
		if (channelDao == null)
			channelDao = new ChannelDao(paramDBHelper.getContext());

		initData(paramDBHelper.getContext());
		return;
	}

	private void initData(Context context) {
		defaultUserChannels = new ArrayList<ChannelItem>();
		defaultOtherChannels = new ArrayList<ChannelItem>();

		// 用户频道和其他频道都为0时，说明第一次使用，需要生成默认数据
		if (getUserChannel().size() == 0 && getOtherChannel().size() == 0) {
			SparseArray<ItemInfo> infos = ArticleTypeUtil.getInstance(context)
					.getTypeInfos();
			for (int i = 0; i < infos.size(); i++) {
				ChannelItem item = new ChannelItem(infos.get(i).getCode(),
						infos.get(i).getName(), i, 1);
				if (isUserChannel(infos.get(i).getCode())) {
					defaultUserChannels.add(item);
				} else {
					defaultOtherChannels.add(item);
				}
			}
		}
	}

	private boolean isUserChannel(int code) {
		boolean result = false;
		for (int i = 0; i < defaultUserCodes.length; i++) {
			if (code == defaultUserCodes[i]) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * 初始化频道管理类
	 * 
	 * @param paramDBHelper
	 * @throws SQLException
	 */
	public static ChannelManage getManage(DBHelper dbHelper)
			throws SQLException {
		if (channelManage == null)
			channelManage = new ChannelManage(dbHelper);
		return channelManage;
	}

	/**
	 * 清除所有的频道
	 */
	public void deleteAllChannel() {
		channelDao.clearFeedTable();
	}

	/**
	 * 获取其他的频道
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ChannelItem> getUserChannel() {
		Object cacheList = channelDao.listCache("selected= ?",
				new String[] { "1" });
		if (cacheList != null
				&& !((List<Map<String, String>>) cacheList).isEmpty()) {
			userExist = true;
			List<Map<String, String>> maplist = (List<Map<String, String>>) cacheList;
			int count = maplist.size();
			List<ChannelItem> list = new ArrayList<ChannelItem>();
			for (int i = 0; i < count; i++) {
				ChannelItem navigate = new ChannelItem();
				navigate.setId(Integer.valueOf(maplist.get(i).get("id")));
				navigate.setName(maplist.get(i).get("name"));
				navigate.setOrderId(Integer.valueOf(maplist.get(i).get(
						"orderId")));
				navigate.setSelected(Integer.valueOf(maplist.get(i).get(
						"selected")));
				list.add(navigate);
			}
			return list;
		}
		initDefaultChannel();
		return defaultUserChannels;
	}

	/**
	 * 获取其他的频道
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ChannelItem> getOtherChannel() {
		Object cacheList = channelDao.listCache("selected= ?",
				new String[] { "0" });
		List<ChannelItem> list = new ArrayList<ChannelItem>();
		if (cacheList != null
				&& !((List<Map<String, String>>) cacheList).isEmpty()) {
			List<Map<String, String>> maplist = (List<Map<String, String>>) cacheList;
			int count = maplist.size();
			for (int i = 0; i < count; i++) {
				ChannelItem navigate = new ChannelItem();
				navigate.setId(Integer.valueOf(maplist.get(i).get("id")));
				navigate.setName(maplist.get(i).get("name"));
				navigate.setOrderId(Integer.valueOf(maplist.get(i).get(
						"orderId")));
				navigate.setSelected(Integer.valueOf(maplist.get(i).get(
						"selected")));
				list.add(navigate);
			}
			return list;
		}
		if (userExist) {
			return list;
		}
		cacheList = defaultOtherChannels;
		return (List<ChannelItem>) cacheList;
	}

	/**
	 * 保存用户频道到数据库
	 * 
	 * @param userList
	 */
	public void saveUserChannel(List<ChannelItem> userList) {
		for (int i = 0; i < userList.size(); i++) {
			ChannelItem channelItem = (ChannelItem) userList.get(i);
			channelItem.setOrderId(i);
			channelItem.setSelected(Integer.valueOf(1));
			channelDao.addCache(channelItem);
		}
	}

	/**
	 * 保存其他频道到数据库
	 * 
	 * @param otherList
	 */
	public void saveOtherChannel(List<ChannelItem> otherList) {
		for (int i = 0; i < otherList.size(); i++) {
			ChannelItem channelItem = (ChannelItem) otherList.get(i);
			channelItem.setOrderId(i);
			channelItem.setSelected(Integer.valueOf(0));
			channelDao.addCache(channelItem);
		}
	}

	/**
	 * 初始化数据库内的频道数据
	 */
	private void initDefaultChannel() {
		deleteAllChannel();
		saveUserChannel(defaultUserChannels);
		saveOtherChannel(defaultOtherChannels);
	}
}