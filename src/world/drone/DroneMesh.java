package world.drone;

import engine.graph.Mesh;
import physics.Drone;

public class DroneMesh {

    Drone drone;

    private LeftWing left;
    private RightWing right;

    public DroneMesh(Drone drone){
        this.drone = drone;

        left = new LeftWing(drone);
        right = new RightWing(drone);
    }

    public Mesh getLeft() {
        return left.getMesh();
    }

    public Mesh getRight() {
        return right.getMesh();
    }
}
