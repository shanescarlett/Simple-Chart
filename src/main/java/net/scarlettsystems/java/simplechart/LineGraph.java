package net.scarlettsystems.java.simplechart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

class LineGraph extends JComponent
{
	private double xAxisMin = -1;
	private double xAxisMax = 1;
	private double yAxisMin = 0;
	private double yAxisMax = 1;
	private final HashMap<Object, double[]> xValues = new HashMap<>();
	private final HashMap<Object, double[]> yValues = new HashMap<>();
	private final HashMap<Object, Color> lineColours = new HashMap<>();
	private boolean xAutoScale = true;
	private boolean yAutoScale = true;
	private boolean legendEnabled = false;
	private boolean gridEnabled = false;

	private float lineThickness = 1;

	private Point dragStartPoint = null;
	private Rectangle selectionRectangle = null;
	private ResizeInputListener resizeInputListener = null;
	private boolean resizeEnabled = true;
	private int xGridCount = 5;
	private int yGridCount = 5;


	private static Stroke dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,0, new float[]{9}, 0);

	interface ResizeInputListener
	{
		void onResizeRequested(Rectangle newRange);
	}

	LineGraph()
	{
		this.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(!resizeEnabled)
					return;
				dragStartPoint = new Point(e.getX(), e.getY());
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if(!resizeEnabled)
					return;
				int x = dragStartPoint.x;
				int y = dragStartPoint.y;
				int x1 = e.getX();
				int y1 = e.getY();
				Rectangle selectedArea = new Rectangle(Math.min(x, x1), Math.min(y, y1), Math.abs(x - x1), Math.abs(y - y1));
				if(resizeInputListener != null)
					resizeInputListener.onResizeRequested(selectedArea);
				dragStartPoint = null;
				selectionRectangle = null;
				repaint();
			}
		});

		this.addMouseMotionListener(new MouseAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent e)
			{
				if(!resizeEnabled)
					return;
				int x = dragStartPoint.x;
				int y = dragStartPoint.y;
				int x1 = e.getX();
				int y1 = e.getY();
				selectionRectangle = new Rectangle(Math.min(x, x1), Math.min(y, y1), Math.abs(x - x1), Math.abs(y - y1));
				repaint();
			}
		});
	}

	@Override
	public Dimension getPreferredSize()
	{
		return getParent().getSize();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		// Draw background
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());

		if(!check())
			return;

		// Draw line
		g2.setStroke(new BasicStroke(lineThickness));
		FontMetrics fm = g.getFontMetrics();
		int squareSize = 16;
		int gap = 8;
		int legendXStart = 10;
		int legendYStart = 10;

		for(Object key : xValues.keySet())
		{
			if(lineColours.containsKey(key))
			{
				g.setColor(lineColours.get(key));
			}
			else
			{
				//Set to default colour
			}
			double[] x = xValues.get(key);
			double[] y = yValues.get(key);

			boolean accumulationSet = false;
			int yMax = Integer.MIN_VALUE;
			int yMin = Integer.MAX_VALUE;
			for(int i = 0; i < x.length - 1; i++)
			{
				int xStart = xValueToPixel(x[i]);
				int xStop = xValueToPixel(x[i + 1]);
				int yStart = yValueToPixel(y[i]);
				int yStop = yValueToPixel(y[i + 1]);

				//No need to draw if start and stop are both out of drawable area
				if(!withinBounds(xStart, yStart) && !withinBounds(xStop, yStop))
					continue;

				if(xStart == xStop)
				{
					//If the data lands on the same discrete pixel, we do not draw but accumulate until the next pixel
					if(yStart < yMin)
						yMin = yStart;
					if(yStart > yMax)
						yMax = yStart;
					if(yStop < yMin)
						yMin = yStop;
					if(yStop > yMax)
						yMax = yStop;
					accumulationSet = true;
				}
				else
				{
					if(accumulationSet)
					{
						g.drawLine(xStart, yMin, xStart, yMax);
						accumulationSet = false;
					}
					g.drawLine(xStart, yStart, xStop, yStop);
				}
			}

			if(gridEnabled)
			{
				g2.setColor(getForeground());
				g2.setStroke(dashedStroke);
				for(int i = 1; i < xGridCount - 1; i++)
				{
					int xPos = (int)Math.round((double)i * getWidth() / (xGridCount - 1));
					g2.drawLine(xPos, 0, xPos, getHeight());
				}
				for(int i = 1; i < yGridCount - 1; i++)
				{
					int yPos = (int)Math.round((double)i * getHeight() / (yGridCount - 1));
					g2.drawLine(0, yPos, getWidth(), yPos);
				}
			}

			if(legendEnabled)
			{

				Rectangle2D rect = fm.getStringBounds(key.toString(), g);
				g.setColor(lineColours.get(key));
				g.fillRect(legendXStart, legendYStart, squareSize, squareSize);
				g.setColor(this.getForeground());
				g.drawString(key.toString(), legendXStart + squareSize + gap, legendYStart + (int)rect.getHeight()/2);
				legendYStart += squareSize + gap;
			}

			if(selectionRectangle != null)
			{
				g.setColor(Color.WHITE);
				g.drawRect(selectionRectangle.x, selectionRectangle.y, selectionRectangle.width, selectionRectangle.height);
			}
		}


	}

	private boolean check()
	{
		for(Object key : xValues.keySet())
		{
			if (xValues.get(key).length < 2)
			{
				System.err.println("Data too short to draw line graph");
				return false;
			}
		}

		if(xAxisMax <= xAxisMin)
		{
			System.err.println("X axis min is larger than X axis max");
			return false;
		}

		if(yAxisMax <= yAxisMin)
		{
			System.err.println("Y axis min is larger than Y axis max");
			return false;
		}

		return true;
	}

	private boolean withinBounds(int x, int y)
	{
		return x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight();
	}

	private int xValueToPixel(double value)
	{
		double portion = (value - xAxisMin) / (xAxisMax - xAxisMin);
		return (int)Math.round(portion * (double)getWidth());
	}

	private int yValueToPixel(double value)
	{
		double portion = (value - yAxisMin) / (yAxisMax - yAxisMin);
		return getHeight() - (int)Math.round(portion * (double)getHeight());
	}

	double getxAxisMin()
	{
		return xAxisMin;
	}

	void setxAxisMin(double xAxisMin)
	{
		xAutoScale = false;
		this.xAxisMin = xAxisMin;
		repaint();
	}

	double getxAxisMax()
	{
		return xAxisMax;
	}

	void setxAxisMax(double xAxisMax)
	{
		xAutoScale = false;
		this.xAxisMax = xAxisMax;
		repaint();
	}

	double getyAxisMin()
	{
		return yAxisMin;
	}

	void setyAxisMin(double yAxisMin)
	{
		yAutoScale = false;
		this.yAxisMin = yAxisMin;
		repaint();
	}

	double getyAxisMax()
	{
		return yAxisMax;
	}

	void setyAxisMax(double yAxisMax)
	{
		yAutoScale = false;
		this.yAxisMax = yAxisMax;
		repaint();
	}

	void setLineColour(Object identifier, Color lineColour)
	{
		lineColours.put(identifier, lineColour);
	}

	void setxAutoScaleEnabled(boolean enabled)
	{
		xAutoScale = enabled;
		autoSetAxes();
	}

	void setyAutoScaleEnabled(boolean enabled)
	{
		yAutoScale = enabled;
		autoSetAxes();
	}

	void setAxisAutoScaleEnabled(boolean enabled)
	{
		xAutoScale = enabled;
		yAutoScale = enabled;
		autoSetAxes();
	}

	void setData(Object identifier, double[] x, double[] y)
	{
		if(x.length != y.length)
		{
			throw new IllegalArgumentException("X and Y array lengths are not equal.");
		}
		xValues.put(identifier, x);
		yValues.put(identifier, y);

		if(!lineColours.containsKey(identifier))
		{
			lineColours.put(identifier, Color.BLACK);
		}
		autoSetAxes();
		SwingUtilities.invokeLater(this::repaint);
	}

	private void autoSetAxes()
	{
		if(xAutoScale)
		{
			xAxisMin = Double.MAX_VALUE;
			xAxisMax = Double.MIN_VALUE;
			for(Object key : xValues.keySet())
			{
				xAxisMin = Math.min(xAxisMin, min(xValues.get(key)));
				xAxisMax = Math.max(xAxisMax, max(xValues.get(key)));
			}
		}
		if(yAutoScale)
		{
			yAxisMin = Double.MAX_VALUE;
			yAxisMax = Double.MIN_VALUE;
			for(Object key : yValues.keySet())
			{
				yAxisMin = Math.min(yAxisMin, min(yValues.get(key)));
				yAxisMax = Math.max(yAxisMax, max(yValues.get(key)));
			}
		}
		if(xAutoScale || yAutoScale)
		{
			resizeInputListener.onResizeRequested(new Rectangle(getWidth(), getHeight()));
		}
	}

	float getLineThickness()
	{
		return lineThickness;
	}

	void setLineThickness(float lineThickness)
	{
		this.lineThickness = lineThickness;
	}

	private static double min(double[] values)
	{
		double value = Double.MAX_VALUE;
		for(double element : values)
		{
			if(element < value)
				value = element;
		}
		return value;
	}

	private static double max(double[] values)
	{
		double value = Double.MIN_VALUE;
		for(double element : values)
		{
			if(element > value)
				value = element;
		}
		return value;
	}

	boolean isLegendEnabled()
	{
		return legendEnabled;
	}

	void setLegendEnabled(boolean legendEnabled)
	{
		this.legendEnabled = legendEnabled;
	}

	void setGridEnabled(boolean enabled)
	{
		this.gridEnabled = enabled;
	}

	void setXGridCount(int count)
	{
		xGridCount = count;
	}

	void setYGridCount(int count)
	{
		yGridCount = count;
	}

	void setResizeEnabled(boolean enabled)
	{
		this.resizeEnabled = enabled;
	}

	void setResizeInputListener(ResizeInputListener l)
	{
		this.resizeInputListener = l;
	}
}
