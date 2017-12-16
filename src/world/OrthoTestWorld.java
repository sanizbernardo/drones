package world;

import java.util.Random;

import engine.IWorldRules;
import entities.WorldObject;
import physics.Motion;
import utils.Cubes;
import utils.FloatMath;
import utils.Utils;

public class OrthoTestWorld extends World implements IWorldRules{

	public OrthoTestWorld() {
		super(1, true);
	}

	@Override
	public void setup() {
    	config = Utils.createDefaultConfig();
    	  
    	physics.init(config, 10);
    	planner = new Motion();
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
        physics.updateDrone(Utils.buildOutputs(leftWingInc, 0,0,0,0));

	}

	@Override
	public String getDescription() {
		return "Test for the Orthographic cameras";
	}

}
