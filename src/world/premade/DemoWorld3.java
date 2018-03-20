package world.premade;

import org.joml.Vector3f;

import entities.WorldObject;
import entities.ground.Ground;
import entities.tarmac.Tarmac;
import pilot.Pilot;
import utils.Cubes;
import utils.Utils;
import world.World;

public class DemoWorld3 extends World {

	public DemoWorld3() {
		super(1, true);
	}
	
	@Override
	public void setup() {
		this.config = Utils.createDefaultConfig();
		
		addDrone(config, new Vector3f(), new Vector3f());
		
		this.planner = new Pilot(new int[] {Pilot.WAIT_PATH, Pilot.TAKING_OFF, Pilot.FLYING, Pilot.TAXIING});
		
		this.worldObjects = new WorldObject[] {new WorldObject(Cubes.getBlueCube().getMesh()),
											   new WorldObject(Cubes.getGreenCube().getMesh()),
											   new WorldObject(Cubes.getYellowCube().getMesh()),
											   new WorldObject(Cubes.getRedCube().getMesh()),
											   new WorldObject(Cubes.getCyanCube().getMesh())};
		
		// TODO: realistische posities
		
		this.worldObjects[0].setPosition(new Vector3f(1, 2, 3));
		this.worldObjects[1].setPosition(new Vector3f(4, 5, 6));
		this.worldObjects[2].setPosition(new Vector3f(7, 8, 9));
		this.worldObjects[3].setPosition(new Vector3f(0, 1, 2));
		this.worldObjects[4].setPosition(new Vector3f(3, 4, 5));
		
		this.ground = new Ground(50);
		this.tarmac = new Tarmac(new Vector3f(0,0,10), 30f, 300f, 0f);
	}

	@Override
	public String getDescription() {
		return "World for demonstrating the third task in the "
				+ "first demo in the second semester.";
	}
}
