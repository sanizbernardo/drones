package recognition;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    //constructor in case of a byte[]
    public ImageProcessing(byte[] imageByte){
    	InputStream in = new ByteArrayInputStream(imageByte);
    	BufferedImage bImageFromConvert = null;
		try {
			bImageFromConvert = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	this.image = bImageFromConvert;
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
        System.out.println("horAngle : " + Math.toDegrees(estimateHorAngle));
        System.out.println("verAngle : " + Math.toDegrees(estimateVerAngle));
        System.out.println("totAngle : " + Math.toDegrees(totalAngle));
        System.out.println("distance : " + estimateDistance);
        System.out.println("cubeCoords : " + Arrays.toString(approx));
        return approx;
    }
    
    private double estimateAngle(double Xco){
    	double angle = Math.atan((Xco/100)*Math.tan(Math.PI/3));
    	return angle;
    }

    public double guessDistance(Cube cube){ //TODO check
        ArrayList<int[]> pixels = cube.getPixels();
        double currentMaxAngle = 0;
//        double largestDistance = -1;
//        int[] pixelOne = null;
//        int[] pixelTwo =  null;
//        for (int[] pixel1 : pixels){
//            for (int[] pixel2 : pixels){
//                double distance = Math.sqrt(Math.pow((pixel1[0]-pixel2[0]),2) + Math.pow((pixel1[1]-pixel2[1]),2));
//                if (distance > largestDistance){
//                    largestDistance = distance;
//                    pixelOne = pixel1;
//                    pixelTwo = pixel2;
//                }
//            }
//        }
        
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
        
        ArrayList<int[]> diffColors = new ArrayList<int[]>();
        ArrayList<Integer> diffColorsAmounts = new ArrayList<Integer>();
        for(int[] pixel : pixels){
        	if (!contains(pixel, diffColors)){
        		diffColors.add(pixel);
        		diffColorsAmounts.add(1);
        	}
        	else{
        		int index = indexOf(pixel,diffColors);
        		int temp = diffColorsAmounts.get(index);
        		temp +=1;
        		diffColorsAmounts.set(index, temp);
        	}
        }
        Collections.sort(diffColorsAmounts);
        Collections.reverse(diffColorsAmounts);
        double biggest = diffColorsAmounts.get(0);
        System.out.println(biggest);
        System.out.println("hier");
        System.out.println(cube.getNbPixels());
        double side = Math.sqrt(biggest);
        System.out.println("side" + side);
    	double x = cube.getAveragePixel()[0];
        System.out.println(Math.atan(((x - 99)/100)*Math.tan(Math.PI/3)));
        //side = side/Math.cos(Math.atan(((x - 99)/100)*Math.tan(Math.PI/3)));
        System.out.println("side" + side);
        double angle = calculateAngle(side, cube.getAveragePixel());
        //double angle = side*getAnglePerPixel();
        System.out.println(getAnglePerPixel());
        System.out.println(angle);
        double dist = 0.5/Math.tan(angle/2);
        if(diffColorsAmounts.size() > 1){
        	double second = diffColorsAmounts.get(1);
        	double secondRatio = second/(biggest+second);
        	double secondAngle = secondRatio*Math.PI/2;
        	System.out.println(secondAngle);
        	System.out.println(dist);
        	dist = dist/Math.cos(secondAngle);
        }
        if(diffColorsAmounts.size() > 1){
        	double third = diffColorsAmounts.get(2);
        	double thirdRatio = third/(biggest+third);
        	double thirdAngle = thirdRatio*Math.PI/2;
        	dist = dist/Math.cos(thirdAngle);
        }
        return dist;
        
    }
    
    private double calculateAngle(double side, int[] averagePixel) {
		//benadering TODO als er tijd over is.
    	double x = averagePixel[0];
    	x -= 99;
    	//double anglePerPixel = Math.atan(((x+1)/100)*Math.tan(Math.PI/3)) - Math.atan((x/100)*Math.tan(Math.PI/3));
    	double anglePerPixel = Math.atan(((0.0+1)/100)*Math.tan(Math.PI/3)) - Math.atan((0.0/100)*Math.tan(Math.PI/3));
    	System.out.println("x: " + x);
    	System.out.println(anglePerPixel);
		return side*anglePerPixel;
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
