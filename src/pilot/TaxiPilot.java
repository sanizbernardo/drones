package pilot;

import com.stormbots.MiniPID;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import org.joml.Vector3f;
import utils.FloatMath;
import utils.Utils;

import java.util.List;

public class TaxiPilot extends PilotPart {

	private Vector3f targetPos;

	private List<Vector3f> targetlist;

	private MiniPID thrustPID, brakePID, turnPID;

	private boolean ended;

	private float time, maxThrust, maxBrakeForce;

	//private Vector3f oldPos = new Vector3f(0, 0, 0);
	private Vector3f oldPos = new Vector3f(0, 0, 0);

	public TaxiPilot() {
		this.targetPos = new Vector3f(0, 0, 100);
		thrustPID = new MiniPID(70, 0.1, 0.1);
		brakePID = new MiniPID(30,0.1,0.1);
		turnPID = new MiniPID(30, 0.02, 0);

		this.ended = false;
	}

	public TaxiPilot(List<Vector3f> targetlist) {
		this.targetlist = targetlist;
		this.targetPos = targetlist.iterator().next();
		thrustPID = new MiniPID(70, 0.1, 0.1);
		brakePID = new MiniPID(30,0.1,0.1);
		turnPID = new MiniPID(30, 0.02, 0);

		this.ended = false;
	}

	public Vector3f getTarget() {
		return targetPos;
	}


	@Override
	public void initialize(AutopilotConfig config) {
		this.maxThrust = config.getMaxThrust();
		this.maxBrakeForce = config.getRMax();

		brakePID.setOutputLimits(0, maxBrakeForce);
		thrustPID.setOutputLimits(0, maxThrust);
		turnPID.setOutputLimits(0, maxThrust);
		brakePID.setSetpoint(0);
		turnPID.setSetpoint(0);
	}


	@Override
	public AutopilotOutputs timePassed(AutopilotInputs input) {
		float dt = input.getElapsedTime() - this.time;

		Vector3f pos = new Vector3f(input.getX(), 0, input.getZ());

		Vector3f vel = pos.sub(this.oldPos, new Vector3f()).mul(1/dt);
		this.oldPos = pos;

		float fBrake = 0, lBrake = 0 , rBrake = 0, thrust, taxispeed;
		float speed = FloatMath.norm(vel);
		float distance = pos.distance(getTarget());
		float targetHeading = FloatMath.atan2(pos.x() - getTarget().x(), pos.z() - getTarget().z());
		float headingerror = targetHeading - input.getHeading();

		if (Float.isNaN(speed)) {
			speed = 0;
		}

		System.out.printf("Current distance: %s\t", distance);
		System.out.printf("Current heading: %s\t", Math.toDegrees(input.getHeading()));
		System.out.printf("Target heading: %s\t", Math.toDegrees(targetHeading));
		System.out.printf("Current speed: %s\t \n", speed);

		float turnaccuracy;
		if (distance < 15f) {
			turnaccuracy = FloatMath.toRadians(2);
			taxispeed = 1f;
		} else {
			turnaccuracy = FloatMath.toRadians(4);
			taxispeed = 5f;
		}

		thrustPID.setSetpoint(taxispeed);

		if (speed <= taxispeed) {
			thrust = (float)thrustPID.getOutput(speed);
			fBrake = 0f;
		} else {
			fBrake = (float)brakePID.getOutput(taxispeed - speed);
			thrust = 0f;
		}

		if (distance < 3f) {
			thrust = 0f;
			if (speed > 1f) {
				lBrake = 0.2f*maxBrakeForce;
				rBrake = 0.2f*maxBrakeForce;
				fBrake = 0.2f*maxBrakeForce;
			} else {
				this.targetPos = new Vector3f(-100, 0, -100);
				//this.targetPos = targetlist.iterator().next();
				if (targetPos == null) {
					System.out.println("Destination reached");
					this.ended = true;
				}
			}
		} else {
//			System.out.println("target: " + targetHeading + " own: " + inputs.getHeading());
			Boolean side = null;
			// null: nee, true: links, false: rechts
			Vector3f result = new Vector3f(FloatMath.cos(input.getHeading()),0,-FloatMath.sin(input.getHeading())).cross(new Vector3f(FloatMath.cos(targetHeading),0,-FloatMath.sin(targetHeading)), new Vector3f());
			System.out.println(result.normalize().y >= 0 ? "left" : "right");
			if (result.normalize().y >= 0 && Math.abs(headingerror) > turnaccuracy)
				side = true;
			else if (result.normalize().y < 0 && Math.abs(headingerror) > turnaccuracy) {;
				side = false;
			}
			if (side != null) {
				thrust = 70f;
				fBrake = 0;
				if (side) {
					lBrake = 0.5f*maxBrakeForce;
				} else {
					rBrake = 0.5f*maxBrakeForce;
				}

			}
		}

/*		else if ( headingerror < -(turnaccuracy) || headingerror > Math.toRadians(179)) {
			thrust = (float)turnPID.getOutput(-Math.abs(headingerror));
			rBrake = (float)0.5*maxBrakeForce;
			fBrake = 0;
		}
		else if (headingerror > turnaccuracy || headingerror < -Math.toRadians(179)) {
			thrust = (float)turnPID.getOutput(-Math.abs(headingerror));
			lBrake = (float)0.5*maxBrakeForce;
			fBrake = 0;
		}
*/
		this.time = input.getElapsedTime();

		return Utils.buildOutputs(0, 0, 0, 0, thrust, lBrake, fBrake, rBrake);
	}


	@Override
	public boolean ended() {
		return ended;
	}


	@Override
	public void close() { }


	@Override
	public String taskName() {
		return "Taxi";
	}

}
