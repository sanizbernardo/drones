import java.util.ArrayList;

/** Opmerkingen / TODO:
 * 	nog niet getest.
 * 	achter iedere byte vgl staat  & 0xFF , niet 100% zeker dat het zo moet.
 * 	 */
public class ImageRecognition {
	
	private final byte[]image;
	private final int nbRows;
	private final int nbColumns;
	private final float horizontalAngleOfView;
	private final float verticalAngleOfView;
	private ArrayList<int[]> corners = new ArrayList<int[]>();
	
	public ImageRecognition(byte[] image, int nbRows, int nbColums, float horizontalAngleOfView, float verticalAngleOfView){
		this.image = image;
		this.nbRows = nbRows;
		this.nbColumns = nbColums;
		this.horizontalAngleOfView = horizontalAngleOfView;
		this.verticalAngleOfView = verticalAngleOfView;
		
		// vinden van bovenste punt van kubus.
		
		for (int i=0; i<this.nbRows; i++){
			if (this.corners.size() > 0) break;
			for (int j=0; j<nbColumns; j++){
				if ((image[(nbColumns*j+i)*3] & 0xFF) != 0){
					int[] coods = {i, j};
					this.corners.add(coods);
					break;
				}
			}
			
		}
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
		
	}

	
	public double getDistApprox(){
		double currentMaxAngle = 0;
		for(int i = 0; i<this.corners.size();i++){
			for(int j = 0; j< this.corners.size();j++){
				if(i!=j){
					double horAngle = this.horizontalAngleOfView*(Math.abs(this.corners.get(i)[0] - this.corners.get(j)[0]))/this.nbRows;
					double verAngle = this.verticalAngleOfView*(Math.abs(this.corners.get(i)[1] - this.corners.get(j)[1]))/this.nbColumns;
					double angle = Math.sqrt(Math.pow(horAngle, 2)+Math.pow(verAngle, 2));
					double angleR = angle/360*2*Math.PI;
					if (angleR>currentMaxAngle) currentMaxAngle = angleR;
				}
			}
		}
		double dist = (Math.sqrt(3)/2)/Math.tan(currentMaxAngle/2);
		return dist;
	}
	
	public double[] getCenter(){
		int sumX = 0;
		int sumY = 0;
		for(int[] i : this.corners){
			sumX += i[0];
			sumY += i[1];
		}
		double X = sumX/this.corners.size();
		double Y = sumY/this.corners.size();
		double[] result = {X-this.nbRows/2,-(Y-this.nbColumns/2)};
		return result;
	}
	
	public int getSurface(){
		int counter=0;
		for(int i = 0; i<this.nbColumns*this.nbRows*3;i+=3){
			if ((image[i] & 0xFF) != 0) counter++; 
		}
		return counter;
	}
}
