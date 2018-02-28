package world.premade;

import engine.IWorldRules;
import entities.WorldObject;
import entities.ground.Ground;
import entities.tarmac.Tarmac;
import utils.Utils;
import world.World;
import org.joml.Vector3f;

public class AirportSetupWorld extends World implements IWorldRules {

    public AirportSetupWorld() {
        super(1, true);
    }

    @Override
    public void setup() {
        config = Utils.createDefaultConfig();

        addDrone(config,  new Vector3f(0,-config.getWheelY()+config.getTyreRadius(),-50), new Vector3f(0, 0, 0));

        planner = null;

        worldObjects = new WorldObject[0];

        this.ground = new Ground(50);
        this.tarmac = new Tarmac(new Vector3f(0,0,0), 50f, 300f, 0f);
    }

    @Override
    public String getDescription() {
        return "A test for the airport setup";
    }

}
