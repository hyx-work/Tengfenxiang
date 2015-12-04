package com.android.tengfenxiang.bean;

/**
 * 任务实体类
 * 
 * @author ccz
 * 
 */
public class Task {

	/**
	 * 任务id
	 */
	private int id;

	/**
	 * 任务名称
	 */
	private String title;

	/**
	 * 任务内容
	 */
	private String content;

	/**
	 * 价格
	 */
	private float price;

	/**
	 * 缩略图
	 */
	private String thumbnails;

	/**
	 * 分享的链接
	 */
	private String shareUrl;

	/**
	 * 开始时间点
	 */
	private String beginTime;

	/**
	 * 结束时间点
	 */
	private String endTime;

	/**
	 * 任务的状态 status：0下线、1进行中、2已抢完
	 */
	private int status;

	/**
	 * 分享的次数
	 */
	private int retweetCount;

	/**
	 * 分享上限次数
	 */
	private int limitRetweetCount;

	public Task() {

	}

	public Task(int id, String title, String content, float price,
			String thumbnails, String shareUrl, String beginTime,
			String endTime, int status, int retweetCount, int limitRetweetCount) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.price = price;
		this.thumbnails = thumbnails;
		this.shareUrl = shareUrl;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.status = status;
		this.retweetCount = retweetCount;
		this.limitRetweetCount = limitRetweetCount;
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

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
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

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(int retweetCount) {
		this.retweetCount = retweetCount;
	}

	public int getLimitRetweetCount() {
		return limitRetweetCount;
	}

	public void setLimitRetweetCount(int limitRetweetCount) {
		this.limitRetweetCount = limitRetweetCount;
	}

}