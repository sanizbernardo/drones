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
	
    private Map<String, WorldObject[]> drones;
    private Map<String, Physics> physics;
    private Map<String, Trail> trails;
    
    private final boolean wantPhysics;
    
    public DroneHelper(boolean wantPhysics) {
    	drones = new HashMap<String,WorldObject[]>();
    	physics = new HashMap<String, Physics>();
    	trails = new HashMap<String, Trail>();
    	
    	this.wantPhysics = wantPhysics;
    }
    
	public void addDrone(AutopilotConfig config, Vector3f startPos, Vector3f startVel) {
        DroneSkeleton droneMesh = new DroneSkeleton(config);
        WorldObject left = new WorldObject(droneMesh.getLeft());
        WorldObject right = new WorldObject(droneMesh.getRight());
        WorldObject body = new WorldObject(droneMesh.getBody());

        WorldObject wheelFront = new WorldObject(droneMesh.getWheel());
        WorldObject wheelBackLeft = new WorldObject(droneMesh.getWheel());
        WorldObject wheelBackRight = new WorldObject(droneMesh.getWheel());
        
        WorldObject[] droneItems = new WorldObject[]{left, right, body, wheelFront, wheelBackLeft, wheelBackRight};
        
        drones.put(config.getDroneID(), droneItems);
        
        Physics physic = new Physics();
        physic.init(config, startPos, startVel);
        
        physics.put(config.getDroneID(), physic);
    }

	public void removeDrone(String droneId) {
		WorldObject[] droneItems = drones.remove(droneId);
		
		for (WorldObject droneItem: droneItems) {
			droneItem.getMesh().cleanUp();
		}		
	}
	
	public void updatePhysics(float interval) {
		if (wantPhysics) {
			for (String droneId: physics.keySet()) {
				try {
					physics.get(droneId).update(interval);
				} catch (PhysicsException e) {
					JOptionPane.showMessageDialog(null, "A physics error occured for drone " + droneId + 
							": " + e.getMessage(),"Physics Exception", JOptionPane.ERROR_MESSAGE);
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
