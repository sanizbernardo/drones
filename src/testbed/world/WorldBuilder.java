package testbed.world;

import java.util.Map;

import org.joml.Vector3f;

import interfaces.AutopilotConfig;
import testbed.engine.IWorldRules;
import testbed.entities.WorldObject;
import testbed.graphics.meshes.cube.*;

public class WorldBuilder extends World implements IWorldRules {
		
	private Map<Vector3f, BufferedCube> cubes;
	
	public WorldBuilder(int tSM, boolean wantPhysicsEngine, boolean wantPlanner, Map<Vector3f, BufferedCube> cubes) {
		super(tSM, wantPhysicsEngine, 1);
		this.cubes = cubes;
	}

	@Override
	public void setupAutopilotModule() {
//		if (wantPlanner) planner = new Pilot(new int[] {});
	}
	
	@Override
	public void setupAirports() {
		
	}

	@Override
	public void setupDrones() {
		
	}

	@Override
	public void setupWorld() {
		
	}
	
	@Override
	public String getDescription() {
		return "Internal hook for GUI world creation";
	}
	
	@SuppressWarnings("deprecation")
	public void setupDrone(AutopilotConfig config, Vector3f startPos, Vector3f startVel) {		
        addDrone(config, startPos, startVel, 0);
	}

}
