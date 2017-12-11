package recognition;

import interfaces.Autopilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

import java.awt.Dimension;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;


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
	private float x, y, z;
	private double dx, dy, dz;
	
	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {		
		// doe berekeningen voor image recog hier
		byte[] image = inputs.getImage();
		ImageProcessing imageProcess = null;
		if (x > 0){
			float[] pos = {0,0,0};
			imageProcess = new ImageProcessing(image, (float) -Math.PI/4,(float) -Math.PI/4, 0f, pos);
		}
		//System.out.println(z);
		//System.out.println(imageProcess == null);
		if(imageProcess != null && !imageProcess.getObjects().isEmpty()){
			Cube cube = imageProcess.getObjects().get(0);
			//TODO: onderstaande statement zorgt ervoor dat afstandsscgatting verkeerd is -> waarom?
			//imageProcess.generateLocations();
			//System.out.println(cube.getLocation()[0] + "  " + cube.getLocation()[1] + "  " + cube.getLocation()[2] + "  " + x + "  " + y +"  "+z);
			//System.out.println(imageProcess.getObjects().size());
			//System.out.println(imageProcess.isOnBorder(cube) || imageProcess.touches(cube));
			//if(imageProcess.getObjects().size()>1){
				//Cube cube2 = imageProcess.getObjects().get(1);
				//System.out.println(imageProcess.isOnBorder(cube2) || imageProcess.touches(cube2));
			//}
			//double[] newDistances = {realDistance,imageProcess.guessDistance(cube)};
			double actualDistance = Math.sqrt(x*x+y*y+z*z);
			if(actualDistance < 80){
				double guess = imageProcess.guessDistance(cube);
				//if (guess > 60){
				//	ArrayList<int[]> hull = imageProcess.getConvexHull(cube);
				//	//for( int[] pixel : hull){
//				//		System.out.println("x: " + pixel[0] + "         y: " + pixel[1]);
					//}
					//System.exit(0);
					//imageProcess.saveImage("testing");
				//}
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
		JFreeChart chart = ChartFactory.createLineChart("Distance approximation", null, null, distances, PlotOrientation.VERTICAL, true, false, false);
		ApplicationFrame frame = new ApplicationFrame("test");
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(900, 600));
		frame.setContentPane(chartPanel);
		frame.pack();
		frame.setVisible(true);
		
	}

}
