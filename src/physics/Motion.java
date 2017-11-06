package physics;

import autopilot.Autopilot;
import datatypes.AutopilotConfig;
import datatypes.AutopilotInputs;
import datatypes.AutopilotOutputs;
import gui.AutopilotGUI;

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
    private Vector3f oldPos, newPos, approxVel = new Vector3f(0,0,0);
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
    
    public void flyStraight(AutopilotInputs input, Vector3f vel) {
//    	to fly straight AOA has to be 0;
//    	AOA is -atan2(y,x) and -atan2(y,x) == 0 if y=0
//    	y = horProjVelD * horStabNormalVectorD
//    	==> y-vel * cos(inclination) + z-vel * sin(inclination) == 0
//    	Vector vel = velocity(input.getX(), input.getY(), input.getZ(), input.getElapsedTime());
    	Vector3f relVelD = transMat(input).transform(vel, new Vector3f());
    	float yVel = (float) relVelD.y;
    	float zVel = (float) relVelD.z;
    	float incl = 0;
    	if (yVel == 0) {
    		setHorStabInclination(0);
//    		System.out.println("0");
    	}else {
    		float n = 0f;
    		incl = (float) (2*(Math.PI*n + Math.atan((zVel-Math.sqrt(Math.pow(yVel,2)+Math.pow(zVel, 2))/yVel))));
    		System.out.println(incl);
    		if (incl > Math.PI/2) {
    			n = -1/2f;
//    			System.out.println("1");
        		incl = (float) (2*(Math.PI*n + Math.atan((zVel-Math.sqrt(Math.pow(yVel,2)+Math.pow(zVel, 2))/yVel))));
    		}
    		else if (incl < -Math.PI/2) {
    			n = 1/2f;
//    			System.out.println("2");
        		incl = (float) (2*(Math.PI*n + Math.atan((zVel-Math.sqrt(Math.pow(yVel,2)+Math.pow(zVel, 2))/yVel))));
    		}
    		if (Math.abs(incl) < Math.PI/4) {
    			setHorStabInclination(incl);
    		}
//    		System.out.println("motion" + ":" + vel);
//    		System.out.println(incl);
    	}
    	
    }
    
    public void stabilisePitch(AutopilotInputs input) {
    	if (input.getPitch() < 0) {
    		setHorStabInclination((float)-Math.toRadians(5));
    	}else if (input.getPitch() > 0) {
    		setHorStabInclination((float)Math.toRadians(5));
    	}else {
    		setHorStabInclination((float)Math.toRadians(0));
    	}
    }
    
    

    @Override
    public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
        setConfig(config);
        
//        gui = new AutopilotGUI(config.getNbColumns(), config.getNbRows());
//        gui.updateImage(inputs.getImage());
//        gui.showGUI();
        
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
    	
    	newPos = new Vector3f(inputs.getX(),inputs.getY(),inputs.getZ());
    	if (oldPos != null)
    		approxVel = newPos.sub(oldPos, new Vector3f()).mul(1/inputs.getElapsedTime(), new Vector3f());
    	oldPos = new Vector3f(newPos);
    	
//    	gui.updateImage(inputs.getImage());
    	
        ImageRecognition recog = new ImageRecognition(inputs.getImage(), config.getNbRows(), config.getNbColumns(), config.getHorizontalAngleOfView(), config.getVerticalAngleOfView());
        double[] center = recog.getCenter();
        
        if(recog.getDistApprox() < 4){
        	System.exit(0);
        }

//        if(center != null) {
//            centerTarget(center, inputs);
//        } else {
////            System.out.println("Image recon ziet niets");
//        }
        
//        Vector newPos = new BasicVector(new double[] {getX(), getY(), getZ()});
//        Vector approxVel = newPos.subtract(oldPos).multiply(1/inputs.getElapsedTime());
//        oldPos = newPos;
        
        flyStraight(inputs, approxVel);

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
	public void simulationEnded() {
	}
}
