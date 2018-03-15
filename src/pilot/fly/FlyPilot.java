package pilot.fly;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import pilot.PilotPart;
import pilot.fly.pid.PitchPID;
import pilot.fly.pid.RollPID;
import pilot.fly.pid.ThrustPID;
import pilot.fly.pid.YawPID;
import recognition.ImageProcessing;
import utils.Constants;
import utils.FloatMath;
import utils.Utils;

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
	private float time = 0;
	public Vector3f approxVel = new Vector3f(0f, 0f, 0f);
	private float climbAngle;
	private final ImageProcessing recog = new ImageProcessing();


	private Vector3f timePassedOldPos = new Vector3f(0, 0, 0);

	
	private PitchPID pitchPID;
	private ThrustPID thrustPID;
	private YawPID yawPID;
	private RollPID rollPID;
	private AOAManager aoaManager;
	private boolean ja = true;
	private float i = 1;
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

		this.order = new State[] {State.Stable, State.Left, State.Stable, State.StrongDown, State.Stable};
		setCurrentState(0);
		
		climbAngle = Constants.climbAngle;
	}
	

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {
		recog.addNewImage(inputs.getImage(), inputs.getPitch(),
				inputs.getHeading(), inputs.getRoll(), new float[] {
						inputs.getX(), inputs.getY(), inputs.getZ() });

		Vector3f pos = new Vector3f(inputs.getX(), inputs.getY(), inputs.getZ());
		
		float dt = inputs.getElapsedTime() - this.time;
		this.time = inputs.getElapsedTime();
		if (dt != 0)
			this.approxVel = pos.sub(this.timePassedOldPos, new Vector3f()).mul(1/dt);
		this.timePassedOldPos = pos;		
		
		if (inputs.getElapsedTime() > 25)
			this.setCurrentState(1);
		if (inputs.getElapsedTime() > 35)
			this.setCurrentState(2);
		if (inputs.getElapsedTime() > 45)
			this.setCurrentState(3);
		
		if (inputs.getY() < 15) {
			this.setCurrentState(4);
		}if (inputs.getElapsedTime() > 85)
			this.ended = true;
		
		
		
		if (inputs.getY() > 200 && ja == true) {
			System.out.println(inputs.getElapsedTime());
			ja = false;
		}
		
		//float desiredHeight = recog.guess();
//		if(3 - inputs.getY() > 4) {
//			
//		}
//		else if(3 - inputs.getY() > 1) {
//			
//		}
//		else if(3 - inputs.getY() < -4) {
//			setCurrentState(0);
//		}else if(3 - inputs.getY() < -1) {
//			setCurrentState(1);
//		}else {
//			setCurrentState(2);
//		}
		
		control(inputs, order[getCurrentState()]);

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
		rollPID.adjustRoll(input, FloatMath.toRadians(0));
		pitchPID.adjustPitchClimb(input, 0f);
		thrustPID.adjustThrustUp(input, 0.4f);
	}

	// causes drone to climb by changing pitch and using thrust to increase
	// vertical velocity
	private void climbPID(AutopilotInputs inputs) {
		pitchPID.adjustPitchClimb(inputs, climbAngle);
		thrustPID.adjustThrustUp(inputs, 6f);
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
//		thrustPID.adjustThrustDown(inputs, -1.5f);
	}
	
//	sterk stijgen > 4
//	stijgen > 1
//	sterk dalen < -4
//	dalen <-1

	private void control(AutopilotInputs input, State state) {

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
				if (Math.abs(input.getRoll()) < 0.01)
					aoaManager.setInclNoAOA(input);
				break;
			case Left:
				turnLeft(input);
				break;
			case Right:
				turnRight(input);
				break;
			default:
				break;
		}
	}
	
	private void turnRight(AutopilotInputs input) {
		rollPID.adjustRoll(input, FloatMath.toRadians(-40));
		thrustPID.adjustThrustUp(input, 0.37f);
		pitchPID.adjustPitchTurn(input, FloatMath.toRadians(0));
	}
	
	private void turnLeft(AutopilotInputs input) {
		rollPID.adjustRoll(input, FloatMath.toRadians(20));
		thrustPID.adjustThrustUp(input, 0.37f);
		pitchPID.adjustPitchTurn(input, FloatMath.toRadians(0));
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
