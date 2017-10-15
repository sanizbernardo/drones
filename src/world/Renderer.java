package world;

import static org.lwjgl.opengl.GL11.*;

import engine.Window;
import engine.graph.ShaderProgram;
import engine.graph.Transformation;
import org.joml.Matrix4f;
import utils.Constants;
import utils.Utils;

public class Renderer {

    private Matrix4f projectionMatrix;


    private final Transformation transformation;

    /**
     * Holds the ShaderProgram
     */
    private ShaderProgram shaderProgram;

    public Renderer() {
        transformation = new Transformation();
    }

    public void init(Window window) throws Exception {
        shaderProgram = new ShaderProgram();

        //can be used to modify properties of the vertex such as position, color, and texture coordinates.
        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
        //is used for calculating individual fragment colors.
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
        shaderProgram.link();

        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("worldMatrix");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Render all the gameObjects
     * @param window
     *        The window in which to render
     * @param gameItems
     *        All game items that need rendering
     */
    public void render(Window window, GameItem[] gameItems) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(Constants.FOV, window.getWidth(), window.getHeight(), Constants.Z_NEAR, Constants.Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Render each gameItem
        for(GameItem gameItem : gameItems) {
            // Set world matrix for this item
            Matrix4f worldMatrix = transformation.getWorldMatrix(
                    gameItem.getPosition(),
                    gameItem.getRotation(),
                    gameItem.getScale());
            shaderProgram.setUniform("worldMatrix", worldMatrix);
            // Render the mesh for this game item
            gameItem.getMesh().render();
        }

        shaderProgram.unbind();
    }

    /**
     * Remove the shader program
     */
    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}
