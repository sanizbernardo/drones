package autopilot.airports;

import interfaces.*;
import org.joml.Vector3f;
import utils.FloatMath;

import java.util.ArrayList;
import java.util.List;

public class AirportManager implements AutopilotModule{

    public AirportManager() {
        airportlist = new ArrayList<>();
        droneList = new ArrayList<>();
        packagelist = new ArrayList<>();
    }

    private float length;
    private float width;

    private List<VirtualAirport> airportlist;
    private List<VirtualDrone> droneList;
    private List<VirtualPackage> packagelist;

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
    }

    @Override
    public void startTimeHasPassed(int drone, AutopilotInputs inputs) {
        droneList.get(drone).setInputs(inputs);
        droneList.get(drone).calcOutputs();
    }

    @Override
    public AutopilotOutputs completeTimeHasPassed(int drone) {
        return droneList.get(drone).getOutputs();
    }

    @Override
    public void deliverPackage(int fromAirport, int fromGate, int toAirport, int toGate) {
        VirtualDrone drone = chooseBestDrone();
        drone.getPilot().fly(drone.getInputs(), airportlist.get(fromAirport), fromGate, airportlist.get(toAirport), toGate);
    }

    @Override
    public void simulationEnded() {
        for (VirtualDrone drone : droneList) {
            drone.endSimulation();
        }
    }
}
