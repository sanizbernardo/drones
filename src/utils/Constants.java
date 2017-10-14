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
     * Renderer constatns
     */

    //Field of View in Radians
    public static final float FOV = (float) Math.toRadians(60.0f);

    public static final float Z_NEAR = 0.01f;

    public static final float Z_FAR = 1000.f;

}
