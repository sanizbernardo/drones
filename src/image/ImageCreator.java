package image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glReadPixels;

public class ImageCreator {
	
	private final int width, height;
	
	public ImageCreator(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
    public byte[] screenShot(){
    	byte[] pixels = new byte[width * height * 3];

        ByteBuffer fb = ByteBuffer.allocateDirect(width * height  * 3);
        
        glReadPixels(0, 0, width, height, GL_RGB, GL_UNSIGNED_BYTE, fb);

        for (int i=0; i < height; i++) {
        	for (int j=0; j < width; j++) {
        		pixels[(i*height+j)*3] = fb.get(((height - i - 1)* height +j)*3);
                pixels[(i*height+j)*3+1] = fb.get(((height - i - 1)* height +j)*3+1);
                pixels[(i*height+j)*3+2] = fb.get(((height - i - 1)* height +j)*3+2);
        	}
        }

        return pixels;
    }
    
    
    public void screenShotExport() {
    	byte[] pixels = screenShot();
        BufferedImage imageOut = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		imageOut.getRaster().setDataElements(0, 0, width, height, pixels);
        
        try {
            ImageIO.write(imageOut, "png" , new File("ss.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        	System.out.println("ScreenShot() exception: " +e);
        }
        
    }
    
}