package pilot;

import org.joml.Vector3f;

import com.stormbots.MiniPID;

import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import utils.FloatMath;
import utils.Utils;

public class LandingPilot extends PilotPart {

	private float maxThrust;
	private float rMax;
	private final float dropAngle = FloatMath.toRadians(10);
	private float time;
	private MiniPID pitchPID;
	
	public LandingPilot() {
		
		this.time = 0;
		
		this.pitchPID = new MiniPID(1.1, 0, 0.3);
		this.pitchPID.setSetpoint(dropAngle);
		this.pitchPID.setOutputLimits(FloatMath.toRadians(10));
	}
	
	@Override
	public void initialize(AutopilotConfig config) {
		this.maxThrust = config.getMaxThrust();
		this.rMax = config.getRMax();
	}

	private Vector3f oldPos = new Vector3f(0, 0, 0);

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs input) {
		Vector3f pos = new Vector3f(input.getX(), input.getY(), input.getZ());
		
		float dt = input.getElapsedTime() - this.time;
		this.time = input.getElapsedTime();
		
		Vector3f vel = pos.sub(this.oldPos, new Vector3f()).mul(1/dt);
		this.oldPos = pos;
		
		float lwIncl = 0, rwIncl = 0, horStabIncl = 0, thrust = 0;
		
		lwIncl = input.getPitch() < FloatMath.toRadians(15) ? 4 : 6;
		rwIncl = input.getPitch() < FloatMath.toRadians(15) ? 4 : 6;
		horStabIncl = (float) -pitchPID.getOutput(input.getPitch());		
		thrust = 0;	
		if(vel.y < -1 && pos.y > 2.5){
			System.out.println(vel.y);
			thrust = maxThrust;
		}
		return Utils.buildOutputs(FloatMath.toRadians(lwIncl), FloatMath.toRadians(rwIncl), 0, horStabIncl, thrust, rMax/2, rMax/2, rMax/2);
	}

	@Override
	public boolean ended() {
		return false;
	}

	@Override
	public void close() {	
	}

	@Override
	public String taskName() {
		return "Landing pilot";
	}

}
