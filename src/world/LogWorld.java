package world;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;
import org.joml.Vector3f;

import entities.WorldObject;
import entities.meshes.cube.Cube;
import gui.GuiUtils;
import interfaces.AutopilotFactory;
import physics.Drone;

public class LogWorld extends World {

	public LogWorld() {
		super(1, true, true);
		planner = AutopilotFactory.createAutopilot();
		config = createConfig();
	}
	
	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public void setup() {
		Cube cube = new Cube(240,0.5f);
		worldObjects = new WorldObject[1];
		worldObjects[0] = new WorldObject(cube.getMesh());
		drone = new Drone(config);
		drone.setVelocity(new Vector3f(0,0,-10));
	}
	
	
	
	
	
	
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
			
			while ((ourLine = ourReader.readLine()) != null && (provLine = provReader.readLine()) != null) {
				String[] floats = ourLine.split(" ");
				float time = Float.valueOf(floats[0].substring(0, floats[0].length()-2)),
						x = Float.valueOf(floats[1]),
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
			ApplicationFrame frame = new ApplicationFrame("position");
			JPanel content = new JPanel();
			content.setLayout(new GridBagLayout());
			frame.setContentPane(content);
			content.add(panelX,GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER));
			content.add(panelY,GuiUtils.buildGBC(1, 0, GridBagConstraints.CENTER));
			content.add(panelZ,GuiUtils.buildGBC(2, 0, GridBagConstraints.CENTER));
			content.add(panelHead,GuiUtils.buildGBC(0, 1, GridBagConstraints.CENTER));
			content.add(panelPit,GuiUtils.buildGBC(1, 1, GridBagConstraints.CENTER));
			content.add(panelRoll,GuiUtils.buildGBC(2, 1, GridBagConstraints.CENTER));
			frame.pack();
			frame.setVisible(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
}
