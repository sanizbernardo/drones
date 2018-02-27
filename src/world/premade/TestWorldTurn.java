package world.premade;


import engine.IWorldRules;
import entities.WorldObject;
import pilot.Pilot;
import utils.Cubes;
import utils.Utils;
import world.World;

import java.util.Random;

import org.joml.Vector3f;

/**
 * Place where all the GameItem are to be placed in
 */
public class TestWorldTurn extends World implements IWorldRules {

    public TestWorldTurn() {
        super(1, true);
    }

    @Override
    public void setup() {
        config = Utils.createDefaultConfig();

        physics.init(config,  new Vector3f(0,100,0), 50);

        planner = new Pilot();
        
        worldObjects = new WorldObject[1];

    }

    @Override
    public String getDescription() {
        return "World to get the drone to turn";
    }

}
