package entities.trail;

import entities.WorldObject;
import org.joml.Vector3f;
import utils.Cubes;
import utils.Utils;

import java.util.ArrayList;

public class Trail {

    private Vector3f last = new Vector3f(0,0,0);

    public void leaveTrail(Vector3f pos,ArrayList<WorldObject> pathObjects) {
        if(Utils.euclDistance(last, pos, 1)) {
            makeTrail(pos, pathObjects);
        }
    }

    private void makeTrail(Vector3f pos, ArrayList<WorldObject> pathObjects) {
        WorldObject cube = new WorldObject(Cubes.getYellowCube().getMesh());
        cube.setPosition(pos);
        cube.setScale(0.1f);
        cube.setRotation(45, 45, 0);
        pathObjects.add(cube);
        last = pos;
    }
}
