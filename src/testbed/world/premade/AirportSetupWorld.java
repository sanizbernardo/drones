package testbed.world.premade;

import testbed.engine.IWorldRules;
import testbed.entities.WorldObject;
import testbed.entities.airport.Tarmac;
import testbed.entities.ground.Ground;
import testbed.world.World;
import utils.FloatMath;
import utils.Utils;

import org.joml.Vector3f;

public class AirportSetupWorld extends World implements IWorldRules {

    public AirportSetupWorld() {
        super(1, true);
    }

    @Override
    public void setup() {
        config = Utils.createDefaultConfig();

        addDrone(config,  new Vector3f(0,-config.getWheelY()+config.getTyreRadius(),-50), new Vector3f(0, 0, 0), FloatMath.toRadians(45));

        planner = null;

        worldObjects = new WorldObject[0];

        this.ground = new Ground(50);
        this.tarmac = new Tarmac(new Vector3f(0,0,0), 50f, 300f, FloatMath.toRadians(45));
    }

    @Override
    public String getDescription() {
        return "A test for the airport setup";
    }
    
}
