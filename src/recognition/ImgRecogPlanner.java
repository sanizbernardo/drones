package recognition;

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

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {
		
		
		// doe berekeningen voor image recog hier
		
		
		return Utils.buildOutputs(0, 0, 0, 0, 0);
	}

	@Override
	public void simulationEnded() {}

}
