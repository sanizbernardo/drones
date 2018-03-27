package entities.trail;

import entities.WorldObject;
import meshes.cube.TrailCube;

import org.joml.Vector3f;

import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Trail {

    private Vector3f last = new Vector3f(0,0,0);
    private List<WorldObject> pathObjects;
    
    public Trail() {
    	 pathObjects = new ArrayList<>();
    }
    
    public void leaveTrail(Vector3f pos) {
        if(Utils.euclDistance(last, pos, 5)) {
            makeTrail(pos);
        }
    }

    private void makeTrail(Vector3f pos) {
        WorldObject cube = new WorldObject(new TrailCube().getMesh());
        cube.setPosition(pos);
        cube.setScale(0.8f);
        cube.setRotation(45, 45, 0);
        pathObjects.add(cube);
        last = pos;
    }
    
    public List<WorldObject> getPathObjects() {
    	return this.pathObjects;
    }
}
