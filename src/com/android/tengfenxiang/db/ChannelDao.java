package com.android.tengfenxiang.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.tengfenxiang.bean.ChannelItem;
import com.android.tengfenxiang.bean.ArticleType;

public class ChannelDao {
	private DBHelper helper;
	private static ChannelDao channelDao;

	private ChannelDao(Context context) {
		helper = new DBHelper(context);
	}

	public static ChannelDao getInstance(Context context) {
		if (null == channelDao) {
			channelDao = new ChannelDao(context);
		}
		return channelDao;
	}

	public synchronized void insertUserChannel(List<ArticleType> types) {
		for (int i = 0; i < types.size(); i++) {
			insert(types.get(i), i);
		}
	}

	private synchronized void insert(ArticleType type, int order) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("id", type.getCode());
		values.put("name", type.getName());
		values.put("orderId", order);
		values.put("selected", 1);

		db.beginTransaction();
		try {
			db.insert("channel", null, values);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	public synchronized void insertChannel(ChannelItem item) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("id", item.getId());
		values.put("name", item.getName());
		values.put("orderId", item.getOrderId());
		values.put("selected", item.getSelected());

		db.beginTransaction();
		try {
			db.insert("channel", null, values);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	public synchronized void deleteAll() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete("channel", null, null);
			// 重置_id从0开始自增长
			String sql = "update sqlite_sequence set seq=0 where name='channel'";
			db.execSQL(sql);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	public synchronized List<ChannelItem> findAllChannel(int selected) {
		List<ChannelItem> items = new ArrayList<ChannelItem>();
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from channel where selected=?",
				new String[] { selected + "" });
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			int orderId = cursor.getInt(cursor.getColumnIndex("orderId"));

			ChannelItem item = new ChannelItem(id, name, orderId, selected);
			items.add(item);
		}
		cursor.close();
		db.close();
		return items;
	}

	public synchronized List<ArticleType> findAllItem(int selected) {
		List<ArticleType> items = new ArrayList<ArticleType>();
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from channel where selected=?",
				new String[] { selected + "" });
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			ArticleType item = new ArticleType(id, name);
			items.add(item);
		}
		cursor.close();
		db.close();
		return items;
	}

	public synchronized void delete(ArticleType info) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete("channel", "id=?", new String[] { info.getCode() + "" });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

}