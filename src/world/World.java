package world;

import engine.IWorldRules;
import engine.Window;
import engine.graph.Camera;
import engine.graph.Renderer;
import entities.WorldObject;
import entities.meshes.cube.Cube;
import entities.meshes.drone.DroneMesh;
import gui.testbed.TestbedGui;
import interfaces.Autopilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotOutputs;

import org.joml.Vector2f;
import org.joml.Vector3f;

import physics.Drone;
import physics.Motion;
import physics.PhysicsEngine;
import utils.Constants;
import utils.IO.KeyboardInput;
import utils.IO.MouseInput;
import utils.Utils;
import utils.image.ImageCreator;

public abstract class World implements IWorldRules {

    private final Vector3f cameraInc;
    private final Camera freeCamera, droneCamera, chaseCamera, topOrthoCamera, rightOrthoCamera;
    protected Autopilot planner = new Motion();

    private Renderer renderer;
    private WorldObject[] droneItems;
    private PhysicsEngine physicsEngine;
    private ImageCreator imageCreator;
    private Cube[] cubeMeshes;
    private int TIME_SLOWDOWN_MULTIPLIER;
    private boolean wantPhysicsEngine, wantPlanner;
    private KeyboardInput keyboardInput;
    private TestbedGui testbedGui;

    protected Drone drone;
    protected AutopilotConfig config;

    /* These are to be directly called in the world classes*/
    WorldObject[] worldObjects;

    World(int tSM, boolean wantPhysicsEngine, boolean wantPlanner) {
        this.TIME_SLOWDOWN_MULTIPLIER = tSM;
        this.wantPhysicsEngine = wantPhysicsEngine;
        this.wantPlanner = wantPlanner;
        this.keyboardInput = new KeyboardInput();
        this.freeCamera = new Camera();
        this.droneCamera = new Camera();
        this.chaseCamera = new Camera();
        this.topOrthoCamera = new Camera();
        this.rightOrthoCamera = new Camera();
        constructCameras();
        this.cameraInc = new Vector3f(0, 0, 0);
        this.testbedGui = new TestbedGui();
    }
    
    private void constructCameras() {
        topOrthoCamera.setPosition(0,200,0);
        topOrthoCamera.setRotation(90, 0,0);
        rightOrthoCamera.setPosition(200, 0, 0);
        rightOrthoCamera.setRotation(0, -90, 0);
    }

    @Override
    public void init(Window window) throws Exception {
        createCubes();
        hooks(window);
        setup();
        addDrone();
        startSimulation();
        testbedGui.showGUI();
    }
    
    public abstract String getDescription();

    private void createCubes() {
        Cube redCube = new Cube(0,1f);
        Cube greenCube = new Cube(120,1f);
        Cube blueCube = new Cube(240,1f);
        cubeMeshes = new Cube[]{redCube, greenCube, blueCube};
    }

    public static AutopilotConfig createConfig() {
        return new AutopilotConfig() {
            public float getGravity() {return 10f;}
            public float getWingX() {return 0.25f;}
            public float getTailSize() {return 0.5f;}
            public float getEngineMass() {return 7f;}
            public float getWingMass() {return 2.5f;}
            public float getTailMass() {return 3f;}
            public float getMaxThrust() {return 5000f;}
            public float getMaxAOA() {return -1f;}
            public float getWingLiftSlope() {return 1.1f;}
            public float getHorStabLiftSlope() {return 0.11f;}
            public float getVerStabLiftSlope() {return 0.11f;}
            public float getHorizontalAngleOfView() {return (float) Math.toRadians(120f);}
            public float getVerticalAngleOfView() {return (float) Math.toRadians(120f);}
            public int getNbColumns() {return 200;}
            public int getNbRows() {return 200;}};
    }

    private void hooks(Window window) {
        physicsEngine = new PhysicsEngine(config);
        renderer = new Renderer(config);
        try {
            renderer.init(window);
        } catch (Exception e) {
            System.out.println("Abstract class World (render.init(window)) gave this error: " + e.getMessage());
            e.printStackTrace();
        }

        imageCreator = new ImageCreator(config.getNbColumns(), config.getNbRows());
    }

    private void addDrone() {
        DroneMesh droneMesh = new DroneMesh(drone);
        WorldObject left = new WorldObject(droneMesh.getLeft());
        WorldObject right = new WorldObject(droneMesh.getRight());
        WorldObject body = new WorldObject(droneMesh.getBody());
        droneItems = new WorldObject[]{left, right, body};
    }

    private void startSimulation() {
        planner.simulationStarted(config, Utils.buildInputs(imageCreator.screenShot(),
                                                            0,
                                                            0,
                                                            0,
                                                            0,
                                                            0,
                                                            0,
                                                            0));
    }

    public Cube[] getCubeMeshes() {
        return cubeMeshes;
    }

    /**
     * World specific init
     */
    public abstract void setup();

    /**
     * Handle input, should be in separate class
     */
    @Override
    public void input(Window window, MouseInput mouseInput) {
        keyboardInput.worldInput(cameraInc, window, imageCreator, renderer);
    }

    /**
     * Handle the game objects internally
     */
    @Override
    public void update(float interval, MouseInput mouseInput) {

        if(wantPhysicsEngine) physicsEngine.update(interval/TIME_SLOWDOWN_MULTIPLIER, drone);

        Vector3f newDronePos = new Vector3f(drone.getPosition());

        // Update camera based on mouse
        freeCamera.movePosition(cameraInc.x * Constants.CAMERA_POS_STEP, cameraInc.y * Constants.CAMERA_POS_STEP, cameraInc.z * Constants.CAMERA_POS_STEP);
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            freeCamera.moveRotation(rotVec.x * Constants.MOUSE_SENSITIVITY, rotVec.y * Constants.MOUSE_SENSITIVITY, 0);
        }

        droneCamera.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);
        droneCamera.setRotation(-(float)Math.toDegrees(drone.getPitch()),-(float)Math.toDegrees(drone.getHeading()),-(float)Math.toDegrees(drone.getRoll()));

        float offset = 5f;
        //TODO: implement getHeading properly...
        chaseCamera.setPosition(newDronePos.x + offset * (float)Math.sin(drone.getHeading()), newDronePos.y, newDronePos.z + offset * (float)Math.cos(drone.getHeading()));
        chaseCamera.setRotation(0,-(float)Math.toDegrees(drone.getHeading()),0);
        
                
        // Update the position of each drone item
        for (WorldObject droneItem : droneItems) {
            droneItem.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);

            droneItem.setRotation(-(float)Math.toDegrees(drone.getPitch()),-(float)Math.toDegrees(drone.getHeading()),-(float)Math.toDegrees(drone.getRoll()));
        }

        if(wantPlanner) plannerUpdate(newDronePos, interval/TIME_SLOWDOWN_MULTIPLIER);

        testbedGui.update(drone.getVelocity(), newDronePos, new Vector3f(drone.getPitch(),drone.getHeading(),drone.getRoll()));
        
    }

    /**
     * This line is only triggered if the specified world does indeed want a motion planner
     * @param newDronePos
     *        The new position of the drone as per the physics engine
     * @param interval
     *        The passed time in this step
     */
    private void plannerUpdate(Vector3f newDronePos, float interval) {
        AutopilotOutputs out = planner.timePassed(
                Utils.buildInputs(imageCreator.screenShot(),
                        newDronePos.x,
                        newDronePos.y,
                        newDronePos.z,
                        drone.getHeading(),
                        drone.getPitch(),
                        drone.getRoll(),
                        interval)
        );

        drone.setHorStabInclination(out.getHorStabInclination());
        drone.setVerStabInclination(out.getVerStabInclination());
        drone.setLeftWingInclination(out.getLeftWingInclination());
        drone.setRightWingInclination(out.getRightWingInclination());
        drone.setThrust(out.getThrust());
    }

    @Override
    public void render(Window window) {
        renderer.render(window, freeCamera, droneCamera, chaseCamera, topOrthoCamera, rightOrthoCamera, worldObjects, droneItems);
    }

    /**
     * Delete VBO and VAO objects
     */
    @Override
    public void cleanup() {
        renderer.cleanup();
        for (WorldObject gameItem : worldObjects) {
            gameItem.getMesh().cleanUp();
        }
    }
    
    @Override
    public void endSimulation() {
    	testbedGui.dispose();
    	planner.simulationEnded();
    }
}
