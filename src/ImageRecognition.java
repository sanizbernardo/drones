import java.util.ArrayList;
import java.util.Arrays;

/** Opmerkingen / TODO:
 * 	nog niet getest.
 * 	achter iedere byte vgl staat  & 0xFF , niet 100% zeker dat het zo moet.
 * 	 */
public class ImageRecognition {
	
	private final byte[]image;
	private final int nbRows;
	private final int nbColumns;
	private ArrayList<ArrayList<Integer>> corners;
	
	public ImageRecognition(byte[] image, int nbRows, int nbColums){
		this.image = image;
		this.nbRows = nbRows;
		this.nbColumns = nbColums;
		
		// vinden van bovenste punt van kubus.
		
		for (int i=0; i<this.nbRows; i++){
			for (int j=0; j<nbColumns; j++){
				if ((image[(nbColumns*j+i)*3] & 0xFF) != 0){
					ArrayList<Integer> coods = new ArrayList<Integer>(Arrays.asList(i,j));
					this.corners.add(coods);
					break;
				}
			}
		}
		int currentX = this.corners.get(0).get(0);
		int currentY = this.corners.get(0).get(1);
		
		// 4 loops om uiterste hoeken van kubus te vinden:
		// de eerste beweegt rechtsonder, de tweede linksonder, etc.
		
		while (true){
			//als rechts en onder geen rood meer is.
			if ((currentX == this.nbColumns-1 || (image[(this.nbColumns*currentY + currentX)*3 + 3] & 0xFF) == 0) && 
					(currentY == this.nbRows-1 || (image[(this.nbColumns*currentY + currentX)*3 + 3*this.nbColumns] & 0xFF)==0)){
				break;
			}
			//als rechts nog rood is.
			else if (currentX != this.nbColumns-1 && (image[(this.nbColumns*currentY + currentX)*3 + 3] & 0xFF) != 0){
				//als rechts een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 + 3]){
					ArrayList<Integer> newCorner = new ArrayList<Integer>(Arrays.asList(currentX,currentY));
					this.corners.add(newCorner);
					currentX +=1;
				}
				else currentX +=1;
			}
			// onder is rood.
			else{
				//als onder een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 + 3*this.nbColumns]){
					ArrayList<Integer> newCorner = new ArrayList<Integer>(Arrays.asList(currentX,currentY));
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
				break;
			}
			//als onder nog rood is.
			else if (currentY != this.nbRows-1 && (image[(this.nbColumns*currentY + currentX)*3 + 3*this.nbColumns] & 0xFF) != 0){
				//als onder een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 + 3*this.nbColumns]){
					ArrayList<Integer> newCorner = new ArrayList<Integer>(Arrays.asList(currentX,currentY));
					this.corners.add(newCorner);
					currentY +=1;
				}
				else currentY +=1;
			}
			// links is rood.
			else{
				//als links een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 - 3]){
					ArrayList<Integer> newCorner = new ArrayList<Integer>(Arrays.asList(currentX,currentY));
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
				break;
			}
			//als links nog rood is.
			else if (currentX != 0 && (image[(this.nbColumns*currentY + currentX)*3 - 3] & 0xFF) != 0){
				//als links een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 - 3]){
					ArrayList<Integer> newCorner = new ArrayList<Integer>(Arrays.asList(currentX,currentY));
					this.corners.add(newCorner);
					currentX -=1;
				}
				else currentX -=1;
			}
			// boven is rood.
			else{
				//als boven een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 - 3*this.nbColumns]){
					ArrayList<Integer> newCorner = new ArrayList<Integer>(Arrays.asList(currentX,currentY));
					this.corners.add(newCorner);
					currentY -=1;
				}
				else currentY -=1;
			}
		}
		
		while (true){
			//als rechts en boven geen rood meer is.
			if ((currentX == this.nbColumns-1 || (image[(200*currentY + currentX)*3 + 3] & 0xFF) == 0) && 
					(currentY == 0 || (image[(this.nbColumns*currentY + currentX)*3 - 3*this.nbColumns] & 0xFF)==0)){
				break;
			}
			//als boven nog rood is.
			else if (currentY != 0 && (image[(this.nbColumns*currentY + currentX)*3 - 3*this.nbColumns] & 0xFF) != 0){
				//als boven een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 - 3*this.nbColumns]){
					ArrayList<Integer> newCorner = new ArrayList<Integer>(Arrays.asList(currentX,currentY));
					this.corners.add(newCorner);
					currentY -=1;
				}
				else currentY -=1;
			}
			// rehts is rood.
			else{
				//als rechts een ander rood is.
				if (image[(this.nbColumns*currentY + currentX)*3] != image[(this.nbColumns*currentY + currentX)*3 + 3]){
					ArrayList<Integer> newCorner = new ArrayList<Integer>(Arrays.asList(currentX,currentY));
					this.corners.add(newCorner);
					currentX +=1;
				}
				else currentX +=1;
			}
		}
	}
	
	
}
