import engine.Engine;
import engine.IWorldRules;
import gui.ConfigSetupGUI;
import utils.Constants;
import world.ImgRecogWorld;
import world.OrthoTestWorld;
import world.TestWorldFlyStraight;


public class Main {

    public static void main(String[] args) {
        try {
        	ConfigSetupGUI gui = new ConfigSetupGUI();
        	//IWorldRules worldRules = gui.showDialog();

        	//untoggle for imagerecog tests
//        	float x,y,z,dx,dy,dz;
//        	x = 0f;
//        	y = 0f;
//        	z = -1f;
//        	dx = 0f;
//        	dy = 0f;
//        	dz = -0.1f;
//            IWorldRules worldRules = new ImgRecogWorld(x, y, z, dx, dy, dz);

            IWorldRules worldRules = new OrthoTestWorld();
            
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