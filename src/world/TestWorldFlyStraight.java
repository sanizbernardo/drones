package world;

import engine.IWorldRules;
import entities.WorldObject;
import physics.Motion;
import utils.Cubes;
import utils.Utils;


/**
 * Place where all the GameItem are to be placed in
 */
public class TestWorldFlyStraight extends World implements IWorldRules {

    public TestWorldFlyStraight() {
        super(10, true);
    }

    /**
     * Is called in the abstract class
     */
    @Override
    public void setup() {
    	config = Utils.createDefaultConfig();
    	  
    	physics.init(config, 20);

    	planner = new Motion();
    	
        worldObjects = new WorldObject[1];
        worldObjects[0] = new WorldObject(Cubes.getCubes()[0].getMesh());
        worldObjects[0].setPosition(0f,0f,-10f);


    }

	@Override
	public String getDescription() {
		return "Demonstrates the ability to fly straight and horizonatally of the autopilot. The drone starts with an initial velocity,"
				+ " flying towards a cube located 100m infront of it.";
	}

}
