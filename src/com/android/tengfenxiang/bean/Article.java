package com.android.tengfenxiang.bean;

/**
 * 文章实体类
 * 
 * @author ccz
 * 
 */
public class Article {

	/**
	 * 文章id
	 */
	private int id;

	/**
	 * 文章标题
	 */
	private String title;

	/**
	 * 文章内容
	 */
	private String content;

	/**
	 * 缩略图
	 */
	private String thumbnails;

	/**
	 * 分享的链接
	 */
	private String shareUrl;

	/**
	 * 点赞数
	 */
	private int likeCount;

	/**
	 * 浏览数
	 */
	private int viewCount;

	public Article() {

	}

	public Article(int id, String title, String content, String thumbnails,
			String shareUrl, int likeCount, int viewCount) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.thumbnails = thumbnails;
		this.shareUrl = shareUrl;
		this.likeCount = likeCount;
		this.viewCount = viewCount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getThumbnails() {
		return thumbnails;
	}

	public void setThumbnails(String thumbnails) {
		this.thumbnails = thumbnails;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

}