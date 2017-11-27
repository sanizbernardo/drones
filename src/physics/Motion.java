package physics;


import com.stormbots.MiniPID;
import gui.AutopilotGUI;
import interfaces.Autopilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

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
    private MiniPID pitchPID, thrustPID, inclPID, yawPID;
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
    
    
    private Matrix3f transMat(AutopilotInputs input) {
		return buildTransformMatrix(input.getPitch(), input.getHeading(), input.getRoll());
	}
    
    public Matrix3f transMatInv(AutopilotInputs input) {
		Matrix3f result = new Matrix3f();
		return transMat(input).invert(result);
	}
    
    private static Matrix3f buildTransformMatrix(float xAngle, float yAngle, float zAngle) {
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

    public float getMass() {
        return config.getEngineMass() + config.getTailMass() + 2*config.getWingMass();
    }

    private Vector3f horProjVel(AutopilotInputs inputs) {
        Vector3f relVelD = transMat(inputs).transform(approxVel, new Vector3f());
        return new Vector3f(0, relVelD.y, relVelD.z);
    }

    private float rightWingAOA(AutopilotInputs inputs) {
        Vector3f horProjVelD = horProjVel(inputs);
        Vector3f WingNormalVectorD = new Vector3f(0f, (float)Math.cos((double) rightWingInclination), (float)Math.sin((double) rightWingInclination));
        Vector3f WingAttackVectorD = new Vector3f(0f, (float)Math.sin((double) rightWingInclination), (float)-Math.cos((double) rightWingInclination));
        return (float) -Math.atan2(horProjVelD.dot(WingNormalVectorD), horProjVelD.dot(WingAttackVectorD));
    }

    private float leftWingAOA(AutopilotInputs inputs) {
        Vector3f horProjVelD = horProjVel(inputs);
        Vector3f WingNormalVectorD = new Vector3f(0f, (float)Math.cos((double) leftWingInclination), (float)Math.sin((double) leftWingInclination));
        Vector3f WingAttackVectorD = new Vector3f(0f, (float)Math.sin((double) leftWingInclination), (float)-Math.cos((double) leftWingInclination));
        return (float) -Math.atan2(horProjVelD.dot(WingNormalVectorD), horProjVelD.dot(WingAttackVectorD));
    }

    public float stabAOA(AutopilotInputs inputs) {
        Vector3f horProjVelD = horProjVel(inputs);
        Vector3f WingNormalVectorD = new Vector3f(0f, (float)Math.cos((double) horStabInclination), (float)Math.sin((double) horStabInclination));
        Vector3f WingAttackVectorD = new Vector3f(0f, (float)Math.sin((double) horStabInclination), (float)-Math.cos((double) horStabInclination));
        return (float) -Math.atan2(horProjVelD.dot(WingNormalVectorD), horProjVelD.dot(WingAttackVectorD));
    }

    // fout -> Flor?
    private float stableInclination(AutopilotInputs inputs) {
        float AOA = rightWingAOA(inputs);
        float L = 2*config.getWingLiftSlope()*AOA*horProjVel(inputs).dot(horProjVel(inputs));
//        double incl = inputs.getPitch() + Math.toRadians(90) - Math.asin(-newThrust*Math.cos(inputs.getPitch())/L);
        double incl = inputs.getPitch() - Math.asin(config.getGravity()*getMass()/L);
        return (float)incl;
    }


    private void adjustPitch(AutopilotInputs input, float target) {
        pitchPID.setSetpoint(target);
        float actual = input.getPitch();
        float output = (float)pitchPID.getOutput(actual);
        if (Math.abs(output) < config.getMaxAOA()) {
            setHorStabInclination(-output);
        }
    }


    private void adjustInclination(AutopilotInputs inputs, float target) {
        inclPID.setSetpoint(target);
        float actual = inputs.getY();
        float output = (float)inclPID.getOutput(actual);
        setLeftWingInclination(output*3);
        setRightWingInclination(output*3);
    }


    private void adjustThrust(AutopilotInputs inputs, float target) {
        thrustPID.setSetpoint(target);
        float actual = approxVel.y();
        float output = (float)thrustPID.getOutput(actual);
        float thrust;
        if (actual - target > 0) {
            thrust = 10*output;
        } else {
            thrust = 100*output;
        }
        if (thrust > config.getMaxThrust()) {
            setNewThrust(config.getMaxThrust());
        } else if (thrust < 0f){
            setNewThrust(0);
        } else {
            setNewThrust(thrust);
        }
    }

    private void flyStraightPID(AutopilotInputs input, float height) {
//        adjustInclination(input, height);
        adjustPitch(input, 0f);
        adjustThrust(input, 0f);
    }

    // causes dront to rise by increasing lift through higher speed.
    private void risePID(AutopilotInputs inputs, float target) {

        if (inputs.getY() < target - 2f) {
            adjustThrust(inputs, 3f);
            System.out.println("Rise");

//            float incl = stableInclination(inputs);
//            if (!Float.isNaN(incl)){
//                setRightWingInclination(incl);
//                setLeftWingInclination(incl);
//            }

        } else if (inputs.getY() > target + 2f) {
            setNewThrust(0f);
            System.out.println("Fall");
        } else {
            flyStraightPID(inputs, 0);
            System.out.println("Fly straight");
        }
    }

    // causes drone to climb by changing pitch and using thrust to increase vertical velocity
    private void climbPID(AutopilotInputs inputs, float target) {
        float climbAngle = (float)Math.toRadians(10);

        if (inputs.getY() < target - 1f) {

            System.out.println("Rise");
            adjustPitch(inputs, climbAngle);
            adjustThrust(inputs, 1.5f);

            float incl = stableInclination(inputs);
            if (!Float.isNaN(incl) && Math.abs(incl) < config.getMaxAOA()){
                setRightWingInclination(incl);
                setLeftWingInclination(incl);
            }

//            if (inputs.getPitch() < climbAngle + 0.05 && inputs.getPitch() > climbAngle - 0.05) {adjustThrust(inputs, 1f);}

        } else if (inputs.getY() > target + 1f) {
            System.out.println("Fall");
            setNewThrust(0f);
        } else {
            System.out.println("Fly straight");
            flyStraightPID(inputs, 0);
        }
    }

    private void stableYawPID(AutopilotInputs input) {
        yawPID.setSetpoint(0f);
        float actual = input.getHeading();
        float output = (float)yawPID.getOutput(actual);
        if (Math.abs(output) < config.getMaxAOA()) {
            setVerStabInclination(-output);
        }
    }
    

    @Override
    public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
        pitchPID = new MiniPID(1.2, 0.15, 0.1);
        pitchPID.setOutputLimits(Math.toRadians(30));
        thrustPID = new MiniPID(1, 0.02, 1);
        thrustPID.setOutputLimits(config.getMaxThrust());
        inclPID = new MiniPID(1.2, 0.15, 0.1);
        inclPID.setOutputLimits(Math.toRadians(30));
        yawPID = new MiniPID(1.2, 0.15, 0.1);
        yawPID.setOutputLimits(Math.toRadians(30));
        setConfig(config);


        gui = new AutopilotGUI(config);
        gui.updateImage(inputs.getImage());
        gui.showGUI();


        
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
        
    	if (center != null) gui.updateImage(inputs.getImage(), (int)center[0], (int)center[1]);
        
        if(recog.getDistApprox() < 4){
//        	System.exit(0);
        }

        climbPID(inputs,20f);
//        risePID(inputs, 20f);
        stableYawPID(inputs);

        
        System.out.printf("height = %s\t pitch = %s\t thrust = %s\t y-velocity = %s\t wings = %s\t \n", inputs.getY(), inputs.getPitch(), newThrust, approxVel.y(), leftWingInclination);

        
        AutopilotOutputs output = new AutopilotOutputs() {
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
        gui.updateOutputs(output);
        return output;
    }

	@Override
	public void simulationEnded() {
		gui.dispose();
	}
}