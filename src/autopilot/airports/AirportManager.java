package autopilot.airports;

import interfaces.*;

import org.joml.Vector3f;

import autopilot.Pilot;
import autopilot.gui.AutopilotGUI;
import utils.FloatMath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class AirportManager implements AutopilotModule{

    private float length;
    private float width;

    private List<VirtualAirport> airportlist;
    private List<VirtualDrone> droneList;
    
    private AutopilotGUI gui;
	
    public AirportManager() {
        airportlist = new ArrayList<>();
        droneList = new ArrayList<>();
    }

    private enum Loc {
    	GATE_0, GATE_1, LANE_0, LANE_1;
    }
    
    public VirtualDrone chooseBestDrone() {
        for (VirtualDrone drone : droneList) {
            if (!drone.isActive())
                return drone;
        }
        return null;
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
        airportlist.add(new VirtualAirport(position, heading, width));
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
    }

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
    	gui.addPackage(pack);
    	
    	VirtualDrone drone = chooseBestDrone();
    	pack.assignDrone(drone);
        VirtualAirport currentAirport = airportlist.stream()
        		                                   .filter((a) -> Pilot.onAirport(drone.getPosition(), a))
        		                                   .collect(Collectors.toList()).get(0);
        
        int dispatchHeight = 50;
        
        if(onAirport(drone.getPosition(), currentAirport) == Loc.GATE_0) {
            drone.getPilot().fly(drone.getInputs(), currentAirport, 0, 
            		                                airportlist.get(fromAirport), fromGate, 
            		                                airportlist.get(toAirport), toGate,
            		                                dispatchHeight);
        } else {
            drone.getPilot().fly(drone.getInputs(), currentAirport, 1, 
            		                                airportlist.get(fromAirport), fromGate, 
            		                                airportlist.get(toAirport), toGate,
            		                                dispatchHeight);
        }
        
        drone.setActive(true);
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
