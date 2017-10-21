package world.drone;

import physics.Drone;

public class LeftWing extends DroneComponent{

    public LeftWing(Drone drone) {
        super(drone);
    }

    protected void setPositions() {
        this.positions = new float[]{
                //left wing
                0f                  , 0f, -0.5f, //#0
                drone.getWingX()*-2f, 0f, -0.5f, //#1
                drone.getWingX()*-2f, 0f, 0.5f, //#2
                0f                  , 0f, 0.5f, //#3
        };
    }

    protected void setColours() {
        this.colours = new float[]{
                //Face 1 (front)
                1f, 0f, 0.0f,
                1f, 0.5f, 0.0f,
                1f, 0.5f, 0.0f,
                1f, 0f, 0.0f,
        };
    }

    protected void setIndices(){
        this.indices = new int[]{
                // Front face
                1, 2, 0, 3, 2, 0,
        };
    }

}
