package PathFinding;

import java.util.ArrayList;
import java.io.*;
import java.util.Random;

public class PathFindingTests {
	
	public static void main(String[] args) {

		ArrayList<float[]> cubelocs = new ArrayList<float[]>();
		cubelocs.add(new float[] {0, 0,0});
		cubelocs.add(new float[] {-100, 30, 0}); 
		cubelocs.add(new float[] {500, 30, 300}); 
		cubelocs.add(new float[] {100, 30, -500});
		cubelocs.add(new float[] {60, 60, -40}); 
		cubelocs.add(new float[] {10, 30, -0});

		float[] start = new float[] {0,0,0};
		IPath path = new IPath(cubelocs, 0.1f, 0.1f, 100f, start, 0.75f);
		new PathDemo(path.getPathArray(), cubelocs);
		
	}
	
	/**
	 * Was used to extract the path to a file
	 * @param x
	 * @param y
	 * @param z
	 */
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

