import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;

public class Drone {
	private final int NbColumns = 0, NbRows = 0;
	private final float wingX = 2.5f, tailSize = 5.46f, engineMass = 70f,
			wingMass = 150f, tailMass = 30f, maxThrust = -1f,
			maxAOA = -1f, wingLiftslope = -1f, horStabLiftSlope = -1f,
			verStabLiftSlope = -1f, horizontalAngleOfView = -1f,
			verticalAngleOfView = -1;

	private float xpos, ypos, zpos, xvel, yvel, zvel, xrot, yrot, zrot, yaw, pitch, roll,
				thrust, leftWingInclination, rightWingInclination, horStabInclination, verStabInclination;
	
	public Drone(float x, float y, float z, float xvel, float yvel, float zvel) {
		this.xpos = x;
		this.ypos = y;
		this.zpos = z;
		this.xvel = xvel;
		this.yvel = yvel;
		this.zvel = zvel;
		this.xrot = 0;
		this.yrot = 0;
		this.zrot = 0;
		this.yaw = 0;
		this.pitch = 0;
		this.roll = 0;
		this.thrust = 0;
		this.leftWingInclination = 0;
		this.rightWingInclination = 0;
		this.horStabInclination = 0;
		this.verStabInclination = 0;
	}
	
	// position
	float getX() {
		return xpos;
	}
	
    float getY() {
    	return ypos;
    }
    
    float getZ() {
    	return zpos;
    }
    
	public void setPos(float x, float y, float z) {
		this.xpos = x;
		this.ypos = y;
		this.zpos = z;
	}
	
	public void setPos(Vector pos) {
		setPos((float) pos.get(0), (float) pos.get(1), (float) pos.get(2));
	}
	
    Vector getPos() {
    	return new BasicVector(new double[] {xpos, ypos, zpos});
    }
    
    
    // velocity
    float getXvel() {
    	return xvel;
    }
    
    float getYvel() {
    	return yvel;
    }
    
    float getZvel() {
    	return zvel;
    }
    
    Vector getVel() {
    	return new BasicVector(new double[] {xvel, yvel, zvel});
    }
    
	public void setVel(float xvel, float yvel, float zvel) {
		this.xvel = xvel;
		this.yvel = yvel;
		this.zvel = zvel;
	}
	
	public void setVel(Vector vel) {
		setPos((float) vel.get(0), (float) vel.get(1), (float) vel.get(2));
	}
	
	
	// rotation
    float getXrot() {
    	return xrot;
    }
    
    float getYrot() {
    	return yrot;
    }
    
    float getZrot() {
    	return zrot;
    }
    
    Vector getRot() {
    	return new BasicVector(new double[] {xrot, yrot, zrot});
    }
    
	public void setRot(float xrot, float yrot, float zrot) {
		this.xrot = xrot;
		this.yrot = yrot;
		this.zrot = zrot;
	}
	
	public void setRot(Vector rot) {
		setRot((float) rot.get(0), (float) rot.get(1), (float) rot.get(2));
	}
	
	
	// thrust
    float getThrust() {
    	return thrust;
    }
    
	public void setThrust(float thrust) {
		if (thrust < maxThrust && thrust >= 0) 
			this.thrust = thrust;
	}
	
	public float getMaxThrust() {
		return maxThrust;
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
	
	float getHorStabInclination() {
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
	
	
	// plane dimensions
	public float getWingX() {
		return wingX;
	}

	public float getTailSize() {
		return tailSize;
	}

	public float getEngineMass() {
		return engineMass;
	}

	public float getWingMass() {
		return wingMass;
	}
	
	public float getTailMass() {
		return tailMass;
	}


	// max AOA
	public float getMaxAOA() {
		return maxAOA;
	}

	
	// lift slopes
	public float getWingLiftSlope() {
		return wingLiftslope;
	}

	public float getHorStabLiftSlope() {
		return horStabLiftSlope;
	}

	public float getVerStabLiftSlope() {
		return verStabLiftSlope;
	}

	
	// camera stats
	public float getHorizontalAngleOfView() {
		return horizontalAngleOfView;
	}

	public float getVerticalAngleOfView() {
		return verticalAngleOfView;
	}

	public int getNbColumns() {
		return NbColumns;
	}

	public int getNbRows() {
		return NbRows;
	}
	
	
    // orientations
    float getHeading() {
    	return yaw;
    }
    
    float getPitch() {
    	return pitch;
    }
    
    float getRoll() {
    	return roll;
    }
}
