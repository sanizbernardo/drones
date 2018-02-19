package entities.meshes.drone;

import entities.meshes.Mesh;
import interfaces.AutopilotConfig;
import utils.Constants;

public class DroneMesh {

    private LeftWing left;
    private RightWing right;
    private Body body;
    private Wheel wheel;

    public DroneMesh(AutopilotConfig config){
        left  = new LeftWing(config, Constants.DRONE_THICKNESS);
        right = new RightWing(config, Constants.DRONE_THICKNESS);
        body  = new Body(config, Constants.DRONE_THICKNESS);
        wheel = new Wheel(config, Constants.DRONE_WHEEL_THICKNESS, Constants.DRONE_TIRE_RADIUS);
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
