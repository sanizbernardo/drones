package world.drone;

import physics.Drone;

public class Body extends DroneComponent {

    public Body(Drone drone) {
        super(drone);
    }

    @Override
    public void setPositions() {
        this.positions = new float[]{
                drone.getTailSize()/8, 0f, drone.getTailSize(), //#0
                -drone.getTailSize()/8, 0f, drone.getTailSize(), //#1
                drone.getTailSize()/8,0,-drone.getEngineZ(),
                -drone.getTailSize()/8,0,-drone.getEngineZ(),
        };
    }

    @Override
    public void setColours() {
        this.colours = new float[]{
                //Face 1 (front)
                0f, 0f, 1f,
                0f, 0f, 1f,
                0f, 0f, 1f,
                0f, 0f, 1f,
        };
    }

    @Override
    public void setIndices() {
        this.indices = new int[]{
                0, 1, 2, 3, 2, 1,
        };
    }
}
