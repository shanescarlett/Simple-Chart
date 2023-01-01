package net.scarlettsystems.java.simplechart;

import javax.swing.*;
import java.awt.*;

class XAxis extends JComponent
{
	private int tickCount = 10;
	private int tickThickness = 1;
	private final boolean isBottom;
	private TickLabelGenerator labelGenerator;

	interface TickLabelGenerator
	{
		String getLabel(int tickIndex);
	}

	XAxis(boolean bottom, TickLabelGenerator labelGenerator)
	{
		this.isBottom = bottom;
		this.labelGenerator = labelGenerator;
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(Integer.MAX_VALUE, (int)Math.round(this.getFontMetrics(this.getFont()).getHeight() * 1.5));
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		//Sanity checks
		if(getWidth() == 0 || getHeight() == 0)
			return;

		//Calculate dimensions
		g.clearRect(0, 0, getWidth(), getHeight());
		FontMetrics fm = this.getFontMetrics(this.getFont());
		int fontHeight = fm.getHeight();
		int tickHeight = fontHeight / 2;
		int tickTop, labelBottom;
		int labelWidth = Math.round((float)getWidth() / tickCount * 0.66666f);
		g.setColor(getForeground());
		g.setFont(this.getFont());

		//Draw
		if(isBottom)
		{
			tickTop = 0;
			labelBottom = getHeight();
			g.drawLine(0, 0, getWidth(), 0);
		}
		else
		{
			tickTop = fontHeight;
			labelBottom = tickHeight;
			g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
		}

		for(int i = 0; i < tickCount; i++)
		{
			int tickLocation = Math.round((float)i * ((float)getWidth() / (tickCount - 1)));
			int tickStartX = Math.min(getWidth() - tickThickness, Math.max(0, Math.round((float)tickLocation - ((float)tickThickness / 2f))));
			int labelStartX;
			String label = labelGenerator.getLabel(i);
			if(i == 0)
				labelStartX = 0;
			else if(i == tickCount - 1)
				labelStartX = getWidth() - labelWidth;
			else
				labelStartX = tickLocation - fm.stringWidth(label) / 2;

			g.fillRect(tickStartX, tickTop, tickThickness, tickHeight);
			g.drawString(label, labelStartX, labelBottom);
		}
	}

	void setTickCount(int count)
	{
		tickCount = count;
		this.repaint();
	}

	int getTickCount()
	{
		return this.tickCount;
	}

	void setTickThickness(int thickness)
	{
		tickThickness = thickness;
		this.repaint();
	}

	void setLabelGenerator(TickLabelGenerator generator)
	{
		this.labelGenerator = generator;
	}
}
