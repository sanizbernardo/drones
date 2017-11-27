package world;

import org.joml.Vector3f;

import entities.WorldObject;
import entities.meshes.cube.Cube;
import interfaces.AutopilotConfig;
import interfaces.AutopilotFactory;
import physics.Drone;

public class LogWorld extends World {

	public LogWorld() {
		super(1, true, true);
		planner = AutopilotFactory.createAutopilot();
		
		// Dit is de default config die in provided testbed wordt gebruikt,
		// als dit veranderd wordt, moet dit ook elke keer bij het opstarten van
		// het provided testbed aangepast worden.
		config = new AutopilotConfig() {
            public float getGravity() {return 9.81f;}
            public float getWingX() {return 0.5f;}
            public float getTailSize() {return 0.5f;}
            public float getEngineMass() {return 0.25f;}
            public float getWingMass() {return 0.25f;}
            public float getTailMass() {return 0.125f;}
            public float getMaxThrust() {return 5f;}
            public float getMaxAOA() {return (float)Math.toRadians(30);}
            public float getWingLiftSlope() {return 0.1f;}
            public float getHorStabLiftSlope() {return 0.05f;}
            public float getVerStabLiftSlope() {return 0.05f;}
            public float getHorizontalAngleOfView() {return (float) Math.toRadians(120f);}
            public float getVerticalAngleOfView() {return (float) Math.toRadians(120f);}
            public int getNbColumns() {return 200;}
            public int getNbRows() {return 200;}};;
	}
	
	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public void setup() {
		Cube cube = new Cube(240,0.5f);
		worldObjects = new WorldObject[1];
		worldObjects[0] = new WorldObject(cube.getMesh());
		worldObjects[0].setPosition(new Vector3f(0,5,0));
		drone = new Drone(config);
		drone.setVelocity(new Vector3f(0,0,-10));
	}
}
