package physics;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.inversion.GaussJordanInverter;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.vector.dense.BasicVector;

import datatypes.*;

public class PhysicsEngine {
			
	private final float gravity, weight, wingLiftSlope, horStabLiftSlope, verStabLiftSlope, tailSize, wingX, engineZ;
	private final Vector weightVector; //posRW, posLW, posE, posT, velRW, velLW, velE, velT, accRW, accLW, accE, accT;
	private final Matrix inertiaInv;
	
	public PhysicsEngine(AutopilotConfig config) {
		this.gravity = config.getGravity();
		this.wingLiftSlope = config.getWingLiftSlope();
		this.horStabLiftSlope = config.getHorStabLiftSlope();
		this.verStabLiftSlope = config.getVerStabLiftSlope();
		this.tailSize = config.getTailSize();
		this.wingX = config.getWingX();
		
		float tailMass = config.getTailMass(), engineMass = config.getEngineMass(), wingMass = config.getWingMass();
		
		this.engineZ = tailMass / engineMass * tailSize;
		
		this.weight = wingMass*2 + engineMass + tailMass;
		this.weightVector = new BasicVector(new double[] {0, -gravity * weight, 0});
		
		float inertiaX = tailSize*tailSize*tailMass + engineZ*engineZ*engineMass,
				inertiaZ = 2*wingX*wingX*wingMass;
		this.inertiaInv = new Basic1DMatrix(3, 3, new double[]{
				1/inertiaX, 0, 						0, 
				0, 			1/(inertiaX+inertiaZ),  0,
				0, 			0,						1/inertiaZ});
	}
	
	float getEngineZ() {
		return this.engineZ;
	}
	
	public Matrix transMat(Drone drone) {
		return buildTransformMatrix(drone.getPitch(), drone.getYaw(), drone.getRoll());
	}
	
	public Matrix transMatInv(Drone drone) {
		return new GaussJordanInverter(transMat(drone)).inverse();
	}
	
	/**
	 * wing inclinations are already updated
	 */
	public void update(float dt, Drone drone) {
		
		
		// acceleration calculation
		
		float leftWingInclination = drone.getLeftWingInclination(), rightWingInclination = drone.getRightWingInclination(),
				horStabInclination = drone.getHorStabInclination(), verStabInclination = drone.getVerStabInclination();
		Vector oldVel = drone.getVelocity();
		
		Vector weightVectorD = transMat(drone).multiply(weightVector),
				thrustVectorD = new BasicVector(new double[] {0, 0, -drone.getThrust()}),
				relVelD = transMat(drone).multiply(oldVel),
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
		Vector acceleration = transMatInv(drone).multiply(accelerationD);
		
		// rotation calculation
		
		Vector wingForce = rightWingLiftD.subtract(leftWingLiftD),
				tailForce = horStabLiftD.add(verStabLiftD);
		
		Vector wingTorque = new BasicVector(new double[]{0, wingX * wingForce.get(2), -wingX * wingForce.get(1)}),
				tailTorque = crossProduct(new BasicVector(new double[]{0, 0, tailSize}), tailForce);
		
		Vector rotAcceleration = inertiaInv.multiply(wingTorque.add(tailTorque));		

		// position/orientation update
		// newx = a/2*t� + v*t + x
		
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
	
	public Vector posRW(Drone drone) {
		double  x = drone.getPosition().get(0) + wingX,
				y = drone.getPosition().get(1),
				z = drone.getPosition().get(2);
		return new BasicVector(new double[]{x, y, z});
	}
	
	public Vector posLW(Drone drone) {
		double  x = drone.getPosition().get(0) - wingX,
				y = drone.getPosition().get(1),
				z = drone.getPosition().get(2);
		return new BasicVector(new double[]{x, y, z});
	}
	
	public Vector posE(Drone drone) {
		double  x = drone.getPosition().get(0),
				y = drone.getPosition().get(1),
				z = drone.getPosition().get(2) - engineZ;
		return new BasicVector(new double[]{x, y, z});
	}
	
	public Vector posT(Drone drone) {
		double  x = drone.getPosition().get(0),
				y = drone.getPosition().get(1),
				z = drone.getPosition().get(2) + tailSize;
		return new BasicVector(new double[]{x, y, z});
	}
	
	public Vector relPosRW(Drone drone) {
		double  x = wingX,
				y = 0,
				z = 0;
		return new BasicVector(new double[]{x, y, z});
	}
	
	public Vector relPosLW(Drone drone) {
		double  x = -wingX,
				y = 0,
				z = 0;
		return new BasicVector(new double[]{x, y, z});
	}
	
	public Vector relPosE(Drone drone) {
		double  x = 0,
				y = 0,
				z = -engineZ;
		return new BasicVector(new double[]{x, y, z});
	}
	
	public Vector relPosT(Drone drone) {
		double  x = 0,
				y = 0,
				z = tailSize;
		return new BasicVector(new double[]{x, y, z});
	}
	
	public Vector angularVelocity(Drone drone) {
		Vector numerator = crossProduct(drone.getPosition(),drone.getVelocity());
		double denominator = drone.getPosition().innerProduct(drone.getPosition());
		return numerator.multiply(1/denominator);
	}
	
	public Vector velRW(Drone drone) {
		return drone.getVelocity().add(crossProduct(this.angularVelocity(drone), this.relPosRW(drone)));
	}
	
	public Vector velLW(Drone drone) {
		return drone.getVelocity().add(crossProduct(this.angularVelocity(drone), this.relPosLW(drone)));
	}
	
	public Vector velE(Drone drone) {
		return drone.getVelocity().add(crossProduct(this.angularVelocity(drone), this.relPosE(drone)));
	}
	
	public Vector velT(Drone drone) {
		return drone.getVelocity().add(crossProduct(this.angularVelocity(drone), this.relPosT(drone)));
	}
	
	public float radius(Drone drone) {
		return (float) Math.sqrt(Math.pow(drone.getPosition().get(0), 2 )+Math.pow(drone.getPosition().get(1), 2 )
		+Math.pow(drone.getPosition().get(2), 2 ));
	}
	
	public Vector accelerationD(Drone drone) {
		float leftWingInclination = drone.getLeftWingInclination(), rightWingInclination = drone.getRightWingInclination(),
				horStabInclination = drone.getHorStabInclination(), verStabInclination = drone.getVerStabInclination();
		Vector oldVel = drone.getVelocity();
		
		Vector weightVectorD = transMat(drone).multiply(weightVector),
				thrustVectorD = new BasicVector(new double[] {0, 0, -drone.getThrust()}),
				relVelD = transMat(drone).multiply(oldVel),
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
		
		return weightVectorD.add(thrustVectorD).add(leftWingLiftD).add(rightWingLiftD).add(horStabLiftD).add(verStabLiftD).divide(weight);
	}
	
	public Vector tanAcc(Drone drone) {
		Vector numerator = drone.getVelocity();
		double denominator = Math.sqrt(drone.getVelocity().innerProduct(drone.getVelocity()));
		Vector normVel = numerator.multiply(1/denominator);
		return crossProduct(accelerationD(drone), normVel);
	}
	
	public Vector angularAcceleration(Drone drone) {
		Vector numerator = tanAcc(drone);
		double denominator = radius(drone);
		return numerator.multiply(1/denominator);
	}
	
	public Vector acceleration(Drone drone) {
		return transMatInv(drone).multiply(accelerationD(drone));
	}

	public Vector accRW(Drone drone) {
		return acceleration(drone).add(crossProduct(angularVelocity(drone), crossProduct(angularVelocity(drone), relPosRW(drone)))
				.add(crossProduct(angularAcceleration(drone), relPosLW(drone))));
	}
	
	public Vector accLW(Drone drone) {
		return acceleration(drone).add(crossProduct(angularVelocity(drone), crossProduct(angularVelocity(drone), relPosRW(drone)))
				.add(crossProduct(angularAcceleration(drone), relPosLW(drone))));
	}
	
	public Vector accE(Drone drone) {
		return acceleration(drone).add(crossProduct(angularVelocity(drone), crossProduct(angularVelocity(drone), relPosRW(drone)))
				.add(crossProduct(angularAcceleration(drone), relPosLW(drone))));
	}
	
	public Vector accT(Drone drone) {
		return acceleration(drone).add(crossProduct(angularVelocity(drone), crossProduct(angularVelocity(drone), relPosRW(drone)))
				.add(crossProduct(angularAcceleration(drone), relPosLW(drone))));
	}
	
}
