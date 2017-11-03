import engine.Engine;
import engine.IWorldRules;
import engine.Window;
import utils.Constants;
import world.StopWorld;

public class Main {

    public static void main(String[] args) {
        try {
            //create a world, this will hold the game objects
            IWorldRules worldRules = new StopWorld(new Window(Constants.TITLE, Constants.WIDTH, Constants.HEIGHT, Constants.VSYNC));
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