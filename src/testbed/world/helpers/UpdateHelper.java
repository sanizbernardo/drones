package testbed.world.helpers;


import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import interfaces.AutopilotModule;
import interfaces.AutopilotOutputs;
import testbed.Physics;
import testbed.entities.packages.PackageGenerator;
import testbed.entities.packages.Package;
import testbed.gui.TestbedGui;

import org.joml.Vector2f;
import org.joml.Vector3f;

import utils.Constants;
import utils.FloatMath;
import utils.PhysicsException;
import utils.IO.MouseInput;
import utils.Utils;

import javax.swing.JOptionPane;

public class UpdateHelper {

	/**
	 * All round attributes
	 */
	private int TIME_SLOWDOWN_MULTIPLIER;

	/**
	 * Camera update cycle
	 */
	private CameraHelper cameraHelper;
	private int followDrone;

	/**
	 * Autopilot update
	 */
	private AutopilotModule autopilotModule;

	/**
	 * TestbedGUI update
	 */
	private TestbedGui testbedGui;
	
	/**
	 * The time passed since start of the simulation
	 */
	private float time;
	
	/**
	 * Drone update
	 */
	private DroneHelper droneHelper;
	
	/**
	 * Package generation
	 */
	private PackageGenerator generator;
	private List<Package> packages;
	
	public UpdateHelper(DroneHelper droneHelper, int TIME_SLOWDOWN_MULTIPLIER, CameraHelper cameraHelper,
						AutopilotModule module, TestbedGui testbedGui, PackageGenerator generator) {
		this.TIME_SLOWDOWN_MULTIPLIER = TIME_SLOWDOWN_MULTIPLIER;
        this.cameraHelper = cameraHelper;
        this.followDrone = 0;
        this.autopilotModule = module;
        this.testbedGui = testbedGui;
        this.time = 0;
        this.droneHelper = droneHelper;
        this.generator = generator;
        this.packages = new ArrayList<>();
    }
	
	public int getFollowDrone() {
		return this.followDrone;
	}
	
	public void nextFollowDrone() {
		boolean found = false;
		for (Entry<String, Integer> a : droneHelper.droneIds.entrySet()) {
			if(found) {
				this.followDrone = a.getValue();
				return;
			}
			if(a.getValue() == followDrone) found = true;
		}
		
		this.followDrone = droneHelper.droneIds.entrySet().iterator().next().getValue();
		
	}

	/**
	 * This function will cycle through all the to update variables
	 * 
	 * @param interval
	 *            The passed time (delta time)
	 * @param mouseInput
	 *            This is an artifact of how we set up the update classes at the
	 *            start
	 */
	public void updateCycle(float interval, MouseInput mouseInput) {
		this.time += interval / TIME_SLOWDOWN_MULTIPLIER;

		droneHelper.update(interval/TIME_SLOWDOWN_MULTIPLIER, this);
		
		if (droneHelper.droneIds.isEmpty()) return;
			
		updatePackages();
		
		Vector3f newDronePos = droneHelper.getDronePhysics(followDrone).getPosition();
		
		updateCameraPositions(mouseInput, newDronePos, followDrone);
		
		updateModule();

		testbedGui.update(droneHelper.getDronePhysics(followDrone).getVelocity(), newDronePos,
						  droneHelper.getDronePhysics(followDrone).getHeading(),
						  droneHelper.getDronePhysics(followDrone).getPitch(),
						  droneHelper.getDronePhysics(followDrone).getRoll());
		
//		testbedGui.setDrone(newDronePos, droneHelper.getDronePhysics(followDrone).getHeading());
//		testbedGui.setCubes(worldObjects);
	}




	private void updateCameraPositions(MouseInput mouseInput, Vector3f newDronePos, int followDrone) {
		// Update camera based on mouse
		cameraHelper.freeCamera.movePosition(cameraHelper.getCameraInc().x * Constants.CAMERA_POS_STEP,
				cameraHelper.getCameraInc().y * Constants.CAMERA_POS_STEP,
				cameraHelper.getCameraInc().z * Constants.CAMERA_POS_STEP);
		if (mouseInput.isRightButtonPressed()) {
			Vector2f rotVec = mouseInput.getDisplVec();
			cameraHelper.freeCamera.moveRotation(FloatMath.toRadians(rotVec.x * Constants.MOUSE_SENSITIVITY),
					FloatMath.toRadians(rotVec.y * Constants.MOUSE_SENSITIVITY), 0);
		}
		
		
		Physics physics = droneHelper.getDronePhysics(followDrone);

		cameraHelper.droneCamera.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);
		cameraHelper.droneCamera.setRotation(-physics.getPitch(),-physics.getHeading(),-physics.getRoll());

		float offset = 17.5f;
		cameraHelper.chaseCamera.setPosition(newDronePos.x + offset * (float) Math.sin(physics.getHeading()),
				newDronePos.y, newDronePos.z + offset * (float) Math.cos(physics.getHeading()));
		cameraHelper.chaseCamera.setRotation(0, -physics.getHeading(), 0);
		
		cameraHelper.updateTopCam(newDronePos);
		cameraHelper.updateRightCam(newDronePos);
	}


	private void updateModule() {
		if (autopilotModule == null)
			return;
		
		for (int droneId: droneHelper.droneIds.values()) {
			Physics physics = droneHelper.getDronePhysics(droneId);
			autopilotModule.startTimeHasPassed(droneId, Utils.buildInputs(null, physics.getPosition(),
												physics.getHeading(), physics.getPitch(), physics.getRoll(), this.time));
		}
		
		for (int droneId: droneHelper.droneIds.values()) {
			AutopilotOutputs output = autopilotModule.completeTimeHasPassed(droneId);
			
			try {
				droneHelper.getDronePhysics(droneId).updateDrone(output);
			} catch (PhysicsException e) {
				JOptionPane.showMessageDialog(testbedGui, "An illegal force was entered for drone " + 
						droneHelper.getDroneConfig(droneId).getDroneID() + ": " + e.getMessage(),
						"Physics Exception", JOptionPane.ERROR_MESSAGE);
				droneHelper.removeDrone(droneId, this);
			}
		}	
	}
	
	private void updatePackages() {
		int[] newDetails = generator.generatePackage(this.time);
		if (newDetails != null) {
			Package newPackage = new Package(newDetails);
			packages.add(newPackage);
			
			autopilotModule.deliverPackage(newPackage.getFromAirport(), newPackage.getFromGate(),
											newPackage.getDestAirport(), newPackage.getDestGate());
		}
		
		
		
		
	}
}
