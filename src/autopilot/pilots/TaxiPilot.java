package autopilot.pilots;

import com.stormbots.MiniPID;

import autopilot.PilotPart;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import org.joml.Vector3f;
import utils.FloatMath;
import utils.Utils;

import java.util.*;

public class TaxiPilot extends PilotPart {

	private Vector3f targetPos;

	private List<Vector3f> targetlist;

	private MiniPID thrustPID;

	private boolean ended;

	private float time, maxThrust, maxBrakeForce;

	private int counter;

	private Vector3f oldPos = new Vector3f(0, 0, 0);

	public TaxiPilot() {
		thrustPID = new MiniPID(100, 0.1, 0.1);

		targetlist = new ArrayList<Vector3f>();
		targetlist.add(new Vector3f(0,0,0));

		this.ended = false;
	}

	public TaxiPilot(List<Vector3f> targetlist) {
		thrustPID = new MiniPID(100, 0.1, 0.1);

		this.targetlist = targetlist;

		this.ended = false;
	}

	public Vector3f getTarget() {
		return targetPos;
	}


	@Override
	public void initialize(AutopilotConfig config) {
		this.maxThrust = config.getMaxThrust();
		this.maxBrakeForce = config.getRMax();

		thrustPID.setOutputLimits(0, maxThrust);

		counter = 0;
		this.targetPos = targetlist.get(counter);
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

		float turnaccuracy;
		if (distance < 15f) {
			turnaccuracy = FloatMath.toRadians(2);
			taxispeed = 3f;
		} else if (distance < 120f) {
			turnaccuracy = FloatMath.toRadians(4);
			taxispeed = 10f;
		} else {
			turnaccuracy = FloatMath.toRadians(4);
			taxispeed = 30f;
		}

		thrustPID.setSetpoint(taxispeed);

		if (speed <= taxispeed) {
			thrust = (float)thrustPID.getOutput(speed);
			fBrake = 0f;
		} else {
			fBrake = maxBrakeForce;
			lBrake = maxBrakeForce;
			rBrake = maxBrakeForce;
			thrust = 0f;
		}

		if (distance < 3f) {
			if (speed > 1f) {
				thrust = 0f;
				lBrake = maxBrakeForce;
				rBrake = maxBrakeForce;
				fBrake = maxBrakeForce;
			} else {
				counter += 1;
				if (counter < targetlist.size()) {
					this.targetPos = targetlist.get(counter);
				} else {
					System.out.println("Final destination reached");
					System.out.println("Elapsed time: " + input.getElapsedTime());
					this.ended = true;
					return Utils.buildOutputs(0, 0, 0, 0, 0, 0, 0, 0);
				}
			}
		} else {
			Boolean side = null;
			Vector3f result = new Vector3f(FloatMath.cos(input.getHeading()),0,-FloatMath.sin(input.getHeading())).cross(new Vector3f(FloatMath.cos(targetHeading),0,-FloatMath.sin(targetHeading)), new Vector3f());
			if (result.normalize().y >= 0 && Math.abs(headingerror) > turnaccuracy)
				side = true;
			else if (result.normalize().y < 0 && Math.abs(headingerror) > turnaccuracy) {;
				side = false;
			}

			if (side != null) {
				thrust = 70f;
				fBrake = 0;
				lBrake = 0;
				rBrake = 0;
				if (side) {
					lBrake = 500f;
				} else {
					rBrake = 500f;
				}
			}
		}
		this.time = input.getElapsedTime();

		return Utils.buildOutputs(0, 0, 0, 0, thrust, lBrake, fBrake, rBrake);
	}
	
	public void setTargetlist(List<Vector3f> targetlist) {
		this.targetlist = targetlist;
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
