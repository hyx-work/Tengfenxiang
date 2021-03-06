package com.android.tengfenxiang.view.graphview;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.android.tengfenxiang.util.DensityUtil;
import com.android.tengfenxiang.view.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.android.tengfenxiang.view.graphview.GraphViewStyle.GridStyle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

abstract public class GraphView extends LinearLayout {
	static final public class GraphViewConfig {
		static final float BORDER = 20;

		static final float RIGHT_BORDER = 35;

		static final float RULING_LENGTH = 5;

		static final int MARKER_HEIGHT_OFFSET = 36;

		static final int MARKER_MARGIN = 5;

		static final int RECT_RADIUS = 3;
	}

	private class GraphViewContentView extends View {
		private float graphwidth;

		public GraphViewContentView(Context context) {
			super(context);
			setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
		}

		@Override
		protected void onDraw(Canvas canvas) {

			paint.setAntiAlias(true);

			paint.setStrokeWidth(DensityUtil.dip2px(context, 1));

			float border = DensityUtil.dip2px(context, GraphViewConfig.BORDER);
			float horstart = 0;
			float height = getHeight();
			float width = getWidth()
					- DensityUtil.dip2px(context, GraphViewConfig.RIGHT_BORDER);
			double maxY = getMaxY();
			double minY = getMinY();
			double maxX = getMaxX(false);
			double minX = getMinX(false);
			double diffX = maxX - minX;

			// 计算底部高度,包含x轴标签
			if (labelTextHeight == null || horLabelTextWidth == null) {
				paint.setTextSize(getGraphViewStyle().getTextSize());
				double testX = ((getMaxX(true) - getMinX(true)) * 0.783)
						+ getMinX(true);
				String testLabel = formatLabel(testX, true);
				paint.getTextBounds(testLabel, 0, testLabel.length(),
						textBounds);
				labelTextHeight = textBounds.height();
				horLabelTextWidth = textBounds.width();
			}
			border += labelTextHeight;

			// 计算图表高度
			float graphheight = height - (2 * border);
			graphwidth = width;

			if (horlabels == null) {
				horlabels = generateHorlabels(graphwidth);
			}
			if (verlabels == null) {
				verlabels = generateVerlabels(graphheight);
			}

			if (graphViewStyle.getGridStyle() != GridStyle.HORIZONTAL) {
				paint.setTextAlign(Align.LEFT);
				int vers = verlabels.length - 1;
				float rulingLength = DensityUtil.dip2px(context,
						GraphViewConfig.RULING_LENGTH);
				float start = horstart
						+ DensityUtil.dip2px(context,
								GraphViewConfig.BORDER / 2);
				float end = start + rulingLength;
				// x轴的终点
				float endX = getWidth() - DensityUtil.dip2px(context, 1);
				paint.setColor(graphViewStyle.getGridColor());
				for (int i = 0; i < verlabels.length; i++) {
					float y = ((graphheight / vers) * i) + border;
					if (i == verlabels.length - 1) {
						// 画统计表的x轴
						canvas.drawLine(
								horstart
										+ DensityUtil.dip2px(context,
												GraphViewConfig.BORDER / 2), y,
								endX, y, paint);
						// 画统计表x轴的箭头
						canvas.drawLine(getWidth() - rulingLength, y - 0.5f
								* rulingLength, endX, y, paint);
						canvas.drawLine(getWidth() - rulingLength, y + 0.5f
								* rulingLength, endX, y, paint);
					} else {
						// 画统计表y轴的刻度
						canvas.drawLine(start, y, end, y, paint);
					}
				}
			}

			drawHorizontalLabels(
					canvas,
					border,
					horstart
							+ DensityUtil.dip2px(context,
									GraphViewConfig.BORDER / 2), height,
					horlabels, graphwidth);

			paint.setColor(graphViewStyle.getHorizontalLabelsColor());
			paint.setTextAlign(Align.CENTER);

			if (maxY == minY) {
				if (maxY == 0) {
					maxY = 2.0d;
					minY = 0.0d;
				} else {
					maxY = maxY * 1.05d;
					minY = minY * 0.95d;
				}
			}

			double diffY = maxY - minY;
			paint.setStrokeCap(Paint.Cap.ROUND);

			for (int i = 0; i < graphSeries.size(); i++) {
				drawSeries(
						canvas,
						_values(i),
						graphwidth,
						graphheight,
						border,
						minX,
						minY,
						diffX,
						diffY,
						horstart
								+ DensityUtil.dip2px(context,
										GraphViewConfig.BORDER / 2),
						graphSeries.get(i).style);
			}

			if (showLegend)
				drawLegend(canvas, height, width);
		}
	}

	static public class GraphViewData implements GraphViewDataInterface {
		public final double valueX;
		public final double valueY;

		public GraphViewData(double valueX, double valueY) {
			super();
			this.valueX = valueX;
			this.valueY = valueY;
		}

		@Override
		public double getX() {
			return valueX;
		}

		@Override
		public double getY() {
			return valueY;
		}
	}

	public enum LegendAlign {
		TOP, MIDDLE, BOTTOM
	}

	private class VerLabelsView extends View {

		public VerLabelsView(Context context) {
			super(context);
			setLayoutParams(new LayoutParams(getGraphViewStyle()
					.getVerticalLabelsWidth() == 0 ? 100 : getGraphViewStyle()
					.getVerticalLabelsWidth(), LayoutParams.MATCH_PARENT));
		}

		@SuppressLint("DrawAllocation")
		@Override
		protected void onDraw(Canvas canvas) {
			paint.setStrokeWidth(0);

			if (labelTextHeight == null || verLabelTextWidth == null) {
				paint.setTextSize(getGraphViewStyle().getTextSize());
				double testY = ((getMaxY() - getMinY()) * 0.783) + getMinY();
				String testLabel = formatLabel(testY, false);
				paint.getTextBounds(testLabel, 0, testLabel.length(),
						textBounds);
				labelTextHeight = (textBounds.height());
				verLabelTextWidth = (textBounds.width());
			}
			if (getGraphViewStyle().getVerticalLabelsWidth() == 0
					&& getLayoutParams().width != verLabelTextWidth
							+ DensityUtil.dip2px(context,
									GraphViewConfig.BORDER / 2)) {
				setLayoutParams(new LayoutParams(verLabelTextWidth
						+ DensityUtil.dip2px(context,
								GraphViewConfig.BORDER / 2),
						LayoutParams.MATCH_PARENT));
			} else if (getGraphViewStyle().getVerticalLabelsWidth() != 0
					&& getGraphViewStyle().getVerticalLabelsWidth() != getLayoutParams().width) {
				setLayoutParams(new LayoutParams(getGraphViewStyle()
						.getVerticalLabelsWidth(), LayoutParams.MATCH_PARENT));
			}

			float border = DensityUtil.dip2px(context, GraphViewConfig.BORDER);
			border += labelTextHeight;
			float height = getHeight();
			float graphheight = height - (2 * border);

			if (verlabels == null) {
				verlabels = generateVerlabels(graphheight);
			}

			paint.setTextAlign(getGraphViewStyle().getVerticalLabelsAlign());
			int labelsWidth = getWidth();
			int labelsOffset = 0;
			if (getGraphViewStyle().getVerticalLabelsAlign() == Align.RIGHT) {
				labelsOffset = labelsWidth;
			} else if (getGraphViewStyle().getVerticalLabelsAlign() == Align.CENTER) {
				labelsOffset = labelsWidth / 2;
			}
			int vers = verlabels.length - 1;
			for (int i = 0; i < verlabels.length; i++) {
				float y = ((graphheight / vers) * i) + border;
				paint.setColor(graphViewStyle.getVerticalLabelsColor());
				canvas.drawText(verlabels[i], labelsOffset,
						y + DensityUtil.dip2px(context, 3), paint);
			}

			paint.setTextAlign(Align.LEFT);
		}
	}

	protected Context context;
	protected final Paint paint;
	private String[] horlabels;
	private String[] verlabels;
	private boolean scrollable;
	private boolean disableTouch;
	private double viewportStart;
	private double viewportSize;
	private final View viewVerLabels;
	private final NumberFormat[] numberformatter = new NumberFormat[2];
	private final List<GraphViewSeries> graphSeries;
	private boolean showLegend = false;
	private LegendAlign legendAlign = LegendAlign.MIDDLE;
	private boolean manualYAxis;
	private boolean manualMaxY;
	private boolean manualMinY;
	private double manualMaxYValue;
	private double manualMinYValue;
	protected GraphViewStyle graphViewStyle;
	private final GraphViewContentView graphViewContentView;
	private CustomLabelFormatter customLabelFormatter;
	private Integer labelTextHeight;
	private Integer horLabelTextWidth;
	private Integer verLabelTextWidth;
	private final Rect textBounds = new Rect();
	private boolean staticHorizontalLabels;
	private boolean staticVerticalLabels;
	private boolean showHorizontalLabels = true;
	private boolean showVerticalLabels = true;

	public GraphView(Context context, AttributeSet attrs) {
		this(context, attrs.getAttributeValue(null, "title"));

		int width = attrs.getAttributeIntValue("android", "layout_width",
				LayoutParams.MATCH_PARENT);
		int height = attrs.getAttributeIntValue("android", "layout_height",
				LayoutParams.MATCH_PARENT);
		setLayoutParams(new LayoutParams(width, height));
	}

	public GraphView(Context context, String title) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		this.context = context;

		graphViewStyle = new GraphViewStyle();
		graphViewStyle.useTextColorFromTheme(context);

		paint = new Paint();
		graphSeries = new ArrayList<GraphViewSeries>();

		viewVerLabels = new VerLabelsView(context);
		addView(viewVerLabels);
		graphViewContentView = new GraphViewContentView(context);
		addView(graphViewContentView, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
	}

	private GraphViewDataInterface[] _values(int idxSeries) {
		GraphViewDataInterface[] values = graphSeries.get(idxSeries).values;
		synchronized (values) {
			if (viewportStart == 0 && viewportSize == 0) {
				return values;
			} else {
				List<GraphViewDataInterface> listData = new ArrayList<GraphViewDataInterface>();
				for (int i = 0; i < values.length; i++) {
					if (values[i].getX() >= viewportStart) {
						if (values[i].getX() > viewportStart + viewportSize) {
							listData.add(values[i]);
							break;
						} else {
							listData.add(values[i]);
						}
					} else {
						if (listData.isEmpty()) {
							listData.add(values[i]);
						}
						listData.set(0, values[i]);
					}
				}
				return listData.toArray(new GraphViewDataInterface[listData
						.size()]);
			}
		}
	}

	public void addSeries(GraphViewSeries series) {
		series.addGraphView(this);
		graphSeries.add(series);
		redrawAll();
	}

	protected void drawHorizontalLabels(Canvas canvas, float border,
			float horstart, float height, String[] horlabels, float graphwidth) {
		int hors = horlabels.length - 1;
		// y轴的终点
		float endY = 1.0f * border / 3.0f;
		for (int i = 0; i < horlabels.length; i++) {
			paint.setColor(graphViewStyle.getGridColor());
			float x = ((graphwidth / hors) * i) + horstart;
			float rulingLength = DensityUtil.dip2px(context,
					GraphViewConfig.RULING_LENGTH);
			if (graphViewStyle.getGridStyle() != GridStyle.VERTICAL) {
				if (0 == i) {
					// 画统计表y轴
					canvas.drawLine(x, height - border, x, endY, paint);
					// 画统计表y轴箭头
					canvas.drawLine(x - 0.5f * rulingLength, endY + 0.866f
							* rulingLength, x, endY, paint);
					canvas.drawLine(x + 0.5f * rulingLength, endY + 0.866f
							* rulingLength, x, endY, paint);
				} else {
					// 画统计表x轴刻度
					canvas.drawLine(x, height - border, x, height - border
							- rulingLength, paint);
				}
			}
			if (showHorizontalLabels) {
				paint.setTextAlign(Align.CENTER);
				paint.setColor(graphViewStyle.getHorizontalLabelsColor());
				canvas.drawText(horlabels[i], x,
						height - DensityUtil.dip2px(context, 5), paint);
			}
		}
	}

	protected void drawLegend(Canvas canvas, float height, float width) {
		float textSize = paint.getTextSize();
		int spacing = getGraphViewStyle().getLegendSpacing();
		int border = getGraphViewStyle().getLegendBorder();
		int legendWidth = getGraphViewStyle().getLegendWidth();

		int shapeSize = (int) (textSize * 0.8d);

		paint.setARGB(180, 100, 100, 100);
		float legendHeight = (shapeSize + spacing) * graphSeries.size() + 2
				* border - spacing;
		float lLeft = width - legendWidth - border * 2;
		float lTop;
		switch (legendAlign) {
		case TOP:
			lTop = 0;
			break;
		case MIDDLE:
			lTop = height / 2 - legendHeight / 2;
			break;
		default:
			lTop = height - GraphViewConfig.BORDER - legendHeight
					- getGraphViewStyle().getLegendMarginBottom();
		}
		float lRight = lLeft + legendWidth;
		float lBottom = lTop + legendHeight;
		canvas.drawRoundRect(new RectF(lLeft, lTop, lRight, lBottom), 8, 8,
				paint);

		for (int i = 0; i < graphSeries.size(); i++) {
			paint.setColor(graphSeries.get(i).style.color);
			canvas.drawRect(new RectF(lLeft + border, lTop + border
					+ (i * (shapeSize + spacing)), lLeft + border + shapeSize,
					lTop + border + (i * (shapeSize + spacing)) + shapeSize),
					paint);
			if (graphSeries.get(i).description != null) {
				paint.setColor(Color.WHITE);
				paint.setTextAlign(Align.LEFT);
				canvas.drawText(graphSeries.get(i).description, lLeft + border
						+ shapeSize + spacing, lTop + border + shapeSize
						+ (i * (shapeSize + spacing)), paint);
			}
		}
	}

	abstract protected void drawSeries(Canvas canvas,
			GraphViewDataInterface[] values, float graphwidth,
			float graphheight, float border, double minX, double minY,
			double diffX, double diffY, float horstart,
			GraphViewSeriesStyle style);

	@Deprecated
	protected String formatLabel(double value, boolean isValueX) {
		if (customLabelFormatter != null) {
			String label = customLabelFormatter.formatLabel(value, isValueX);
			if (label != null) {
				return label;
			}
		}
		int i = isValueX ? 1 : 0;
		if (numberformatter[i] == null) {
			numberformatter[i] = NumberFormat.getNumberInstance();
			double highestvalue = isValueX ? getMaxX(false) : getMaxY();
			double lowestvalue = isValueX ? getMinX(false) : getMinY();
			if (highestvalue - lowestvalue < 100) {
				numberformatter[i].setMaximumFractionDigits(1);
			} else {
				numberformatter[i].setMaximumFractionDigits(0);
			}
		}
		return numberformatter[i].format(value);
	}

	private String[] generateHorlabels(float graphwidth) {
		int numLabels = getGraphViewStyle().getNumHorizontalLabels() - 1;
		if (numLabels < 0) {
			if (graphwidth <= 0)
				graphwidth = 1f;
			numLabels = (int) (graphwidth / (horLabelTextWidth * 2));
		}

		String[] labels = new String[numLabels + 1];
		double min = getMinX(false);
		double max = getMaxX(false);
		for (int i = 0; i <= numLabels; i++) {
			labels[i] = formatLabel(min + ((max - min) * i / numLabels), true);
		}
		return labels;
	}

	synchronized private String[] generateVerlabels(float graphheight) {
		int numLabels = getGraphViewStyle().getNumVerticalLabels() - 1;
		if (numLabels < 0) {
			if (graphheight <= 0)
				graphheight = 1f;
			numLabels = (int) (graphheight / (labelTextHeight * 3));
		}
		String[] labels = new String[numLabels + 1];
		double min = getMinY();
		double max = getMaxY();
		if (max == min) {
			if (max == 0) {
				max = 2.0d;
				min = 0.0d;
			} else {
				max = max * 1.05d;
				min = min * 0.95d;
			}
		}

		for (int i = 0; i <= numLabels; i++) {
			labels[numLabels - i] = formatLabel(min
					+ ((max - min) * i / numLabels), false);
		}
		return labels;
	}

	public CustomLabelFormatter getCustomLabelFormatter() {
		return customLabelFormatter;
	}

	public GraphViewStyle getGraphViewStyle() {
		return graphViewStyle;
	}

	public LegendAlign getLegendAlign() {
		return legendAlign;
	}

	@Deprecated
	public float getLegendWidth() {
		return getGraphViewStyle().getLegendWidth();
	}

	protected double getMaxX(boolean ignoreViewport) {
		if (!ignoreViewport && viewportSize != 0) {
			return viewportStart + viewportSize;
		} else {
			double highest = 0;
			if (graphSeries.size() > 0) {
				GraphViewDataInterface[] values = graphSeries.get(0).values;
				if (values.length == 0) {
					highest = 0;
				} else {
					highest = values[values.length - 1].getX();
				}
				for (int i = 1; i < graphSeries.size(); i++) {
					values = graphSeries.get(i).values;
					if (values.length > 0) {
						highest = Math.max(highest,
								values[values.length - 1].getX());
					}
				}
			}
			return highest;
		}
	}

	protected double getMaxY() {
		double largest;
		if (manualYAxis || manualMaxY) {
			largest = manualMaxYValue;
		} else {
			largest = Integer.MIN_VALUE;
			for (int i = 0; i < graphSeries.size(); i++) {
				GraphViewDataInterface[] values = _values(i);
				for (int ii = 0; ii < values.length; ii++)
					if (values[ii].getY() > largest)
						largest = values[ii].getY();
			}
		}
		return largest;
	}

	protected double getMinX(boolean ignoreViewport) {
		if (!ignoreViewport && viewportSize != 0) {
			return viewportStart;
		} else {
			double lowest = 0;
			if (graphSeries.size() > 0) {
				GraphViewDataInterface[] values = graphSeries.get(0).values;
				if (values.length == 0) {
					lowest = 0;
				} else {
					lowest = values[0].getX();
				}
				for (int i = 1; i < graphSeries.size(); i++) {
					values = graphSeries.get(i).values;
					if (values.length > 0) {
						lowest = Math.min(lowest, values[0].getX());
					}
				}
			}
			return lowest;
		}
	}

	protected double getMinY() {
		double smallest;
		if (manualYAxis || manualMinY) {
			smallest = manualMinYValue;
		} else {
			smallest = Integer.MAX_VALUE;
			for (int i = 0; i < graphSeries.size(); i++) {
				GraphViewDataInterface[] values = _values(i);
				for (int ii = 0; ii < values.length; ii++)
					if (values[ii].getY() < smallest)
						smallest = values[ii].getY();
			}
		}
		return smallest;
	}

	public double getViewportSize() {
		return viewportSize;
	}

	public boolean isDisableTouch() {
		return disableTouch;
	}

	public boolean isScrollable() {
		return scrollable;
	}

	public boolean isShowLegend() {
		return showLegend;
	}

	public void redrawAll() {
		if (!staticVerticalLabels)
			verlabels = null;
		if (!staticHorizontalLabels)
			horlabels = null;
		numberformatter[0] = null;
		numberformatter[1] = null;
		labelTextHeight = null;
		horLabelTextWidth = null;
		verLabelTextWidth = null;

		invalidate();
		viewVerLabels.invalidate();
		graphViewContentView.invalidate();
	}

	public void removeAllSeries() {
		for (GraphViewSeries s : graphSeries) {
			s.removeGraphView(this);
		}
		while (!graphSeries.isEmpty()) {
			graphSeries.remove(0);
		}
		redrawAll();
	}

	public void removeSeries(GraphViewSeries series) {
		series.removeGraphView(this);
		graphSeries.remove(series);
		redrawAll();
	}

	public void removeSeries(int index) {
		if (index < 0 || index >= graphSeries.size()) {
			throw new IndexOutOfBoundsException("No series at index " + index);
		}

		removeSeries(graphSeries.get(index));
	}

	public void scrollToEnd() {
		if (!scrollable)
			throw new IllegalStateException("This GraphView is not scrollable.");
		double max = getMaxX(true);
		viewportStart = max - viewportSize;

		if (!staticVerticalLabels)
			verlabels = null;
		if (!staticHorizontalLabels)
			horlabels = null;

		invalidate();
		viewVerLabels.invalidate();
		graphViewContentView.invalidate();
	}

	public void setCustomLabelFormatter(
			CustomLabelFormatter customLabelFormatter) {
		this.customLabelFormatter = customLabelFormatter;
	}

	public void setDisableTouch(boolean disableTouch) {
		this.disableTouch = disableTouch;
	}

	public void setGraphViewStyle(GraphViewStyle style) {
		graphViewStyle = style;
		labelTextHeight = null;
	}

	public void setHorizontalLabels(String[] horlabels) {
		staticHorizontalLabels = horlabels != null;
		this.horlabels = horlabels;
	}

	public void setLegendAlign(LegendAlign legendAlign) {
		this.legendAlign = legendAlign;
	}

	@Deprecated
	public void setLegendWidth(float legendWidth) {
		getGraphViewStyle().setLegendWidth((int) legendWidth);
	}

	public void setManualYAxis(boolean manualYAxis) {
		this.manualYAxis = manualYAxis;
	}

	public void setManualMaxY(boolean manualMaxY) {
		this.manualMaxY = manualMaxY;
	}

	public void setManualMinY(boolean manualMinY) {
		this.manualMinY = manualMinY;
	}

	public void setManualYAxisBounds(double max, double min) {
		manualMaxYValue = max;
		manualMinYValue = min;
		manualYAxis = true;
	}

	public void setManualYMaxBound(double max) {
		manualMaxYValue = max;
		manualMaxY = true;
	}

	public void setManualYMinBound(double min) {
		manualMinYValue = min;
		manualMinY = true;
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	public void setVerticalLabels(String[] verlabels) {
		staticVerticalLabels = verlabels != null;
		this.verlabels = verlabels;
	}

	public void setViewPort(double start, double size) {
		if (size < 0) {
			throw new IllegalArgumentException(
					"Viewport size must be greater than 0!");
		}
		viewportStart = start;
		viewportSize = size;
	}

	public void setShowHorizontalLabels(boolean showHorizontalLabels) {
		this.showHorizontalLabels = showHorizontalLabels;
		redrawAll();
	}

	public boolean getShowHorizontalLabels() {
		return showHorizontalLabels;
	}

	public void setShowVerticalLabels(boolean showVerticalLabels) {
		this.showVerticalLabels = showVerticalLabels;
		if (this.showVerticalLabels) {
			addView(viewVerLabels, 0);
		} else {
			removeView(viewVerLabels);
		}
	}

	public boolean getShowVerticalLabels() {
		return showVerticalLabels;
	}

}