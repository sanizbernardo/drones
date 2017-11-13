package recognition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Toon on 31/10/17
 */
public class ImageProcessingApp {

    public static void main(String[] Args) throws IOException{
        //ImageProcessing imageD = new ImageProcessing("C:\\Users\\Gebruiker\\Desktop\\TestImages\\higherres.png");
        //ImageProcessing image = new ImageProcessing("C:\\Users\\Gebruiker\\Documents\\Masterbranch\\ss.png");
    	
    	//Path path = Paths.get("C:\\Gebruikers\\Tomas\\Afbeeldingen\\ss2.png");
    	//byte[] data = Files.readAllBytes(path);
    	ImageProcessing image = new ImageProcessing("ss6.png");
    	
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        System.out.println("time elapsed: " + (end-start) + " milliseconds");

        //image.saveImage("processedImage");

        for (Cube cube : image.getObjects()){
            //System.out.println(Arrays.toString(cube.getCubeData()));
            image.approximateLocation(cube, null, null);
        }
    }
}
