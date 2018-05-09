package autopilot.airports;

public class TransportEvent {
	
	private int fromAirport, fromGate, toAirport, toGate;

	public TransportEvent(int fromAirport, int fromGate, int toAirport, int toGate) {
		this.fromAirport = fromAirport;
		this.fromGate = fromGate;
		this.toAirport = toAirport;
		this.toGate = toGate;
	}

	public int getFromAirport() {
		return fromAirport;
	}

	public int getFromGate() {
		return fromGate;
	}

	public int getToAirport() {
		return toAirport;
	}

	public int getToGate() {
		return toGate;
	}
}
