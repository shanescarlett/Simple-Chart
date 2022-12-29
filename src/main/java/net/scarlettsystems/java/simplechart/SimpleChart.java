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
	private Component xAxisBottom = null;
	private Component xAxisTop = null;
	private Component yAxisLeft = null;
	private Component yAxisRight = null;


	private XAxisLocation xAxisLocation;
	private YAxisLocation yAxisLocation;
	private int xTickCount = 10;
	private int yTickCount = 10;

	public SimpleChart()
	{
		this.setLayout(new BorderLayout());
		this.graph = new LineGraph();
		this.add(graph, BorderLayout.CENTER);
		getBottomXAxis();
		getTopXAxis();
		setXAxisLocation(XAxisLocation.BOTTOM);
	}

	private void getBottomXAxis()
	{
		if(xAxisBottom != null)
			this.remove(xAxisBottom);
		xAxisBottom = new XAxis(true, idx -> String.valueOf(getXValue(idx)));
		this.add(xAxisBottom, BorderLayout.SOUTH);
	}

	private void getTopXAxis()
	{
		if(xAxisTop != null)
			this.remove(xAxisTop);
		xAxisTop = new XAxis(false, idx -> String.valueOf(getXValue(idx)));
		this.add(xAxisTop, BorderLayout.NORTH);
	}

	private static JPanel getHorizontalTicks(int count)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalStrut(0));
		for(int i = 0; i < count; i++)
		{
			Tick tick = new Tick(Tick.Orientation.VERTICAL, 1);
			panel.add(tick);
			if(i < (count - 1))
			{
				panel.add(Box.createHorizontalGlue());
			}
		}
		panel.add(Box.createHorizontalStrut(0));
		return panel;
	}

	private JPanel getHorizontalLabels()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		for(int i = 0; i < xTickCount; i++)
		{
			JLabel label = new JLabel();
//			if(xTickLabelGenerator != null)
//				label.setText(xTickLabelGenerator.getLabel(getXValue(i)));
//			else
//				label.setText(String.valueOf(getXValue(i)));

			if(i == 0)
			{
				label.setHorizontalAlignment(SwingConstants.LEFT);
			}
			else if(i == xTickCount - 1)
			{
				label.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			else
			{
				label.setHorizontalAlignment(SwingConstants.CENTER);
			}

			panel.add(label);
			panel.addComponentListener(new ComponentAdapter()
			{
				@Override
				public void componentResized(ComponentEvent e)
				{
					sizeXLabels(panel);
				}

				@Override
				public void componentMoved(ComponentEvent e)
				{
					sizeXLabels(panel);
				}
			});
		}
		return panel;
	}

	private void sizeXLabels(JPanel panel)
	{
		for(int j = 0; j < panel.getComponentCount(); j++)
		{
			JLabel lab = ((JLabel)panel.getComponent(j));

			if(j == 0)
			{
				setAllSizes(lab, (int)Math.round(0.5/(xTickCount - 1) * panel.getWidth()), panel.getHeight());
			}
			else if(j == panel.getComponentCount() - 1)
			{
				setAllSizes(lab, (int)Math.round(0.5/(xTickCount - 1) * panel.getWidth()), panel.getHeight());
			}
			else
			{
				setAllSizes(lab, (int)Math.round(1.0/(xTickCount - 1) * panel.getWidth()), panel.getHeight());
			}
		}
	}

	private static void setAllSizes(Component component, int width, int height)
	{
		component.setSize(new Dimension(width, height));
		component.setPreferredSize(new Dimension(width, height));
		component.setMinimumSize(new Dimension(width, height));
		component.setMaximumSize(new Dimension(width, height));
	}

	private double getXValue(int i)
	{
		double increment = (graph.getxAxisMax() - graph.getxAxisMin()) / (xTickCount - 1);
		return graph.getxAxisMin() + increment * i ;
	}

	public void setXTickCount(int count)
	{
		if(count < 2)
			throw new IllegalArgumentException("Number of X ticks must be greater than 2");
		this.xTickCount = count;
		getBottomXAxis();
		getTopXAxis();
	}

	public void setYTickCount(int count)
	{
		if(count < 2)
			throw new IllegalArgumentException("Number of Y ticks must be greater than 2");
		this.yTickCount = count;
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
	}

	public void setYAxisMax(double value)
	{
		graph.setyAxisMax(value);
	}

	public void setLineThickness(float thickness)
	{
		graph.setLineThickness(thickness);
	}

	public void setLineColour(Object identifier, Color colour)
	{
		graph.setLineColour(identifier, colour);
	}

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
	}
}
