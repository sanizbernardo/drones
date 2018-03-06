package pilot.fly.pid;

import pilot.fly.FlyPilot;

import com.stormbots.MiniPID;

public class YawPID {
	
	MiniPID yawPID;
	
	FlyPilot pilot;
	
	public YawPID(FlyPilot pilot) {
		yawPID = new MiniPID(0.2, 0, 0);
		yawPID.setOutputLimits(Math.toRadians(30));
	}
	
	// // Uses PID controller to stabilise yaw
		// private void adjustHeading(AutopilotInputs input, float target) {
		// float actual = input.getHeading();
		// Vector3f rel = getRelVel(input);
		// float turn = (float) Math.atan2(rel.x(), -rel.z());
		//
		// if (Math.abs(actual - target) < FloatMath.toRadians(1) ) {
		// float stable = turn - actual;
		// setVerStabInclination(stable);
		// // adjustRoll(input, 0f);
		// return;
		// }
		//
		// yawPID.setSetpoint(target);
		// float min = turn - actual + config.getMaxAOA();
		// float max = turn - actual - config.getMaxAOA();
		// yawPID.setOutputLimits(min, max);
		//
		// float output = (float)yawPID.getOutput(actual);
		//
		// setVerStabInclination(-output);
		// }

}
