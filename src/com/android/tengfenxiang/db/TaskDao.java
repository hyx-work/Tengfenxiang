package com.android.tengfenxiang.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.tengfenxiang.bean.Task;

public class TaskDao {

	private DBHelper helper;
	private static TaskDao taskDao;

	private TaskDao(Context context) {
		helper = new DBHelper(context);
	}

	public static TaskDao getInstance(Context context) {
		if (null == taskDao) {
			taskDao = new TaskDao(context);
		}
		return taskDao;
	}

	public synchronized List<Task> findAll() {
		List<Task> tasks = new ArrayList<Task>();
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from task", null);
		while (cursor.moveToNext()) {
			int id = cursor.getInt(0);
			String title = cursor.getString(1);
			String content = cursor.getString(2);
			float price = cursor.getFloat(3);
			String thumbnails = cursor.getString(4);
			String shareUrl = cursor.getString(5);
			String beginTime = cursor.getString(6);
			String endTime = cursor.getString(7);
			int status = cursor.getInt(8);
			int retweetCount = cursor.getInt(9);
			int limitRetweetCount = cursor.getInt(10);
			Task task = new Task(id, title, content, price, thumbnails,
					shareUrl, beginTime, endTime, status, retweetCount,
					limitRetweetCount);
			tasks.add(task);
		}
		cursor.close();
		db.close();
		return tasks;
	}

	public synchronized void insert(List<Task> tasks) {
		for (Task task : tasks) {
			insert(task);
		}
	}

	public synchronized void deleteAll() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete("task", null, null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	private synchronized void insert(Task task) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("id", task.getId());
		values.put("title", task.getTitle());
		values.put("content", task.getContent());
		values.put("price", task.getPrice());
		values.put("thumbnails", task.getThumbnails());
		values.put("shareUrl", task.getShareUrl());
		values.put("beginTime", task.getBeginTime());
		values.put("endTime", task.getEndTime());
		values.put("status", task.getStatus());
		values.put("retweetCount", task.getRetweetCount());
		values.put("limitRetweetCount", task.getLimitRetweetCount());

		db.beginTransaction();
		try {
			db.insert("task", null, values);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

}