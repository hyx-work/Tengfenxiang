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

	private int[] permittedState = { R.string.is_fail_permitted,
			R.string.is_not_permitted };
	private int[] withdrawState = { R.string.is_not_withdraw,
			R.string.is_withdraw };

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
		viewHolder.points.setText(getPoints(withdraws.get(position)));
		return convertView;
	}

	private String getPoints(Withdraw withdraw) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(withdraw.getRequestPoints())
				.append(context.getString(R.string.unit_yuan)).append("(");
		int isPermitted = withdraw.getIsPermitted();
		int isWithdraw = withdraw.getIsWithdraw();
		switch (isPermitted) {
		case -1:
		case 0:
			buffer.append(context.getString(permittedState[isPermitted + 1]));
			break;
		default:
			buffer.append(context.getString(withdrawState[isWithdraw]));
			break;
		}
		buffer.append(")");
		return buffer.toString();
	}

	public class ViewHolder {
		public TextView data;
		public TextView points;
	}
}