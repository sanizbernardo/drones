package image;

import javax.imageio.ImageIO;

import utils.Constants;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glReadPixels;

/**
 * Created by arno on 16/10/2017.
 */
public class ImageCreator {

    private int WIDTH = Constants.WIDTH, HEIGHT = Constants.HEIGHT;

    //=========================getScreenImage==================================//
    public void screenShotOld(){
        //Creating an rbg array of total pixels
        int[] pixels = new int[WIDTH * HEIGHT];
        int bindex;
        // allocate space for RBG pixels
        ByteBuffer fb = ByteBuffer.allocateDirect(WIDTH * HEIGHT * 3);
        
        // grab a copy of the current frame contents as RGB
        glReadPixels(0, 0, WIDTH, HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, fb);

        // convert RGB data in ByteBuffer to integer array
        for (int i=0; i < pixels.length; i++) {
            bindex = i * 3;
            pixels[i] =
                    ((fb.get(bindex) << 16))  +
                            ((fb.get(bindex+1) << 8))  +
                            ((fb.get(bindex+2) << 0));
        }
        //Allocate colored pixel to buffered Image
        BufferedImage imageIn = null;
        try{
            imageIn = new BufferedImage(WIDTH, HEIGHT,BufferedImage.TYPE_INT_RGB);
            imageIn.setRGB(0, 0, WIDTH, HEIGHT, pixels, 0 , WIDTH);
        } catch (Exception e) {
            e.printStackTrace();
        }


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
    }
    
    
    public byte[] screenShot(){
    	byte[] pixels = new byte[WIDTH * HEIGHT * 3];

        ByteBuffer fb = ByteBuffer.allocateDirect(WIDTH * HEIGHT * 3);
        
        glReadPixels(0, 0, WIDTH, HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, fb);

        for (int i=0; i < HEIGHT; i++) {
        	for (int j=0; j < WIDTH; j++) {
        		pixels[(i*WIDTH+j)*3] = fb.get(((HEIGHT-i-1)*WIDTH+j)*3);
                pixels[(i*WIDTH+j)*3+1] = fb.get(((HEIGHT-i-1)*WIDTH+j)*3+1);
                pixels[(i*WIDTH+j)*3+2] = fb.get(((HEIGHT-i-1)*WIDTH+j)*3+2);
        	}
        }
        
        return pixels;
        
//		for exporting image.
//        BufferedImage imageOut = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
//		imageOut.getRaster().setDataElements(0, 0, WIDTH, HEIGHT, pixels);
//        
//        try {
//            ImageIO.write(imageOut, "png" , new File("ss.png"));
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        	System.out.println("ScreenShot() exception: " +e);
//        }
    }
    
}
