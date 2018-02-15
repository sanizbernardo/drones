package world.helpers;

import engine.graph.Camera;
import org.joml.Vector3f;

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
        //topOrthoCamera.setRotation(90, 0, -90);
        topOrthoCamera.setRotation(90, 0, 0);
        this.rightOrthoCamera = new Camera();
        rightOrthoCamera.setPosition(200, 0, 0);
        rightOrthoCamera.setRotation(0, -90, 0);
    }

    public Vector3f getCameraInc() {
        return cameraInc;
    }
}
