package world.premade;

import java.util.Arrays;

import org.joml.Vector3f;

import entities.WorldObject;
import entities.ground.Ground;
import entities.tarmac.Tarmac;
import pilot.Pilot;
import utils.Cubes;
import utils.FloatMath;
import utils.Utils;
import world.World;

public class DemoWorld2 extends World {

	public DemoWorld2() {
		super(1, true);
	}
	
	@Override
	public void setup() {
		this.config = Utils.createDefaultConfig();
		
		addDrone(config, new Vector3f(0, -config.getWheelY() + config.getTyreRadius(), 0), new Vector3f(0,0,0), FloatMath.toRadians(0));
		
		this.planner = new Pilot(new int[] {Pilot.WAIT_PATH, Pilot.TAKING_OFF, Pilot.FLYING, Pilot.LANDING});
		
		this.worldObjects = new WorldObject[] {new WorldObject(Cubes.getBlueCube().getMesh()),
											   new WorldObject(Cubes.getGreenCube().getMesh()),
											   new WorldObject(Cubes.getYellowCube().getMesh()),
											   new WorldObject(Cubes.getRedCube().getMesh()),
											   new WorldObject(Cubes.getCyanCube().getMesh())};
		
		// TODO: realistische posities
		
		this.worldObjects[0].setPosition(new Vector3f(100, 200, 300));
		this.worldObjects[1].setPosition(new Vector3f(400, 500, 600));
		this.worldObjects[2].setPosition(new Vector3f(700, 800, 900));
		this.worldObjects[3].setPosition(new Vector3f(000, 100, 200));
		this.worldObjects[4].setPosition(new Vector3f(300, 400, 500));
		
		Arrays.asList(worldObjects).stream().forEach(c -> c.setScale(5));
		
		this.ground = new Ground(50);
		this.tarmac = new Tarmac(new Vector3f(0,0,10), 30f, 300f, 0f);
	}

	@Override
	public String getDescription() {
		return "World for demonstrating the second task in the "
				+ "first demo in the second semester.";
	}
}
