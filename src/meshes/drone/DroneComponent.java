package meshes.drone;

import meshes.Mesh;
import interfaces.AutopilotConfig;

public abstract class DroneComponent {

	protected AutopilotConfig config;

    float[] positions, colours;
    int[] indices;

    private Mesh mesh;

    public void finalize(AutopilotConfig config) {
        this.config = config;
        setPositions();
        setColours();
        setIndices();

        this.mesh = new Mesh(positions, colours, indices);
    }

    abstract protected void setPositions();
    abstract protected void setColours();
    abstract protected void setIndices();

    public Mesh getMesh() {
        return mesh;
    }
}
