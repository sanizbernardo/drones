package autopilot.airports;

import autopilot.pilots.FlyPilot;
import interfaces.AutopilotConfig;
import org.joml.Vector3f;

public class VirtualDrone {

    public VirtualDrone(Vector3f position, float heading, AutopilotConfig config) {
        this.position = position;
        this.heading = heading;
        this.config = config;
    }

    private Vector3f position;
    private float heading;
    private AutopilotConfig config;

    public Vector3f getPosition() {
        return this.position;
    }

    public float getHeading() {
        return  this.heading;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setHeading(float heading) {
        this.heading = heading;
    }

    public AutopilotConfig getConfig() {
        return config;
    }
}
