package autopilot.airports;

import interfaces.*;

import org.joml.Vector3f;

import autopilot.Pilot;
import autopilot.gui.AutopilotGUI;
import autopilot.pilots.LandingPilot;
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
        
        droneList.add(new VirtualDrone(position, heading, config, this));
        
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
    	gui.addPackage(pack);
		transportQueue.add(pack);
    	pack.setStatus("In queue");
    }
    
    /**
     * Checks if we need to assign a drone that is idling or
     * if a drone picked up a packet.
     */
    private void handleTransportEvents() {
    	forcedPickupSchedule();
    	regularSchedule();
    }

    /**
     * A regular schedule cycle should do this
     */
	private void regularSchedule() {
		// if there wasn't a drone that picked up a package already, do a simple schedule
    	if(!transportQueue.isEmpty() && getReadyDrones().size() > 0) {
    		ArrayList<VirtualPackage> schedPack = new ArrayList<>();
    		
    		for(VirtualPackage pack : transportQueue) {
    			VirtualDrone drone = chooseBestDrone(pack.getFromAirport(), pack.getFromGate());
    	    	if(drone == null) continue;
    	    	
    	    	schedPack.add(pack);
    	    	pack.assignDrone(drone);
    	    	drone.setPackage(pack);
    	    	
    			ArrayList<VirtualAirport> currentAirportList = (ArrayList<VirtualAirport>) airportlist.stream()
    			 		                                   .filter((a) -> Pilot.onAirport(drone.getPosition(), a))
    			 		                                   .collect(Collectors.toList());

    			if(currentAirportList.size() >= 1) {
    				VirtualAirport currentAirport = currentAirportList.get(0);
    				
    				//get a slice from the set
    				int currentSlice = (droneList.indexOf(drone)*10)+MIN_HEIGHT; 
    				
    				// when a drone has a pilot, it is considered active
    				drone.setPilot(new Pilot(drone, this));
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
    			} else { //this is a big time error case but I'm brute force checking it because it randomly rarely happens
    				transportQueue.add(pack);
    				drone.setPackage(null);
    				pack.setStatus("In queue*");
    			}
    		}
    		
    		transportQueue.removeAll(schedPack);
    		
    	}
	}

	/**
	 * When a drone is forced to pickup a packet that was already 
	 * present on the airport it will need the this schedule so our
	 * system remains consistent with the testbed.
	 */
	private void forcedPickupSchedule() {
    	ArrayList<VirtualPackage> deletionList = new ArrayList<>();
    	
    	// assign the package to a drone that already picked it up
    	for(VirtualPackage pack : transportQueue) {
    		for(VirtualDrone vDrone : droneList) {
    			if(vDrone.isActive()) continue;
    			//where is this drone?
    			ArrayList<VirtualAirport> currentAirports = getDroneAirports(vDrone);
    			if(currentAirports.size() == 0) continue;
    			
    			VirtualAirport currentAirport = currentAirports.get(0);
    			if(currentAirport == null) continue;

    			Loc location = whereOnAirport(vDrone.getPosition(), currentAirport);
    			int gate = location == Loc.GATE_0 ? 0 : 1;
    			
    			if(airportlist.indexOf(currentAirport) == pack.getFromAirport() && pack.getFromGate() == gate) {
    				
    				pack.assignDrone(vDrone);
    				vDrone.setPackage(pack);
    				
    				
    				vDrone.setPilot(new Pilot(vDrone, this));
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
	}
    
    /**
     * Returns a list of VirtualDrones that are ready for a new task.
     * In our current logic that means they do not have a Pilot assigned
     * to them.
     */
    private ArrayList<VirtualDrone> getReadyDrones() {
    	return (ArrayList<VirtualDrone>) droneList.stream()
    			                                  .filter(d -> !d.isActive())
    			                                  .collect(Collectors.toList());
    }
    
    /**
     * Returns the airports* that the drone is currently on. This is returned
     * as a list because theoretically this could be multiple airports.
     * @param vDrone the drone we're searching airports for
     * @return an ArrayList of airports the drone is on
     */
    private ArrayList<VirtualAirport> getDroneAirports(VirtualDrone vDrone) {
    	return (ArrayList<VirtualAirport>) airportlist.stream()
                									  .filter((a) -> Pilot.onAirport(vDrone.getPosition(), a))
                                                      .collect(Collectors.toList());
    }
    
    public VirtualDrone chooseBestDrone(int airport, int gate) {
    	
    	// is there already a drone going there? fok off
    	for(VirtualDrone vDrone : droneList) {
    		if(vDrone.getPackage() == null) continue;
    		if(vDrone.getPackage().getToAirport() == airport 
    		   && vDrone.getPackage().getToGate() == gate) {
    			return null;
    		}
    	}
    	
    	
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
	
	public boolean onFullAirport(Vector3f pos, VirtualAirport airport) {
		Vector3f diff = pos.sub(airport.getPosition(), new Vector3f());
		float len = diff.dot(new Vector3f(-FloatMath.sin(airport.getHeading()), 0, -FloatMath.cos(airport.getHeading())));
		float wid = diff.dot(new Vector3f(-FloatMath.cos(airport.getHeading()), 0, FloatMath.sin(airport.getHeading())));
		
		if (Math.abs(len) > airport.getWidth() / 2 + airport.getLength()) {	
			return false;
		} else if (Math.abs(wid) > airport.getWidth()) {
			return false;
		}
		else return true;
		
	}
	
	public boolean checkAirport(VirtualAirport airport, int gate){
		Loc loc;
		if(gate == 0) loc = Loc.GATE_0;
		else loc = Loc.GATE_1;
		for(VirtualDrone drone : droneList){
			Pilot dronePilot = drone.getPilot();
			Vector3f dronePos = drone.getPosition();
			if(onFullAirport(dronePos, airport) && (whereOnAirport(dronePos, airport) == loc || whereOnAirport(dronePos, airport) == Loc.LANE_0 || whereOnAirport(dronePos, airport) == Loc.LANE_1)){
				return false;
			}
			else if(dronePilot != null && dronePilot.currentPilot() instanceof LandingPilot && ((LandingPilot) dronePilot.currentPilot()).getCurrentDestionationAirport() == airport){
				return false;
			}
			
		}
		return true;
	}

    @Override
    public void simulationEnded() {
        for (VirtualDrone drone : droneList) {
            drone.endSimulation();
        }
        gui.dispose();
    }
   
}
