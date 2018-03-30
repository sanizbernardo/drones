package testbed.world.premade;


import testbed.engine.IWorldRules;
import testbed.entities.WorldObject;
import testbed.world.World;
import utils.Cubes;
import utils.PhysicsException;
import utils.Utils;

import java.util.Random;

import org.joml.Vector3f;

/**
 * Place where all the GameItem are to be placed in
 */
public class CubeWorld extends World implements IWorldRules {

    public CubeWorld() {
        super(1, true);
    }

    @Override
    public void setup() {
    	config = Utils.createDefaultConfig();
  
    	addDrone(config, new Vector3f(0, 100, 0), new Vector3f(0, 0, 0));

    	planner = null;
    	
        Random rand = new Random();

        //World specifics
        worldObjects = new WorldObject[2000];

        for(int i = 0; i < worldObjects.length; i++) {
            WorldObject cube = new WorldObject(Cubes.getCubes()[rand.nextInt(Cubes.getCubes().length)].getMesh());
            cube.setScale(0.5f);
            int x1 = rand.nextInt(100)-50,
            		y = rand.nextInt(100)-50,
            		z = rand.nextInt(100)-50;

            cube.setPosition(x1, y, z);
            worldObjects[i] = cube;
        }

        float leftWingInc = (float)Math.toRadians(5f);
        float thrust = 0f;
        try {
        	droneHelper.getDronePhysics(config.getDroneID()).updateDrone(Utils.buildOutputs(leftWingInc,0, 0, 0, thrust, 0, 0, 0));
		} catch (PhysicsException e) {}

        
    }

	@Override
	public String getDescription() {
		return "Generates a world filled with 7000 cubes, randomly generated from -50 to 50 on all axes." +
				" The drone has no autopilot attached, and is set up to start spinning around after a few seconds" +
				"<br> This world is made for testing rendering";
	}

}
