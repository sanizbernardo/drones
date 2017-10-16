import static org.junit.Assert.*;

import java.util.function.IntToDoubleFunction;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.la4j.vector.dense.BasicVector;

import datatypes.AutopilotConfig;

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
			public float getMaxThrust() {return -1f;}
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
	 */
	@Test
	public void testFallingNoWings1() {
		float dt = 1f;
		
		droneNoWings.setPosition(new BasicVector(new double[]{0,5000,0}));
		
		IntToDoubleFunction posFuncY = i -> 5000.0 - 10.0*i*dt*i*dt/2.0;
		IntToDoubleFunction velFuncY = i -> -10.0*i*dt;
		
		for (int i = 1; i <= 20/dt; i++) {
			physicsEngineNoWings.update(dt, droneNoWings);
			assertEquals(0, droneNoWings.getPosition().get(0), EPSILON);
			assertEquals(posFuncY.applyAsDouble(i), droneNoWings.getPosition().get(1), EPSILON);
			assertEquals(0, droneNoWings.getPosition().get(2), EPSILON);
			assertEquals(0, droneNoWings.getVelocity().get(0), EPSILON);
			assertEquals(velFuncY.applyAsDouble(i), droneNoWings.getVelocity().get(1), EPSILON);
			assertEquals(0, droneNoWings.getVelocity().get(2), EPSILON);
		}
	}
	
	/**
	 * Falling test 2: no wings, starting from (0,5000,0) with an initial speed of (0,0,-10) and no thrust, for 20 seconds.
	 */
	@Test
	public void testFallingNoWings2() {
		float dt = 1f;
		
		droneNoWings.setPosition(new BasicVector(new double[]{0,5000,0}));
		droneNoWings.setVelocity(new BasicVector(new double[]{0,0,-10}));
		
		IntToDoubleFunction posFuncY = i -> 5000.0 - 10.0*i*dt*i*dt/2.0;
		IntToDoubleFunction posFuncZ = i -> -10.0*i*dt;
		IntToDoubleFunction velFuncY = i -> -10.0*i*dt;
		
		for (int i = 1; i <= 20/dt; i++) {
			physicsEngineNoWings.update(dt, droneNoWings);
			assertEquals(0, droneNoWings.getPosition().get(0), EPSILON);
			assertEquals(posFuncY.applyAsDouble(i), droneNoWings.getPosition().get(1), EPSILON);
			assertEquals(posFuncZ.applyAsDouble(i), droneNoWings.getPosition().get(2), EPSILON);
			assertEquals(0, droneNoWings.getVelocity().get(0), EPSILON);
			assertEquals(velFuncY.applyAsDouble(i), droneNoWings.getVelocity().get(1), EPSILON);
			assertEquals(-10, droneNoWings.getVelocity().get(2), EPSILON);
		}
	}
	
	/**
	 * Thrust test, no wings, starting from (0,2000,0) with no initial speed with no thrust for 5 seconds and a thrust of 2000 for 15 seconds.
	 */
	@Test
	public void testThrustNoWings() {
		droneNoWings.setPosition(new BasicVector(new double[]{0,2000,0}));
		
		IntToDoubleFunction posFuncY = i -> 2000.0 - 10.0*i*i/2.0;
		IntToDoubleFunction posFuncZ = i -> (i >= 5) ? -2000.0/150.0/2.0*(i-4)*(i-4) : 0d;
		IntToDoubleFunction velFuncY = i -> -10.0*i;
		IntToDoubleFunction velFuncZ = i -> (i >= 5) ? -2000.0/150.0*(i-4) : 0d;
		
		for (int i = 1; i <= 20; i++) {
			if (i == 5)
				droneNoWings.setThrust(2000);
			physicsEngineNoWings.update(1, droneNoWings);
			assertEquals(0, droneNoWings.getPosition().get(0), EPSILON);
			assertEquals(posFuncY.applyAsDouble(i), droneNoWings.getPosition().get(1), EPSILON);
			assertEquals(posFuncZ.applyAsDouble(i), droneNoWings.getPosition().get(2), EPSILON);
			assertEquals(0, droneNoWings.getVelocity().get(0), EPSILON);
			assertEquals(velFuncY.applyAsDouble(i), droneNoWings.getVelocity().get(1), EPSILON);
			assertEquals(velFuncZ.applyAsDouble(i), droneNoWings.getVelocity().get(2), EPSILON);
		}
	}
	
	/**
	 * Wings (no hor stab) falling test, starting from (0,2000,0) with no initial speed and no thrust, for 20 seconds 
	 */
	@Test
	public void testWingsFalling1() {
		float dt = 1f;
		
		droneNoHorStab.setPosition(new BasicVector(new double[]{0,2000,0}));		
		
		float vy = 0f, y = 2000f;
				
		for (int i = 1; i <= 20/dt; i++) {
			physicsNoHorStab.update(dt, droneNoHorStab);
			y += vy + (-10 + 0.11/150.0 * Math.PI * vy * vy)/2.0;
			vy += -10 + 0.11/150.0 * Math.PI * vy * vy;	
			
			assertEquals(0, droneNoHorStab.getPosition().get(0), EPSILON);
			assertEquals(y, droneNoHorStab.getPosition().get(1), EPSILON);
			assertEquals(0, droneNoHorStab.getPosition().get(2), EPSILON);
			assertEquals(0, droneNoHorStab.getVelocity().get(0), EPSILON);
			assertEquals(vy, droneNoHorStab.getVelocity().get(1), EPSILON);
			assertEquals(0, droneNoHorStab.getVelocity().get(2), EPSILON);
		}		
	}
	
//	/**
//	 * Cruise test
//	 */
//	@Test
//	public void testCruise() {
//		
//		droneNoHorStab.setPosition(new BasicVector(new double[]{0,2000,0}));
//		droneNoHorStab.setOrientation(new BasicVector(new double[]{0, 0, 0.78}));
//		droneNoHorStab.setVelocity(new BasicVector(new double[]{0, 0, -150}));
//		
//		for (int i = 1; i <= 200; i++) {
//			physicsNoHorStab.update(0.1f, droneNoHorStab);
//			System.out.println(droneNoHorStab.getPosition() + " | " + droneNoHorStab.getVelocity() + " | " + droneNoHorStab.getOrientation());
//		}
//	}

}
