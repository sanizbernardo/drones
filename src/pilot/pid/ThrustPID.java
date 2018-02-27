package pilot.pid;

import pilot.FlyPilot;
import interfaces.AutopilotInputs;

import com.stormbots.MiniPID;

public class ThrustPID {

	MiniPID thrustUpPID, thrustDownPID;
	FlyPilot pilot;

	public ThrustPID(FlyPilot pilot) {
		thrustUpPID = new MiniPID(1, 0, 0);
		thrustDownPID = new MiniPID(1, 0.05, 0.05);
		
		this.pilot = pilot;
	}

	// PID sets thrust so that y component of velocity is equal to target.
	public void adjustThrustUp(AutopilotInputs inputs, float target) {
		thrustUpPID.setSetpoint(target);
		float actual = pilot.approxVel.y();
		float output = (float) thrustUpPID.getOutput(actual);

		// Check that received output is within bounds
		if (output > pilot.getConfig().getMaxThrust()) {
			pilot.setNewThrust(pilot.getConfig().getMaxThrust());
		} else if (output < 0f) {
			pilot.setNewThrust(0);
		} else {
			pilot.setNewThrust(output * 400);
		}
	}

	public void adjustThrustDown(AutopilotInputs inputs, float target) {
		thrustDownPID.setSetpoint(target);
		float actual = pilot.approxVel.y();
		float output = (float) thrustDownPID.getOutput(actual);

		// Check that received output is within bounds
		if (output > pilot.getConfig().getMaxThrust()) {
			pilot.setNewThrust(pilot.getConfig().getMaxThrust());
		} else if (output < 0f) {
			pilot.setNewThrust(0);
		} else {
			pilot.setNewThrust(output);
		}
	}

}
