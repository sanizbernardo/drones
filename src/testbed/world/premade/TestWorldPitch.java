package testbed.world.premade;

import testbed.engine.IWorldRules;
import testbed.entities.WorldObject;
import testbed.entities.airport.Tarmac;
import testbed.entities.ground.Ground;
import testbed.world.World;
import utils.Utils;

import org.joml.Vector3f;

import autopilot.Pilot;

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

        addDrone(config, new Vector3f(0,100,0), new Vector3f(0,0,-40));

        planner = new Pilot(new int[] {Pilot.FLYING});
        
        worldObjects = new WorldObject[1];
        
        this.ground = new Ground(50);
		this.tarmac = new Tarmac(new Vector3f(0,0,0), 50f, 300f, 0f);

    }

    @Override
    public String getDescription() {
        return "World to get the drone to turn";
    }

}
