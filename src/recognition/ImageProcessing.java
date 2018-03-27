package recognition;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import utils.FloatMath;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.OptionalDouble;

/**
 * Created by Toon en Tomas on 31/10/17
 */
public class ImageProcessing {

    private int imageWidth;
    private int imageHeight;
    private BufferedImage image;
    private double fieldOfView = 120;
    private float pitch;
    private float heading;
    private float roll;
    private ArrayList<Cube> cubes;
    private Matrix3f transMat;
    private float[] dronePosition;

    //constructor in case of a byte[]
    public ImageProcessing(){  			 	
    }
    
    public void addNewImage(byte[] imageByte, float pitch, float heading, float roll, float[] dronePosition){
    	//initializing
    	
    	this.imageHeight = 200;
    	this.imageWidth = 200;
        BufferedImage imageOut = new BufferedImage(this.imageWidth, this.imageHeight, BufferedImage.TYPE_3BYTE_BGR);
		imageOut.getRaster().setDataElements(0, 0, this.imageWidth, this.imageHeight, imageByte);
		this.image = imageOut;
		this.heading = heading;
		this.pitch = pitch;
		this.roll = roll;
		this.dronePosition = dronePosition;
		
		//transformation matrix for axial system from drone (z in view direction) to world
		
		this.transMat = new Matrix3f().identity();
		if (Math.abs(-this.roll) > 1E-6)
			transMat.rotate(-this.roll, new Vector3f(0, 0, 1));
		if (Math.abs(-this.pitch) > 1E-6)
			transMat.rotate(-this.pitch, new Vector3f(1, 0, 0));
		if (Math.abs(-this.heading) > 1E-6)
			transMat.rotate(-this.heading, new Vector3f(0, 1, 0));
		this.transMat.invert();
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

    //method for checking background (currently black)
    private boolean checkBg(float[] hsv){
        if(hsv[0] == 0 && hsv[1] == 0 && hsv[2] == 1){
            return true;
        } else {
            return false;
        }
    }

    //Creates the different cube objects and stores pixels belonging to said object
    public ArrayList<Cube> getObjects() {
        ArrayList<Cube> objects = new ArrayList<>();
        ArrayList<float[]> colors = new ArrayList<>();
        for (int i = 0; i < this.imageWidth; i++) {
            for (int j = 0; j < this.imageHeight; j++) {
                float[] hsv = rgbConversion(this.image.getRGB(i, j));           //Retrieves the hsv color value of the pixel
                int[] pixel = {i, j};
                float h = hsv[0];
                float s = hsv[1];
                if (! checkBg(hsv) && ! checkGround(hsv)) {                                           //Checks whether the pixel is background
                    if (! contains2(colors, h, s)){                             //Checks whether we already encountered a certain colortype
                        Cube newObject = new Cube(h, s);						//creates new cube obect
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

    private boolean checkGround(float[] hsv) {
		float v = hsv[2];
    	return v > 0.5;
	}

	// checks if a color (given h and s) is in a list of colors
    private boolean contains2(ArrayList<float[]> colors, float h, float s) {
    	for(float[] color : colors){
    		//TODO die 0.01?? der is ergens een afrondingsfout, hoe kunt ge die dan vinde
    		//     misschien de maximale fout bij die rgbconversion berekenen.
    		//     en ook nog is checke da ge voor h hetzelfde moet doen
    		if(h == color[0] && s < color[1] + 0.01 && s > color[1] - 0.01) return true;
    	}
    	return false;
	}

    //method that generates the locations (an approximate) of each cube
    public ArrayList<Cube> generateLocations(){
    	getObjects();
    	ArrayList<Cube> retList = new ArrayList<Cube>();
    	ArrayList<Cube> ignoreList = new ArrayList<Cube>();
    	if(this.cubes == null) return retList;
    	
    	//if a cube is behind another one and touches that cube on the image or the cube touches the border
    	//that cube is added to a list of cubes we ignore.
    	for( Cube newCube : this.cubes){
    		if(isOnBorder(newCube)) ignoreList.add(newCube);
    		else{
    			ArrayList<float[]> touchesColors = touchesCubes(newCube);
    			if(touchesColors.size() > 0){
    				for(float[] color : touchesColors){
    					Cube otherCube = findCube(color);
    					//each cube has equal size -> the one with the biggest size is the closest one
    					if(otherCube == null) {
    						continue;
    					}
    					if (otherCube.getNbPixels() < newCube.getNbPixels()){
    						ignoreList.add(otherCube);
    					}
    					else ignoreList.add(newCube);
    				}
    			}
    		}
    	}
    	
    	//all other cubes are added to a return list
    	for(Cube newCube : this.cubes){
    		if(!ignoreList.contains(newCube)) retList.add(newCube);
    	}
    	
    	//We calculate the approximate location for each cube in the return list
    	for(Cube cube : retList){
    		double[] toTransform = approximateLocation(cube);
    		Vector3f pos = FloatMath.transform(transMat, new Vector3f((float)toTransform[0],(float)toTransform[1],(float)toTransform[2]));
    		
    		float[] location = {this.dronePosition[0]+ pos.x,this.dronePosition[1]+pos.y,this.dronePosition[2]+pos.z};
    		cube.setLocation(location);
    	}
    	
    	//sorted by approximate distance
    	Collections.sort(retList, new Comparator<Cube>() {
            @Override
            public int compare(Cube cube1, Cube cube2)
            {

                return  Double.compare(cube1.getDist(), cube2.getDist());
            }
        });
    	return retList;
    }
    
    //if a cube with a certain color exists in this.colors, return that cube
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

        double estimateHorAngle1 = calculateAngleX(99, averagePixel[0]);
        double estimateHorAngle2 = 2*Math.atan(Math.cos(calculateAngleY(99, averagePixel[1]))*Math.tan(estimateHorAngle1/2));
        double estimateVerAngle = calculateAngleY(99, averagePixel[1]);
        estimateVerAngle = 2*Math.atan(Math.cos(calculateAngleX(99, averagePixel[0]))*Math.tan(estimateVerAngle/2));
        double estimateDistance = guessDistance(cube);

        double estimateX = Math.sin(estimateHorAngle2)*estimateDistance;
        double estimateY = Math.sin(estimateVerAngle)*estimateDistance;
        double estimateZ = (-1) * estimateDistance*Math.cos(estimateVerAngle)*Math.cos(estimateHorAngle1);
        
        if(averagePixel[0] < 100){
        	estimateX *= -1;
        }
        if(averagePixel[1] > 100){
        	estimateY *= -1;
        }
        
        double[] approx = {estimateX, estimateY, estimateZ};
        return approx;
    }
    
    //method that guesse a distance from the drone to a cube
    public double guessDistance(Cube cube){
        ArrayList<int[]> pixels = cube.getConvexHull();
        
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
        
        boolean same = sameColor(pixelOne, pixelTwo);
        
        int[] newPixelOne = pixelOne.clone();
        int[] newPixelTwo = pixelTwo.clone();
   	
        if (pixelOne[0] > pixelTwo[0]){
        	pixelOne[0] += 1;
        	newPixelOne[0] += 1;
        }
        else {
        	pixelTwo[0] +=1;
        	newPixelTwo[0] +=1;
        }
        if (pixelOne[1] > pixelTwo[1]){
        	pixelOne[1] += 1;
        	newPixelOne[1] += 1;
        }
        else {
        	pixelTwo[1] +=1;
        	newPixelTwo[1] +=1;
        }
        
        pixelOne = newPixelOne;
        pixelTwo = newPixelTwo;
        
        int[] averagePixel = cube.getAveragePixel();
        double angleX = calculateAngleX(pixelOne[0], pixelTwo[0]);
        angleX = 2*Math.atan(Math.cos(calculateAngleY(99, averagePixel[1]))*Math.tan(angleX/2));

        double angleY = calculateAngleY(pixelOne[1], pixelTwo[1]);
        angleY = 2*Math.atan(Math.cos(calculateAngleX(99, averagePixel[0]))*Math.tan(angleY/2));
        
        double totAngle = Math.sqrt(angleX*angleX + angleY*angleY);
        
        if(same){
        	double dist = (5*Math.sqrt(2)/2)/Math.tan(totAngle/2) + 2.5;
        	cube.setDist(dist);
        	return dist;
        }
        double dist = (5*Math.sqrt(3)/2)/Math.tan(totAngle/2);
    	cube.setDist(dist);
        return dist;        
    }
    
    //checks whether two pixels are the same color 
    private boolean sameColor(int[] pixelOne, int[] pixelTwo){
    	float[] hsv1 = rgbConversion(this.image.getRGB(pixelOne[0], pixelOne[1])); 
		float[] hsv2 = rgbConversion(this.image.getRGB(pixelTwo[0], pixelTwo[1]));
		float v1 = hsv1[2];
		float v2 = hsv2[2];
		if(v1 == v2) return true;
		return false;
	}
    
    //calculates angle between two x coordinates
    private double calculateAngleX(double x1, double x2) {
    	double angle = Math.atan(((x2-100)/100)*Math.tan(Math.PI/3)) - Math.atan(((x1-100)/100)*Math.tan(Math.PI/3));
		return Math.abs(angle);
	}

    //calculates angle between two y coordinates
    private double calculateAngleY(double y1, double y2) {
    	double angle = Math.atan(((y2-100)/100)*Math.tan(Math.PI/3)) - Math.atan(((y1-100)/100)*Math.tan(Math.PI/3));
		return Math.abs(angle);
	}
	

    //checks if cube is on the border of the image
    public boolean isOnBorder(Cube cube){
    	ArrayList<int[]> hull = cube.getConvexHull();
    	for(int[] pixel : hull){
        	if(pixel[0] == 0 || pixel[0] == 199 || pixel[1] == 0 || pixel[1] == 199) return true;
    	}
    	return false;
    }
    
    //returns a list of colors that touch a given cube
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
            if(!checkBg(hsv2) && !checkGround(hsv2) && (h != h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]-1, pixel[1]));
            h2 = hsv2[0];
            s2 = hsv2[1];
            if(!checkBg(hsv2) && !checkGround(hsv2) && (h !=h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]-1, pixel[1]+1));
            h2 = hsv2[0];
            s2 = hsv2[1];
            if(!checkBg(hsv2) && !checkGround(hsv2) && (h !=h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
            hsv2 = rgbConversion(this.image.getRGB(pixel[0], pixel[1]-1));
            h2 = hsv2[0];
            s2 = hsv2[1];
            if(!checkBg(hsv2) && !checkGround(hsv2) && (h !=h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
            hsv2 = rgbConversion(this.image.getRGB(pixel[0], pixel[1]+1));
            h2 = hsv2[0];
            s2 = hsv2[1];
            if(!checkBg(hsv2) && !checkGround(hsv2) && (h !=h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]+1, pixel[1]-1));
            h2 = hsv2[0];
            s2 = hsv2[1];
            if(!checkBg(hsv2) && !checkGround(hsv2) && (h !=h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]+1, pixel[1]));
            h2 = hsv2[0];
            s2 = hsv2[1];
            if(!checkBg(hsv2) && !checkGround(hsv2) && (h !=h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
            hsv2 = rgbConversion(this.image.getRGB(pixel[0]+1, pixel[1]+1));
            h2 = hsv2[0];
            s2 = hsv2[1];
            if(!checkBg(hsv2) && !checkGround(hsv2) && (h !=h2 || s < s2 - 0.01 || s > s2 + 0.01) && !contains2(retList, h2, s2)) {
            	float[] n = {h2, s2};
            	retList.add(n);
            }
    	}
 
    	return retList;
    }
    
	private float timePassedLastDesiredHeight = 0;
	private int timePassedIterationCount = 0;
	private ArrayList<Float> timePassedAverageList = new ArrayList<>();
	private float timePassedDesiredHeight;
	
	public float guess() {
		float guess = Float.NaN;

		ArrayList<Cube> list = null;
		timePassedIterationCount++;
		if (timePassedIterationCount % 2 == 0) {
			list = generateLocations();

			if (list != null && !list.isEmpty()) {
				timePassedAverageList.add(list.get(0).getLocation()[1]);

				if (timePassedIterationCount >= 14 && !timePassedAverageList.isEmpty()) {

					OptionalDouble t = timePassedAverageList.stream().mapToDouble(a -> a)
							.average();
					guess = (float) t.getAsDouble();

					timePassedAverageList.clear();

					timePassedIterationCount = 0;
				}
			}

		}

		if (!Float.isNaN(guess)) {
			timePassedDesiredHeight = list.get(0).getLocation()[1];
			timePassedLastDesiredHeight = timePassedDesiredHeight;

		} else {
			timePassedDesiredHeight = timePassedLastDesiredHeight;
		}
		
		return timePassedDesiredHeight;
	}

	//some getters
    
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