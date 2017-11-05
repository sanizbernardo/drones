package world;

import org.junit.Test;
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
public class TestWorld extends World implements IWorldRules {

    public TestWorld() {
        super(10, true, true);
    }

    @Override
    public void setup() {

        int AMOUNT_OF_CUBES = 1;
        worldObjects = new WorldObject[AMOUNT_OF_CUBES];

        worldObjects[0] = new WorldObject(getCubeMeshes()[0].getMesh());
        worldObjects[0].setPosition(0f, 10f, -30f);

        drone.setThrust(20f);
        drone.setVelocity(new Vector3f(0f, 0f, -4f));

    }
}