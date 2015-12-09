package com.android.tengfenxiang.adapter;

import java.util.List;

import com.android.tengfenxiang.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonInfoListAdapter extends BaseAdapter {
	private List<String> infos;
	private List<String> values;
	private Activity context;

	public PersonInfoListAdapter(Activity context, List<String> infos,
			List<String> values) {
		this.context = context;
		this.infos = infos;
		this.values = values;
	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public Object getItem(int position) {
		return infos.get(position);
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
					R.layout.person_info_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.info = (TextView) convertView.findViewById(R.id.info);
			viewHolder.value = (TextView) convertView.findViewById(R.id.value);
			viewHolder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.info.setText(infos.get(position));
		if (position < values.size()) {
			viewHolder.value.setText(values.get(position));
		}
		if (0 == position) {
			viewHolder.arrow.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	public class ViewHolder {
		public TextView info;
		public TextView value;
		public ImageView arrow;
	}
}