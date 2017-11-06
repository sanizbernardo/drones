import engine.Engine;
import engine.IWorldRules;
import engine.Window;
import utils.Constants;
import world.CubeWorld;
import world.StopWorld;
import world.TestWorld;

public class Main {

    public static void main(String[] args) {
        try {
            //create a world, this will hold the game objects

            IWorldRules worldRules = new CubeWorld();

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