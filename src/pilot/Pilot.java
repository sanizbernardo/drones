package pilot;

import interfaces.Autopilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

public class Pilot implements Autopilot {
	
	private static final int STATE_TAKING_OFF = 0, 
							 STATE_LANDING = 1,
							 STATE_FLYING = 2,
							 STATE_TAXIING = 3;
	
	private int index;
	private final int[] order;
	
	private PilotPart[] pilots;
	
	
	public Pilot() {
		this.pilots = new PilotPart[4];
		
		this.pilots[STATE_TAKING_OFF] = new TakeOffPilot();
		this.pilots[STATE_LANDING] = new LandingPilot();
		this.pilots[STATE_FLYING] = new FlyPilot();
		this.pilots[STATE_TAXIING] = new TaxiPilot();
		
		this.order = new int[] {STATE_TAKING_OFF, STATE_FLYING, STATE_LANDING, STATE_TAXIING};
		this.index = 0;
	}
	
	
	@Override
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		for (PilotPart pilot: this.pilots) {
			pilot.initialize(config);
		}
		
		return timePassed(inputs);
	}
	

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {
		AutopilotOutputs output = currentPilot().timePassed(inputs);
		
		if (currentPilot().ended()) {
			currentPilot().close();
			this.pilots[state()] = null;
			
			this.index ++;
						
			if (this.index == this.order.length);
				// TODO: fix dit
		}
		
		return output;
	}

	@Override
	public void simulationEnded() {
		for (PilotPart pilot: this.pilots) {
			if (pilot != null) {
				pilot.close();
			}
		}
		
	}
	
	private int state() {
		return this.order[this.index];
	}
	
	private PilotPart currentPilot() {
		return this.pilots[state()];
	}

}
