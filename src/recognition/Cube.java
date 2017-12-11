package recognition;

import java.util.ArrayList;

public class Cube {

    public Cube(float hValue, float sValue){
        this.hValue = hValue;
        this.sValue = sValue;
    }

    private int[] XYZ = null;
    private float hValue, sValue;
    private ArrayList<int[]> pixels = new ArrayList<>();
    private float[] location;
    private double dist;

    public void setLocation(float[]location){
    	this.location = location;
    }
    
    public float[] getLocation(){
    	return this.location;
    }
    
    public void setDist(double dist){
    	this.dist = dist;
    }
    
    public double getDist(){
    	return this.dist;
    }
    //method for calculating best location approximation
    //TODO
    public void setNewXYZ(int[] newXYZ){
        if (! (this.XYZ == null)){
            setXYZ((this.getX()+newXYZ[0])/2, (this.getY()+newXYZ[1])/2, (this.getZ()+newXYZ[2])/2);
        }else{
            setXYZ(newXYZ[0], newXYZ[1], newXYZ[2]);
        }
    }

    private void setXYZ(int x, int y, int z){
        this.XYZ[0] = x;
        this.XYZ[1] = y;
        this.XYZ[2] = z;
    }

    //setter
    public void addPixel(int[] pixel){
        pixels.add(pixel);
    }


    //getters
    public int getX(){
        return XYZ[0];
    }
    public int getY(){
        return XYZ[1];
    }
    public int getZ(){
        return XYZ[2];
    }
    public float gethValue(){
        return  this.hValue;
    }
    public float getsValue(){
        return  this.sValue;
    }
    public int getNbPixels(){
        return pixels.size();
    }
    public int[] getAveragePixel(){
        int x = 0;
        int y = 0;
        int counter = 0;
        for (int[] pixel: pixels){
            x += pixel[0];
            y += pixel[1];
            counter += 1;
        }
        int[] midPixel = {x/counter, y/counter};
        return midPixel;
    }
    public int getHeight(){
        int maxY = -1; int minY = Integer.MAX_VALUE;
        for (int[] pixel: pixels){
            if(pixel[1] > maxY)
                maxY = pixel[1];
            else if (pixel[1] < minY)
                minY = pixel[1];
        }
        return maxY - minY;
    }
    public int[] getCubeData(){
        int[] output = {getAveragePixel()[0], getAveragePixel()[1], getNbPixels(), getHeight()};
        return output;
    }
    public ArrayList<int[]> getPixels(){
        return this.pixels;
    }
    

    private ArrayList<int[]> convexhull = null;
    
    public ArrayList<int[]> getConvexHull(){
		if (convexhull != null){
			return convexhull;
		}
		else{
			ArrayList<int[]> pixels = this.getPixels();
			int[] pointOnHull = pixels.get(0);
			for(int[] pixel : pixels){
				if((pixel[0] < pointOnHull[0]) || (pixel[0] == pointOnHull[0] && pixel[1] > pointOnHull[1])){
					pointOnHull = pixel;
				}
			}
			ArrayList<int[]> hull = new ArrayList<int[]>();
			int[] endPoint = null;
			while(hull.isEmpty() || endPoint != hull.get(0)){
				hull.add(pointOnHull);
				endPoint = pixels.get(0);
				for(int i = 1; i < pixels.size(); i++){
					if((endPoint[0] == pointOnHull[0] && endPoint[1] == pointOnHull[1]) 
							|| orientation(pointOnHull, pixels.get(i), endPoint) == 2 
							|| (orientation(pointOnHull, pixels.get(i), endPoint) == 0 && distance(pointOnHull, endPoint) < distance(pointOnHull, pixels.get(i)))){
						endPoint = pixels.get(i);
					}
				}
				pointOnHull = endPoint;
			}
			for (int i = 2; i < hull.size(); i++){
				if(orientation(hull.get(i-2), hull.get(i-1), hull.get(i))==0){
					hull.remove(i-1);
					i--;
				}
			}
			this.convexhull = hull;
			return hull;
		}
	}
	
    private double distance(int[] point1, int[] point2) {
		double xDiff = point1[0] - point2[0];
		double yDiff = point1[1] - point2[1];
		return Math.sqrt(xDiff*xDiff + yDiff*yDiff);
	}

	private int orientation(int[] p, int[] q, int[] r) {
    	int val = (q[1] - p[1]) * (r[0] - q[0]) -
                (q[0] - p[0]) * (r[1] - q[1]);
    
        if (val == 0) return 0;  // collinear
        return (val > 0)? 1: 2; // clock or counterclock wise
	}

}
