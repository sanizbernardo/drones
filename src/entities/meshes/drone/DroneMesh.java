package entities.meshes.drone;

import entities.meshes.Mesh;
import physics.Drone;

public class DroneMesh {


    private LeftWing left;
    private RightWing right;
    private Body body;

    public DroneMesh(Drone drone){

        left = new LeftWing(drone);
        right = new RightWing(drone);
        body = new Body(drone);
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
