package pilot.fly.pid;

import pilot.fly.FlyPilot;
import pilot.fly.FlyPilot.State;
import utils.FloatMath;

import com.stormbots.MiniPID;

import interfaces.AutopilotInputs;

public class RollPID {

	MiniPID rollPID;

	FlyPilot pilot;

	public RollPID(FlyPilot pilot) {
		rollPID = new MiniPID(0.2, 0, 0);
		rollPID.setOutputLimits(Math.toRadians(10));

		this.pilot = pilot;
	}

	public void adjustRoll(AutopilotInputs inputs, float target, State state) {
		rollPID.setSetpoint(target);
		float actual = inputs.getRoll();
		float output = (float) rollPID.getOutput(actual);
		if (state == State.StrongUp) {
			output = 0.5f*output;
		}
			pilot.setLeftWingInclination(FloatMath.toRadians(7) - output);
			pilot.setRightWingInclination(FloatMath.toRadians(7) + output);
	}

}
