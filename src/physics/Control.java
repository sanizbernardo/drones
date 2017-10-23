package physics;

import datatypes.AutopilotConfig;
import datatypes.AutopilotInputs;
import datatypes.AutopilotOutputs;
import autopilot.Autopilot;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Control implements KeyListener, Autopilot {

    public Control(float thrust, Drone drone) {
        this.setDeltaThrust(thrust);
        this.setDrone(drone);
    }

    private final float angle = (float)(Math.PI/12);

    private float deltathrust;

    private Drone drone;

    private float getAngle() {
        return angle;
    }

    private float getDeltaThrust() { return deltathrust; }

    private void setDeltaThrust(float value) { this.deltathrust = value; }

    private Drone getDrone(){
        return this.drone;
    }

    private void setDrone(Drone drone) {
        this.drone = drone;
    }


    private boolean iPressed = false;

    private boolean jPressed = false;

    private boolean kPressed = false;

    private boolean lPressed = false;

    private boolean uPressed = false;

    private boolean oPressed = false;




    /**
     * Besturing van drone met toetsenbord
     *
     */

    private void pitchUpKey(){
        this.getDrone().setHorStabInclination(this.getDrone().getHorStabInclination() - getAngle());
    }

    private void pitchDownKey(){
        this.getDrone().setHorStabInclination(this.getDrone().getHorStabInclination() + getAngle());
    }

    private void rollLeftKey() {
        this.getDrone().setLeftWingInclination(this.getDrone().getLeftWingInclination()-getAngle());
        this.getDrone().setRightWingInclination(this.getDrone().getRightWingInclination()+getAngle());
    }

    private void rollRightKey() {
        this.getDrone().setLeftWingInclination(this.getDrone().getLeftWingInclination()+getAngle());
        this.getDrone().setRightWingInclination(this.getDrone().getRightWingInclination()-getAngle());
    }

    private void yawLeftKey() {
        this.getDrone().setVerStabInclination(this.getDrone().getVerStabInclination()+getAngle());
    }

    private void yawRightKey() {
        this.getDrone().setVerStabInclination(this.getDrone().getVerStabInclination()-getAngle());
    }

    private void thrustUpKey() { this.getDrone().setThrust(this.getDrone().getThrust() + getDeltaThrust());}

    private void thrustDownKey() {this.getDrone().setThrust(this.getDrone().getThrust() - getDeltaThrust());}



    /**
     * KeyListener controls
     *
     */
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_I) {
            if (!iPressed){
                pitchUpKey();
                iPressed = true;
                System.out.println("Pitching up");
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_J) {
            if (!jPressed){
                rollLeftKey();
                jPressed = true;
                System.out.println("Rolling left");
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_K) {
            if (!kPressed){
                pitchDownKey();
                kPressed = true;
                System.out.println("Pitching down");
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_L) {
            if (!lPressed){
                rollRightKey();
                lPressed = true;
                System.out.println("Rolling right");
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_U) {
            if (!uPressed){
                yawLeftKey();
                uPressed = true;
                System.out.println("Yawing left");
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_O) {
            if (!oPressed){
                yawRightKey();
                oPressed = true;
                System.out.println("Yawing right");
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_SHIFT) { thrustUpKey(); }

        if (e.getKeyCode() == KeyEvent.VK_CONTROL) { thrustDownKey(); }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_I) {
            pitchDownKey();
            iPressed = false;
            System.out.println("Stopping pitch");
        }

        if (e.getKeyCode() == KeyEvent.VK_J) {
            rollRightKey();
            jPressed = false;
            System.out.println("Stopping roll");
        }

        if (e.getKeyCode() == KeyEvent.VK_K) {
            pitchUpKey();
            kPressed = false;
            System.out.println("Stopping pitch");
        }

        if (e.getKeyCode() == KeyEvent.VK_L) {
            rollLeftKey();
            lPressed = false;
            System.out.println("Stopping roll");
        }

        if (e.getKeyCode() == KeyEvent.VK_U) {
            yawRightKey();
            uPressed = false;
            System.out.println("Stopping yaw");
        }

        if (e.getKeyCode() == KeyEvent.VK_O) {
            yawLeftKey();
            oPressed = false;
            System.out.println("Stopping yaw");
        }

        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            System.out.println("Thrust is now " + getDrone().getThrust());
        }

        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            System.out.println("Thrust is now " + getDrone().getThrust());
        }
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

