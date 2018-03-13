package PathFinding;

import java.util.ArrayList;
import java.io.*;
import java.util.Random;

public class PathFindingTests {
	
	public static void main(String[] args) {
		//Add some test-goal location
		ArrayList<float[]> cubelocs = new ArrayList<float[]>();
		cubelocs.add(new float[] {-100, 30, 0}); //Standard location
		cubelocs.add(new float[] {500, 30, 300}); //Not in reach of turn
		cubelocs.add(new float[] {100, 30, -500}); //Directly above current location
		cubelocs.add(new float[] {60, 60, -40}); //Directly above current location
		cubelocs.add(new float[] {10, 30, -0}); //Directly above current location

		//Add the starting location
		float[] start = new float[] {0,0,0};
		//Create a path object, this calls setPath() automatically
		IPath path = new IPath(cubelocs, 0.1f, 0.1f, 100f, start, 0.75f);
		path.setPath();

		//Draw the path
		new PathDemo(path.getPathArray(), cubelocs);
		
	}
	
	//Extract path into text-file (locations are tuples)
	//Used for drawing the path using a Python module I already had, 
	//implemented similar functionality in PathDemo now.
	@SuppressWarnings("unused")
	private static void extractPath(float[] x, float[] y, float[] z) {
		//Write the found path to a file for later graphic representation
		BufferedWriter	bw = null;
		Random rand = new Random();
		try{
			File file = new File("./src/PathFinding/Path"+Integer.toString(rand.nextInt(100))+".txt");
			//Making sure there is a file to overwrite
			if (!file.exists()) {
				file.createNewFile();}

			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			
			for(int i = 0; i<x.length; i++){
				bw.write("("+Float.toString(x[i])+", "+Float.toString(y[i])+", "+Float.toString(z[i])+")");
				if (i != x.length-1) {
					bw.newLine();					
				}

			}
			System.out.println("Writing file complete");
					
			
		}catch(Exception e) 
		{
			System.out.println("IOException");
		}
		finally 
		{
			try {
				if (bw!=null)
					bw.close();
			}catch(Exception ex) {
				System.out.println("Error closing buffer");
			}				
		}
	}
}

