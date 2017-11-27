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
    private Vector3f oldPos;
    private Vector3f approxVel = new Vector3f(0,0,0);
    private AutopilotConfig config;
    private MiniPID pitchPID, thrustPID, inclPID, yawPID;
    private AutopilotGUI gui;


    public float getX() { return x; }

    public float getY() { return y; }

    private float getZ() { return z; }

    private float getNewThrust() { return newThrust; }

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

	// total mass of aircraft
    private float getMass() {
        return config.getEngineMass() + config.getTailMass() + 2*config.getWingMass();
    }


    private Vector3f horProjVel(AutopilotInputs inputs) {
        Vector3f relVelD = transMat(inputs).transform(approxVel, new Vector3f());
        return new Vector3f(0, relVelD.y, relVelD.z);
    }

    // AOA of right wing
    private float rightWingAOA(AutopilotInputs inputs) {
        Vector3f horProjVelD = horProjVel(inputs);
        Vector3f WingNormalVectorD = new Vector3f(0f, (float)Math.cos((double) rightWingInclination), (float)Math.sin((double) rightWingInclination));
        Vector3f WingAttackVectorD = new Vector3f(0f, (float)Math.sin((double) rightWingInclination), (float)-Math.cos((double) rightWingInclination));
        return (float) -Math.atan2(horProjVelD.dot(WingNormalVectorD), horProjVelD.dot(WingAttackVectorD));
    }

    // AOA of left wing
    private float leftWingAOA(AutopilotInputs inputs) {
        Vector3f horProjVelD = horProjVel(inputs);
        Vector3f WingNormalVectorD = new Vector3f(0f, (float)Math.cos((double) leftWingInclination), (float)Math.sin((double) leftWingInclination));
        Vector3f WingAttackVectorD = new Vector3f(0f, (float)Math.sin((double) leftWingInclination), (float)-Math.cos((double) leftWingInclination));
        return (float) -Math.atan2(horProjVelD.dot(WingNormalVectorD), horProjVelD.dot(WingAttackVectorD));
    }

    // AOA of horizontal stabiliser
    private float horStabAOA(AutopilotInputs inputs) {
        Vector3f horProjVelD = horProjVel(inputs);
        Vector3f WingNormalVectorD = new Vector3f(0f, (float)Math.cos((double) horStabInclination), (float)Math.sin((double) horStabInclination));
        Vector3f WingAttackVectorD = new Vector3f(0f, (float)Math.sin((double) horStabInclination), (float)-Math.cos((double) horStabInclination));
        return (float) -Math.atan2(horProjVelD.dot(WingNormalVectorD), horProjVelD.dot(WingAttackVectorD));
    }

    // AOA of vertical stabiliser
    private float verStabAOA(AutopilotInputs inputs) {
        Vector3f horProjVelD = horProjVel(inputs);
        Vector3f WingNormalVectorD = new Vector3f(0f, (float)Math.cos((double) verStabInclination), (float)Math.sin((double) verStabInclination));
        Vector3f WingAttackVectorD = new Vector3f(0f, (float)Math.sin((double) verStabInclination), (float)-Math.cos((double) verStabInclination));
        return (float) -Math.atan2(horProjVelD.dot(WingNormalVectorD), horProjVelD.dot(WingAttackVectorD));
    }

    // Calculate wing inclination such that lift cancels weight
    private float stableInclination(AutopilotInputs inputs) {
        float AOA = rightWingAOA(inputs);
        float L = 2*config.getWingLiftSlope()*AOA*horProjVel(inputs).dot(horProjVel(inputs));
        // incl might be incorrect...
        double incl = inputs.getPitch() - Math.asin(config.getGravity()*getMass()/L);
        return (float)incl;
    }


    // PID uses horizontal stabbiliser to adjust pitch.
    private void adjustPitch(AutopilotInputs input, float target) {
        pitchPID.setSetpoint(target);
        float actual = input.getPitch();
        float output = (float)pitchPID.getOutput(actual);
        float hAOA = horStabAOA(input);
        if (Math.abs(output) < config.getMaxAOA() - 0.02) {
            setHorStabInclination(-output);
        }
    }

    // not used currently
    private void adjustInclination(AutopilotInputs inputs, float target) {
        inclPID.setSetpoint(target);
        float actual = inputs.getY();
        float output = (float)inclPID.getOutput(actual);
        setLeftWingInclination(output*3);
        setRightWingInclination(output*3);
    }

    // PID sets thrust so that y component of velocity is equal to target.
    private void adjustThrust(AutopilotInputs inputs, float target) {
        thrustPID.setSetpoint(target);
        float actual = approxVel.y();
        float output = (float)thrustPID.getOutput(actual);
        float thrust;
        // This scaling is necessary for flying straight
        if (actual - target > 0) {
            thrust = 10*output;
        } else {
            thrust = 50*output;
        }
        // Check that received output is within bounds
        if (thrust > config.getMaxThrust()) {
            setNewThrust(config.getMaxThrust());
        } else if (thrust < 0f){
            setNewThrust(0);
        } else {
            setNewThrust(thrust);
        }
    }

    // Set wings to empirical values found by Flor. PIDs set pitch and thrust to fly straight.
    private void flyStraightPID(AutopilotInputs input, float height) {
        setLeftWingInclination(0.1721f);
        setRightWingInclination(0.1721f);
        adjustPitch(input, 0f);
        adjustThrust(input, 0f);
    }

    // causes drone to rise by increasing lift through higher speed. Not used currently.
    private void risePID(AutopilotInputs inputs, float target) {

        if (inputs.getY() < target - 2f) {
            adjustThrust(inputs, 3f);
            System.out.println("Rise");

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
        // Pitch of plane during climb
        float climbAngle = (float)Math.toRadians(25);

        if (inputs.getY() < target - 3f) {
            // aircraft is below target, must therefore pitch up and thrust

            System.out.println("Rise");
            adjustPitch(inputs, climbAngle);
            adjustThrust(inputs, 2f);

            // Stabilisers are set to cancel the weight of the aircraft. Vertical component of thrust causes drone to rise.
            float incl = stableInclination(inputs);
            float wAOA = leftWingAOA(inputs);
            // AOA often returns NaN -> error?
            if (!Float.isNaN(incl) && Math.abs(incl) < config.getMaxAOA() - 0.02){
                setRightWingInclination(incl);
                setLeftWingInclination(incl);
            }

        } else if (inputs.getY() > target + 3f) {
            // if aircraft overshoots target, it simply drops -> can probably be done better
            System.out.println("Fall");
            adjustPitch(inputs, 0f);
            adjustThrust(inputs, -2f);
        } else {
            // If within 3m from target, flies straight
            System.out.println("Fly straight");
            flyStraightPID(inputs, 0);
        }
    }

    // Uses PID controller to stabilise yaw
    private void stableYawPID(AutopilotInputs input) {
        yawPID.setSetpoint(0f);
        float actual = input.getHeading();
        float output = (float)yawPID.getOutput(actual);
        float vAOA = verStabAOA(input);
        if (Math.abs(output) < config.getMaxAOA() - 0.02) {
            setVerStabInclination(-output);
        }
    }
    

    // Initialises all the parameters for the autpilot
    @Override
    public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {

        pitchPID = new MiniPID(1.2, 0.15, 0.1);
        pitchPID.setOutputLimits(Math.toRadians(30));
        thrustPID = new MiniPID(0.6, 0.02, 1.2);
        thrustPID.setOutputLimits(config.getMaxThrust());
//        inclPID = new MiniPID(1.2, 0.15, 0.1);
//        inclPID.setOutputLimits(Math.toRadians(30));
        yawPID = new MiniPID(1.2, 0.15, 0.1);
        yawPID.setOutputLimits(Math.toRadians(30));

        setConfig(config);
        gui = new AutopilotGUI(config);
        gui.updateImage(inputs.getImage());
        gui.showGUI();

        // Some empirical values found by Flor, for flying straight
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

        //first approximates velocity; useful for AOA
        Vector3f newPos = new Vector3f(inputs.getX(), inputs.getY(), inputs.getZ());
    	if (oldPos != null)
    		approxVel = newPos.sub(oldPos, new Vector3f()).mul(1/inputs.getElapsedTime(), new Vector3f());
    	oldPos = new Vector3f(newPos);

    	//get the information from image recognition
    	ImageRecognition recog = new ImageRecognition(inputs.getImage(), config.getNbRows(), config.getNbColumns(), config.getHorizontalAngleOfView(), config.getVerticalAngleOfView());
        double[] center = recog.getCenter();
        
    	if (center != null) gui.updateImage(inputs.getImage(), (int)center[0], (int)center[1]);

    	// end simulation if target is reached
        if(recog.getDistApprox() < 4){
//        	System.exit(0);
        }

        // target of climb should be the z position of the cube
        climbPID(inputs,12f);
        stableYawPID(inputs);

        // prints useful variables
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