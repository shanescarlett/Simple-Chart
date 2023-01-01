package net.scarlettsystems.java.simplechart;

import javax.swing.*;
import java.awt.*;

public class YAxis extends JComponent
{
	private int tickCount = 10;
	private int tickThickness = 1;
	private final boolean isLeft;
	private XAxis.TickLabelGenerator labelGenerator;

	interface TickLabelGenerator
	{
		String getLabel(int tickIndex);
	}

	YAxis(boolean left, XAxis.TickLabelGenerator labelGenerator)
	{
		this.isLeft = left;
		this.labelGenerator = labelGenerator;
	}

	@Override
	public Dimension getPreferredSize()
	{
		FontMetrics fm = this.getFontMetrics(this.getFont());
		int preferredWidth = 0;
		for(int i = 0; i < tickCount; i++)
		{
			int w = fm.stringWidth(labelGenerator.getLabel(i));
			if(w > preferredWidth)
				preferredWidth = w;
		}
		return new Dimension((int)Math.round(preferredWidth + fm.getHeight() * 0.5),Integer.MAX_VALUE);
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
		int tickWidth = fontHeight / 2;
		int tickLeft, labelLeft;
		int labelWidth = Math.round((float)getWidth() / tickCount * 0.66666f);
		g.setColor(getForeground());
		g.setFont(this.getFont());

		//Draw
		if(isLeft)
		{
			tickLeft = getWidth() - tickWidth;
			labelLeft = 0;
			g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
		}
		else
		{
			tickLeft = 0;
			labelLeft = tickWidth;
			g.drawLine(0, 0, 0, getHeight());
		}

		for(int i = 0; i < tickCount; i++)
		{
			int tickLocation = Math.round((float)i * ((float)getHeight() / (tickCount - 1)));
			int tickStartY = Math.min(getHeight() - tickThickness, Math.max(0, Math.round((float)tickLocation - ((float)tickThickness / 2f))));
			int labelStartY;
			String label = labelGenerator.getLabel(tickCount - i - 1);
			if(i == 0)
				labelStartY = fm.getHeight();
			else if(i == tickCount - 1)
				labelStartY = getHeight() - fm.getHeight() / 2;
			else
				labelStartY = tickLocation + fm.getHeight() / 4;

			g.fillRect(tickLeft, tickStartY, tickWidth, tickThickness);
			g.drawString(label, labelLeft, labelStartY);
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

	void setLabelGenerator(XAxis.TickLabelGenerator generator)
	{
		this.labelGenerator = generator;
	}
}