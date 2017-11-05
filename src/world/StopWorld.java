package world;

import engine.IWorldRules;
import entities.WorldObject;


/**
 * Place where all the GameItem are to be placed in
 */
public class StopWorld extends World implements IWorldRules {

    public StopWorld() {
        //Geef de vertraging van je wereld mee
        super(20, true, true);
    }

    /**
     * Is called in the abstract class
     */
    @Override
    public void setup() {
        /* Init the objects and set them as you like */
        int AMOUNT_OF_CUBES = 1;
        worldObjects = new WorldObject[AMOUNT_OF_CUBES];

        /* Do something with your new batch of objects */
        worldObjects[0] = new WorldObject(getCubeMeshes()[0].getMesh());
        worldObjects[0].setPosition(0f,0f,-10f);

        /* Give your drone some values */
        drone.setThrust(30);
    }

}
