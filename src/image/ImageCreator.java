package image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glReadPixels;

public class ImageCreator {
	
	private final int width, height, multiplier;
	
	public ImageCreator(int width, int height) {
		this.width = width;
		this.height = height;
		
        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
            multiplier = 2;
        } else {
            multiplier = 1;
        }
	}
	
    public byte[] screenShot(){
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
        
        return pixels;
    }
    
    
    public void screenShotExport() {
    	byte[] pixels = screenShot();
        BufferedImage imageOut = new BufferedImage(width * multiplier, height * multiplier, BufferedImage.TYPE_3BYTE_BGR);
		imageOut.getRaster().setDataElements(0, 0, width * multiplier, height * multiplier, pixels);
        
        try {
            ImageIO.write(imageOut, "png" , new File("ss.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        	System.out.println("ScreenShot() exception: " +e);
        }
        
    }
    
}