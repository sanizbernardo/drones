package world;

import engine.IWorldRules;
import engine.Window;
import engine.graph.Mesh;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

public class TestWorld implements IWorldRules {

    private int direction = 0;
    
    private float color = 0.0f;

    private final Renderer renderer;

    private Mesh mesh;

    /**
     * Create the renderer of this world
     */
    public TestWorld() {
        renderer = new Renderer();
    }
    
    @Override
    /**
     * Init
     */
    public void init(Window window) throws Exception {
        renderer.init(window);
        float[] positions = new float[]{
            -0.5f,  0.5f, -1.05f,
            -0.5f, -0.5f, -1.05f,
             0.5f, -0.5f, -1.05f,
             0.5f,  0.5f, -1.05f,
        };
        float[] colours = new float[]{
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
        };
        int[] indices = new int[]{
            0, 1, 3, 3, 1, 2,
        };
        mesh = new Mesh(positions, colours, indices);
    }
    
    @Override
    /**
     * Handle input, should be in seperate class
     */
    public void input(Window window) {
        if ( window.isKeyPressed(GLFW_KEY_UP) ) {
            direction = 1;
        } else if ( window.isKeyPressed(GLFW_KEY_DOWN) ) {
            direction = -1;
        } else {
            direction = 0;
        }
    }

    @Override
    /**
     * Handle the game objects internally
     */
    public void update(float interval) {
        color += direction * 0.01f;
        if (color > 1) {
            color = 1.0f;
        } else if ( color < 0 ) {
            color = 0.0f;
        }
    }

    @Override
    /**
     * Draw to the screen
     */
    public void render(Window window) {
        window.setClearColor(color, color, color, 0.0f);
        renderer.render(window, mesh);
    }

    /**
     * Delete VBO VAO
     */
    @Override
    public void cleanup() {
        renderer.cleanup();
        mesh.cleanUp();
    }
}
