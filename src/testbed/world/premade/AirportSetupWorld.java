package testbed.world.premade;

import testbed.entities.WorldObject;
import testbed.entities.ground.Ground;
import testbed.world.World;
import utils.FloatMath;
import utils.PhysicsException;
import utils.Utils;

import org.joml.Vector3f;

public class AirportSetupWorld extends World {

    public AirportSetupWorld() {
        super(1, true, 20, 25, 250);
    }

    @Override
    public void setupAutopilotModule() {
    	
    }
    
    @Override
    public void setupAirports() {
    	addAirport(new Vector3f(100, 0, 0), FloatMath.toRadians(0));
    	addAirport(new Vector3f(200, 0, 0), FloatMath.toRadians(0));
    	addAirport(new Vector3f(300, 0, 0), FloatMath.toRadians(0));
    	addAirport(new Vector3f(400, 0, 0), FloatMath.toRadians(0));
    	addAirport(new Vector3f(500, 0, 0), FloatMath.toRadians(0));
    	addAirport(new Vector3f(600, 0, 0), FloatMath.toRadians(0));
    	addAirport(new Vector3f(700, 0, 0), FloatMath.toRadians(0));
    	addAirport(new Vector3f(800, 0, 0), FloatMath.toRadians(0));
    	addAirport(new Vector3f(900, 0, 0), FloatMath.toRadians(0));
    	addAirport(new Vector3f(1000, 0, 0), FloatMath.toRadians(0));
    }
    
	@Override
	public void setupDrones() {
        addDrone("drone1", 0, 0, 0);
        addDrone("drone2", 0, 1, 0);
        addDrone("drone3", 1, 0, 0);
        addDrone("drone4", 1, 1, 0);
        addDrone("drone5", 2, 0, 0);
        addDrone("drone6", 2, 1, 0);
        addDrone("drone7", 3, 0, 0);
        addDrone("drone8", 3, 1, 0);
        addDrone("drone9", 4, 0, 0);
        addDrone("drone10", 4, 1, 0);
        addDrone("drone1a", 5, 0, 0);
        addDrone("drone2a", 5, 1, 0);
        addDrone("drone3a", 6, 0, 0);
        addDrone("drone4a", 6, 1, 0);
        addDrone("drone5a", 7, 0, 0);
        addDrone("drone6a", 7, 1, 0);
        addDrone("drone7a", 8, 0, 0);
        addDrone("drone8a", 8, 1, 0);
        addDrone("drone9a", 9, 0, 0);
        addDrone("drone10a", 9, 1, 0);
	}

	@Override
	public void setupWorld() {
		try {
			droneHelper.getDronePhysics(0).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(1).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(2).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(3).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(4).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(5).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(6).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(7).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(8).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(9).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(10).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(11).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(12).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(13).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(14).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(15).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(16).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(17).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(18).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			droneHelper.getDronePhysics(19).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));

		} catch (PhysicsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        worldObjects = new WorldObject[0]; 

        this.ground = new Ground(50);		
	}
	
    @Override
    public String getDescription() {
        return "A test for the airport setup";
    }
}
