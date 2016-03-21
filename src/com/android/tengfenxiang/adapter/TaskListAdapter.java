package com.android.tengfenxiang.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.Task;
import com.bumptech.glide.Glide;

/**
 * 任务列表适配器
 * 
 * @author ccz
 * 
 */
public class TaskListAdapter extends BaseAdapter {

	private Activity context;
	private List<Task> tasks;

	/**
	 * 任务状态对应显示的文字
	 */
	private static int taskStatus[] = { R.string.status_offline,
			R.string.status_online, R.string.status_finish };

	/**
	 * 任务状态对应显示的背景色
	 */
	private static int statusBg[] = { R.drawable.status_offline_bg,
			R.drawable.status_online_bg, R.drawable.status_finish_bg };

	public TaskListAdapter(Activity context, List<Task> tasks) {
		this.context = context;
		this.tasks = tasks;
	}

	@Override
	public int getCount() {
		return tasks.size();
	}

	@Override
	public Object getItem(int arg0) {
		return tasks.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = context.getLayoutInflater().inflate(
					R.layout.task_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.price = (TextView) convertView.findViewById(R.id.price);
			viewHolder.retweetCount = (TextView) convertView
					.findViewById(R.id.retweet_count);
			viewHolder.restCount = (TextView) convertView
					.findViewById(R.id.rest_count);
			viewHolder.endTime = (TextView) convertView
					.findViewById(R.id.end_time);
			viewHolder.status = (TextView) convertView
					.findViewById(R.id.status);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// 利用缓存框架加载图片
		Glide.with(context.getApplicationContext())
				.load(tasks.get(position).getThumbnails()).centerCrop()
				.crossFade().into(viewHolder.image);

		viewHolder.title.setText(tasks.get(position).getTitle());
		StringBuffer point = new StringBuffer();
		point.append(tasks.get(position).getPrice());
		point.append(context.getString(R.string.unit_point));
		viewHolder.price.setText(point.toString());

		int limit = tasks.get(position).getLimitRetweetCount();
		int retweet = tasks.get(position).getRetweetCount();
		int rest = limit - retweet;

		String retweetCount = context.getString(R.string.retweet_count)
				+ retweet;
		viewHolder.retweetCount.setText(retweetCount);

		String restCount = context.getString(R.string.rest_count) + rest;
		viewHolder.restCount.setText(restCount);

		String endTimeHint = context.getString(R.string.end_time);
		String endTime = tasks.get(position).getEndTime().split(" ")[0];
		viewHolder.endTime.setText(endTimeHint + endTime);

		int status = tasks.get(position).getStatus();
		viewHolder.status.setText(taskStatus[status]);
		viewHolder.status.setBackgroundResource(statusBg[status]);
		return convertView;
	}

	public class ViewHolder {
		public ImageView image;
		public TextView title;
		public TextView price;
		public TextView retweetCount;
		public TextView restCount;
		public TextView endTime;
		public TextView status;
	}
}