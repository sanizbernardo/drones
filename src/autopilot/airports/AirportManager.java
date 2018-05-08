package autopilot.airports;

import interfaces.*;

import org.joml.Vector3f;

import autopilot.Pilot;
import autopilot.gui.AutopilotGUI;
import utils.FloatMath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class AirportManager implements AutopilotModule{

    private float length;
    private float width;
    private int MIN_HEIGHT = 50;

    private List<VirtualAirport> airportlist;
    private List<VirtualDrone> droneList;
    private List<VirtualPackage> packagelist;
    private Set<Integer> airSlices;
    private Queue<TransportEvent> transportQueue;
    
    private AutopilotGUI gui;
    private int droneAmount = 0;
	
    public AirportManager() {
        airportlist = new ArrayList<>();
        droneList = new ArrayList<>();
        packagelist = new ArrayList<>();
        airSlices = new HashSet<>();
        transportQueue = new LinkedList<TransportEvent>();
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
        airportlist.add(new VirtualAirport(position, heading, width, length));
    }

    @Override
    public void defineDrone(int airport, int gate, int pointingToRunway, AutopilotConfig config) {
        VirtualAirport chosen = airportlist.get(airport);
        Vector3f position = chosen.getGate(gate);
        float heading = chosen.getHeading();
        heading += (pointingToRunway == 0? 0: FloatMath.PI * (heading > 0? -1: 1));
        
        airSlices.add(MIN_HEIGHT + droneAmount * 10);
        droneAmount++;
        
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
    	transportQueue.add(new TransportEvent(fromAirport, fromGate, toAirport, toGate));
    }
    
    private void handleTransportEvents() {
    	if(!transportQueue.isEmpty()) {
    		TransportEvent event = transportQueue.peek();
	    	VirtualDrone drone = chooseBestDrone(event.getFromAirport());
	    	
	    	if(drone == null) return;
	    	
	    	transportQueue.poll();
	    	
			VirtualAirport currentAirport = airportlist.stream()
			 		                                   .filter((a) -> Pilot.onAirport(drone.getPosition(), a))
			 		                                   .collect(Collectors.toList()).get(0);
	
			
			//reset all slices
			for(VirtualDrone vDrone : droneList)  {
				int indivSlice = (droneList.indexOf(vDrone)*10)+MIN_HEIGHT;
			 	if(!vDrone.isActive() && !airSlices.contains(indivSlice)) {
			 		airSlices.add(indivSlice);
			 	}
			}
			//get a slice from the set
			int currentSlice = airSlices.iterator().next(); 
			
			// when a drone has a pilot, it is considered active
			drone.setPilot(new Pilot(drone));
			drone.getPilot().simulationStarted(drone.getConfig(), drone.getInputs());	
			
			//make sure no other airplane will take it
			airSlices.remove(currentSlice); 
			 
			if(onAirport(drone.getPosition(), currentAirport) == Loc.GATE_0) {
			     drone.getPilot().fly(drone.getInputs(), currentAirport, 0, 
			     		                                airportlist.get(event.getFromAirport()), event.getFromGate(), 
			     		                                airportlist.get(event.getToAirport()), event.getToGate(),
			     		                                currentSlice);
		    } else {
			     drone.getPilot().fly(drone.getInputs(), currentAirport, 1, 
			     		                                airportlist.get(event.getFromAirport()), event.getFromGate(), 
			     		                                airportlist.get(event.getToAirport()), event.getToGate(),
			     		                                currentSlice);
			 }
			 
    	}
    }
    
    public VirtualDrone chooseBestDrone(int airport) {
    	//choose a drone that is not active AND is on the same gate/airport
        for (VirtualDrone drone : droneList) {
        	System.out.println(Pilot.onAirport(drone.getPosition(), airportlist.get(airport)));
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
    
	private Loc onAirport(Vector3f pos, VirtualAirport airport) {
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
