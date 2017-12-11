package recognition;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import javafx.scene.effect.FloatMapBuilder;
import utils.FloatMath;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Toon en Tomas on 31/10/17
 */
public class ImageProcessing {

    private int imageWidth;
    private int imageHeight;
    private BufferedImage image;
    private double fieldOfView = 120;
    private final float pitch;
    private final float heading;
    private final float roll;
    private ArrayList<Cube> cubes;
    private final Matrix3f transMat;
    private final float[] dronePosition;

    //constructor in case of a byte[]
    public ImageProcessing(byte[] imageByte, float pitch, float heading, float roll, float[] dronePosition){
//    	InputStream in = new ByteArrayInputStream(imageByte);
//    	System.out.println(in == null);
//    	BufferedImage bImageFromConvert = null;
//		try {
//			bImageFromConvert = ImageIO.read(in);
//		} catch (IOException e) {
//			// TOD Auto-generated catch block
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
		this.heading = heading;
		this.pitch = pitch;
		this.roll = roll;
		this.dronePosition = dronePosition;
		
		this.transMat = new Matrix3f().identity();
		if (Math.abs(this.heading) > 1E-6)
			transMat.rotate(heading, new Vector3f(0, 1, 0));
		if (Math.abs(this.pitch) > 1E-6)
			transMat.rotate(pitch, new Vector3f(1, 0, 0));
		if (Math.abs(this.roll) > 1E-6)
			transMat.rotate(roll, new Vector3f(0, 0, 1));
		this.transMat.invert();
		Vector3f pos = FloatMath.transform(transMat, new Vector3f(1,2,3));
			 	
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
        this.pitch=0;
        this.heading=0;
        this.roll=0;
        this.transMat=null;
        this.dronePosition = null;
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
            ImageIO.write(this.image, "png", new File(imageName));
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
    private boolean checkBg(float[] hsv){
        if(hsv[0] == 0 && hsv[1] == 0 && hsv[2] == 1){
            return true;
        } else {
            return false;
        }
    }

    //Creates the different cube objects
    public ArrayList<Cube> getObjects() {
        ArrayList<Cube> objects = new ArrayList<>();
        ArrayList<float[]> colors = new ArrayList<>();
        for (int i = 0; i < this.imageWidth; i++) {
            for (int j = 0; j < this.imageHeight; j++) {
                float[] hsv = rgbConversion(this.image.getRGB(i, j));           //Retrieves the hsv color value of the pixel
                int[] pixel = {i, j};
                float h = hsv[0];
                float s = hsv[1];
                if (! checkBg(hsv)) {                                           //Checks whether the pixel is black or not
                    if (! contains2(colors, h, s)){                                  //Checks whether we already encountered a certain colortype
                        Cube newObject = new Cube(h, s);
                        objects.add(newObject);
                        newObject.addPixel(pixel);
                        float[] newColor = {h, s};
                        colors.add(newColor);
                    } else {                                                    //Creates a new cube when finding new colortype
                        for (Cube cube : objects) {
                            if (cube.gethValue() == h && cube.getsValue() == s){
                                cube.addPixel(pixel);
                            }
                        }
                    }
                }
            }
        }
        this.cubes = objects;
        return objects;
    }

    private boolean contains2(ArrayList<float[]> colors, float h, float s) {
    	for(float[] color : colors){
    		//TODO die 0.01?? der is ergens een afrondingsfout, hoe kunt ge die dan vinde
    		//     misschien de maximale fout bij die rgbconversion berekenen.
    		//     en ook nog is checke da ge voor h hetzelfde moet doen
    		if(h == color[0] && s < color[1] + 0.01 && s > color[1] - 0.01) return true;
    	}
    	return false;
	}

    
    public ArrayList<Cube> generateLocations(){
    	ArrayList<Cube> retList = new ArrayList<Cube>();
    	ArrayList<Cube> ignoreList = new ArrayList<Cube>();
    	for( Cube newCube : this.cubes){
    		if(isOnBorder(newCube)) ignoreList.add(newCube);
    		else{
    			ArrayList<float[]> touchesColors = touchesCubes(newCube);
    			if(touchesColors.size() > 0){
    				for(float[] color : touchesColors){
    					Cube otherCube = findCube(color);
    					if (otherCube.getNbPixels() < newCube.getNbPixels()){
    						ignoreList.add(otherCube);
    					}
    					else ignoreList.add(newCube);
    				}
    			}
    		}
    	}
    	for(Cube newCube : this.cubes){
    		if(!ignoreList.contains(newCube)) retList.add(newCube);
    	}
    	
    	for(Cube cube : retList){
    		double[] toTransform = approximateLocation(cube);
    		Vector3f pos = FloatMath.transform(transMat, new Vector3f((float)toTransform[0],(float)toTransform[1],(float)toTransform[2]));
    		
    		float[] location = {this.dronePosition[0]+ pos.x,this.dronePosition[1]+pos.y,this.dronePosition[2]+pos.z};
    		cube.setLocation(location);
    	}
    	
    	Collections.sort(retList, new Comparator<Cube>() {
            @Override
            public int compare(Cube cube1, Cube cube2)
            {

                return  Double.compare(cube1.getDist(), cube2.getDist());
            }
        });
    	return retList;
    }
    
	private Cube findCube(float[] color) {
		for(Cube cube : this.cubes){
			if(cube.gethValue() == color[0] && cube.getsValue() > color[1] -0.01 
					&& cube.getsValue() < color[1] +0.01) return cube;
		}
		return null;
	}

	//Approximate location of cube in world 
    public double[] approximateLocation(Cube cube){
        int[] averagePixel = cube.getAveragePixel();

        //averagePixel[0] -= 100;
        //averagePixel[1] = -averagePixel[1]+100;
        double anglePerPixel = getAnglePerPixel();

        //double estimateHorAngle = estimateAngle(averagePixel[0]);
        double estimateHorAngle1 = calculateAngleX(99, averagePixel[0]);
        double estimateHorAngle2 = 2*Math.atan(Math.cos(calculateAngleY(99, averagePixel[1]))*Math.tan(estimateHorAngle1/2));
        //double estimateVerAngle = estimateAngle(averagePixel[1]);
        double estimateVerAngle = calculateAngleY(99, averagePixel[1]);
        estimateVerAngle = 2*Math.atan(Math.cos(calculateAngleX(99, averagePixel[0]))*Math.tan(estimateVerAngle/2));
        double estimateDistance = guessDistance(cube);
        System.out.println(estimateDistance);
        //double totalAngle = Math.sqrt(estimateHorAngle*estimateHorAngle+estimateVerAngle*estimateVerAngle);

        double estimateX = Math.sin(estimateHorAngle2)*estimateDistance;
        double estimateY = Math.sin(estimateVerAngle)*estimateDistance;
        //double estimateZ = -(dronePosition[2] + Math.cos(totalAngle)*estimateDistance);
        double estimateZ = (-1) * estimateDistance*Math.cos(estimateVerAngle)*Math.cos(estimateHorAngle1);
        //double estimateZ = (-1) * Math.sqrt(estimateDistance*estimateDistance - 
        //		Math.sqrt(estimateX*estimateX + estimateY*estimateY)*Math.sqrt(estimateX*estimateX + estimateY*estimateY));
        
        if(averagePixel[0] < 100){
        	estimateX *= -1;
        }
        if(averagePixel[1] > 100){
        	estimateY *= -1;
        }
        
        double[] approx = {estimateX, estimateY, estimateZ};
        System.out.println(approx[0] + "  " + approx[1] + "  " + approx[2] );
		
        //prints for testing:
        //System.out.println("horAngle : " + Math.toDegrees(estimateHorAngle));
        //System.out.println("verAngle : " + Math.toDegrees(estimateVerAngle));
        //System.out.println("totAngle : " + Math.toDegrees(totalAngle));
        //System.out.println("distance : " + estimateDistance);
        System.out.println("cubeCoords : " + Arrays.toString(approx));
        return approx;
    }
    
    private double estimateAngle(double Xco){
    	double angle = Math.atan((Xco/100)*Math.tan(Math.PI/3));
    	return angle;
    }

    public double guessDistance(Cube cube){
    	//double start = System.currentTimeMillis();
        ArrayList<int[]> pixels = cube.getConvexHull();
    	//double end = System.currentTimeMillis();
    	//System.out.println(end-start);
        double currentMaxAngle = 0;
        //System.out.println("hier");
        
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
//        System.out.println(pixelOne[0] + "     " + pixelOne[1]);
//        System.out.println(pixelTwo[0] + "     " + pixelTwo[1]);
        
        boolean same = sameColor(pixelOne, pixelTwo);
        
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
//        System.out.println("x1:   " + angleX);
        angleX = 2*Math.atan(Math.cos(calculateAngleY(99, averagePixel[1]))*Math.tan(angleX/2));
//        System.out.println("x2:   " + angleX + "     " + Math.cos(calculateAngleY(100, averagePixel[1])));
        double angleY = calculateAngleY(pixelOne[1], pixelTwo[1]);
        angleY = 2*Math.atan(Math.cos(calculateAngleX(99, averagePixel[0]))*Math.tan(angleY/2));
        double totAngle = Math.sqrt(angleX*angleX + angleY*angleY);
        
//        System.out.println(Math.toDegrees(angleX));
//        System.out.println(Math.toDegrees(angleY));
//        System.out.println(Math.toDegrees(totAngle));
        double sum = 0;
        for(double i = 0; i < 199; i++){
        	sum += calculateAngleX(i, i+1);
        }
//        System.out.println("should be 2PI/3: " + sum);
        if(same){
        	double dist = (Math.sqrt(2)/2)/Math.tan(totAngle/2) + 0.5;
        	cube.setDist(dist);
        	return dist;
        }
        double dist = (Math.sqrt(3)/2)/Math.tan(totAngle/2);
    	cube.setDist(dist);
        return dist;
        
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
//        //TOD:: ms ipv te kijken naar de meeste berekenen welke kleur de 'voorste' is.
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
    
    private boolean sameColor(int[] pixelOne, int[] pixelTwo){
    	float[] hsv1 = rgbConversion(this.image.getRGB(pixelOne[0], pixelOne[1])); 
		float[] hsv2 = rgbConversion(this.image.getRGB(pixelTwo[0], pixelTwo[1]));
		float v1 = hsv1[2];
		float v2 = hsv2[2];
		if(v1 == v2) return true;
		return false;
	}

	//TODO opkuisen
    private double calculateAngle(double side, int[] averagePixel) {
    	//double x = averagePixel[0];
    	//x -= 99;
    	double x = 100*Math.tan(pitch)/Math.tan(Math.PI/3);
    	double anglePerPixel = Math.atan(((x+1)/100)*Math.tan(Math.PI/3)) - Math.atan((x/100)*Math.tan(Math.PI/3));
    	//double anglePerPixel = Math.atan(((0.0+1)/100)*Math.tan(Math.PI/3)) - Math.atan((0.0/100)*Math.tan(Math.PI/3));
//    	System.out.println(anglePerPixel);
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


	

	//some getters
    private double getAnglePerPixel(){
            return Math.toRadians(this.fieldOfView/200);
    }

    public boolean isOnBorder(Cube cube){
    	ArrayList<int[]> hull = cube.getConvexHull();
    	for(int[] pixel : hull){
        	if(pixel[0] == 0 || pixel[0] == 199 || pixel[1] == 0 || pixel[1] == 199) return true;
    	}
    	return false;
    }
    
    public ArrayList<float[]> touchesCubes(Cube cube){
    	ArrayList<int[]> hull = cube.getConvexHull();
    	ArrayList<float[]> retList = new ArrayList<float[]>();
    	for(int[] pixel : hull){
            float[] hsv = rgbConversion(this.image.getRGB(pixel[0], pixel[1]));
            float h = hsv[0];
            float s = hsv[1];
            float[] hsv2 = rgbConversion(this.image.getRGB(pixel[0]-1, pixel[1]-1));
            float h2 = hsv2[0];
            float s2 = hsv2[1];
            if(!checkBg(hsv2) && (h != h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]-1, pixel[1]));
            h2 = hsv2[0];
            s2 = hsv2[1];
            if(!checkBg(hsv2) && (h !=h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]-1, pixel[1]+1));
            h2 = hsv2[0];
            s2 = hsv2[1];
            if(!checkBg(hsv2) && (h !=h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
            hsv2 = rgbConversion(this.image.getRGB(pixel[0], pixel[1]-1));
            h2 = hsv2[0];
            s2 = hsv2[1];
            if(!checkBg(hsv2) && (h !=h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
            hsv2 = rgbConversion(this.image.getRGB(pixel[0], pixel[1]+1));
            h2 = hsv2[0];
            s2 = hsv2[1];
            if(!checkBg(hsv2) && (h !=h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]+1, pixel[1]-1));
            h2 = hsv2[0];
            s2 = hsv2[1];
            if(!checkBg(hsv2) && (h !=h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]+1, pixel[1]));
            h2 = hsv2[0];
            s2 = hsv2[1];
            if(!checkBg(hsv2) && (h !=h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]+1, pixel[1]+1));
            h2 = hsv2[0];
            s2 = hsv2[1];
            if(!checkBg(hsv2) && (h !=h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
    	}
 
    	return retList;
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
