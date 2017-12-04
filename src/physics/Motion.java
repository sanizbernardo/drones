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
    private float z = Float.NaN;
    private float leftWingInclination;
    private float rightWingInclination;
    private float horStabInclination;
    private float verStabInclination;
    private float newThrust;
    private Vector3f oldPos;
    private Vector3f approxVel = new Vector3f(0f,0f,0f);
    private AutopilotConfig config;
    private MiniPID pitchPID, thrustPID, inclPID, yawPID;
    private float climbAngle;
    private AutopilotGUI gui;


    public float getX() { return x; }

    public float getY() { return y; }

    private float getHorStabInclination() { return horStabInclination; }

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
    
    
//    private Matrix3f transMat(AutopilotInputs input) {
//		return buildTransformMatrix(input.getPitch(), input.getHeading(), input.getRoll());
//	}
//    
//    private static Matrix3f buildTransformMatrix(float xAngle, float yAngle, float zAngle) {
//		Matrix3f xRot = new Matrix3f(
//				1f, 					  0f,					    0f,
//				0f,  (float)Math.cos(xAngle),  (float)Math.sin(xAngle),
//				0f, (float)-Math.sin(xAngle), (float)Math.cos(xAngle)),
//				
//			   yRot = new Matrix3f(
//				(float)Math.cos(yAngle),  0f, (float)Math.sin(yAngle),
//									 0f,  1f, 						0f,
//				(float)-Math.sin(yAngle),  0f, (float)Math.cos(yAngle)),
//			   
//			   zRot = new Matrix3f(
//				 (float)Math.cos(zAngle), (float)Math.sin(zAngle), 0f,
//				(float)-Math.sin(zAngle), (float)Math.cos(zAngle), 0f,
//									  0f, 					   0f, 1f);
//		
//		return yRot.mul(xRot).mul(zRot);
//	}

	// total mass of aircraft
    private float getMass() {
        return config.getEngineMass() + config.getTailMass() + 2*config.getWingMass();
    }

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

    public Vector3f getRelVel(AutopilotInputs input) {
        return getTransMat(input).transform(approxVel, new Vector3f());
    }

    // function for maintaining constant pitch
    public void maintainPitch(AutopilotInputs input) {
//    	to fly straight AOA has to be 0;
//    	AOA is -atan2(y,x) and -atan2(y,x) == 0 if y=0
//    	y = horProjVelD * horStabNormalVectorD
//    	==> y-vel * cos(inclination) + z-vel * sin(inclination) == 0
        Vector3f relVel = getRelVel(input);
        float yVel = (float) relVel.y;
        float zVel = (float) relVel.z;
        float incl = 0;
        if (yVel == 0) {
            setHorStabInclination(0);
        }else {
            float n = 0f;
            incl = (float) (2*(Math.PI*n + Math.atan((zVel-Math.sqrt(Math.pow(yVel,2)+Math.pow(zVel, 2))/yVel))));
            if (incl > Math.PI/2) {
                n = -1/2f;
                incl = (float) (2*(Math.PI*n + Math.atan((zVel-Math.sqrt(Math.pow(yVel,2)+Math.pow(zVel, 2))/yVel))));
            }
            else if (incl < -Math.PI/2) {
                n = 1/2f;
                incl = (float) (2*(Math.PI*n + Math.atan((zVel-Math.sqrt(Math.pow(yVel,2)+Math.pow(zVel, 2))/yVel))));
            }
            if (Math.abs(incl) < Math.PI/4) {
                setHorStabInclination(incl);
            }
        }
    }


    // PID uses horizontal stabiliser to adjust pitch.
    private void adjustPitch(AutopilotInputs input, float target) {
        pitchPID.setSetpoint(target);

        try {
            Vector3f rel = getRelVel(input);
            float climbAngle = (float) Math.atan2(rel.y(), -rel.z());
            float min = climbAngle - input.getPitch() + config.getMaxAOA();
            float max = climbAngle - input.getPitch() - config.getMaxAOA();
            pitchPID.setOutputLimits(min, max);
        } catch (Exception e) {

        }

        float actual = input.getPitch();
        float output = (float)pitchPID.getOutput(actual);

        setHorStabInclination((float) (-output));

//        if (output > max) {
//            setHorStabInclination(-max);
//        } else if (output < min) {
//            setHorStabInclination(-min);
//        } else {
//            setHorStabInclination(-output);
//        }

//        System.out.printf("%s\t %s\t \n",output, horStabInclination);
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

        thrust = (float) (output);
        
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
        setLeftWingInclination(FloatMath.toRadians(7));
        setRightWingInclination(FloatMath.toRadians(7));
        adjustPitch(input, 0f);
//        maintainPitch(input);
        adjustThrust(input, 0.1f);
    }
    
    private void adjustheightPID(AutopilotInputs input, float height) {
        float actualHeight = input.getY();
        Vector3f rel = getRelVel(input);
        float climbAngle = (float) Math.atan2(rel.y(), -rel.z());
        float incl;
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
        // aircraft is below target, must therefore pitch up and thrust
    	System.out.println("Rise");
    	adjustPitch(inputs, climbAngle);
        adjustThrust(inputs, 2f);
        setLeftWingInclination(FloatMath.toRadians(7));
        setRightWingInclination(FloatMath.toRadians(7));
    }
    
    private void dropPID(AutopilotInputs inputs, float target) {
        // if aircraft overshoots target, it simply drops -> can probably be done better
        System.out.println("Fall");
        adjustPitch(inputs, FloatMath.toRadians(-5));
        adjustThrust(inputs, -3f);
        

    }

    // Uses PID controller to stabilise yaw
    private void stableYawPID(AutopilotInputs input) {
        yawPID.setSetpoint(0f);
        float actual = input.getHeading();
        float output = (float)yawPID.getOutput(actual);

        if (Math.abs(output) < config.getMaxAOA() - 0.02) {
            setVerStabInclination(-output);
        }
    }
    

    // Initialises all the parameters for the autpilot
    @Override
    public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {


        pitchPID = new MiniPID(0.5, 0.005, 0.005);
        pitchPID.setOutputLimits(Math.toRadians(30));
        thrustPID = new MiniPID(2, 0.01, 0.005);
//        thrustPID.setOutputLimits(0f, config.getMaxThrust());

        yawPID = new MiniPID(1.2, 0.15, 0.1);
        yawPID.setOutputLimits(Math.toRadians(30));
        climbAngle = FloatMath.toRadians(25);

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

//        flyStraightPID(inputs, 0f);
        // target of climb should be the z position of the cube

//        climbPID(inputs, 10f);

//        stableYawPID(inputs);
    	adjustheightPID(inputs, 10f);
    	if (inputs.getY() >= 10) {
    		System.out.println("goal reached:" + inputs.getZ());
    	}

        // prints useful variables
//        System.out.printf("height = %s\t pitch = %s\t thrust = %s\t y-velocity = %s\t hStab = %s\t \n", inputs.getY(), inputs.getPitch(), newThrust, approxVel.y(), getHorStabInclination());
//        System.out.printf("height = %s\t pitch = %s\t hStab = %s\t y-vel = %s\t thrust = %s\t \n", inputs.getY(), inputs.getPitch(), getHorStabInclination(), approxVel.y(), newThrust);

//        Vector3f test = new Vector3f(0,10,-10);
//        System.out.println(Math.toDegrees(Math.atan2(test.y(), -test.z())));


        
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