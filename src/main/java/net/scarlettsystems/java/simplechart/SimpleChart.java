package net.scarlettsystems.java.simplechart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SimpleChart extends JPanel
{
	public enum XAxisLocation{NONE, BOTTOM, TOP, BOTH}
	public enum YAxisLocation{NONE, LEFT, RIGHT, BOTH}


	private final LineGraph graph;
	private XAxis xAxisBottom = null;
	private XAxis xAxisTop = null;
	private YAxis yAxisLeft = null;
	private YAxis yAxisRight = null;
	private final JPanel centrePanel;
	private final JPanel leftPanel;
	private final JPanel rightPanel;


	private XAxisLocation xAxisLocation;
	private YAxisLocation yAxisLocation;
	private int xTickCount = 10;
	private int yTickCount = 10;

	private LabelGenerator labelGenerator;

	public interface LabelGenerator
	{
		String getTopXTickLabel(double value, int index);
		String getBottomXTickLabel(double value, int index);
		String getLeftYTickLabel(double value, int index);
		String getRightYTickLabel(double value, int index);
	}

	public SimpleChart()
	{
		this.setLayout(new BorderLayout());
		this.graph = new LineGraph();
		this.graph.setResizeInputListener(getResizeInputListener());
		centrePanel = new JPanel();
		centrePanel.setLayout(new BorderLayout());
		leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());

		rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		centrePanel.add(graph, BorderLayout.CENTER);
		getBottomXAxis();
		getTopXAxis();
		getLeftAxis();
		getRightAxis();
		setXAxisLocation(XAxisLocation.BOTTOM);

		leftPanel.add(new Spacer(), BorderLayout.NORTH);
		leftPanel.add(new Spacer(), BorderLayout.SOUTH);
		rightPanel.add(new Spacer(), BorderLayout.NORTH);
		rightPanel.add(new Spacer(), BorderLayout.SOUTH);
		this.add(centrePanel, BorderLayout.CENTER);
		this.add(leftPanel, BorderLayout.WEST);
		this.add(rightPanel, BorderLayout.EAST);

		this.labelGenerator = new LabelGenerator()
		{
			@Override
			public String getTopXTickLabel(double value, int index)
			{
				return String.valueOf(value);
			}

			@Override
			public String getBottomXTickLabel(double value, int index)
			{
				return String.valueOf(value);
			}

			@Override
			public String getLeftYTickLabel(double value, int index)
			{
				return String.valueOf(value);
			}

			@Override
			public String getRightYTickLabel(double value, int index)
			{
				return String.valueOf(value);
			}
		};
	}

	private void getBottomXAxis()
	{
		if(xAxisBottom != null)
			centrePanel.remove(xAxisBottom);
		xAxisBottom = new XAxis(true, idx -> labelGenerator.getBottomXTickLabel(getXValue(idx), idx));
		centrePanel.add(xAxisBottom, BorderLayout.SOUTH);
	}

	private void getTopXAxis()
	{
		if(xAxisTop != null)
			centrePanel.remove(xAxisTop);
		xAxisTop = new XAxis(false, idx -> labelGenerator.getTopXTickLabel(getXValue(idx), idx));
		centrePanel.add(xAxisTop, BorderLayout.NORTH);
	}

	private void getLeftAxis()
	{
		if(yAxisLeft != null)
			leftPanel.remove(yAxisLeft);
		yAxisLeft = new YAxis(true, idx -> labelGenerator.getLeftYTickLabel(getYValue(idx), idx));
		leftPanel.add(yAxisLeft, BorderLayout.CENTER);
	}

	private void getRightAxis()
	{
		if(yAxisRight != null)
			rightPanel.remove(yAxisRight);
		yAxisRight = new YAxis(false, idx -> labelGenerator.getRightYTickLabel(getYValue(idx), idx));
		rightPanel.add(yAxisRight, BorderLayout.CENTER);
	}

	private double getXValue(int i)
	{
		double increment = (graph.getxAxisMax() - graph.getxAxisMin()) / (xAxisBottom.getTickCount() - 1);
		return graph.getxAxisMin() + increment * i ;
	}

	private double getYValue(int i)
	{
		double increment = (graph.getyAxisMax() - graph.getyAxisMin()) / (yAxisRight.getTickCount() - 1);
		return graph.getyAxisMin() + increment * i ;
	}

	/***
	 * Set the tick count for the X axis.
	 * The count includes the left and rightmost ticks of the axis, and thus should be a number greater than 2.
	 * @param count: the number of ticks
	 */
	public void setXTickCount(int count)
	{
		if(count < 2)
			throw new IllegalArgumentException("Number of X ticks must be greater than 2");
		xAxisBottom.setTickCount(count);
		xAxisTop.setTickCount(count);
	}

	/***
	 * Set the tick count for the Y axis.
	 * The count includes the bottom and topmost ticks of the axis, and thus should be a number greater than 2.
	 * @param count: the number of ticks
	 */
	public void setYTickCount(int count)
	{
		if(count < 2)
			throw new IllegalArgumentException("Number of Y ticks must be greater than 2");
		yAxisLeft.setTickCount(count);
		yAxisRight.setTickCount(count);
	}

	public void setXAxisMin(double value)
	{
		graph.setxAxisMin(value);
		getBottomXAxis();
		getTopXAxis();
	}

	public void setXAxisMax(double value)
	{
		graph.setxAxisMax(value);
		getBottomXAxis();
		getTopXAxis();
	}

	public void setYAxisMin(double value)
	{
		graph.setyAxisMin(value);
		getLeftAxis();
		getRightAxis();
	}

	public void setYAxisMax(double value)
	{
		graph.setyAxisMax(value);
		getLeftAxis();
		getRightAxis();
	}

	public void setXAxisAutoEnabled(boolean enabled)
	{
		graph.setxAutoScaleEnabled(enabled);
		getBottomXAxis();
		getTopXAxis();
		SwingUtilities.invokeLater(SimpleChart.this::refresh);
	}

	public void setYAxisAutoEnabled(boolean enabled)
	{
		graph.setyAutoScaleEnabled(enabled);
		getLeftAxis();
		getRightAxis();
		SwingUtilities.invokeLater(SimpleChart.this::refresh);
	}

	public void setLineThickness(float thickness)
	{
		graph.setLineThickness(thickness);
	}

	public void setLineColour(Object identifier, Color colour)
	{
		graph.setLineColour(identifier, colour);
	}

	/***
	 * Set the data for the line graph. The identifier should be a hashable object used to refer to the graph.
	 * Setting the data with the same identifier will overwrite the original line. Multiple line graphs can be drawn
	 * by specifying different identifiers. The graph's legend, if enabled, will call the identifier's toString() method
	 * to display its name. Arrays x and y must be of equal length.
	 *
	 * @param identifier line graphs hashable identifier object
	 * @param x data for the x-axis
	 * @param y data for the y-axis
	 */
	public void setData(Object identifier, double[] x, double[] y)
	{
		graph.setData(identifier, x, y);
	}

	public void setXAxisLocation(XAxisLocation l)
	{
		switch(l)
		{
			case BOTTOM:
				xAxisTop.setVisible(false);
				xAxisBottom.setVisible(true);
				break;
			case TOP:
				xAxisTop.setVisible(true);
				xAxisBottom.setVisible(false);
				break;
			case BOTH:
				xAxisTop.setVisible(true);
				xAxisBottom.setVisible(true);
				break;
			case NONE:
				xAxisTop.setVisible(false);
				xAxisBottom.setVisible(false);
				break;
		}
		SwingUtilities.invokeLater(this::revalidate);
		SwingUtilities.invokeLater(this::repaint);
	}

	public void setTickLabelGenerator(LabelGenerator g)
	{
		labelGenerator = g;
		SwingUtilities.invokeLater(SimpleChart.this::refresh);
	}

	private LineGraph.ResizeInputListener getResizeInputListener()
	{
		return new LineGraph.ResizeInputListener()
		{
			@Override
			public void onResizeRequested(Rectangle newRange)
			{
				double xRange = graph.getxAxisMax() -  graph.getxAxisMin();
				double yRange = graph.getyAxisMax() -  graph.getyAxisMin();
				double xMin = graph.getxAxisMin() + xRange * (newRange.getX() / graph.getWidth());
				double xMax = graph.getxAxisMin() + xRange * (newRange.getMaxX() / graph.getWidth());
				//Y coords are inverted due to pixel number vs. graph orientation
				double yMax = graph.getyAxisMin() + yRange * (1 - newRange.getY() / graph.getHeight());
				double yMin = graph.getyAxisMin() + yRange * (1 - newRange.getMaxY() / graph.getHeight());
				setXAxisMin(xMin);
				setXAxisMax(xMax);
				setYAxisMin(yMin);
				setYAxisMax(yMax);
				SwingUtilities.invokeLater(SimpleChart.this::refresh);
			}
		};
	}

	private void refresh()
	{
		this.revalidate();
		this.repaint();
		centrePanel.revalidate();
		leftPanel.revalidate();
		rightPanel.revalidate();
	}
}
