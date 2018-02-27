package world;

import entities.ground.Ground;
import entities.tarmac.Tarmac;
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
import world.helpers.DroneHelper;
import world.helpers.UpdateHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.joml.Vector3f;

public abstract class World implements IWorldRules {

	private CameraHelper cameraHelper;

	private Renderer renderer;
	private ImageCreator imageCreator;
	private int TIME_SLOWDOWN_MULTIPLIER;
	private KeyboardInput keyboardInput;
	private TestbedGui testbedGui;
	private UpdateHelper updateHelper;
	private BufferedWriter writer;
	private float time;

	/* These are to be directly called in the world classes */
	protected Autopilot planner;
	protected WorldObject[] worldObjects;
	protected AutopilotConfig config;
	protected Engine gameEngine;
	protected DroneHelper droneHelper;
	
	protected Ground ground;
	protected Tarmac tarmac;

	public World(int tSM, boolean wantPhysicsEngine) {
		this.cameraHelper = new CameraHelper();

		this.TIME_SLOWDOWN_MULTIPLIER = tSM;

		this.keyboardInput = new KeyboardInput();

		this.testbedGui = new TestbedGui();

		this.droneHelper = new DroneHelper(wantPhysicsEngine, 1);
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
    	
    	setup();
    	
    	try {
			this.renderer = new Renderer(config);
		    this.renderer.init(window);
		} catch (Exception e) {
		    System.out.println("Abstract class World (render.init(window)) gave this error: " + e.getMessage());
		    e.printStackTrace();
		}
		
		this.imageCreator = new ImageCreator(config.getNbColumns(), config.getNbRows(), window);
		
		if (planner != null) {
			Physics physics = droneHelper.getDronePhysics(config.getDroneID());
			planner.simulationStarted(config, Utils.buildInputs(imageCreator.screenShot(),
				physics.getPosition(), physics.getHeading(), physics.getPitch(), physics.getRoll(), 0));
		}
		
		testbedGui.showGUI();

		this.updateHelper = new UpdateHelper(
				droneHelper,
				config,
                TIME_SLOWDOWN_MULTIPLIER,
                cameraHelper,
                worldObjects,
                planner,
                testbedGui,
                imageCreator);
    }

	public void initLogging() {
		try {
			File file = new File("position.log");
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			writer.write("position log, x  y  z  heading  pitch  roll lincl hincl rincl vinlc thrust\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void addDrone(AutopilotConfig config, Vector3f startPos, Vector3f startVel) {
		droneHelper.addDrone(config, startPos, startVel);
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
		this.time += interval;
		
		updateHelper.updateCycle(interval, mouseInput);
		
		if (droneHelper.getNbDrones() == 0) {
			gameEngine.setLoopShouldExit();
			return;
		}
		
		Physics physics = droneHelper.getDronePhysics(config.getDroneID());
		if (writer != null) {
			try {
				writer.write(time + ": " + physics.getPosition().x + " " + physics.getPosition().y + " "
						+ physics.getPosition().z + " " + physics.getHeading() + " " + physics.getPitch() + " "
						+ physics.getRoll() + " " + physics.getLWInclination() + " " + physics.getHSInclination() + " "
						+ physics.getRWInclination() + " " + physics.getVSInclination() + " " + physics.getThrust()
						+ "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void render(Window window) {
		renderer.render(window, cameraHelper, worldObjects, droneHelper, ground, tarmac);
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
		try {
			if (writer != null)
				writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
