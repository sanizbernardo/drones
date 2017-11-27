package world;

import java.util.Map;

import org.joml.Vector3f;

import engine.IWorldRules;
import entities.WorldObject;
import entities.meshes.cube.*;
import interfaces.AutopilotConfig;
import physics.Drone;

public class WorldBuilder extends World implements IWorldRules {
		
	private Map<Vector3f, BufferedCube> cubes;
	
	public WorldBuilder(int tSM, boolean wantPhysicsEngine, boolean wantPlanner, Map<Vector3f, BufferedCube> cubes) {
		super(tSM, wantPhysicsEngine, wantPlanner);
		this.cubes = cubes;
	}

	@Override
	public void setup() {
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
	
	public void setupDrone(AutopilotConfig config, Vector3f startPos, Vector3f startVel, Vector3f startOrientation) {
        this.config = config;
		
		this.drone = new Drone(config);
		this.drone.setPosition(startPos);
		this.drone.setVelocity(startVel);
	}

}
