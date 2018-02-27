package world.premade;

import engine.IWorldRules;
import entities.WorldObject;
import pilot.Pilot;
import utils.Cubes;
import utils.PhysicsException;
import utils.Utils;
import world.World;


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
    	
    	planner = new Pilot();

        worldObjects = new WorldObject[1];
        worldObjects[0] = new WorldObject(Cubes.getCubes()[0].getMesh());
        worldObjects[0].setPosition(0f,0f,-10f);

        float thrust = 30f;
        try {
			physics.updateDrone(Utils.buildOutputs(0 ,0, 0, 0, thrust,0,0,0));
		} catch (PhysicsException e) {
			e.printStackTrace();
		}
    }

	@Override
	public String getDescription() {
		return "This world just generates a cube 10m in straight front of the drone, and lets the drone fly to it." +
				" The purpose of this world is demonstrating the ability to exit the simulation based on the distance to the cube.";
	}

}
