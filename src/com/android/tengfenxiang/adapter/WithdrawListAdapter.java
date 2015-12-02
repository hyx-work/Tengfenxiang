package com.android.tengfenxiang.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.Withdraw;

public class WithdrawListAdapter extends BaseAdapter {
	private Activity context;
	private List<Withdraw> withdraws;

	public WithdrawListAdapter(Activity context, List<Withdraw> withdraws) {
		this.context = context;
		this.withdraws = withdraws;
	}

	@Override
	public int getCount() {
		return withdraws.size();
	}

	@Override
	public Object getItem(int arg0) {
		return withdraws.get(arg0);
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
					R.layout.withdraw_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.data = (TextView) convertView
					.findViewById(R.id.withdraw_data);
			viewHolder.points = (TextView) convertView
					.findViewById(R.id.withdraw_count);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}
		String date[] = withdraws.get(position).getRequestTime().split(" ");
		viewHolder.data.setText(date[0]);
		viewHolder.points.setText(withdraws.get(position).getRequestPoints()
				+ context.getString(R.string.unit_yuan));
		return convertView;
	}

	public class ViewHolder {
		public TextView data;
		public TextView points;
	}
}