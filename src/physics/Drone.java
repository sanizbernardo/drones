package physics;

import java.lang.Math;

import org.joml.*;

import datatypes.AutopilotConfig;

public class Drone {
	
	private final float maxThrust;

	private final float wingX, tailSize;

	private Vector3f position, velocity, orientation, rotation, forwardD = new Vector3f(0, 0, -1), 
			right = new Vector3f(1, 0, 0);
	
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

	public Vector3f getForward() {
		return  transMat().transform(forwardD, new Vector3f());
	}
	
	public Vector3f getH0() {
		Vector3f forward = getForward();
		return new Vector3f(forward.x, 0, forward.z);
	}
	
	public Vector3f getH() {
		Vector3f heading0 = getH0();
		return heading0.div(heading0.dot(heading0), new Vector3f());
	}
	
	public Vector3f getR0() {
		Vector3f heading = getH();
		return heading.cross(new Vector3f(), new Vector3f());
	}
	
	public Vector3f getU0() {
		Vector3f forward = getForward();
		return getR0().cross(forward, new Vector3f());
	}
	
    public float getPitch() {
    	Vector3f forward = getForward();
    	float y = forward.dot(new Vector3f(0, 1, 0));
    	float x = forward.dot(getH());
    	return (float) Math.atan2(y,x);
    }
    
    public float getHeading() {
    	Vector3f heading = getH();
    	float y = heading.dot(new Vector3f(-1, 0, 0));
    	float x = heading.dot(forwardD);
    	return (float) Math.atan2(y,x);
    }
    public float getRoll() {
    	float y = right.dot(getU0());
    	float x = right.dot(getR0());
    	return (float) Math.atan2(y,x);
//    	return 0f;
    }
    
    public float getYaw() {
    	return (float) orientation.get(1);
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
	
	public static Matrix3f buildTransformMatrix(float xAngle, float yAngle, float zAngle) {
		// column major -> transposed
		Matrix3f xRot = new Matrix3f(
				1f, 					  0f,					    0f,
				0f,  (float)Math.cos(xAngle),  (float)-Math.sin(xAngle),
				0f, (float)Math.sin(xAngle), (float)Math.cos(xAngle)),
				
			   yRot = new Matrix3f(
				(float)Math.cos(yAngle),  0f, (float)Math.sin(yAngle),
									 0f,  1f, 						0f,
				(float)-Math.sin(yAngle),  0f, (float)Math.cos(yAngle)),
			   
			   zRot = new Matrix3f(
				 (float)Math.cos(zAngle), (float)-Math.sin(zAngle), 0f,
				(float)Math.sin(zAngle), (float)Math.cos(zAngle), 0f,
									  0f, 					   0f, 1f);
		
		return xRot.mul(yRot).mul(zRot);
	}
	
	public Matrix3f transMat() {
		return buildTransformMatrix(orientation.x, orientation.y, orientation.z);
	}
}
