package world.drone;

import engine.graph.Mesh;
import physics.Drone;

public class LeftWing {
    Drone drone;


    private float[] positions, colours;

    private int[] indices;

    private Mesh mesh;

    public LeftWing(Drone drone){
        this.drone = drone;
        setPositions();
        setColours();
        setIndices();
        this.mesh = new Mesh(this.getPositions(), this.getColours(), this.getIndices());

    }


    private void setPositions() {
        this.positions = new float[]{
                //left wing
                0f                  , 0f, -0.5f, //#0
                drone.getWingX()*-2f, 0f, -0.5f, //#1
                drone.getWingX()*-2f, 0f, 0.5f, //#2
                0f                  , 0f, 0.5f, //#3
        };
    }

    private void setColours() {
        this.colours = new float[]{
                //Face 1 (front)
                1f, 0f, 0.0f,
                1f, 0.5f, 0.0f,
                1f, 0.5f, 0.0f,
                1f, 0f, 0.0f,
        };
    }

    private void setIndices(){
        this.indices = new int[]{
                // Front face
                1, 2, 0, 3, 2, 0,
        };
    }

    public float[] getPositions() {
        return positions;
    }

    public float[] getColours() {
        return colours;
    }

    public int[] getIndices() {
        return indices;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
