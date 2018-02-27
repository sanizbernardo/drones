package pilot;

import com.stormbots.MiniPID;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import org.joml.Vector3f;
import utils.FloatMath;
import utils.Utils;

public class TaxiPilot extends PilotPart {

	private final Vector3f targetPos;

	private MiniPID thrustPID;

	private boolean ended;

	//private Vector3f oldPos = new Vector3f(0, 0, 0);
	private Vector3f oldPos = new Vector3f(0, 0, 0);

	public TaxiPilot() {
		this.targetPos = new Vector3f(-100, 0, -100);
		thrustPID = new MiniPID(1, 0, 0);

		this.ended = false;
	}

	public Vector3f getTarget() {
		return targetPos;
	}

	public float turn(float distance, float speed) {

		return 0;
	}


	@Override
	public void initialize(AutopilotConfig config) {
	}


	@Override
	public AutopilotOutputs timePassed(AutopilotInputs input) {
		Vector3f pos = new Vector3f(input.getX(), input.getY(), input.getZ());

		Vector3f vel = pos.sub(this.oldPos, new Vector3f()).mul(1/input.getElapsedTime());
		this.oldPos = pos;

		float fBrake = 0, lBrake = 0 , rBrake = 0, thrust, taxispeed;
		float speed = FloatMath.norm(vel);
		float distance = pos.distance(targetPos);
		float targetHeading = (float) Math.atan2(targetPos.x() - pos.x(), targetPos.z() - pos.z());

		System.out.printf("Current distance: %s\\t", distance);
		System.out.printf("Current heading: %s\\t", input.getHeading());
		System.out.printf("Target heading: %s\\t \\n", targetHeading);

		if (distance < 20) {
			taxispeed = 1;
		} else if (distance < 100) {
			taxispeed = 5;
		} else {taxispeed = 20;}

		thrustPID.setSetpoint(taxispeed);
		thrust = (float)thrustPID.getOutput(speed);

		if (speed > 0.7*taxispeed && speed < 1.3*taxispeed) {
			if (targetHeading - input.getHeading() > Math.toRadians(10)) {
				lBrake = turn(distance, speed);
			}
			else if (targetHeading - input.getHeading() < Math.toRadians(10)) {
				rBrake = -turn(distance, speed);
			}
		}

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
		return "Reached destination";
	}

}
