package world.helpers;

import engine.graph.Camera;

import org.joml.Vector3f;

import utils.FloatMath;

public class CameraHelper {

    private final Vector3f cameraInc;
    public Camera freeCamera, droneCamera, chaseCamera, topOrthoCamera, rightOrthoCamera;

    public CameraHelper() {
        constructCameras();
        this.cameraInc = new Vector3f(0, 0, 0);
    }

    private void constructCameras() {
        this.freeCamera = new Camera();
        this.droneCamera = new Camera();
        this.chaseCamera = new Camera();

        this.topOrthoCamera = new Camera();
        topOrthoCamera.setPosition(0,200,0);
        topOrthoCamera.setRotation( FloatMath.toRadians(90), 0, 0);
        this.rightOrthoCamera = new Camera();
        rightOrthoCamera.setPosition(200, 0, 0);
        rightOrthoCamera.setRotation(0, FloatMath.toRadians(-90), 0);
    }

    public void updateTopCam(Vector3f pos) {
    	topOrthoCamera.setPosition(pos.x, 200, pos.z);
    }
    
    public void updateRightCam(Vector3f pos) {
    	rightOrthoCamera.setPosition(200, pos.y, pos.z);
    }
    
    public Vector3f getCameraInc() {
        return cameraInc;
    }
}
