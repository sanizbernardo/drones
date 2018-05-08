package testbed.world.premade;

import org.joml.Vector3f;

import autopilot.airports.AirportManager;
import testbed.entities.ground.Ground;
import testbed.entities.packages.PackageGenerator;
import testbed.entities.packages.PackageGenerators;
import testbed.world.World;
import utils.FloatMath;

public class ChainingWorld extends World{

	public ChainingWorld() {
		super(1, true, 1);
	}

	@Override
	public void setupAutopilotModule() {
		this.autopilotModule = new AirportManager();
	}

	@Override
	public void setupAirports() {
		addAirport(new Vector3f(-200, 0, -11), 0);
		addAirport(new Vector3f(1015, 0, 1089), FloatMath.toRadians(45));
	}

	@Override
	public void setupDrones() {
		addDrone("drone1", 0, 0, 0);
	}

	@Override
	public void setupWorld() {
		this.ground = new Ground(50);
		
		this.generator = PackageGenerators.random(0.005f, 2);
	}

	@Override
	public String getDescription() {
		return "Testing the autopilot module";
	}
	
}
