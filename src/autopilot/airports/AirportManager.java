package autopilot.airports;

import interfaces.*;

import org.joml.Vector3f;

import autopilot.Pilot;
import autopilot.gui.AutopilotGUI;
import utils.FloatMath;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class AirportManager implements AutopilotModule{

    private float length;
    private float width;
    private int MIN_HEIGHT = 50;

    private List<VirtualAirport> airportlist;
    private List<VirtualDrone> droneList;
    private Queue<VirtualPackage> transportQueue;
    
    private AutopilotGUI gui;
	
    public AirportManager() {
        airportlist = new ArrayList<>();
        droneList = new ArrayList<>();
        transportQueue = new LinkedList<VirtualPackage>();
    }

    private enum Loc {
    	GATE_0, GATE_1, LANE_0, LANE_1;
    }

    @Override
    public void defineAirportParams(float length, float width) {
        this.length = length;
        this.width = width;
    }

    @Override
    public void defineAirport(float centerX, float centerZ, float centerToRunway0X, float centerToRunway0Z) {
        Vector3f position = new Vector3f(centerX, 0, centerZ);
        float heading = FloatMath.atan2(-centerToRunway0X, -centerToRunway0Z);

        airportlist.add(new VirtualAirport(airportlist.size(), position, heading, width, length));
    }

    @Override
    public void defineDrone(int airport, int gate, int pointingToRunway, AutopilotConfig config) {
        VirtualAirport chosen = airportlist.get(airport);
        Vector3f position = chosen.getGate(gate);
        float heading = chosen.getHeading();
        heading += (pointingToRunway == 0? 0: FloatMath.PI * (heading > 0? -1: 1));
                
        droneList.add(new VirtualDrone(position, heading, config));
        
        if (droneList.size() == 1) {
        	gui = new AutopilotGUI(droneList);
        	gui.showGUI();
        } else
        	gui.updateDrones();
    }

    @Override
    public void startTimeHasPassed(int drone, AutopilotInputs inputs) {
        droneList.get(drone).setInputs(inputs);
        droneList.get(drone).calcOutputs();
        handleTransportEvents();
    }
;
    @Override
    public AutopilotOutputs completeTimeHasPassed(int drone) {
        if (drone ==  droneList.size() - 1)
        	gui.updateOutputs();
        	
    	if (gui.manualControl(drone))
        	return gui.getOutputs();
        else
        	return droneList.get(drone).getOutputs();
    }

    @Override
    public void deliverPackage(int fromAirport, int fromGate, int toAirport, int toGate) {
    	VirtualPackage pack = new VirtualPackage(fromAirport, fromGate, toAirport, toGate);
    	pack.setStatus("In queue");
    	gui.addPackage(pack);
    	transportQueue.add(pack);
    }
    
    private void handleTransportEvents() {
    	// fetch all the active drones
    	ArrayList<VirtualDrone> actives = new ArrayList<>();
    	for(VirtualDrone vDrone : droneList) {
    		if(vDrone.isActive()) {
    			actives.add(vDrone);
    		}
    	}
    	
    	ArrayList<VirtualPackage> deletionList = new ArrayList<>();
    	
    	// assign the package to a drone that already picked it up
    	for(VirtualPackage pack : transportQueue) {
    		for(VirtualDrone vDrone : actives) {
    			//where is this drone?
    			VirtualAirport currentAirport = airportlist.stream()
                         .filter((a) -> Pilot.onAirport(vDrone.getPosition(), a))
                         .collect(Collectors.toList()).get(0);
    			
    			Loc location = whereOnAirport(vDrone.getPosition(), currentAirport);
    			int gate = location == Loc.GATE_0 ? 0 : 1;
    			
    			if(currentAirport == null) continue;
    			
    			if(airportlist.indexOf(currentAirport) == pack.getFromAirport() && pack.getFromGate() == gate) {
    				
    				pack.assignDrone(vDrone);
    				vDrone.setPackage(pack);
    				
    				
    				vDrone.setPilot(new Pilot(vDrone));
    				vDrone.getPilot().simulationStarted(vDrone.getConfig(), vDrone.getInputs());
    				
    				int currentSlice = (droneList.indexOf(vDrone)*10)+MIN_HEIGHT; 
    				
    				vDrone.getPilot().fly(vDrone.getInputs(), currentAirport, 0, 
                             airportlist.get(pack.getFromAirport()), pack.getFromGate(), 
                             airportlist.get(pack.getToAirport()), pack.getToGate(),
                             currentSlice);
    				
    				deletionList.add(pack);
    			}
    		}
    	}
    	
    	// these packages shouldn't be scheduled normally and need to be handled first
    	for(VirtualPackage pack : deletionList) {
    		transportQueue.remove(pack);
    	}
    	
    	// if there wasn't a drone that picked up a package already, do a simple schedule
    	if(!transportQueue.isEmpty()) {
    		VirtualPackage pack = transportQueue.peek();
	    	VirtualDrone drone = chooseBestDrone(pack.getFromAirport(), pack.getFromGate());
	    	if(drone == null) return;
	    	
	    	transportQueue.poll();
	    	pack.assignDrone(drone);
	    	drone.setPackage(pack);
	    	
			VirtualAirport currentAirport = airportlist.stream()
			 		                                   .filter((a) -> Pilot.onAirport(drone.getPosition(), a))
			 		                                   .collect(Collectors.toList()).get(0);

			//get a slice from the set
			int currentSlice = (droneList.indexOf(drone)*10)+MIN_HEIGHT; 
			
			// when a drone has a pilot, it is considered active
			drone.setPilot(new Pilot(drone));
			drone.getPilot().simulationStarted(drone.getConfig(), drone.getInputs());	
			
			if(whereOnAirport(drone.getPosition(), currentAirport) == Loc.GATE_0) {
			     drone.getPilot().fly(drone.getInputs(), currentAirport, 0, 
			     		                                airportlist.get(pack.getFromAirport()), pack.getFromGate(), 
			     		                                airportlist.get(pack.getToAirport()), pack.getToGate(),
			     		                                currentSlice);
		    } else {
			     drone.getPilot().fly(drone.getInputs(), currentAirport, 1, 
			     		                                airportlist.get(pack.getFromAirport()), pack.getFromGate(), 
			     		                                airportlist.get(pack.getToAirport()), pack.getToGate(),
			     		                                currentSlice);
			 }
			 
    	}
    }
    
    public VirtualDrone chooseBestDrone(int airport, int gate) {
    	//choose a drone that is not active AND is on the same gate
    	Loc gateLoc = (gate == 0) ? Loc.GATE_0 : Loc.GATE_1;
        for (VirtualDrone drone : droneList) {
            if (!drone.isActive() 
            		&& Pilot.onAirport(drone.getPosition(), airportlist.get(airport)) 
            		&& this.whereOnAirport(drone.getPosition(), airportlist.get(airport)) == gateLoc)
                return drone;
        }
    	
    	
    	//choose a drone that is not active AND is on the same airport
        for (VirtualDrone drone : droneList) {
            if (!drone.isActive() && Pilot.onAirport(drone.getPosition(), airportlist.get(airport)))
                return drone;
        }
    	
    	//if we can't find a drone that is already on that airport, pick a random non-active one
        for (VirtualDrone drone : droneList) {
            if (!drone.isActive())
                return drone;
        }
        return null;
    }
    
	private Loc whereOnAirport(Vector3f pos, VirtualAirport airport) {
		Vector3f diff = pos.sub(airport.getPosition(), new Vector3f());
		float len = diff.dot(new Vector3f(-FloatMath.sin(airport.getHeading()), 0, -FloatMath.cos(airport.getHeading())));
		float wid = diff.dot(new Vector3f(-FloatMath.cos(airport.getHeading()), 0, FloatMath.sin(airport.getHeading())));
		
		if (len > airport.getWidth() / 2)
			return Loc.LANE_0;
		else if (len < - airport.getWidth() / 2)
			return Loc.LANE_1;
		else if (wid > 0)
			return Loc.GATE_0;
		else
			return Loc.GATE_1;
	}

    @Override
    public void simulationEnded() {
        for (VirtualDrone drone : droneList) {
            drone.endSimulation();
        }
        gui.dispose();
    }
   
}
