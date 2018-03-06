package pilot.fly.pid;

import interfaces.AutopilotInputs;

import org.joml.Vector3f;

import pilot.fly.FlyPilot;

import com.stormbots.MiniPID;

public class PitchPID {
	
	MiniPID pitchClimbPID, pitchDownPID;
	FlyPilot pilot;
	
	public PitchPID(FlyPilot pilot) {

		pitchClimbPID = new MiniPID(2.5, 0, 0.5);
		pitchClimbPID.setOutputLimits(Math.toRadians(10));
		pitchDownPID = new MiniPID(3, 0, 0.5);
		pitchDownPID.setOutputLimits(Math.toRadians(10));
		
		this.pilot = pilot;
	}
	
	// PID uses horizontal stabiliser to adjust pitch.
	public void adjustPitchClimb(AutopilotInputs input, float target) {
		pitchClimbPID.setSetpoint(target);

//		Vector3f rel = pilot.getRelVel(input);
//		float climb = (float) Math.atan2(rel.y(), -rel.z());
//		float min = climb - input.getPitch() + pilot.getConfig().getMaxAOA();
//		float max = climb - input.getPitch() - pilot.getConfig().getMaxAOA();
//		pitchUpPID.setOutputLimits(min, max);

		float actual = input.getPitch();
		float output = (float) pitchClimbPID.getOutput(actual);

		pilot.setHorStabInclination(-output);
	}

	public void adjustPitchDown(AutopilotInputs input, float target) {
		pitchDownPID.setSetpoint(target);

//		Vector3f rel = pilot.getRelVel(input);
//		float climb = (float) Math.atan2(rel.y(), -rel.z());
//		float min = climb - input.getPitch() + pilot.getConfig().getMaxAOA();
//		float max = climb - input.getPitch() - pilot.getConfig().getMaxAOA();
//		pitchDownPID.setOutputLimits(min, max);

		float actual = input.getPitch();
		float output = (float) pitchDownPID.getOutput(actual);

		pilot.setHorStabInclination(-output);
	}

}
