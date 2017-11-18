package world;

import org.joml.Vector3f;

import datatypes.AutopilotConfig;
import engine.IWorldRules;
import entities.WorldObject;

public class WorldBuilder extends World implements IWorldRules {
		
	public WorldBuilder(int tSM, boolean wantPhysicsEngine, boolean wantPlanner, WorldObject[] worldObjects) {
		super(tSM, wantPhysicsEngine, wantPlanner);
		
		this.config = config;
		this.worldObjects = worldObjects;
	}

	@Override
	public void setup() {
		
	}

	@Override
	public String getDescription() {
		return "Internal hook for GUI world creation";
	}
	
	public void setupConfig(AutopilotConfig config) {
		this.config = config;
	}
	
	public void setupDrone(Vector3f startPos, Vector3f startVel, Vector3f startOrientation) {
		this.drone.setPosition(startPos);
		this.drone.setVelocity(startVel);
		this.drone.setOrientation(startOrientation);
	}

}
