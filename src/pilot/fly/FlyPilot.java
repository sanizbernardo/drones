package pilot.fly;

import java.util.ArrayList;
import java.util.OptionalDouble;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import pilot.PilotPart;
import pilot.fly.pid.PitchPID;
import pilot.fly.pid.RollPID;
import pilot.fly.pid.ThrustPID;
import pilot.fly.pid.YawPID;
import recognition.Cube;
import recognition.ImageProcessing;
import utils.Constants;
import utils.FloatMath;
import utils.Utils;

import com.stormbots.MiniPID;

import gui.AutopilotGUI;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

public class FlyPilot extends PilotPart {

	private AutopilotConfig config;

	private boolean ended;

	private float leftWingInclination;
	private float rightWingInclination;
	private float horStabInclination;
	private float verStabInclination;
	private float newThrust;
	public Vector3f approxVel = new Vector3f(0f, 0f, 0f);
	private float climbAngle;
//	private ImageProcessing recog;

	private Vector3f timePassedOldPos;

	
	private PitchPID pitchPID;
	private ThrustPID thrustPID;
	private YawPID yawPID;
	private RollPID rollPID;
	private AOAManager aoaManager;

	private State[] order;
	private int currentState;

	private enum State{Left, Right, Stable, Up, Down, StrongUp, StrongDown}
	
	@Override
	public void initialize(AutopilotConfig config) {
		this.config = config;

		this.pitchPID = new PitchPID(this);
		this.thrustPID = new ThrustPID(this);
		this.yawPID = new YawPID(this);
		this.rollPID = new RollPID(this);
		
		this.aoaManager = new AOAManager(this);

		this.order = new State[] {State.StrongUp};
		setCurrentState(0);
		
		climbAngle = Constants.climbAngle;
	}
	

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {

		//TODO: reenable
//		recog = new ImageProcessing(inputs.getImage(), inputs.getPitch(),
//				inputs.getHeading(), inputs.getRoll(), new float[] {
//						inputs.getX(), inputs.getY(), inputs.getZ() });

		Vector3f newPos = new Vector3f(inputs.getX(), inputs.getY(), inputs.getZ());
		
		
		if (timePassedOldPos != null)
			approxVel = (newPos.sub(timePassedOldPos, new Vector3f()))
			                   .mul(1 / inputs.getElapsedTime(), new Vector3f());
		timePassedOldPos = new Vector3f(newPos);

		
		//float desiredHeight = recog.guess();



		adjustHeight(inputs, order[getCurrentState()]);

		AutopilotOutputs output = Utils.buildOutputs(leftWingInclination,
				rightWingInclination, verStabInclination, horStabInclination,
				getNewThrust(), 0, 0, 0);

		return output;
	}
	
	private void setCurrentState(int state) {
		this.currentState = state;
	}
	
	private int getCurrentState() {
		return currentState;
	}
	

	Vector3f horProjVel(AutopilotInputs inputs) {
		Vector3f relVelD = getTransMat(inputs).transform(approxVel,
				new Vector3f());
		return new Vector3f(0, relVelD.y, relVelD.z);
	}


	// PIDs set pitch and thrust to fly straight.
	private void flyStraightPID(AutopilotInputs input) {
		pitchPID.adjustPitchClimb(input, 0f);
		thrustPID.adjustThrustUp(input, 0.4f);
	}

	// causes drone to climb by changing pitch and using thrust to increase
	// vertical velocity
	private void climbPID(AutopilotInputs inputs) {
		pitchPID.adjustPitchClimb(inputs, climbAngle);
		thrustPID.adjustThrustUp(inputs, 4f);
	}

	private void dropPID(AutopilotInputs inputs) {
		pitchPID.adjustPitchDown(inputs, FloatMath.toRadians(-3f));
		thrustPID.adjustThrustDown(inputs, -2f);
	}

	// causes drone to rise by increasing lift through higher speed.
	private void risePID(AutopilotInputs inputs) {
		// pitch op 0
		pitchPID.adjustPitchClimb(inputs, FloatMath.toRadians(4));
		// thrust bijgeven
		thrustPID.adjustThrustUp(inputs, 2f);
	}

	private void descendPID(AutopilotInputs inputs) {
		// pitch op 0
		pitchPID.adjustPitchDown(inputs, 0);
		// val vertragen
		thrustPID.adjustThrustDown(inputs, -1.5f);
	}

	private void adjustHeight(AutopilotInputs input, State state) {

		switch(state){
			case StrongUp:
				climbPID(input);
				aoaManager.setInclNoAOA(input);
				break;
			case Up:
				risePID(input);
				aoaManager.setInclNoAOA(input);
				break;
			case StrongDown:
				dropPID(input);
				setLeftWingInclination(FloatMath.toRadians(2));
				setRightWingInclination(FloatMath.toRadians(2));
				break;
			case Down:
				descendPID(input);
				aoaManager.setInclNoAOA(input);
				break;
			case Stable:
				flyStraightPID(input);
				aoaManager.setInclNoAOA(input);
				break;
			case Left:
				break;
			case Right:
				break;
			default:
				break;
		}

	}
	
	private void turn(AutopilotInputs input) {
		rollPID.adjustRoll(input, FloatMath.toRadians(5));
		thrustPID.adjustThrustUp(input, 0.4f);
		pitchPID.adjustPitchClimb(input, FloatMath.toRadians(4));
	}


	private float getMass() {
		return config.getEngineMass() + config.getTailMass() + 2
				* config.getWingMass();
	}

	public Vector3f getRelVel(AutopilotInputs input) {
		return getTransMat(input).transform(approxVel, new Vector3f());
	}

	private Matrix3f getTransMat(AutopilotInputs inputs) {
		float heading = inputs.getHeading();
		float pitch = inputs.getPitch();
		float roll = inputs.getRoll();

		Matrix3f transMat = new Matrix3f().identity();

		if (Math.abs(heading) > 1E-6)
			transMat.rotate(heading, new Vector3f(0, 1, 0));
		if (Math.abs(pitch) > 1E-6)
			transMat.rotate(pitch, new Vector3f(1, 0, 0));
		if (Math.abs(roll) > 1E-6)
			transMat.rotate(roll, new Vector3f(0, 0, 1));

		return transMat;
	}
	
	public AutopilotConfig getConfig() {
		return config;
	}

	public float getNewThrust() {
		return newThrust;
	}
	
	public float getVerStabInclination() {
		return verStabInclination;
	}

	public float getLeftWingInclination() {
		return leftWingInclination;
	}

	public float getRightWingInclination() {
		return rightWingInclination;
	}

	public float getHorStabInclination() {
		return horStabInclination;
	}

	public void setLeftWingInclination(float leftWingInclination) {
		this.leftWingInclination = leftWingInclination;
	}

	public void setRightWingInclination(float rightWingInclination) {
		this.rightWingInclination = rightWingInclination;
	}

	public void setNewThrust(float newThrust) {
		this.newThrust = newThrust;
	}

	public void setHorStabInclination(float horStabInclination) {
		this.horStabInclination = horStabInclination;
	}

	@Override
	public boolean ended() {
		return ended;
	}

	@Override
	public void close() {
		// TODO: maybe add imageRecog .close()
	}

	@Override
	public String taskName() {
		return "Fly";
	}

}
