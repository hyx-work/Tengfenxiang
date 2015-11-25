package com.android.tengfenxiang.adapter;

import java.util.List;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.SigninStatus;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SigninListAdapter extends BaseAdapter {

	private List<SigninStatus> points;
	private Activity context;

	public SigninListAdapter(Activity context, List<SigninStatus> points) {
		this.context = context;
		this.points = points;
	}

	@Override
	public int getCount() {
		return points.size();
	}

	@Override
	public Object getItem(int position) {
		return points.get(position);
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
					R.layout.signin_info_item, null);
			viewHolder = new ViewHolder();
			viewHolder.date = (TextView) convertView
					.findViewById(R.id.signin_date);
			viewHolder.status = (TextView) convertView
					.findViewById(R.id.signin_status);
			viewHolder.profit = (TextView) convertView
					.findViewById(R.id.signin_profit);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.date.setText(points.get(position).getSigninDate());

		if (1 == points.get(position).getStatus()) {
			viewHolder.status.setText(context.getString(R.string.have_signin));
		} else {
			viewHolder.status.setText(context.getString(R.string.not_signin));
		}

		String profit = points.get(position) + context.getString(R.string.profit_info);
		viewHolder.profit.setText(profit);

		return convertView;
	}

	public class ViewHolder {
		public TextView date;
		public TextView status;
		public TextView profit;
	}
}