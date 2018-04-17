package testbed.world;

import interfaces.AutopilotConfig;
import interfaces.AutopilotModule;
import testbed.engine.*;
import testbed.entities.WorldObject;
import testbed.entities.airport.Airport;
import testbed.entities.ground.Ground;
import testbed.graphics.Renderer;
import testbed.gui.TestbedGui;
import testbed.world.helpers.*;
import utils.Constants;
import utils.FloatMath;
import utils.Utils;
import utils.IO.KeyboardInput;
import utils.IO.MouseInput;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

public abstract class World implements IWorldRules {

	private CameraHelper cameraHelper;

	private Renderer renderer;
	private final int TIME_SLOWDOWN_MULTIPLIER;
	private KeyboardInput keyboardInput;
	private TestbedGui testbedGui;
	private UpdateHelper updateHelper;
	private LogHelper logHelper;
	private String logDrone;
	private float time;
	private final float airportWidth, airportLength;
	private List<Airport> airports;
	
	/* These are to be directly called in the world classes */
	protected AutopilotModule autopilotModule;
	protected WorldObject[] worldObjects;
	protected Engine gameEngine;
	protected Ground ground;
	protected DroneHelper droneHelper;

	
	public World(int tSM, boolean wantPhysicsEngine, int nbDrones, float airportWidth, float airportLength) {
		this.cameraHelper = new CameraHelper();

		this.TIME_SLOWDOWN_MULTIPLIER = tSM;
		
		this.airportWidth = airportWidth;
		this.airportLength = airportLength;

		this.keyboardInput = new KeyboardInput();

		this.testbedGui = new TestbedGui();

		this.droneHelper = new DroneHelper(wantPhysicsEngine, nbDrones, testbedGui);
	}

	
	public World(int tSM, boolean wantPhysicsEngine, int nbDrones) {
		this(tSM, wantPhysicsEngine, nbDrones, Constants.DEFAULT_AIRPORT_WIDTH, Constants.DEFAULT_AIRPORT_LENGTH);
	}
	
	/**
	 * Generate autopilotmodule
	 */
	public abstract void setupAutopilotModule();
	
	/**
	 * Generate airports
	 */
	public abstract void setupAirports();
	
	/**
	 * Generate drones and autopilot
	 */
	public abstract void setupDrones();
	
	/**
	 * Generate cubes
	 */
	public abstract void setupWorld();

	public abstract String getDescription();
	
	
	@Override
    public void init(Window window, Engine engine) throws Exception {
    	this.gameEngine = engine; 
    	this.time = 0;
    	
    	this.airports = new ArrayList<Airport>();
    	this.worldObjects = new WorldObject[0]; 
    	
    	setupAutopilotModule();
    	
    	if (autopilotModule != null)
    		autopilotModule.defineAirportParams(this.airportLength, this.airportWidth);
    	
    	setupAirports();
    	
    	if (autopilotModule != null)
    		for (Airport port: airports) {
    			autopilotModule.defineAirport(port.getPosition().x, port.getPosition().z,
    									  port.getDirection().x, port.getDirection().z);
    		}
    	
    	setupDrones();
    	setupWorld();
    	
    	try {
			this.renderer = new Renderer();
		    this.renderer.init(window);
		} catch (Exception e) {
		    System.out.println("Abstract class World (render.init(window)) gave this error: " + e.getMessage());
		    e.printStackTrace();
		}
    	
		testbedGui.showGUI();

		this.updateHelper = new UpdateHelper(droneHelper, TIME_SLOWDOWN_MULTIPLIER, cameraHelper,
											 worldObjects, autopilotModule, testbedGui);
    }
	
	public void nextFollowDrone() {
		updateHelper.nextFollowDrone();
	}

	
	public void addDrone(String droneId, int airportId, int gate, int facing) {
		addDrone(Utils.createDefaultConfig(droneId), airportId, gate, facing);
	}
	
	public void addDrone(AutopilotConfig config, int airportId, int gate, int facing) {
		Airport port = airports.get(airportId);
		Vector3f pos = port.getPosition().add(port.getPerpDirection()
						.mul(port.getWidth()/2 * (gate == 0? 1: -1), new Vector3f()),new Vector3f());
		pos.y += config.getTyreRadius() - config.getWheelY();
		
		float heading = FloatMath.atan2(-port.getDirection().x, -port.getDirection().z);
		heading += (facing == 0? 0: FloatMath.PI * (heading > 0? -1: 1));
		
		droneHelper.addDrone(config, pos, new Vector3f(), heading, airports);
		
		if (autopilotModule != null)
			autopilotModule.defineDrone(airportId, gate, facing, config);
	}
	
	@Deprecated
	public void addDrone(AutopilotConfig config, Vector3f startPos, Vector3f startVel, float startHeading) {
		droneHelper.addDrone(config, startPos, startVel, startHeading, airports);
	}

	
	public void addAirport(Vector3f position, float heading) {
		this.airports.add(new Airport(airportWidth, airportLength, position, heading));
	}
	
	
	public void initLogging(String droneId) {
		this.logHelper = new LogHelper();
		this.logDrone = droneId;
	}
	
	
	@Override
	public void input(Window window, MouseInput mouseInput) {
		keyboardInput.worldInput(cameraHelper.getCameraInc(), window, renderer, this);
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
		
		if (autopilotModule != null)
			autopilotModule.simulationEnded();
		
		if (logHelper != null)
			logHelper.close();
	}
}
