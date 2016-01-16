package com.android.tengfenxiang.adapter;

import java.util.List;

import com.android.tengfenxiang.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IntegralListAdapter extends BaseAdapter {

	private List<String> infos;
	private List<String> values;
	private Activity context;

	public IntegralListAdapter(Activity context, List<String> infos,
			List<String> values) {
		this.infos = infos;
		this.values = values;
		this.context = context;
	}

	public IntegralListAdapter(Activity context, List<String> infos) {
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
					R.layout.integral_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.info = (TextView) convertView.findViewById(R.id.info);
			viewHolder.value = (TextView) convertView.findViewById(R.id.value);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.info.setText(infos.get(position));
		if (null != values && position < values.size()) {
			viewHolder.value.setText(values.get(position));
		}
		return convertView;
	}

	public class ViewHolder {
		public TextView info;
		public TextView value;
	}
}