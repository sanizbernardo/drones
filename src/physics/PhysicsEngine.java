package physics;

import org.joml.Vector3f;
import org.joml.Matrix3f;

import datatypes.*;

public class PhysicsEngine {
			
	private final float gravity, weight, wingLiftSlope, horStabLiftSlope, verStabLiftSlope, tailSize, wingX, engineZ;
	private final Vector3f weightVector; //posRW, posLW, posE, posT, velRW, velLW, velE, velT, accRW, accLW, accE, accT;
	private final Matrix3f inertia, inertiaInv;
	private Matrix3f rotMat;
	private Vector3f wingTorque = new Vector3f(0,0,0), tailTorque = new Vector3f(0,0,0);
	
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
		this.weightVector = new Vector3f(0f, -gravity * weight, 0f);
		
		float inertiaX = tailSize*tailSize*tailMass + engineZ*engineZ*engineMass,
				inertiaZ = 2*wingX*wingX*wingMass;
		this.inertia = new Matrix3f(
				inertiaX, 	0, 						0, 
				0, 			inertiaX+inertiaZ,		0,
				0, 			0,						inertiaZ);
		
		inertiaInv = new Matrix3f();
		inertia.invert(this.inertiaInv);
		
		this.rotMat = new Matrix3f(
				1,			0,		0, 
				0, 			1,		0,
				0, 			0,		1);
	}
	
	float getEngineZ() {
		return this.engineZ;
	}
	
//	public Matrix3f transMat(Drone drone) {
//		return buildTransformMatrix(drone.getPitch(), drone.getHeading(), drone.getRoll());
//	}
//	
//	public Matrix3f transMatInv(Drone drone) {
//		Matrix3f result = new Matrix3f();
//		return rotMat.invert(result);
//	}
	
	/**
	 * wing inclinations are already updated
	 */
	public void update(float dt, Drone drone) {
		
		
		updateRotMat(dt, drone);
		Vector3f oldVel = drone.getVelocity();
		Vector3f acceleration = acceleration(dt, drone);
		
		drone.setPosition(drone.getPosition().add(oldVel.mul(dt, new Vector3f())).add(acceleration.mul(dt*dt/2, new Vector3f()), new Vector3f()));
		drone.setOrientation(drone.getOrientation().add(drone.getRotation().mul(dt), new Vector3f()).add(angularAcceleration(dt, drone).mul(dt*dt/2, new Vector3f()), new Vector3f()));
				
		drone.setVelocity(oldVel.add(acceleration.mul(dt, new Vector3f()), new Vector3f()));
		drone.setRotation(drone.getRotation().add(angularVelocity(dt, drone), new Vector3f()));
		System.out.println(drone.getPitch());
		System.out.println(rotMat);
		System.out.println(angularVelocity(dt, drone));
	}
	
//	public static Matrix3f buildTransformMatrix(float xAngle, float yAngle, float zAngle) {
//		// column major -> transposed
//		Matrix3f xRot = new Matrix3f(
//				1f, 					  0f,					    0f,
//				0f,  (float)Math.cos(xAngle),  -(float)Math.sin(xAngle),
//				0f, (float)Math.sin(xAngle), (float)Math.cos(xAngle)),
//				
//			   yRot = new Matrix3f(
//				(float)Math.cos(yAngle),  0f, (float)Math.sin(yAngle),
//									 0f,  1f, 						0f,
//				(float)-Math.sin(yAngle),  0f, (float)Math.cos(yAngle)),
//			   
//			   zRot = new Matrix3f(
//				 (float)Math.cos(zAngle), (float)-Math.sin(zAngle), 0f,
//				(float)Math.sin(zAngle), (float)Math.cos(zAngle), 0f,
//									  0f, 					   0f, 1f);
//		
//		return xRot.mul(yRot).mul(zRot);
//	}
	
	public void updateRotMat(float dt, Drone drone) {
		Vector3f angVel = angularVelocity(dt, drone);
		float norm = (float) Math.sqrt(angVel.dot(angVel));
		if (norm != 0) {
			Vector3f normAngVel = angVel.div(norm, new Vector3f());
			float angle = norm*dt;
			Matrix3f uniMat = new Matrix3f(1,			0,		0, 
					0, 			1,		0,
					0, 			0,		1);
			Matrix3f newMat = uniMat.rotate(-angle, normAngVel, new Matrix3f());
			rotMat = newMat.mul(rotMat);
		}
		drone.setRotMat(rotMat);
	}
	
	public Vector3f posRW(Drone drone) {
		return drone.getPosition().add(rotMat.transform(relPosRW(), new Vector3f()), new Vector3f());
	}
	
	public Vector3f posLW(Drone drone) {
		return drone.getPosition().add(rotMat.transform(relPosLW(), new Vector3f()), new Vector3f());
	}
	
	public Vector3f posE(Drone drone) {
		return drone.getPosition().add(rotMat.transform(relPosE(), new Vector3f()), new Vector3f());
	}
	
	public Vector3f posT(Drone drone) {
		return drone.getPosition().add(rotMat.transform(relPosT(), new Vector3f()), new Vector3f());
	}
	
	public Vector3f relPosRW() {
		return new Vector3f(wingX, 0f, 0f);
	}
	
	public Vector3f relPosLW() {
		return new Vector3f(-wingX, 0f, 0f);
	}
	
	public Vector3f relPosE() {
		return new Vector3f(0f, 0f, -engineZ);
	}
	
	public Vector3f relPosT() {
		return new Vector3f(0f, 0f, tailSize);
	}
	
	public Vector3f angularVelocity(float dt, Drone drone) {
		return angularAcceleration(dt, drone).mul(dt, new Vector3f());
	}
	
	public Vector3f velRW(float dt, Drone drone) {
		return drone.getVelocity().add(angularVelocity(dt, drone).cross(relPosRW(), new Vector3f()), new Vector3f());
	}
	
	public Vector3f velLW(float dt, Drone drone) {
		return drone.getVelocity().add(angularVelocity(dt, drone).cross(relPosLW(), new Vector3f()), new Vector3f());
	}
	
//	public Vector3f velE(float dt, Drone drone) {
//		return drone.getVelocity().add(angularVelocity(dt, drone).cross(relPosE(), new Vector3f()));
//	}
	
	public Vector3f velT(float dt, Drone drone) {
		return drone.getVelocity().add(angularVelocity(dt, drone).cross(relPosT(), new Vector3f()), new Vector3f());
	}
	
	public float radius(Drone drone) {
		return (float) Math.sqrt(Math.pow(drone.getPosition().get(0), 2 )+Math.pow(drone.getPosition().get(1), 2 )
		+Math.pow(drone.getPosition().get(2), 2 ));
	}
	
	public Vector3f accelerationD(float dt, Drone drone) {
		float leftWingInclination = drone.getLeftWingInclination(), rightWingInclination = drone.getRightWingInclination(),
				horStabInclination = drone.getHorStabInclination(), verStabInclination = drone.getVerStabInclination();
		
		Vector3f oldVelLW = velLW(dt,drone);
		Vector3f oldVelRW = velRW(dt,drone);
		Vector3f oldVelT = velT(dt,drone);
		
		Vector3f weightVectorD = rotMat.transform(weightVector, new Vector3f()),
				thrustVectorD = new Vector3f(0f, 0f, -drone.getThrust()),
				relVelLW = rotMat.transform(oldVelLW, new Vector3f()),
				relVelRW = rotMat.transform(oldVelRW, new Vector3f()),
				relVelT = rotMat.transform(oldVelT, new Vector3f()),
				leftWingAttackVectorD = new Vector3f(0f, (float)Math.sin((double) leftWingInclination), (float)-Math.cos((double) leftWingInclination)),
				rightWingAttackVectorD = new Vector3f(0f, (float)Math.sin((double) rightWingInclination), (float)-Math.cos((double) rightWingInclination)),
				horStabAttackVectorD = new Vector3f(0f, (float)Math.sin((double) horStabInclination), (float)-Math.cos((double) horStabInclination)),
				verStabAttackVectorD = new Vector3f((float)-Math.sin((double) verStabInclination), 0f, (float)-Math.cos((double) verStabInclination)),
				leftWingNormalVectorD = new Vector3f(0f, (float)Math.cos((double) leftWingInclination), (float)Math.sin((double) leftWingInclination)),
				rightWingNormalVectorD = new Vector3f(0f, (float)Math.cos((double) rightWingInclination), (float)Math.sin((double) rightWingInclination)),
				horStabNormalVectorD = new Vector3f(0f, (float)Math.cos((double) horStabInclination), (float)Math.sin((double) horStabInclination)),
				verStabNormalVectorD = new Vector3f((float)-Math.cos((double) verStabInclination), 0f, (float)Math.sin((double) verStabInclination));
		
		System.out.println(weightVectorD);
		Vector3f horProjVelLW = new Vector3f(0, relVelLW.y, relVelLW.z),
				horProjVelRW = new Vector3f(0, relVelRW.y, relVelRW.z),
				horProjVelT = new Vector3f(0, relVelT.y, relVelT.z),
				verProjVelT = new Vector3f(relVelT.x,0, relVelT.z);
		
		float leftWingAOA = (float) -Math.atan2(horProjVelLW.dot(leftWingNormalVectorD), horProjVelLW.dot(leftWingAttackVectorD)),
				rightWingAOA = (float) -Math.atan2(horProjVelRW.dot(rightWingNormalVectorD), horProjVelRW.dot(rightWingAttackVectorD)),
				horStabAOA = (float) -Math.atan2(horProjVelT.dot(horStabNormalVectorD), horProjVelT.dot(horStabAttackVectorD)),
				verStabAOA = (float) -Math.atan2(verProjVelT.dot(verStabNormalVectorD), verProjVelT.dot(verStabAttackVectorD));
		
		
		Vector3f leftWingLiftD = leftWingNormalVectorD.mul(wingLiftSlope * horProjVelLW.dot(horProjVelLW) * leftWingAOA, new Vector3f()),
				rightWingLiftD = rightWingNormalVectorD.mul(wingLiftSlope * horProjVelRW.dot(horProjVelRW) * rightWingAOA, new Vector3f()),
				horStabLiftD = horStabNormalVectorD.mul(horStabLiftSlope * horProjVelT.dot(horProjVelT) * horStabAOA, new Vector3f()),
				verStabLiftD = verStabNormalVectorD.mul(verStabLiftSlope * verProjVelT.dot(verProjVelT) * verStabAOA, new Vector3f());
	
		Vector3f wingForce = rightWingLiftD.sub(leftWingLiftD, new Vector3f()),
				tailForce = horStabLiftD.add(verStabLiftD, new Vector3f());
		
		this.wingTorque = new Vector3f(0f, wingX * wingForce.z, -wingX * wingForce.y);
		this.tailTorque = (new Vector3f(0f, 0f, tailSize)).cross(tailForce, new Vector3f());

		//float incl = (float) (2*(Math.atan((drone.getVelocity().get(2)-Math.sqrt(Math.pow(drone.getVelocity().get(1),2)+Math.pow(drone.getVelocity().get(2), 2))/drone.getVelocity().get(1)))));
//		System.out.println("proj:" + horProjVelD);
//		System.out.println("pos:" + drone.getPosition());
//		System.out.println("pitch" +":" + drone.getPitch());
//		System.out.println("roll" +":" + drone.getRoll());
//		System.out.println("heading" +":" + drone.getHeading());
//		System.out.println("gewicht:" + (weightVectorD.add(leftWingLiftD.add(rightWingLiftD, new Vector3f()), new Vector3f())).y);
//		System.out.println("thrust:" + (thrustVectorD.sub(leftWingLiftD.add(rightWingLiftD, new Vector3f()), new Vector3f())).z);
//		System.out.println("gewicht:" + weightVectorD);
//		System.out.println("thrust:" + leftWingLiftD);
		
		return weightVectorD.add(thrustVectorD, new Vector3f()).add(leftWingLiftD, new Vector3f())
							.add(rightWingLiftD, new Vector3f()).add(horStabLiftD, new Vector3f())
							.add(verStabLiftD, new Vector3f()).div(weight, new Vector3f());
	}
	
	public Vector3f angularAcceleration(float dt, Drone drone) {
		return inertiaInv.transform(torque(drone).sub(drone.getRotation().cross(
				inertia.transform(drone.getRotation(), new Vector3f()), new Vector3f()), new Vector3f()), new Vector3f());
	}
	
	public Vector3f torque(Drone drone) {
		return wingTorque.add(tailTorque, new Vector3f());
	}
	
	public Vector3f acceleration(float dt, Drone drone) {
		return rotMat.invert(new Matrix3f()).transform(accelerationD(dt, drone), new Vector3f());
	}

//	public Vector3f accRW(float dt, Drone drone) {
//		return acceleration(dt, drone).add(angularVelocity(dt, drone).cross(angularVelocity(dt, drone).cross(relPosRW(), new Vector3f()), new Vector3f())
//				.add(angularAcceleration(dt, drone).cross(relPosLW(), new Vector3f()), new Vector3f()), new Vector3f());
//	}
//	
//	public Vector3f accLW(float dt, Drone drone) {
//		return acceleration(dt, drone).add(angularVelocity(dt, drone).cross(angularVelocity(dt, drone).cross(relPosRW(), new Vector3f()), new Vector3f())
//				.add(angularAcceleration(dt, drone).cross(relPosLW(), new Vector3f()), new Vector3f()), new Vector3f());
//	}
//	
//	public Vector3f accE(float dt, Drone drone) {
//		return acceleration(dt,drone).add(angularVelocity(dt, drone).cross(angularVelocity(dt, drone).cross(relPosRW(), new Vector3f()), new Vector3f())
//				.add(angularAcceleration(dt, drone).cross(relPosLW(), new Vector3f()), new Vector3f()), new Vector3f());
//	}
//	
//	public Vector3f accT(float dt, Drone drone) {
//		return acceleration(dt, drone).add(angularVelocity(dt, drone).cross(angularVelocity(dt, drone).cross(relPosRW(), new Vector3f()), new Vector3f())
//				.add(angularAcceleration(dt, drone).cross(relPosLW(), new Vector3f()), new Vector3f()), new Vector3f());
//	}

}
