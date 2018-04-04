package testbed.world;

import interfaces.Autopilot;
import interfaces.AutopilotConfig;
import testbed.Physics;
import testbed.engine.*;
import testbed.entities.WorldObject;
import testbed.entities.airport.Airport;
import testbed.entities.ground.Ground;
import testbed.graphics.Renderer;
import testbed.gui.TestbedGui;
import testbed.world.helpers.*;
import utils.Utils;
import utils.IO.KeyboardInput;
import utils.IO.MouseInput;

import org.joml.Vector3f;

public abstract class World implements IWorldRules {

	private CameraHelper cameraHelper;

	private Renderer renderer;
	private int TIME_SLOWDOWN_MULTIPLIER;
	private KeyboardInput keyboardInput;
	private TestbedGui testbedGui;
	private UpdateHelper updateHelper;
	private LogHelper logHelper;
	private String logDrone;
	private float time;

	/* These are to be directly called in the world classes */
	protected Autopilot planner;
	protected WorldObject[] worldObjects;
	protected Engine gameEngine;
	protected Airport[] airports;
	protected Ground ground;
	protected DroneHelper droneHelper;

	
	public World(int tSM, boolean wantPhysicsEngine, int nbDrones) {
		this.cameraHelper = new CameraHelper();

		this.TIME_SLOWDOWN_MULTIPLIER = tSM;

		this.keyboardInput = new KeyboardInput();

		this.testbedGui = new TestbedGui();

		this.droneHelper = new DroneHelper(wantPhysicsEngine, nbDrones, testbedGui);
	}

	/**
	 * World specific setup.
	 *
	 * Things you have to do here: generate config, init physics, generate
	 * worldObjects, and make your planner.
	 */
	public abstract void setup();

	public abstract String getDescription();

	
	@Override
    public void init(Window window, Engine engine) throws Exception {
    	this.gameEngine = engine; 
    	this.time = 0;
    	
    	this.airports = new Airport[0];
    	this.worldObjects = new WorldObject[0]; 
    	
    	setup();
    	
    	try {
			this.renderer = new Renderer();
		    this.renderer.init(window);
		} catch (Exception e) {
		    System.out.println("Abstract class World (render.init(window)) gave this error: " + e.getMessage());
		    e.printStackTrace();
		}
				
		if (planner != null) {
			Physics physics = droneHelper.getDronePhysics(0);
			planner.simulationStarted(physics.getConfig(), Utils.buildInputs(null,
				physics.getPosition(), physics.getHeading(), physics.getPitch(), physics.getRoll(), 0));
		}
		
		testbedGui.showGUI();

		this.updateHelper = new UpdateHelper(droneHelper, TIME_SLOWDOWN_MULTIPLIER, cameraHelper,
											 worldObjects, planner, testbedGui);
    }

	
	public void addDrone(AutopilotConfig config, Vector3f startPos, Vector3f startVel) {
		droneHelper.addDrone(config, startPos, startVel, 0f);
	}
	
	public void addDrone(AutopilotConfig config, Vector3f startPos, Vector3f startVel, float startHeading) {
		droneHelper.addDrone(config, startPos, startVel, startHeading);
	}

	
	public void initLogging(String droneId) {
		this.logHelper = new LogHelper();
		this.logDrone = droneId;
	}
	
	
	@Override
	public void input(Window window, MouseInput mouseInput) {
		keyboardInput.worldInput(cameraHelper.getCameraInc(), window, renderer);
	}

	
	/**
	 * Handle the game objects internally
	 */
	@Override
	public void update(float interval, MouseInput mouseInput) {
		this.time += interval;
		
		updateHelper.updateCycle(interval, mouseInput);
		
		if (droneHelper.droneIds.isEmpty()) {
			gameEngine.setLoopShouldExit();
			return;
		}

		if (logHelper != null) 
			logHelper.log(time, droneHelper.getDronePhysics(logDrone));
	}

	
	@Override
	public void render(Window window) {
		renderer.render(window, cameraHelper, worldObjects, droneHelper, ground, airports);
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
		if (planner != null)
			planner.simulationEnded();
		if (logHelper != null)
			logHelper.close();
	}
}
