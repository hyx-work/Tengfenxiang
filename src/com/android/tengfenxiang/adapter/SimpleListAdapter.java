package com.android.tengfenxiang.adapter;

import java.util.List;

import com.android.tengfenxiang.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SimpleListAdapter extends BaseAdapter {

	private List<String> infos;
	private List<String> values;
	private Activity context;

	public SimpleListAdapter(Activity context, List<String> infos,
			List<String> values) {
		this.infos = infos;
		this.values = values;
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
			viewHolder.info = (TextView) convertView.findViewById(R.id.info);
			viewHolder.value = (TextView) convertView.findViewById(R.id.value);
			viewHolder.info.setText(infos.get(position));
			viewHolder.value.setText(values.get(position));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		return convertView;
	}

	public class ViewHolder {
		public TextView info;
		public TextView value;
	}
}