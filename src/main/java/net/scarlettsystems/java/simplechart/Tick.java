package net.scarlettsystems.java.simplechart;

import javax.swing.*;
import java.awt.*;

class Tick extends JComponent
{
	enum Orientation
	{
		VERTICAL, HORIZONTAL
	}

	Tick(Orientation orientation, int thickness)
	{
		if (orientation == Orientation.VERTICAL)
		{
			this.setMinimumSize(new Dimension(thickness, 0));
			this.setPreferredSize(new Dimension(thickness, -1));
			this.setMaximumSize(new Dimension(thickness, Integer.MAX_VALUE));
		}
		else
		{
			this.setMinimumSize(new Dimension(0, thickness));
			this.setPreferredSize(new Dimension(-1, thickness));
			this.setMaximumSize(new Dimension(Integer.MAX_VALUE, thickness));
		}
		this.setBackground(UIManager.getColor("Label.foreground"));
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
