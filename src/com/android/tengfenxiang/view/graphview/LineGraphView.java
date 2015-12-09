package com.android.tengfenxiang.view.graphview;

import com.android.tengfenxiang.util.DensityUtil;
import com.android.tengfenxiang.view.graphview.GraphViewSeries.GraphViewSeriesStyle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

public class LineGraphView extends GraphView {
	private Paint paintBackground;
	private boolean drawBackground;
	private boolean drawDataPoints;
	private float dataPointsRadius = 10f;
	private float markerX;
	private float markerY;
	private String content;
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

		paint.setStrokeWidth(DensityUtil.dip2px(context, style.thickness * 1.0f));
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

				canvas.drawLine(startX, startY, endX, endY, paint);
				if (bgPath != null) {
					if (i == 1) {
						firstX = startX;
						bgPath.moveTo(startX, startY + style.thickness);
					}
					bgPath.lineTo(endX, endY + style.thickness);
				}

				if (i == values.length - 1) {
					markerX = endX;
					markerY = endY;
					content = String.valueOf(values[i].getY());
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

		paint.setColor(Color.rgb(250, 98, 65));

		canvas.drawCircle(markerX, markerY,
				DensityUtil.dip2px(context, GraphViewConfig.MARKER_MARGIN),
				paint);

		paint.setColor(Color.WHITE);
		canvas.drawCircle(markerX, markerY,
				DensityUtil.dip2px(context, GraphViewConfig.RECT_RADIUS), paint);

		drawMarker(canvas, content, markerX, markerY + style.thickness);
	}

	private void drawMarker(Canvas canvas, String content, float x, float y) {
		Rect popupTextRect = new Rect();
		paint.getTextBounds(content, 0, content.length(), popupTextRect);
		paint.setAntiAlias(true);
		paint.setColor(Color.rgb(250, 98, 65));

		RectF r = new RectF(x - popupTextRect.width() * 5 / 6
				- GraphViewConfig.MARKER_MARGIN, y
				- DensityUtil.dip2px(context,
						GraphViewConfig.MARKER_HEIGHT_OFFSET), x
				+ popupTextRect.width() * 1 / 6
				+ DensityUtil.dip2px(context, GraphViewConfig.MARKER_MARGIN), y
				- DensityUtil.dip2px(context,
						GraphViewConfig.MARKER_HEIGHT_OFFSET / 2));
		canvas.drawRoundRect(r,
				DensityUtil.dip2px(context, GraphViewConfig.RECT_RADIUS),
				DensityUtil.dip2px(context, GraphViewConfig.RECT_RADIUS), paint);

		Path path = new Path();
		path.moveTo(
				x,
				y
						- DensityUtil.dip2px(context,
								GraphViewConfig.MARKER_HEIGHT_OFFSET / 2));
		path.lineTo(x, y - DensityUtil.dip2px(context, 10));
		path.lineTo(
				x - DensityUtil.dip2px(context, 5),
				y
						- DensityUtil.dip2px(context,
								GraphViewConfig.MARKER_HEIGHT_OFFSET / 2));
		path.close();
		canvas.drawPath(path, paint);
		paint.setColor(Color.WHITE);
		FontMetricsInt fontMetrics = paint.getFontMetricsInt();
		int baseline = (int) (r.top
				+ (r.bottom - r.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top);
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(content, r.centerX(), baseline, paint);
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