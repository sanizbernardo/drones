package testbed.world.premade;


import org.joml.Vector3f;

import autopilot.Pilot;
import testbed.entities.WorldObject;
import testbed.entities.airport.Tarmac;
import testbed.entities.ground.Ground;
import testbed.world.World;
import utils.Utils;

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
