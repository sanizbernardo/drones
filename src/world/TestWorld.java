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
        float[] positions = new float[]{
//                // V0
//                -0.5f, 0.5f, 0.5f,
//                // V1
//                -0.5f, -0.5f, 0.5f,
//                // V2
//                0.5f, -0.5f, 0.5f,
//                // V3
//                0.5f, 0.5f, 0.5f,
//                // V4
//                -0.5f, 0.5f, -0.5f,
//                // V5
//                0.5f, 0.5f, -0.5f,
//                // V6
//                -0.5f, -0.5f, -0.5f,
//                // V7
//                0.5f, -0.5f, -0.5f,

                //Face 1 (front)
                -0.5f, 0.5f, 0.5f, //V0 #0
                -0.5f, -0.5f, 0.5f, //V1 #1
                0.5f, -0.5f, 0.5f, //V2 #2
                0.5f, 0.5f, 0.5f, //V3 #3

                //Face 2 (top)
                -0.5f, 0.5f, 0.5f, //V0 #4
                0.5f, 0.5f, 0.5f, //V3 #5
                -0.5f, 0.5f, -0.5f, //V4 #6
                0.5f, 0.5f, -0.5f, //V5 #7

                //Face 3 (back)
                -0.5f, 0.5f, -0.5f, //V4 #8
                0.5f, 0.5f, -0.5f, //V5 #9
                -0.5f, -0.5f, -0.5f, //V6 #10
                0.5f, -0.5f, -0.5f, //V7 #11

                //Face 4 (bottom)
                -0.5f, -0.5f, 0.5f, //V1 #12
                -0.5f, -0.5f, -0.5f, //V6 #13
                0.5f, -0.5f, -0.5f, //V7 #14
                0.5f, -0.5f, 0.5f, //V2 #15

                //Face 5 (left)
                -0.5f, 0.5f, 0.5f, //V0 #16
                -0.5f, 0.5f, -0.5f, //V4 #17
                -0.5f, -0.5f, 0.5f, //V1 #18
                -0.5f, -0.5f, -0.5f, //V6 #19

                //Face 6 (right)
                0.5f, 0.5f, 0.5f, //V3 #20
                0.5f, -0.5f, 0.5f, //V2 #21
                0.5f, 0.5f, -0.5f, //V5 #22
                0.5f, -0.5f, -0.5f, //V7 #23

        };
        float[] colours = new float[]{
                //Face 1 (front)
                0.15f, 0.0f, 0.0f,
                0.15f, 0.0f, 0.0f,
                0.15f, 0.0f, 0.0f,
                0.15f, 0.0f, 0.0f,


                //Face 2 (top)
                0.3f, 0.0f, 0.0f,
                0.3f, 0.0f, 0.0f,
                0.3f, 0.0f, 0.0f,
                0.3f, 0.0f, 0.0f,

                //Face 3 (back)
                0.45f, 0.0f, 0.0f,
                0.45f, 0.0f, 0.0f,
                0.45f, 0.0f, 0.0f,
                0.45f, 0.0f, 0.0f,

                //Face 4 (bottom)
                0.6f, 0.0f, 0.0f,
                0.6f, 0.0f, 0.0f,
                0.6f, 0.0f, 0.0f,
                0.6f, 0.0f, 0.0f,


                //Face 5 (left)
                0.75f, 0.0f, 0.0f,
                0.75f, 0.0f, 0.0f,
                0.75f, 0.0f, 0.0f,
                0.75f, 0.0f, 0.0f,

                //Face 6 (right)
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
        };
        int[] indices = new int[]{
            // Front face
            1, 0, 2, 3, 2, 0,
            // Top face
            6, 7, 4, 5, 4, 7,
            // Back face
            8, 9, 10, 11, 10, 9,
            // Bottom face
            12, 13, 15, 14, 15, 13,
            // Left face
            16, 17, 18, 19, 18, 17,
            // Right face
            20, 22, 21, 23, 21, 22,
        };
        Mesh mesh = new Mesh(positions, colours, indices);
        GameItem gameItem1 = new GameItem(mesh);
        gameItem1.setScale(0.5f);
        gameItem1.setPosition(0, 0, -2);

        gameItems = new GameItem[]{gameItem1};
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
