package recognition;

import java.util.ArrayList;

/** Opmerkingen / TODO:
 * 	nog niet mooi getest, maar met print statements, die geven wel verwachte waarden
 * 	gaat foutmelding geven als er geen rode pixel aanwezig is -> voorlopig eenvoudige oplossing, 
 * 		control moet eerst checken of atLeastOneRed() true geeft.
 * 	 */

/**
* A class that analyzes the images with functions that return useful information for the autopilot.
*
* @author  Tomas Geens
*/
public class ImageRecognition {
	
	private final byte[]image;
	private final int nbRows;
	private final int nbColumns;
	private final float horizontalAngleOfView;
	private final float verticalAngleOfView;
	private ArrayList<int[]> corners = new ArrayList<int[]>();
	private final double[] center;
	private final int surface;
	private boolean atLeastOneRed = false;
	
	public ImageRecognition(byte[] image, int nbRows, int nbColums, float horizontalAngleOfView, float verticalAngleOfView){
		this.image = image;
		this.nbRows = nbRows;
		this.nbColumns = nbColums;
		this.horizontalAngleOfView = horizontalAngleOfView;
		this.verticalAngleOfView = verticalAngleOfView;
		
		// vinden van bovenste punt van kubus.
		
		for (int i=0; i<this.nbRows; i++){
			if (this.corners.size() > 0) break;
			for (int j=0; j<this.nbColumns; j++){
				if ((image[(this.nbColumns*i+j)*3] & 0xFF) != 0){
					int[] coods = {j, i};
					this.corners.add(coods);
					break;
				}
			}
			
		}
		if (this.corners.isEmpty()){
			this.atLeastOneRed = false;
			this.surface = 0;
			this.center = null;
		}
		else{
		int currentX = this.corners.get(0)[0];
		int currentY = this.corners.get(0)[1];
		
		// 4 loops om uiterste hoeken van kubus te vinden:
		// de eerste beweegt rechtsonder, de tweede linksonder, etc.
		
		while (true){
			//als rechts en onder geen rood meer is.
			if ((currentX == this.nbColumns-1 || (image[(this.nbColumns*currentY + currentX)*3 + 3] & 0xFF) == 0) && 
					(currentY == this.nbRows-1 || (image[(this.nbColumns*currentY + currentX)*3 + 3*this.nbColumns] & 0xFF)==0)){
				
				int[] coods = {currentX,currentY};
				this.corners.add(coods);
				break;
			}
			//als rechts nog rood is.
			else if (currentX != this.nbColumns-1 && (image[(this.nbColumns*currentY + currentX)*3 + 3] & 0xFF) != 0){
				//als rechts een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 + 3]){
					int[] newCorner = {currentX,currentY};
					this.corners.add(newCorner);
					currentX +=1;
				}
				else currentX +=1;
			}
			// onder is rood.
			else{
				//als onder een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 + 3*this.nbColumns]){
					int[] newCorner = {currentX,currentY};
					this.corners.add(newCorner);
					currentY +=1;
				}
				else currentY +=1;
			}
		}
		
		while (true){
			//als links en onder geen rood meer is.
			if ((currentX == 0 || (image[(this.nbColumns*currentY + currentX)*3 - 3] & 0xFF) == 0) && 
					(currentY == this.nbRows-1 || (image[(this.nbColumns*currentY + currentX)*3 + 3*this.nbColumns] & 0xFF)==0)){
				int[] coods = {currentX,currentY};
				this.corners.add(coods);
				break;
			}
			//als onder nog rood is.
			else if (currentY != this.nbRows-1 && (image[(this.nbColumns*currentY + currentX)*3 + 3*this.nbColumns] & 0xFF) != 0){
				//als onder een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 + 3*this.nbColumns]){
					int[] newCorner = {currentX,currentY};
					this.corners.add(newCorner);
					currentY +=1;
				}
				else currentY +=1;
			}
			// links is rood.
			else{
				//als links een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 - 3]){
					int[] newCorner = {currentX,currentY};
					this.corners.add(newCorner);
					currentX -=1;
				}
				else currentX -=1;
			}
		}
		
		while (true){
			//als links en boven geen rood meer is.
			if ((currentX == 0 || (image[(this.nbColumns*currentY + currentX)*3 - 3] & 0xFF) == 0) && 
					(currentY == 0 || (image[(this.nbColumns*currentY + currentX)*3 - 3*this.nbColumns] & 0xFF)==0)){
				int[] coods = {currentX,currentY};
				this.corners.add(coods);
				break;
			}
			//als links nog rood is.
			else if (currentX != 0 && (image[(this.nbColumns*currentY + currentX)*3 - 3] & 0xFF) != 0){
				//als links een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 - 3]){
					int[] newCorner = {currentX,currentY};
					this.corners.add(newCorner);
					currentX -=1;
				}
				else currentX -=1;
			}
			// boven is rood.
			else{
				//als boven een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 - 3*this.nbColumns]){
					int[] newCorner = {currentX,currentY};
					this.corners.add(newCorner);
					currentY -=1;
				}
				else currentY -=1;
			}
		}
		
		while (true){
			//als rechts en boven geen rood meer is.
			if ((currentX == this.nbColumns-1 || (image[(this.nbColumns*currentY + currentX)*3 + 3] & 0xFF) == 0) && 
					(currentY == 0 || (image[(this.nbColumns*currentY + currentX)*3 - 3*this.nbColumns] & 0xFF)==0)){
				int[] coods = {currentX,currentY};
				this.corners.add(coods);
				break;
			}
			//als boven nog rood is.
			else if (currentY != 0 && (image[(this.nbColumns*currentY + currentX)*3 - 3*this.nbColumns] & 0xFF) != 0){
				//als boven een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 - 3*this.nbColumns]){
					int[] newCorner = {currentX,currentY};
					this.corners.add(newCorner);
					currentY -=1;
				}
				else currentY -=1;
			}
			// rehts is rood.
			else{
				//als rechts een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 + 3]){
					int[] newCorner = {currentX,currentY};
					this.corners.add(newCorner);
					currentX +=1;
				}
				else currentX +=1;
			}
		}

		int sumX = 0;
		int sumY = 0;
		int counter = 0;
		for (int i=0; i<this.nbRows; i++){
			for (int j=0; j<nbColumns; j++){
				if ((image[(nbColumns*i+j)*3] & 0xFF) != 0){
					int[] coods = {j, i};
					sumX += coods[0];
					sumY += coods[1];
					counter++;
				}
			}	
		}
		double X = (double)sumX/(double)counter;
		double Y = (double)sumY/(double)counter;
//		System.out.println("Cube in sight, centering at: " + X + "   " + Y);
		double[] center = {X-(double)this.nbColumns/2,-(Y-(double)this.nbRows/2)};
		this.center = center;
		this.surface = counter;
		}
	}

	
	public double getDistApprox(){
		double currentMaxAngle = 0;
		for(int i = 0; i<this.corners.size();i++){
			for(int j = 0; j< this.corners.size();j++){
				if(i!=j){
					double horAngle = this.horizontalAngleOfView*(Math.abs(this.corners.get(i)[0] - this.corners.get(j)[0]))/this.nbRows;
					double verAngle = this.verticalAngleOfView*(Math.abs(this.corners.get(i)[1] - this.corners.get(j)[1]))/this.nbColumns;
					double angle = Math.sqrt(Math.pow(horAngle, 2)+Math.pow(verAngle, 2));
					//use if angle is in grades
					//double angleR = angle/360*2*Math.PI;
					if (angle>currentMaxAngle) currentMaxAngle = angle;
				}
			}
		}
		double dist = (Math.sqrt(3)/2)/Math.tan(currentMaxAngle/2);
		return dist;
	}
	
	public double[] getCenter(){
		return this.center;
	}
	
	public int getSurface(){
		return this.surface;
	}
	
	public boolean middleIsRed(){
		if(this.image[((this.nbRows/2)*this.nbColumns+this.nbColumns/2)*3] != 0){
			return true;
		}
		return false;
	}
	
	public boolean atLeastOneRed(){
		return this.atLeastOneRed;
	}
	
}
