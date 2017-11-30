package entities.meshes.drone;

import interfaces.AutopilotConfig;

public class LeftWing extends DroneComponent{

    public LeftWing(AutopilotConfig config, float width) {
        super(config, width);
    }

    protected void setPositions() {
        this.positions = new float[]{
                //left wing top
                0f                  , this.width, -config.getWingX()/4, //#0
                config.getWingX()*-2f, this.width, -config.getWingX()/4, //#1
                config.getWingX()*-2f, this.width, config.getWingX()/4, //#2
                0f                  , this.width, config.getWingX()/4, //#3
                
                //left wing bot
                0f                  , -this.width, -config.getWingX()/4, //#0
                config.getWingX()*-2f, -this.width, -config.getWingX()/4, //#1
                config.getWingX()*-2f, -this.width, config.getWingX()/4, //#2
                0f                  , -this.width, config.getWingX()/4, //#3
        };
    }

    protected void setColours() {
        this.colours = new float[]{
                //Face 1 (front)
                1f, 0f, 0.0f,
                1f, 0.5f, 0.0f,
                1f, 0.5f, 0.0f,
                1f, 0f, 0.0f,
                
                //bot
                1f, 0f, 0.0f,
                1f, 0.5f, 0.0f,
                1f, 0.5f, 0.0f,
                1f, 0f, 0.0f,
        };
    }

    protected void setIndices(){
        this.indices = new int[]{
                //Top wing plane
                1, 2, 0, 3, 2, 0,
                //Bot wing plane
                5, 6, 4, 7, 6, 4,
                
                //Back
                5, 0, 4, 5, 1, 0,
                
                //Font 
                6, 2, 3, 6, 3, 7,
                
                //left side
                1, 2, 5, 5, 2, 6, 
                
                //right side
                7, 0 ,3, 7, 3, 4,
        };
    }

}
