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
import utils.FloatMath;

public class Motion implements Autopilot {

    public Motion() {
    }

    private float x = Float.NaN;
    private float y = Float.NaN;
    private float leftWingInclination;
    private float rightWingInclination;
    private float horStabInclination;
    private float verStabInclination;
    private float newThrust;
    private Vector3f oldPos;
    private Vector3f approxVel = new Vector3f(0f,0f,0f);
    private AutopilotConfig config;
    private MiniPID pitchPID, thrustPID, yawPID, descentPID, rollPID;
    private float climbAngle;
    private AutopilotGUI gui;


    public float getX() { return x; }

    public float getY() { return y; }

    private float getHorStabInclination() { return horStabInclination; }

    private float getNewThrust() { return newThrust; }

    // total mass of aircraft
    private float getMass() {
        return config.getEngineMass() + config.getTailMass() + 2*config.getWingMass();
    }

    private Vector3f getRelVel(AutopilotInputs input) {
        return getTransMat(input).transform(approxVel, new Vector3f());
    }

    public void setX(float x) { this.x = x; }

    public void setY(float y) { this.y = y; }
    
    private void setLeftWingInclination(float val) { this.leftWingInclination = val; }

    private void setRightWingInclination(float val) { this.rightWingInclination = val; }

    private void setHorStabInclination(float val) { this.horStabInclination = val; }

    private void setVerStabInclination(float val) { this.verStabInclination = val; }

    private void setNewThrust(float val) { this.newThrust = val; }
    
    private void setConfig(AutopilotConfig config){ this.config = config;}



	private Matrix3f getTransMat(AutopilotInputs inputs) {
    	float heading = inputs.getHeading();
    	float pitch = inputs.getPitch();
    	float roll = inputs.getRoll();
    	
    	Matrix3f transMat = new Matrix3f().identity();
    	
    	if (Math.abs(heading) > 1E-6)
			transMat.rotate(heading, new Vector3f(0, 1, 0));
		if (Math.abs(pitch) > 1E-6)
			transMat.rotate(pitch, new Vector3f(1, 0, 0));
		if (Math.abs(roll) > 1E-6)
			transMat.rotate(roll, new Vector3f(0, 0, 1));
		
		return transMat;
    }

    private Vector3f horProjVel(AutopilotInputs inputs) {
        Vector3f relVelD = getTransMat(inputs).transform(approxVel, new Vector3f());
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
        float rAOA = rightWingAOA(inputs);
        float lAOA = leftWingAOA(inputs);
        float L = config.getWingLiftSlope()*(rAOA + lAOA)*horProjVel(inputs).dot(horProjVel(inputs));
        // incl might be incorrect...
        double incl = inputs.getPitch() - Math.asin(config.getGravity()*getMass()/L);
        return (float)incl;
    }



    // PID uses horizontal stabiliser to adjust pitch.
    private void adjustPitch(AutopilotInputs input, float target) {
        pitchPID.setSetpoint(target);

        Vector3f rel = getRelVel(input);
        float climb = (float) Math.atan2(rel.y(), -rel.z());
        float min = climb - input.getPitch() + config.getMaxAOA();
        float max = climb - input.getPitch() - config.getMaxAOA();
        pitchPID.setOutputLimits(min, max);

        float actual = input.getPitch();
        float output = (float)pitchPID.getOutput(actual);

        setHorStabInclination(-output);
    }

    // PID sets thrust so that y component of velocity is equal to target.
    private void adjustThrust(AutopilotInputs inputs, float target) {
        thrustPID.setSetpoint(target);
        float actual = approxVel.y();
        float output = (float)thrustPID.getOutput(actual);

        // Check that received output is within bounds
        if (output > config.getMaxThrust()) {
            setNewThrust(config.getMaxThrust());
        } else if (output < 0f){
            setNewThrust(0);
        } else {
            setNewThrust(output);
        }
    }

    // Uses PID controller to stabilise yaw
    private void adjustYaw(AutopilotInputs input, float target) {
        yawPID.setSetpoint(target);

        Vector3f rel = getRelVel(input);
        float turn = (float) Math.atan2(rel.x(), -rel.z());
        float min = turn - input.getHeading() + config.getMaxAOA();
        float max = turn - input.getHeading() - config.getMaxAOA();
        pitchPID.setOutputLimits(min, max);

        float actual = input.getHeading();
        float output = (float)yawPID.getOutput(actual);

        setVerStabInclination(-output);
    }

    private void adjustRoll(AutopilotInputs inputs, float target) {
        rollPID.setSetpoint(target);
        float actual = inputs.getRoll();
        float output = (float)rollPID.getOutput(actual);
        setLeftWingInclination(leftWingInclination - output);
        setRightWingInclination(rightWingInclination + output);

        System.out.printf("rollOutput = %s\t", FloatMath.toDegrees(output));
    }



    // Set wings to empirical values found by Flor. PIDs set pitch and thrust to fly straight.
    private void flyStraightPID(AutopilotInputs input, float height) {
        setLeftWingInclination(FloatMath.toRadians(7));
        setRightWingInclination(FloatMath.toRadians(7));
        adjustPitch(input, 0f);
//        maintainPitch(input);
        adjustThrust(input, 0.1f);
    }

    // causes drone to climb by changing pitch and using thrust to increase vertical velocity
    private void climbPID(AutopilotInputs inputs, float target) {
        // aircraft is below target, must therefore pitch up and thrust
        System.out.println("Rise");
        adjustPitch(inputs, climbAngle);
        adjustThrust(inputs, 2f);
        setLeftWingInclination(FloatMath.toRadians(7));
        setRightWingInclination(FloatMath.toRadians(7));
    }

    private void dropPID(AutopilotInputs inputs, float target) {
        // if aircraft overshoots target, it simply drops
        System.out.println("Fall");
        adjustPitch(inputs, FloatMath.toRadians(0f));
        adjustThrust(inputs, -2f);
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
    
    private void adjustHeight(AutopilotInputs input, float height) {
        float actualHeight = input.getY();

        //sterk stijgen
        if (height - actualHeight > 1) {
        	climbPID(input, height);
        }
        //stijgen
        else if (height - actualHeight > 0.5) {
        	//pitch op 0
        	adjustPitch(input, 0);
        	//thrust bijgeven
        	adjustThrust(input, 1f);
        }
        //sterk dalen
        else if (height - actualHeight < -1) {
        	dropPID(input, height);
        }
        //dalen
        else if (height - actualHeight < -0.5) {
        	//pitch op 0
        	adjustPitch(input, 0);
        	//thrust afnemen
        	adjustThrust(input, -1f);
        }
        //horizontaal blijven
        else {
        	flyStraightPID(input, height);
        }

//        Vector3f rel = getRelVel(input);
//        float climb = (float) Math.atan2(rel.y(), -rel.z());
//        float incl = climb - input.getPitch() + FloatMath.toRadians(5);
//        setLeftWingInclination(incl);
//        setRightWingInclination(incl);
    }



    // Initialises all the parameters for the autpilot
    @Override
    public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {

        pitchPID = new MiniPID(0.5, 0.005, 0.005);
        pitchPID.setOutputLimits(Math.toRadians(30));
        thrustPID = new MiniPID(2, 0.01, 0.005);
//        thrustPID.setOutputLimits(0f, config.getMaxThrust());
        yawPID = new MiniPID(0.5, 0.005, 0.005);
        yawPID.setOutputLimits(Math.toRadians(30));
        climbAngle = FloatMath.toRadians(25);
        descentPID = new MiniPID(1.2, 0.15, 0.1);
//        descentPID.setOutputLimits(0f, config.getMaxAOA());
        rollPID = new MiniPID(0.0005, 0, 0);
        rollPID.setOutputLimits(Math.toRadians(30));

        setConfig(config);
        gui = new AutopilotGUI(config);
        gui.updateImage(inputs.getImage());
        gui.showGUI();

        
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
    public AutopilotOutputs timePassed(AutopilotInputs inputs) {

        //first approximates velocity; useful for AOA
        Vector3f newPos = new Vector3f(inputs.getX(), inputs.getY(), inputs.getZ());
    	if (oldPos != null)
    		approxVel = (newPos.sub(oldPos, new Vector3f())).mul(1/inputs.getElapsedTime(), new Vector3f());
    	oldPos = new Vector3f(newPos);

    	//get the information from image recognition
//    	ImageRecognition recog = new ImageRecognition(inputs.getImage(), config.getNbRows(), config.getNbColumns(), config.getHorizontalAngleOfView(), config.getVerticalAngleOfView());
//        double[] center = recog.getCenter();
        
//    	if (center != null) gui.updateImage(inputs.getImage(), (int)center[0], (int)center[1]);

    	// end simulation if target is reached
//        if(recog.getDistApprox() < 4){
//        	System.exit(0);
//        }

    	adjustHeight(inputs, 0f);
        adjustYaw(inputs, FloatMath.toRadians(30));
        adjustRoll(inputs, 0f);
    	if (inputs.getY() >= 10) {
    		//System.out.println("goal reached:" + inputs.getZ());
    	}

        // prints useful variables
        //System.out.printf("height = %s\t pitch = %s\t hStab = %s\t y-vel = %s\t thrust = %s\t \n", inputs.getY(), FloatMath.toDegrees(inputs.getPitch()), FloatMath.toDegrees(getHorStabInclination()), approxVel.y(), newThrust);
        System.out.printf("heading = %s\t vStab = %s\t roll = %s\t leftWing = %s\t rightWing = %s\t \n", FloatMath.toDegrees(inputs.getHeading()), FloatMath.toDegrees(verStabInclination), FloatMath.toDegrees(inputs.getRoll()), FloatMath.toDegrees(leftWingInclination), FloatMath.toDegrees(rightWingInclination));


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