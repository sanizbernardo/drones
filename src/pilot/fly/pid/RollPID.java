package pilot.fly.pid;

import pilot.fly.FlyPilot;

import com.stormbots.MiniPID;

public class RollPID {
	
	MiniPID rollPID;
	
	FlyPilot pilot;
	
	public RollPID(FlyPilot pilot) {
		rollPID = new MiniPID(1, 0.000005, 0);
		rollPID.setOutputLimits(Math.toRadians(30));
		
		this.pilot = pilot;
	}
	

	// private void adjustRoll(AutopilotInputs inputs, float target) {
	// rollPID.setSetpoint(target);
	// float actual = inputs.getRoll();
	// float output = (float)rollPID.getOutput(actual);
	// setLeftWingInclination(leftWingInclination - output);
	// setRightWingInclination(rightWingInclination + output);
	// }

}
