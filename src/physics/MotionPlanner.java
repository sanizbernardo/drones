package physics;

import autopilot.Autopilot;
import datatypes.AutopilotConfig;
import datatypes.AutopilotInputs;
import datatypes.AutopilotOutputs;
import gui.AutopilotGUI;

import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;
import recognition.ImageRecognition;
import utils.Constants;

public class MotionPlanner implements Autopilot {

    public MotionPlanner() {
    }

    private float x = Float.NaN;
    private float y = Float.NaN;
    private float z = Float.NaN;
    private float leftWingInclination;
    private float rightWingInclination;
    private float horStabInclination;
    private float verStabInclination;
    private float newThrust;
    private AutopilotConfig config;
    
    private AutopilotGUI gui;

    private float getAngle() {
        return (float) (Math.PI / 12);
    }

    private float getDeltaThrust() {
        return 25f; }

    public float getX() { return x; }

    public float getY() { return y; }

    private float getZ() { return z; }

    private float getNewThrust() { return newThrust; }

    private float getMinSpeed() {
        return 500f; }

    private float getMaxSpeed() {
        return 2000f; }

    public void setX(float x) { this.x = x; }

    public void setY(float y) { this.y = y; }

    private void setZ(float z) { this.z = z; }

    private void setLeftWingInclination(float val) { this.leftWingInclination = val; }

    private void setRightWingInclination(float val) { this.rightWingInclination = val; }

    private void setHorStabInclination(float val) { this.horStabInclination = val; }

    private void setVerStabInclination(float val) { this.verStabInclination = val; }

    private void setNewThrust(float val) { this.newThrust = val; }

    private void setConfig(AutopilotConfig config){ this.config = config;}


    //Controls voor autopiloot

    private void pitchUp(){
        setHorStabInclination(-getAngle());
    }

    private void pitchUp(float angle){
        setHorStabInclination(-angle);
    }

    private void pitchDown(){
        setHorStabInclination(getAngle());
    }

    private void pitchDown(float angle){
        setHorStabInclination(angle);
    }

    public void rollLeft() {
        setLeftWingInclination(-getAngle());
        setRightWingInclination(getAngle());
    }

    private void rollLeft(float angle) {
        setLeftWingInclination(-angle);
        setRightWingInclination(angle);
    }

    public void rollRight() {
        setLeftWingInclination(getAngle());
        setRightWingInclination(-getAngle());
    }

    private void rollRight(float angle) {
        setLeftWingInclination(angle);
        setRightWingInclination(-angle);
    }

    private void yawLeft() {
        setVerStabInclination(getAngle());
    }

    public void yawLeft(float angle) {
        setVerStabInclination(angle);
    }

    private void yawRight() { setVerStabInclination(-getAngle()); }

    public void yawRight(float angle) { setVerStabInclination(-angle); }

    private void thrustUp() {
        float newval = getNewThrust() + getDeltaThrust();
        if (newval < config.getMaxThrust()){
            setNewThrust(newval);
        } else {
            setNewThrust(config.getMaxThrust());
        }
    }

    public void thrustUp(float thrust) {
        float newval = getNewThrust() + thrust;
        if (newval < config.getMaxThrust()){
            setNewThrust(newval);
        } else {
            setNewThrust(config.getMaxThrust());
        }
    }

    private void thrustDown() {setNewThrust(getNewThrust() - getDeltaThrust());}

    public void thrustDown(float thrust) {setNewThrust(getNewThrust() - thrust);}



    private double velocity(float x, float y, float z, float time) {
        if (Float.isNaN(getX())|| Float.isNaN(getY()) || Float.isNaN(getZ())){
            setX(x);
            setY(y);
            setZ(z);
            return 0;
        }
        Vector dpos = new BasicVector(new double[]{ x-getX(), y-getY(), z-getZ()});
        Vector vel = dpos.multiply(1/time);
        setX(x);
        setY(y);
        setZ(z);
        return vel.norm();
    }

    private void adjustThrust(AutopilotInputs input) {
        if (velocity(input.getX(), input.getY(), input.getZ(), input.getElapsedTime()) < getMinSpeed()){
            thrustUp();
        } else if (velocity(input.getX(), input.getY(), input.getZ(), input.getElapsedTime()) > getMaxSpeed()){
            thrustDown();
        }
    }

    private void stabiliseRoll(float deltaRoll) {
        if (deltaRoll < 0) {
            rollLeft(-deltaRoll);
        } else if (deltaRoll > 0){
            rollRight(deltaRoll);
        }
    }

    private void stabilisePitch(float deltaPitch) {
        if (deltaPitch < 0) {
            pitchUp(-deltaPitch);
        } else if (deltaPitch > 0){
            pitchDown(deltaPitch);
        }
    }

    private void centerTarget(double[] coordinates, AutopilotInputs input) {
        if (coordinates[0] > 0) {
            yawRight();
        } else if (coordinates[0] < 0) {
            yawLeft();
        } else {stabiliseRoll(input.getRoll());}

        if (coordinates[1] > 0) {
            pitchDown();
        } else if (coordinates[1] < 0) {
            pitchUp();
        } else {stabilisePitch(input.getPitch());}

        adjustThrust(input);
    }




    @Override
    public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
        setConfig(config);

        if(!Constants.isMac) {
            gui = new AutopilotGUI(config.getNbColumns(), config.getNbRows());
            gui.updateImage(inputs.getImage());
            gui.showGUI();
        }
        return new AutopilotOutputs() {

            @Override
            public float getThrust() { return 0; }

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
    	
    	if(!Constants.isMac) gui.updateImage(inputs.getImage());
    	
        ImageRecognition recog = new ImageRecognition(inputs.getImage(), config.getNbRows(), config.getNbColumns(), config.getHorizontalAngleOfView(), config.getVerticalAngleOfView());
        double[] center = recog.getCenter();
        
        if(recog.getDistApprox() < 4){
        	System.exit(0);
        }

        if(center != null) { centerTarget(center, inputs); }

        return new AutopilotOutputs() {
            @Override
            public float getThrust() {
                return getNewThrust();
            }

            @Override
            public float getLeftWingInclination() { return leftWingInclination; }

            @Override
            public float getRightWingInclination() {
                return rightWingInclination;
            }

            @Override
            public float getHorStabInclination() {
                return horStabInclination;
            }

            @Override
            public float getVerStabInclination() {
                return verStabInclination;
            }
        };
    }

    @Override
    public void simulationEnded() { }
}