package world;

import java.util.Map;

import meshes.cube.*;

import org.joml.Vector3f;

import engine.IWorldRules;
import entities.WorldObject;
import interfaces.AutopilotConfig;
import physics.Motion;

public class WorldBuilder extends World implements IWorldRules {
		
	private Map<Vector3f, BufferedCube> cubes;
	private boolean wantPlanner;
	
	public WorldBuilder(int tSM, boolean wantPhysicsEngine, boolean wantPlanner, Map<Vector3f, BufferedCube> cubes) {
		super(tSM, wantPhysicsEngine);
		this.cubes = cubes;
		this.wantPlanner = wantPlanner;
	}

	@Override
	public void setup() {
		
		//TODO: provide correct planner
		if (wantPlanner) planner = new Motion();
		
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
	
	public void setupDrone(AutopilotConfig config, Vector3f startPos, float startVel, Vector3f startOrientation) {
        this.config = config;
		
        addDrone(config, startPos, new Vector3f(startVel));
	}

}
