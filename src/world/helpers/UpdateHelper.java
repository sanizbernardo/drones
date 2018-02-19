package world.helpers;

import entities.WorldObject;
import entities.trail.Trail;
import gui.testbed.TestbedGui;
import interfaces.Autopilot;
import interfaces.AutopilotOutputs;

import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import physics.Physics;
import utils.Constants;
import utils.FloatMath;
import utils.IO.MouseInput;
import utils.Utils;
import utils.image.ImageCreator;

import java.util.ArrayList;

public class UpdateHelper {

    public UpdateHelper(Physics worldPhysics,
                        Boolean wantPhysics,
                        Trail trail,
                        ArrayList<WorldObject> pathObjects,
                        int TIME_SLOWDOWN_MULTIPLIER,
                        CameraHelper cameraHelper,
                        WorldObject[] droneItems,
                        WorldObject[] worldObjects,
                        Autopilot planner,
                        TestbedGui testbedGui,
                        ImageCreator imageCreator) {

        this.physics = worldPhysics;
        this.wantPhysics = wantPhysics;
        this.trail = trail;
        this.pathObjects = pathObjects;
        this.TIME_SLOWDOWN_MULTIPLIER = TIME_SLOWDOWN_MULTIPLIER;
        this.cameraHelper = cameraHelper;
        this.droneItems = droneItems;
        this.worldObjects = worldObjects;
        this.planner = planner;
        this.testbedGui = testbedGui;
        this.imageCreator = imageCreator;
    }

    /**
     * All round attributes
     */
    private Physics physics;
    private boolean wantPhysics;
    private int TIME_SLOWDOWN_MULTIPLIER;

    /**
     * Trail update cycle
     */
    private Trail trail;
    private ArrayList<WorldObject> pathObjects;

    /**
     * Camera update cycle
     */
    private CameraHelper cameraHelper;

    /**
     * Drone model update cycle
     */
    private WorldObject[] droneItems;

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
     * This function will cycle through all the to update variables
     * @param interval
     *        The passed time (delta time)
     * @param mouseInput
     *        This is an artefact of how we set up the update classes at the start
     */
    public void updateCycle(float interval, MouseInput mouseInput) {
        updateTrail(this.trail, this.physics, this.pathObjects);

        updateTouchedCubes();

        updatePhysics(interval);

        Vector3f newDronePos = new Vector3f(physics.getPosition());
        updateCameraPositions(mouseInput, newDronePos);

        updateDroneItems(newDronePos);

        updatePlanner(interval, newDronePos);

        testbedGui.update(physics.getVelocity(), newDronePos, physics.getHeading(), physics.getPitch(), physics.getRoll());

    }

    private void updateTrail(Trail trail, Physics physics, ArrayList<WorldObject> pathObjects) {
        trail.leaveTrail(physics.getPosition(), pathObjects);
    }

    private void updateTouchedCubes() {
        removeTouchedCubes();
    }

    /**
     * Setting them to scale 0 to prevent LWJGL errors, again this can be improved a lot but not going to waste time on this
     */
    private void removeTouchedCubes() {
        Vector3f pos = physics.getPosition();
        for (int i = 0; i < worldObjects.length; i++) {
            WorldObject cube = worldObjects[i];
            if(cube != null && !Utils.euclDistance(cube.getPosition(), pos, Constants.PICKUP_DISTANCE)) {
                System.out.printf("Hit (%s ,%s, %s), drone was at (%s, %s, %s). #%s\n", cube.getPosition().x,cube.getPosition().y,cube.getPosition().z, pos.x, pos.y, pos.z, cubeoounter);
                //De lijn hieronder is ranzig en ik excuseer mij op voorhand dat ik dit zelfs heb durven typen, mijn excuses
                worldObjects[i] = null;
                cubeoounter++;
            }
        }
    }
    private int cubeoounter = 1;



    private void updatePhysics(float interval) {
        if (this.wantPhysics) {
            try {
                this.physics.update(interval/this.TIME_SLOWDOWN_MULTIPLIER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    private void updateCameraPositions( MouseInput mouseInput, Vector3f newDronePos) {
        // Update camera based on mouse
        cameraHelper.freeCamera.movePosition(cameraHelper.getCameraInc().x * Constants.CAMERA_POS_STEP,
                cameraHelper.getCameraInc().y * Constants.CAMERA_POS_STEP,
                cameraHelper.getCameraInc().z * Constants.CAMERA_POS_STEP);
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            cameraHelper.freeCamera.moveRotation(FloatMath.toRadians(rotVec.x * Constants.MOUSE_SENSITIVITY),
                    FloatMath.toRadians(rotVec.y * Constants.MOUSE_SENSITIVITY),
                    0);
        }

        cameraHelper.droneCamera.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);
        cameraHelper.droneCamera.setRotation(-physics.getPitch(),-physics.getHeading(),-physics.getRoll());

        float offset = 10f;
        cameraHelper.chaseCamera.setPosition(newDronePos.x + offset * (float)Math.sin(physics.getHeading()), newDronePos.y, newDronePos.z + offset * (float)Math.cos(physics.getHeading()));
        cameraHelper.chaseCamera.setRotation(0,-physics.getHeading(),0);
    }




    private void updateDroneItems(Vector3f newDronePos) {
        // Update the position of each drone item

        for (WorldObject droneItem : droneItems) {
            droneItem.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);
            droneItem.setRotation(-physics.getPitch(),-physics.getHeading(),-physics.getRoll());
        }

        translateWheels();

        rotateWings();
    }
    
    private void rotateWings() {
    	/*        Vector3f leftWing = droneItems[Constants.DRONE_LEFT_WING].getRotation();
        leftWing = FloatMath.transform(physics.getTransMat(), leftWing);
        Matrix3f rot = new Matrix3f().identity().rotateX(physics.getLWInclination());
        leftWing = FloatMath.transform(rot, leftWing);
        leftWing = FloatMath.transform(physics.getTransMatInv(), leftWing);*/
    }
    
    private void translateWheels() {
        setWheel(Constants.DRONE_WHEEL_FRONT, 0, 0, -2.1f);
        setWheel(Constants.DRONE_WHEEL_BACK_LEFT, -1, 0, 1.4f);
        setWheel(Constants.DRONE_WHEEL_BACK_RIGHT, 1, 0, 1.4f);
    }
    
    private void setWheel(int id, float x, float y, float z) {
	    Vector3f wheel = droneItems[id].getPosition();
        Vector3f wheelT = FloatMath.transform(physics.getTransMat(), wheel); //Not accessing the position of the drone anymore
        wheelT.add(new Vector3f(x,y,z));
        wheelT = FloatMath.transform(physics.getTransMatInv(), wheelT);
        droneItems[id].setPosition(wheelT.x,  wheelT.y,  wheelT.z);
    }

    private void updatePlanner(float interval, Vector3f newDronePos) {
        if (planner != null) plannerUpdate(newDronePos, interval/TIME_SLOWDOWN_MULTIPLIER);
    }

    /**
     * This line is only triggered if the specified world does indeed want a motion planner
     */
    private void plannerUpdate(Vector3f newDronePos, float interval) {
        AutopilotOutputs out = planner.timePassed(
                Utils.buildInputs(imageCreator.screenShot(),
                        newDronePos.x,
                        newDronePos.y,
                        newDronePos.z,
                        physics.getHeading(),
                        physics.getPitch(),
                        physics.getRoll(),
                        interval)
        );

        physics.updateDrone(out);
    }

}
