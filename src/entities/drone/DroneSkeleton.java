package entities.drone;

import meshes.Mesh;
import meshes.drone.Body;
import meshes.drone.LeftWing;
import meshes.drone.RightWing;
import meshes.drone.Wheel;
import interfaces.AutopilotConfig;
import utils.Constants;

public class DroneSkeleton {

    private LeftWing left;
    private RightWing right;
    private Body body;
    private Wheel wheel;

    public DroneSkeleton(AutopilotConfig config){
        left  = new LeftWing(config, Constants.DRONE_THICKNESS);
        right = new RightWing(config, Constants.DRONE_THICKNESS);
        body  = new Body(config, Constants.DRONE_THICKNESS);
        wheel = new Wheel(config, Constants.DRONE_WHEEL_THICKNESS);
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

    public Mesh getWheel() {
        return wheel.getMesh();
    }

}
