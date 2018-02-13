package world;

import org.joml.Vector3f;

import entities.WorldObject;
import entities.meshes.cube.Cube;
import interfaces.AutopilotConfig;
import physics.LogPilot;

public class LogWorld extends World {

	public LogWorld() {
		super(1, true);
		


	}
	
	@Override
	public void setup() {
		// Dit is de default config die in provided testbed wordt gebruikt,
		// als dit veranderd wordt, moet dit ook elke keer bij het opstarten van
		// het provided testbed aangepast worden.
		config = new AutopilotConfig() {
            @Override
			public float getGravity() {return 9.81f;}
            @Override
			public float getWingX() {return 0.5f;}
            @Override
			public float getTailSize() {return 0.5f;}
            @Override
			public float getEngineMass() {return 0.25f;}
            @Override
			public float getWingMass() {return 0.25f;}
            @Override
			public float getTailMass() {return 0.125f;}
            @Override
			public float getMaxThrust() {return 5f;}
            @Override
			public float getMaxAOA() {return (float)Math.toRadians(30);}
            @Override
			public float getWingLiftSlope() {return 0.1f;}
            @Override
			public float getHorStabLiftSlope() {return 0.05f;}
            @Override
			public float getVerStabLiftSlope() {return 0.05f;}
            @Override
			public float getHorizontalAngleOfView() {return (float) Math.toRadians(120f);}
            @Override
			public float getVerticalAngleOfView() {return (float) Math.toRadians(120f);}
            @Override
			public int getNbColumns() {return 200;}
            @Override
			public int getNbRows() {return 200;}
			@Override
			public String getDroneID() {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public float getWheelY() {
				// TODO Auto-generated method stub
				return 0;
			}
			@Override
			public float getFrontWheelZ() {
				// TODO Auto-generated method stub
				return 0;
			}
			@Override
			public float getRearWheelZ() {
				// TODO Auto-generated method stub
				return 0;
			}
			@Override
			public float getRearWheelX() {
				// TODO Auto-generated method stub
				return 0;
			}
			@Override
			public float getTyreSlope() {
				// TODO Auto-generated method stub
				return 0;
			}
			@Override
			public float getDampSlope() {
				// TODO Auto-generated method stub
				return 0;
			}
			@Override
			public float getTyreRadius() {
				// TODO Auto-generated method stub
				return 0;
			}
			@Override
			public float getRMax() {
				// TODO Auto-generated method stub
				return 0;
			}
			@Override
			public float getFcMax() {
				// TODO Auto-generated method stub
				return 0;
			}};
		
        physics.init(config);
            
		planner = new LogPilot();
		
		Cube cube = new Cube(240,0.5f);
		worldObjects = new WorldObject[1];
		worldObjects[0] = new WorldObject(cube.getMesh());
		worldObjects[0].setPosition(new Vector3f(0,5,0));
	}
	
	@Override
	public String getDescription() {
		return null;
	}


}
