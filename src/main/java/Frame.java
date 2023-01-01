import net.scarlettsystems.java.simplechart.SimpleChart;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame
{
	private final SimpleChart chart;

	public Frame()
	{
		chart = new SimpleChart();
		this.setContentPane(getContent());
		this.setMinimumSize(new Dimension(1920, 1080));
		this.setPreferredSize(new Dimension(1920, 1080));
		this.add(getContent());
		this.pack();
		this.setVisible(true);
	}

	private JPanel getContent()
	{

		double[] x = new double[1024];
		double[] y = new double[1024];
		for(int i = 0; i < 1024; i++)
		{
			x[i] = 6.0 * Math.PI / 1024 * i;
			y[i] = Math.sin(x[i]);
		}
		chart.setData("1", x, y);
		chart.setLineColour("1", Color.decode("#00a0ff"));
		chart.setLineThickness(2);
		chart.setXAxisMin(0);
		chart.setXAxisMax(2.0 * Math.PI);
		chart.setYAxisMin(-1);
		chart.setYAxisMax(1);
		chart.setYTickCount(5);
		chart.setGridEnabled(true);
		chart.setTickLabelGenerator(new SimpleChart.LabelGenerator()
		{
			@Override
			public String getTopXTickLabel(double value, int index)
			{
				return String.format("%.3f", value);
			}

			@Override
			public String getBottomXTickLabel(double value, int index)
			{
				return String.format("%.3f", value);
			}

			@Override
			public String getLeftYTickLabel(double value, int index)
			{
				return String.format("%.3f", value);
			}

			@Override
			public String getRightYTickLabel(double value, int index)
			{
				return String.format("%.3f", value);
			}
		});

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

		JButton autoButton = new JButton("Auto Scale");
		autoButton.addActionListener(a -> setAutoScale());
		buttons.add(autoButton);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(chart);
		panel.add(buttons, BorderLayout.NORTH);
		return panel;
	}

	private void setAutoScale()
	{
		chart.setAxesAutoEnabled(true);
	}
}
