package physics;

import autopilot.Autopilot;
import datatypes.AutopilotConfig;
import datatypes.AutopilotInputs;
import datatypes.AutopilotOutputs;

public class MotionPlanner implements Autopilot {

    public MotionPlanner(float thrust, Drone drone) {
        this.setDeltaThrust(thrust);
        this.setDrone(drone);
    }

    private float angle = (float)(Math.PI/12);

    private float deltathrust;

    private Drone drone;

    public float getAngle() {
        return angle;
    }

    public void setAngle(float value) {
        this.angle = value;
    }

    public float getDeltaThrust() { return deltathrust; }

    public void setDeltaThrust(float value) { this.deltathrust = value; }

    public Drone getDrone(){
        return this.drone;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }



    //Controls voor autopiloot

    public void pitchUp(){
        this.getDrone().setHorStabInclination(-getAngle());
    }

    public void pitchUp(float angle){
        this.getDrone().setHorStabInclination(-angle);
    }

    public void pitchDown(){
        this.getDrone().setHorStabInclination(getAngle());
    }

    public void pitchDown(float angle){
        this.getDrone().setHorStabInclination(angle);
    }

    public void rollLeft() {
        this.getDrone().setLeftWingInclination(-getAngle());
        this.getDrone().setRightWingInclination(getAngle());
    }

    public void rollLeft(float angle) {
        this.getDrone().setLeftWingInclination(-angle);
        this.getDrone().setRightWingInclination(angle);
    }

    public void rollRight() {
        this.getDrone().setLeftWingInclination(getAngle());
        this.getDrone().setRightWingInclination(-getAngle());
    }

    public void rollRight(float angle) {
        this.getDrone().setLeftWingInclination(angle);
        this.getDrone().setRightWingInclination(-angle);
    }

    public void yawLeft() {
        this.getDrone().setVerStabInclination(getAngle());
    }

    public void yawLeft(float angle) {
        this.getDrone().setVerStabInclination(angle);
    }

    public void yawRight() { this.getDrone().setVerStabInclination(-getAngle()); }

    public void yawRight(float angle) { this.getDrone().setVerStabInclination(-angle); }

    public void thrustUp() { this.getDrone().setThrust(this.getDrone().getThrust() + getDeltaThrust());}

    public void thrustUp(float thrust) { this.getDrone().setThrust(this.getDrone().getThrust() + thrust);}

    public void thrustDown() {this.getDrone().setThrust(this.getDrone().getThrust() - getDeltaThrust());}

    public void thrustDown(float thrust) {this.getDrone().setThrust(this.getDrone().getThrust() - thrust);}



    public void stabiliseRoll() {
        float deltaRoll = getDrone().getRoll();
        if (deltaRoll < 0) {
            rollLeft(-deltaRoll);
        } else if (deltaRoll > 0){
            rollRight(deltaRoll);
        }
    }

    public void stabilisePitch() {
        float deltaPitch = getDrone().getPitch();
        if (deltaPitch < 0) {
            pitchUp(-deltaPitch);
        } else if (deltaPitch > 0){
            pitchDown(deltaPitch);
        }
    }

    public void centerTarget(double[] coordinates) {
        if (coordinates[0] > 0) {
            yawRight();
        } else if (coordinates[0] < 0) {
            yawLeft();
        } else {stabiliseRoll();}

        if (coordinates[1] > 0) {
            pitchDown();
        } else if (coordinates[1] < 0) {
            pitchUp();
        } else {stabilisePitch();}
    }




    /**
     * Unsure how to connect to autopilot
     */
    @Override
    public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
        return new AutopilotOutputs() {
            @Override
            public float getThrust() { return 500f; }

            @Override
            public float getLeftWingInclination() { return 0; }

            @Override
            public float getRightWingInclination() {
                return 0;
            }

            @Override
            public float getHorStabInclination() {
                return 0;
            }

            @Override
            public float getVerStabInclination() {
                return 0;
            }
        };
    }

    @Override
    public AutopilotOutputs timePassed(AutopilotInputs inputs) {


        return new AutopilotOutputs() {
            @Override
            public float getThrust() {
                return getDrone().getThrust();
            }

            @Override
            public float getLeftWingInclination() { return getDrone().getLeftWingInclination(); }

            @Override
            public float getRightWingInclination() {
                return getDrone().getRightWingInclination();
            }

            @Override
            public float getHorStabInclination() {
                return getDrone().getHorStabInclination();
            }

            @Override
            public float getVerStabInclination() {
                return getDrone().getVerStabInclination();
            }
        };
    }

    @Override
    public void simulationEnded() {
        System.out.println("Simulation ended");
    }
}
