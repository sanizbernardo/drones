package engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

    private final Matrix4f projectionMatrix;

    private final Matrix4f worldMatrix;

    /**
     * Define all the transformations to be applied to GameObjects
     */
    public Transformation() {
        worldMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
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
     * The world space provides a common point of reference for all the game objects to make
     * the internal representation a lot easier. All vertices must be transformed from the model
     * space to the world space.
     * @param offset
     *        The object's axis's offset
     * @param rotation
     *        The object's axis's rotation
     * @param scale
     *        Scaling factor of the object's model in the world view
     * @return
     *        The world matrix
     */
    public Matrix4f getWorldMatrix(Vector3f offset, Vector3f rotation, float scale) {
        worldMatrix.identity().translate(offset).
                rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).
                scale(scale);
        return worldMatrix;
    }
}
