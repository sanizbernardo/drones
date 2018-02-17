package world;

import entities.trail.Trail;
import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.Engine;
import engine.IWorldRules;
import engine.Window;
import engine.graph.Camera;
import engine.graph.Renderer;
import entities.WorldObject;
import entities.meshes.drone.DroneMesh;
import gui.testbed.TestbedGui;
import interfaces.Autopilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotOutputs;
import physics.Physics;
import utils.Constants;
import utils.PhysicsException;
import utils.Utils;
import utils.IO.KeyboardInput;
import utils.IO.MouseInput;
import utils.image.ImageCreator;

import java.util.ArrayList;


public abstract class World implements IWorldRules {

    private final Vector3f cameraInc;
    private Camera freeCamera, droneCamera, chaseCamera, topOrthoCamera, rightOrthoCamera;
	
    private Renderer renderer;
    private WorldObject[] droneItems;
    private ImageCreator imageCreator;
    private int TIME_SLOWDOWN_MULTIPLIER;
    private KeyboardInput keyboardInput;
    private TestbedGui testbedGui;
    private boolean wantPhysics;
    
    /* These are to be directly called in the world classes*/
    protected Autopilot planner;
    protected WorldObject[] worldObjects;
    protected AutopilotConfig config;
    protected Physics physics;
    protected Engine gameEngine;
    protected ArrayList<WorldObject> pathObjects = new ArrayList<>();
    protected Trail trail;
    
    
    public World(int tSM, boolean wantPhysicsEngine) {
        this.TIME_SLOWDOWN_MULTIPLIER = tSM;
        
        this.wantPhysics = wantPhysicsEngine;
        this.physics = new Physics();
        
        this.keyboardInput = new KeyboardInput();
        
        constructCameras();
        this.cameraInc = new Vector3f(0, 0, 0);
        
        this.testbedGui = new TestbedGui();
    }
    
    
    private void constructCameras() {
    	this.freeCamera = new Camera();
        this.droneCamera = new Camera();
        this.chaseCamera = new Camera();
        
        this.topOrthoCamera = new Camera();
        topOrthoCamera.setPosition(0,200,0);
        //topOrthoCamera.setRotation(90, 0, -90);
        topOrthoCamera.setRotation(90, 0, 0);
        this.rightOrthoCamera = new Camera();
        rightOrthoCamera.setPosition(200, 0, 0);
        rightOrthoCamera.setRotation(0, -90, 0);
    }

    @Override
    public void init(Window window, Engine engine) throws Exception {
    	this.gameEngine = engine; 
    	
    	setup();
    	
    	try {
			this.renderer = new Renderer(config);
		    this.renderer.init(window);
		} catch (Exception e) {
		    System.out.println("Abstract class World (render.init(window)) gave this error: " + e.getMessage());
		    e.printStackTrace();
		}
		
		this.imageCreator = new ImageCreator(config.getNbColumns(), config.getNbRows(), window);
		
		addDrone();
		
		if (planner != null)
			planner.simulationStarted(config, Utils.buildInputs(imageCreator.screenShot(),
				physics.getPosition(), physics.getHeading(), physics.getPitch(), physics.getRoll(), 0));
		
		testbedGui.showGUI();

		this.trail = new Trail();
    }

    
    private void addDrone() {
        DroneMesh droneMesh = new DroneMesh(config);
        WorldObject left = new WorldObject(droneMesh.getLeft());
        WorldObject right = new WorldObject(droneMesh.getRight());
        WorldObject body = new WorldObject(droneMesh.getBody());
        droneItems = new WorldObject[]{left, right, body};
    }

    /**
     * World specific setup. 
     * 
     * Things you have to do here: generate config, init physics, 
     * generate worldObjects, and make your planner.
     */
    public abstract void setup();
    
    public abstract String getDescription();

    
    @Override
    public void input(Window window, MouseInput mouseInput) {
        keyboardInput.worldInput(cameraInc, window, imageCreator, renderer);
    }

    /**
     * Handle the game objects internally
     */
    @Override
    public void update(float interval, MouseInput mouseInput) {

        trail.leaveTrail(physics.getPosition(), pathObjects);

        //TODO: the implementation of this function is REALLY bad, N^2 (can be reduced to NlogN +-)
        //TODO: so if you run cubeWorld this will really mess up the speed of the program.
        touchedCubes();

        if (wantPhysics) {
			try {
				physics.update(interval/TIME_SLOWDOWN_MULTIPLIER);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        Vector3f newDronePos = new Vector3f(physics.getPosition());

        // Update camera based on mouse
        freeCamera.movePosition(cameraInc.x * Constants.CAMERA_POS_STEP, cameraInc.y * Constants.CAMERA_POS_STEP, cameraInc.z * Constants.CAMERA_POS_STEP);
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            freeCamera.moveRotation(rotVec.x * Constants.MOUSE_SENSITIVITY, rotVec.y * Constants.MOUSE_SENSITIVITY, 0);
        }

        droneCamera.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);
        droneCamera.setRotation(-(float)Math.toDegrees(physics.getPitch()),-(float)Math.toDegrees(physics.getHeading()),-(float)Math.toDegrees(physics.getRoll()));

        float offset = 1f;
        chaseCamera.setPosition(newDronePos.x + offset * (float)Math.sin(physics.getHeading()), newDronePos.y, newDronePos.z + offset * (float)Math.cos(physics.getHeading()));
        chaseCamera.setRotation(0,-(float)Math.toDegrees(physics.getHeading()),0);
        
        
        // Update the position of each drone item
        for (WorldObject droneItem : droneItems) {
            droneItem.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);

            droneItem.setRotation(-(float)Math.toDegrees(physics.getPitch()),-(float)Math.toDegrees(physics.getHeading()),-(float)Math.toDegrees(physics.getRoll()));
        }

        
        if (planner != null)
			try {
				plannerUpdate(newDronePos, interval/TIME_SLOWDOWN_MULTIPLIER);
			} catch (PhysicsException e) {
				// TODO Better physics error handeling
				System.out.println("Error uccured during physics calculations:" + e.getMessage());
				endSimulation();
			}

        testbedGui.update(physics.getVelocity(), newDronePos, physics.getHeading(), physics.getPitch(), physics.getRoll());
        
    }

    int cubeoounter = 1;

    /**
     * Setting them to scale 0 to prevent LWJGL errors, againthis can be improved a lot but not going to waste time on this
     */
    private void touchedCubes() {
        Vector3f pos = physics.getPosition();
        for (int i = 0; i < worldObjects.length; i++) {
            WorldObject cube = worldObjects[i];
            if(cube != null && !Utils.euclDistance(cube.getPosition(), pos,4)) {
                System.out.printf("Hit (%s ,%s, %s), drone was at (%s, %s, %s). #%s\n", cube.getPosition().x,cube.getPosition().y,cube.getPosition().z, pos.x, pos.y, pos.z, cubeoounter);
                //De lijn hieronder is ranzig en ik excuseer mij op voorhand dat ik dit zelfs heb durven typen, mijn excuses
                worldObjects[i] = null;
                cubeoounter++;
            }
        }
    }

    /**
     * This line is only triggered if the specified world does indeed want a motion planner
     * @param newDronePos
     *        The new position of the drone as per the physics engine
     * @param interval
     *        The passed time in this step
     * @throws PhysicsException 
     */
    private void plannerUpdate(Vector3f newDronePos, float interval) throws PhysicsException {
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

    @Override
    public void render(Window window) {
        renderer.render(window, freeCamera, droneCamera, chaseCamera, topOrthoCamera, rightOrthoCamera, worldObjects, droneItems, pathObjects);
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
    	if (planner != null) planner.simulationEnded();
    }
}
