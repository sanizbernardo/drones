package recognition;

import java.util.ArrayList;

import autopilot.Autopilot;
import datatypes.AutopilotConfig;
import datatypes.AutopilotInputs;
import datatypes.AutopilotOutputs;
import utils.Utils;

public class ImgRecogPlanner implements Autopilot {

	@Override
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		return Utils.buildOutputs(0, 0, 0, 0, 0);
	}

	double distances;
	
	
	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {
		
		
		// doe berekeningen voor image recog hier
		byte[] image = inputs.getImage();
		ImageProcessing imageProcess = new ImageProcessing(image);
		imageProcess.saveImage("test");
		Cube cube = imageProcess.getObjects().get(0);
		distances = imageProcess.guessDistance(cube);
		//distances.add(imageProcess.guessDistance(cube));
		System.out.println(distances);
		return Utils.buildOutputs(0, 0, 0, 0, 0);
	}

	@Override
	public void simulationEnded() {}

}
