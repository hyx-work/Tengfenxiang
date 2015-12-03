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
	 * 任务状态对应显示的图片
	 */
	private static int statusImageId[] = { R.drawable.ic_empty,
			R.drawable.ic_empty, R.drawable.ic_empty };

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
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.content);
			viewHolder.price = (TextView) convertView.findViewById(R.id.price);
			viewHolder.limitCount = (TextView) convertView
					.findViewById(R.id.limit_count);
			viewHolder.restCount = (TextView) convertView
					.findViewById(R.id.rest_count);
			viewHolder.beginTime = (TextView) convertView
					.findViewById(R.id.begin_time);
			viewHolder.endTime = (TextView) convertView
					.findViewById(R.id.end_time);
			viewHolder.status = (ImageView) convertView
					.findViewById(R.id.status);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		imageLoader.displayImage(tasks.get(position).getThumbnails(),
				viewHolder.image, options);
		viewHolder.title.setText(tasks.get(position).getTitle());
		viewHolder.content.setText(tasks.get(position).getContent());
		viewHolder.price.setText(tasks.get(position).getPrice()
				+ context.getString(R.string.unit_yuan));
		int limit = tasks.get(position).getLimitRetweetCount();
		viewHolder.limitCount.setText(limit + "");
		int rest = limit - tasks.get(position).getRetweetCount();
		viewHolder.restCount.setText(rest + "");
		String beginTimeHint = context.getString(R.string.begin_time);
		String beginTime = tasks.get(position).getBeginTime().split(" ")[0];
		viewHolder.beginTime.setText(beginTimeHint + beginTime);
		String endTimeHint = context.getString(R.string.end_time);
		String endTime = tasks.get(position).getEndTime().split(" ")[0];
		viewHolder.endTime.setText(endTimeHint + endTime);
		int status = tasks.get(position).getStatus();
		viewHolder.status.setImageResource(statusImageId[status]);
		return convertView;
	}

	public class ViewHolder {
		public ImageView image;
		public TextView title;
		public TextView content;
		public TextView price;
		public TextView limitCount;
		public TextView restCount;
		public TextView beginTime;
		public TextView endTime;
		public ImageView status;
	}
}