package com.android.tengfenxiang.db;

import java.util.ArrayList;
import java.util.List;

import com.android.tengfenxiang.bean.Article;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BannerDao {

	private DBHelper helper;
	private static BannerDao bannerDao;

	private BannerDao(Context context) {
		helper = new DBHelper(context);
	}

	public static BannerDao getInstance(Context context) {
		if (null == bannerDao) {
			bannerDao = new BannerDao(context);
		}
		return bannerDao;
	}

	public List<Article> findAll(int type) {
		List<Article> articles = new ArrayList<Article>();
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from banner where type=?",
				new String[] { type + "" });
		while (cursor.moveToNext()) {
			int id = cursor.getInt(0);
			String title = cursor.getString(1);
			String content = cursor.getString(2);
			String thumbnails = cursor.getString(3);
			String shareUrl = cursor.getString(4);
			int likeCount = cursor.getInt(5);
			int viewCount = cursor.getInt(6);
			Article article = new Article(id, title, content, thumbnails,
					shareUrl, likeCount, viewCount);
			articles.add(article);
		}
		cursor.close();
		db.close();
		return articles;
	}

	public void insert(List<Article> articles, int type) {
		for (Article article : articles) {
			insert(article, type);
		}
	}

	public void deleteAll(int type) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete("banner", "type=?", new String[] { type + "" });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	public void deleteAll() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete("banner", null, null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	private void insert(Article article, int type) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("id", article.getId());
		values.put("title", article.getTitle());
		values.put("content", article.getContent());
		values.put("thumbnails", article.getThumbnails());
		values.put("shareUrl", article.getShareUrl());
		values.put("likeCount", article.getLikeCount());
		values.put("viewCount", article.getViewCount());
		values.put("type", type);

		db.beginTransaction();
		try {
			db.insert("banner", null, values);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

}