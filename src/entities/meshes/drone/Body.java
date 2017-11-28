package entities.meshes.drone;

import interfaces.AutopilotConfig;
import utils.Utils;

public class Body extends DroneComponent {

    public Body(AutopilotConfig config) {
        super(config);
    }

    @Override
    public void setPositions() {
        this.positions = new float[]{
        		config.getTailSize()/8, 0f, config.getTailSize(), //#0
                -config.getTailSize()/8, 0f, config.getTailSize(), //#1
                config.getTailSize()/8,0,-Utils.getEngineZ(config),
                -config.getTailSize()/8,0,-Utils.getEngineZ(config),
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
