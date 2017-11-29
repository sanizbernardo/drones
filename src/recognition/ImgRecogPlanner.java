package recognition;

import java.awt.Dimension;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.sql.Savepoint;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;

import autopilot.Autopilot;
import datatypes.AutopilotConfig;
import datatypes.AutopilotInputs;
import datatypes.AutopilotOutputs;
import utils.Utils;

public class ImgRecogPlanner implements Autopilot {
	
	public ImgRecogPlanner(double x, double y, double z, double dx, double dy, double dz) {
		this.x = (float)x;
		this.y = (float)y;
		this.z = (float)z;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
	}

	@Override
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		return Utils.buildOutputs(0, 0, 0, 0, 0);
	}

	private DefaultCategoryDataset distances = new DefaultCategoryDataset();
	private int i = 0;
	private boolean graphmade = false;

	private float x, y, z;
	private double dx, dy, dz;
	
	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {		
		// doe berekeningen voor image recog hier
		byte[] image = inputs.getImage();
		ImageProcessing imageProcess = new ImageProcessing(image);
		if(!imageProcess.getObjects().isEmpty()){
			Cube cube = imageProcess.getObjects().get(0);
			//double[] newDistances = {realDistance,imageProcess.guessDistance(cube)};
			double actualDistance = Math.sqrt(x*x+y*y+z*z);
			if(actualDistance < 60){
				double guess = imageProcess.guessDistance(cube);
				if (guess > 60){
					ArrayList<int[]> hull = imageProcess.getConvexHull(cube);
					for( int[] pixel : hull){
						System.out.println("x: " + pixel[0] + "         y: " + pixel[1]);
					}
					//System.exit(0);
					//imageProcess.saveImage("testing");
				}
				distances.addValue(actualDistance, "actual", "" + i);
				distances.addValue(guess, "target", "" + i);
				double difference = Math.abs(actualDistance-guess);
				distances.addValue(difference, "diff", "" + i);
			}
			//distances.add(newDistances);
			x += dx;
			y += dy;
			z += dz;
			i +=1;
		}
		else{
			x += dx;
			y += dy;
			z += dz;
			i +=1;
		}
		return Utils.buildOutputs(0, 0, 0, 0, 0);
	}

	@Override
	public void simulationEnded() {
		JFreeChart chart = ChartFactory.createLineChart("distance guess", null, null, distances, PlotOrientation.VERTICAL, true, false, false);
		ApplicationFrame frame = new ApplicationFrame("test");
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(900, 600));
		frame.setContentPane(chartPanel);
		frame.pack();
		frame.setVisible(true);
		graphmade = true;
		
	}

}
