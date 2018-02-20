package engine.graph;

import static org.lwjgl.opengl.GL11.*;
import engine.Window;
import meshes.Mesh;

import org.joml.Matrix4f;

import utils.*;
import world.helpers.CameraHelper;
import entities.WorldObject;
import entities.ground.Ground;
import interfaces.AutopilotConfig;

import java.util.ArrayList;

public class Renderer {

    private final Transformation transformation;

    
    private final int droneCamWidth, droneCamHeight;
    private final float droneCamFOV;
   
    
    private int freeCamX, freeCamY, freeCamWidth, freeCamHeigth;
    private int chaseCamX, chaseCamY, chaseCamWidth, chaseCamHeigth;
    private int droneCamX, droneCamY;
    private int topOrthoCamX, topOrthoCamY, topOrthoCamWidth, topOrthoCamHeigth;
    private int rightOrthoCamX, rightOrthoCamY, rightOrthoCamWidth, rightOrthoCamHeigth;

    private boolean ortho = false;
    

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

    public void toggleOrtho() {
    	this.ortho = !ortho;
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
    	
    	// clear all just to be sure
    	glScissor(0,0,window.getWidth(), window.getHeight());
    	glClearColor(0f, 0f, 0f, 1f);
    	
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    	
        
        // background for chase cam
        glScissor(chaseCamX,chaseCamY,chaseCamWidth,chaseCamHeigth);
        glClearColor(.51f, .51f, .51f, 1f);
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
    	// background for droneCam
    	glScissor(droneCamX, droneCamY, droneCamWidth, droneCamHeight);
    	glClearColor(1f, 1f, 1f, 0f);
    	
    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    	
    	if(!ortho) {
        	// background for free camera
        	glScissor(freeCamX, freeCamY, freeCamWidth, freeCamHeigth);
        	glClearColor(.41f, .4f, .4f, 1f);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    	} else {
            // background for top ortho cam
            glScissor(topOrthoCamX,topOrthoCamY,topOrthoCamWidth,topOrthoCamHeigth );
            glClearColor(.30f, .30f, .30f, 1f);
            
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            
            // background for right ortho cam
            glScissor(rightOrthoCamX, rightOrthoCamY, rightOrthoCamWidth, rightOrthoCamHeigth);
            glClearColor(.50f, .50f, .50f, 1f);
            
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            //end
    	}
    	
        glDisable(GL_SCISSOR_TEST);
    }

    public void render(Window window, CameraHelper cameraHelper, WorldObject[] gameItems, WorldObject[] droneItems, ArrayList<WorldObject> pathObjects, Ground ground) {
		clear(window);
		

		Matrix4f projectionMatrix;
		Matrix4f viewMatrix;
        
        projectionMatrix = chaseCam(window, cameraHelper, gameItems, droneItems);
		
        droneCam(cameraHelper, gameItems);
		
        if(!ortho) {
            freeCam(window, cameraHelper, gameItems, droneItems, pathObjects, ground);
        } else {
        	int size = 40;
        	
            projectionMatrix = topOrthoCam(window, cameraHelper, gameItems,
					droneItems, pathObjects, projectionMatrix, size);
            
            rightOrthCam(window, cameraHelper, gameItems, droneItems,
					pathObjects, projectionMatrix, size);
        }
    }

	private Matrix4f chaseCam(Window window, CameraHelper cameraHelper,
			WorldObject[] gameItems, WorldObject[] droneItems) {
		Matrix4f projectionMatrix;
		Matrix4f viewMatrix;
		shaderProgram.bind();
		chaseCamWidth =  (int) (window.getWidth() * 0.25);    chaseCamHeigth = (int) (window.getHeight() * 0.5);
		chaseCamX = 0;              chaseCamY = (int) (window.getHeight() * 0.5);
		glViewport(chaseCamX,chaseCamY,chaseCamWidth, chaseCamHeigth);
		  
		//Update projection Matrix
		projectionMatrix = transformation.getProjectionMatrix((float) Math.toRadians(90), chaseCamWidth, chaseCamHeigth, Constants.Z_NEAR, Constants.Z_FAR);
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);
		  
		//Update view Matrix
		viewMatrix = transformation.getViewMatrix(cameraHelper.chaseCamera);
		
		renderWorldItems(gameItems, viewMatrix);
		renderDroneItems(droneItems, viewMatrix, 1);
        shaderProgram.unbind();
		return projectionMatrix;
	}

	private Matrix4f droneCam(CameraHelper cameraHelper, WorldObject[] gameItems) {
		Matrix4f projectionMatrix;
		Matrix4f viewMatrix;
		shaderProgram.bind();
		droneCamX = (int) ((chaseCamWidth - droneCamWidth) / 2);
		droneCamY = (int) ((chaseCamHeigth - droneCamHeight) / 2);
		glViewport(droneCamX,droneCamY,droneCamWidth,droneCamHeight);
		          
		// Update projection Matrix
		projectionMatrix = transformation.getProjectionMatrix(droneCamFOV, droneCamWidth, droneCamHeight, Constants.Z_NEAR, Constants.Z_FAR);
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);
		
		// Update view Matrix
		viewMatrix = transformation.getViewMatrix(cameraHelper.droneCamera);
		
		renderWorldItems(gameItems, viewMatrix);
        shaderProgram.unbind();
		return projectionMatrix;
	}

	private void freeCam(Window window, CameraHelper cameraHelper,
			WorldObject[] gameItems, WorldObject[] droneItems,
			ArrayList<WorldObject> pathObjects, Ground ground) {
		Matrix4f projectionMatrix;
		Matrix4f viewMatrix;
		shaderProgram.bind();
		freeCamX = chaseCamWidth;            freeCamY = 0;
		freeCamWidth = window.getWidth() - chaseCamWidth ;    freeCamHeigth = window.getHeight();
		glViewport(freeCamX, freeCamY, freeCamWidth ,freeCamHeigth);


		// Update projection Matrix
		projectionMatrix = transformation.getProjectionMatrix(Constants.FOV, (window.getWidth()) - droneCamWidth, window.getHeight(), Constants.Z_NEAR, Constants.Z_FAR);
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);

		// Update view Matrix
		viewMatrix = transformation.getViewMatrix(cameraHelper.freeCamera);

		renderGround(ground, viewMatrix);
		renderTrail(pathObjects, viewMatrix);
		renderWorldItems(gameItems, viewMatrix);
		renderDroneItems(droneItems, viewMatrix, 1);
		shaderProgram.unbind();
	}

	
	private Matrix4f topOrthoCam(Window window, CameraHelper cameraHelper,
			WorldObject[] gameItems, WorldObject[] droneItems,
			ArrayList<WorldObject> pathObjects, Matrix4f projectionMatrix,
			int size) {
		Matrix4f viewMatrix;
		shaderProgram.bind();
		topOrthoCamX = chaseCamWidth;     
		topOrthoCamY = chaseCamY;
		topOrthoCamWidth = window.getWidth() - chaseCamWidth;       
		topOrthoCamHeigth = (int) (window.getHeight() * 0.5);
		glViewport(topOrthoCamX, topOrthoCamY, topOrthoCamWidth, topOrthoCamHeigth);
		// Update projection Matrix
		
        cameraHelper.updateTopCam(droneItems[Constants.DRONE_BODY].getPosition());

		projectionMatrix = projectionMatrix.identity().ortho(-size * 4 / 3,size * 4 / 3,-size/2,size/2, Constants.Z_NEAR, Constants.Z_FAR).rotateZ(FloatMath.toRadians(-90));
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);
		
		// Update view Matrix
		viewMatrix = transformation.getViewMatrix(cameraHelper.topOrthoCamera);

		renderTrail(pathObjects, viewMatrix);
		renderWorldItems(gameItems, viewMatrix);
		renderDroneItems(droneItems, viewMatrix, 1);
		shaderProgram.unbind();
		return projectionMatrix;
	}
	
	private void rightOrthCam(Window window, CameraHelper cameraHelper,
			WorldObject[] gameItems, WorldObject[] droneItems,
			ArrayList<WorldObject> pathObjects, Matrix4f projectionMatrix,
			int size) {
		Matrix4f viewMatrix;
		shaderProgram.bind();

		rightOrthoCamX = chaseCamWidth;  
		rightOrthoCamY = 0;
		rightOrthoCamWidth = window.getWidth() - chaseCamWidth;       				
		rightOrthoCamHeigth = (int) (window.getHeight() * 0.5);
		glViewport(rightOrthoCamX, rightOrthoCamY, rightOrthoCamWidth, rightOrthoCamHeigth);
		// Update projection Matrix
		
		cameraHelper.updateRightCam(droneItems[Constants.DRONE_BODY].getPosition());
		
		projectionMatrix = projectionMatrix.identity().ortho(-size * 4 / 3,size * 4 / 3,-size/4,size/4, Constants.Z_NEAR, Constants.Z_FAR);
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);
		
		// Update view Matrix
		viewMatrix = transformation.getViewMatrix(cameraHelper.rightOrthoCamera);

		renderTrail(pathObjects, viewMatrix);
		renderWorldItems(gameItems, viewMatrix);
		renderDroneItems(droneItems, viewMatrix, 1);
		shaderProgram.unbind();
	}

    private void renderTrail(ArrayList<WorldObject> trailItems, Matrix4f viewMatrix) {
        if(trailItems.isEmpty()) return;
        for (WorldObject gameItem : trailItems) {
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mesh for this game item
            gameItem.getMesh().render();
        }
    }

    private void renderGround(Ground ground, Matrix4f viewMatrix) {
    	if(ground == null || ground.getTiles().isEmpty()) return;
    	for(WorldObject tile : ground.getTiles()) {
    		Matrix4f modelViewMatrix = transformation.getModelViewMatrix(tile, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
    		tile.getMesh().render();
    	}
    }
    
    private void renderWorldItems(WorldObject[] gameItems, Matrix4f viewMatrix) {
        for (WorldObject gameItem : gameItems) {
            if (gameItem == null) continue;
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mesh for this game item
            gameItem.getMesh().render();
        }
    }
    
    private void renderDroneItems(WorldObject[] droneItems, Matrix4f viewMatrix, int size) {
        for(WorldObject droneItem: droneItems) {
            droneItem.setScale(size);
        	// Set model view matrix for this item
        	Matrix4f modelViewMatrix = transformation.getModelViewMatrix(droneItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mesh for this game item
            droneItem.getMesh().render();
            droneItem.setScale(1);
		}
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
