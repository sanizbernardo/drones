package PathFinding;

import java.util.ArrayList;

import interfaces.Path;

public class IPath implements Path{

	public IPath(ArrayList<float[]> cubeLocations, float maxInclination, float maxDeclination, float turningRadius){
		this.cubeLocations = cubeLocations;
		this.maxInclination = maxInclination;
		this.maxDeclination = maxDeclination;
		this.turningRadius = turningRadius;
		setPath();
	}

	private final ArrayList<float[]> cubeLocations;
	private final float maxInclination;
	private final float maxDeclination;
	private final float turningRadius;
	private float[] x;
	private float[] y;
	private float[] z;
	
	
	private void setPath() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float[] getX() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[] getY() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[] getZ() {
		// TODO Auto-generated method stub
		return null;
	}

}
