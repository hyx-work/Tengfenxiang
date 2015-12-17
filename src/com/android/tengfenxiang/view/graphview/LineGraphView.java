package com.android.tengfenxiang.view.graphview;

import com.android.tengfenxiang.util.DensityUtil;
import com.android.tengfenxiang.view.graphview.GraphViewSeries.GraphViewSeriesStyle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

public class LineGraphView extends GraphView {
	private Paint paintBackground;
	private boolean drawBackground;
	private boolean drawDataPoints;
	private float dataPointsRadius = 10f;
	private Context context;

	public LineGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public LineGraphView(Context context, String title) {
		super(context, title);
		this.context = context;
		init();
	}

	private void init() {
		paintBackground = new Paint();
		paintBackground.setColor(Color.rgb(20, 40, 60));
		paintBackground.setStrokeWidth(4);
		paintBackground.setAlpha(128);
	}

	@Override
	public void drawSeries(Canvas canvas, GraphViewDataInterface[] values,
			float graphwidth, float graphheight, float border, double minX,
			double minY, double diffX, double diffY, float horstart,
			GraphViewSeriesStyle style) {
		double lastEndY = 0;
		double lastEndX = 0;

		paint.setStrokeWidth(DensityUtil
				.dip2px(context, style.thickness * 1.0f));
		paint.setColor(style.color);

		Path bgPath = null;
		if (drawBackground) {
			bgPath = new Path();
		}

		lastEndY = 0;
		lastEndX = 0;
		float firstX = 0;
		for (int i = 0; i < values.length; i++) {
			double valY = values[i].getY() - minY;
			double ratY = valY / diffY;
			double y = graphheight * ratY;

			// 根据点的个数来分配X轴刻度的长度，而不进行平均分配
			double ratX = i * 1.0 / ((values.length - 1) * 1.0);
			double x = graphwidth * ratX;

			if (i > 0) {
				float startX = (float) lastEndX + (horstart + 1);
				float startY = (float) (border - lastEndY) + graphheight;
				float endX = (float) x + (horstart + 1);
				float endY = (float) (border - y) + graphheight;

				if (drawDataPoints) {
					canvas.drawCircle(endX, endY, dataPointsRadius, paint);
				}

				// 画两点间的直线
				paint.setColor(Color.rgb(250, 98, 65));
				canvas.drawLine(startX, startY, endX, endY, paint);
				if (bgPath != null) {
					if (i == 1) {
						firstX = startX;
						bgPath.moveTo(startX, startY + style.thickness);
					}
					bgPath.lineTo(endX, endY + style.thickness);
				}

				// 在直线的起点画一个圆圈
				paint.setColor(Color.rgb(250, 98, 65));
				canvas.drawCircle(startX, startY, DensityUtil.dip2px(context,
						GraphViewConfig.MARKER_MARGIN), paint);
				paint.setColor(Color.WHITE);
				canvas.drawCircle(startX, startY, DensityUtil.dip2px(context,
						GraphViewConfig.RECT_RADIUS), paint);

				// 画最后一个圆圈
				if (i == values.length - 1) {
					paint.setColor(Color.rgb(250, 98, 65));
					canvas.drawCircle(endX, endY, DensityUtil.dip2px(context,
							GraphViewConfig.MARKER_MARGIN), paint);
					paint.setColor(Color.WHITE);
					canvas.drawCircle(endX, endY, DensityUtil.dip2px(context,
							GraphViewConfig.RECT_RADIUS), paint);
				}
			} else if (drawDataPoints) {
				float first_X = (float) x + (horstart + 1);
				float first_Y = (float) (border - y) + graphheight;
				canvas.drawCircle(first_X, first_Y, dataPointsRadius, paint);
			}
			lastEndY = y;
			lastEndX = x;
		}

		if (bgPath != null) {
			bgPath.lineTo(
					(float) lastEndX
							+ DensityUtil.dip2px(context,
									GraphViewConfig.BORDER / 2), graphheight
							+ border);
			bgPath.lineTo(firstX, graphheight + border);
			bgPath.close();
			canvas.drawPath(bgPath, paintBackground);
		}
	}

	public int getBackgroundColor() {
		return paintBackground.getColor();
	}

	public float getDataPointsRadius() {
		return dataPointsRadius;
	}

	public boolean getDrawBackground() {
		return drawBackground;
	}

	public boolean getDrawDataPoints() {
		return drawDataPoints;
	}

	@Override
	public void setBackgroundColor(int color) {
		paintBackground.setColor(color);
	}

	public void setDataPointsRadius(float dataPointsRadius) {
		this.dataPointsRadius = dataPointsRadius;
	}

	public void setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
	}

	public void setDrawDataPoints(boolean drawDataPoints) {
		this.drawDataPoints = drawDataPoints;
	}

}