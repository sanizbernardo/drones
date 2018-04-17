package testbed.world.premade;

import testbed.entities.WorldObject;
import testbed.entities.ground.Ground;
import testbed.world.World;
import utils.FloatMath;
import org.joml.Vector3f;

public class AirportSetupWorld extends World {

    public AirportSetupWorld() {
        super(1, true, 4, 25, 250);
    }

    @Override
    public void setupAutopilotModule() {
    	
    }
    
    @Override
    public void setupAirports() {
    	addAirport(new Vector3f(0, 0, 0), FloatMath.toRadians(0));
    	
    	addAirport(new Vector3f(450, 0, -450), FloatMath.toRadians(45));

    }
    
	@Override
	public void setupDrones() {
        addDrone("drone1", 0, 0, 0);
        addDrone("drone2", 0, 1, 1);
        addDrone("drone3", 1, 0, 0);
        addDrone("drone4", 1, 1, 1);
       
	}

	@Override
	public void setupWorld() {
        worldObjects = new WorldObject[0]; 

        this.ground = new Ground(50);		
	}
	
    @Override
    public String getDescription() {
        return "A test for the airport setup";
    }
}
