package testbed.entities.packages;

public class Package {

	public static final int WAITING = 0,
							IN_PROGRESS = 1,
							DELIVERED = 2;
	
	private final int from, dest;
	
	private final int fromGate, destGate;
	
	private int status;
	
	public Package(int from, int dest, int fromGate, int destGate) {
		this.from = from;
		this.dest = dest;
		
		this.fromGate = fromGate;
		this.destGate = destGate;
		
		this.status = WAITING;
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
	
	public int getStatus() {
		return this.status;
	}
	
	public void pickUp() {
		this.status = IN_PROGRESS;
	}
	
	public void deliver() {
		this.status = DELIVERED;
	}

	public String getStatusDesc() {
		switch (this.status) {
		case WAITING:
			return "Waiting";

		case IN_PROGRESS:
			return "In progress";
			
		case DELIVERED:
			return "Delivered";
			
		default:
			return "";
		}
	}
}
