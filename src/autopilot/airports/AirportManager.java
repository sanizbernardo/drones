package autopilot.airports;

import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotModule;
import interfaces.AutopilotOutputs;
import org.joml.Vector3f;
import utils.FloatMath;

import java.util.ArrayList;
import java.util.List;

public class AirportManager implements AutopilotModule{

    public AirportManager() {
        airportlist = new ArrayList<VirtualAirport>();
        droneList = new ArrayList<VirtualDrone>();
    }

    private float length;
    private float width;

    private List<VirtualAirport> airportlist;
    private List<VirtualDrone> droneList;

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
        VirtualDrone current = droneList.get(drone);
    }

    @Override
    public AutopilotOutputs completeTimeHasPassed(int drone) {
        VirtualDrone current = droneList.get(drone);
        return null;
    }

    @Override
    public void deliverPackage(int fromAirport, int fromGate, int toAirport, int toGate) {

    }

    @Override
    public void simulationEnded() {

    }
}
