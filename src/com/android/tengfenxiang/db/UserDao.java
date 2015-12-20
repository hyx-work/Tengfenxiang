package com.android.tengfenxiang.db;

import com.android.tengfenxiang.bean.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDao {

	private DBHelper helper;
	private static UserDao userDao;

	private UserDao(Context context) {
		helper = new DBHelper(context);
	}

	public static UserDao getInstance(Context context) {
		if (null == userDao) {
			userDao = new UserDao(context);
		}
		return userDao;
	}

	/**
	 * 插入用户实例
	 * 
	 * @param user
	 */
	public void insert(User user) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("id", user.getId());
		values.put("nickName", user.getNickName());
		values.put("phone", user.getPhone());
		values.put("alipay", user.getAlipay());
		values.put("avatar", user.getAvatar());
		values.put("gender", user.getGender());
		values.put("province", user.getProvince());
		values.put("city", user.getCity());
		values.put("wechat", user.getWechat());
		values.put("qq", user.getQq());
		values.put("email", user.getEmail());
		values.put("inviteCode", user.getInviteCode());
		db.beginTransaction();
		try {
			db.insert("user", null, values);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	/**
	 * 删除用户实例
	 * 
	 * @param phone
	 */
	public void deleteUser(String phone) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete("user", "phone=?", new String[] { phone });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	/**
	 * 根据手机号码查找用户
	 * 
	 * @param phone
	 * @return
	 */
	public User findUser(String phone) {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.query("user", new String[] { "id", "nickName",
				"phone", "alipay", "avatar", "gender", "province", "city",
				"wechat", "qq", "email", "inviteCode" }, "phone=?",
				new String[] { phone }, null, null, null, null);
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String nickName = cursor.getString(cursor
					.getColumnIndex("nickName"));
			String alipay = cursor.getString(cursor.getColumnIndex("alipay"));
			String avatar = cursor.getString(cursor.getColumnIndex("avatar"));
			int gender = cursor.getInt(cursor.getColumnIndex("gender"));
			int province = cursor.getInt(cursor.getColumnIndex("province"));
			int city = cursor.getInt(cursor.getColumnIndex("city"));
			String wechat = cursor.getString(cursor.getColumnIndex("wechat"));
			String qq = cursor.getString(cursor.getColumnIndex("qq"));
			String email = cursor.getString(cursor.getColumnIndex("email"));
			String inviteCode = cursor.getString(cursor
					.getColumnIndex("inviteCode"));
			cursor.close();
			db.close();
			return new User(id, nickName, phone, alipay, avatar, gender,
					province, city, wechat, qq, email, inviteCode);
		}
		cursor.close();
		db.close();
		return null;
	}

	/**
	 * 更新数据库中的信息
	 * 
	 * @param user
	 */
	public void update(User user) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("id", user.getId());
		values.put("nickName", user.getNickName());
		values.put("phone", user.getPhone());
		values.put("alipay", user.getAlipay());
		values.put("avatar", user.getAvatar());
		values.put("gender", user.getGender());
		values.put("province", user.getProvince());
		values.put("city", user.getCity());
		values.put("wechat", user.getWechat());
		values.put("qq", user.getQq());
		values.put("email", user.getEmail());
		values.put("inviteCode", user.getInviteCode());
		db.update("user", values, "id=?",
				new String[] { String.valueOf(user.getId()) });
		db.close();
	}
}