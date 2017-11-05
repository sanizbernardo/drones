package utils.IO;

import engine.Window;
import org.joml.Vector3f;
import utils.image.ImageCreator;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

public class KeyboardInput {

    public void worldInput(Vector3f cameraInc, Window window, ImageCreator imageCreator) {
        cameraInc.set(0, 0, 0);
        int mult = 1;
        if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            mult = 20;
        }
        if (window.isKeyPressed(GLFW_KEY_C)) {
            imageCreator.screenShotExport();
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
