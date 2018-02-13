package world;

import engine.IWorldRules;
import entities.WorldObject;
import physics.Motion;
import utils.Cubes;
import utils.Utils;


/**
 * Place where all the GameItem are to be placed in
 */
public class StopWorld extends World implements IWorldRules {

    public StopWorld() {
    	super(1, true);
    }

    @Override
    public void setup() {
    	config = Utils.createDefaultConfig();
    	  
    	physics.init(config, 20f);
    	
    	planner = new Motion();

        worldObjects = new WorldObject[1];
        worldObjects[0] = new WorldObject(Cubes.getCubes()[0].getMesh());
        worldObjects[0].setPosition(0f,0f,-10f);

        float thrust = 30f;
        physics.updateDrone(Utils.buildOutputs(0 ,0, 0, 0, thrust,-1,-1,-1));
    }

	@Override
	public String getDescription() {
		return "This world just generates a cube 10m in straight front of the drone, and lets the drone fly to it." +
				" The purpose of this world is demonstrating the ability to exit the simulation based on the distance to the cube.";
	}

}
