package PathFinding;

import java.util.ArrayList;

import entities.meshes.cube.Cube;
import interfaces.Path;
import utils.Cubes;

public class IPath implements Path{

	public IPath(ArrayList<float[]> cubeLocations, float maxInclination, float maxDeclination, float turningRadius, float[] startLoc, float heading){
		this.cubeLocations = cubeLocations;
		this.maxInclination = maxInclination;
		this.maxDeclination = maxDeclination;
		this.turningRadius = turningRadius;
		this.heading = heading;
		this.location = startLoc;
		setPath();
	}

	private final ArrayList<float[]> cubeLocations;
	private final float maxInclination;
	private final float maxDeclination;
	private final float turningRadius;
	private float heading;
	private float[] location;
	private ArrayList<Float> x = new ArrayList<Float>();
	private ArrayList<Float> y = new ArrayList<Float>();
	private ArrayList<Float> z = new ArrayList<Float>();
	
	
	private void setPath() {
		ArrayList<float[]> cubesInPath = new ArrayList<>();
		addLiftOffPoint();
		for(int i = 0; i<cubeLocations.size(); i++){
			float[] closestCube = findClosest(cubesInPath);
			addToPath(closestCube);
			cubesInPath.add(closestCube);
		}
	}
	
	private void addLiftOffPoint() {
		float[] liftOffPoint = {0, 20, -500};//TODO depends on startlocation & heading
		x.add(liftOffPoint[0]);
		y.add(liftOffPoint[1]);
		z.add(liftOffPoint[2]);
		this.location = liftOffPoint;
		
	}
	
	//Find the closest goal location to current location which has not yet been visited
	private float[] findClosest(ArrayList<float[]> cubesAlreadyInPath){
		float[] currentClosest = null;
		double currentDist = Float.MAX_VALUE;
		for (float[] cube: cubeLocations){
			if (! cubesAlreadyInPath.contains(cube)) {
				double newDist = Math.sqrt(Math.pow((double)(cube[0]-x.get(x.size()-1)), 2) + Math.pow((double)(cube[1]-y.get(y.size()-1)), 2) + Math.pow((double)(cube[2]-z.get(z.size()-1)), 2));
				if(newDist<currentDist){
					currentClosest = cube;
					currentDist = newDist;
				}				
			}
		}
		return currentClosest;
	}
	
	//tempLoc?
	//TODO All 1.5f are to be optimized
	private void addToPath(float[] closestCube) {
		//down
		if(closestCube[1] < this.location[1]){
			float newX = this.location[0] + 1.5f*(float)Math.sin(heading) * (this.location[1] - closestCube[1])/this.maxDeclination;
			float newY = closestCube[1];
			float newZ = this.location[2] - 1.5f*(float)Math.cos(heading) * (this.location[1] - closestCube[1])/this.maxDeclination;
			this.x.add(newX);
			this.y.add(newY);
			this.z.add(newZ);
			this.location = new float[] {newX, newY, newZ};
		}
		//up
		else if(closestCube[1] > this.location[1]){
			float newX = this.location[0] + 1.5f*(float)Math.sin(heading) * (closestCube[1] - this.location[1])/this.maxInclination;
			float newY = closestCube[1];
			float newZ = this.location[2] - 1.5f*(float)Math.cos(heading) * (closestCube[1] - this.location[1])/this.maxInclination;
			this.x.add(newX);
			this.y.add(newY);
			this.z.add(newZ);
			this.location = new float[] {newX, newY, newZ};
		}
		
		float corner = 0;
		
		//behind
		if(orientation(this.location, auxLocPlusX(1), closestCube) == 1){
			float dm = (float)Math.sqrt(Math.pow(this.location[0] - auxLocPlusX(this.turningRadius)[0], 2) + Math.pow(this.location[2] - auxLocPlusX(this.turningRadius)[2], 2));
			float dg = (float)Math.sqrt(Math.pow(this.location[0] - closestCube[0], 2) + Math.pow(this.location[2] - closestCube[2], 2));
			float mg = (float)Math.sqrt(Math.pow(auxLocPlusX(this.turningRadius)[0] - closestCube[0], 2) + Math.pow(auxLocPlusX(this.turningRadius)[2] - closestCube[2], 2));
			float bigCorner = (float)(2*Math.PI) - (float)Math.acos(dg*dg - dm*dm - mg*mg)/(-2*dm*mg);
			float smallCorner = (float)Math.acos(turningRadius/mg);
			corner = bigCorner - smallCorner;
		}
		//in front
		else{
			float dm = (float)Math.sqrt(Math.pow(this.location[0] - auxLocPlusX(this.turningRadius)[0], 2) + Math.pow(this.location[2] - auxLocPlusX(this.turningRadius)[2], 2));
			float dg = (float)Math.sqrt(Math.pow(this.location[0] - closestCube[0], 2) + Math.pow(this.location[2] - closestCube[2], 2));
			float mg = (float)Math.sqrt(Math.pow(auxLocPlusX(this.turningRadius)[0] - closestCube[0], 2) + Math.pow(auxLocPlusX(this.turningRadius)[2] - closestCube[2], 2));
			float bigCorner = (float)Math.acos(dg*dg - dm*dm - mg*mg)/(-2*dm*mg);
			float smallCorner = (float)Math.acos(turningRadius/mg);
			corner = bigCorner - smallCorner;
		}

		//this number is a choice: maybe it is better to let this number depend on corner, or distance between locations //TODO
		int NbPoints = 10;
		float cornerPiece = corner/NbPoints;
		
		//to the right
		if(orientation(this.location, auxLocPlusMinZ(1), closestCube) == 1){
			float fwd = turningRadius*(float)Math.sin(cornerPiece);
			float rightd = turningRadius - (float)Math.cos(cornerPiece)*turningRadius;
			for(int i = 0; i < NbPoints; i++){
				float newX = this.location[0] + (float)Math.sin(heading)*fwd + (float)Math.cos(heading)*rightd;
				float newY = this.location[1];
				float newZ = this.location[2] - (float)Math.cos(heading)*fwd + (float)Math.sin(heading)*rightd;
				this.x.add(newX);
				this.y.add(newY);
				this.z.add(newZ);
				this.heading += cornerPiece;
				this.location = new float[] {newX, newY, newZ};
			}
		}
		//to the left
		else if(orientation(this.location, auxLocPlusMinZ(1), closestCube) == 2){
			for(int i = 0; i < NbPoints; i++){
				float fwd = turningRadius*(float)Math.sin(cornerPiece);
				float leftd = turningRadius - (float)Math.cos(cornerPiece)*turningRadius;
				float newX = this.location[0] + (float)Math.sin(heading)*fwd - (float)Math.cos(heading)*leftd;
				float newY = this.location[1];
				float newZ = this.location[2] - (float)Math.cos(heading)*fwd - (float)Math.sin(heading)*leftd;
				this.x.add(newX);
				this.y.add(newY);
				this.z.add(newZ);
				this.heading -= cornerPiece;
				this.location = new float[] {newX, newY, newZ};
			}
		}
		
		//done turning -> got to cube
		this.x.add(closestCube[0]);
		this.y.add(closestCube[1]);
		this.z.add(closestCube[2]);
		this.location = new float[] {closestCube[0], closestCube[1], closestCube[2]};
	}
	
	//check wether we can reach a location in case we start turning right now
	private Boolean turnable(float[] goalLocation, float turnRadius) {
		Boolean result = True;
		float[] currentLoc = this.location;
		float[] circleCentre1 = currentLoc + turnRadius*(-1/this.heading);
		float[] circleCentre2 = currentLoc - turnRadius*(-1/this.heading);
		if (Math.sqrt(Math.pow(goalLocation[0]-circleCentre1[0], 2)+Math.pow(goalLocation[2]-circlecentre1[2], 2)) < turnRadius)
			result = False;		
		if (Math.sqrt(Math.pow(goalLocation[0]-circleCentre2[0], 2)+Math.pow(goalLocation[2]-circlecentre2[2], 2)) < turnRadius)
			result = False;		
		return result;
	}
	
	
	private float[] auxLocPlusMinZ(float arg) {
		float[] newLoc = new float[3];
		newLoc[0] = this.location[0] + (float)Math.sin(heading)*arg;
		newLoc[1] = this.location[1];
		newLoc[2] = this.location[2] - (float)Math.cos(heading)*arg;
		return newLoc;
	}
	
	private float[] auxLocPlusX(float arg){
		float[] newLoc = new float[3];
		newLoc[0] = this.location[0] + (float)Math.cos(heading)*arg;
		newLoc[1] = this.location[1];
		newLoc[2] = this.location[2] - (float)Math.sin(heading)*arg;
		return newLoc;
		}

	private int orientation(float[] p, float[] q, float[] r) {
    	float val = (q[2] - p[2]) * (r[0] - q[0]) -
                (q[0] - p[0]) * (r[2] - q[2]);

		System.out.println(val);
        if (val == 0) return 0;  // collinear
        return (val > 0)? 1: 2; // clock or counterclock wise
	}
	
	@Override
	public float[] getX() {
		float[] x = new float[this.x.size()];
		int i = 0;
		for (Float f: this.x) {
			x[i++] = (f != null ? f : Float.NaN);
		}
		return x;
	}

	@Override
	public float[] getY() {
		float[] y = new float[this.y.size()];
		int i = 0;
		for (Float f: this.y) {
			y[i++] = (f != null ? f : Float.NaN);
		}
		return y;
	}

	@Override
	public float[] getZ() {
		float[] z = new float[this.z.size()];
		int i = 0;
		for (Float f: this.z) {
			z[i++] = (f != null ? f : Float.NaN);
		}
		return z;
	}

}
