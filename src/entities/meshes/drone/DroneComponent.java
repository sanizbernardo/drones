package entities.meshes.drone;

import entities.meshes.Mesh;
import physics.Drone;

public abstract class DroneComponent {

    protected Drone drone;
    protected float[] positions, colours;
    protected int[] indices;
    private Mesh mesh;

    public DroneComponent(Drone drone){
        this.drone = drone;
        setPositions();
        setColours();
        setIndices();

        this.mesh = new Mesh(this.getPositions(), this.getColours(), this.getIndices());
    }

    abstract protected void setPositions();
    abstract protected void setColours();
    abstract protected void setIndices();

    private float[] getPositions() {
        return positions;
    }

    private float[] getColours() {
        return colours;
    }

    public int[] getIndices() {
        return indices;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
