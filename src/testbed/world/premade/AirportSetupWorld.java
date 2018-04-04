package testbed.world.premade;

import testbed.engine.IWorldRules;
import testbed.entities.WorldObject;
import testbed.entities.airport.Airport;
import testbed.entities.ground.Ground;
import testbed.world.World;
import utils.FloatMath;
import utils.Utils;

import org.joml.Vector3f;

import interfaces.AutopilotConfig;

public class AirportSetupWorld extends World implements IWorldRules {

    public AirportSetupWorld() {
        super(1, true, 1);
    }

    @Override
    public void setup() {
    	
    	this.airports = new Airport[] {new Airport(20, 200, new Vector3f(0, 0, 0), FloatMath.toRadians(-120))};
    	
    	AutopilotConfig config = Utils.createDefaultConfig("drone1");

        addDrone(config,  new Vector3f(0,-config.getWheelY()+config.getTyreRadius(),0), new Vector3f(0, 0, 0), FloatMath.toRadians(-120));

        planner = null;

        worldObjects = new WorldObject[0]; 

        this.ground = new Ground(50);
    }

    @Override
    public String getDescription() {
        return "A test for the airport setup";
    }
    
}
