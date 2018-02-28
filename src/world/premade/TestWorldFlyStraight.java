package world.premade;

import org.joml.Vector3f;

import engine.IWorldRules;
import entities.WorldObject;
import pilot.Pilot;
import utils.Cubes;
import utils.Utils;
import world.World;

/**
 * Place where all the GameItem are to be placed in
 */
public class TestWorldFlyStraight extends World implements IWorldRules {

    public TestWorldFlyStraight() {
        super(1, true);
    }

    /**
     * Is called in the abstract class
     */
    @Override
    public void setup() {
    	config = Utils.createDefaultConfig();
    	  
    	addDrone(config, new Vector3f(0,100,0), new Vector3f(0,0,-10));

    	planner = new Pilot();
    	
        worldObjects = new WorldObject[1];
        worldObjects[0] = new WorldObject(Cubes.getCubes()[0].getMesh());
        worldObjects[0].setPosition(0f,0f,-400f);

    }

	@Override
	public String getDescription() {
		return "Demonstrates the ability to fly straight and horizonatally of the autopilot. The drone starts with an initial velocity,"
				+ " flying towards a cube located 100m infront of it.";
	}

}
