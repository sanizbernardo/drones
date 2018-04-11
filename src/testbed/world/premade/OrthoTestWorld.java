package testbed.world.premade;

import java.util.Random;

import org.joml.Vector3f;

import autopilot.Pilot;
import interfaces.AutopilotConfig;
import testbed.engine.IWorldRules;
import testbed.entities.WorldObject;
import testbed.world.World;
import utils.Cubes;
import utils.FloatMath;
import utils.PhysicsException;
import utils.Utils;

public class OrthoTestWorld extends World implements IWorldRules{

	public OrthoTestWorld() {
		super(1, true, 1);
	}

	@Override
	public void setupAirports() {
		addAirport(new Vector3f(0,0,0), 0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setupDrones() {
    	AutopilotConfig config = Utils.createDefaultConfig("drone1");
  	  
    	addDrone(config, new Vector3f(0, -config.getWheelY() + config.getTyreRadius(), 0), new Vector3f(0, 0, -10), 0);
    	
    	planner = new Pilot(new int[] {});

        float leftWingInc = FloatMath.toRadians(0f);
        try {
        	droneHelper.getDronePhysics(config.getDroneID()).updateDrone(Utils.buildOutputs(leftWingInc, 0,0,0,0,0,0,0));
		} catch (PhysicsException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void setupWorld() {
        worldObjects = new WorldObject[100];

        Random rand = new Random();
		
        for(int i = 0; i < worldObjects.length; i++) {
            WorldObject cube = new WorldObject(Cubes.getCubes()[rand.nextInt(Cubes.getCubes().length)].getMesh());
            cube.setScale(0.5f);
            int x1 = rand.nextInt(100)-50,
            		y = 0,
            		z = rand.nextInt(100)-50;

            cube.setPosition(x1, y, z);
            worldObjects[i] = cube;
        }		
	}	
	
	@Override
	public String getDescription() {
		return "Test for the Orthographic cameras";
	}



}
