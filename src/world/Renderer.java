package world;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import engine.Window;
import engine.graph.Mesh;
import engine.graph.ShaderProgram;
import org.joml.Matrix4f;
import utils.Constants;
import utils.Utils;

public class Renderer {

    private Matrix4f projectionMatrix;

    /**
     * Holds the ShaderProgram
     */
    private ShaderProgram shaderProgram;

    public Renderer() {
    }

    public void init(Window window) throws Exception {
        shaderProgram = new ShaderProgram();

        //can be used to modify properties of the vertex such as position, color, and texture coordinates.
        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
        //is used for calculating individual fragment colors.
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));

        shaderProgram.link();

        // Create projection matrix, removed all rest working with uniforms
        float aspectRatio = (float) window.getWidth() / window.getHeight();
        projectionMatrix = new Matrix4f().perspective(Constants.FOV, aspectRatio, Constants.Z_NEAR, Constants.Z_FAR);
        shaderProgram.createUniform("projectionMatrix");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Mesh mesh) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Draw the mesh
        glBindVertexArray(mesh.getVaoId());
        //Enable both attributes
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        //Disable both attributes
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}
