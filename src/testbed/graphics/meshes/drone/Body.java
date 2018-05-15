package testbed.graphics.meshes.drone;

import interfaces.AutopilotConfig;
import utils.Utils;

public class Body extends DroneComponent {

    private float thickness;

    public Body(AutopilotConfig config, float thickness) {
        this.thickness = thickness;
        finalizer(config);
    }

    @Override
    public void setPositions() {
        this.positions = new float[]{
        		config.getTailSize()/8 , this.thickness, config.getTailSize(), //#0
                -config.getTailSize()/8, this.thickness, config.getTailSize(), //#1
                config.getTailSize()/8 , this.thickness, -Utils.getEngineZ(config), //#2
                -config.getTailSize()/8, this.thickness, -Utils.getEngineZ(config), //#3
                
        		config.getTailSize()/8 , -this.thickness, config.getTailSize(), //#0
                -config.getTailSize()/8, -this.thickness, config.getTailSize(), //#1
                config.getTailSize()/8 , -this.thickness, -Utils.getEngineZ(config), //#2
                -config.getTailSize()/8, -this.thickness, -Utils.getEngineZ(config), //#3
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
                //Face 1 (front)
                0f, 1f, 1f,
                0f, 1f, 1f,
                0f, 1f, 1f,
                0f, 1f, 1f,
        };
    }

    @Override
    public void setIndices() {
        this.indices = new int[]{
        		//top
                0, 1, 2, 3, 2, 1,
                //bot
                5, 6, 7, 5, 6, 4,
                //right
                4, 6, 2, 4, 0, 2,
                //left
                5, 1, 3, 5, 3, 7,
                //front
                7, 3, 2, 7, 2, 6,
                //back
                5, 1, 0, 5, 0, 4, 
        };
    }
}
