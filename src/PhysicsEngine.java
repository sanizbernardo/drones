import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;

import datatypes.*;

public class PhysicsEngine {
		
	public PhysicsEngine(AutopilotConfig config) {
		
	}
	
	/**
	 * wings are already updated
	 */
	public void update(float dt, Drone drone){
		// position calculation
		
		Vector newpos = new BasicVector();
		
		// acceleration calculation
		
		Vector accceleration = new BasicVector();
		
		// rotation calculation
		
		Vector rotAcceleration = new BasicVector();

		// velocities updates
		
		Vector newvel = new BasicVector();
		Vector newrot = new BasicVector();
		
		// drone update
		drone.setPos(newpos);
		drone.setVel(newvel);
		drone.setRot(newrot);
	}
	
	
	
	
}
