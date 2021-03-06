package autopilot.airports;

import org.joml.Vector3f;
import utils.FloatMath;

public class VirtualAirport {

    public VirtualAirport(int id, Vector3f position, float heading, float width, float length) {
        this.id = id;
    	this.position = position;
        this.heading = heading;
        this.width = width;
        this.length = length;

        Vector3f direction = new Vector3f(-FloatMath.sin(heading), 0, -FloatMath.cos(heading));
        Vector3f directionPerp = new Vector3f(-FloatMath.cos(heading), 0, FloatMath.sin(heading));

        this.tarmacs = new Vector3f[] {
                position.add(direction.mul(width/2f, new Vector3f()), new Vector3f()),
                position.sub(direction.mul(width/2f, new Vector3f()), new Vector3f())};

        this.gates = new Vector3f[] {
                position.add(directionPerp.mul(width/2f, new Vector3f()), new Vector3f()),
                position.sub(directionPerp.mul(width/2f, new Vector3f()), new Vector3f())};
    }

    private Vector3f position;
    private Vector3f[] tarmacs, gates;
    private float heading;  
    private float width, length;
    private int id;

    public int getId() {
    	return this.id;
    }

    public Vector3f getTarmac(int i) {
        return tarmacs[i];
    }

    public Vector3f getGate(int i) {
        return gates[i];
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getHeading() {
        return  heading;
    }

	public float getWidth() {
		return width;
	}
	
	public float getLength(){
		return length;
	}
    
}
