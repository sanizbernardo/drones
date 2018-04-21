package testbed.entities.packages;

public class Package {

	private final int from, dest;
	
	private final int fromGate, destGate;
	
	public Package(int from, int dest, int fromGate, int destGate) {
		this.from = from;
		this.dest = dest;
		
		this.fromGate = fromGate;
		this.destGate = destGate;
	}
	
	public Package(int[] details) {
		this.from = details[0];
		this.dest = details[2];
		
		this.fromGate = details[1];
		this.destGate = details[3];
	}
	
	
	public int getFromAirport() {
		return this.from;
	}
	
	public int getDestAirport() {
		return this.dest;
	}
	
	public int getFromGate() {
		return this.fromGate;
	}
	
	public int getDestGate() {
		return this.destGate;
	}
}
