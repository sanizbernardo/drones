package world.helpers;

import entities.WorldObject;
import entities.trail.Trail;
import gui.testbed.TestbedGui;
import interfaces.Autopilot;
import interfaces.AutopilotOutputs;
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
            cameraHelper.freeCamera.moveRotation(rotVec.x * Constants.MOUSE_SENSITIVITY,
                    rotVec.y * Constants.MOUSE_SENSITIVITY,
                    0);
        }

        cameraHelper.droneCamera.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);
        cameraHelper.droneCamera.setRotation(-(float)Math.toDegrees(physics.getPitch()),-(float)Math.toDegrees(physics.getHeading()),-(float)Math.toDegrees(physics.getRoll()));

        float offset = 10f;
        cameraHelper.chaseCamera.setPosition(newDronePos.x + offset * (float)Math.sin(physics.getHeading()), newDronePos.y, newDronePos.z + offset * (float)Math.cos(physics.getHeading()));
        cameraHelper.chaseCamera.setRotation(0,-(float)Math.toDegrees(physics.getHeading()),0);
    }




    private void updateDroneItems(Vector3f newDronePos) {
        // Update the position of each drone item

        for (WorldObject droneItem : droneItems) {
            droneItem.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);

            droneItem.setRotation(-(float)Math.toDegrees(physics.getPitch()),-(float)Math.toDegrees(physics.getHeading()),-(float)Math.toDegrees(physics.getRoll()));
        }

        //get the current rotation of the wing
        Vector3f refferedRot = droneItems[Constants.DRONE_LEFT_WING].getRotation();
        //make a deepcopy so that we're not changing the actual rotation
        Vector3f leftWing = new Vector3f(refferedRot.x, refferedRot.y, refferedRot.z);
        //transform our deepcopy to the drone axi
        FloatMath.transform(physics.getTransMat(), leftWing);
        //rotate the wing aorund the drone's x-axis
        leftWing.mul(new Vector3f(FloatMath.toDegrees(-physics.getLWInclination()) ,0, 0));
        //go back to the world axi
        FloatMath.transform(physics.getTransMatInv(), leftWing);

        droneItems[Constants.DRONE_LEFT_WING].setRotation(leftWing.x, leftWing.y, leftWing.z);


        //get the current rotation of the wing
        Vector3f refferedRot2 = droneItems[Constants.DRONE_RIGHT_WING].getRotation();
        //make a deepcopy so that we're not changing the actual rotation
        Vector3f rightWing = new Vector3f(refferedRot2.x, refferedRot2.y, refferedRot2.z);
        //transform our deepcopy to the drone axi
        FloatMath.transform(physics.getTransMat(), rightWing);
        //rotate the wing aorund the drone's x-axis
        rightWing.mul(new Vector3f(FloatMath.toDegrees(-physics.getRWInclination()) ,0, 0));
        //go back to the world axi
        FloatMath.transform(physics.getTransMatInv(), rightWing);

        droneItems[Constants.DRONE_RIGHT_WING].setRotation(rightWing.x, rightWing.y, rightWing.z);
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
