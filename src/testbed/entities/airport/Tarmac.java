package testbed.entities.airport;

import testbed.entities.WorldObject;
import testbed.graphics.meshes.airport.TarmacMesh;

import org.joml.Vector3f;

import utils.Utils;
import utils.graphics.RGBTuple;

public class Tarmac {

    private Vector3f position;
    private float width, length, rotation;
    private WorldObject object;
    private RGBTuple colorTarmac = new RGBTuple();
    private RGBTuple colorStripes = new RGBTuple();

    private float UP_DELTA = 0.05f;

    public Tarmac(Vector3f position, float width, float length, float rotation) {
        this.position = position;
        this.width = width;
        this.rotation = rotation;
        this.length = length;

        this.colorTarmac.set(Utils.toRGB(250, 0.1f, 0.7f));
        this.colorStripes.set(Utils.toRGB(0,0,1f));

        this.object = new WorldObject(new TarmacMesh(width, length, UP_DELTA, colorTarmac, colorStripes).getMesh());
        this.object.setPosition(position);
        this.object.setRotation(0, -rotation, 0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return length;
    }

    public float getRotation() {
        return rotation;
    }

    public WorldObject getObject() {
        return object;
    }
}
