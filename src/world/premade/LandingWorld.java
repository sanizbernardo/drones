package world.premade;

import org.joml.Vector3f;


import entities.WorldObject;
import entities.ground.Ground;
import entities.tarmac.Tarmac;
import interfaces.Autopilot;
import physics.Physics;
import pilot.Pilot;
import utils.Cubes;
import utils.Utils;
import world.World;

public class LandingWorld extends World{

	public LandingWorld() {
		super(1,true);
	}

	@Override
	public void setup() {
		this.config = Utils.createDefaultConfig();
		

		addDrone(config, new Vector3f(0, 3, 0), new Vector3f(0,0,-40));
		
		this.planner = new Pilot(new int[] {Pilot.LANDING});
		
		this.worldObjects = new WorldObject[500];
		
		for (int i = 0; i < this.worldObjects.length; i++) {
			WorldObject cube = new WorldObject(Cubes.getPinkCube().getMesh());
			cube.setScale(1);
			cube.setPosition(0, -0.5f, i*-25);
			
			this.worldObjects[i] = cube;
		}
		
		this.ground = new Ground(50);
		this.tarmac = new Tarmac(new Vector3f(0,0,-200), 50f, 300f, 0f);
		
	}

	@Override
	public String getDescription() {
		return "World to test the pilot responsible for landing";
	}

}
