package pilot;

import com.stormbots.MiniPID;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import org.joml.Vector3f;
import utils.FloatMath;
import utils.Utils;

public class TaxiPilot extends PilotPart {

	private Vector3f targetPos;

	private MiniPID thrustPID;

	private boolean ended;

	private float time, maxThrust, maxBrakeForce;

	//private Vector3f oldPos = new Vector3f(0, 0, 0);
	private Vector3f oldPos = new Vector3f(0, 0, 0);

	public TaxiPilot() {
		this.targetPos = new Vector3f(-100, 0, -100);
		thrustPID = new MiniPID(50, 0, 0);

		this.ended = false;
	}

	public Vector3f getTarget() {
		return targetPos;
	}

	public float turn(float speed) {
		float brakeforce = (float)15*speed*speed;

		System.out.println(brakeforce);

		if (brakeforce < maxBrakeForce) {
			return brakeforce;
		} else {
			return maxBrakeForce;
		}
	}



	@Override
	public void initialize(AutopilotConfig config) {
		this.maxThrust = config.getMaxThrust();
		this.maxBrakeForce = config.getRMax();

		thrustPID.setOutputLimits(0, maxThrust);
	}


	@Override
	public AutopilotOutputs timePassed(AutopilotInputs input) {



		float dt = input.getElapsedTime() - this.time;

		Vector3f pos = new Vector3f(input.getX(), 0, input.getZ());

		Vector3f vel = pos.sub(this.oldPos, new Vector3f()).mul(1/dt);
		this.oldPos = pos;

		float fBrake = 0, lBrake = 0 , rBrake = 0, thrust, taxispeed;
		float speed = FloatMath.norm(vel);
		float distance = pos.distance(targetPos);
		float targetHeading = (float) Math.atan2(pos.x() - targetPos.x(), pos.z() - targetPos.z());

		if (Float.isNaN(speed)) {
			speed = 0;
		}

		System.out.printf("Current distance: %s\t", distance);
		System.out.printf("Current heading: %s\t", Math.toDegrees(input.getHeading()));
		System.out.printf("Target heading: %s\t", Math.toDegrees(targetHeading));
		System.out.printf("Current speed: %s\t \n", speed);

		float turnaccuracy;
		if (distance < 4) {
			turnaccuracy = (float)Math.toRadians(2);
		} else {
			turnaccuracy = (float)Math.toRadians(10);
		}

		if (distance < 1) {
			thrust = 0;
			if (speed > 1) {
				lBrake = (float)0.2*maxBrakeForce;
				rBrake = (float)0.2*maxBrakeForce;
				fBrake = (float)0.2*maxBrakeForce;
			} else {
				System.out.println("Destination reached");
				this.targetPos = new Vector3f(100, 0, -200);
			}
		}
		else if (targetHeading - input.getHeading() < -(turnaccuracy)) {
			thrust = 70;
			rBrake = maxBrakeForce;
		}
		else if (targetHeading - input.getHeading() > turnaccuracy) {
			thrust = 70;
			lBrake = maxBrakeForce;
		} else {
			if (distance > 10) {taxispeed = 5;}
			else {taxispeed = 1;}
			if (speed <= 1.2*taxispeed) {
				thrustPID.setSetpoint(taxispeed);
				thrust = (float)thrustPID.getOutput(speed);
			} else {
				thrust = 0;
				lBrake = (float)0.2*maxBrakeForce;
				rBrake = (float)0.2*maxBrakeForce;
			}
		}



		/*
		if (distance < 20) {
			taxispeed = 1;
		} else {
			taxispeed = 4;
		}

		thrustPID.setSetpoint(taxispeed);
		thrust = (float)thrustPID.getOutput(speed);

		if (targetHeading - input.getHeading() < -(Math.toRadians(10))) {
			rBrake = turn(speed);
		}
		else if (targetHeading - input.getHeading() > Math.toRadians(10)) {
			lBrake = turn(speed);
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
