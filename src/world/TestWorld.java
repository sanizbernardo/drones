package world;

import IO.MouseInput;
import engine.IWorldRules;
import engine.Window;
import engine.graph.Camera;
import engine.graph.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import utils.Constants;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

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
    private final Renderer renderer;

    /**
     * A camera that defines what we see. An OpenGL camera cannot move on its own, so we move
     * the world itself around. When the camera "goes up" we just shift all world objects
     * downward. Same goes for all other translations and rotations.
     */
    private final Camera camera;

    /**
     * A list of all the GameItems.
     */
    private GameItem[] gameItems;

    /**
     * Create the renderer of this world
     */
    public TestWorld() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }
    
    /**
     * Init
     */
    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        Cube redCube = new Cube();

        GameItem cube1= new GameItem(redCube.getMesh());
        cube1.setScale(0.5f);
        cube1.setPosition(0, 0, -2);

        GameItem cube2 = new GameItem(redCube.getMesh());
        cube2.setScale(0.5f);
        cube2.setPosition(0, 0, -10);

        gameItems = new GameItem[]{cube1, cube2};
    }
    

    /**
     * Handle input, should be in seperate class
     */
    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
    }

    /**
     * Handle the game objects internally
     */
    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * Constants.CAMERA_POS_STEP, cameraInc.y * Constants.CAMERA_POS_STEP, cameraInc.z * Constants.CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * Constants.MOUSE_SENSITIVITY, rotVec.y * Constants.MOUSE_SENSITIVITY, 0);
        }
    }

    /**
     * Draw to the screen
     */
    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems);
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
