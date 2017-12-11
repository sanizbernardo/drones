import engine.Engine;
import engine.IWorldRules;
import utils.Constants;
import world.ImgRecogWorld;

public class ImageRecognitionTest {
	public static void main(String[] args) {
        try {

        	float x,y,z,dx,dy,dz;
        	x = -1.5f;
        	y = -1.5f;
        	z = -8f;
        	dx = 0.005f;
        	dy = 0.005f;
        	dz = 0f;
            IWorldRules worldRules = new ImgRecogWorld(x, y, z, dx, dy, dz);
        	
            //IWorldRules worldRules = new TestWorld();
        	
            //create a game engine
            Engine gameEng = new Engine(Constants.TITLE, Constants.WIDTH, Constants.HEIGHT, Constants.VSYNC, worldRules);
            //start the game loop
            gameEng.start();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);		
        }
	}
}
