package meshes.drone;

import meshes.AbstractMesh;
import meshes.Mesh;
import interfaces.AutopilotConfig;

public abstract class DroneComponent extends AbstractMesh {

	protected AutopilotConfig config;

    public void finalizer(AutopilotConfig config) {
    	this.config = config;
    	
        finalizer();
    }

}
