package pilot;

import org.joml.Vector3f;

import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import utils.FloatMath;
import utils.Utils;

public class LandingPilot extends PilotPart {

	private boolean ended;
	private float maxThrust;
	private final float climbAngle = FloatMath.toRadians(20);
	
	@Override
	public void initialize(AutopilotConfig config) {
		this.maxThrust = config.getMaxThrust();
		
	}

	private Vector3f oldPos = new Vector3f(0, 0, 0);

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs input) {
		Vector3f pos = new Vector3f(input.getX(), input.getY(), input.getZ());
		
		Vector3f vel = pos.sub(this.oldPos, new Vector3f()).mul(1/input.getElapsedTime());
		this.oldPos = pos;

		float lwIncl, rwIncl, horStabIncl, thrust;
		
		if(pos.y < 3){
			if(vel.y > 0){
				lwIncl = -10;
				rwIncl = -10;
			}
			else if(vel.y < -1.0){
				lwIncl = 10;
				rwIncl = 10;
			}
			else{
				lwIncl = 0;
				rwIncl = 0;				
			}
			thrust = 0;
		}
		else{
			if(vel.y > -1.5){
				lwIncl = -10;
				rwIncl = -10;
			}
			else if(vel.y < -1.7){
				lwIncl = 10;
				rwIncl = 10;
			}
			else{
				lwIncl = 0;
				rwIncl = 0;
			}
			if(vel.z < -40){
				thrust = 0;
			}
			else{
				thrust = this.maxThrust;
			}
		}
		
		horStabIncl = -0.5f*(this.climbAngle + input.getPitch());		
		horStabIncl = horStabIncl > 12 ? 12: horStabIncl;
		
		return Utils.buildOutputs(FloatMath.toRadians(lwIncl), FloatMath.toRadians(rwIncl), 0, FloatMath.toRadians(horStabIncl), thrust, 0, 0, 0);
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
		return "Landing pilot";
	}

}
