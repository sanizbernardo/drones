package physics;

import autopilot.Autopilot;
import datatypes.AutopilotConfig;
import datatypes.AutopilotInputs;
import datatypes.AutopilotOutputs;
import gui.AutopilotGUI;

import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;
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
    private Vector oldPos, newPos, approxVel = new BasicVector(new double[] {0,0,0});
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
    
//    private Vector velocity(float x, float y, float z, float time) {
//        if (Float.isNaN(getX())|| Float.isNaN(getY()) || Float.isNaN(getZ())){
//            setX(x);
//            setY(y);
//            setZ(z);
//            return new BasicVector(new double[]{ 0,0,0});
//        }
//        Vector dpos = new BasicVector(new double[]{ x-getX(), y-getY(), z-getZ()});
//        Vector vel = dpos.multiply(1/time);
//        setX(x);
//        setY(y);
//        setZ(z);
//        return vel;
//    }
    
    public void flyStraight(AutopilotInputs inputn, Vector vel) {
//    	to fly straight AOA has to be 0;
//    	AOA is -atan2(y,x) and -atan2(y,x) == 0 if y=0
//    	y = horProjVelD * horStabNormalVectorD
//    	==> y-vel * cos(inclination) + z-vel * sin(inclination) == 0
//    	Vector vel = velocity(input.getX(), input.getY(), input.getZ(), input.getElapsedTime());
    	float yVel = (float) vel.get(1);
    	float zVel = (float) vel.get(2);
    	float incl = 0;
    	if (yVel == 0) {
    		setHorStabInclination(0);
//    		System.out.println("0");
    	}else {
    		float n = 0f;
    		incl = (float) (2*(Math.PI*n + Math.atan((zVel-Math.sqrt(Math.pow(yVel,2)+Math.pow(zVel, 2))/yVel))));
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
//    		System.out.println("motion" + ":" + zVel);
//    		System.out.println(Math.toDegrees(this.horStabInclination));
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
    	
    	newPos = new BasicVector(new double[]{inputs.getX(),inputs.getY(),inputs.getZ()});
    	if (oldPos != null)
    		approxVel = newPos.subtract(oldPos).multiply(1/inputs.getElapsedTime());
    	oldPos = newPos.copy();
    	
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
