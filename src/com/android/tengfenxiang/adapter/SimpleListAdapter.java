package com.android.tengfenxiang.adapter;

import java.util.List;

import com.android.tengfenxiang.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleListAdapter extends BaseAdapter {

	private List<Integer> icons;
	private List<String> infos;
	private List<String> values;
	private Activity context;

	public SimpleListAdapter(List<Integer> icons, List<String> infos,
			Activity context) {
		this.infos = infos;
		this.icons = icons;
		this.context = context;
	}

	public SimpleListAdapter(Activity context, List<String> infos,
			List<String> values) {
		this.infos = infos;
		this.values = values;
		this.context = context;
	}

	public SimpleListAdapter(Activity context, List<String> infos) {
		this.infos = infos;
		this.context = context;
	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public Object getItem(int arg0) {
		return infos.get(arg0);
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
					R.layout.simple_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.info = (TextView) convertView.findViewById(R.id.info);
			viewHolder.value = (TextView) convertView.findViewById(R.id.value);
			viewHolder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// 设置左边的提示图标
		if (null == icons || icons.size() == 0) {
			viewHolder.icon.setVisibility(View.GONE);
		} else {
			viewHolder.icon.setImageResource(icons.get(position));
		}
		viewHolder.info.setText(infos.get(position));
		// 如果是双信息类型，则隐藏箭头，显示右边的信息
		if (null != values && position < values.size()) {
			viewHolder.value.setText(values.get(position));
			viewHolder.arrow.setVisibility(View.GONE);
		}
		return convertView;
	}

	public class ViewHolder {
		public ImageView icon;
		public TextView info;
		public TextView value;
		public ImageView arrow;
	}
}