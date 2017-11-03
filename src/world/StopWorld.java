package world;

import javafx.scene.paint.Stop;
import utils.IO.MouseInput;
import datatypes.AutopilotConfig;
import datatypes.AutopilotInputs;
import datatypes.AutopilotOutputs;
import engine.IWorldRules;
import engine.Window;
import engine.graph.Camera;
import utils.image.ImageCreator;
import org.joml.Vector2f;
import org.joml.Vector3f;

import physics.Drone;
import physics.MotionPlanner;
import physics.PhysicsEngine;
import utils.Constants;
import entities.meshes.cube.Cube;
import entities.WorldObject;
import engine.graph.Renderer;
import entities.meshes.drone.DroneMesh;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

/**
 * Place where all the GameItem are to be placed in
 */
public class StopWorld extends World implements IWorldRules {

    public StopWorld(Window window) {
        super(window);
    }

    /**
     * Init
     */
    @Override
    public void init(Window window) throws Exception {
        worldObjects = new WorldObject[1];

        drone.setThrust(30);

        startSimulation();
    }


    /**
     * Handle the game objects internally
     */
    @Override
    public void update(float interval, MouseInput mouseInput) {

        /*
         * ---Section handled by testbed---
         */
        physicsEngine.update(interval/25, drone);


        Vector3f newDronePos = new Vector3f((float)drone.getPosition().get(0), (float)drone.getPosition().get(1), (float)drone.getPosition().get(2));

    	/*
    	 *  Update camera positions
    	 */
        freeCamera.movePosition(cameraInc.x * Constants.CAMERA_POS_STEP, cameraInc.y * Constants.CAMERA_POS_STEP, cameraInc.z * Constants.CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            freeCamera.moveRotation(rotVec.x * Constants.MOUSE_SENSITIVITY, rotVec.y * Constants.MOUSE_SENSITIVITY, 0);
        }
        
        droneCamera.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);
        droneCamera.setRotation(-(float)Math.toDegrees(drone.getPitch()),-(float)Math.toDegrees(drone.getYaw()),-(float)Math.toDegrees(drone.getRoll()));


        // Update the position of each drone item
        for (WorldObject droneItem : droneItems) {
            droneItem.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);
            droneItem.setRotation(-(float)Math.toDegrees(drone.getPitch()),-(float)Math.toDegrees(drone.getYaw()),-(float)Math.toDegrees(drone.getRoll()));
        }

        /*
         * ---Section handled by motion planner---
         */

        AutopilotOutputs out = planner.timePassed(new AutopilotInputs() {
            @Override
            public byte[] getImage() {
                return imageCreator.screenShot();
            }

            @Override
            public float getX() {
                return newDronePos.x;
            }

            @Override
            public float getY() {
                return newDronePos.y;
            }

            @Override
            public float getZ() {
                return newDronePos.z;
            }

            @Override
            public float getHeading() {
                return drone.getHeading();
            }

            @Override
            public float getPitch() {
                return drone.getPitch();
            }

            @Override
            public float getRoll() {
                return drone.getRoll();
            }

            @Override
            public float getElapsedTime() {
                return interval;
            }
        });

        drone.setHorStabInclination(out.getHorStabInclination());
        drone.setVerStabInclination(out.getVerStabInclination());
        drone.setLeftWingInclination(out.getLeftWingInclination());
        drone.setRightWingInclination(out.getRightWingInclination());
        drone.setThrust(out.getThrust());

    }

}
