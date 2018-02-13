package world;


import engine.IWorldRules;
import entities.WorldObject;
import physics.Motion;
import utils.Cubes;
import utils.Utils;

import java.util.Random;

/**
 * Place where all the GameItem are to be placed in
 */
public class YZWorld extends World implements IWorldRules {

    public YZWorld() {
        super(1, true);
    }

    @Override
    public void setup() {
        config = Utils.createDefaultConfig();

        physics.init(config, 50);

        planner = new Motion();

        Random rand = new Random();

        //World specifics
        worldObjects = new WorldObject[5];

        for(int i = 1; i <= worldObjects.length; i++) {
            WorldObject cube = new WorldObject(Cubes.getCubes()[rand.nextInt(Cubes.getCubes().length)].getMesh());
            cube.setScale(0.5f);
            int y = rand.nextInt(10)-10;

            cube.setPosition(0, y, -i*40);
            worldObjects[i-1] = cube;
        }

        System.out.printf("World started with cubes on y: %s  %s  %s  %s  %s \n", worldObjects[0].getPosition().y,worldObjects[1].getPosition().y,worldObjects[2].getPosition().y,worldObjects[3].getPosition().y,worldObjects[4].getPosition().y);
    }

    @Override
    public String getDescription() {
        return "Same as the cylinder M2.3 assignment but only in the XY plane.";
    }

}
