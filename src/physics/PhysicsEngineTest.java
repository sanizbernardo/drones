package physics;

import static org.junit.Assert.*;

import java.util.function.IntToDoubleFunction;

import org.joml.Vector3f;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import interfaces.AutopilotConfig;

public class PhysicsEngineTest {
	
	private static final float EPSILON = 0.001f;
	
	private static AutopilotConfig configNoWings, configNoHorStab;
	private static PhysicsEngine physicsEngineNoWings, physicsNoHorStab;
	private Drone droneNoWings, droneNoHorStab;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		configNoWings = new AutopilotConfig(){
			public float getGravity() {return 10f;}
			public float getWingX() {return 2.5f;}
			public float getTailSize() {return 5f;}
			public float getEngineMass() {return 70f;}
			public float getWingMass() {return 25f;}
			public float getTailMass() {return 30f;}
			public float getMaxThrust() {return 5000f;}
			public float getMaxAOA() {return -1f;}
			public float getWingLiftSlope() {return 0f;}
			public float getHorStabLiftSlope() {return 0f;}
			public float getVerStabLiftSlope() {return 0f;}
			public float getHorizontalAngleOfView() {return -1f;}
			public float getVerticalAngleOfView() {return -1f;}
			public int getNbColumns() {return -1;}
			public int getNbRows() {return -1;}			
		};
		physicsEngineNoWings = new PhysicsEngine(configNoWings);
		
		configNoHorStab = new AutopilotConfig(){
			public float getGravity() {return 10f;}
			public float getWingX() {return 2.5f;}
			public float getTailSize() {return 5f;}
			public float getEngineMass() {return 70f;}
			public float getWingMass() {return 25f;}
			public float getTailMass() {return 30f;}
			public float getMaxThrust() {return -1f;}
			public float getMaxAOA() {return -1f;}
			public float getWingLiftSlope() {return 0.11f;}
			public float getHorStabLiftSlope() {return 0f;}
			public float getVerStabLiftSlope() {return 0.11f;}
			public float getHorizontalAngleOfView() {return -1f;}
			public float getVerticalAngleOfView() {return -1f;}
			public int getNbColumns() {return -1;}
			public int getNbRows() {return -1;}			
		};
		physicsNoHorStab = new PhysicsEngine(configNoHorStab);
	}

	@Before
	public void setUp() throws Exception {
		droneNoWings = new Drone(configNoWings);
		droneNoHorStab = new Drone(configNoHorStab);
	}

	/**
	 * Falling test 1: no wings, starting from (0,5000,0) with no initial speed and no thrust, for 20 seconds.
	 * @throws Exception 
	 */
	@Test
	public void testFallingNoWings1() throws Exception {
		float dt = 1f;
		
		droneNoWings.setPosition(new Vector3f(0f, 5000f, 0f));
		
		IntToDoubleFunction posFuncY = i -> 5000.0 - 10.0*i*dt*i*dt/2.0;
		IntToDoubleFunction velFuncY = i -> -10.0*i*dt;
		
		for (int i = 1; i <= 20/dt; i++) {
			physicsEngineNoWings.update(dt, droneNoWings);
			assertEquals(0, droneNoWings.getPosition().x, EPSILON);
			assertEquals(posFuncY.applyAsDouble(i), droneNoWings.getPosition().y, EPSILON);
			assertEquals(0, droneNoWings.getPosition().z, EPSILON);
			assertEquals(0, droneNoWings.getVelocity().x, EPSILON);
			assertEquals(velFuncY.applyAsDouble(i), droneNoWings.getVelocity().y, EPSILON);
			assertEquals(0, droneNoWings.getVelocity().z, EPSILON);
		}
	}
	
	/**
	 * Falling test 2: no wings, starting from (0,5000,0) with an initial speed of (0,0,-10) and no thrust, for 20 seconds.
	 * @throws Exception 
	 */
	@Test
	public void testFallingNoWings2() throws Exception {
		float dt = 1f;
		
		droneNoWings.setPosition(new Vector3f(0f, 5000f, 0f));
		droneNoWings.setVelocity(new Vector3f(0f, 0f, -10f));
		
		IntToDoubleFunction posFuncY = i -> 5000.0 - 10.0*i*dt*i*dt/2.0;
		IntToDoubleFunction posFuncZ = i -> -10.0*i*dt;
		IntToDoubleFunction velFuncY = i -> -10.0*i*dt;
		
		for (int i = 1; i <= 20/dt; i++) {
			physicsEngineNoWings.update(dt, droneNoWings);
			assertEquals(0, droneNoWings.getPosition().x, EPSILON);
			assertEquals(posFuncY.applyAsDouble(i), droneNoWings.getPosition().y, EPSILON);
			assertEquals(posFuncZ.applyAsDouble(i), droneNoWings.getPosition().z, EPSILON);
			assertEquals(0, droneNoWings.getVelocity().x, EPSILON);
			assertEquals(velFuncY.applyAsDouble(i), droneNoWings.getVelocity().y, EPSILON);
			assertEquals(-10, droneNoWings.getVelocity().z, EPSILON);
		}
	}
	
	/**
	 * Thrust test, no wings, starting from (0,2000,0) with no initial speed with no thrust for 5 seconds and a thrust of 2000 for 15 seconds.
	 * @throws Exception 
	 */
	@Test
	public void testThrustNoWings() throws Exception {
		droneNoWings.setPosition(new Vector3f(0f, 2000f, 0f));
		
		IntToDoubleFunction posFuncY = i -> 2000.0 - 10.0*i*i/2.0;
		IntToDoubleFunction posFuncZ = i -> (i >= 5) ? -2000.0/150.0/2.0*(i-4)*(i-4) : 0d;
		IntToDoubleFunction velFuncY = i -> -10.0*i;
		IntToDoubleFunction velFuncZ = i -> (i >= 5) ? -2000.0/150.0*(i-4) : 0d;
		
		for (int i = 1; i <= 20; i++) {
			if (i == 5)
				droneNoWings.setThrust(2000);
			physicsEngineNoWings.update(1, droneNoWings);
			assertEquals(0, droneNoWings.getPosition().x, EPSILON);
			assertEquals(posFuncY.applyAsDouble(i), droneNoWings.getPosition().y, EPSILON);
			assertEquals(posFuncZ.applyAsDouble(i), droneNoWings.getPosition().z, EPSILON);
			assertEquals(0, droneNoWings.getVelocity().x, EPSILON);
			assertEquals(velFuncY.applyAsDouble(i), droneNoWings.getVelocity().y, EPSILON);
			assertEquals(velFuncZ.applyAsDouble(i), droneNoWings.getVelocity().z, EPSILON);
		}
	}
	
	/**
	 * Wings (no hor stab) falling test, starting from (0,2000,0) with no initial speed and no thrust, for 20 seconds 
	 * @throws Exception 
	 */
	@Test
	public void testWingsFalling1() throws Exception {
		float dt = 1f;
		
		droneNoHorStab.setPosition(new Vector3f(0f, 2000f, 0f));		
		
		float vy = 0f, y = 2000f;
				
		for (int i = 1; i <= 20/dt; i++) {
			physicsNoHorStab.update(dt, droneNoHorStab);
			y += vy + (-10 + 0.11/150.0 * Math.PI * vy * vy)/2.0;
			vy += -10 + 0.11/150.0 * Math.PI * vy * vy;	
			
			assertEquals(0, droneNoHorStab.getPosition().x, EPSILON);
			assertEquals(y, droneNoHorStab.getPosition().y, EPSILON);
			assertEquals(0, droneNoHorStab.getPosition().z, EPSILON);
			assertEquals(0, droneNoHorStab.getVelocity().x, EPSILON);
			assertEquals(vy, droneNoHorStab.getVelocity().y, EPSILON);
			assertEquals(0, droneNoHorStab.getVelocity().z, EPSILON);
		}		
	}
}
