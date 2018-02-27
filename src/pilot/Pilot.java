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
	
	private static final int STATE_TAKING_OFF = 0, 
							 STATE_LANDING = 1,
							 STATE_FLYING = 2,
							 STATE_TAXIING = 3;
	
	private int index;
	private final int[] order;
	
	private AutopilotGUI gui;
	
	private PilotPart[] pilots;
	
	
	public Pilot() {
		this.pilots = new PilotPart[4];
		
		this.pilots[STATE_TAKING_OFF] = new TakeOffPilot(250);
		this.pilots[STATE_LANDING] = new LandingPilot();
		this.pilots[STATE_FLYING] = new FlyPilot();
		this.pilots[STATE_TAXIING] = new TaxiPilot();
		
		this.order = new int[] {STATE_FLYING};
		this.index = 0;
	}
	
	
	@Override
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		this.gui = new AutopilotGUI(config);
		this.gui.showGUI();
		
		for (PilotPart pilot: this.pilots) {
			pilot.initialize(config);
		}
		
		return timePassed(inputs);
	}
	

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {
		if (this.gui.manualControl())
			return this.gui.getOutputs();
		
		this.gui.updateImage(inputs.getImage());
		
		if (this.index >= this.order.length)
			return Utils.buildOutputs(0, 0, 0, 0, 0, 0, 0, 0);

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
		return this.order[this.index];
	}
	
	private PilotPart currentPilot() {
		return this.pilots[state()];
	}


	@Override
	public void setPath(Path path) {

	}

}
