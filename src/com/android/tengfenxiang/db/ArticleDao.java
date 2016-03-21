package com.android.tengfenxiang.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.tengfenxiang.bean.Article;

public class ArticleDao {

	private DBHelper helper;
	private static ArticleDao articleDao;

	private ArticleDao(Context context) {
		helper = new DBHelper(context);
	}

	public static ArticleDao getInstance(Context context) {
		if (null == articleDao) {
			articleDao = new ArticleDao(context);
		}
		return articleDao;
	}

	public synchronized List<Article> findAll(int type) {
		List<Article> articles = new ArrayList<Article>();
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from article where type=?",
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

	public synchronized void insert(List<Article> articles, int type) {
		for (Article article : articles) {
			insert(article, type);
		}
	}

	public synchronized void deleteAll(int type) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete("article", "type=?", new String[] { type + "" });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	public synchronized void deleteAll() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete("article", null, null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	private synchronized void insert(Article article, int type) {
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
			db.insert("article", null, values);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

}