package world.premade;

import engine.IWorldRules;
import entities.WorldObject;
import physics.Motion;
import utils.Cubes;
import utils.PhysicsException;
import utils.Utils;
import world.World;

/**
 * Place where all the GameItem are to be placed in
 */
public class TestWorld extends World implements IWorldRules {

    public TestWorld() {
        super(1, true);
    }

    @Override
    public void setup() {
    	config = Utils.createDefaultConfig();
    	  
    	physics.init(config, 12f);

    	planner = new Motion();

        worldObjects = new WorldObject[5];

        worldObjects[0] = new WorldObject(Cubes.getCubes()[0].getMesh());
        worldObjects[0].setPosition(0f, 10f, -50f);
        worldObjects[1] = new WorldObject(Cubes.getCubes()[1].getMesh());
        worldObjects[1].setPosition(0f, 4f, -100f);
        worldObjects[2] = new WorldObject(Cubes.getCubes()[2].getMesh());
        worldObjects[2].setPosition(0f, 0f, -150f);
        worldObjects[3] = new WorldObject(Cubes.getCubes()[3].getMesh());
        worldObjects[3].setPosition(0f, -2f, -200f);
        worldObjects[4] = new WorldObject(Cubes.getCubes()[4].getMesh());
        worldObjects[4].setPosition(0f, 8f, -250f);

        float thrust = 20f;
        try {
			physics.updateDrone(Utils.buildOutputs(0 ,0, 0, 0, thrust,0,0,0));
		} catch (PhysicsException e) {
			e.printStackTrace();
		}
    }

	@Override
	public String getDescription() {
		return "World made for the first demonstration, serves nearly no purpose anymore.";
	}
}