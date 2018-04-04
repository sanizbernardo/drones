package interfaces;

public interface AutopilotModule {
    
	public void defineAirportParams(float length, float width);
    
    /**
     * (centerToRunway0X, centerToRunway0Z) constitutes a unit vector pointing from the center of the airport towards runway 0
     */
	public void defineAirport(float centerX, float centerZ, float centerToRunway0X, float centerToRunway0Z); 
    
    /**
     * Airport and gate define the drone's initial location, pointingToRunway its initial orientation.
     * The first drone that is defined is drone 0, etc.
     */
	public void defineDrone(int airport, int gate, int pointingToRunway, AutopilotConfig config); 
    
    /**
     * Allows the autopilots for all drones to run in parallel if desired.
     * Called with drone = 0 through N - 1, in that order, if N drones have been defined.
     */
	public void startTimeHasPassed(int drone, AutopilotInputs inputs); 
    
	/**
	 * Called with drone = 0 through N - 1, in that order, if N drones have been defined.
	 */
	public AutopilotOutputs completeTimeHasPassed(int drone); 
    
	/**
     * Informs the autopilot module of a new package delivery request generated by the testbed.
	 */
    public void deliverPackage(int fromAirport, int fromGate, int toAirport, int toGate); 
    
    public void simulationEnded();
}
