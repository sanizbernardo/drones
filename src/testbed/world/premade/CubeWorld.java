package testbed.world.premade;


import testbed.entities.WorldObject;
import testbed.world.World;
import utils.Cubes;
import utils.PhysicsException;
import utils.Utils;

import java.util.Random;

import org.joml.Vector3f;

import interfaces.AutopilotConfig;

public class CubeWorld extends World {

    public CubeWorld() {
        super(1, false, 1);
    }
	@Override
	public void setupAirports() { }

	@SuppressWarnings("deprecation")
	@Override
    public void setupDrones() {
    	AutopilotConfig config = Utils.createDefaultConfig("drone1");
  
    	addDrone(config, new Vector3f(0, 100, 0), new Vector3f(0, 40, 0), 0);
    	
        float leftWingInc = (float)Math.toRadians(5f);
    	
    	try {
        	droneHelper.getDronePhysics(0).updateDrone(Utils.buildOutputs(leftWingInc,0, 0, 0, 0, 0, 0, 0));
		} catch (PhysicsException e) {}
    }
    
	@Override
	public void setupWorld() {
		planner = null;
    	
        Random rand = new Random();

        worldObjects = new WorldObject[2000];

        for(int i = 0; i < worldObjects.length; i++) {
            WorldObject cube = new WorldObject(Cubes.getCubes()[rand.nextInt(Cubes.getCubes().length)].getMesh());
            cube.setScale(0.5f);
            int x = rand.nextInt(100)-50,
            		y = rand.nextInt(100)+50,
            		z = rand.nextInt(100)-50;

            cube.setPosition(x, y, z);
            worldObjects[i] = cube;
        }
	}

	@Override
	public String getDescription() {
		return "Generates a world filled with 2000 cubes, randomly generated from -50 to 50 on all axes." +
				" The drone has no autopilot attached, and is set up to start spinning around after a few seconds" +
				"<br> This world is made for testing rendering";
	}

}
