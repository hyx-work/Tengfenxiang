package com.android.tengfenxiang.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.SQLException;

import com.android.tengfenxiang.bean.ArticleType;
import com.android.tengfenxiang.bean.ChannelItem;
import com.android.tengfenxiang.bean.Setting;
import com.android.tengfenxiang.db.ChannelDao;

public class ChannelUtil {

	public static ChannelUtil channelManage;

	private ChannelDao dao;

	private List<ChannelItem> userChannelItems;

	private List<ChannelItem> otherChannelItems;

	private ChannelUtil(Context context) throws SQLException {
		if (dao == null) {
			dao = ChannelDao.getInstance(context);
		}
	}

	public static ChannelUtil getInstance(Context context) throws SQLException {
		if (channelManage == null) {
			channelManage = new ChannelUtil(context);
		}
		return channelManage;
	}

	/**
	 * 初始化本地文章类型，耗时的数据库操作，最好在子线程中执行
	 * 
	 * @param setting
	 */
	public void initData(Setting setting) {
		userChannelItems = dao.findAllChannel(1);
		otherChannelItems = dao.findAllChannel(0);

		// 如果传入的静态配置对象为null或者其中的文章类型信息为空，则直接返回
		if (null == setting || null == setting.getArticleTypes()
				|| setting.getArticleTypes().size() == 0) {
			return;
		}

		List<ArticleType> infos = new ArrayList<ArticleType>();
		infos.addAll(setting.getArticleTypes());

		// 如果当前本地没有缓存的文章类型信息，则初始化并返回
		if (userChannelItems.size() == 0 && otherChannelItems.size() == 0) {
			dao.insertUserChannel(infos);
			userChannelItems = dao.findAllChannel(1);
			otherChannelItems = dao.findAllChannel(0);
			return;
		}

		// 第一步，找出配置文件中没有但是本地中有的类型，删除掉
		boolean step1 = step1(infos);

		// 第二步，找出配置文件中有但是本地中没有的类型，添加
		boolean step2 = step2(infos);

		// 第三步，更新
		// 两边数据是一致的
		if (step1 == false && step2 == false) {
			return;
		}
		userChannelItems = dao.findAllChannel(1);
		otherChannelItems = dao.findAllChannel(0);
		deleteAllChannel();
		saveUserChannel(userChannelItems);
		saveOtherChannel(otherChannelItems);
		userChannelItems = dao.findAllChannel(1);
		otherChannelItems = dao.findAllChannel(0);
	}

	/**
	 * 第一步，找出配置文件中没有但是本地中有的类型，删除掉
	 * 
	 * @param infos
	 */
	private boolean step1(List<ArticleType> infos) {
		List<ArticleType> userTmps = dao.findAllItem(1);
		List<ArticleType> otherTmps = dao.findAllItem(0);
		for (int i = 0; i < infos.size(); i++) {
			for (int j = 0; j < userTmps.size(); j++) {
				if (userTmps.get(j).getCode() == infos.get(i).getCode()) {
					userTmps.remove(j);
					break;
				}
			}
		}
		for (int i = 0; i < infos.size(); i++) {
			for (int j = 0; j < otherTmps.size(); j++) {
				if (otherTmps.get(j).getCode() == infos.get(i).getCode()) {
					otherTmps.remove(j);
					break;
				}
			}
		}
		if (!userTmps.isEmpty() || !otherTmps.isEmpty()) {
			if (!userTmps.isEmpty()) {
				for (ArticleType info : userTmps) {
					dao.delete(info);
				}
			}
			if (!otherTmps.isEmpty()) {
				for (ArticleType info : otherTmps) {
					dao.delete(info);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 第二步，找出配置文件中有但是本地中没有的类型，添加
	 * 
	 * @param infos
	 */
	private boolean step2(List<ArticleType> infos) {
		List<ArticleType> userTmps = dao.findAllItem(1);
		List<ArticleType> otherTmps = dao.findAllItem(0);
		for (int i = 0; i < userTmps.size(); i++) {
			for (int j = 0; j < infos.size(); j++) {
				if (infos.get(j).getCode() == userTmps.get(i).getCode()) {
					infos.remove(j);
					break;
				}
			}
		}
		for (int i = 0; i < otherTmps.size(); i++) {
			for (int j = 0; j < infos.size(); j++) {
				if (infos.get(j).getCode() == otherTmps.get(i).getCode()) {
					infos.remove(j);
					break;
				}
			}
		}
		if (!infos.isEmpty()) {
			dao.insertUserChannel(infos);
			return true;
		} else {
			return false;
		}
	}

	public void deleteAllChannel() {
		dao.deleteAll();
	}

	public List<ChannelItem> getUserChannelItems() {
		return userChannelItems;
	}

	public void setUserChannelItems(List<ChannelItem> userChannelItems) {
		this.userChannelItems = userChannelItems;
	}

	public List<ChannelItem> getOtherChannelItems() {
		return otherChannelItems;
	}

	public void setOtherChannelItems(List<ChannelItem> otherChannelItems) {
		this.otherChannelItems = otherChannelItems;
	}

	public void saveUserChannel(List<ChannelItem> userList) {
		for (int i = 0; i < userList.size(); i++) {
			ChannelItem channelItem = (ChannelItem) userList.get(i);
			channelItem.setOrderId(i);
			channelItem.setSelected(Integer.valueOf(1));
			dao.insertChannel(channelItem);
		}
	}

	public void saveOtherChannel(List<ChannelItem> otherList) {
		for (int i = 0; i < otherList.size(); i++) {
			ChannelItem channelItem = (ChannelItem) otherList.get(i);
			channelItem.setOrderId(i);
			channelItem.setSelected(Integer.valueOf(0));
			dao.insertChannel(channelItem);
		}
	}

}