import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.inversion.GaussJordanInverter;
import org.la4j.inversion.MatrixInverter;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.vector.dense.BasicVector;

import datatypes.*;

public class PhysicsEngine {
			
	public PhysicsEngine() {
		
	}
	
	/**
	 * wings are already updated
	 */
	public void update(float dt, Drone drone, World world) {
		Matrix transMat = buildTransformMatrix(drone.getRoll(), drone.getHeading(), drone.getPitch());
		Matrix transMatInv = (new GaussJordanInverter(transMat)).inverse();
		
		// position calculation
		
		Vector newpos = new BasicVector();
		
		// acceleration calculation
		float weight = drone.getWingMass()*2 + drone.getEngineMass() + drone.getTailMass();
		Vector thrustVectorD = new BasicVector(new double[] {0, 0, -drone.getThrust()}),
				weightVector = new BasicVector(new double[] {0, -world.getGravity() * weight, 0}),
				relVelD = transMat.multiply(drone.getVel().subtract(world.getWind())),
				leftWingAttackVectorD = new BasicVector(new double[] {0, Math.sin((double) drone.getLeftWingInclination()), -Math.cos((double) drone.getLeftWingInclination())}),
				rightWingAttackVectorD = new BasicVector(new double[] {0, Math.sin((double) drone.getRightWingInclination()), -Math.cos((double) drone.getRightWingInclination())}),
				horStabAttackVectorD = new BasicVector(new double[] {0, Math.sin((double) drone.getHorStabInclination()), -Math.cos((double) drone.getHorStabInclination())}),
				verStabAttackVectorD = new BasicVector(new double[] {-Math.sin((double) drone.getVerStabInclination()), -Math.cos((double) drone.getVerStabInclination())}),
				leftWingNormalVectorD = new BasicVector(new double[] {0, -Math.cos((double) drone.getLeftWingInclination()), -Math.sin((double) drone.getLeftWingInclination())}),
				rightWingNormalVectorD = new BasicVector(new double[] {0, -Math.cos((double) drone.getRightWingInclination()), -Math.sin((double) drone.getRightWingInclination()),}),
				horStabNormalVectorD = new BasicVector(new double[] {0, -Math.cos((double) drone.getHorStabInclination()), -Math.sin((double) drone.getHorStabInclination()),}),
				verStabNormalVectorD = new BasicVector(new double[] {Math.cos((double) drone.getVerStabInclination()), 0, -Math.sin((double) drone.getVerStabInclination())});
		
		Vector weightVectorD = transMat.multiply(weightVector),
				horProjVelD = new BasicVector(new double[] {relVelD.get(0), relVelD.get(1),0}),
				verProjVelD = new BasicVector(new double[] {relVelD.get(0), relVelD.get(1),0});

		float leftWingAOA = (float) -Math.atan2(horProjVelD.innerProduct(leftWingNormalVectorD), horProjVelD.innerProduct(leftWingAttackVectorD)),
				rightWingAOA = (float) -Math.atan2(horProjVelD.innerProduct(rightWingNormalVectorD), horProjVelD.innerProduct(rightWingAttackVectorD)),
				horStabAOA = (float) -Math.atan2(horProjVelD.innerProduct(horStabNormalVectorD), horProjVelD.innerProduct(horStabAttackVectorD)),
				verStabAOA = (float) -Math.atan2(verProjVelD.innerProduct(verStabNormalVectorD), verProjVelD.innerProduct(verStabAttackVectorD));
		
		Vector leftWingLiftD = leftWingNormalVectorD.multiply(drone.getWingLiftSlope() * horProjVelD.innerProduct(horProjVelD) * leftWingAOA),
				rightWingLiftD = rightWingNormalVectorD.multiply(drone.getWingLiftSlope() * horProjVelD.innerProduct(horProjVelD) * rightWingAOA),
				horStabLiftD = horStabNormalVectorD.multiply(drone.getHorStabLiftSlope() * horProjVelD.innerProduct(horProjVelD) * horStabAOA),
				verStabLiftD = verStabNormalVectorD.multiply(drone.getVerStabLiftSlope() * verProjVelD.innerProduct(verProjVelD) * verStabAOA);
		
		Vector accelerationD = weightVectorD.add(thrustVectorD).add(leftWingLiftD).add(rightWingLiftD).add(horStabLiftD).add(verStabLiftD).divide(weight);
		Vector acceleration = transMatInv.multiply(accelerationD);
		
		// rotation calculation
		
		Vector rotAcceleration = new BasicVector();

		// velocities updates
		
		Vector newvel = new BasicVector();
		Vector newrot = new BasicVector();
		
		// drone update
		drone.setPos(newpos);
		drone.setVel(newvel);
		drone.setRot(newrot);
	}
	
	public static Matrix buildTransformMatrix(float xAngle, float yAngle, float zAngle) {
		return new Basic1DMatrix();
	}
	
	
	
	
	
}
