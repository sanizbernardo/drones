package testbed.world.premade;

import org.joml.Vector3f;

import autopilot.Pilot;
import testbed.entities.WorldObject;
import testbed.entities.ground.Ground;
import testbed.world.World;

public class TaxiWorld extends World {

    public TaxiWorld() {
    	super(1, true, 1, 20, 200);
    }

    @Override
    public void setupAirports() {	
		addAirport(new Vector3f(0, 0, 0), 0); 
    }

    @Override
    public String getDescription() {
        return "World to test the pilot responsible for taxiing";
    }

	@Override
	public void setupDrones() {		
        addDrone("drone1", 0, 0, 0);

        this.planner = new Pilot(new int[] {Pilot.TAXIING});
	}

	@Override
	public void setupWorld() {
		this.worldObjects = new WorldObject[] {};

        this.ground = new Ground(50);		
	}

}
