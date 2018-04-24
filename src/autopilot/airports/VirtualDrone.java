package autopilot.airports;

import autopilot.Pilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import org.joml.Vector3f;

public class VirtualDrone {

    public VirtualDrone(Vector3f position, float heading, AutopilotConfig config) {
        this.position = position;
        this.heading = heading;
        this.config = config;
    }

    private Vector3f position;
    private float heading;
    private Pilot pilot;
    private AutopilotConfig config;
    private AutopilotInputs currentinputs;
    private AutopilotOutputs currentoutputs;
    private boolean active;

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

    public void setPilot(int[] tasks) {
        this.pilot = new Pilot(tasks);
    }

    public AutopilotConfig getConfig() {
        return config;
    }

    public AutopilotInputs getInputs() {
        return currentinputs;
    }

    public void setInputs(AutopilotInputs inputs) {
        this.currentinputs = inputs;
    }

    public AutopilotOutputs getOutputs() {
        return currentoutputs;
    }

    public void setOutputs(AutopilotOutputs outputs){
        this.currentoutputs = outputs;
    }

    public void calcOutputs() {
        setOutputs(pilot.timePassed(getInputs()));
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean x) {
        this.active = x;
    }

    public void endSimulation() {
        pilot.simulationEnded();
    }
}
