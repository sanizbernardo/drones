import engine.Engine;
import engine.IWorldRules;
import utils.Constants;
import world.ImgRecogWorld;

public class ImageRecognitionTest {
	public static void main(String[] args) {
        try {

        	float x,y,z,dx,dy,dz;
        	x = 1f;
        	y = 1f;
        	z = -1f;
        	dx = 0.1f;
        	dy = 0.1f;
        	dz = -0.1f;
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
