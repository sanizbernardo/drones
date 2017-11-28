import engine.Engine;
import engine.IWorldRules;
import gui.ConfigSetupGUI;
import utils.Constants;
import world.NewPhysicsWorld;

public class Main {

    public static void main(String[] args) {
        try {
        	ConfigSetupGUI gui = new ConfigSetupGUI();
        	IWorldRules worldRules = new NewPhysicsWorld(1, true);
        	
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