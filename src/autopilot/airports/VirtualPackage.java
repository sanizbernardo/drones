package autopilot.airports;

import org.joml.Vector3f;

public class VirtualPackage {

    public VirtualPackage(Vector3f position) {
        setPosition(position);
    }

    private Vector3f position;

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }
}
