package utils.IO;

import testbed.engine.Window;
import testbed.graphics.Renderer;

import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

public class KeyboardInput {
	
	private long old = 0;
	
    public void worldInput(Vector3f cameraInc, Window window, Renderer renderer) {
        cameraInc.set(0, 0, 0);
        int mult = 1;
        long now = System.currentTimeMillis();
        if(window.isKeyPressed(GLFW_KEY_R) && now-old >= 250) {
        	renderer.toggleOrtho();
        	old = System.currentTimeMillis();
        }
        if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            mult = 20;
        }
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -mult;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = mult;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -mult;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = mult;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -mult;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = mult;
        }
    }

}
