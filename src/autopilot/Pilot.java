package autopilot;

import org.joml.Vector3f;

import autopilot.airports.VirtualAirport;
import autopilot.pilots.FlyPilot;
import autopilot.pilots.HandbrakePilot;
import autopilot.pilots.LandingPilot;
import autopilot.pilots.TakeOffPilot;
import autopilot.pilots.TaxiPilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import utils.FloatMath;
import utils.Utils;

public class Pilot {
	
	public static final int TAKING_OFF = 0, 
							 LANDING = 1,
							 FLYING = 2,
							 TAXIING = 3,
							 HANDBRAKE = 4;
	
	private int index;
	private int[] tasks;
		
	private PilotPart[] pilots;
	
	private AutopilotConfig config;
		
	public Pilot() {
		this.tasks = new int[] {};
		this.pilots = new PilotPart[5];
	}
	
	public void simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		this.config = config;
	}
	
	private void init() {
		for (PilotPart pilot: this.pilots) {
			pilot.initialize(config);
		}
	}
	
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {
		
		if (this.index >= this.tasks.length) {
			return Utils.buildOutputs(0, 0, 0, 0, 0, 0, 0, 0);
		}
		
		AutopilotOutputs output = currentPilot().timePassed(inputs);
		
		if (currentPilot().ended()) {
			currentPilot().close();
			this.pilots[state()] = null;
			
			this.index ++;
		}
		
		return output;
	}

	public void fly(AutopilotInputs inputs, VirtualAirport fromAirport, int fromGate, VirtualAirport toAirport, int toGate) {
		
		if(config == null) throw new RuntimeException("Access before sim started");
		
		this.index = 0;
		
		int FLY_HEIGHT = 50; //to be given by airportManager
	
		this.pilots[TAKING_OFF] = new TakeOffPilot(FLY_HEIGHT);
		this.pilots[LANDING] = new LandingPilot(toAirport);
		this.pilots[FLYING] = new FlyPilot(toAirport);
		this.pilots[TAXIING] = new TaxiPilot(fromAirport.getGate(fromGate), fromGate == 0 ? 
								                                                 fromAirport.getHeading() 
								                                                 : 
								                                                 fromAirport.getHeading() + FloatMath.PI * (fromAirport.getHeading() < 0 ? 1 : -1));
		this.pilots[HANDBRAKE] = new HandbrakePilot();
		
		this.init();
		
		this.tasks = new int[] {TAXIING, TAKING_OFF, FLYING, LANDING};
	}

	
	public void simulationEnded() {
		for (PilotPart pilot: this.pilots) {
			if (pilot != null) {
				pilot.close();
			}
		}
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
