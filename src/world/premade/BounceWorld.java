package world.premade;

import org.joml.Vector3f;

import pilot.Pilot;
import entities.WorldObject;
import entities.ground.Ground;
import pilot.Pilot;
import utils.FloatMath;
import utils.PhysicsException;
import utils.Utils;
import world.World;

public class BounceWorld extends World {
	
	public BounceWorld() {
		super(1, true);
	}
	
	@Override
	public void setup() {
		this.config = Utils.createDefaultConfig();
		
		addDrone(config, new Vector3f(0, -config.getWheelY() + config.getTyreRadius(), 0), new Vector3f(0,-1.8f,0));
		
		this.planner = new Pilot(new int[] {});
		
		this.worldObjects = new WorldObject[0];
		
		this.ground = new Ground(10);
		
		try {
			droneHelper.getDronePhysics(config.getDroneID()).updateDrone(Utils.buildOutputs(-FloatMath.PI/2, -FloatMath.PI/2, 0, -FloatMath.PI/2, 0, 0, 0, 0));
		} catch (PhysicsException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDescription() {
		return "Testing for maximum inpact velocity";
	}

}
