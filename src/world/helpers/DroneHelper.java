package world.helpers;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.joml.Vector3f;

import entities.WorldObject;
import entities.drone.DroneSkeleton;
import entities.trail.Trail;
import interfaces.AutopilotConfig;
import physics.Physics;
import utils.Constants;
import utils.FloatMath;
import utils.PhysicsException;

public class DroneHelper {

	private int nbDrones;
	private Map<String, Integer> droneIds;

	private WorldObject[][] drones;
	private Physics[] physics;
	private Trail[] trails;
	private JFrame rootFrame;

	private final boolean wantPhysics;

	public DroneHelper(boolean wantPhysics, int nbDrones, JFrame rootFrame) {
		this.nbDrones = nbDrones;
		this.droneIds = new HashMap<>();

		this.drones = new WorldObject[nbDrones][];
		this.physics = new Physics[nbDrones];
		this.trails = new Trail[nbDrones];

		this.rootFrame = rootFrame;
		this.wantPhysics = wantPhysics;
	}

	public void addDrone(AutopilotConfig config, Vector3f startPos,
			Vector3f startVel, float startHeading) {
		int i = 0;
		while (droneIds.containsValue(i) && i < nbDrones) {
			i += 1;
		}
		if (i == nbDrones)
			throw new IllegalArgumentException("Max amount of drones reached");
		droneIds.put(config.getDroneID(), i);

		DroneSkeleton droneMesh = new DroneSkeleton(config);
		WorldObject left = new WorldObject(droneMesh.getLeft());
		WorldObject right = new WorldObject(droneMesh.getRight());
		WorldObject body = new WorldObject(droneMesh.getBody());

		WorldObject wheelFront = new WorldObject(droneMesh.getWheel());
		WorldObject wheelBackLeft = new WorldObject(droneMesh.getWheel());
		WorldObject wheelBackRight = new WorldObject(droneMesh.getWheel());

		WorldObject[] droneItems = new WorldObject[] { left, right, body,
				wheelFront, wheelBackLeft, wheelBackRight };

		drones[i] = droneItems;

		Physics physic = new Physics();
		physic.init(config, startPos, startVel, startHeading);

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

	public void update(float interval) {
		updatePhysics(interval);

		updateTrails();

		updateDroneItems();
	}

	private void updatePhysics(float interval) {
		if (wantPhysics) {
			for (String droneId : droneIds.keySet()) {
				try {
					getDronePhysics(droneId).update(interval);
				} catch (PhysicsException e) {
					JOptionPane.showMessageDialog(rootFrame,
							"A physics error occured for drone " + droneId
									+ ": " + e.getMessage(),
							"Physics Exception", JOptionPane.ERROR_MESSAGE);
					removeDrone(droneId);
				} catch (NullPointerException e) {
				}
			}
		}

		checkCollision();
	}

	private void checkCollision() {
		for (String droneId1 : droneIds.keySet()) {
			int i = droneIds.get(droneId1);
			for (String droneId2 : droneIds.keySet()) {
				int j = droneIds.get(droneId2);
				if (i < j)
					if (FloatMath.norm(physics[i].getPosition().sub(
							physics[j].getPosition())) <= Constants.COLLISION_RANGE) {
						JOptionPane.showMessageDialog(rootFrame, "Drone "
								+ droneId1 + " and drone " + droneId2
								+ " collided.", "Collision Exception",
								JOptionPane.ERROR_MESSAGE);
						removeDrone(droneId1);
						removeDrone(droneId2);
					}
			}
		}
	}

	private void updateTrails() {
		for (String droneId : droneIds.keySet()) {
			getDroneTrail(droneId).leaveTrail(
					getDronePhysics(droneId).getPosition());
		}
	}

	private void updateDroneItems() {
		for (String droneId : droneIds.keySet()) {
			Physics physics = getDronePhysics(droneId);
			Vector3f dronePos = physics.getPosition();
			// Update the position of each drone item
			for (WorldObject droneItem : drones[droneIds.get(droneId)]) {
				droneItem.setPosition(dronePos.x, dronePos.y, dronePos.z);
				droneItem.setRotation(-physics.getPitch(),
						-physics.getHeading(), -physics.getRoll());
			}

			translateWheels(droneId);
			rotateWings(droneId);
		}
	}

	private void rotateWings(String droneId) {
//		Physics physics = getDronePhysics(droneId);
//		Vector3f orientation = new Vector3f(-physics.getPitch(),-physics.getHeading(), -physics.getRoll());
//		Vector3f droneOrientation = FloatMath.transform(physics.getTransMat(), orientation);
//		Matrix3f rot = new Matrix3f().rotate(FloatMath.toRadians(45), droneOrientation);
//		Vector3f rotatedDroneOrientation = FloatMath.transform(rot, droneOrientation);
//		Vector3f r = FloatMath.transform(physics.getTransMatInv(), droneOrientation);
//		drones[droneIds.get(droneId)][Constants.DRONE_LEFT_WING].setRotation(r.x, r.y, r.z);
	}

	private void translateWheels(String droneId) {
		Physics physics = getDronePhysics(droneId);
		setWheel(droneId, Constants.DRONE_WHEEL_FRONT, 0, physics.getConfig()
				.getWheelY(), physics.getConfig().getFrontWheelZ());
		setWheel(droneId, Constants.DRONE_WHEEL_BACK_LEFT, -physics.getConfig()
				.getRearWheelX(), physics.getConfig().getWheelY(), physics
				.getConfig().getRearWheelZ());
		setWheel(droneId, Constants.DRONE_WHEEL_BACK_RIGHT, physics.getConfig()
				.getRearWheelX(), physics.getConfig().getWheelY(), physics
				.getConfig().getRearWheelZ());
	}

	private void setWheel(String droneId, int id, float x, float y, float z) {
		Vector3f wheel = drones[droneIds.get(droneId)][id].getPosition();
		Vector3f wheelT = FloatMath.transform(getDronePhysics(droneId)
				.getTransMat(), wheel);

		wheelT.add(new Vector3f(x, y, z));
		Physics physics = getDronePhysics(droneId);
		wheelT = FloatMath.transform(physics.getTransMatInv(), wheelT);
		drones[droneIds.get(droneId)][id].setPosition(wheelT.x, wheelT.y,
				wheelT.z);
	}

	public Physics getDronePhysics(String droneId) {
		return droneIds.containsKey(droneId) ? physics[droneIds.get(droneId)]
				: null;
	}

	public Trail getDroneTrail(String droneId) {
		return droneIds.containsKey(droneId) ? trails[droneIds.get(droneId)]
				: null;
	}

	public WorldObject[] getDroneItems(String droneId) {
		return droneIds.containsKey(droneId) ? drones[droneIds.get(droneId)]
				: null;
	}

	public int getNbDrones() {
		return droneIds.size();
	}

}
