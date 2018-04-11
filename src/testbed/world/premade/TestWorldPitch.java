package testbed.world.premade;

import testbed.engine.IWorldRules;
import testbed.entities.WorldObject;
import testbed.entities.ground.Ground;
import testbed.world.World;
import utils.Utils;

import org.joml.Vector3f;

import autopilot.Pilot;
import interfaces.AutopilotConfig;

/**
 * Place where all the GameItem are to be placed in
 */
public class TestWorldPitch extends World implements IWorldRules {

    public TestWorldPitch() {
        super(1, true, 1, 20, 200);
    }

    @Override
    public void setupAirports() {

    }

	@SuppressWarnings("deprecation")
	@Override
	public void setupDrones() {
		AutopilotConfig config = Utils.createDefaultConfig("drone1");
		
        addDrone(config, new Vector3f(0,100,0), new Vector3f(0,0,-40), 0);
        
        planner = new Pilot(new int[] {Pilot.FLYING});
	}

	@Override
	public void setupWorld() {
        worldObjects = new WorldObject[0];
        
        this.ground = new Ground(50);		
	}
    
    @Override
    public String getDescription() {
        return "World to get the drone to turn";
    }

}
