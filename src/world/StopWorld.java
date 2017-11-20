package world;

import org.joml.Vector3f;

import engine.IWorldRules;
import entities.WorldObject;
import physics.Drone;


/**
 * Place where all the GameItem are to be placed in
 */
public class StopWorld extends World implements IWorldRules {

    public StopWorld() {
    	super(20, true, true);
    	
    	this.config = createConfig();
    }

    /**
     * Is called in the abstract class
     */
    @Override
    public void setup() {
        /* Init the objects and set them as you like */
        int AMOUNT_OF_CUBES = 1;
        worldObjects = new WorldObject[AMOUNT_OF_CUBES];

        /* Do something with your new batch of objects */
        worldObjects[0] = new WorldObject(getCubeMeshes()[0].getMesh());
        worldObjects[0].setPosition(0f,0f,-10f);

        /* Give your drone some values */
        drone = new Drone(config);
        
        drone.setVelocity(new Vector3f(0,0,-20));
        drone.setThrust(30);
    }

	@Override
	public String getDescription() {
		return "This world just generates a cube 10m in straight front of the drone, and lets the drone fly to it." +
				" The purpose of this world is demonstrating the ability to exit the simulation based on the distance to the cube.";
	}

}
