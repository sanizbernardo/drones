package autopilot.pilots;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import autopilot.PilotPart;
import autopilot.airports.VirtualAirport;
import autopilot.pilots.FlyPilot.State;
import autopilot.pilots.fly.pid.PitchPID;
import autopilot.pilots.fly.pid.RollPID;
import autopilot.pilots.fly.pid.ThrustPID;
import utils.Constants;
import utils.FloatMath;
import utils.Utils;

import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

public class FlyPilot extends PilotPart {

	private boolean ended;

	private VirtualAirport currentDestionationAirport;
	
	public static enum State{Left, SoftLeft, VerySoftLeft, Right, SoftRight, VerySoftRight, Stable, Up, Down, StrongUp, StrongDown, SlowDown};
	private State currentState;
	
	private float climbAngle;
	private float rMax;
	private float maxThrust;
	private float turnRadius;
	
	private float leftWingInclination;
	private float rightWingInclination;
	private float horStabInclination;
	private float verStabInclination;
	private float newThrust;
	private float stableTime = 0f;
	
	private Vector3f timePassedOldPos = new Vector3f(0, 0, 0);
	public Vector3f approxVel = new Vector3f(0f, 0f, 0f);
	private float time = 0f;
	
	private PitchPID pitchPID;
	private ThrustPID thrustPID;
	private RollPID rollPID;
	
	boolean check = true;
	
	public FlyPilot(VirtualAirport destinationAirport) {
		this.currentDestionationAirport = destinationAirport;
	}
	
	
	@Override
	public void initialize(AutopilotConfig config) {
		this.climbAngle = Constants.climbAngle;
		this.rMax = config.getRMax();
		this.maxThrust = config.getMaxThrust();
		this.turnRadius = 576f;
//		pointBR = new Vector3f(-this.turnRadius, 100f, -this.turnRadius - 700f);
		pointBR = getTargetPos(currentDestionationAirport.getPosition(), currentDestionationAirport.getHeading(), 1200);
		this.pitchPID = new PitchPID(this);
		this.thrustPID = new ThrustPID(this);
		this.rollPID = new RollPID(this);
		
		
	}
	

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {
		Vector3f pos = new Vector3f(inputs.getX(), inputs.getY(), inputs.getZ());
		
		float dt = inputs.getElapsedTime() - this.time;
		this.time = inputs.getElapsedTime();
		if (dt != 0)
			this.approxVel = pos.sub(this.timePassedOldPos, new Vector3f()).mul(1/dt);
		this.timePassedOldPos = pos;
		
		if(!part1Complete && pos.distance(pointBR) < this.turnRadius+100 && check){
			setCurrentState(State.Stable);
			control(inputs, currentState);
			return Utils.buildOutputs(leftWingInclination,
					rightWingInclination, verStabInclination, horStabInclination,
					newThrust, rMax, rMax, rMax);
		}
		
		
		check = false;
		
		if (this.stableTime > 0) {
			setCurrentState(State.Stable);
			this.stableTime -=  dt;

			control(inputs, currentState);

			if(stableTime <= 0) {
				this.ended = true;
			}
			
			return Utils.buildOutputs(leftWingInclination,
					rightWingInclination, verStabInclination, horStabInclination,
					newThrust, rMax, rMax, rMax);
		}

		// moeten we omhoog?
		if ((60 - pos.y) > 5) {//TODO magic number
			if (inputs.getRoll() > FloatMath.toRadians(5))
				setCurrentState(State.Stable);
			else
				setCurrentState(State.StrongUp);
		}
		// moeten we omlaag?
		else if (pos.y - 60 > 5) {
			if (inputs.getRoll() > FloatMath.toRadians(5))
				setCurrentState(State.Stable);
			else
				setCurrentState(State.StrongDown);
		}
		// iets meer stijgen
		else if (getCurrentState() == State.StrongUp && (60 - pos.y) > 2) {
			
		}
		else {
			// draaien nodig?
			//Vector3f diff = getCurrentCube().sub(pos, new Vector3f());
			float targetHeading = makeNormal(getTargetHeading(inputs));
			Boolean side = null;
			Boolean sideSmall = null;
			Boolean sideVerySmall = null;
			// null: nee, true: links, false: rechts
			Vector3f result = new Vector3f(FloatMath.cos(inputs.getHeading()),0,-FloatMath.sin(inputs.getHeading())).cross(new Vector3f(FloatMath.cos(targetHeading),0,-FloatMath.sin(targetHeading)), new Vector3f());
			if (result.normalize().y >= 0 && Math.abs(targetHeading - inputs.getHeading()) > FloatMath.toRadians(8f))
				side = true;
			else if (result.normalize().y < 0 && Math.abs(targetHeading - inputs.getHeading()) > FloatMath.toRadians(8f)) {;
				side = false;
			}
			if (result.normalize().y >= 0 && Math.abs(targetHeading - inputs.getHeading()) > FloatMath.toRadians(5f))
				sideSmall = true;
			else if (result.normalize().y < 0 && Math.abs(targetHeading - inputs.getHeading()) > FloatMath.toRadians(5f)) {;
				sideSmall = false;
			}
			if (result.normalize().y >= 0 && Math.abs(targetHeading - inputs.getHeading()) > FloatMath.toRadians(2f))
				sideVerySmall = true;
			else if (result.normalize().y < 0 && Math.abs(targetHeading - inputs.getHeading()) > FloatMath.toRadians(2f)) {;
				sideVerySmall = false;
			}
			
			// bocht haalbaar?
			if (side != null) {
//				if (turnable(pos, inputs.getHeading(), getCurrentCube(), this.turnRadius)) {
						setCurrentState(side? State.Left: State.Right);
//				} else
//				// bocht niet haalbaar, rechtdoor gaan. 
//				setCurrentState(State.Stable);
			}
			else if(side == null && sideSmall != null){
				setCurrentState(sideSmall? State.SoftLeft: State.SoftRight);
			}else if(sideSmall == null && sideVerySmall != null){
				setCurrentState(sideVerySmall? State.VerySoftLeft: State.VerySoftRight);
			}
			else {
				setCurrentState(State.Stable);
			}
		}
		if(part1Complete){
			setCurrentState(part2Direction);
		}
		control(inputs, currentState);
		if(stableTime > 0){
			setCurrentState(State.Stable);
		}
		return Utils.buildOutputs(leftWingInclination,
				rightWingInclination, verStabInclination, horStabInclination,
				newThrust, rMax, rMax, rMax);
	}
	
	


	private void setCurrentState(State state) {
		this.currentState = state;
	}
	
	public State getCurrentState() {
		return currentState;
	}

	
	
	private void control(AutopilotInputs input, State state) {
		switch(state){
			case StrongUp:
				climbPID(input);
				break;
			case Up:
				risePID(input);
				break;
			case StrongDown:
				dropPID(input);
				break;
			case Down:
				descendPID(input);
				break;
			case Stable:
				flyStraightPID(input);
				break;
			case Left:
				turnLeft(input);
				break;
			case Right:
				turnRight(input);
				break;
			case SoftLeft:
				turnSoftLeft(input);
				break;
			case VerySoftLeft:
				turnVerySoftLeft(input);
				break;
			case SoftRight:
				turnSoftRight(input);
				break;	
			case VerySoftRight:
				turnVerySoftRight(input);
				break;
			case SlowDown:
				slowDown(input);
				setLeftWingInclination(FloatMath.toRadians(2));
				setRightWingInclination(FloatMath.toRadians(2));
				break;
			default:
				break;
		}
	}
	
	
	/**
	 * PIDs set pitch and thrust to fly straight.
	 */
	private void flyStraightPID(AutopilotInputs input) {
		rollPID.adjustRoll(input, FloatMath.toRadians(0), State.Stable);
		pitchPID.adjustPitchClimb(input, 0f);
		thrustPID.adjustThrustUp(input, 0.4f);
	}

	/**
	 * Causes drone to climb by changing pitch and using thrust to increase
	 * vertical velocity
	 */
	private void climbPID(AutopilotInputs inputs) {
		rollPID.adjustRoll(inputs, FloatMath.toRadians(0), State.StrongUp);
		pitchPID.adjustPitchClimb(inputs, climbAngle);
		thrustPID.adjustThrustUp(inputs, 6f);
	}

	private void dropPID(AutopilotInputs inputs) {
		rollPID.adjustRoll(inputs, FloatMath.toRadians(0), State.StrongDown);
		pitchPID.adjustPitchDown(inputs, FloatMath.toRadians(-3f));
		thrustPID.adjustThrustDown(inputs, -2f);
	}

	/**
	 * Causes drone to rise by increasing lift through higher speed.
	 */
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
		// thrustPID.adjustThrustDown(inputs, -1.5f);
	}
	
	private void slowDown(AutopilotInputs input) {
		rollPID.adjustRoll(input, FloatMath.toRadians(0), State.SlowDown);	
		pitchPID.adjustPitchTurn(input, FloatMath.toRadians(0));
		setNewThrust(0);
	}

	private void turnRight(AutopilotInputs input) {
		rollPID.adjustRoll(input, FloatMath.toRadians(-20), State.Right);
		thrustPID.adjustThrustUp(input, 0.37f);
		pitchPID.adjustPitchTurn(input, FloatMath.toRadians(0));
	}
	
	private void turnLeft(AutopilotInputs input) {
		rollPID.adjustRoll(input, FloatMath.toRadians(20), State.Left);
		thrustPID.adjustThrustUp(input, 0.37f);
		pitchPID.adjustPitchTurn(input, FloatMath.toRadians(0));
	}  
	
	private void turnSoftRight(AutopilotInputs input) {
		System.out.println("skhjflhgflsqdhgflqshkjdgfhlk");
		rollPID.adjustRoll(input, FloatMath.toRadians(-10), State.Right);
		thrustPID.adjustThrustUp(input, 0.37f);
		pitchPID.adjustPitchTurn(input, FloatMath.toRadians(0));
	}
	
	private void turnSoftLeft(AutopilotInputs input) {
		System.out.println("skhjflhgflsqdhgflqshkjdgfhlk2");
		rollPID.adjustRoll(input, FloatMath.toRadians(10), State.Left);
		thrustPID.adjustThrustUp(input, 0.37f);
		pitchPID.adjustPitchTurn(input, FloatMath.toRadians(0));
	}
	
	private void turnVerySoftRight(AutopilotInputs input) {
		System.out.println("skhjflhgflsqdhgflqshkjdgfhlk");
		rollPID.adjustRoll(input, FloatMath.toRadians(-4), State.Right);
		thrustPID.adjustThrustUp(input, 0.37f);
		pitchPID.adjustPitchTurn(input, FloatMath.toRadians(0));
	}
	
	private void turnVerySoftLeft(AutopilotInputs input) {
		System.out.println("skhjflhgflsqdhgflqshkjdgfhlk2");
		rollPID.adjustRoll(input, FloatMath.toRadians(4), State.Left);
		thrustPID.adjustThrustUp(input, 0.37f);
		pitchPID.adjustPitchTurn(input, FloatMath.toRadians(0));
	}

	
	public Vector3f horProjVel(AutopilotInputs inputs) {
		Vector3f relVelD = getTransMat(inputs).transform(approxVel,
				new Vector3f());
		return new Vector3f(0, relVelD.y, relVelD.z);
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
	
	private static boolean turnable(Vector3f pos, float heading, Vector3f target, float radius) {
		Vector3f perp = new Vector3f(FloatMath.sin(heading + FloatMath.toRadians(90)), 0, 
									   FloatMath.cos(heading + FloatMath.toRadians(90)));
		
		Vector3f center1 = pos.add(perp.mul(radius), new Vector3f()),
				 center2 = pos.add(perp.mul(-radius), new Vector3f());
		
		if (center1.distance(target) < radius || center2.distance(target) < radius)
			return false;
		
		return true;
	}
	
	
	public float getMaxThrust() {
		return maxThrust;
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
		
	}

	@Override
	public String taskName() {
		return "Fly: " + (this.currentState == null? "" :this.currentState.name());
	}
	
	
	
	private boolean part1Complete = false;
	private State part2Direction = State.Stable;

	private Vector3f pointBR;
	
	private float getTargetHeading(AutopilotInputs inputs){
		float[] pointBeforeRunway = {pointBR.x, pointBR.y, pointBR.z}; //TODO
		float headingRunwayToPoint = currentDestionationAirport.getHeading(); //TODO: dit zou hetzelfde moeten zijn als centerToRunway0 of + 180� (centerToRunway1)
		if(!part1Complete){
			return getTargetHeadingPart1(inputs, pointBeforeRunway, headingRunwayToPoint);

		}
		else{
			float heading = makeNormal(getTargetHeadingPart2(inputs, pointBeforeRunway, headingRunwayToPoint));
			if(Math.abs(Math.toDegrees(inputs.getHeading() - heading)) < 3.3){
				this.stableTime = 1.5f;
			}
			return heading;
		}
	}
	
	private float headingChecker = 0f;
	
	private float getTargetHeadingPart1(AutopilotInputs inputs, float[] pointBeforeRunway, float headingRunwayToPoint){
		float[] planePosition = {inputs.getX(), inputs.getY(), inputs.getZ()};
		
		
		float temp = 0;
		if(orientation(pointBeforeRunway, auxLocPlusMinZ(pointBeforeRunway, headingRunwayToPoint, 1), planePosition) == 1){
			temp = this.turnRadius;
			part2Direction = State.Left;
		}
		else{
			temp = -this.turnRadius;
			part2Direction = State.Right;
		}
		
		Vector3f pos = new Vector3f(inputs.getX(), inputs.getY(), inputs.getZ());
		float[] aux = auxLocPlusX(pointBeforeRunway, headingRunwayToPoint, temp);
		Vector3f pBRPaux = new Vector3f(aux[0], aux[1], aux[2]);
		if(pos.distance(pBRPaux) < this.turnRadius + 10 && pos.distance(pBRPaux) > this.turnRadius - 10 && /*pos.distance(pointBR) < this.turnRadius + 100 && */ Math.abs(headingChecker - inputs.getHeading()) < 0.1){ //TODO deze condities nog is nakijken
			part1Complete = true;
		}
		
		float corner = 0;
		
		//plane behind the pointBeforeRunway
		if(orientation(pointBeforeRunway, auxLocPlusX(pointBeforeRunway, headingRunwayToPoint, 1), planePosition) == 1){
			float dm = this.turnRadius;
			float dg = (float)Math.sqrt(Math.pow(pointBeforeRunway[0] - planePosition[0], 2) + Math.pow(pointBeforeRunway[2] - planePosition[2], 2));
			float mg = (float)Math.sqrt(Math.pow(auxLocPlusX(pointBeforeRunway, headingRunwayToPoint, temp)[0] - planePosition[0], 2) + Math.pow(auxLocPlusX(pointBeforeRunway, headingRunwayToPoint, temp)[2] - planePosition[2], 2));
			float bigCorner = (float)(2*Math.PI) - (float)Math.acos((dg*dg - dm*dm - mg*mg)/(-2*dm*mg));
			float smallCorner = (float)Math.acos(turnRadius/mg);
			corner = bigCorner - smallCorner;
		}
		//Plane in front of the pointBeforeRunway
		else{
			float dm = this.turnRadius;
			float dg = (float)Math.sqrt(Math.pow(pointBeforeRunway[0] - planePosition[0], 2) + Math.pow(pointBeforeRunway[2] - planePosition[2], 2));
			float mg = (float)Math.sqrt(Math.pow(auxLocPlusX(pointBeforeRunway, headingRunwayToPoint, temp)[0] - planePosition[0], 2) + Math.pow(auxLocPlusX(pointBeforeRunway, headingRunwayToPoint, temp)[2] - planePosition[2], 2));
			float bigCorner = (float)Math.acos((dg*dg - dm*dm - mg*mg)/(-2*dm*mg));
			float smallCorner = (float)Math.acos(turnRadius/mg);
			corner = bigCorner - smallCorner;
		}
		float retval =0f;
		//Plane to the right of the pointBeforeRunway
		if(orientation(pointBeforeRunway, auxLocPlusMinZ(pointBeforeRunway, headingRunwayToPoint, 1), planePosition) == 1){
			retval = headingRunwayToPoint - corner + (float) Math.PI;
			if(!Float.isNaN(retval)){
				headingChecker = makeNormal(retval);
			}
			return retval;
		}
		//Plane to the left of the pointBeforeRunway
		else if(orientation(pointBeforeRunway, auxLocPlusMinZ(pointBeforeRunway, headingRunwayToPoint, 1), planePosition) == 2){
			retval = headingRunwayToPoint + corner + (float) Math.PI;
			if(!Float.isNaN(retval)){
				headingChecker = makeNormal(retval);
			}
			return retval;
		}
		retval = headingRunwayToPoint + (float) Math.PI;
		if(!Float.isNaN(retval)){
			headingChecker = makeNormal(retval);
		}
		return retval;
	}
	
	private float getTargetHeadingPart2(AutopilotInputs inputs, float[] pointBeforeRunway, float headingRunwayToPoint){
		//TODO
		return headingRunwayToPoint + (float) Math.PI;
		
	}
	
	//Helper functions
	private float[] auxLocPlusMinZ(float[] planePosition, float heading, float arg) {
		float[] newLoc = new float[3];
		newLoc[0] = planePosition[0] + (float)Math.sin(-heading)*arg;
		newLoc[1] = planePosition[1];
		newLoc[2] = planePosition[2] - (float)Math.cos(-heading)*arg;
		return newLoc;
	}
	
	private float[] auxLocPlusX(float[] planePosition, float heading, float arg){
		float[] newLoc = new float[3];
		newLoc[0] = planePosition[0] + (float)Math.cos(-heading)*arg;
		newLoc[1] = planePosition[1];
		newLoc[2] = planePosition[2] + (float)Math.sin(-heading)*arg;
		return newLoc;
		}

	private int orientation(float[] p, float[] q, float[] r) {
    	float val = (-q[2] + p[2]) * (r[0] - q[0]) -
                (q[0] - p[0]) * (-r[2] + q[2]);

        if (val == 0) return 0;
        return (val > 0)? 1: 2;	//Clock - Counterclockwise
	}
	

	private float makeNormal(float targetHeading) {
		float ret = targetHeading;
		while(ret > Math.PI){
			ret -= (float) Math.PI * 2;
		}
		while(ret < -Math.PI){
			ret += (float) Math.PI * 2;
		}
		return ret;
	}
	
	private Vector3f getTargetPos(Vector3f airportPos, float heading, float distance) {
		Vector3f targetPos = new Vector3f();
		targetPos.x = (float) (airportPos.x + Math.sin(Math.PI+heading)*distance);
		targetPos.y = airportPos.y;
		targetPos.z = (float) (airportPos.z + Math.cos(Math.PI+heading)*distance);
		return targetPos;
	}
}
