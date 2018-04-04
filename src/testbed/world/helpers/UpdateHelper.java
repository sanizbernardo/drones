package testbed.world.helpers;

import interfaces.Autopilot;
import interfaces.AutopilotOutputs;
import testbed.Physics;
import testbed.entities.WorldObject;
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
	 * World content update (cubes)
	 */
	private WorldObject[] worldObjects;

	/**
	 * Autopilot update
	 */
	private Autopilot planner;

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
	
	
	public UpdateHelper(DroneHelper droneHelper, int TIME_SLOWDOWN_MULTIPLIER, CameraHelper cameraHelper,
					    WorldObject[] worldObjects, Autopilot planner, TestbedGui testbedGui) {

    	this.droneHelper = droneHelper;
        this.TIME_SLOWDOWN_MULTIPLIER = TIME_SLOWDOWN_MULTIPLIER;
        this.cameraHelper = cameraHelper;
        this.worldObjects = worldObjects;
        this.planner = planner;
        this.testbedGui = testbedGui;
        this.time = 0;
        this.followDrone = 0;
    }

	/**
	 * This function will cycle through all the to update variables
	 * 
	 * @param interval
	 *            The passed time (delta time)
	 * @param mouseInput
	 *            This is an artefact of how we set up the update classes at the
	 *            start
	 */
	public void updateCycle(float interval, MouseInput mouseInput) {
		this.time += interval / TIME_SLOWDOWN_MULTIPLIER;

		droneHelper.update(interval/TIME_SLOWDOWN_MULTIPLIER);
		
		if (droneHelper.droneIds.isEmpty())
			return;
		
		Vector3f newDronePos = droneHelper.getDronePhysics(0).getPosition();
		
		updateCameraPositions(mouseInput, newDronePos, followDrone);

		updatePlanner(newDronePos);

		testbedGui.update(droneHelper.getDronePhysics(followDrone).getVelocity(), newDronePos,
						  droneHelper.getDronePhysics(followDrone).getHeading(),
						  droneHelper.getDronePhysics(followDrone).getPitch(),
						  droneHelper.getDronePhysics(followDrone).getRoll());
		
		testbedGui.setDrone(newDronePos, droneHelper.getDronePhysics(followDrone).getHeading());
		testbedGui.setCubes(worldObjects);
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
		
		
		Physics physics = droneHelper.getDronePhysics(0);

		cameraHelper.droneCamera.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);
		cameraHelper.droneCamera.setRotation(-physics.getPitch(),-physics.getHeading(),-physics.getRoll());

		float offset = 17.5f;
		cameraHelper.chaseCamera.setPosition(newDronePos.x + offset * (float) Math.sin(physics.getHeading()),
				newDronePos.y, newDronePos.z + offset * (float) Math.cos(physics.getHeading()));
		cameraHelper.chaseCamera.setRotation(0, -physics.getHeading(), 0);
		
		cameraHelper.updateTopCam(newDronePos);
		cameraHelper.updateRightCam(newDronePos);
	}


	private void updatePlanner(Vector3f newDronePos) {
		if (planner != null)
			plannerUpdate(newDronePos);
	}


	/**
	 * This line is only triggered if the specified world does indeed want a
	 * motion planner
	 */
	private void plannerUpdate(Vector3f newDronePos) {
		Physics physics = droneHelper.getDronePhysics(0);

		AutopilotOutputs out = planner.timePassed(Utils.buildInputs(null, newDronePos.x,
				newDronePos.y, newDronePos.z, physics.getHeading(), physics.getPitch(), physics.getRoll(), time));

		try {
			physics.updateDrone(out);
		} catch (PhysicsException e) {
			JOptionPane.showMessageDialog(testbedGui, "An illegal force was entered for the drone: " + e.getMessage(), "Physics Exception",
					JOptionPane.ERROR_MESSAGE);
			droneHelper.removeDrone(0);
		}
	}

}
