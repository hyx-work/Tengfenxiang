package com.android.tengfenxiang.adapter;

import java.util.List;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.Task;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 任务列表适配器
 * 
 * @author ccz
 * 
 */
public class TaskListAdapter extends BaseAdapter {

	private Activity context;
	private List<Task> tasks;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

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

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_empty)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).build();
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
			viewHolder.limitCount = (TextView) convertView
					.findViewById(R.id.limit_count);
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

		imageLoader.displayImage(tasks.get(position).getThumbnails(),
				viewHolder.image, options);
		viewHolder.title.setText(tasks.get(position).getTitle());
		StringBuffer point = new StringBuffer();
		point.append(context.getString(R.string.unit_yuan_en));
		point.append(tasks.get(position).getPrice());
		point.append(context.getString(R.string.unit_point));
		viewHolder.price.setText(point.toString());

		int limit = tasks.get(position).getLimitRetweetCount();
		String limitCount = context.getString(R.string.limit_count) + limit;
		viewHolder.limitCount.setText(limitCount);

		int rest = tasks.get(position).getRetweetCount();
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
		public TextView limitCount;
		public TextView restCount;
		public TextView endTime;
		public TextView status;
	}
}