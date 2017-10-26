package utils;

import java.awt.Color;
import java.io.InputStream;
import java.util.Scanner;

public class Utils {

    /**
     * Read a file given by filename. It will start searching in the folder marked a resource location!
     * @param fileName
     *        The name of the file (with path of necessary)
     * @return
     *        The content of the file as a String
     * @throws Exception
     *        If something goes wrong
     */
    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Utils.class.getClass().getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, "UTF-8")) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    /**
     * Converts a hsv color to rgb
     * @param h
     * 		 hue value, range: 0-360
     * @param s
     * 		 saturation value, range: 0-1
     * @param v
     * 		 value value ???, range: 0-1
     * @return {r, g, b}
     * 		 range 0-1 		  
     */
    public static float[] toRGB(int h, float s, float v) {
    	Color rgb = new Color(Color.HSBtoRGB(h/360f, s, v));
    	return new float[]{rgb.getRed()/255f, rgb.getGreen()/255f, rgb.getBlue()/255f};
    }
    
    public static void main(String[] args) {
    	
	}
    
}
