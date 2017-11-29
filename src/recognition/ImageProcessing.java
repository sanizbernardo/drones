package recognition;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Toon on 31/10/17
 */
public class ImageProcessing {

    private int imageWidth;
    private int imageHeight;
    private BufferedImage image;
    private double fieldOfView = 120;
    //TODO: pitch en jaw in constructor
    private final double pitch = Math.PI/4;
    private final double jaw = 0;
    private ArrayList<int[]> convexhull = null;

    //constructor in case of a byte[]
    public ImageProcessing(byte[] imageByte){
//    	InputStream in = new ByteArrayInputStream(imageByte);
//    	System.out.println(in == null);
//    	BufferedImage bImageFromConvert = null;
//		try {
//			bImageFromConvert = ImageIO.read(in);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(bImageFromConvert == null);
//    	this.image = bImageFromConvert;
    	
//    	this.imageWidth = 200;
//    	this.imageHeight = 200;
//    	final int bytes_per_pixel = 3;
//    	byte[] raw = new byte[this.imageWidth * this.imageHeight * bytes_per_pixel];
//    	BufferedImage image = new BufferedImage(this.imageWidth, this.imageHeight, BufferedImage.TYPE_3BYTE_BGR);
//    	IntBuffer intBuf
//    	        = ByteBuffer.wrap(raw)
//    	        .order(ByteOrder.LITTLE_ENDIAN)
//    	        .asIntBuffer();
//    	int[] array = new int[intBuf.remaining()];
//    	intBuf.get(array);
//    	image.setRGB(0, 0, this.imageWidth, this.imageHeight, array, 0, this.imageWidth);
    	
    	this.imageHeight = 200;
    	this.imageWidth = 200;
        BufferedImage imageOut = new BufferedImage(this.imageWidth, this.imageHeight, BufferedImage.TYPE_3BYTE_BGR);
		imageOut.getRaster().setDataElements(0, 0, this.imageWidth, this.imageHeight, imageByte);
		this.image = imageOut;
    }

    //constructor for local images (mainly testing purposes)
    public  ImageProcessing(String imgPath){
        try{
            this.image = ImageIO.read(new File(imgPath));
            this.imageHeight = this.image.getHeight();
            this.imageWidth = this.image.getWidth();
        }catch(IOException e){
            System.out.println("Software could not load the image");
        }
    }

    //Saves the passed image with the passed name in PNG format
    public void saveImage(BufferedImage image, String imageName){
        try{
            File outputFile = new File(imageName);
            ImageIO.write(image, "png", outputFile);
        }catch (IOException e){}
    }

    //Saves this.image with the passed name in PNG format
    public void saveImage(String imageName){
        try{
            ImageIO.write(this.image, "png", new File("outputFile.png"));
        }catch (IOException e){}
    }

    //Creates an image with the given pixels colored.
    public BufferedImage drawImage(ArrayList<int[]> pixels){
        BufferedImage drawing = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int nbPixels = pixels.size();
        for (int j = 0; j < nbPixels; j++){
            int[] pixel = pixels.get(j);
            drawing.setRGB(pixel[0], pixel[1], 0xFF0000);
        }
        return drawing;
    }

    //method for rgb to hsv conversion
    public float[] rgbConversion(int rgb){
        int clr =  rgb;
        int  red   = (clr & 0x00ff0000) >> 16;
        int  green = (clr & 0x0000ff00) >> 8;
        int  blue  =  clr & 0x000000ff;
        float[] hsv = new float[3];
        Color.RGBtoHSB(red, green, blue, hsv);
        //System.out.println(Arrays.toString(hsv));
        return hsv;
    }

    //method for checking black pixel
    //TODO change from black to white
    private boolean checkBg(float[] hsv){
        if(hsv[0] == 0 && hsv[1] == 0 && hsv[2] == 0){
            return true;
        } else {
            return false;
        }
    }

    //Creates the different cube objects
    public ArrayList<Cube> getObjects() {
        ArrayList<Cube> objects = new ArrayList<>();
        ArrayList<Float> colors = new ArrayList<>();
        for (int i = 0; i < this.imageWidth; i++) {
            for (int j = 0; j < this.imageHeight; j++) {
                float[] hsv = rgbConversion(this.image.getRGB(i, j));           //Retrieves the hsv color value of the pixel
                int[] pixel = {i, j};
                float h = hsv[0];
                if (! checkBg(hsv)) {                                           //Checks whether the pixel is black or not
                    if (! colors.contains(h)){                                  //Checks whether we already encountered a certain colortype
                        Cube newObject = new Cube(h);
                        objects.add(newObject);
                        newObject.addPixel(pixel);
                        colors.add(h);
                    } else {                                                    //Creates a new cube when finding new colortype
                        for (Cube cube : objects) {
                            if (cube.gethValue() == h){
                                cube.addPixel(pixel);
                            }
                        }
                    }
                }
            }
        }
        return objects;
    }

    //Approximate location of cube in world //TODO
    public double[] approximateLocation(Cube cube, int[] dronePos, int[] viewDirection){
        int[] dronePosition = {0,0,0};
        int[] averagePixel = cube.getAveragePixel();
        int[] vDirection = viewDirection;

        averagePixel[0] -= 100;
        averagePixel[1] = -averagePixel[1]+100;
        double anglePerPixel = getAnglePerPixel();

        double estimateHorAngle = estimateAngle(averagePixel[0]);
        double estimateVerAngle = estimateAngle(averagePixel[1]);
        double estimateDistance = guessDistance(cube);
        double totalAngle = Math.sqrt(estimateHorAngle*estimateHorAngle+estimateVerAngle*estimateVerAngle);

        System.out.println();
        double estimateX = dronePosition[0] + Math.sin(estimateHorAngle)*estimateDistance*Math.cos(estimateVerAngle);
        double estimateY = dronePosition[1] + Math.sin(estimateVerAngle)*estimateDistance*Math.cos(estimateHorAngle);
        //double estimateZ = -(dronePosition[2] + Math.cos(totalAngle)*estimateDistance);
        double estimateZ = (-1) * estimateDistance*Math.cos(estimateVerAngle)*Math.cos(estimateHorAngle);
        
        double[] approx = {estimateX, estimateY, estimateZ};
        //prints for testing:
        //System.out.println("horAngle : " + Math.toDegrees(estimateHorAngle));
        //System.out.println("verAngle : " + Math.toDegrees(estimateVerAngle));
        //System.out.println("totAngle : " + Math.toDegrees(totalAngle));
        //System.out.println("distance : " + estimateDistance);
        //System.out.println("cubeCoords : " + Arrays.toString(approx));
        return approx;
    }
    
    private double estimateAngle(double Xco){
    	double angle = Math.atan((Xco/100)*Math.tan(Math.PI/3));
    	return angle;
    }

    public double guessDistance(Cube cube){ //TODO check
        ArrayList<int[]> pixels = this.getConvexHull(cube);
        double currentMaxAngle = 0;
        
//        oudere methodes:
        double largestDistance = -1;
        int[] pixelOne = null;
        int[] pixelTwo =  null;
        for (int[] pixel1 : pixels){
            for (int[] pixel2 : pixels){
                double distance = Math.sqrt(Math.pow((pixel1[0]-pixel2[0]),2) + Math.pow((pixel1[1]-pixel2[1]),2));
                if (distance > largestDistance){
                    largestDistance = distance;
                    pixelOne = pixel1;
                    pixelTwo = pixel2;
                }
            }
        }
        System.out.println(pixelOne[0] + "     " + pixelOne[1]);
        System.out.println(pixelTwo[0] + "     " + pixelTwo[1]);
        
        if (pixelOne[0] > pixelTwo[0]){
        	pixelOne[0] += 1;
        }
        else {
        	pixelTwo[0] +=1;
        }
        if (pixelOne[1] > pixelTwo[1]){
        	pixelOne[1] += 1;
        }
        else {
        	pixelTwo[1] +=1;
        }
        int[] averagePixel = cube.getAveragePixel();
        double angleX = calculateAngleX(pixelOne[0], pixelTwo[0]);
        System.out.println("x1:   " + angleX);
        angleX = 2*Math.atan(Math.cos(calculateAngleY(100, averagePixel[1]))*Math.tan(angleX/2));
        System.out.println("x2:   " + angleX + "     " + Math.cos(calculateAngleY(100, averagePixel[1])));
        double angleY = calculateAngleY(pixelOne[1], pixelTwo[1]);
        angleY = 2*Math.atan(Math.cos(calculateAngleX(100, averagePixel[0]))*Math.tan(angleY/2));
        double totAngle = Math.sqrt(angleX*angleX + angleY*angleY);
        
        System.out.println(Math.toDegrees(angleX));
        System.out.println(Math.toDegrees(angleY));
        System.out.println(Math.toDegrees(totAngle));
        double sum = 0;
        for(double i = 0; i < 199; i++){
        	sum += calculateAngleX(i, i+1);
        }
        System.out.println("should be 2PI/3: " + sum);
        return (Math.sqrt(3)/2)/Math.tan(totAngle/2);
        
//        for(int i = 0; i<pixels.size();i++){
//            for(int j = 0; j< pixels.size();j++){
//                if(i!=j){
//                    double horAngle = Math.toRadians(getFieldOfView())*(Math.abs(pixels.get(i)[0] - pixels.get(j)[0]))/getImageWidth();
//                    double verAngle = Math.toRadians(getFieldOfView())*(Math.abs(pixels.get(i)[1] - pixels.get(j)[1]))/getImageHeight();
//                    double angle = Math.sqrt(Math.pow(horAngle, 2)+Math.pow(verAngle, 2));
//                    if (angle>currentMaxAngle) currentMaxAngle = angle;
//                }
//            }
//        }
//        double dist = Math.abs((Math.sqrt(3)/2)/Math.tan(currentMaxAngle/2));
//        return dist;
        
//        ArrayList<int[]> diffColors = new ArrayList<int[]>();
//        ArrayList<Integer> diffColorsAmounts = new ArrayList<Integer>();
//        for(int[] pixel : pixels){
//        	if (!contains(pixel, diffColors)){
//        		diffColors.add(pixel);
//        		diffColorsAmounts.add(1);
//        	}
//        	else{
//        		int index = indexOf(pixel,diffColors);
//        		int temp = diffColorsAmounts.get(index);
//        		temp +=1;
//        		diffColorsAmounts.set(index, temp);
//        	}
//        }
//        Collections.sort(diffColorsAmounts);
//        Collections.reverse(diffColorsAmounts);
//        //TODO:: ms ipv te kijken naar de meeste berekenen welke kleur de 'voorste' is.
//        double biggest = diffColorsAmounts.get(0)/(Math.cos(pitch)*Math.cos(pitch))/(Math.cos(jaw)*Math.cos(jaw));
//        System.out.println(cube.getNbPixels());
//        double side = Math.sqrt(biggest);
//    	double x = cube.getAveragePixel()[0];
//        //side = side/Math.cos(Math.atan(((x - 99)/100)*Math.tan(Math.PI/3)));
//        double angle = calculateAngle(side, cube.getAveragePixel());
//        //double angle = side*getAnglePerPixel();
//        double dist = 0.5/Math.tan(angle/2);
//        if(diffColorsAmounts.size() > 1){
//        	double second = diffColorsAmounts.get(1);
//        	double secondRatio = second/(biggest+second);
//        	double secondAngle = secondRatio*Math.PI/2;
//        	dist = dist/Math.cos(secondAngle);
//        }
//        if(diffColorsAmounts.size() > 2){
//        	double third = diffColorsAmounts.get(2);
//        	double thirdRatio = third/(biggest+third);
//        	double thirdAngle = thirdRatio*Math.PI/2;
//        	dist = dist/Math.cos(thirdAngle);
//        }
//        return dist + 0.5;
        
    }
    
    //TODO opkuisen
    private double calculateAngle(double side, int[] averagePixel) {
    	//double x = averagePixel[0];
    	//x -= 99;
    	double x = 100*Math.tan(pitch)/Math.tan(Math.PI/3);
    	double anglePerPixel = Math.atan(((x+1)/100)*Math.tan(Math.PI/3)) - Math.atan((x/100)*Math.tan(Math.PI/3));
    	//double anglePerPixel = Math.atan(((0.0+1)/100)*Math.tan(Math.PI/3)) - Math.atan((0.0/100)*Math.tan(Math.PI/3));
    	System.out.println(anglePerPixel);
		return side*anglePerPixel;
	}
    
    private double calculateAngleX(double x1, double x2) {
    	//double x = averagePixel[0];
    	//x -= 99;
    	double angle = Math.atan(((x2-100)/100)*Math.tan(Math.PI/3)) - Math.atan(((x1-100)/100)*Math.tan(Math.PI/3));
    	//double anglePerPixel = Math.atan(((0.0+1)/100)*Math.tan(Math.PI/3)) - Math.atan((0.0/100)*Math.tan(Math.PI/3));
		return Math.abs(angle);
	}
    
    private double calculateAngleY(double y1, double y2) {
    	//double x = averagePixel[0];
    	//x -= 99;
    	double angle = Math.atan(((y2-100)/100)*Math.tan(Math.PI/3)) - Math.atan(((y1-100)/100)*Math.tan(Math.PI/3));
    	//double anglePerPixel = Math.atan(((0.0+1)/100)*Math.tan(Math.PI/3)) - Math.atan((0.0/100)*Math.tan(Math.PI/3));
		return Math.abs(angle);
	}

	private int indexOf(int[] pixel, ArrayList<int[]> diffColors) {
		int index = 0;
		while(index < diffColors.size()){
			if(getRGB(pixel[0], pixel[1]) == getRGB(diffColors.get(index)[0],diffColors.get(index)[1])) return index;
			index +=1;
		}
		return -1;
	}

	private boolean contains(int[] pixel, ArrayList<int[]> diffColors) {
		for(int[] otherPixel : diffColors){
			if(getRGB(pixel[0], pixel[1]) == getRGB(otherPixel[0],otherPixel[1]))return true;
		}
		return false;
	}


	public ArrayList<int[]> getConvexHull(Cube cube){
		if (convexhull != null){
			return convexhull;
		}
		else{
			ArrayList<int[]> pixels = cube.getPixels();
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
					if((endPoint[0] == pointOnHull[0] && endPoint[1] == pointOnHull[1]) || orientation(pointOnHull, pixels.get(i), endPoint) == 2){
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
			return hull;
		}
	}
	
    private int orientation(int[] p, int[] q, int[] r) {
    	int val = (q[1] - p[1]) * (r[0] - q[0]) -
                (q[0] - p[0]) * (r[1] - q[1]);
    
        if (val == 0) return 0;  // collinear
        return (val > 0)? 1: 2; // clock or counterclock wise
	}

	//some getters
    private double getAnglePerPixel(){
            return Math.toRadians(this.fieldOfView/200);
    }

    public boolean isOnBorder(int[] pixel){
    	if(pixel[0] == 0 || pixel[0] == 199 || pixel[1] == 0 || pixel[1] == 199) return true;
    	return false;
    }
    
    public boolean touches(ArrayList<int[]> hull){
    	for(int[] pixel : hull){
            float[] hsv = rgbConversion(this.image.getRGB(pixel[0], pixel[1]));
            float h = hsv[0];
            float[] hsv2 = rgbConversion(this.image.getRGB(pixel[0]-1, pixel[1]-1));
            float h2 = hsv[0];
            if(!checkBg(hsv2) && h !=h2) return true;
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]-1, pixel[1]));
            h2 = hsv[0];
            if(!checkBg(hsv2) && h !=h2) return true;
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]-1, pixel[1]+1));
            h2 = hsv[0];
            if(!checkBg(hsv2) && h !=h2) return true;
            hsv2 = rgbConversion(this.image.getRGB(pixel[0], pixel[1]-1));
            h2 = hsv[0];
            if(!checkBg(hsv2) && h !=h2) return true;
            hsv2 = rgbConversion(this.image.getRGB(pixel[0], pixel[1]+1));
            h2 = hsv[0];
            if(!checkBg(hsv2) && h !=h2) return true;
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]+1, pixel[1]-1));
            h2 = hsv[0];
            if(!checkBg(hsv2) && h !=h2) return true;
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]+1, pixel[1]));
            h2 = hsv[0];
            if(!checkBg(hsv2) && h !=h2) return true;
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]+1, pixel[1]+1));
            h2 = hsv[0];
            if(!checkBg(hsv2) && h !=h2) return true;
    	}
 
    	return false;
    }
    
    
    public double getFieldOfView(){
        return fieldOfView;
    }

    public int getImageWidth(){
        return imageWidth;
    }

    public int getImageHeight(){
        return imageHeight;
    }

    public int getRGB(int x,int y){
        return this.image.getRGB(x,y);
    }

    public BufferedImage getImage() {
        return image;
    }
}
