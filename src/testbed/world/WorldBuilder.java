package testbed.world;

import java.util.Map;

import org.joml.Vector3f;

import autopilot.Pilot;
import interfaces.AutopilotConfig;
import testbed.engine.IWorldRules;
import testbed.entities.WorldObject;
import testbed.graphics.meshes.cube.*;

public class WorldBuilder extends World implements IWorldRules {
		
	private Map<Vector3f, BufferedCube> cubes;
	private boolean wantPlanner;
	
	public WorldBuilder(int tSM, boolean wantPhysicsEngine, boolean wantPlanner, Map<Vector3f, BufferedCube> cubes) {
		super(tSM, wantPhysicsEngine, 1);
		this.cubes = cubes;
		this.wantPlanner = wantPlanner;
	}

	@Override
	public void setupAirports() {
		
	}

	@Override
	public void setupDrones() {
		//TODO: provide correct planner
		if (wantPlanner) planner = new Pilot(new int[] {});
	}

	@Override
	public void setupWorld() {
		this.worldObjects = new WorldObject[cubes.size()];	
		int i = 0;		
		for (Vector3f pos: cubes.keySet()) {
			Cube cube = cubes.get(pos).setup();
			this.worldObjects[i] = new WorldObject(cube.getMesh());
			this.worldObjects[i].setPosition(pos);
			i++;
		}
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
