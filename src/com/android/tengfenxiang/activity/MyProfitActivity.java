package com.android.tengfenxiang.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.MyProfitListAdapter;
import com.android.tengfenxiang.bean.Summary;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.graphview.CustomLabelFormatter;
import com.android.tengfenxiang.view.graphview.GraphView;
import com.android.tengfenxiang.view.graphview.GraphView.GraphViewData;
import com.android.tengfenxiang.view.graphview.GraphViewSeries;
import com.android.tengfenxiang.view.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.android.tengfenxiang.view.graphview.LineGraphView;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyProfitActivity extends BaseActivity {

	private ListView summaryListView;

	private Summary summary;
	private ArrayList<String> titles = new ArrayList<String>();

	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_profit);

		dialog = new LoadingDialog(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		dialog.showDialog();
		getSummary(currentUser.getId());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}

	private void initView() {
		summaryListView = (ListView) findViewById(R.id.summary);
		fillList();
		addListener();
		if (summary.getRecent() != null && summary.getRecent().size() > 0) {
			initChartView();
		} else {
			initEmptyHint();
		}
	}

	private void addListener() {
		summaryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				switch (arg2) {
				case 0:
				case 1:
				case 2:
					intent.setClass(MyProfitActivity.this,
							IntegralActivity.class);
					intent.putExtra("title", titles.get(arg2));
					intent.putExtra("detailType", arg2);
					break;
				case 3:
					intent.setClass(MyProfitActivity.this,
							WithdrawActivity.class);
					break;
				default:
					intent.setClass(MyProfitActivity.this,
							ApplyWithdrawActivity.class);
					intent.putExtra("withdrawPoints",
							summary.getWithdrawablePoints());
					break;
				}
				startActivity(intent);
			}
		});
	}

	private void fillList() {
		titles = new ArrayList<String>();
		titles.add(getString(R.string.realtime_points));
		titles.add(getString(R.string.yesterday_points));
		titles.add(getString(R.string.total_points));
		titles.add(getString(R.string.withdraw_points));
		titles.add(getString(R.string.withdrawable_points));
		ArrayList<String> values = new ArrayList<String>();
		values.add(summary.getRealtimePoints() + getString(R.string.unit_point));
		values.add(summary.getYesterdayPoints()
				+ getString(R.string.unit_point));
		values.add(summary.getTotalPoints() + getString(R.string.unit_point));
		values.add(summary.getWithdrawPoints() + getString(R.string.unit_yuan));
		values.add(summary.getWithdrawablePoints()
				+ getString(R.string.unit_yuan));
		MyProfitListAdapter adapter = new MyProfitListAdapter(
				MyProfitActivity.this, titles, values);
		summaryListView.setAdapter(adapter);
	}

	/**
	 * 获取收益概览信息
	 * 
	 * @param context
	 * @param userId
	 *            用户ID
	 */
	public void getSummary(int userId) {
		// 根据用户id构造请求地址
		String url = Constant.SUMMARY_URL + "?userId=" + userId;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				summary = (Summary) ResponseUtil.handleResponse(
						getApplication(), response, Summary.class);
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				initView();
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				summary = new Summary();
				initView();
				Toast.makeText(getApplication(), R.string.unknow_error,
						Toast.LENGTH_SHORT).show();
			}
		};

		StringRequest request = new StringRequest(url, listener, errorListener);
		RequestManager.getRequestQueue(getApplication()).add(request);
	}

	/**
	 * 初始化收益曲线表格
	 */
	private void initChartView() {
		long now = new Date().getTime();
		long t = 86400000;

		// 曲线的颜色
		int lineColor = getResources().getColor(R.color.chart_line_color);
		// 显示文字的颜色
		int fontColor = getResources().getColor(R.color.chart_font_color);
		// 网格线的颜色
		int gridColor = getResources().getColor(R.color.chart_grid_color);

		GraphViewSeriesStyle style = new GraphViewSeriesStyle(lineColor, 2);
		GraphViewSeries points = new GraphViewSeries("", style,
				new GraphViewData[] { new GraphViewData(now - 6 * t, 2.2d),
						new GraphViewData(now - 5 * t, 2.0d),
						new GraphViewData(now - 4 * t, 1.5d),
						new GraphViewData(now - 3 * t, 3.0d),
						new GraphViewData(now - 2 * t, 2.5d),
						new GraphViewData(now - 1 * t, 1.0d),
						new GraphViewData(now, 3.0d) });

		GraphView graphView;
		graphView = new LineGraphView(this, "");

		((LineGraphView) graphView).setDataPointsRadius(0);
		graphView.getGraphViewStyle().setGridColor(gridColor);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(fontColor);
		graphView.getGraphViewStyle().setVerticalLabelsColor(fontColor);
		graphView.getGraphViewStyle().setNumHorizontalLabels(7);
		graphView.getGraphViewStyle().setNumVerticalLabels(4);
		graphView.getGraphViewStyle().setTextSize(12);
		graphView.addSeries(points);

		// 设置日期显示
		final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd",
				Locale.CHINESE);
		graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
			@Override
			public String formatLabel(double value, boolean isValueX) {
				if (isValueX) {
					Date d = new Date((long) value);
					return dateFormat.format(d);
				}
				return null;
			}
		});

		LinearLayout layout = (LinearLayout) findViewById(R.id.profit_chart);
		layout.addView(graphView);
	}

	/**
	 * 初始化提示文字
	 */
	private void initEmptyHint() {
		// TODO Auto-generated method stub
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		TextView textView = new TextView(this);
		textView.setGravity(Gravity.CENTER);
		textView.setText(R.string.empty_profit_date);
		textView.setTextColor(getResources().getColor(R.color.red));
		textView.setLayoutParams(params);
		LinearLayout layout = (LinearLayout) findViewById(R.id.profit_chart);
		layout.addView(textView);
	}
}