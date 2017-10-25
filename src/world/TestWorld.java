package world;

import IO.MouseInput;
import datatypes.AutopilotConfig;
import engine.IWorldRules;
import engine.Window;
import engine.graph.Camera;
import gui.ConfigSetupGUI;
import image.ImageCreator;
import org.joml.Vector2f;
import org.joml.Vector3f;

import physics.Drone;
import physics.PhysicsEngine;
import utils.Constants;
import world.drone.DroneMesh;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

import java.util.Random;

/**
 * Place where all the GameItem are to be placed in
 */
public class TestWorld implements IWorldRules {

    /**
     * The angle of the camera in the current world
     */
    private final Vector3f cameraInc;

    /**
     * The renderer that is rendering this world
     */
    private Renderer renderer;

    /**
     * A camera that defines what we see. An OpenGL camera cannot move on its own, so we move
     * the world itself around. When the camera "goes up" we just shift all world objects
     * downward. Same goes for all other translations and rotations.
     */
    private final Camera freeCamera, droneCamera;

    /**
     * A list of all the GameItems.
     */
    private GameItem[] gameItems, droneItems;

    /**
     * Create the renderer of this world
     */
    public TestWorld() {
        freeCamera = new Camera();
        droneCamera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    private PhysicsEngine physicsEngine;
    private Drone drone;
    
    
    private ImageCreator imageCreator;

    
    /**
     * Init
     */
    @Override
    public void init(Window window) throws Exception {
    	
        Cube redCube = new Cube();
        gameItems = new GameItem[10000];

        Random rand = new Random();

        for(int i = 0; i<gameItems.length; i++) {
            GameItem cube = new GameItem(redCube.getMesh());
            cube.setScale(0.5f);
            int x = rand.nextInt(200)-100,
            		y = rand.nextInt(200)-100,
            		z = rand.nextInt(200)-100;

            cube.setPosition(x, y, z);
            gameItems[i] = cube;
        }
        
          //Doesn't work on Mac for some reason
//        ConfigSetupGUI configSetup = new ConfigSetupGUI();
//        AutopilotConfig config = configSetup.showDialog();

        AutopilotConfig config = new AutopilotConfig() {
            public float getGravity() {return 10f;}
            public float getWingX() {return 2.5f;}
            public float getTailSize() {return 5f;}
            public float getEngineMass() {return 70f;}
            public float getWingMass() {return 25f;}
            public float getTailMass() {return 30f;}
            public float getMaxThrust() {return 5000f;}
            public float getMaxAOA() {return -1f;}
            public float getWingLiftSlope() {return 0.11f;}
            public float getHorStabLiftSlope() {return 0.11f;}
            public float getVerStabLiftSlope() {return 0.11f;}
            public float getHorizontalAngleOfView() {return (float) Math.toRadians(120f);}
            public float getVerticalAngleOfView() {return (float) Math.toRadians(120f);}
            public int getNbColumns() {return 200;}
            public int getNbRows() {return 200;}};


        physicsEngine = new PhysicsEngine(config);
        renderer = new Renderer(config);
        renderer.init(window);
        
        imageCreator = new ImageCreator(config.getNbColumns(), config.getNbRows());

        drone = new Drone(config);
        drone.setThrust(200f);
        drone.setLeftWingInclination((float)Math.toRadians(0));


        DroneMesh droneMesh = new DroneMesh(drone);
        GameItem left = new GameItem(droneMesh.getLeft());
        GameItem right = new GameItem(droneMesh.getRight());
        GameItem body = new GameItem(droneMesh.getBody());

        droneItems = new GameItem[]{left, right, body};
        
    }

    /**
     * Handle input, should be in seperate class
     */
    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        int mult = 1;
        if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            mult = 5;
        }
        if (window.isKeyPressed(GLFW_KEY_C)) {
            imageCreator.screenShot();
        }
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1 * mult;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1 * mult;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = 1 * mult;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = -1 * mult;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1 * mult;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1 * mult;
        }
    }

    /**
     * Handle the game objects internally
     */
    @Override
    public void update(float interval, MouseInput mouseInput) {
    	physicsEngine.update(interval, drone);
    	
    	Vector3f newDronePos = new Vector3f((float)drone.getPosition().get(0), (float)drone.getPosition().get(1), (float)drone.getPosition().get(2));

    	// Update camera position
        freeCamera.movePosition(cameraInc.x * Constants.CAMERA_POS_STEP, cameraInc.y * Constants.CAMERA_POS_STEP, cameraInc.z * Constants.CAMERA_POS_STEP);
        droneCamera.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);
        droneCamera.setRotation(-(float)Math.toDegrees(drone.getPitch()),-(float)Math.toDegrees(drone.getYaw()),-(float)Math.toDegrees(drone.getRoll()));

        // Update the position of each drone item
        for (GameItem droneItem : droneItems) {
            droneItem.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);

            droneItem.setRotation(-(float)Math.toDegrees(drone.getPitch()),-(float)Math.toDegrees(drone.getYaw()),-(float)Math.toDegrees(drone.getRoll()));
        }

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            freeCamera.moveRotation(rotVec.x * Constants.MOUSE_SENSITIVITY, rotVec.y * Constants.MOUSE_SENSITIVITY, 0);
        }
    }

    /**
     * Draw to the screen
     */
    @Override
    public void render(Window window) {
    	renderer.render(window, freeCamera, droneCamera, gameItems, droneItems);
    }

    /**
     * Delete VBO VAO
     */
    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }
}
