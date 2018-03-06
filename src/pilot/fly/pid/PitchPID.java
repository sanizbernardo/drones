package pilot.fly.pid;

import interfaces.AutopilotInputs;

import org.joml.Vector3f;

import pilot.fly.FlyPilot;

import com.stormbots.MiniPID;

public class PitchPID {
	
	MiniPID pitchUpPID, pitchDownPID;
	FlyPilot pilot;
	
	public PitchPID(FlyPilot pilot) {
		pitchUpPID = new MiniPID(1.5, 0, 0);
		pitchUpPID.setOutputLimits(Math.toRadians(20));
		pitchDownPID = new MiniPID(1, 0, 0.07);
		pitchDownPID.setOutputLimits(Math.toRadians(20));
		
		this.pilot = pilot;
	}
	
	// PID uses horizontal stabiliser to adjust pitch.
	public void adjustPitchUp(AutopilotInputs input, float target) {
		pitchUpPID.setSetpoint(target);

		Vector3f rel = pilot.getRelVel(input);
		float climb = (float) Math.atan2(rel.y(), -rel.z());
		float min = climb - input.getPitch() + pilot.getConfig().getMaxAOA();
		float max = climb - input.getPitch() - pilot.getConfig().getMaxAOA();
		pitchUpPID.setOutputLimits(min, max);

		float actual = input.getPitch();
		float output = (float) pitchUpPID.getOutput(actual);

		pilot.setHorStabInclination(-output);
	}

	public void adjustPitchDown(AutopilotInputs input, float target) {
		pitchDownPID.setSetpoint(target);

		Vector3f rel = pilot.getRelVel(input);
		float climb = (float) Math.atan2(rel.y(), -rel.z());
		float min = climb - input.getPitch() + pilot.getConfig().getMaxAOA();
		float max = climb - input.getPitch() - pilot.getConfig().getMaxAOA();
		pitchDownPID.setOutputLimits(min, max);

		float actual = input.getPitch();
		float output = (float) pitchDownPID.getOutput(actual);

		pilot.setHorStabInclination(-output);
	}

}
