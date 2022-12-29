import net.scarlettsystems.java.simplechart.SimpleChart;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame
{
	public Frame()
	{
		this.setContentPane(getContent());
		this.setMinimumSize(new Dimension(1920, 1080));
		this.setPreferredSize(new Dimension(1920, 1080));
		this.add(getContent());
		this.pack();
		this.setVisible(true);
	}

	private JPanel getContent()
	{
		SimpleChart chart = new SimpleChart();
		double[] x = new double[1024];
		double[] y = new double[1024];
		for(int i = 0; i < 1024; i++)
		{
			x[i] = 2.0 * Math.PI / 1024 * i;
			y[i] = Math.sin(x[i]);
		}
		chart.setData("1", x, y);
		chart.setLineColour("1", Color.decode("#00a0ff"));
		chart.setLineThickness(2);
		chart.setXAxisMin(0);
		chart.setXAxisMax(2.0 * Math.PI);
		chart.setYAxisMin(-1);
		chart.setYAxisMax(1);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(chart);
		return panel;
	}
}
