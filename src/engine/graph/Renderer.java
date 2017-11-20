package engine.graph;

import static org.lwjgl.opengl.GL11.*;

import engine.Window;
import org.joml.Matrix4f;

import utils.*;
import entities.WorldObject;
import interfaces.AutopilotConfig;

public class Renderer {

    private final Transformation transformation;

    
    private final int droneCamWidth, droneCamHeight;
    private final float droneCamFOV;
    /**
     * Holds the ShaderProgram
     */
    private ShaderProgram shaderProgram;
    
    /**
     * Holds the basic functionalities for the
     */
    public Renderer(AutopilotConfig config) {
        this.transformation = new Transformation();
        this.droneCamWidth = config.getNbColumns();
        this.droneCamHeight = config.getNbRows();
        this.droneCamFOV = config.getHorizontalAngleOfView();
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

    public void clear(Window window) {
    	glEnable(GL_SCISSOR_TEST);
    	
    	
    	// background for droneCam
    	glScissor(0, 0, droneCamWidth, droneCamHeight);
    	glClearColor(1f, 1f, 1f, 0f);
    	
    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    	
    	// background for free camera
    	glScissor(droneCamWidth, 0, window.getWidth(), window.getHeight());
    	glClearColor(.41f, .4f, .4f, 1f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


        // background for info
        glScissor(0, droneCamHeight,droneCamWidth,(window.getHeight()));
        glClearColor(.81f, .8f, .8f, 1f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glDisable(GL_SCISSOR_TEST);
    }

    /**
     * Render all the gameObjects
     * @param window
     *        The window in which to render
     * @param freeCamera
     * 		  The free moving camera
     * @param droneCamera
     * 		  The camera attached to the drone
     * @param gameItems
     *        All game items that need rendering that are not a part of the drone
     * @param droneItems
     * 		  The game items that are part of the drone
     * 		  (wich will not be rendered on the droneCam view)
     */
    public void render(Window window, Camera freeCamera, Camera droneCamera, WorldObject[] gameItems, WorldObject[] droneItems) {
        clear(window);


        // The free camera window
        glViewport(droneCamWidth, 0, window.getWidth(), window.getHeight());
        
        shaderProgram.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(Constants.FOV, (window.getWidth()) - droneCamWidth, window.getHeight(), Constants.Z_NEAR, Constants.Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(freeCamera);

        // Render each gameItem
        for(WorldObject gameItem : gameItems) {
            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mesh for this game item
            gameItem.getMesh().render();
        }
        
        // Render each droneItem
        for(WorldObject droneItem: droneItems) {
        	// Set model view matrix for this item
        	Matrix4f modelViewMatrix = transformation.getModelViewMatrix(droneItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mesh for this game item
            droneItem.getMesh().render();
		}

        shaderProgram.unbind();
        
        
        
        // The droneCam window
        glViewport(0,0,droneCamWidth,droneCamHeight);
                
        shaderProgram.bind();

        // Update projection Matrix
        projectionMatrix = transformation.getProjectionMatrix(droneCamFOV, droneCamWidth, droneCamHeight, Constants.Z_NEAR, Constants.Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        viewMatrix = transformation.getViewMatrix(droneCamera);

        // Render each gameItem
        for (WorldObject gameItem : gameItems) {
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
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
