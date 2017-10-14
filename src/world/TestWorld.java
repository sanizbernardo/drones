package world;

import engine.IWorldRules;
import engine.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

public class TestWorld implements IWorldRules {

    private int direction = 0;
    
    private float color = 0.0f;

    private final Renderer renderer;

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
    public void init() throws Exception {
        renderer.init();
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
        renderer.render(window);
    }

    /**
     * Delete VBO VAO
     */
    @Override
    public void cleanup() {
        renderer.cleanup();
    }
}
