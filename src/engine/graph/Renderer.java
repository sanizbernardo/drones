package engine.graph;

import static org.lwjgl.opengl.GL11.*;

import engine.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import datatypes.AutopilotConfig;
import utils.*;
import entities.WorldObject;
import sun.awt.datatransfer.DataTransferer.CharsetComparator;

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
    	glScissor(droneCamX, droneCamY, droneCamWidth, droneCamHeight);
    	glClearColor(1f, 1f, 1f, 0f);
    	
    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    	
    	// background for free camera
    	glScissor(freeCamX, freeCamY, freeCamWidth, freeCamHeigth);
    	glClearColor(.41f, .4f, .4f, 1f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


        // background for info
        glScissor(0, droneCamHeight,droneCamWidth,(window.getHeight()));
        glClearColor(.81f, .8f, .8f, 1f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        
        // background for chase cam
        glScissor(chaseCamX,chaseCamY,chaseCamWidth,chaseCamHeigth);
        glClearColor(.51f, .51f, .51f, 1f);
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        
        // background for top ortho cam
        glScissor(topOrthoCamX,topOrthoCamY,topOrthoCamWidth,topOrthoCamHeigth );
        glClearColor(.30f, .30f, .30f, 1f);
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        //end
        glDisable(GL_SCISSOR_TEST);
    }

    private int freeCamX, freeCamY, freeCamWidth, freeCamHeigth;
    private int chaseCamX, chaseCamY, chaseCamWidth, chaseCamHeigth;
    private int droneCamX, droneCamY;
    private int topOrthoCamX, topOrthoCamY, topOrthoCamWidth, topOrthoCamHeigth;

    public void render(Window window, Camera freeCamera, Camera droneCamera, Camera chaseCamera, Camera topOrthoCamera, WorldObject[] gameItems, WorldObject[] droneItems) {
        clear(window);


        // The free camera window
        
        freeCamX = droneCamWidth;
        freeCamY = (int) (droneCamWidth * 1.25);
        freeCamWidth = window.getWidth();
        freeCamHeigth = window.getHeight();
        glViewport(freeCamX, freeCamY, freeCamWidth ,freeCamHeigth);
        
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
        droneCamX = 0;
        droneCamY = 0;
        glViewport(droneCamX,droneCamY,droneCamWidth,droneCamHeight);
                
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
        
        
        
        
        
        // The Chase camera
        chaseCamWidth = droneCamWidth + 100;
        chaseCamHeigth = (int) (droneCamWidth * 1.25);
        chaseCamX = droneCamWidth;
        chaseCamY = 0;
        glViewport(chaseCamX,chaseCamY,chaseCamWidth, chaseCamHeigth);
        
        shaderProgram.bind();

        // Update projection Matrix
        projectionMatrix = transformation.getProjectionMatrix((float) Math.toRadians(60), chaseCamWidth, chaseCamHeigth, Constants.Z_NEAR, Constants.Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);
        
        // Update view Matrix
        viewMatrix = transformation.getViewMatrixY(chaseCamera);

        // Render each gameItem
        for (WorldObject gameItem : gameItems) {
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
        
        
        
        
        
        //Top ortho cam
        topOrthoCamX = chaseCamX + chaseCamWidth;
        topOrthoCamY = 0;
        topOrthoCamWidth = droneCamWidth + 100;
        topOrthoCamHeigth = (int) (droneCamWidth * 1.25);
        glViewport(topOrthoCamX, topOrthoCamY, topOrthoCamWidth, topOrthoCamHeigth);
        
        shaderProgram.bind();

        // Update projection Matrix
        int size = 50;
        projectionMatrix = projectionMatrix.identity().ortho(-size, size,-size, size, Constants.Z_NEAR, Constants.Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);
        
        // Update view Matrix
        viewMatrix = transformation.getViewMatrix(topOrthoCamera);

        // Render each gameItem
        for (WorldObject gameItem : gameItems) {
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
