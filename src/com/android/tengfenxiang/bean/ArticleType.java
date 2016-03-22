package com.android.tengfenxiang.bean;

public class ArticleType {

	private int code;

	private String name;

	public ArticleType() {
		super();
	}

	public ArticleType(int code, String name) {
		super();
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}