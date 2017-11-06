package physics;

import org.joml.*;

import datatypes.AutopilotConfig;

public class Drone {
	
	private final float maxThrust;

	private final float wingX, tailSize;

	private Vector3f position, velocity, orientation, rotation;
	
	private float thrust, leftWingInclination, rightWingInclination, horStabInclination, verStabInclination;

	private float engineZ;

	public Drone(AutopilotConfig config) {
		this(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f),
				new Vector3f(0f, 0f, 0f), config);
	}
	
	public Drone(Vector3f position, Vector3f velocity, Vector3f orientation, AutopilotConfig config) {
		this.setPosition(position);		
		this.setVelocity(velocity);
		this.setOrientation(orientation);
		this.setRotation(new Vector3f(0f, 0f, 0f));

		this.wingX = config.getWingX();
		this.tailSize = config.getTailSize();

		engineZ = config.getTailMass() / config.getEngineMass() * tailSize;


		this.thrust = 0;
		this.leftWingInclination = 0;
		this.rightWingInclination = 0;
		this.horStabInclination = 0;
		this.verStabInclination = 0;
		
		this.maxThrust = config.getMaxThrust();
	}
	
	// position
	public void setPosition(Vector3f pos) {
		this.position = pos;
	}
	
    public Vector3f getPosition() {
    	return position;
    }
    
    
    // velocity    

    public Vector3f getVelocity() {

    	return velocity;
    }
	
	public void setVelocity(Vector3f vel) {
		this.velocity = vel;
	}
	
	
	// rotation    
    public Vector3f getRotation() {
    	return rotation;
    }
    	
	public void setRotation(Vector3f rot) {
		this.rotation = rot;
	}
	
	
	// thrust
    float getThrust() {
    	return thrust;
    }

    public float getMaxThrust() { return  maxThrust; }
    
	public void setThrust(float thrust) {
		if (thrust <= maxThrust && thrust >= 0) 
			this.thrust = thrust;
	}
	
	
	// inclination
    float getLeftWingInclination() {
    	return leftWingInclination;
    }
    
	public void setLeftWingInclination(float inclination) {
		this.leftWingInclination = inclination;
	}
	
	float getRightWingInclination() {
    	return rightWingInclination;
    }
	
	public void setRightWingInclination(float inclination) {
		this.rightWingInclination = inclination;
	}
	
	public float getHorStabInclination() {
    	return horStabInclination;
    }
	
	public void setHorStabInclination(float inclination) {
		this.horStabInclination = inclination;
	}
	
	float getVerStabInclination() {
    	return verStabInclination;
    }
	
	public void setVerStabInclination(float inclination) {
		this.verStabInclination = inclination;
	}
	
	
    // orientations

    public float getPitch() {
    	return (float) orientation.get(0);
    }
    
    public float getHeading() {
    	return 0f;
    }
    
    public float getYaw() {
    	return (float) orientation.get(1);
    }
    
    public float getRoll() {
    	return (float) orientation.get(2);
    }
    
    public Vector3f getOrientation() {
    	return orientation;
    }
    
    public void setOrientation(Vector3f orientation) {
    	this.orientation = orientation;
    }

    // sizes

	public float getWingX() {
		return wingX;
	}

	public float getTailSize() {
		return tailSize;
	}

	public float getEngineZ() {
		return engineZ;
	}
}
