package pilot.fly;

import interfaces.AutopilotInputs;

import org.joml.Vector3f;

import utils.FloatMath;

public class AOAManager {

	FlyPilot pilot;
	
	public AOAManager(FlyPilot pilot) {
		this.pilot = pilot;
	}
	
	// AOA of right wing
	public float rightWingAOA(AutopilotInputs inputs) {
		Vector3f horProjVelD = pilot.horProjVel(inputs);
		Vector3f WingAttackVectorD = new Vector3f(0f,
				(float) Math.sin((double) pilot.getRightWingInclination()),
				(float) -Math.cos((double) pilot.getRightWingInclination()));
		Vector3f WingNormalVectorD = FloatMath.cross(new Vector3f(1, 0, 0),
				WingAttackVectorD);
		return (float) -Math.atan2(horProjVelD.dot(WingNormalVectorD),
				horProjVelD.dot(WingAttackVectorD));
	}

	// AOA of left wing
	public float leftWingAOA(AutopilotInputs inputs) {
		Vector3f horProjVelD = pilot.horProjVel(inputs);
		Vector3f WingNormalVectorD = new Vector3f(0f,
				(float) Math.cos((double) pilot.getLeftWingInclination()),
				(float) Math.sin((double) pilot.getLeftWingInclination()));
		Vector3f WingAttackVectorD = new Vector3f(0f,
				(float) Math.sin((double) pilot.getLeftWingInclination()),
				(float) -Math.cos((double) pilot.getLeftWingInclination()));
		return (float) -Math.atan2(horProjVelD.dot(WingNormalVectorD),
				horProjVelD.dot(WingAttackVectorD));
	}

	// AOA of horizontal stabiliser
	public float horStabAOA(AutopilotInputs inputs) {
		Vector3f horProjVelD = pilot.horProjVel(inputs);
		Vector3f WingNormalVectorD = new Vector3f(0f,
				(float) Math.cos((double) pilot.getHorStabInclination()),
				(float) Math.sin((double) pilot.getHorStabInclination()));
		Vector3f WingAttackVectorD = new Vector3f(0f,
				(float) Math.sin((double) pilot.getHorStabInclination()),
				(float) -Math.cos((double) pilot.getHorStabInclination()));
		return (float) -Math.atan2(horProjVelD.dot(WingNormalVectorD),
				horProjVelD.dot(WingAttackVectorD));
	}

	// AOA of vertical stabiliser
	public float verStabAOA(AutopilotInputs inputs) {
		Vector3f horProjVelD = pilot.horProjVel(inputs);
		Vector3f WingNormalVectorD = new Vector3f(0f,
				(float) Math.cos((double) pilot.getVerStabInclination()),
				(float) Math.sin((double) pilot.getVerStabInclination()));
		Vector3f WingAttackVectorD = new Vector3f(0f,
				(float) Math.sin((double) pilot.getVerStabInclination()),
				(float) -Math.cos((double) pilot.getVerStabInclination()));
		return (float) -Math.atan2(horProjVelD.dot(WingNormalVectorD),
				horProjVelD.dot(WingAttackVectorD));
	}

	void setInclNoAOA(AutopilotInputs inputs) {
		// float rAOA = rightWingAOA(inputs);
		// float lAOA = leftWingAOA(inputs);
		// if (approxVel.z() == 0) {
		// setRightWingInclination(FloatMath.toRadians(10));
		// setLeftWingInclination(FloatMath.toRadians(10));
		// }else {
		// if (rAOA > FloatMath.toRadians(10)) {
		// setRightWingInclination(FloatMath.toRadians(10-(FloatMath.toDegrees(rAOA)-10)));
		// }else if (rAOA < FloatMath.toRadians(-10)) {
		// setRightWingInclination(FloatMath.toRadians(10+(FloatMath.toDegrees(rAOA)+10)));
		// }else
		// setRightWingInclination(FloatMath.toRadians(10));
		// if (lAOA > FloatMath.toRadians(10)) {
		// setLeftWingInclination(FloatMath.toRadians(10-(FloatMath.toDegrees(lAOA)-10)));
		// }else if (lAOA < FloatMath.toRadians(-10)) {
		// setLeftWingInclination(FloatMath.toRadians(10+(FloatMath.toDegrees(lAOA)+10)));
		// }else
		// setLeftWingInclination(FloatMath.toRadians(10));
		// }
		pilot.setRightWingInclination(FloatMath.toRadians(7));
		pilot.setLeftWingInclination(FloatMath.toRadians(7));
	}
	
}
