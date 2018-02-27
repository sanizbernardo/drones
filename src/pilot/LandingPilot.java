package pilot;

import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

public class LandingPilot extends PilotPart {

	private boolean ended;
	
	@Override
	public void initialize(AutopilotConfig config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean ended() {
		return ended;
	}

	@Override
	public void close() {	
	}

	@Override
	public String taskName() {
		return "Landing pilot";
	}

}
