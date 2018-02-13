package physics;


import com.stormbots.MiniPID;
import gui.AutopilotGUI;
import interfaces.Autopilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

import java.util.ArrayList;
import java.util.OptionalDouble;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import recognition.Cube;
import recognition.ImageProcessing;
import utils.FloatMath;

public class Motion implements Autopilot {

    public Motion() {
        pitchUpPID = new MiniPID(1, 0.02, 0.1);
        pitchUpPID.setOutputLimits(Math.toRadians(30));
        thrustUpPID = new MiniPID(5, 0.02, 0.02);
        pitchDownPID = new MiniPID(1, 0, 0.07);
        pitchDownPID.setOutputLimits(Math.toRadians(30));
        thrustDownPID = new MiniPID(1, 0.05, 0.05);
        //YawPID still needs a lot of thought
        yawPID = new MiniPID(0.2, 0, 0);
        yawPID.setOutputLimits(Math.toRadians(30));
        rollPID = new MiniPID(1, 0.000005, 0);
        rollPID.setOutputLimits(Math.toRadians(30));

        climbAngle = FloatMath.toRadians(25);
       
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
    private MiniPID pitchUpPID, thrustUpPID, pitchDownPID, thrustDownPID, yawPID, rollPID;
    private float climbAngle;
    private AutopilotGUI gui;
    private ImageProcessing recog;


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
        double incl = inputs.getPitch() - Math.asin(config.getGravity()*getMass()/L);
        return (float)incl;
    }



    // PID uses horizontal stabiliser to adjust pitch.
    private void adjustPitchUp(AutopilotInputs input, float target) {
        pitchUpPID.setSetpoint(target);

        Vector3f rel = getRelVel(input);
        float climb = (float) Math.atan2(rel.y(), -rel.z());
        float min = climb - input.getPitch() + config.getMaxAOA();
        float max = climb - input.getPitch() - config.getMaxAOA();
        pitchUpPID.setOutputLimits(min, max);

        float actual = input.getPitch();
        float output = (float)pitchUpPID.getOutput(actual);

        setHorStabInclination(-output);
    }
    
    private void adjustPitchDown(AutopilotInputs input, float target) {
        pitchDownPID.setSetpoint(target);

        Vector3f rel = getRelVel(input);
        float climb = (float) Math.atan2(rel.y(), -rel.z());
        float min = climb - input.getPitch() + config.getMaxAOA();
        float max = climb - input.getPitch() - config.getMaxAOA();
        pitchDownPID.setOutputLimits(min, max);

        float actual = input.getPitch();
        float output = (float)pitchDownPID.getOutput(actual);

        setHorStabInclination(-output);
    }

    // PID sets thrust so that y component of velocity is equal to target.
    private void adjustThrustUp(AutopilotInputs inputs, float target) {
        thrustUpPID.setSetpoint(target);
        float actual = approxVel.y();
        float output = (float)thrustUpPID.getOutput(actual);

        // Check that received output is within bounds
        if (output > config.getMaxThrust()) {
            setNewThrust(config.getMaxThrust());
        } else if (output < 0f){
            setNewThrust(0);
        } else {
            setNewThrust(output);
        }
    }
    
    private void adjustThrustDown(AutopilotInputs inputs, float target) {
        thrustDownPID.setSetpoint(target);
        float actual = approxVel.y();
        float output = (float)thrustDownPID.getOutput(actual);

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
    private void adjustHeading(AutopilotInputs input, float target) {
        float actual = input.getHeading();
        Vector3f rel = getRelVel(input);
        float turn = (float) Math.atan2(rel.x(), -rel.z());

        if (Math.abs(actual - target) < FloatMath.toRadians(1) ) {
            float stable = turn - actual;
            setVerStabInclination(stable);
//            adjustRoll(input, 0f);
            return;
        }

        yawPID.setSetpoint(target);
        float min = turn - actual + config.getMaxAOA();
        float max = turn - actual - config.getMaxAOA();
        yawPID.setOutputLimits(min, max);

        float output = (float)yawPID.getOutput(actual);

        setVerStabInclination(-output);
    }

    private void adjustRoll(AutopilotInputs inputs, float target) {
        rollPID.setSetpoint(target);
        float actual = inputs.getRoll();
        float output = (float)rollPID.getOutput(actual);
        setLeftWingInclination(leftWingInclination - output);
        setRightWingInclination(rightWingInclination + output);
    }

    // Set wings to empirical values found by Flor. PIDs set pitch and thrust to fly straight.
    private void flyStraightPID(AutopilotInputs input) {
        adjustPitchUp(input, 0f);
        adjustThrustUp(input, 0f);
        setLeftWingInclination(FloatMath.toRadians(7));
        setRightWingInclination(FloatMath.toRadians(7));
    }

    // causes drone to climb by changing pitch and using thrust to increase vertical velocity
    private void climbPID(AutopilotInputs inputs) {
        adjustPitchUp(inputs, climbAngle);
        adjustThrustUp(inputs, 4f);
    }

    private void dropPID(AutopilotInputs inputs) {
        adjustPitchDown(inputs, FloatMath.toRadians(-3f));
        adjustThrustDown(inputs, -3f);
    }

    // causes drone to rise by increasing lift through higher speed.
    private void risePID(AutopilotInputs inputs) {
        //pitch op 0
        adjustPitchUp(inputs, 0);
        //thrust bijgeven
        adjustThrustUp(inputs, 60f);
    }

    private void descendPID(AutopilotInputs inputs) {
        //pitch op 0
        adjustPitchDown(inputs, 0);
        //val vertragen
        adjustThrustDown(inputs, 20f);
    }
    
    private void adjustHeight(AutopilotInputs input, float height) {
        float actualHeight = input.getY();

        //sterk stijgen
        if (height - actualHeight > 2) {
//            System.out.println("Climb");
        	climbPID(input);
        	setLeftWingInclination(FloatMath.toRadians(7));
            setRightWingInclination(FloatMath.toRadians(7));
        }
        //stijgen
        else if (height - actualHeight > 0.5) {
//            System.out.println("Rise");
        	risePID(input);
        	setLeftWingInclination(FloatMath.toRadians(7));
            setRightWingInclination(FloatMath.toRadians(7));
        }
        //sterk dalen
        else if (height - actualHeight < -2) {
//            System.out.println("Drop");
        	dropPID(input);
        	setLeftWingInclination(FloatMath.toRadians(2));
            setRightWingInclination(FloatMath.toRadians(2));
        }
        //dalen
        else if (height - actualHeight < -0.5) {
//            System.out.println("Descend");
        	descendPID(input);
        	setLeftWingInclination(FloatMath.toRadians(7));
            setRightWingInclination(FloatMath.toRadians(7));
        }
        //horizontaal blijven
        else {
//            System.out.println("Level");
        	flyStraightPID(input);
        	setLeftWingInclination(FloatMath.toRadians(7));
            setRightWingInclination(FloatMath.toRadians(7));
        }
    }
    
    private void adjustWidth(AutopilotInputs input, float width) {
    	float actualWidth = input.getX();
    	if (width-actualWidth > 3) {
    		
    	}
    }



    // Initialises all the parameters for the autpilot
    @Override
    public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {

        setConfig(config);
        gui = new AutopilotGUI(config);
        gui.updateImage(inputs.getImage());
        gui.showGUI();
        
        recog = new ImageProcessing(inputs.getImage(), inputs.getPitch(), inputs.getHeading(), inputs.getRoll(), new float[] {inputs.getX(),inputs.getY(),inputs.getZ()}) ;
        
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

			@Override
			public float getFrontBrakeForce() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public float getLeftBrakeForce() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public float getRightBrakeForce() {
				// TODO Auto-generated method stub
				return 0;
			}
        };
    }

    private float last = 0;
    private int efficiencyCounter = 0;
    private ArrayList<Float> avgList = new ArrayList<>();
    private float height;

    @Override
    public AutopilotOutputs timePassed(AutopilotInputs inputs) {

    	gui.updateImage(inputs.getImage());
    	
        recog = new ImageProcessing(inputs.getImage(), inputs.getPitch(), inputs.getHeading(), inputs.getRoll(), new float[] {inputs.getX(),inputs.getY(),inputs.getZ()}) ;
    	
        //first approximates velocity; useful for AOA
        Vector3f newPos = new Vector3f(inputs.getX(), inputs.getY(), inputs.getZ());
    	if (oldPos != null)
    		approxVel = (newPos.sub(oldPos, new Vector3f())).mul(1/inputs.getElapsedTime(), new Vector3f());
    	oldPos = new Vector3f(newPos);

        float guess = Float.NaN;

    	ArrayList<Cube> list = null;
    	efficiencyCounter++;
    	if (efficiencyCounter % 2 == 0) {
    		 list = recog.generateLocations();

    		 if (list != null && !list.isEmpty()) {
    			 avgList.add(list.get(0).getLocation()[1]);

                 if (efficiencyCounter >= 14 && !avgList.isEmpty()) {

                     OptionalDouble t = avgList.stream()
                             .mapToDouble(a -> a)
                             .average();
                     guess = (float) t.getAsDouble();

                     avgList.clear();

                     efficiencyCounter = 0;
                 }
    		 }


    	}

        if(!Float.isNaN(guess)) {
        	height = list.get(0).getLocation()[1];
        	last = height;

        } else {
        	height = last;
        }
        //System.out.println(height);
        


    	adjustHeight(inputs, height);
//        adjustHeading(inputs, FloatMath.toRadians(15));
        adjustRoll(inputs, 0f);
    	if (Math.abs(height - inputs.getY()) < 4) {
//    		System.out.println("goal reached:" + inputs.getZ());
    	}

       
//        System.out.printf("distance = %s\t height = %s\t pitch = %s\t hStab = %s\t y-vel = %s\t thrust = %s\t \n", inputs.getZ(), inputs.getY(), FloatMath.toDegrees(inputs.getPitch()), FloatMath.toDegrees(getHorStabInclination()), approxVel.y(), newThrust);
//        System.out.printf("heading = %s\t vStab = %s\t roll = %s\t leftWing = %s\t rightWing = %s\t \n", FloatMath.toDegrees(inputs.getHeading()), FloatMath.toDegrees(verStabInclination), FloatMath.toDegrees(inputs.getRoll()), FloatMath.toDegrees(leftWingInclination), FloatMath.toDegrees(rightWingInclination));
//        System.out.printf("height = %s\t pitch = %s\t wAOA = %s\t \n", inputs.getY(), FloatMath.toDegrees(inputs.getPitch()), (leftWingAOA(inputs)));


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

			@Override
			public float getFrontBrakeForce() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public float getLeftBrakeForce() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public float getRightBrakeForce() {
				// TODO Auto-generated method stub
				return 0;
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