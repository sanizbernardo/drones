package testbed.world.premade;

import org.joml.Vector3f;

import autopilot.Pilot;
import interfaces.AutopilotConfig;
import testbed.entities.WorldObject;
import testbed.entities.airport.Airport;
import testbed.entities.ground.Ground;
import testbed.world.World;
import utils.Cubes;
import utils.FloatMath;
import utils.Utils;

public class TakeOffWorld extends World {
	
	public TakeOffWorld() {
		super(1, true, 1);
	}
	
	@Override
	public void setup() {
		AutopilotConfig config = Utils.createDefaultConfig("drone1");
		
		this.airports = new Airport[] {new Airport(20, 200, new Vector3f(0, 0, 0), 0)}; 
				
		addDrone(config, new Vector3f(0, -config.getWheelY() + config.getTyreRadius(), 0), new Vector3f(0,0,0), FloatMath.toRadians(90));
		
		this.planner = new Pilot(new int[] {Pilot.WAIT_PATH, Pilot.TAKING_OFF});
		
		this.worldObjects = new WorldObject[] {new WorldObject(Cubes.getBlueCube().getMesh())};
		this.worldObjects[0].setPosition(0, 10, -10);
		
		this.ground = new Ground(50);
	}

	@Override
	public String getDescription() {
		return "World to test the pilot responsible for taking off";
	}

}
