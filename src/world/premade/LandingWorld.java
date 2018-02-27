package world.premade;

import org.joml.Vector3f;


import entities.WorldObject;
import entities.ground.Ground;
import entities.tarmac.Tarmac;
import physics.Physics;
import pilot.Pilot;
import utils.Cubes;
import utils.Utils;
import world.World;

public class LandingWorld extends World{

	public LandingWorld() {
		super(1,true);
	}

	@Override
	public void setup() {
		this.config = Utils.createDefaultConfig();
		
		this.physics = new Physics(true);
		this.physics.init(config, new Vector3f(0, -config.getWheelY() + config.getTyreRadius(), 0), 0);
		
		this.planner = new Pilot();
		
		this.ground = new Ground(50);
		this.tarmac = new Tarmac(new Vector3f(0,0,0), 50f, 300f, 0f);
		
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
