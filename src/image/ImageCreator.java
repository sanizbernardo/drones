package image;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glReadPixels;

public class ImageCreator {
	
	private final int width, height;
	
	public ImageCreator(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
    /**
     * Based on
     */
    public byte[] screenShotOld(){
        int multiplier;
        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
            multiplier = 2;
        } else {
            multiplier = 1;
        }

        //Creating an rbg array of total pixels
        int[] pixels = new int[200 * 200 * multiplier * multiplier];
        int bindex;
        // allocate space for RBG pixels
        ByteBuffer fb = ByteBuffer.allocateDirect(200 * 200 * 3 * multiplier * multiplier);

        // grab a copy of the current frame contents as RGB
        glReadPixels(0, 0, 200 * multiplier, 200 * multiplier, GL_RGB, GL_UNSIGNED_BYTE, fb);

        // convert RGB data in ByteBuffer to integer array
        for (int i=0; i < pixels.length; i++) {
            bindex = i * 3;
            pixels[i] =
                    ((fb.get(bindex)& 0xFF) << 16)  +
                            ((fb.get(bindex+1) & 0xFF) << 8) +
                            (fb.get(bindex+2)& 0xFF);
        }
        //Allocate colored pixel to buffered Image
        BufferedImage imageIn = new BufferedImage(200* multiplier, 200 * multiplier,BufferedImage.TYPE_INT_RGB);
        imageIn.setRGB(0, 0, 200 * multiplier, 200 * multiplier, pixels, 0 , 200 * multiplier);

        //Creating the transformation direction (horizontal)
        AffineTransform at =  AffineTransform.getScaleInstance(1, -1);
        at.translate(0, -imageIn.getHeight(null));

        //Applying transformation
        AffineTransformOp opRotated = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage imageOut = opRotated.filter(imageIn, null);
        
        try {//Try to create image, else show exception.
            ImageIO.write(imageOut, "png" , new File("ss.png"));
        }
        catch (Exception e) {
            System.out.println("ScreenShot() exception: " +e);
        }
        
        return null;
    }
    
    
    public byte[] screenShot(){
    	int multiplier;
        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
            multiplier = 2;
        } else {
            multiplier = 1;
        }
    	
    	byte[] pixels = new byte[width * multiplier * height * multiplier * 3];

        ByteBuffer fb = ByteBuffer.allocateDirect(width * multiplier * height  * multiplier * 3);
        
        glReadPixels(0, 0, width * multiplier, height * multiplier, GL_RGB, GL_UNSIGNED_BYTE, fb);

        for (int i=0; i < height * multiplier; i++) {
        	for (int j=0; j < width * multiplier; j++) {
        		pixels[(i*height * multiplier+j)*3] = fb.get(((height * multiplier - i - 1)* height * multiplier+j)*3);
                pixels[(i*height * multiplier+j)*3+1] = fb.get(((height * multiplier - i - 1)* height * multiplier+j)*3+1);
                pixels[(i*height * multiplier+j)*3+2] = fb.get(((height * multiplier - i - 1)* height * multiplier+j)*3+2);
        	}
        }
        
//		for exporting image.
        BufferedImage imageOut = new BufferedImage(width * multiplier, height * multiplier, BufferedImage.TYPE_3BYTE_BGR);
		imageOut.getRaster().setDataElements(0, 0, width * multiplier, height * multiplier, pixels);
        
        try {
            ImageIO.write(imageOut, "png" , new File("ss.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        	System.out.println("ScreenShot() exception: " +e);
        }
        
        return pixels;
    }
    
}