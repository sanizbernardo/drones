package entities.meshes.drone;

import interfaces.AutopilotConfig;

public class RightWing extends DroneComponent {


    public RightWing(AutopilotConfig config) {
        super(config);
    }

    protected void setPositions() {
        this.positions = new float[]{
                //right wing
                0f                  , 0f, -config.getWingX()/4, //#0
                config.getWingX()*2f, 0f, -config.getWingX()/4, //#1
                config.getWingX()*2f, 0f, config.getWingX()/4, //#2
                0f                  , 0f, config.getWingX()/4, //#3
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
