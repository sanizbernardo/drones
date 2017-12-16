import java.awt.Dimension;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;

import com.stormbots.MiniPID;

public class PIDPlotTest {

	public static void main(String[] args) {

		MiniPID pid = new MiniPID(1.2, 0.15, 0.1);
		double target = 100, actual = 0, output = 0;
		pid.setSetpoint(target);

		DefaultCategoryDataset set = new DefaultCategoryDataset();
		for (int i = 0; i < 100; i += 1) {
			actual += output;
			output = pid.getOutput(actual);

			if (i == 30) {
				pid.setSetpoint(50);
				target = 50;
			}
			if (i == 75) {
				pid.setSetpoint(130);
				target = 130;
			}

			set.addValue(actual, "actual", "" + i);
			set.addValue(target, "target", "" + i);
		}

		JFreeChart chart = ChartFactory.createLineChart("pid", null, null, set, PlotOrientation.VERTICAL, true, false, false);
		ApplicationFrame frame = new ApplicationFrame("test");
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(900, 600));
		frame.setContentPane(chartPanel);
		frame.pack();
		frame.setVisible(true);
	}

}
