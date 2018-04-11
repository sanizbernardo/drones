package testbed.world.premade;

import java.util.Arrays;

import org.joml.Vector3f;

import autopilot.Pilot;
import interfaces.AutopilotConfig;
import testbed.entities.WorldObject;
import testbed.entities.ground.Ground;
import testbed.world.World;
import utils.Cubes;
import utils.Utils;

public class DemoWorld2 extends World {

	public DemoWorld2() {
		super(1, true, 1, 20, 200);
	}
	
	@Override
	public void setupAirports() {	
		addAirport(new Vector3f(0, 0, 0), 0); 
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void setupDrones() {
		AutopilotConfig config = Utils.createDefaultConfig("drone1");

		addDrone(config, new Vector3f(0, -config.getWheelY() + config.getTyreRadius(), 0), new Vector3f(0,0,0), 0);
		
		this.planner = new Pilot(new int[] {Pilot.WAIT_PATH, Pilot.TAKING_OFF, Pilot.FLYING, Pilot.LANDING, Pilot.HANDBRAKE});
	}

	@Override
	public void setupWorld() {
		this.worldObjects = new WorldObject[] {new WorldObject(Cubes.getBlueCube().getMesh()),
											   new WorldObject(Cubes.getGreenCube().getMesh()),
											   new WorldObject(Cubes.getYellowCube().getMesh()),
											   new WorldObject(Cubes.getRedCube().getMesh()),
											   new WorldObject(Cubes.getCyanCube().getMesh())};
		
		this.worldObjects[0].setPosition(new Vector3f(-50, 100, -1200));
		this.worldObjects[1].setPosition(new Vector3f(-800, 150, -500));
		this.worldObjects[2].setPosition(new Vector3f(0,125,500));
		this.worldObjects[3].setPosition(new Vector3f(1400, 100, -200));
		this.worldObjects[4].setPosition(new Vector3f(100, 75, -300));
		
		Arrays.asList(worldObjects).stream().forEach(c -> c.setScale(5));
		
		this.ground = new Ground(50);		
	}
	
	@Override
	public String getDescription() {
		return "World for demonstrating the second task in the "
				+ "first demo in the second semester.";
	}
}
