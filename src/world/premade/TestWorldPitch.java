package world.premade;

import engine.IWorldRules;
import entities.WorldObject;
import entities.ground.Ground;
import entities.tarmac.Tarmac;
import pilot.Pilot;
import utils.Utils;
import world.World;

import org.joml.Vector3f;

/**
 * Place where all the GameItem are to be placed in
 */
public class TestWorldPitch extends World implements IWorldRules {

    public TestWorldPitch() {
        super(1, true);
    }

    @Override
    public void setup() {
        config = Utils.createDefaultConfig();

        addDrone(config, new Vector3f(0,100,0), new Vector3f(0,0,-40));


        planner = new Pilot(new int[] {Pilot.FLYING});
        
        worldObjects = new WorldObject[1];
        
        this.ground = new Ground(50);
		this.tarmac = new Tarmac(new Vector3f(0,0,0), 50f, 300f, 0f);

    }

    @Override
    public String getDescription() {
        return "World to get the drone to turn";
    }

}
