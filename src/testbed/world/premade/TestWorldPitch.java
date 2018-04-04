package testbed.world.premade;

import testbed.engine.IWorldRules;
import testbed.entities.WorldObject;
import testbed.entities.airport.Airport;
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
        super(1, true, 1);
    }

    @Override
    public void setup() {
		AutopilotConfig config = Utils.createDefaultConfig("drone1");
		
		this.airports = new Airport[] {new Airport(20, 200, new Vector3f(0, 0, 0), 0)}; 
		
        addDrone(config, new Vector3f(0,100,0), new Vector3f(0,0,-40));

        planner = new Pilot(new int[] {Pilot.FLYING});
        
        worldObjects = new WorldObject[1];
        
        this.ground = new Ground(50);

    }

    @Override
    public String getDescription() {
        return "World to get the drone to turn";
    }

}
