package world.drone;

import engine.graph.Mesh;
import physics.Drone;

public class RightWing extends DroneComponent {


    public RightWing(Drone drone) {
        super(drone);
    }

    protected void setPositions() {
        this.positions = new float[]{
                //right wing
                0f                  , 0f, -drone.getWingX()/4, //#0
                drone.getWingX()*2f, 0f, -drone.getWingX()/4, //#1
                drone.getWingX()*2f, 0f, drone.getWingX()/4, //#2
                0f                  , 0f, drone.getWingX()/4, //#3
        };
    }

    protected void setColours() {
        this.colours = new float[]{
                1f, 1f, 0.0f,
                1f, 0.5f, 0.0f,
                1f, 0.5f, 0.0f,
                1f, 1f, 0.0f,
        };
    }

    protected void setIndices(){
        this.indices = new int[]{
                1, 2, 0, 3, 2, 0,
        };
    }

}
