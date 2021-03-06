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

import utils.GuiUtils;

public class PlotPhysicsTests {
	
	public static void main(String[] args) {
		try {
			File ourFile = new File("position.log"),
					provFile = new File("provided_testbed/position.log");
			BufferedReader ourReader = new BufferedReader(new FileReader(ourFile)),
					provReader = new BufferedReader(new FileReader(provFile));
			String ourLine = ourReader.readLine(),
					provLine = provReader.readLine();
			
			DefaultCategoryDataset dataX = new DefaultCategoryDataset(),
					dataY = new DefaultCategoryDataset(),
					dataZ = new DefaultCategoryDataset(),
					dataHead = new DefaultCategoryDataset(),
					dataPit = new DefaultCategoryDataset(),
					dataRoll = new DefaultCategoryDataset();
			
			float time = 0;
			
			while ((ourLine = ourReader.readLine()) != null && (provLine = provReader.readLine()) != null) {
				String[] floats = ourLine.split(" ");
				time = Float.valueOf(floats[0].substring(0, floats[0].length()-2));
				float	x = Float.valueOf(floats[1]),
						y = Float.valueOf(floats[2]),
						z = Float.valueOf(floats[3]),
						head = Float.valueOf(floats[4]),
						pit = Float.valueOf(floats[5]),
						roll = Float.valueOf(floats[6]);
				dataX.addValue(x, "ourX", time + "");
				dataY.addValue(y, "ourY", time + "");
				dataZ.addValue(z, "ourZ", time + "");
				dataHead.addValue(head, "ourHead", time + "");
				dataPit.addValue(pit, "ourPit", time + "");
				dataRoll.addValue(roll, "ourRoll", time + "");
				
				floats = provLine.split(" ");
				time = Float.valueOf(floats[0].substring(0, floats[0].length()-2));
				x = Float.valueOf(floats[1]);
				y = Float.valueOf(floats[2]);
				z = Float.valueOf(floats[3]);
				head = Float.valueOf(floats[4]);
				pit = Float.valueOf(floats[5]);
				roll = Float.valueOf(floats[6]);
				dataX.addValue(x, "provX", time + "");
				dataY.addValue(y, "provY", time + "");
				dataZ.addValue(z, "provZ", time + "");
				dataHead.addValue(head, "provHead", time + "");
				dataPit.addValue(pit, "provPit", time + "");
				dataRoll.addValue(roll, "provRoll", time + "");
			}
			
			System.out.println("logging until " + time + "s");
			ourReader.close();
			provReader.close();
		
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
			
			ApplicationFrame frame = new ApplicationFrame("Physics comparing");
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
