package com.android.tengfenxiang.view.graphview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.ContextThemeWrapper;

public class GraphViewStyle {
	private int verticalLabelsColor;
	private int horizontalLabelsColor;
	private int gridColor;
	private GridStyle gridStyle = GridStyle.BOTH;
	private float textSize;
	private int verticalLabelsWidth;
	private int numVerticalLabels;
	private int numHorizontalLabels;
	private int legendWidth;
	private int legendBorder;
	private int legendSpacing;
	private int legendMarginBottom;
	private Align verticalLabelsAlign;

	public GraphViewStyle() {
		setDefaults();
	}

	public GraphViewStyle(int vLabelsColor, int hLabelsColor, int gridColor) {
		setDefaults();
		this.verticalLabelsColor = vLabelsColor;
		this.horizontalLabelsColor = hLabelsColor;
		this.gridColor = gridColor;
	}

	public int getGridColor() {
		return gridColor;
	}
	
	public GridStyle getGridStyle() {
		return gridStyle;
	}

	public int getHorizontalLabelsColor() {
		return horizontalLabelsColor;
	}

	public int getLegendBorder() {
		return legendBorder;
	}

	public int getLegendSpacing() {
		return legendSpacing;
	}

	public int getLegendWidth() {
		return legendWidth;
	}

	public int getLegendMarginBottom() {
		return legendMarginBottom;
	}

	public int getNumHorizontalLabels() {
		return numHorizontalLabels;
	}

	public int getNumVerticalLabels() {
		return numVerticalLabels;
	}

	public float getTextSize() {
		return textSize;
	}

	public Align getVerticalLabelsAlign() {
		return verticalLabelsAlign;
	}

	public int getVerticalLabelsColor() {
		return verticalLabelsColor;
	}

	public int getVerticalLabelsWidth() {
		return verticalLabelsWidth;
	}

	private void setDefaults() {
		verticalLabelsColor = Color.WHITE;
		horizontalLabelsColor = Color.WHITE;
		gridColor = Color.DKGRAY;
		textSize = 30f;
		legendWidth = 120;
		legendBorder = 10;
		legendSpacing = 10;
		legendMarginBottom = 0;
		verticalLabelsAlign = Align.LEFT;
	}

	public void setGridStyle(GridStyle style) {
		gridStyle = style;
	}
	
	public void setGridColor(int c) {
		gridColor = c;
	}

	public void setHorizontalLabelsColor(int c) {
		horizontalLabelsColor = c;
	}

	public void setLegendBorder(int legendBorder) {
		this.legendBorder = legendBorder;
	}

	public void setLegendSpacing(int legendSpacing) {
		this.legendSpacing = legendSpacing;
	}

	public void setLegendWidth(int legendWidth) {
		this.legendWidth = legendWidth;
	}

	public void setLegendMarginBottom(int legendMarginBottom) {
		this.legendMarginBottom = legendMarginBottom;
	}

	public void setNumHorizontalLabels(int numHorizontalLabels) {
		this.numHorizontalLabels = numHorizontalLabels;
	}

	public void setNumVerticalLabels(int numVerticalLabels) {
		this.numVerticalLabels = numVerticalLabels;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public void setVerticalLabelsAlign(Align verticalLabelsAlign) {
		this.verticalLabelsAlign = verticalLabelsAlign;
	}

	public void setVerticalLabelsColor(int c) {
		verticalLabelsColor = c;
	}

	public void setVerticalLabelsWidth(int verticalLabelsWidth) {
		this.verticalLabelsWidth = verticalLabelsWidth;
	}

	public void useTextColorFromTheme(Context context) {
		if (context instanceof ContextThemeWrapper) {
			TypedArray array = ((ContextThemeWrapper) context).getTheme().obtainStyledAttributes(new int[] {android.R.attr.textColorPrimary});
			int color = array.getColor(0, getVerticalLabelsColor());
			array.recycle();

			setVerticalLabelsColor(color);
			setHorizontalLabelsColor(color);
		}
	}

	public enum GridStyle {
		BOTH, VERTICAL, HORIZONTAL
	}
}