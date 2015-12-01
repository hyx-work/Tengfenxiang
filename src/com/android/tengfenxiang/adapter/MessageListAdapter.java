package com.android.tengfenxiang.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.Message;

public class MessageListAdapter extends BaseAdapter {
	private List<Message> messages;

	/**
	 * 消息是否展开显示
	 */
	private List<Boolean> isOpen;
	private Activity context;

	public MessageListAdapter(Activity context, List<Message> messages,
			List<Boolean> isOpen) {
		this.context = context;
		this.messages = messages;
		this.isOpen = isOpen;
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = context.getLayoutInflater().inflate(
					R.layout.message_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.content);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.title.setText(messages.get(position).getTitle());
		viewHolder.isOpen = isOpen.get(position);
		if (viewHolder.isOpen) {
			viewHolder.content.setSingleLine(false);
		} else {
			viewHolder.content.setSingleLine(true);
		}
		viewHolder.content.setText(messages.get(position).getDetail());
		viewHolder.time.setText(messages.get(position).getCreateDate());

		return convertView;
	}

	public class ViewHolder {
		public TextView title;
		public TextView content;
		public TextView time;
		public boolean isOpen = true;
	}
}