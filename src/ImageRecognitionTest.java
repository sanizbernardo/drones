import testbed.engine.Engine;
import testbed.engine.IWorldRules;
import testbed.world.premade.ImgRecogWorld;
import utils.Constants;

public class ImageRecognitionTest {
	public static void main(String[] args) {
        try {

        	float x,y,z,dx,dy,dz;
        	x = 3f;
        	y = 3f;
        	z = -3f;
        	dx = 0.03f;
        	dy = 0.03f;
        	dz = -0.03f;
            IWorldRules worldRules = new ImgRecogWorld(x, y, z, dx, dy, dz);
        	
            //IWorldRules worldRules = new TestWorld();
        	
            //create a game engine
            Engine gameEng = new Engine(Constants.TITLE, Constants.VSYNC, worldRules);
            //start the game loop
            gameEng.start();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);		
        }
	}
}
