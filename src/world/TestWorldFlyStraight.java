package world;

import engine.IWorldRules;
import entities.WorldObject;
import physics.Motion;
import utils.Cubes;
import utils.Utils;


/**
 * Place where all the GameItem are to be placed in
 */
public class TestWorldFlyStraight extends World implements IWorldRules {

    public TestWorldFlyStraight() {
        super(1, true);
    }

    /**
     * Is called in the abstract class
     */
    @Override
    public void setup() {
    	config = Utils.createDefaultConfig();

//        config = new AutopilotConfig() {
//            public float getGravity() {return 9.81f;}
//            public float getWingX() {return 0.25f;}
//            public float getTailSize() {return 0.5f;}
//            public float getEngineMass() {return 3.5f;}
//            public float getWingMass() {return 1.25f;}
//            public float getTailMass() {return 1.5f;}
//            public float getMaxThrust() {return 5000f;}
//            public float getMaxAOA() {return (float) Math.toRadians(45);}
//            public float getWingLiftSlope() {return 0.11f;}
//            public float getHorStabLiftSlope() {return 0.11f;}
//            public float getVerStabLiftSlope() {return 0.11f;}
//            public float getHorizontalAngleOfView() {return (float) Math.toRadians(120f);}
//            public float getVerticalAngleOfView() {return (float) Math.toRadians(120f);}
//            public int getNbColumns() {return 200;}
//            public int getNbRows() {return 200;}};
    	  
    	physics.init(config, 20);

    	planner = new Motion();
    	
        worldObjects = new WorldObject[1];
        worldObjects[0] = new WorldObject(Cubes.getCubes()[0].getMesh());
        worldObjects[0].setPosition(0f,0f,-100f);

    }

	@Override
	public String getDescription() {
		return "Demonstrates the ability to fly straight and horizonatally of the autopilot. The drone starts with an initial velocity,"
				+ " flying towards a cube located 100m infront of it.";
	}

}
