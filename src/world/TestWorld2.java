package world;

import org.joml.Vector3f;

import engine.IWorldRules;
import entities.WorldObject;
import physics.Motion;
import utils.FloatMath;
import utils.Utils;

/**
 * Place where all the GameItem are to be placed in
 */
public class TestWorld2 extends World implements IWorldRules {

    public TestWorld2() {
        super(1, true);
    }

    @Override
    public void setup() {
    	config = Utils.createDefaultConfig();
    	  
    	physics.init(config, new Vector3f(0, 0, 0), 12, 0, 0, FloatMath.toRadians(15));

    	planner = new Motion();
    	
    	worldObjects = new WorldObject[0];

        float thrust = 20f;
        physics.updateDrone(Utils.buildOutputs(0 ,0, 0, 0, thrust,-1,-1,-1));
    }

	@Override
	public String getDescription() {
		return "World made for the first demonstration, serves nearly no purpose anymore.";
	}
}