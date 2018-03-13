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

	private MiniPID thrustPID, brakePID, turnPID;

	private boolean ended;

	private float time, maxThrust, maxBrakeForce;

	//private Vector3f oldPos = new Vector3f(0, 0, 0);
	private Vector3f oldPos = new Vector3f(0, 0, 0);

	public TaxiPilot() {
		this.targetPos = new Vector3f(100, 0, -100);
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
		float targetHeading = (float) Math.atan2(pos.x() - getTarget().x(), pos.z() - getTarget().z());
		float headingerror = targetHeading - input.getHeading();

		if (Float.isNaN(speed)) {
			speed = 0;
		}

		System.out.printf("Current distance: %s\t", distance);
		System.out.printf("Current heading: %s\t", Math.toDegrees(input.getHeading()));
		System.out.printf("Target heading: %s\t", Math.toDegrees(targetHeading));
		System.out.printf("Current speed: %s\t \n", speed);

		float turnaccuracy;
		if (distance < 15) {
			turnaccuracy = (float)Math.toRadians(3);
			taxispeed = 1;
		} else {
			turnaccuracy = (float)Math.toRadians(7);
			taxispeed = 5;
		}

		thrustPID.setSetpoint(taxispeed);

		if (speed <= taxispeed) {
			thrust = (float)thrustPID.getOutput(speed);
			fBrake = 0;
		} else {
			fBrake = (float)brakePID.getOutput(taxispeed - speed);
			thrust = 0;

		}

		if (distance < 1) {
			thrust = 0;
			if (speed > 1) {
				lBrake = (float)0.2*maxBrakeForce;
				rBrake = (float)0.2*maxBrakeForce;
				fBrake = (float)0.2*maxBrakeForce;
			} else {
				System.out.println("Destination reached");
				this.targetPos = new Vector3f(-100, 0, -200);
			}
		}
		else if ( headingerror < -(turnaccuracy) || headingerror > Math.toRadians(179)) {
			thrust = (float)turnPID.getOutput(-Math.abs(headingerror));
			rBrake = (float)0.5*maxBrakeForce;
			fBrake = 0;
		}
		else if (headingerror > turnaccuracy || headingerror < -Math.toRadians(179)) {
			thrust = (float)turnPID.getOutput(-Math.abs(headingerror));
			lBrake = (float)0.5*maxBrakeForce;
			fBrake = 0;
		}

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
