package pilot;

import pilot.fly.FlyPilot;

import org.joml.Vector3f;

import gui.AutopilotGUI;
import interfaces.Autopilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Path;
import utils.Utils;

public class Pilot implements Autopilot {
	
	public static final int TAKING_OFF = 0, 
							 LANDING = 1,
							 FLYING = 2,
							 TAXIING = 3,
							 WAIT_PATH = -1;
	
	private int index;
	private final int[] tasks;
	
	private AutopilotGUI gui;
	
	private PilotPart[] pilots;
	
	private Path path;
	
	private AutopilotConfig config;
	
	public Pilot(int[] tasks) {
		this.pilots = new PilotPart[4];
		
		this.pilots[TAKING_OFF] = new TakeOffPilot(100);
		this.pilots[FLYING] = new FlyPilot(null);
		this.pilots[LANDING] = new LandingPilot();
		this.pilots[TAXIING] = new TaxiPilot(new Vector3f());
		this.tasks = tasks;
		this.index = 0;
	}
		
	@Override
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		this.gui = new AutopilotGUI(config);
		this.gui.showGUI();
		this.path = null;
		this.config = config;
		
		for (PilotPart pilot: this.pilots) {
			pilot.initialize(config);
		}
		
		return timePassed(inputs);
	}
	

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {
		this.gui.updateImage(inputs.getImage());
		
		if (this.gui.manualControl())
			return this.gui.getOutputs();
		
		if (this.index >= this.tasks.length) {
			this.gui.setTask("Done");
			return Utils.buildOutputs(0, 0, 0, 0, 0, 0, 0, 0);
		}

		if (state() == WAIT_PATH) {
			if (this.path != null) {
				
				Vector3f[] cubes = pathToCubes(this.path);
				
				this.pilots[TAKING_OFF] = new TakeOffPilot(cubes[0].y-10);
				this.pilots[FLYING] = new FlyPilot(cubes);
				this.pilots[TAXIING] = new TaxiPilot(new Vector3f(inputs.getX(), inputs.getY(), inputs.getZ()));
				
				this.pilots[TAKING_OFF].initialize(this.config);
				this.pilots[FLYING].initialize(this.config);
				this.pilots[TAXIING].initialize(this.config);
				
				this.index += 1;
			}
			return Utils.buildOutputs(0, 0, 0, 0, 0, 0, 0, 0);
		}
		
		this.gui.setTask(currentPilot().taskName());
		
		AutopilotOutputs output = currentPilot().timePassed(inputs);
		
		if (currentPilot().ended()) {
			currentPilot().close();
			this.pilots[state()] = null;
			
			this.index ++;
		}
		
		this.gui.updateOutputs(output);
		return output;
	}


	@Override
	public void simulationEnded() {
		for (PilotPart pilot: this.pilots) {
			if (pilot != null) {
				pilot.close();
			}
		}
		
		this.gui.dispose();
	}
	
	@Override
	public void setPath(Path path) {
		this.path = path;
	}
	
	private int state() {
		return this.tasks[this.index];
	}
	
	private PilotPart currentPilot() {
		return this.pilots[state()];
	}
	
	
	private Vector3f[] pathToCubes(Path path) {
		Vector3f[] cubes = new Vector3f[path.getX().length];
		
		for (int i = 0; i < path.getX().length; i++) {
			cubes[i] = new Vector3f(path.getX()[i], path.getY()[i], path.getZ()[i]);
		}
		
		return cubes;
	}
	
	
	public static float getTakeoffDist(float height) {
		if (height > 150)
			return 343.0420f + 3.0874f * height;

		if (height > 50 )
			return 254.0966f + 3.5519f * height;
		
		return Float.NaN;
	}

}
