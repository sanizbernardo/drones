package entities.meshes.drone;

import entities.meshes.Mesh;
import interfaces.AutopilotConfig;

public class DroneMesh {


    private LeftWing left;
    private RightWing right;
    private Body body;

    public DroneMesh(AutopilotConfig config){
    	float width = 0.01f;
        left = new LeftWing(config, width);
        right = new RightWing(config, width);
        body = new Body(config, width);
    }

    public Mesh getLeft() {
        return left.getMesh();
    }

    public Mesh getRight() {
        return right.getMesh();
    }

    public Mesh getBody() {
        return body.getMesh();
    }
}
