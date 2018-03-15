package world.premade;


import entities.WorldObject;
import entities.ground.Ground;
import entities.tarmac.Tarmac;
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

        addDrone(config, new Vector3f(0, -config.getWheelY() + config.getTyreRadius(), 0), new Vector3f(0,0,0));

        this.planner = new Pilot(new int[] {Pilot.TAXIING});

        this.worldObjects = new WorldObject[] {};

        this.ground = new Ground(50);
        this.tarmac = new Tarmac(new Vector3f(0,0,0), 50f, 300f, 0f);
    }

    @Override
    public String getDescription() {
        return "World to test the pilot responsible for taxiing";
    }


}
