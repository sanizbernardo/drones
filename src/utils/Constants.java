package utils;

public class Constants {
    /**
     * Window constants
     */
    public static final String TITLE = "PnO Drone Simulation";
    public static final boolean VSYNC = true;
    //only for initialization!
    public static final int WIDTH = 600;
    //only for initialization!
    public static final int HEIGHT = 480;


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
}
