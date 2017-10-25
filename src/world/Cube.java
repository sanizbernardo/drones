package world;

import engine.graph.Mesh;

public class Cube {


    private float[] positions;
    private float[] colours;
    private int[] indices;
    private Mesh mesh;

    public Cube(){
        setPositions();
        setColours();
        setIndices();
        this.mesh = new Mesh(this.getPositions(), this.getColours(), this.getIndices());
    }

    private void setPositions() {
        this.positions = new float[]{
            //Face 1 (front)
            -0.5f, 0.5f, 0.5f, //V0 #0
            -0.5f, -0.5f, 0.5f, //V1 #1
            0.5f, -0.5f, 0.5f, //V2 #2
            0.5f, 0.5f, 0.5f, //V3 #3

            //Face 2 (top)
            -0.5f, 0.5f, 0.5f, //V0 #4
            0.5f, 0.5f, 0.5f, //V3 #5
            -0.5f, 0.5f, -0.5f, //V4 #6
            0.5f, 0.5f, -0.5f, //V5 #7

            //Face 3 (back)
            -0.5f, 0.5f, -0.5f, //V4 #8
            0.5f, 0.5f, -0.5f, //V5 #9
            -0.5f, -0.5f, -0.5f, //V6 #10
            0.5f, -0.5f, -0.5f, //V7 #11

            //Face 4 (bottom)
            -0.5f, -0.5f, 0.5f, //V1 #12
            -0.5f, -0.5f, -0.5f, //V6 #13
            0.5f, -0.5f, -0.5f, //V7 #14
            0.5f, -0.5f, 0.5f, //V2 #15

            //Face 5 (left)
            -0.5f, 0.5f, 0.5f, //V0 #16
            -0.5f, 0.5f, -0.5f, //V4 #17
            -0.5f, -0.5f, 0.5f, //V1 #18
            -0.5f, -0.5f, -0.5f, //V6 #19

            //Face 6 (right)
            0.5f, 0.5f, 0.5f, //V3 #20
            0.5f, -0.5f, 0.5f, //V2 #21
            0.5f, 0.5f, -0.5f, //V5 #22
            0.5f, -0.5f, -0.5f, //V7 #23
        };
    }

    public void setColours() {
        this.colours = new float[]{
            //Face 1 (front) pos Z
            0.7f, 0.0f, 0.0f,
            0.7f, 0.0f, 0.0f,
            0.7f, 0.0f, 0.0f,
            0.7f, 0.0f, 0.0f,

            //Face 2 (top) pos Y
            1f,0f,0f,
            1f,0f,0f,
            1f,0f,0f,
            1f,0f,0f,

            //Face 3 (back) neg Z
            0.45f, 0.0f, 0.0f,
            0.45f, 0.0f, 0.0f,
            0.45f, 0.0f, 0.0f,
            0.45f, 0.0f, 0.0f,

            //Face 4 (bottom) neg Y
            0.15f, 0.0f, 0.0f,
            0.15f, 0.0f, 0.0f,
            0.15f, 0.0f, 0.0f,
            0.15f, 0.0f, 0.0f,


            //Face 5 (left) neg X
            0.3f, 0.0f, 0.0f,
            0.3f, 0.0f, 0.0f,
            0.3f, 0.0f, 0.0f,
            0.3f, 0.0f, 0.0f,

            //Face 6 (right) pos X
            0.854f, 0.0f, 0.0f,
            0.854f, 0.0f, 0.0f,
            0.854f, 0.0f, 0.0f,
            0.854f, 0.0f, 0.0f,
        };
    }

    private void setIndices(){
        this.indices = new int[]{
            // Front face
            1, 0, 2, 3, 2, 0,
            // Top face
            6, 7, 4, 5, 4, 7,
            // Back face
            8, 9, 10, 11, 10, 9,
            // Bottom face
            12, 13, 15, 14, 15, 13,
            // Left face
            16, 17, 18, 19, 18, 17,
            // Right face
            20, 22, 21, 23, 21, 22,
        };
    }

    public Mesh getMesh() {
        return mesh;
    }

    public float[] getPositions() {
        return positions;
    }

    public float[] getColours() {
        return colours;
    }

    public int[] getIndices() {
        return indices;
    }
}

/*
// V0
-0.5f, 0.5f, 0.5f,
// V1
-0.5f, -0.5f, 0.5f,
// V2
0.5f, -0.5f, 0.5f,
// V3
0.5f, 0.5f, 0.5f,
// V4
-0.5f, 0.5f, -0.5f,
// V5
0.5f, 0.5f, -0.5f,
// V6
-0.5f, -0.5f, -0.5f,
// V7
0.5f, -0.5f, -0.5f
*/