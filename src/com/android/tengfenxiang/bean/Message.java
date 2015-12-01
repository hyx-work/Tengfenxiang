package com.android.tengfenxiang.bean;

/**
 * 系统消息实体类
 * 
 * @author ccz
 * 
 */
public class Message {

		/**
		 * 消息id
		 */
		private int id;

		/**
		 * 消息创建时间
		 */
		private String createDate;

		/**
		 * 消息的标题
		 */
		private String title;

		/**
		 * 消息详情
		 */
		private String detail;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getCreateDate() {
			return createDate;
		}

		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

}