package world;

import java.util.Random;

import org.joml.Vector3f;

import engine.IWorldRules;
import entities.WorldObject;
import physics.Drone;

public class OrthoTestWorld extends World implements IWorldRules{

	public OrthoTestWorld() {
		super(2, true, false);
		
    	this.config = createConfig();
	}

	@Override
	public void setup() {
        worldObjects = new WorldObject[100];

        Random rand = new Random();
        
        for(int i = 0; i < worldObjects.length; i++) {
            WorldObject cube = new WorldObject(getCubeMeshes()[rand.nextInt(getCubeMeshes().length)].getMesh());
            cube.setScale(0.5f);
            int x1 = rand.nextInt(100)-50,
            		y = 0,
            		z = rand.nextInt(100)-50;

            cube.setPosition(x1, y, z);
            worldObjects[i] = cube;
        }
        
        drone = new Drone(config);
        
        drone.setVelocity(new Vector3f(0,0,-15));
        drone.setLeftWingInclination(1);
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Test for the Orthographic cameras";
	}

}
