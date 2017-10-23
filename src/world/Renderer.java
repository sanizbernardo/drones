package world;

import static org.lwjgl.opengl.GL11.*;

import engine.Window;
import engine.graph.Camera;
import engine.graph.ShaderProgram;
import engine.graph.Transformation;
import org.joml.Matrix4f;
import utils.Constants;
import utils.Utils;

public class Renderer {

    private final Transformation transformation;

    /**
     * Holds the ShaderProgram
     */
    private ShaderProgram shaderProgram;

    /**
     * Holds the basic functionalities for the
     */
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
        shaderProgram.createUniform("modelViewMatrix");
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
    public void render(Window window, Camera freeCamera, Camera droneCamera, GameItem[] gameItems) {
        clear();

//        if (window.isResized()) {
//            glViewport(200, 0, window.getWidth(), window.getHeight());
//            window.setResized(false);
//        }

        int multiplier;
        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
            multiplier = 2;
        } else {
            multiplier = 1;
        }


        glViewport(200 * multiplier, 0, window.getWidth() * multiplier, window.getHeight() * multiplier);
        
        shaderProgram.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(Constants.FOV, window.getWidth() * multiplier, window.getHeight() * multiplier, Constants.Z_NEAR, Constants.Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(freeCamera);

        // Render each gameItem
        for(GameItem gameItem : gameItems) {
            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mes for this game item
            gameItem.getMesh().render();
        }

        shaderProgram.unbind();
        
        
        
        
        glViewport(0,0,200 * multiplier,200 * multiplier);
                
        shaderProgram.bind();

        // Update projection Matrix
        projectionMatrix = transformation.getProjectionMatrix(Constants.FOV, 200 * multiplier , 200 * multiplier, Constants.Z_NEAR, Constants.Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        viewMatrix = transformation.getViewMatrix(droneCamera);

        // Render each gameItem
        for (int i = 0; i < gameItems.length - 3; i++) {
            GameItem gameItem = gameItems[i];
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mes for this game item
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
