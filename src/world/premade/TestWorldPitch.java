package world.premade;

import engine.IWorldRules;
import entities.WorldObject;
import pilot.Pilot;
import utils.Utils;
import world.World;

import org.joml.Vector3f;

/**
 * Place where all the GameItem are to be placed in
 */
public class TestWorldPitch extends World implements IWorldRules {

    public TestWorldPitch() {
        super(1, true);
    }

    @Override
    public void setup() {
        config = Utils.createDefaultConfig();

        addDrone(config, new Vector3f(0,100,0), new Vector3f(0,0,-50));

        planner = new Pilot(new int[] {Pilot.FLYING});
        
        worldObjects = new WorldObject[1];

    }

    @Override
    public String getDescription() {
        return "World to get the drone to turn";
    }

}
