package engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import entities.WorldObject;

public class Transformation {

    private final Matrix4f projectionMatrix;

    private final Matrix4f modelViewMatrix;

    private final Matrix4f viewMatrix;
    /**
     * Define all the transformations to be applied to GameObjects
     */
    public Transformation() {
        projectionMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
    }

    /**
     * Returns a correctly set projection matrix. This will make objects that
     * are further to appear a lot smaller. Also height and Z-value clashing
     * will be fixed due to the zNear and zFar values. (frustum)
     * We add this as a uniform to the vertexShader so that we can further use the advantages
     * given by the GPU. As this matrix wont change often the performance will get a huge boost.
     */
    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    /**
     * This returns the view matrix of the camera. This will apply transformations to the game world/ game objects
     * so that they are viewed properly through the camera perspective. (as we can't move the camera itself around)
     * @param camera
     *        The camera we are using
     * @return
     *        The new viewMatrix to which potential new camera changes have been applied.
     */
    public Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();

        viewMatrix.identity();
        // First do the rotation so camera rotates over to its position ORDER MUST BE Z Y X
        viewMatrix.rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
                .rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
//        viewMatrix.rotateXYZ((float)Math.toRadians(rotation.x),(float)Math.toRadians(rotation.y),(float)Math.toRadians(rotation.z));
        // Then do the translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

    /**
     * As we're changing the position of the objects depening on our camera location we need a common
     * world space to place our object references in. The modelViewMatrix merges the view matrix and the
     * model's matrix so we can see the objects are kept up to date with the camera 'movement' and the
     * object's own rotation and movement.
     * @param gameItem
     *        The item we're updating
     * @param viewMatrix
     *        The view matrix (takes note of the current camera angles)
     * @return
     *        The modelViewMatrix
     */
    public Matrix4f getModelViewMatrix(WorldObject gameItem, Matrix4f viewMatrix) {
        Vector3f rotation = gameItem.getRotation();
        modelViewMatrix.identity().translate(gameItem.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameItem.getScale());
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(modelViewMatrix);
    }
}
