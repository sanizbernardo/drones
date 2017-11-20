package world;

import engine.IWorldRules;
import org.joml.Vector3f;
import entities.WorldObject;

/**
 * Place where all the GameItem are to be placed in
 */
public class TestWorld extends World implements IWorldRules {

    public TestWorld() {
        super(1, true, true);
    }

    @Override
    public void setup() {

        int AMOUNT_OF_CUBES = 1;
        worldObjects = new WorldObject[AMOUNT_OF_CUBES];

        worldObjects[0] = new WorldObject(getCubeMeshes()[0].getMesh());
        worldObjects[0].setPosition(0f, 10f, -30f);

        drone.setThrust(20f);
        drone.setVelocity(new Vector3f(0f, 0f, -4f));

    }
}