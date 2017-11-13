package world;

import engine.IWorldRules;
import entities.WorldObject;

public class WorldBuilder extends World implements IWorldRules {
		
	public WorldBuilder(int tSM, boolean wantPhysicsEngine, boolean wantPlanner, WorldObject[] worldObjects) {
		super(tSM, wantPhysicsEngine, wantPlanner);
		
		this.worldObjects = worldObjects;
	}

	@Override
	public void setup() {
		
	}

	@Override
	public String getDescription() {
		return "Internal hook for GUI world creation";
	}

}
