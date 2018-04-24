package autopilot;

import java.util.Arrays;

import org.joml.Vector3f;

import autopilot.airports.VirtualAirport;
import autopilot.pilots.FlyPilot;
import autopilot.pilots.HandbrakePilot;
import autopilot.pilots.LandingPilot;
import autopilot.pilots.TakeOffPilot;
import autopilot.pilots.TaxiPilot;
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
							 HANDBRAKE = 4,
							 WAIT_PATH = -1;
	
	private int index;
	private int[] tasks;
		
	private PilotPart[] pilots;
	
	private AutopilotConfig config;
	
	public Pilot(int[] tasks) {
		this.pilots = new PilotPart[5];
		
		this.pilots[TAKING_OFF] = new TakeOffPilot(100);
		this.pilots[FLYING] = new FlyPilot(null);
		this.pilots[LANDING] = new LandingPilot();
		this.pilots[TAXIING] = new TaxiPilot();
		this.pilots[HANDBRAKE] = new HandbrakePilot();  
		
		this.tasks = tasks;
		this.index = 0;
	}
		
	@Override
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		this.config = config;
		
		for (PilotPart pilot: this.pilots) {
			pilot.initialize(config);
		}
		
		return timePassed(inputs);
	}
	

	@Override
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
		this.index = 0;
		
		int FLY_HEIGHT = 100; //to be given by airportManager

		this.pilots[TAXIING] = new TaxiPilot(Arrays.asList(new Vector3f(fromAirport.getGate(fromGate))));
		this.pilots[TAKING_OFF] = new TakeOffPilot(FLY_HEIGHT);
		this.pilots[FLYING] = new FlyPilot(new Vector3f[] {new Vector3f(0,100,0)});
		this.tasks = new int[] {TAXIING, TAKING_OFF, FLYING};
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
