package recognition;

import java.util.ArrayList;

import autopilot.Autopilot;
import datatypes.AutopilotConfig;
import datatypes.AutopilotInputs;
import datatypes.AutopilotOutputs;
import utils.Utils;

public class ImgRecogPlanner implements Autopilot {
	
	public ImgRecogPlanner() {}

	@Override
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		return Utils.buildOutputs(0, 0, 0, 0, 0);
	}

	private double distances;
	
	
	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {
		
		
		// doe berekeningen voor image recog hier
		byte[] image = inputs.getImage();
		System.out.println(image.length);
		ImageProcessing imageProcess = new ImageProcessing(image);
		imageProcess.saveImage("test");
		//Dit geeft ook null, het ligt aan getImage die zwart is.
		//ImageRecognition recog = new ImageRecognition(image, 200, 200, 120, 120);
		//System.out.println(recog.getCenter());
		if(!imageProcess.getObjects().isEmpty()){
			System.out.println("normaal moet dit gespamt worden");
		}
		Cube cube = imageProcess.getObjects().get(0);
		distances = imageProcess.guessDistance(cube);
		//distances.add(imageProcess.guessDistance(cube));
		System.out.println(distances);
		return Utils.buildOutputs(0, 0, 0, 0, 0);
	}

	@Override
	public void simulationEnded() {}

}
