package world.drone;

import physics.Drone;

public class Body extends DroneComponent {

    public Body(Drone drone) {
        super(drone);
    }

    @Override
    public void setPositions() {
        this.positions = new float[]{
                0.5f, 0f, drone.getTailSize(), //#0
                -0.5f, 0f, drone.getTailSize(), //#1
                0.5f,0,-drone.getEngineZ(),
                -0.5f,0,-drone.getEngineZ(),
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
