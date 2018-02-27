package world.helpers;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.joml.Vector3f;

import entities.WorldObject;
import entities.drone.DroneSkeleton;
import entities.trail.Trail;
import interfaces.AutopilotConfig;
import physics.Physics;
import utils.PhysicsException;

public class DroneHelper {

	private int nbDrones;
	private Map<String, Integer> droneIds;

	private WorldObject[][] drones;
	private Physics[] physics;
	private Trail[] trails;

	private final boolean wantPhysics;

	public DroneHelper(boolean wantPhysics, int nbDrones) {
		this.nbDrones = nbDrones;
		droneIds = new HashMap<>();

		drones = new WorldObject[nbDrones][];
		physics = new Physics[nbDrones];
		trails = new Trail[nbDrones];

		this.wantPhysics = wantPhysics;
	}

	public void addDrone(AutopilotConfig config, Vector3f startPos, Vector3f startVel) {
		int i = 0;
		while (droneIds.containsValue(i) && i < nbDrones) {
			i += 1;
		} 
		if (i ==nbDrones)
			throw new IllegalArgumentException();
		droneIds.put(config.getDroneID(), i);

		DroneSkeleton droneMesh = new DroneSkeleton(config);
		WorldObject left = new WorldObject(droneMesh.getLeft());
		WorldObject right = new WorldObject(droneMesh.getRight());
		WorldObject body = new WorldObject(droneMesh.getBody());

		WorldObject wheelFront = new WorldObject(droneMesh.getWheel());
		WorldObject wheelBackLeft = new WorldObject(droneMesh.getWheel());
		WorldObject wheelBackRight = new WorldObject(droneMesh.getWheel());

		WorldObject[] droneItems = new WorldObject[] { left, right, body, wheelFront, wheelBackLeft, wheelBackRight };

		drones[i] = droneItems;

		Physics physic = new Physics();
		physic.init(config, startPos, startVel);

		physics[i] = physic;
		
		trails[i] = new Trail();
	}

	public void removeDrone(String droneId) {
		int index = droneIds.remove(droneId);
		
		WorldObject[] droneItems = drones[index];

		for (WorldObject droneItem : droneItems) {
			droneItem.getMesh().cleanUp();
		}
		
		drones[index] = null;
		physics[index] = null;
		trails[index] = null;		
	}

	public void updatePhysics(float interval) {
		if (wantPhysics) {
			for (String droneId : physics.keySet()) {
				try {
					physics.get(droneId).update(interval);
				} catch (PhysicsException e) {
					JOptionPane.showMessageDialog(null,
							"A physics error occured for drone " + droneId + ": " + e.getMessage(), "Physics Exception",
							JOptionPane.ERROR_MESSAGE);
					removeDrone(droneId);
				}
			}
		}
	}

	public Physics getDronePhysics(String droneId) {
		return physics.get(droneId);
	}

	public void updateTrails() {

	}
}
