import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;

import gui.GuiUtils;
import utils.FloatMath;

public class PlotSimulation {
	
	public static void main(String[] args) throws Exception {
		File file = new File("position.log");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String line = reader.readLine();
		
		DefaultCategoryDataset dataX = new DefaultCategoryDataset(),
				dataY = new DefaultCategoryDataset(),
				dataZ = new DefaultCategoryDataset(),
				dataHead = new DefaultCategoryDataset(),
				dataPit = new DefaultCategoryDataset(),
				dataRoll = new DefaultCategoryDataset();
	
		while ((line = reader.readLine()) != null) {
			String[] floats = line.split(" ");
			float time = Float.valueOf(floats[0].substring(0, floats[0].length()-2)),
				  x = Float.valueOf(floats[1]),
				  y = Float.valueOf(floats[2]),
				  z = Float.valueOf(floats[3]),
				  head = FloatMath.toDegrees(Float.valueOf(floats[4])),
				  pit = FloatMath.toDegrees(Float.valueOf(floats[5])),
				  roll = FloatMath.toDegrees(Float.valueOf(floats[6]));
			dataX.addValue(x, "ourX", time + "");
			dataY.addValue(y, "ourY", time + "");
			dataZ.addValue(z, "ourZ", time + "");
			dataHead.addValue(head, "ourHead", time + "");
			dataPit.addValue(pit, "ourPit", time + "");
			dataRoll.addValue(roll, "ourRoll", time + "");
		}
		
		reader.close();
		
		JFreeChart chartX = ChartFactory.createLineChart("X", null, null, dataX, PlotOrientation.VERTICAL, true, false, false),
				chartY = ChartFactory.createLineChart("Y", null, null, dataY, PlotOrientation.VERTICAL, true, false, false),
				chartZ = ChartFactory.createLineChart("Z", null, null, dataZ, PlotOrientation.VERTICAL, true, false, false),
				chartHead = ChartFactory.createLineChart("Heading", null, null, dataHead, PlotOrientation.VERTICAL, true, false, false),
				chartPit = ChartFactory.createLineChart("Pitch", null, null, dataPit, PlotOrientation.VERTICAL, true, false, false),
				chartRoll = ChartFactory.createLineChart("Roll", null, null, dataRoll, PlotOrientation.VERTICAL, true, false, false);
		
		ChartPanel panelX = new ChartPanel(chartX),
				panelY = new ChartPanel(chartY),
				panelZ = new ChartPanel(chartZ),
				panelHead = new ChartPanel(chartHead),
				panelPit = new ChartPanel(chartPit),
				panelRoll = new ChartPanel(chartRoll);
		
		panelX.setPreferredSize(new Dimension(600, 500));
		panelY.setPreferredSize(new Dimension(600, 500));
		panelZ.setPreferredSize(new Dimension(600, 500));
		panelHead.setPreferredSize(new Dimension(600, 500));
		panelPit.setPreferredSize(new Dimension(600, 500));
		panelRoll.setPreferredSize(new Dimension(600, 500));
		
		ApplicationFrame frame = new ApplicationFrame("Simulation analysis");
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		JScrollPane content = new JScrollPane(panel);
		frame.setContentPane(content);
		panel.setPreferredSize(new Dimension(1900, 1100));
		
		panel.add(panelX,GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER));
		panel.add(panelY,GuiUtils.buildGBC(1, 0, GridBagConstraints.CENTER));
		panel.add(panelZ,GuiUtils.buildGBC(2, 0, GridBagConstraints.CENTER));
		panel.add(panelHead,GuiUtils.buildGBC(0, 1, GridBagConstraints.CENTER));
		panel.add(panelPit,GuiUtils.buildGBC(1, 1, GridBagConstraints.CENTER));
		panel.add(panelRoll,GuiUtils.buildGBC(2, 1, GridBagConstraints.CENTER));
		
		frame.pack();
		frame.setVisible(true);
	}
	
	
	
	
	
}
