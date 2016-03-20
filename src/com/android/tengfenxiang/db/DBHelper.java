package com.android.tengfenxiang.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	final private static String DBname = "tengfenxiang";

	// 数据库增加banner表，用于缓存文章页面的顶部banner
	// user表增加district, streetInfo, withdrawableCash字段
	// 数据库版本号修改为2
	final private static int mDbVersion = 2;

	private Context context;

	public DBHelper(Context context, CursorFactory factory) {
		super(context, DBname, factory, mDbVersion);
		this.context = context;
	}

	public DBHelper(Context context) {
		this(context, null);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String creatTaskTable = "create table if not exists task(id integer, title text, content text, price float, thumbnails text, shareUrl text, beginTime text, endTime text, status integer, retweetCount integer, limitRetweetCount integer)";
		String createArticleTable = "create table if not exists article(id integer, title text, content text, thumbnails text, shareUrl text, likeCount integer, viewCount integer, type integer)";
		String createBannerTable = "create table if not exists banner(id integer, title text, content text, thumbnails text, shareUrl text, likeCount integer, viewCount integer, type integer)";
		String createChannelTable = "create table if not exists channel(_id integer primary key autoincrement,  id integer , name text , orderId integer , selected selected)";
		String createUserTable = "create table if not exists user(id integer primary key, nickName text, phone text, alipay text, avatar text, gender integer, province integer, city integer, district integer, streetInfo text, wechat text, qq text, email text, inviteCode text, withdrawableCash integer)";
		db.execSQL(createArticleTable);
		db.execSQL(creatTaskTable);
		db.execSQL(createChannelTable);
		db.execSQL(createUserTable);
		db.execSQL(createBannerTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS task");
		db.execSQL("DROP TABLE IF EXISTS article");
		// 数据库升级时channel删除，因为这里保存的是用户的数据不是缓存
		// db.execSQL("DROP TABLE IF EXISTS channel");
		db.execSQL("DROP TABLE IF EXISTS user");
		db.execSQL("DROP TABLE IF EXISTS banner");
		onCreate(db);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}