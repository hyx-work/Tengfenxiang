package com.android.tengfenxiang.view.graphview;

import java.util.ArrayList;
import java.util.List;

public class GraphViewSeries {

	static public class GraphViewSeriesStyle {
		public int color = 0xff0077cc;
		public int thickness = 3;
		private ValueDependentColor valueDependentColor;

		public GraphViewSeriesStyle() {
			super();
		}
		public GraphViewSeriesStyle(int color, int thickness) {
			super();
			this.color = color;
			this.thickness = thickness;
		}
		
		public ValueDependentColor getValueDependentColor() {
			return valueDependentColor;
		}

		public void setValueDependentColor(ValueDependentColor valueDependentColor) {
			this.valueDependentColor = valueDependentColor;
		}
	}

	final String description;
	final GraphViewSeriesStyle style;
	GraphViewDataInterface[] values;
	private final List<GraphView> graphViews = new ArrayList<GraphView>();

	public GraphViewSeries(GraphViewDataInterface[] values) {
		description = null;
		style = new GraphViewSeriesStyle();
		this.values = values;
        checkValueOrder();
	}

	public GraphViewSeries(String description, GraphViewSeriesStyle style, GraphViewDataInterface[] values) {
		super();
		this.description = description;
		if (style == null) {
			style = new GraphViewSeriesStyle();
		}
		this.style = style;
		this.values = values;
        checkValueOrder();
    }

	public void addGraphView(GraphView graphView) {
		this.graphViews.add(graphView);
	}

	@Deprecated
	public void appendData(GraphViewDataInterface value, boolean scrollToEnd) {
        if (value.getX() < values[values.length-1].getX()) {
            throw new IllegalArgumentException("new x-value must be greater then the last value. x-values has to be ordered in ASC.");
        }
		GraphViewDataInterface[] newValues = new GraphViewDataInterface[values.length + 1];
		int offset = values.length;
		System.arraycopy(values, 0, newValues, 0, offset);

		newValues[values.length] = value;
		values = newValues;
		for (GraphView g : graphViews) {
			if (scrollToEnd) {
				g.scrollToEnd();
			}
		}
	}

	public void appendData(GraphViewDataInterface value, boolean scrollToEnd, int maxDataCount) {
        if (value.getX() < values[values.length-1].getX()) {
            throw new IllegalArgumentException("new x-value must be greater then the last value. x-values has to be ordered in ASC.");
        }
		synchronized (values) {
			int curDataCount = values.length;
			GraphViewDataInterface[] newValues;
			if (curDataCount < maxDataCount) {
				// enough space
				newValues = new GraphViewDataInterface[curDataCount + 1];
				System.arraycopy(values, 0, newValues, 0, curDataCount);
				// append new data
				newValues[curDataCount] = value;
			} else {
				// we have to trim one data
				newValues = new GraphViewDataInterface[maxDataCount];
				System.arraycopy(values, 1, newValues, 0, curDataCount-1);
				// append new data
				newValues[maxDataCount-1] = value;
			}
			values = newValues;
		}

		// update linked graph views
		for (GraphView g : graphViews) {
			if (scrollToEnd) {
				g.scrollToEnd();
			}
		}
	}

	public GraphViewSeriesStyle getStyle() {
		return style;
	}

	public void removeGraphView(GraphView graphView) {
		graphViews.remove(graphView);
	}

	public void resetData(GraphViewDataInterface[] values) {
		this.values = values;
        checkValueOrder();
        for (GraphView g : graphViews) {
			g.redrawAll();
		}
	}

    private void checkValueOrder() {
        if (values.length>0) {
            double lx = values[0].getX();
            for (int i=1;i<values.length;i++) {
                if (lx > values[i].getX()) {
                    throw new IllegalArgumentException("The order of the values is not correct. X-Values have to be ordered ASC. First the lowest x value and at least the highest x value.");
                }
                lx = values[i].getX();
            }
        }
    }
}