package PathFinding;

import java.util.ArrayList;

public class PathFindingTests {
	
	public static void main(String[] args) {
		//Add a goal location
		ArrayList<float[]> cubelocs = new ArrayList<float[]>();
		cubelocs.add(new float[] {0,30,-1000});
		cubelocs.add(new float[] {10,30,-1000});
		//Add the starting location
		float[] start = new float[] {0,0,0};
		//Create a path object, this calls setPath() automatically
		IPath pad = new IPath(cubelocs, 0.1f, 0.1f, 100f, start, 0f);
		pad.setPath();
		//Get the found path
		float[] padx = pad.getX();
		float[] pady = pad.getY();
		float[] padz = pad.getZ();
		for(int i = 0; i<padx.length; i++){
			System.out.println(padx[i] + "     "  + pady[i] + "     "  + padz[i]);
		}
	}
}
