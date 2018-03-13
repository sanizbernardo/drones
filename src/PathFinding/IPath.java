package PathFinding;

import java.util.ArrayList;

import interfaces.Path;

/*
 * This class contains the method setPath() which generates a path for the drone to follow.
 * This path contains all target locations it was given and takes several drone parameters into account to ensure it is valid.
 */
public class IPath implements Path{

	public IPath(ArrayList<float[]> cubeLocations, float maxInclination, float maxDeclination, float turningRadius, float[] startLoc, float heading){
		this.cubeLocations = cubeLocations;
		this.maxInclination = maxInclination;
		this.maxDeclination = maxDeclination;
		this.turningRadius = turningRadius;
		this.heading = heading;
		this.location = startLoc;
	}

	//All cubes that have to visited
	private final ArrayList<float[]> cubeLocations;
	//Parameters defined by drone
	private final float maxInclination;
	private final float maxDeclination;
	private final float turningRadius;
	//Drone location and heading
	private float heading;
	private float[] location;
	//Storage of path
	private ArrayList<Float> x = new ArrayList<Float>();
	private ArrayList<Float> y = new ArrayList<Float>();
	private ArrayList<Float> z = new ArrayList<Float>();
	private ArrayList<Float> s = new ArrayList<Float>();
	private float p_state = 50;
	
	
	public void setPath() {
		ArrayList<float[]> cubesInPath = new ArrayList<>();
		addLiftOffPoint();
		for(int i = 0; i<cubeLocations.size(); i++){
			float[] closestCube = findClosest(cubesInPath);
			addToPath(closestCube);
			cubesInPath.add(closestCube);
		}
		s.add(-1f);
	}
	
	//TODO alter method to take starting location and heading into account
	private void addLiftOffPoint() {
		float[] liftOffPoint = {0, 30, -100};
		addLocation(liftOffPoint[0], liftOffPoint[1], liftOffPoint[2]);
		this.location = liftOffPoint;
	}
	
	private float[] findClosest(ArrayList<float[]> cubesAlreadyInPath){
		float[] currentClosest = null;
		double currentDist = Float.MAX_VALUE;
		for (float[] cube: cubeLocations){
			if (! cubesAlreadyInPath.contains(cube)) {
				double newDist = Math.sqrt(Math.pow((double)(cube[0]-x.get(x.size()-1)), 2) + Math.pow((double)(cube[1]-y.get(y.size()-1)), 2) + Math.pow((double)(cube[2]-z.get(z.size()-1)), 2));
				if(newDist < currentDist){
					currentClosest = cube;
					currentDist = newDist;
				}				
			}
		}
		return currentClosest;
	}
	
	private void addToPath(float[] closestCube) {
		//Cube on lower y-level
		if(closestCube[1] < this.location[1]){
			float newX = this.location[0] + 1.5f*(float)Math.sin(heading) * (this.location[1] - closestCube[1])/this.maxDeclination;
			float newY = closestCube[1];
			float newZ = this.location[2] - 1.5f*(float)Math.cos(heading) * (this.location[1] - closestCube[1])/this.maxDeclination;
			addLocation(newX, newY, newZ);
			this.location = new float[] {newX, newY, newZ};
			s.add(3f);
		}
		//Cube on higher y-level
		else if(closestCube[1] > this.location[1]){
			float newX = this.location[0] + 1.5f*(float)Math.sin(heading) * (closestCube[1] - this.location[1])/this.maxInclination;
			float newY = closestCube[1];
			float newZ = this.location[2] - 1.5f*(float)Math.cos(heading) * (closestCube[1] - this.location[1])/this.maxInclination;
			addLocation(newX, newY, newZ);
			this.location = new float[] {newX, newY, newZ};
			s.add(4f);
		}
		
		float corner = 0;
		//If not within turning reach, adjust by getting in reach
		if(!turnable(closestCube, turningRadius)){
			float newX = this.location[0] + 2*turningRadius*(float)Math.sin(heading);
			float newY = closestCube[1];
			float newZ = this.location[2] - 2*turningRadius*(float)Math.cos(heading);
			addLocation(newX, newY, newZ);
			s.add(0f);
			this.location = new float[] {newX, newY, newZ};			
		}
		
		float temp = 0;
		if(orientation(this.location, auxLocPlusMinZ(1), closestCube) == 1){
			temp = this.turningRadius;
		}
		else{
			temp = -this.turningRadius;
		}
		
		//Cube behind current location
		if(orientation(this.location, auxLocPlusX(1), closestCube) == 1){
			float dm = this.turningRadius;
			float dg = (float)Math.sqrt(Math.pow(this.location[0] - closestCube[0], 2) + Math.pow(this.location[2] - closestCube[2], 2));
			float mg = (float)Math.sqrt(Math.pow(auxLocPlusX(temp)[0] - closestCube[0], 2) + Math.pow(auxLocPlusX(temp)[2] - closestCube[2], 2));
			float bigCorner = (float)(2*Math.PI) - (float)Math.acos((dg*dg - dm*dm - mg*mg)/(-2*dm*mg));
			float smallCorner = (float)Math.acos(turningRadius/mg);
			corner = bigCorner - smallCorner;
		}
		//Cube in front of current location
		else{
			float dm = this.turningRadius;
			float dg = (float)Math.sqrt(Math.pow(this.location[0] - closestCube[0], 2) + Math.pow(this.location[2] - closestCube[2], 2));
			float mg = (float)Math.sqrt(Math.pow(auxLocPlusX(temp)[0] - closestCube[0], 2) + Math.pow(auxLocPlusX(temp)[2] - closestCube[2], 2));
			float bigCorner = (float)Math.acos((dg*dg - dm*dm - mg*mg)/(-2*dm*mg));
			float smallCorner = (float)Math.acos(turningRadius/mg);
			corner = bigCorner - smallCorner;
		}

		int NbPoints = (int)Math.floor(corner*3) + 1;
		float cornerPiece = corner/NbPoints;
		
		//Cube to the right of the current location
		if(orientation(this.location, auxLocPlusMinZ(1), closestCube) == 1){
			float fwd = turningRadius*(float)Math.sin(cornerPiece);
			float rightd = turningRadius - (float)Math.cos(cornerPiece)*turningRadius;
			for(int i = 0; i < NbPoints; i++){
				float newX = this.location[0] + (float)Math.sin(heading)*fwd + (float)Math.cos(heading)*rightd;
				float newY = this.location[1];
				float newZ = this.location[2] - (float)Math.cos(heading)*fwd + (float)Math.sin(heading)*rightd;
				addLocation(newX, newY, newZ);
				s.add(2f);
				this.heading += cornerPiece;
				this.location = new float[] {newX, newY, newZ};
			}
		}
		//Cube to the left of the current location
		else if(orientation(this.location, auxLocPlusMinZ(1), closestCube) == 2){
			for(int i = 0; i < NbPoints; i++){
				float fwd = turningRadius*(float)Math.sin(cornerPiece);
				float leftd = turningRadius - (float)Math.cos(cornerPiece)*turningRadius;
				float newX = this.location[0] + (float)Math.sin(heading)*fwd - (float)Math.cos(heading)*leftd;
				float newY = this.location[1];
				float newZ = this.location[2] - (float)Math.cos(heading)*fwd - (float)Math.sin(heading)*leftd;
				addLocation(newX, newY, newZ);
				s.add(1f);
				this.heading -= cornerPiece;
				this.location = new float[] {newX, newY, newZ};
			}
		}
		
		//done turning -> got to cube
		addLocation(closestCube[0], closestCube[1], closestCube[2]);
		s.add(0f);
		this.location = new float[] {closestCube[0], closestCube[1], closestCube[2]};
	}
		
	//Check if a location is within turning radius
	private Boolean turnable(float[] goalLocation, float turningRadius) {
		float[] circleCentre1 = auxLocPlusX(turningRadius);
		float[] circleCentre2 = auxLocPlusX(-1*turningRadius);
		if (Math.sqrt(Math.pow(goalLocation[0]-circleCentre1[0], 2)+Math.pow(goalLocation[2]-circleCentre1[2], 2)) < turningRadius){
			return false;	
		}
		if (Math.sqrt(Math.pow(goalLocation[0]-circleCentre2[0], 2)+Math.pow(goalLocation[2]-circleCentre2[2], 2)) < turningRadius){
			return false;
		}
		return true;
	}
	
	//Helper functions
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
		newLoc[2] = this.location[2] + (float)Math.sin(heading)*arg;
		return newLoc;
		}
	
	private void addLocation(float x, float y, float z) {
		this.x.add(x); this.y.add(y); this.z.add(z);
	}

	private int orientation(float[] p, float[] q, float[] r) {
    	float val = (-q[2] + p[2]) * (r[0] - q[0]) -
                (q[0] - p[0]) * (-r[2] + q[2]);

        if (val == 0) return 0;
        return (val > 0)? 1: 2;	//Clock - Counterclockwise
	}
	
	//Getters (Convert the ArrayLists to float[])
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
	
	/**
	 * Returntype: ArrayList of float[] = [x, y, z, state]
	 */
	public ArrayList<float[]> getPathArray(){
		ArrayList<float[]> path = new ArrayList<>();
		for (int i=0; i<this.x.size();i++) {
			float[] point = new float[4];
			point[0] = this.x.get(i);point[1] = this.y.get(i);point[2] = this.z.get(i);
			point[3] = this.s.get(i);
			path.add(point);
		}
		return path;
	}

}
