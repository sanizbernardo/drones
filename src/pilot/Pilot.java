package pilot;

import pilot.fly.FlyPilot;
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
	
	public Pilot(int[] tasks) {
		this.pilots = new PilotPart[4];
		
		this.pilots[TAKING_OFF] = new TakeOffPilot(500);
		this.pilots[LANDING] = new LandingPilot();
		this.pilots[FLYING] = new FlyPilot();
		this.pilots[TAXIING] = new TaxiPilot();
		this.tasks = tasks;
		this.index = 0;
	}
	
	
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
				//calculate path...
				
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
	
	private int state() {
		return this.tasks[this.index];
	}
	
	private PilotPart currentPilot() {
		return this.pilots[state()];
	}


	@Override
	public void setPath(Path path) {
		this.path = path;
	}

}
