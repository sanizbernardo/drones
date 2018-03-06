package world.premade;


import entities.WorldObject;
import org.joml.Vector3f;
import physics.Physics;
import pilot.Pilot;
import utils.Cubes;
import utils.Utils;
import world.World;

public class TaxiWorld extends World {

    public TaxiWorld() { super(1, true);
    }

    @Override
    public void setup() {
        this.config = Utils.createDefaultConfig();

        this.physics = new Physics(true);

        this.physics.init(config, new Vector3f(0, -config.getWheelY() + config.getTyreRadius(), 0), 0);

        this.planner = new Pilot();

        this.worldObjects = new WorldObject[40];
    }

    @Override
    public String getDescription() {
        return "World to test the pilot responsible for taxiing";
    }


}
