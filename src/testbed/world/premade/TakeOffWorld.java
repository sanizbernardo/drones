package testbed.world.premade;

import org.joml.Vector3f;

import autopilot.Pilot;
import testbed.entities.WorldObject;
import testbed.entities.ground.Ground;
import testbed.world.World;
import utils.Cubes;

public class TakeOffWorld extends World {
	
	public TakeOffWorld() {
		super(1, true, 1, 20, 200);
	}
	
	@Override
	public void setupAirports() {		
		addAirport(new Vector3f(0, 0, 0), 0); 
	}

	@Override
	public String getDescription() {
		return "World to test the pilot responsible for taking off";
	}

	@Override
	public void setupDrones() {
		addDrone("drone1", 0, 0, 0);
		
		this.planner = new Pilot(new int[] {Pilot.WAIT_PATH, Pilot.TAKING_OFF});
	}

	@Override
	public void setupWorld() {
		this.worldObjects = new WorldObject[] {new WorldObject(Cubes.getBlueCube().getMesh())};
		this.worldObjects[0].setPosition(0, 10, -10);
		
		this.ground = new Ground(50);		
	}

}
