package pilot;

import java.util.ArrayList;
import java.util.OptionalDouble;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import recognition.Cube;
import recognition.ImageProcessing;
import utils.FloatMath;
import utils.Utils;

import com.stormbots.MiniPID;

import gui.AutopilotGUI;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

public class FlyPilot extends PilotPart {

    private AutopilotConfig config;
    
    private MiniPID pitchUpPID, thrustUpPID, pitchDownPID, thrustDownPID, yawPID, rollPID;
    
	private boolean ended;
	
	
	
	

	
    private float x = Float.NaN;
    private float y = Float.NaN;
    private float leftWingInclination;
    private float rightWingInclination;
    private float horStabInclination;
    private float verStabInclination;
    private float newThrust;
    private Vector3f oldPos;
    private Vector3f approxVel = new Vector3f(0f,0f,0f);
    private float climbAngle;
    private AutopilotGUI gui;
    private ImageProcessing recog;

	
	@Override
	public void initialize(AutopilotConfig config) {
		this.config = config;
		
        pitchUpPID = new MiniPID(1.5, 0, 0);
        pitchUpPID.setOutputLimits(Math.toRadians(20));
        thrustUpPID = new MiniPID(1, 0, 0);
        pitchDownPID = new MiniPID(1, 0, 0.07);
        pitchDownPID.setOutputLimits(Math.toRadians(20));
        thrustDownPID = new MiniPID(1, 0.05, 0.05);
        //YawPID still needs a lot of thought
        yawPID = new MiniPID(0.2, 0, 0);
        yawPID.setOutputLimits(Math.toRadians(30));
        rollPID = new MiniPID(1, 0.000005, 0);
        rollPID.setOutputLimits(Math.toRadians(30));

        climbAngle = FloatMath.toRadians(10);
		
	}
	


    private Vector3f horProjVel(AutopilotInputs inputs) {
        Vector3f relVelD = getTransMat(inputs).transform(approxVel, new Vector3f());
        return new Vector3f(0, relVelD.y, relVelD.z);
    }

    // AOA of right wing
    private float rightWingAOA(AutopilotInputs inputs) {
        Vector3f horProjVelD = horProjVel(inputs);
        Vector3f WingAttackVectorD = new Vector3f(0f, (float)Math.sin((double) rightWingInclination), (float)-Math.cos((double) rightWingInclination));
        Vector3f WingNormalVectorD = FloatMath.cross(new Vector3f(1,0,0), WingAttackVectorD);
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
    

//  // Calculate wing inclination such that lift cancels weight
//  private float stableInclination(AutopilotInputs inputs) {
//      float rAOA = rightWingAOA(inputs);
//      float lAOA = leftWingAOA(inputs);
//      float L = config.getWingLiftSlope()*(rAOA + lAOA)*horProjVel(inputs).dot(horProjVel(inputs));
//      double incl = inputs.getPitch() - Math.asin(config.getGravity()*getMass()/L);
//      return (float)incl;
//  }
  
  private void setInclNoAOA(AutopilotInputs inputs) {
//  	float rAOA = rightWingAOA(inputs);
//      float lAOA = leftWingAOA(inputs);
//      if (approxVel.z() == 0) {
//      	setRightWingInclination(FloatMath.toRadians(10));
//      	setLeftWingInclination(FloatMath.toRadians(10));
//      }else {
//      	if (rAOA > FloatMath.toRadians(10)) {
//      		setRightWingInclination(FloatMath.toRadians(10-(FloatMath.toDegrees(rAOA)-10)));
//      	}else if (rAOA < FloatMath.toRadians(-10)) {
//      		setRightWingInclination(FloatMath.toRadians(10+(FloatMath.toDegrees(rAOA)+10)));
//      	}else
//      		setRightWingInclination(FloatMath.toRadians(10));
//      	if (lAOA > FloatMath.toRadians(10)) {
//      		setLeftWingInclination(FloatMath.toRadians(10-(FloatMath.toDegrees(lAOA)-10)));
//      	}else if (lAOA < FloatMath.toRadians(-10)) {
//      		setLeftWingInclination(FloatMath.toRadians(10+(FloatMath.toDegrees(lAOA)+10)));
//      	}else
//      		setLeftWingInclination(FloatMath.toRadians(10));
//      }
  	setRightWingInclination(FloatMath.toRadians(7));
  	setLeftWingInclination(FloatMath.toRadians(7));
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
		      setNewThrust(output*400);
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
  
	//// Uses PID controller to stabilise yaw
	//private void adjustHeading(AutopilotInputs input, float target) {
	//    float actual = input.getHeading();
	//    Vector3f rel = getRelVel(input);
	//    float turn = (float) Math.atan2(rel.x(), -rel.z());
	//
	//    if (Math.abs(actual - target) < FloatMath.toRadians(1) ) {
	//        float stable = turn - actual;
	//        setVerStabInclination(stable);
	////        adjustRoll(input, 0f);
	//        return;
	//    }
	//
	//    yawPID.setSetpoint(target);
	//    float min = turn - actual + config.getMaxAOA();
	//    float max = turn - actual - config.getMaxAOA();
	//    yawPID.setOutputLimits(min, max);
	//
	//    float output = (float)yawPID.getOutput(actual);
	//
	//    setVerStabInclination(-output);
	//}
	
	//private void adjustRoll(AutopilotInputs inputs, float target) {
	//    rollPID.setSetpoint(target);
	//    float actual = inputs.getRoll();
	//    float output = (float)rollPID.getOutput(actual);
	//    setLeftWingInclination(leftWingInclination - output);
	//    setRightWingInclination(rightWingInclination + output);
	//}
  
  


  // Set wings to empirical values found by Flor. PIDs set pitch and thrust to fly straight.
  private void flyStraightPID(AutopilotInputs input) {
      adjustPitchUp(input, 0f);
      adjustThrustUp(input, 0.2f);
  }

  // causes drone to climb by changing pitch and using thrust to increase vertical velocity
  private void climbPID(AutopilotInputs inputs) {
      adjustPitchUp(inputs, climbAngle);
      adjustThrustUp(inputs, 4f);
  }

  private void dropPID(AutopilotInputs inputs) {
      adjustPitchDown(inputs, FloatMath.toRadians(-3f));
      adjustThrustDown(inputs, -2f);
  }

  // causes drone to rise by increasing lift through higher speed.
  private void risePID(AutopilotInputs inputs) {
      //pitch op 0
      adjustPitchUp(inputs, 0);
      //thrust bijgeven
      adjustThrustUp(inputs, 3f);
  }

  private void descendPID(AutopilotInputs inputs) {
      //pitch op 0
      adjustPitchDown(inputs, 0);
      //val vertragen
      adjustThrustDown(inputs, -1.5f);
  }
  
  private void adjustHeight(AutopilotInputs input, float height) {
      float actualHeight = input.getY();

      //sterk stijgen
      if (height - actualHeight > 2) {
//          System.out.println("Climb");
      	climbPID(input);
      	setInclNoAOA(input);
      }
      //stijgen
      else if (height - actualHeight > 0.5) {
//          System.out.println("Rise");
      	risePID(input);
      	setInclNoAOA(input);
      }
      //sterk dalen
      else if (height - actualHeight < -2) {
//          System.out.println("Drop");
      	dropPID(input);
      	setLeftWingInclination(FloatMath.toRadians(2));
          setRightWingInclination(FloatMath.toRadians(2));
      }
      //dalen
      else if (height - actualHeight < -0.5) {
//          System.out.println("Descend");
      	descendPID(input);
      	setInclNoAOA(input);
      }
      //horizontaal blijven
      else {
//          System.out.println("Level");
      	flyStraightPID(input);
      	setInclNoAOA(input);
      }
  }
  
  private void adjustWidth(AutopilotInputs input, float width) {
		float actualWidth = input.getX();
		if (width-actualWidth > 3) {
			
		}
}
	
	  
	
	private float last = 0;
	private int efficiencyCounter = 0;
	private ArrayList<Float> avgList = new ArrayList<>();
	private float height;
		
	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {
				  	
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
	      	
	//  	adjustHeight(inputs, height);
	      adjustHeight(inputs, 200);
	//    adjustHeading(inputs, FloatMath.toRadians(15));
	//      adjustRoll(inputs, 0f);
	  	if (Math.abs(height - inputs.getY()) < 4) {
	//  		System.out.println("goal reached:" + inputs.getZ());
	  	}

  		AutopilotOutputs output = Utils.buildOutputs(leftWingInclination, rightWingInclination, verStabInclination, horStabInclination, getNewThrust(), 0,0,0);
  	
    	return output;
    }
  
	private float getMass() {
		return config.getEngineMass() + config.getTailMass() + 2*config.getWingMass();
	}

    private Vector3f getRelVel(AutopilotInputs input) {
        return getTransMat(input).transform(approxVel, new Vector3f());
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
	
	public float getNewThrust() {
		return newThrust;
	}



	public void setLeftWingInclination(float leftWingInclination) {
		this.leftWingInclination = leftWingInclination;
	}



	public void setRightWingInclination(float rightWingInclination) {
		this.rightWingInclination = rightWingInclination;
	}

	

	public void setNewThrust(float newThrust) {
		this.newThrust = newThrust;
	}

	
	public void setHorStabInclination(float horStabInclination) {
		this.horStabInclination = horStabInclination;
	}


	@Override
	public boolean ended() {
		return ended;
	}

	@Override
	public void close() {
		// TODO: maybe add imageRecog .close()
	}

	@Override
	public String taskName() {
		return "Fly";
	}

}
