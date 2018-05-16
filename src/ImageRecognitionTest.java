import engine.Engine;
import engine.IWorldRules;
import utils.Constants;
import world.premade.ImgRecogWorld;

public class ImageRecognitionTest {
	public static void main(String[] args) {
        try {

        	float x,y,z,dx,dy,dz;
        	x = 3f;
        	y = 3f;
        	z = -3f;
        	dx = 0f;
        	dy = 0f;
        	dz = 0f;
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
