import engine.Engine;
import engine.IWorldRules;
import engine.Window;
import utils.Constants;
import world.CubeWorld;
import world.ImgRecogWorld;
import world.StopWorld;
import world.TestWorld;
import world.TestWorldFlyStraight;

public class Main {

    public static void main(String[] args) {
        try {
            //create a world, this will hold the game objects

        	float x,y,z,dx,dy,dz;
        	x = 0;
        	y = 0;
        	z = -4;
        	dx = 0;
        	dy = 0;
        	dz = -0.1f;
            IWorldRules worldRules = new ImgRecogWorld(x, y, z, dx, dy, dz);

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