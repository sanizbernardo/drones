package recognition;

import java.util.ArrayList;

public class Cube {

    public Cube(float hValue){
        this.hValue = hValue;
    }

    private int[] XYZ = null;
    private float hValue;
    private ArrayList<int[]> pixels = new ArrayList<>();

    //method for calculating best location approximation
    //TODO
    public void setNewXYZ(int[] newXYZ){
        if (! (this.XYZ == null)){
            setXYZ((this.getX()+newXYZ[0])/2, (this.getY()+newXYZ[1])/2, (this.getZ()+newXYZ[2])/2);
        }else{
            setXYZ(newXYZ[0], newXYZ[1], newXYZ[2]);
        }
    }

    private void setXYZ(int x, int y, int z){
        this.XYZ[0] = x;
        this.XYZ[1] = y;
        this.XYZ[2] = z;
    }

    //setter
    public void addPixel(int[] pixel){
        pixels.add(pixel);
    }


    //getters
    public int getX(){
        return XYZ[0];
    }
    public int getY(){
        return XYZ[1];
    }
    public int getZ(){
        return XYZ[2];
    }
    public float gethValue(){
        return  this.hValue;
    }
    public int getNbPixels(){
        return pixels.size();
    }
    public int[] getAveragePixel(){
        int x = 0;
        int y = 0;
        int counter = 0;
        for (int[] pixel: pixels){
            x += pixel[0];
            y += pixel[1];
            counter += 1;
        }
        int[] midPixel = {x/counter, y/counter};
        return midPixel;
    }
    public int getHeight(){
        int maxY = -1; int minY = Integer.MAX_VALUE;
        for (int[] pixel: pixels){
            if(pixel[1] > maxY)
                maxY = pixel[1];
            else if (pixel[1] < minY)
                minY = pixel[1];
        }
        return maxY - minY;
    }
    public int[] getCubeData(){
        int[] output = {getAveragePixel()[0], getAveragePixel()[1], getNbPixels(), getHeight()};
        return output;
    }
    public ArrayList<int[]> getPixels(){
        return this.pixels;
    }

}
