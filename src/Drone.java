import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;

public class Drone {
	private final int NbColumns = 0, NbRows = 0;
	private final float wingX = (float) 2.5, tailSize = (float) 5.46, engineMass = (float) 70,
			wingMass = (float) 150, tailMass = (float) 30, maxThrust = (float) -1,
			maxAOA = (float) -1, wingLiftslope = (float) -1, horStabLiftSlope = (float) -1,
			verStabLiftSlope = (float) -1, horizontalAngleOfView = (float) -1,
			verticalAngleOfView = (float) -1;

	private float xpos, ypos, zpos, xvel, yvel, zvel, xrot, yrot, zrot, yaw, pitch, roll;
	
	public Drone(float x, float y, float z) {
		this.xpos = x;
		this.ypos = y;
		this.zpos = z;
		this.yaw = 0;
		this.pitch = 0;
		this.roll = 0;
	}
	
	public void setPos(float x, float y, float z) {
		this.xpos = x;
		this.ypos = y;
		this.zpos = z;
	}
	
	public void setPos(Vector pos) {
		setPos((float) pos.get(0), (float) pos.get(1), (float) pos.get(2));
	}
	
	public void setVel(float xvel, float yvel, float zvel) {
		this.xvel = xvel;
		this.yvel = yvel;
		this.zvel = zvel;
	}
	
	public void setVel(Vector vel) {
		setPos((float) vel.get(0), (float) vel.get(1), (float) vel.get(2));
	}
	
	public void setRot(float xrot, float yrot, float zrot) {
		this.xrot = xrot;
		this.yrot = yrot;
		this.zrot = zrot;
	}
	
	public void setRot(Vector rot) {
		setRot((float) rot.get(0), (float) rot.get(1), (float) rot.get(2));
	}

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

	public float getMaxThrust() {
		return maxThrust;
	}

	public float getMaxAOA() {
		return maxAOA;
	}

	public float getWingLiftSlope() {
		return wingLiftslope;
	}

	public float getHorStabLiftSlope() {
		return horStabLiftSlope;
	}

	public float getVerStabLiftSlope() {
		return verStabLiftSlope;
	}

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
	
	float getX() {
		return xpos;
	}
	
    float getY() {
    	return ypos;
    }
    
    float getZ() {
    	return zpos;
    }
    
    float getHeading() {
    	return yaw;
    }
    
    float getPitch() {
    	return pitch;
    }
    
    float getRoll() {
    	return roll;
    }
    
    float getXvel() {
    	return xvel;
    }
    
    float getYvel() {
    	return yvel;
    }
    
    float getZvel() {
    	return zvel;
    }
    
    float getXrot() {
    	return xrot;
    }
    
    float getYrot() {
    	return yrot;
    }
    
    float getZrot() {
    	return zrot;
    }
    
    Vector getPos() {
    	return new BasicVector(new double[] {xpos, ypos, zpos});
    }
    
    Vector getVel() {
    	return new BasicVector(new double[] {xvel, yvel, zvel});
    }
    
    Vector getRot() {
    	return new BasicVector(new double[] {xrot, yrot, zrot});
    }
}
