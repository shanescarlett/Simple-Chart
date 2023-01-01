package net.scarlettsystems.java.simplechart;

import javax.swing.*;
import java.awt.*;

public class Spacer extends JComponent
{
	@Override
	public Dimension getPreferredSize()
	{
		FontMetrics fm = this.getFontMetrics(this.getFont());
		return new Dimension(0, (int)Math.round(fm.getHeight() * 1.5));
	}
}
