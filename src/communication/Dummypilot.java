package communication;
import interfaces.Autopilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

public class Dummypilot implements Autopilot {

	@Override
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		return new AutopilotOutputs() {			
			@Override
			public float getVerStabInclination() {
				return -1;
			}
			@Override
			public float getThrust() {
				return -1;
			}
			@Override
			public float getRightWingInclination() {
				return -1;
			}
			@Override
			public float getLeftWingInclination() {
				return -1;
			}
			@Override
			public float getHorStabInclination() {
				return -1;
			}
		};
	}

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {
		return new AutopilotOutputs() {			
			@Override
			public float getVerStabInclination() {
				return -1*inputs.getElapsedTime();
			}
			@Override
			public float getThrust() {
				return -1*inputs.getElapsedTime();
			}
			@Override
			public float getRightWingInclination() {
				return -1*inputs.getElapsedTime();
			}
			@Override
			public float getLeftWingInclination() {
				return -1*inputs.getElapsedTime();
			}
			@Override
			public float getHorStabInclination() {
				return -1*inputs.getElapsedTime();
			}
		};
	}

	@Override
	public void simulationEnded() {
		System.out.println("exiting");
	}

}
