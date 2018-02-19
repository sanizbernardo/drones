package utils;

public class Constants {

    /**
     * Window constants
     */
    public static final String TITLE = "PnO Drone Simulation";
    public static final boolean VSYNC = true;
    //only for initialization!
    public static final int WIDTH = 1500;
    //only for initialization!
    public static final int HEIGHT = 750;

    /**
     * Drone pickup accuracy
     */

    public static final float PICKUP_DISTANCE = 4f;


    /**
     * Renderer constants
     */
    //Field of View in Radians
    public static final float FOV = (float) Math.toRadians(90f);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000.f;
    public static final float DRONE_THICKNESS = 0.1f;  //in meters
    public static final float DRONE_WHEEL_THICKNESS = 0.08f;
    public static final int DRONE_LEFT_WING = 0;
    public static final int DRONE_RIGHT_WING = 1;
    public static final int DRONE_BODY = 2;
    public static final int DRONE_WHEEL_FRONT = 3;
    public static final int DRONE_WHEEL_BACK_LEFT = 4;
    public static final int DRONE_WHEEL_BACK_RIGHT = 5;
    public static final float DRONE_TIRE_RADIUS = 0.35f;
    /**
     * Game engine
     */
    public static final int TARGET_FPS = 75;

    public static final int TARGET_UPS = 100;

    /**
     * physics.World constants
     */
    public static final float MOUSE_SENSITIVITY = 0.4f;

    public static final float CAMERA_POS_STEP = 0.05f;
    
    
    /**
     * default config settings
     */
    public static final float DEFAULT_GRAVITY = 9.81f;
    
    public static final float DEFAULT_WINGX = 0.25f;
    
    public static final float DEFAULT_TAILSIZE = 0.25f;
    
    public static final float DEFAULT_ENGINE_MASS = 0.125f;
    
    public static final float DEFAULT_WING_MASS = 0.125f;
    
    public static final float DEFAULT_TAIL_MASS = 0.0625f;
    
    public static final int DEFAULT_MAX_AOA = 45;
    
    public static final float DEFAULT_WING_LIFTSLOPE = 0.1f;
    
    public static final float DEFAULT_VER_STAB_LIFTSLOPE = 0.05f;
    
    public static final float DEFAULT_HOR_STAB_LIFTSLOPE = 0.05f;
    
    public static final int DEFAULT_VER_FOV = 120;
    
    public static final int DEFAULT_HOR_FOV = 120;
    
    public static final int DEFAULT_NB_COLS = 200;
    
    public static final int DEFAULT_NB_ROWS = 200;
}