package pilot;

import pilot.fly.FlyPilot;

import java.util.ArrayList;

import PathFinding.IPath;
import gui.AutopilotGUI;
import interfaces.Autopilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Path;
import utils.FloatMath;
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
	
	public Pilot(int[] tasks) {
		this.pilots = new PilotPart[4];
		
		this.pilots[TAKING_OFF] = new TakeOffPilot(100);
		this.pilots[FLYING] = new FlyPilot();
		this.pilots[LANDING] = new LandingPilot();
		this.pilots[TAXIING] = new TaxiPilot();
		this.tasks = tasks;
		this.index = 0;
	}
	
	ArrayList<float[]> points;
	
	@Override
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		this.gui = new AutopilotGUI(config);
		this.gui.showGUI();
		this.path = null;
		
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
		
		if (this.index >= this.tasks.length)
			return Utils.buildOutputs(0, 0, 0, 0, 0, 0, 0, 0);

		if (state() == WAIT_PATH) {
			if (this.path != null) {
				for (int i = 0; i < path.getX().length; i++) {
					System.out.println(path.getX()[i] + " " + path.getY()[i] + " " + path.getZ()[i]);
				}
				
				float[] start = new float[] {inputs.getX(), inputs.getY(), inputs.getZ()};
				IPath padplanner = new IPath(path, 0.1053f, 0.1095f, 1145.8f, start, inputs.getHeading());
				points = padplanner.getPathArray();
				
				//TODO change states based on pad
				
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
	
	
	public static float getTakeoffDist(float height) {
		if (height > 150)
			return 343.0420f + 3.0874f * height;

		if (height > 50 )
			return 254.0966f + 3.5519f * height;
		
		return Float.NaN;
	}

}
