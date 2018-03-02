package PathFinding;

import java.util.ArrayList;
import java.io.*;
import java.util.Random;

public class PathFindingTests {
	
	public static void main(String[] args) {
		//Add some test-goal location
		ArrayList<float[]> cubelocs = new ArrayList<float[]>();
		cubelocs.add(new float[] {0,30,-1000}); //Standard location
		cubelocs.add(new float[] {10,30,-1000}); //Not in reach of turn
		cubelocs.add(new float[] {10, 100, -1000}); //Directly above current location
		//Add the starting location
		float[] start = new float[] {0,0,0};
		//Create a path object, this calls setPath() automatically
		IPath pad = new IPath(cubelocs, 0.1f, 0.1f, 100f, start, 0f);
		pad.setPath();
		//Get the found path
		float[] padx = pad.getX();
		float[] pady = pad.getY();
		float[] padz = pad.getZ();
		
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
			
			for(int i = 0; i<padx.length; i++){
				bw.write("("+Float.toString(padx[i])+", "+Float.toString(pady[i])+", "+Float.toString(padz[i])+")");
				if (i != padx.length-1) {
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
