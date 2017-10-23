package utils;

public class Constants {
    /**
     * Window constants
     */
    public static final String TITLE = "PnO Drone Simulation";
    public static final boolean VSYNC = true;
    //only for initialization!
    public static final int WIDTH = 1000;
    //only for initialization!
    public static final int HEIGHT = 660;


    /**
     * Renderer constants
     */
    //Field of View in Radians
    public static final float FOV = (float) Math.toRadians(60.0f);

    public static final float Z_NEAR = 0.01f;

    public static final float Z_FAR = 1000.f;

    /**
     * Game engine
     */
    public static final int TARGET_FPS = 75;

    public static final int TARGET_UPS = 30;

    /**
     * physics.World constants
     */
    public static final float MOUSE_SENSITIVITY = 0.4f;

    public static final float CAMERA_POS_STEP = 0.05f;
    
    
    /**
     * default setup settings
     */
    public static final float DEFAULT_GRAVITY = 9.81f;
    
    public static final float DEFAULT_WINGX = 2.5f;
    
    public static final float DEFAULT_TAILSIZE = 5f;
    
    public static final float DEFAULT_ENGINE_MASS = 70f;
    
    public static final float DEFAULT_WING_MASS = 25f;
    
    public static final float DEFAULT_TAIL_MASS = 30f;
    
    public static final int DEFAULT_MAX_AOA = 60;
    
    public static final float DEFAULT_WING_LIFTSLOPE = 0.11f;
    
    public static final float DEFAULT_VER_STAB_LIFTSLOPE = 0.11f;
    
    public static final float DEFAULT_HOR_STAB_LIFTSLOPE = 0.11f;
    
    public static final int DEFAULT_VER_FOV = 120;
    
    public static final int DEFAULT_HOR_FOV = 120;
    
    public static final int DEFAULT_NB_COLS = 200;
    
    public static final int DEFAULT_NB_ROWS = 200;
}
