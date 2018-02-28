package world.premade;

import java.util.Random;

import org.joml.Vector3f;

import engine.IWorldRules;
import entities.WorldObject;
import pilot.Pilot;
import utils.Cubes;
import utils.FloatMath;
import utils.PhysicsException;
import utils.Utils;
import world.World;

public class OrthoTestWorld extends World implements IWorldRules{

	public OrthoTestWorld() {
		super(1, true);
	}

	@Override
	public void setup() {
    	config = Utils.createDefaultConfig();
    	  
    	addDrone(config, new Vector3f(0,0,0), new Vector3f(0,0,-10));
    	planner = new Pilot();

        worldObjects = new WorldObject[100];

        Random rand = new Random();
        
        for(int i = 0; i < worldObjects.length; i++) {
            WorldObject cube = new WorldObject(Cubes.getCubes()[rand.nextInt(Cubes.getCubes().length)].getMesh());
            cube.setScale(0.5f);
            int x1 = rand.nextInt(100)-50,
            		y = 0,
            		z = rand.nextInt(100)-50;

            cube.setPosition(x1, y, z);
            worldObjects[i] = cube;
        }

        float leftWingInc = FloatMath.toRadians(0f);
        try {
        	droneHelper.getDronePhysics(config.getDroneID()).updateDrone(Utils.buildOutputs(leftWingInc, 0,0,0,0,0,0,0));
		} catch (PhysicsException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getDescription() {
		return "Test for the Orthographic cameras";
	}

}
