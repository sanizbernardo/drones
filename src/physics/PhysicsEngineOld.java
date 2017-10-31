//package physics;
//
//import org.la4j.Matrix;
//import org.la4j.Vector;
//import org.la4j.inversion.GaussJordanInverter;
//import org.la4j.matrix.dense.Basic1DMatrix;
//import org.la4j.vector.dense.BasicVector;
//
//import datatypes.*;
//
//public class PhysicsEngineOld {
//
//	private final float gravity, weight, wingLiftSlope, horStabLiftSlope, verStabLiftSlope, tailSize, wingX, engineZ;
//	private final Vector weightVector; //posRW, posLW, posE, posT, velRW, velLW, velE, velT, accRW, accLW, accE, accT;
//	private final Matrix inertia, inertiaInv;
//	private Vector wingTorque, tailTorque;
//
//	public PhysicsEngineOld(AutopilotConfig config) {
//		this.gravity = config.getGravity();
//		this.wingLiftSlope = config.getWingLiftSlope();
//		this.horStabLiftSlope = config.getHorStabLiftSlope();
//		this.verStabLiftSlope = config.getVerStabLiftSlope();
//		this.tailSize = config.getTailSize();
//		this.wingX = config.getWingX();
//
//		float tailMass = config.getTailMass(), engineMass = config.getEngineMass(), wingMass = config.getWingMass();
//
//		this.engineZ = tailMass / engineMass * tailSize;
//
//		this.weight = wingMass*2 + engineMass + tailMass;
//		this.weightVector = new BasicVector(new double[] {0, -gravity * weight, 0});
//
//		float inertiaX = tailSize*tailSize*tailMass + engineZ*engineZ*engineMass,
//				inertiaZ = 2*wingX*wingX*wingMass;
//		this.inertia = new Basic1DMatrix(3, 3, new double[]{
//				inertiaX, 	0, 						0,
//				0, 			inertiaX+inertiaZ,		0,
//				0, 			0,						inertiaZ});
//
//		this.inertiaInv = new GaussJordanInverter(inertia).inverse();
//	}
//
//	float getEngineZ() {
//		return this.engineZ;
//	}
//
//	public Matrix transMat(Drone drone) {
//		return buildTransformMatrix(drone.getPitch(), drone.getYaw(), drone.getRoll());
//	}
//
//	public Matrix transMatInv(Drone drone) {
//		return new GaussJordanInverter(transMat(drone)).inverse();
//	}
//
//	/**
//	 * wing inclinations are already updated
//	 */
//	public void update(float dt, Drone drone) {
//
//		Vector oldVel = drone.getVelocity();
//
//		// rotation calculation
//
////		Vector wingForce = rightWingLiftD.subtract(leftWingLiftD),
////				tailForce = horStabLiftD.add(verStabLiftD);
////
////		Vector wingTorque = new BasicVector(new double[]{0, wingX * wingForce.get(2), -wingX * wingForce.get(1)}),
////				tailTorque = crossProduct(new BasicVector(new double[]{0, 0, tailSize}), tailForce);
//
////		Vector rotAcceleration = inertiaInv.multiply(wingTorque.add(tailTorque));
//
//		// position/orientation update
//		// newx = a/2*t� + v*t + x
//
//		drone.setPosition(drone.getPosition().add(oldVel.multiply(dt)).add(acceleration(drone).multiply(dt*dt/2)));
//		drone.setOrientation(drone.getOrientation().add(drone.getRotation().multiply(dt)).add(angularAcceleration(dt, drone).multiply(dt*dt/2)));
//
//		// velocity/rotation update
//		// newv = a*t + v
//
//		drone.setVelocity(oldVel.add(acceleration(drone).multiply(dt)));
//		drone.setRotation(drone.getRotation().add(angularVelocity(dt, drone)));
//	}
//
//	public static Matrix buildTransformMatrix(float xAngle, float yAngle, float zAngle) {
//		Matrix xRot = new Basic1DMatrix(3,3, new double[]{
//				1, 0,				 0,
//				0, Math.cos(xAngle), -Math.sin(xAngle),
//				0, Math.sin(xAngle), Math.cos(xAngle)}),
//
//			   yRot = new Basic1DMatrix(3,3, new double[]{
//				Math.cos(yAngle),  0, Math.sin(yAngle),
//				0, 				   1, 0,
//				-Math.sin(yAngle), 0, Math.cos(yAngle)}),
//
//			   zRot = new Basic1DMatrix(3,3, new double[]{
//				Math.cos(zAngle), -Math.sin(zAngle), 0,
//				Math.sin(zAngle), Math.cos(zAngle),  0,
//				0, 				  0, 				 1});
//
//		return yRot.multiply(xRot).multiply(zRot);
//	}
//
//
//	public Vector crossProduct(Vector v1, Vector v2) {
//		double  x = v1.get(1)*v2.get(2) - v1.get(2)*v2.get(1),
//				y = v1.get(2)*v2.get(0) - v1.get(0)*v2.get(2),
//				z = v1.get(0)*v2.get(1) - v1.get(1)*v2.get(0);
//		return new BasicVector(new double[]{x, y, z});
//	}
//
//	public Vector posRW(Drone drone) {
//		return drone.getPosition().add(transMat(drone).multiply(relPosRW(drone)));
//	}
//
//	public Vector posLW(Drone drone) {
//		return drone.getPosition().add(transMat(drone).multiply(relPosLW(drone)));
//	}
//
//	public Vector posE(Drone drone) {
//		return drone.getPosition().add(transMat(drone).multiply(relPosE(drone)));
//	}
//
//	public Vector posT(Drone drone) {
//		return drone.getPosition().add(transMat(drone).multiply(relPosT(drone)));
//	}
//
//	public Vector relPosRW(Drone drone) {
//		double  x = wingX;
//		return new BasicVector(new double[]{x, 0, 0});
//	}
//
//	public Vector relPosLW(Drone drone) {
//		double  x = -wingX;
//		return new BasicVector(new double[]{x, 0, 0});
//	}
//
//	public Vector relPosE(Drone drone) {
//		double 	z = -engineZ;
//		return new BasicVector(new double[]{0, 0, z});
//	}
//
//	public Vector relPosT(Drone drone) {
//		double  z = tailSize;
//		return new BasicVector(new double[]{0, 0, z});
//	}
//
//	public Vector angularVelocity(float dt, Drone drone) {
//		return angularAcceleration(dt, drone).multiply(dt);
//	}
//
//	public Vector velRW(float dt, Drone drone) {
//		return drone.getVelocity().add(crossProduct(this.angularVelocity(dt, drone), this.relPosRW(drone)));
//	}
//
//	public Vector velLW(float dt, Drone drone) {
//		return drone.getVelocity().add(crossProduct(this.angularVelocity(dt, drone), this.relPosLW(drone)));
//	}
//
//	public Vector velE(float dt, Drone drone) {
//		return drone.getVelocity().add(crossProduct(this.angularVelocity(dt, drone), this.relPosE(drone)));
//	}
//
//	public Vector velT(float dt, Drone drone) {
//		return drone.getVelocity().add(crossProduct(this.angularVelocity(dt, drone), this.relPosT(drone)));
//	}
//
//	public float radius(Drone drone) {
//		return (float) Math.sqrt(Math.pow(drone.getPosition().get(0), 2 )+Math.pow(drone.getPosition().get(1), 2 )
//		+Math.pow(drone.getPosition().get(2), 2 ));
//	}
//
//	public Vector accelerationD(Drone drone) {
//		float leftWingInclination = drone.getLeftWingInclination(), rightWingInclination = drone.getRightWingInclination(),
//				horStabInclination = drone.getHorStabInclination(), verStabInclination = drone.getVerStabInclination();
//		Vector oldVel = drone.getVelocity();
//
//		Vector weightVectorD = transMat(drone).multiply(weightVector),
//				thrustVectorD = new BasicVector(new double[] {0, 0, -drone.getThrust()}),
//				relVelD = transMat(drone).multiply(oldVel),
//				leftWingAttackVectorD = new BasicVector(new double[] {0, Math.sin((double) leftWingInclination), -Math.cos((double) leftWingInclination)}),
//				rightWingAttackVectorD = new BasicVector(new double[] {0, Math.sin((double) rightWingInclination), -Math.cos((double) rightWingInclination)}),
//				horStabAttackVectorD = new BasicVector(new double[] {0, Math.sin((double) horStabInclination), -Math.cos((double) horStabInclination)}),
//				verStabAttackVectorD = new BasicVector(new double[] {-Math.sin((double) verStabInclination), 0, -Math.cos((double) verStabInclination)}),
//				leftWingNormalVectorD = new BasicVector(new double[] {0, Math.cos((double) leftWingInclination), Math.sin((double) leftWingInclination)}),
//				rightWingNormalVectorD = new BasicVector(new double[] {0, Math.cos((double) rightWingInclination), Math.sin((double) rightWingInclination),}),
//				horStabNormalVectorD = new BasicVector(new double[] {0, Math.cos((double) horStabInclination), Math.sin((double) horStabInclination),}),
//				verStabNormalVectorD = new BasicVector(new double[] {-Math.cos((double) verStabInclination), 0, Math.sin((double) verStabInclination)});
//
//		Vector horProjVelD = new BasicVector(new double[] {0, relVelD.get(1), relVelD.get(2)}),
//				verProjVelD = new BasicVector(new double[] {relVelD.get(0),0, relVelD.get(2)});
//
//		float leftWingAOA = (float) -Math.atan2(horProjVelD.innerProduct(leftWingNormalVectorD), horProjVelD.innerProduct(leftWingAttackVectorD)),
//				rightWingAOA = (float) -Math.atan2(horProjVelD.innerProduct(rightWingNormalVectorD), horProjVelD.innerProduct(rightWingAttackVectorD)),
//				horStabAOA = (float) -Math.atan2(horProjVelD.innerProduct(horStabNormalVectorD), horProjVelD.innerProduct(horStabAttackVectorD)),
//				verStabAOA = (float) -Math.atan2(verProjVelD.innerProduct(verStabNormalVectorD), verProjVelD.innerProduct(verStabAttackVectorD));
//
//
//		Vector leftWingLiftD = leftWingNormalVectorD.multiply(wingLiftSlope * horProjVelD.innerProduct(horProjVelD) * leftWingAOA),
//				rightWingLiftD = rightWingNormalVectorD.multiply(wingLiftSlope * horProjVelD.innerProduct(horProjVelD) * rightWingAOA),
//				horStabLiftD = horStabNormalVectorD.multiply(horStabLiftSlope * horProjVelD.innerProduct(horProjVelD) * horStabAOA),
//				verStabLiftD = verStabNormalVectorD.multiply(verStabLiftSlope * verProjVelD.innerProduct(verProjVelD) * verStabAOA);
//
//		Vector wingForce = rightWingLiftD.subtract(leftWingLiftD),
//				tailForce = horStabLiftD.add(verStabLiftD);
//
//		this.wingTorque = new BasicVector(new double[]{0, wingX * wingForce.get(2), -wingX * wingForce.get(1)});
//		this.tailTorque = crossProduct(new BasicVector(new double[]{0, 0, tailSize}), tailForce);
//
//		return weightVectorD.add(thrustVectorD).add(leftWingLiftD).add(rightWingLiftD).add(horStabLiftD).add(verStabLiftD).divide(weight);
//	}
//
////	public Vector tanAcc(Drone drone) {
////		Vector numerator = drone.getVelocity();
////		double denominator = Math.sqrt(drone.getVelocity().innerProduct(drone.getVelocity()));
////		Vector normVel = numerator.multiply(1/denominator);
////		return normVel.multiply(accelerationD(drone).innerProduct(normVel));
////	}
//
////	public Vector angularAcceleration(Drone drone) {
////		Vector numerator = tanAcc(drone);
////		double denominator = radius(drone);
////		return numerator.multiply(1/denominator);
////	}
//
//	public Vector angularAcceleration(float dt, Drone drone) {
//		return inertiaInv.multiply(torque(drone).subtract(crossProduct(drone.getRotation(), inertia.multiply(drone.getRotation()))));
//	}
//
//	public Vector torque(Drone drone) {
//		return wingTorque.add(tailTorque);
//	}
//
//	public Vector acceleration(Drone drone) {
//		return transMatInv(drone).multiply(accelerationD(drone));
//	}
//
//	public Vector accRW(float dt, Drone drone) {
//		return acceleration(drone).add(crossProduct(angularVelocity(dt, drone), crossProduct(angularVelocity(dt, drone), relPosRW(drone)))
//				.add(crossProduct(angularAcceleration(dt, drone), relPosLW(drone))));
//	}
//
//	public Vector accLW(float dt, Drone drone) {
//		return acceleration(drone).add(crossProduct(angularVelocity(dt, drone), crossProduct(angularVelocity(dt, drone), relPosRW(drone)))
//				.add(crossProduct(angularAcceleration(dt, drone), relPosLW(drone))));
//	}
//
//	public Vector accE(float dt, Drone drone) {
//		return acceleration(drone).add(crossProduct(angularVelocity(dt, drone), crossProduct(angularVelocity(dt, drone), relPosRW(drone)))
//				.add(crossProduct(angularAcceleration(dt, drone), relPosLW(drone))));
//	}
//
//	public Vector accT(float dt, Drone drone) {
//		return acceleration(drone).add(crossProduct(angularVelocity(dt, drone), crossProduct(angularVelocity(dt, drone), relPosRW(drone)))
//				.add(crossProduct(angularAcceleration(dt, drone), relPosLW(drone))));
//	}
//
//}
