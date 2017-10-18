package image;

import utils.Constants;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glReadPixels;

public class ImageCreator {

    /**
     * Based on
     */
    public byte[] screenShot(){
        int multiplier;
        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
            multiplier = 2;
        } else {
            multiplier = 1;
        }

        //Creating an rbg array of total pixels
        int[] pixels = new int[Constants.WIDTH * Constants.HEIGHT * multiplier * multiplier];
        int bindex;
        // allocate space for RBG pixels
        ByteBuffer fb = ByteBuffer.allocateDirect(Constants.WIDTH * Constants.HEIGHT * 3 * multiplier * multiplier);

        // grab a copy of the current frame contents as RGB
        glReadPixels(0, 0, Constants.WIDTH * multiplier, Constants.HEIGHT * multiplier, GL_RGB, GL_UNSIGNED_BYTE, fb);

        // convert RGB data in ByteBuffer to integer array
        for (int i=0; i < pixels.length; i++) {
            bindex = i * 3;
            pixels[i] =
                    ((fb.get(bindex)& 0xFF) << 16)  +
                            ((fb.get(bindex+1) & 0xFF) << 8) +
                            (fb.get(bindex+2)& 0xFF);
        }
        //Allocate colored pixel to buffered Image
        BufferedImage imageIn = new BufferedImage(Constants.WIDTH * multiplier, Constants.HEIGHT * multiplier,BufferedImage.TYPE_INT_RGB);
        imageIn.setRGB(0, 0, Constants.WIDTH * multiplier, Constants.HEIGHT * multiplier, pixels, 0 , Constants.WIDTH * multiplier);

        //Creating the transformation direction (horizontal)
        AffineTransform at =  AffineTransform.getScaleInstance(1, -1);
        at.translate(0, -imageIn.getHeight(null));

        //Applying transformation
        AffineTransformOp opRotated = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage imageOut = opRotated.filter(imageIn, null);
        
        try {//Try to screate image, else show exception.
            ImageIO.write(imageOut, "png" , new File("ss.png"));
        }
        catch (Exception e) {
            System.out.println("ScreenShot() exception: " +e);
        }
        
        return (byte[]) imageOut.getData().getDataElements(0, 0, Constants.WIDTH * multiplier, Constants.HEIGHT * multiplier, new byte[Constants.WIDTH * multiplier * Constants.HEIGHT * multiplier * 3]);
    }
    
    public byte[] screenShotAlt(){
    	byte[] pixels = new byte[Constants.WIDTH * Constants.HEIGHT * 3];

        ByteBuffer fb = ByteBuffer.allocateDirect(Constants.WIDTH * Constants.HEIGHT * 3);
        
        glReadPixels(0, 0, Constants.WIDTH, Constants.HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, fb);

        for (int i=0; i < Constants.HEIGHT; i++) {
        	for (int j=0; j < Constants.WIDTH; j++) {
        		pixels[(i*Constants.WIDTH+j)*3] = fb.get(((Constants.HEIGHT-i-1)*Constants.WIDTH+j)*3);
                pixels[(i*Constants.WIDTH+j)*3+1] = fb.get(((Constants.HEIGHT-i-1)*Constants.WIDTH+j)*3+1);
                pixels[(i*Constants.WIDTH+j)*3+2] = fb.get(((Constants.HEIGHT-i-1)*Constants.WIDTH+j)*3+2);
        	}
        }
        
//		for exporting image.
        BufferedImage imageOut = new BufferedImage(Constants.WIDTH, Constants.HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		imageOut.getRaster().setDataElements(0, 0, Constants.WIDTH, Constants.HEIGHT, pixels);
        
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