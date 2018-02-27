package pilot;

import org.joml.Vector3f;

import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import utils.FloatMath;
import utils.Utils;

public class TakeOffPilot extends PilotPart {
	
	private final float targetHeight,
						takeOffSpeed = 120f/3.6f,
						climbAngle = FloatMath.toRadians(20);
	
	private float maxThrust;
	
	private boolean ended;
	
	private Vector3f oldPos = new Vector3f(0, 0, 0);
		
	
	public TakeOffPilot(float targetHeight) {
		this.targetHeight = targetHeight;
		
		this.ended = false;
	}
	
	
	@Override
	public void initialize(AutopilotConfig config) {
		this.maxThrust = config.getMaxThrust();
	}

	
	@Override
	public AutopilotOutputs timePassed(AutopilotInputs input) {
		Vector3f pos = new Vector3f(input.getX(), input.getY(), input.getZ());
		
		Vector3f vel = pos.sub(this.oldPos, new Vector3f()).mul(1/input.getElapsedTime());
		this.oldPos = pos;
		
		float lwIncl, rwIncl, horStabIncl, thrust;
		float speed = FloatMath.norm(vel);
		
		
		if (speed < takeOffSpeed) {			
			lwIncl = 0;
			rwIncl = 0;
			horStabIncl = 0;
			thrust = this.maxThrust;
		} else {
			lwIncl = 10;
			rwIncl = 10;
			horStabIncl = -0.5f*(this.climbAngle + input.getPitch());		
			horStabIncl = horStabIncl > 12 ? 12: horStabIncl;
			thrust = this.maxThrust;
		}
		
		if (pos.y > targetHeight) {
			lwIncl = 0;
			rwIncl = 0;
			horStabIncl = 0;
			thrust = 0;
			this.ended = true;
		}
				
		return Utils.buildOutputs(FloatMath.toRadians(lwIncl), FloatMath.toRadians(rwIncl), 0, FloatMath.toRadians(horStabIncl), thrust, 0, 0, 0);
	}

	
	@Override
	public boolean ended() {
		return ended;
	}

	
	@Override
	public void close() { }


	@Override
	public String taskName() {
		return "Takeoff";
	}
}
