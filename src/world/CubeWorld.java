package world;


import engine.IWorldRules;
import org.joml.Vector3f;
import entities.WorldObject;

import java.util.Random;

/**
 * Place where all the GameItem are to be placed in
 */
public class CubeWorld extends World implements IWorldRules {

    public CubeWorld() {
        super(3, true, false);
    }

    @Override
    public void setup() {
        Random rand = new Random();

        worldObjects = new WorldObject[7000];

        for(int i = 0; i < worldObjects.length; i++) {
            WorldObject cube = new WorldObject(getCubeMeshes()[rand.nextInt(getCubeMeshes().length)].getMesh());
            cube.setScale(0.5f);
            int x1 = rand.nextInt(100)-50,
            		y = rand.nextInt(100)-50,
            		z = rand.nextInt(100)-50;

            cube.setPosition(x1, y, z);
            worldObjects[i] = cube;
        }

        drone.setThrust(20f);
        drone.setVelocity(new Vector3f(0f, 0f, -10f));
        drone.setLeftWingInclination((float)Math.toRadians(90f));
        
    }

}
