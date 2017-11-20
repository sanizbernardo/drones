package physics;

import autopilot.Autopilot;
import com.stormbots.MiniPID;
import datatypes.AutopilotConfig;
import datatypes.AutopilotInputs;
import datatypes.AutopilotOutputs;
import gui.AutopilotGUI;

import org.joml.Matrix3f;
import org.joml.Vector3f;
import recognition.ImageRecognition;
import utils.Constants;

public class Motion implements Autopilot {

    public Motion() {
    }

    private float x = Float.NaN;
    private float y = Float.NaN;
    private float z = Float.NaN;
    private float leftWingInclination;
    private float rightWingInclination;
    private float horStabInclination;
    private float verStabInclination;
    private float newThrust;
    private Vector3f oldPos, newPos, approxVel = new Vector3f(0,0,0);
    private AutopilotConfig config;
    private MiniPID pitchPID, thrustPID, inclPID;
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
    
    
    public Matrix3f transMat(AutopilotInputs input) {
		return buildTransformMatrix(input.getPitch(), input.getHeading(), input.getRoll());
	}
    
    public Matrix3f transMatInv(AutopilotInputs input) {
		Matrix3f result = new Matrix3f();
		return transMat(input).invert(result);
	}
    
    public static Matrix3f buildTransformMatrix(float xAngle, float yAngle, float zAngle) {
		// column major -> transposed
		Matrix3f xRot = new Matrix3f(
				1f, 					  0f,					    0f,
				0f,  (float)Math.cos(xAngle),  (float)Math.sin(xAngle),
				0f, (float)-Math.sin(xAngle), (float)Math.cos(xAngle)),
				
			   yRot = new Matrix3f(
				(float)Math.cos(yAngle),  0f, (float)Math.sin(yAngle),
									 0f,  1f, 						0f,
				(float)-Math.sin(yAngle),  0f, (float)Math.cos(yAngle)),
			   
			   zRot = new Matrix3f(
				 (float)Math.cos(zAngle), (float)Math.sin(zAngle), 0f,
				(float)-Math.sin(zAngle), (float)Math.cos(zAngle), 0f,
									  0f, 					   0f, 1f);
		
		return yRot.mul(xRot).mul(zRot);
	}


    public void flyStablePID (AutopilotInputs input, float target) {
        pitchPID.setSetpoint(target);
        float actual = input.getPitch();
        float output = (float)pitchPID.getOutput(actual);
        setHorStabInclination(-output);
    }

    public void adjustInclination(AutopilotInputs inputs, float target) {
        inclPID.setSetpoint(target);
        float actual = approxVel.y();
        float output = (float)inclPID.getOutput(actual);
    }

    public void adjustThrust(AutopilotInputs inputs, float target) {
        thrustPID.setSetpoint(target);
        float actual = approxVel.y();
        float output = (float)thrustPID.getOutput(actual);
        float thrust;
        if (actual - target > 0) {
            thrust = 10*output;
        } else {
            thrust = 200*output;
        }
        if (thrust > config.getMaxThrust()) {
            setNewThrust(config.getMaxThrust());
        } else {
            setNewThrust(thrust);
        }
    }

    public void flyStraightPID(AutopilotInputs input, float target) {
        flyStablePID(input, target);
        adjustThrust(input, target);
    }

    public void climbPID(AutopilotInputs inputs, float target) {
        if (inputs.getY() < target - 2f) {
            adjustInclination(inputs, (float)Math.toRadians(15));
            adjustThrust(inputs, 1f);
        } else if (inputs.getY() > target + 2f) {
            setNewThrust(0f);
        } else {
            adjustInclination(inputs, (float)Math.toRadians(0));
            flyStraightPID(inputs, target);
        }
    }
    

    @Override
    public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
        pitchPID = new MiniPID(1.2, 0.15, 0.1);
        pitchPID.setOutputLimits(Math.toRadians(45));
        thrustPID = new MiniPID(1.2, 0.15, 0.1);
        thrustPID.setOutputLimits(config.getMaxThrust());
        inclPID = new MiniPID(1.2, 0.15, 0.1);
        inclPID.setOutputLimits(Math.toRadians(45));
        setConfig(config);

        if(!Constants.isMac) {
            gui = new AutopilotGUI(config.getNbColumns(), config.getNbRows(), (int)config.getMaxThrust());
            gui.updateImage(inputs.getImage());
            gui.showGUI();
        }

        
        setLeftWingInclination(0.1721f);
        setRightWingInclination(0.1721f);
        setNewThrust(25f);

        
        return new AutopilotOutputs() {

            @Override
            public float getThrust() {
            	return 0;
            }

            @Override
            public float getLeftWingInclination() { 
            	return 0;
            }

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
    	
    	newPos = new Vector3f(inputs.getX(),inputs.getY(),inputs.getZ());
    	if (oldPos != null)
    		approxVel = newPos.sub(oldPos, new Vector3f()).mul(1/inputs.getElapsedTime(), new Vector3f());
    	oldPos = new Vector3f(newPos);
    	
    	ImageRecognition recog = new ImageRecognition(inputs.getImage(), config.getNbRows(), config.getNbColumns(), config.getHorizontalAngleOfView(), config.getVerticalAngleOfView());
        double[] center = recog.getCenter();
        
        if(!Constants.isMac) {
        	if (center != null) gui.updateImage(inputs.getImage(), (int)center[0], (int)center[1]);
        }
        
        if(recog.getDistApprox() < 4){
//        	System.exit(0);
        }


//        flyStraightPID(inputs, 0f);
        climbPID(inputs,5f);

        
        System.out.printf("height = %s\t thrust = %s\t velocity = %s\t %s\t %s\t\n", inputs.getY(), newThrust, approxVel.x(), approxVel.y(), approxVel.z());

        
        return new AutopilotOutputs() {
            @Override
            public float getThrust() {
                return getNewThrust();
            }

            @Override
            public float getLeftWingInclination() { 
            	return leftWingInclination;
            }

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
	public void simulationEnded() {
	}
}
