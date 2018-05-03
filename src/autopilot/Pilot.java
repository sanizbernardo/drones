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
							 HANDBRAKE = 4,
							 PICKUP_TAXI = 5,
							 
							 TAKING_OFF_2 = 6, 
							 LANDING_2 = 7,
							 FLYING_2 = 8,
							 TAXIING_2 = 9,
							 HANDBRAKE_2 = 10,
							 PICKUP_TAXI_2 = 11;
	
	private int index;
	private int[] tasks;
		
	private PilotPart[] pilots;
	
	private AutopilotConfig config;
		
	public Pilot() {
		this.tasks = new int[] {};
		this.pilots = new PilotPart[12];
	}
	
	public void simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		this.config = config;
	}
	
	private void init() {
		for (PilotPart pilot: this.pilots) {
			if (pilot != null) pilot.initialize(config);
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

	public void fly(AutopilotInputs inputs, VirtualAirport currentAirport, int currentGate, 
			                                VirtualAirport fromAirport, int fromGate, 
			                                VirtualAirport toAirport, int toGate,
			                                int flyHeight) {
		
		if(config == null) throw new RuntimeException("Access before sim started");
		this.index = 0;

		Vector3f pos = new Vector3f(inputs.getX(),inputs.getY(),inputs.getZ());
		
		if(onAirport(pos, fromAirport)) {
			flyLocal(fromAirport, fromGate, toAirport, toGate, flyHeight);
		} else {
			flyRemote(currentAirport, currentGate, fromAirport, fromGate, toAirport, toGate, flyHeight);
		}
	}
	
	private void flyRemote(VirtualAirport currentAirport, int currentGate, VirtualAirport fromAirport, int fromGate,
			VirtualAirport toAirport, int toGate, int FLY_HEIGHT) {
		
		//first flight
		this.pilots[TAKING_OFF] = new TakeOffPilot(FLY_HEIGHT);
		this.pilots[LANDING] = new LandingPilot(fromAirport);
		this.pilots[FLYING] = new FlyPilot(fromAirport);
		
		this.pilots[TAXIING] = new TaxiPilot(currentAirport.getGate(currentGate), currentGate == 0 ? 
											 currentAirport.getHeading() 
                                             : 
                                             currentAirport.getHeading() + FloatMath.PI * (currentAirport.getHeading() < 0 ? 1 : -1));
		this.pilots[HANDBRAKE] = new HandbrakePilot();
		this.pilots[PICKUP_TAXI] = new TaxiPilot(fromAirport.getGate(fromGate), fromGate == 0 ? 
							                 fromAirport.getHeading() 
							                 : 
							                 fromAirport.getHeading() + FloatMath.PI * (fromAirport.getHeading() < 0 ? 1 : -1));
		
		
		//second flight
		this.pilots[TAKING_OFF_2] = new TakeOffPilot(FLY_HEIGHT);
		this.pilots[LANDING_2] = new LandingPilot(toAirport);
		this.pilots[FLYING_2] = new FlyPilot(toAirport);
		this.pilots[TAXIING_2] = new TaxiPilot(fromAirport.getGate(fromGate), fromGate == 0 ? 
                                             fromAirport.getHeading() 
                                             : 
                                             fromAirport.getHeading() + FloatMath.PI * (fromAirport.getHeading() < 0 ? 1 : -1));
		this.pilots[HANDBRAKE_2] = new HandbrakePilot();
		this.pilots[PICKUP_TAXI_2] = new TaxiPilot(toAirport.getGate(toGate), toGate == 0 ? 
							                 toAirport.getHeading() 
							                 : 
							                 toAirport.getHeading() + FloatMath.PI * (toAirport.getHeading() < 0 ? 1 : -1));
		this.init();
		
		this.tasks = new int[] {TAXIING, TAKING_OFF, FLYING, LANDING, PICKUP_TAXI, HANDBRAKE, TAXIING_2, TAKING_OFF_2, FLYING_2, LANDING_2, PICKUP_TAXI_2, HANDBRAKE_2};
	}
	
	private void flyLocal(VirtualAirport fromAirport, int fromGate,
			VirtualAirport toAirport, int toGate, int FLY_HEIGHT) {
		this.pilots[TAKING_OFF] = new TakeOffPilot(FLY_HEIGHT);
		this.pilots[LANDING] = new LandingPilot(toAirport);
		this.pilots[FLYING] = new FlyPilot(toAirport);
		this.pilots[TAXIING] = new TaxiPilot(fromAirport.getGate(fromGate), fromGate == 0 ? 
                                             fromAirport.getHeading() 
                                             : 
                                             fromAirport.getHeading() + FloatMath.PI * (fromAirport.getHeading() < 0 ? 1 : -1));
		this.pilots[HANDBRAKE] = new HandbrakePilot();
		this.pilots[PICKUP_TAXI] = new TaxiPilot(toAirport.getGate(toGate), toGate == 0 ? 
							                 toAirport.getHeading() 
							                 : 
							                 toAirport.getHeading() + FloatMath.PI * (toAirport.getHeading() < 0 ? 1 : -1));
		
		this.init();
		
		this.tasks = new int[] {TAXIING, TAKING_OFF, FLYING, LANDING, PICKUP_TAXI, HANDBRAKE};
	}
	
	public static boolean onAirport(Vector3f pos, VirtualAirport airport) {
		Vector3f diff = pos.sub(airport.getPosition(), new Vector3f());
		float len = diff.dot(new Vector3f(-FloatMath.sin(airport.getHeading()), 0, -FloatMath.cos(airport.getHeading())));
		float wid = diff.dot(new Vector3f(-FloatMath.cos(airport.getHeading()), 0, FloatMath.sin(airport.getHeading())));
		
		if (Math.abs(len) > airport.getWidth() / 2) {	
			return false;
		} else if (Math.abs(wid) > airport.getWidth()) {
			return false;
		}
		else return true;
		
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
