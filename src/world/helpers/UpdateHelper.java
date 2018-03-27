package world.helpers;

import entities.WorldObject;
import gui.TestbedGui;
import interfaces.Autopilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotOutputs;
import interfaces.Path;

import org.joml.Vector2f;
import org.joml.Vector3f;

import physics.Physics;
import utils.Constants;
import utils.FloatMath;
import utils.PhysicsException;
import utils.IO.MouseInput;
import utils.Utils;
import utils.image.ImageCreator;

import java.util.Random;

import javax.swing.JOptionPane;

public class UpdateHelper {

	public UpdateHelper(DroneHelper droneHelper,
						AutopilotConfig config,
                        int TIME_SLOWDOWN_MULTIPLIER,
                        CameraHelper cameraHelper,
                        WorldObject[] worldObjects,
                        Autopilot planner,
                        TestbedGui testbedGui,
                        ImageCreator imageCreator) {

    	this.droneHelper = droneHelper;
    	this.config = config;
        this.TIME_SLOWDOWN_MULTIPLIER = TIME_SLOWDOWN_MULTIPLIER;
        this.cameraHelper = cameraHelper;
        this.worldObjects = worldObjects;
        this.planner = planner;
        this.testbedGui = testbedGui;
        this.imageCreator = imageCreator;
        this.time = 0;
    }

	/**
	 * All round attributes
	 */
	private int TIME_SLOWDOWN_MULTIPLIER;
	private AutopilotConfig config;

	/**
	 * Camera update cycle
	 */
	private CameraHelper cameraHelper;

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
	 * ImageRecog update
	 */
	private ImageCreator imageCreator;
	/**
	 * The time passed since start of the simulation
	 */
	private float time;
	/**
	 * Drone update
	 */
	private DroneHelper droneHelper;
	/**
	 * Cube hit counter
	 */
	private int cubeoounter = 1;
	
	private boolean pathSet = false;


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
		
		if (droneHelper.getNbDrones() == 0) {
			return;
		}
		Vector3f newDronePos = droneHelper.getDronePhysics(config.getDroneID()).getPosition();
		
		updateTouchedCubes(newDronePos);

		updateCameraPositions(mouseInput, newDronePos);

		updatePlanner(newDronePos);

		testbedGui.update(droneHelper.getDronePhysics(config.getDroneID()).getVelocity(), newDronePos,
						  droneHelper.getDronePhysics(config.getDroneID()).getHeading(),
						  droneHelper.getDronePhysics(config.getDroneID()).getPitch(),
						  droneHelper.getDronePhysics(config.getDroneID()).getRoll());
		
		testbedGui.setDrone(newDronePos, droneHelper.getDronePhysics(config.getDroneID()).getHeading());
		testbedGui.setCubes(worldObjects);
	}

	private void updateTouchedCubes(Vector3f pos) {
		removeTouchedCubes(pos);
	}

	/**
	 * Setting them to scale 0 to prevent LWJGL errors, again this can be
	 * improved a lot but not going to waste time on this
	 */
	private void removeTouchedCubes(Vector3f pos) {
		for (int i = 0; i < worldObjects.length; i++) {
			WorldObject cube = worldObjects[i];
			if (cube != null && !Utils.euclDistance(cube.getPosition(), pos, Constants.PICKUP_DISTANCE)) {
				System.out.printf("Hit (%s ,%s, %s), drone was at (%s, %s, %s). #%s\n", cube.getPosition().x,
						cube.getPosition().y, cube.getPosition().z, pos.x, pos.y, pos.z, cubeoounter);
				// Arno: "De lijn hieronder is ranzig en ik excuseer mij op voorhand
				// dat ik dit zelfs heb durven typen, mijn excuses"
				worldObjects[i] = null;
				cubeoounter++;
			}
		}
	}



	private void updateCameraPositions(MouseInput mouseInput, Vector3f newDronePos) {
		// Update camera based on mouse
		cameraHelper.freeCamera.movePosition(cameraHelper.getCameraInc().x * Constants.CAMERA_POS_STEP,
				cameraHelper.getCameraInc().y * Constants.CAMERA_POS_STEP,
				cameraHelper.getCameraInc().z * Constants.CAMERA_POS_STEP);
		if (mouseInput.isRightButtonPressed()) {
			Vector2f rotVec = mouseInput.getDisplVec();
			cameraHelper.freeCamera.moveRotation(FloatMath.toRadians(rotVec.x * Constants.MOUSE_SENSITIVITY),
					FloatMath.toRadians(rotVec.y * Constants.MOUSE_SENSITIVITY), 0);
		}
		
		
		Physics physics = droneHelper.getDronePhysics(config.getDroneID());

		cameraHelper.droneCamera.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);
//		Vector3f newRot = FloatMath.transform(physics.getTransMat(), new Vector3f(-physics.getPitch(), -physics.getHeading(), -physics.getRoll()));
		cameraHelper.droneCamera.setRotation(-physics.getPitch(),-physics.getHeading(),-physics.getRoll());

		float offset = 17.5f;
		cameraHelper.chaseCamera.setPosition(newDronePos.x + offset * (float) Math.sin(physics.getHeading()),
				newDronePos.y, newDronePos.z + offset * (float) Math.cos(physics.getHeading()));
		cameraHelper.chaseCamera.setRotation(0, -physics.getHeading(), 0);
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
		if (!pathSet && testbedGui.setPath()) {
			Path path = buildPath();			
			planner.setPath(path);
			
			pathSet = true;
		}
		
		Physics physics = droneHelper.getDronePhysics(config.getDroneID());

		AutopilotOutputs out = planner.timePassed(Utils.buildInputs(imageCreator.screenShot(), newDronePos.x,
				newDronePos.y, newDronePos.z, physics.getHeading(), physics.getPitch(), physics.getRoll(), time));

		try {
			physics.updateDrone(out);
		} catch (PhysicsException e) {
			JOptionPane.showMessageDialog(testbedGui, "An illegal force was entered for the drone: " + e.getMessage(), "Physics Exception",
					JOptionPane.ERROR_MESSAGE);
			droneHelper.removeDrone(config.getDroneID());
		}
	}

	private Path buildPath() {
		float[] x = new float[worldObjects.length],
				y = new float[worldObjects.length],
				z = new float[worldObjects.length];
		
		
		Random random = new Random();
		int i = 0;
		for (WorldObject cube: worldObjects) {
			Vector3f pos = cube.getPosition();
			
			Vector3f rand = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
			rand.sub(new Vector3f(0.5f)).normalize();
			rand.mul(Constants.PATH_ACCURACY);
			
			Vector3f guess = pos.add(rand, new Vector3f());
			
			x[i] = guess.x;
			y[i] = guess.y;
			z[i] = guess.z;
			i ++;
		}
				
		return new Path() {
			public float[] getX() {return x;}

			public float[] getY() {return y;}
			
			public float[] getZ() {return z;}
		};
	}

}
