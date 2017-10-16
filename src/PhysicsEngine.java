import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.inversion.GaussJordanInverter;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.vector.dense.BasicVector;

import datatypes.*;

public class PhysicsEngine {
			
	private final float gravity, weight, wingLiftSlope, horStabLiftSlope, verStabLiftSlope, tailSize, wingX;
	private final Vector weightVector;
	private final Matrix inertiaInv;
	
	public PhysicsEngine(AutopilotConfig config) {
		this.gravity = config.getGravity();
		this.wingLiftSlope = config.getWingLiftSlope();
		this.horStabLiftSlope = config.getHorStabLiftSlope();
		this.verStabLiftSlope = config.getVerStabLiftSlope();
		this.tailSize = config.getTailSize();
		this.wingX = config.getWingX();
		
		float tailMass = config.getTailMass(), engineMass = config.getEngineMass(), wingMass = config.getWingMass();
		
		float engineZ = tailMass / engineMass * tailSize;
		
		this.weight = wingMass*2 + engineMass + tailMass;
		this.weightVector = new BasicVector(new double[] {0, -gravity * weight, 0});
		
		float inertiaX = tailSize*tailSize*tailMass + engineZ*engineZ*engineMass,
				inertiaZ = 2*wingX*wingX*wingMass;
		this.inertiaInv = new Basic1DMatrix(3, 3, new double[]{
				1/inertiaX, 0, 						0, 
				0, 			1/(inertiaX+inertiaZ),  0,
				0, 			0,						1/inertiaZ});
	}
	
	
	/**
	 * wing inclinations are already updated
	 */
	public void update(float dt, Drone drone) {
		Matrix transMat = buildTransformMatrix(drone.getPitch(), drone.getHeading(), drone.getRoll());
		Matrix transMatInv = (new GaussJordanInverter(transMat)).inverse();
		
		// acceleration calculation
		
		float leftWingInclination = drone.getLeftWingInclination(), rightWingInclination = drone.getRightWingInclination(),
				horStabInclination = drone.getHorStabInclination(), verStabInclination = drone.getVerStabInclination();
		Vector oldVel = drone.getVelocity();
		
		Vector weightVectorD = transMat.multiply(weightVector),
				thrustVectorD = new BasicVector(new double[] {0, 0, -drone.getThrust()}),
				relVelD = transMat.multiply(oldVel),
				leftWingAttackVectorD = new BasicVector(new double[] {0, Math.sin((double) leftWingInclination), -Math.cos((double) leftWingInclination)}),
				rightWingAttackVectorD = new BasicVector(new double[] {0, Math.sin((double) rightWingInclination), -Math.cos((double) rightWingInclination)}),
				horStabAttackVectorD = new BasicVector(new double[] {0, Math.sin((double) horStabInclination), -Math.cos((double) horStabInclination)}),
				verStabAttackVectorD = new BasicVector(new double[] {-Math.sin((double) verStabInclination), 0, -Math.cos((double) verStabInclination)}),
				leftWingNormalVectorD = new BasicVector(new double[] {0, Math.cos((double) leftWingInclination), Math.sin((double) leftWingInclination)}),
				rightWingNormalVectorD = new BasicVector(new double[] {0, Math.cos((double) rightWingInclination), Math.sin((double) rightWingInclination),}),
				horStabNormalVectorD = new BasicVector(new double[] {0, Math.cos((double) horStabInclination), Math.sin((double) horStabInclination),}),
				verStabNormalVectorD = new BasicVector(new double[] {-Math.cos((double) verStabInclination), 0, Math.sin((double) verStabInclination)});
		
		Vector horProjVelD = new BasicVector(new double[] {0, relVelD.get(1), relVelD.get(2)}),
				verProjVelD = new BasicVector(new double[] {relVelD.get(0),0, relVelD.get(2)});
		
		float leftWingAOA = (float) -Math.atan2(horProjVelD.innerProduct(leftWingNormalVectorD), horProjVelD.innerProduct(leftWingAttackVectorD)),
				rightWingAOA = (float) -Math.atan2(horProjVelD.innerProduct(rightWingNormalVectorD), horProjVelD.innerProduct(rightWingAttackVectorD)),
				horStabAOA = (float) -Math.atan2(horProjVelD.innerProduct(horStabNormalVectorD), horProjVelD.innerProduct(horStabAttackVectorD)),
				verStabAOA = (float) -Math.atan2(verProjVelD.innerProduct(verStabNormalVectorD), verProjVelD.innerProduct(verStabAttackVectorD));
		
		
		Vector leftWingLiftD = leftWingNormalVectorD.multiply(wingLiftSlope * horProjVelD.innerProduct(horProjVelD) * leftWingAOA),
				rightWingLiftD = rightWingNormalVectorD.multiply(wingLiftSlope * horProjVelD.innerProduct(horProjVelD) * rightWingAOA),
				horStabLiftD = horStabNormalVectorD.multiply(horStabLiftSlope * horProjVelD.innerProduct(horProjVelD) * horStabAOA),
				verStabLiftD = verStabNormalVectorD.multiply(verStabLiftSlope * verProjVelD.innerProduct(verProjVelD) * verStabAOA);
		
		Vector accelerationD = weightVectorD.add(thrustVectorD).add(leftWingLiftD).add(rightWingLiftD).add(horStabLiftD).add(verStabLiftD).divide(weight);
		Vector acceleration = transMatInv.multiply(accelerationD);
		
		// rotation calculation
		
		Vector wingForce = rightWingLiftD.subtract(leftWingLiftD),
				tailForce = horStabLiftD.add(verStabLiftD);
		
		Vector wingTorque = new BasicVector(new double[]{0, wingX * wingForce.get(2), -wingX * wingForce.get(1)}),
				tailTorque = crossProduct(new BasicVector(new double[]{0, 0, tailSize}), tailForce);
		
		Vector rotAcceleration = inertiaInv.multiply(wingTorque.add(tailTorque));		

		// position/orientation update
		// newx = a/2*t² + v*t + x
		
		drone.setPosition(drone.getPosition().add(oldVel.multiply(dt)).add(acceleration.multiply(dt*dt/2)));
		drone.setOrientation(drone.getOrientation().add(drone.getRotation().multiply(dt)).add(rotAcceleration.multiply(dt*dt/2)));
		
		// velocity/rotation update
		// newv = a*t + v
		
		drone.setVelocity(oldVel.add(acceleration.multiply(dt)));
		drone.setRotation(drone.getRotation().add(rotAcceleration.multiply(dt)));
	}
	
	public static Matrix buildTransformMatrix(float xAngle, float yAngle, float zAngle) {
		Matrix xRot = new Basic1DMatrix(3,3, new double[]{
				1, 0,				 0,
				0, Math.cos(xAngle), -Math.sin(xAngle),
				0, Math.sin(xAngle), Math.cos(xAngle)}),
				
			   yRot = new Basic1DMatrix(3,3, new double[]{
				Math.cos(yAngle),  0, Math.sin(yAngle),
				0, 				   1, 0,
				-Math.sin(yAngle), 0, Math.cos(yAngle)}),
			   
			   zRot = new Basic1DMatrix(3,3, new double[]{
				Math.cos(zAngle), -Math.sin(zAngle), 0,
				Math.sin(zAngle), Math.cos(zAngle),  0,
				0, 				  0, 				 1});
		
		return yRot.multiply(xRot).multiply(zRot);
	}
	
	
	public Vector crossProduct(Vector v1, Vector v2) {
		double  x = v1.get(1)*v2.get(2) - v1.get(2)*v2.get(1),
				y = v1.get(2)*v2.get(0) - v1.get(0)*v2.get(2),
				z = v1.get(0)*v2.get(1) - v1.get(1)*v2.get(0);
		return new BasicVector(new double[]{x, y, z});
	}
	
	
}
