package world;

import entities.drone.DroneSkeleton;
import entities.ground.Ground;
import entities.tarmac.Tarmac;
import entities.trail.Trail;
import gui.TestbedGui;
import engine.Engine;
import engine.IWorldRules;
import engine.Window;
import graphics.Renderer;
import entities.WorldObject;
import interfaces.Autopilot;
import interfaces.AutopilotConfig;
import physics.Physics;
import utils.Utils;
import utils.IO.KeyboardInput;
import utils.IO.MouseInput;
import utils.image.ImageCreator;
import world.helpers.CameraHelper;
import world.helpers.UpdateHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public abstract class World implements IWorldRules {

    private CameraHelper cameraHelper;
	
    private Renderer renderer;
    private WorldObject[] droneItems;
    private ImageCreator imageCreator;
    private int TIME_SLOWDOWN_MULTIPLIER;
    private KeyboardInput keyboardInput;
    private TestbedGui testbedGui;
    private boolean wantPhysics;
    private UpdateHelper updateHelper;
    private BufferedWriter writer;
    private float time;
    
    /* These are to be directly called in the world classes*/
    protected Autopilot planner;
    protected WorldObject[] worldObjects;
    protected AutopilotConfig config;
    protected Physics physics;
    protected Engine gameEngine;
    protected ArrayList<WorldObject> pathObjects = new ArrayList<>();

    protected Trail trail;
    protected Ground ground;
    protected Tarmac tarmac;
    
    public World(int tSM, boolean wantPhysicsEngine) {
        this.cameraHelper = new CameraHelper();

        this.TIME_SLOWDOWN_MULTIPLIER = tSM;
        
        this.wantPhysics = wantPhysicsEngine;
        this.physics = new Physics();
        
        this.keyboardInput = new KeyboardInput();

        this.testbedGui = new TestbedGui();
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
    public void init(Window window, Engine engine) throws Exception {
    	this.gameEngine = engine; 
    	this.time = 0;
    	
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

		this.updateHelper = new UpdateHelper(physics,
                wantPhysics,
                trail,
                pathObjects,
                TIME_SLOWDOWN_MULTIPLIER,
                cameraHelper,
                droneItems,
                worldObjects,
                planner,
                testbedGui,
                imageCreator);
    }

    public void initLogging() {
    	try {
			File file = new File("position.log");
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			writer.write("position log, x  y  z  heading  pitch  roll\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
    }
    
    
    private void addDrone() {
        DroneSkeleton droneMesh = new DroneSkeleton(config);
        WorldObject left = new WorldObject(droneMesh.getLeft());
        WorldObject right = new WorldObject(droneMesh.getRight());
        WorldObject body = new WorldObject(droneMesh.getBody());

        WorldObject wheelFront = new WorldObject(droneMesh.getWheel());
        WorldObject wheelBackLeft = new WorldObject(droneMesh.getWheel());
        WorldObject wheelBackRight = new WorldObject(droneMesh.getWheel());
        
        droneItems = new WorldObject[]{left, right, body, wheelFront, wheelBackLeft, wheelBackRight};
    }



    
    @Override
    public void input(Window window, MouseInput mouseInput) {
        keyboardInput.worldInput(cameraHelper.getCameraInc(), window, imageCreator, renderer);
    }

    /**
     * Handle the game objects internally
     */
    @Override
    public void update(float interval, MouseInput mouseInput) {
        updateHelper.updateCycle(interval, mouseInput);
		
        this.time += interval;
        if (writer != null) {
	        try {
				writer.write(time + ": " + physics.getPosition().x + " " + physics.getPosition().y + " " + physics.getPosition().z +
							" " + physics.getHeading() + " " + physics.getPitch() + " " + physics.getRoll() + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
    }

    @Override
    public void render(Window window) {
        renderer.render(window,
                        cameraHelper,
                        worldObjects,
                        droneItems,
                        pathObjects,
                        ground,
                        tarmac);
    }

    /**
     * Delete VBO and VAO objects
     */
    @Override
    public void cleanup() {
        renderer.cleanup();
        for (WorldObject gameItem : worldObjects) {
            if (gameItem != null)
            	gameItem.getMesh().cleanUp();
        }
    }
    
    @Override
    public void endSimulation() {
    	testbedGui.dispose();
    	if (planner != null) planner.simulationEnded();
    	try {
    		if (writer != null)
    			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
