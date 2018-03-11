package world.premade;

import org.joml.Vector3f;

import engine.IWorldRules;
import entities.WorldObject;
import physics.Physics;
import pilot.Pilot;
import utils.Cubes;
import utils.FloatMath;
import utils.Utils;
import utils.IO.MouseInput;
import world.World;

public class RotationWorld extends World implements IWorldRules{

	public RotationWorld() {
		super(1, false);
	}

	@Override
	public void setup() {
    	config = Utils.createDefaultConfig();
    	
    	addDrone(config, new Vector3f(0,0,0), new Vector3f(0,0,-12));
    	
    	planner = new Pilot(new int[] {});
        worldObjects = new WorldObject[] {new WorldObject(Cubes.getBlueCube().getMesh())};

        worldObjects[0].setPosition(new Vector3f(0,0,-20));
	}
	
	float heading = 0f;
	float pitch = 0f;
	float roll = 0f;
	
	boolean headingOn = true;
	boolean pitchOn = true;
	boolean rollOn = true;

	@Override
	public void update(float interval, MouseInput mouseInput) {

		float step = 0.005f;

		Physics physics = droneHelper.getDronePhysics(config.getDroneID());
//		if (heading <= FloatMath.toRadians(45) && rollOn) {
//			heading += step;
//			pitch += step;
//			roll += step;
//			
//			System.out.println(physics.getHeading() + " " + physics.getPitch() + " "  + physics.getRoll());
//
//			physics.init(config, new Vector3f(0, 0, 0), 0, heading, pitch, roll);
//
//		} else if (roll >= 0) {
//			rollOn = false;
//			roll -= step;
//			physics.init(config, new Vector3f(0, 0, 0), 0, heading, pitch, roll);
//		}else if (pitch >= 0) {
//			pitch -= step;
//			physics.init(config, new Vector3f(0, 0, 0), 0, heading, pitch, roll);
//		}else if (heading >= 0) {
//			heading -= step;
//			physics.init(config, new Vector3f(0, 0, 0), 0, heading, pitch, roll);
//		}
//		
//		super.update(interval, mouseInput);
	}

	@Override
	public String getDescription() {
		return "Test for rotations";
	}

}
