package utils;

import java.awt.Color;
import java.io.InputStream;
import java.util.Scanner;

import org.joml.Vector3f;

import interfaces.*;

public class Utils {

    /**
     * Read a file given by filename. It will start searching in the folder marked a resource location!
     * @param fileName
     *        The name of the file (with path of necessary)
     * @return
     *        The content of the file as a String
     * @throws Exception
     *        If something goes wrong
     */
    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Utils.class.getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, "UTF-8")) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    /**
     * Converts a hsv color to rgb
     * @param h
     * 		 hue value, range: 0-360
     * @param s
     * 		 saturation value, range: 0-1
     * @param v
     * 		 value value ???, range: 0-1
     * @return {r, g, b}
     * 		 range 0-1 		  
     */
    public static float[] toRGB(int h, float s, float v) {
    	Color rgb = new Color(Color.HSBtoRGB(h/360f, s, v));
    	return new float[]{rgb.getRed()/255f, rgb.getGreen()/255f, rgb.getBlue()/255f};
    }
    
    
    public static AutopilotOutputs buildOutputs(float lwIncl, float rwIncl, float verStabIncl, float horStabIncl, float thrust) {
    	return new AutopilotOutputs() {
			public float getVerStabInclination() {
				return verStabIncl;
			}
			public float getThrust() {
				return thrust;
			}
			public float getRightWingInclination() {
				return rwIncl;
			}
			public float getLeftWingInclination() {
				return lwIncl;
			}
			public float getHorStabInclination() {
				return horStabIncl;
			}
		};
    }
    
    
    public static AutopilotInputs buildInputs(byte[] image, float x, float y, float z, float heading, float pitch, float roll, float dt) {
    	return new AutopilotInputs() {
			public float getZ() {
				return z;
			}
			public float getY() {
				return y;
			}
			public float getX() {
				return x;
			}
			public float getRoll() {
				return roll;
			}
			public float getPitch() {
				return pitch;
			}
			public byte[] getImage() {
				return image;
			}
			public float getHeading() {
				return heading;
			}
			public float getElapsedTime() {
				return dt;
			}
		};
    }
    
    
    public static AutopilotInputs buildInputs(byte[] image, Vector3f pos, float heading, float pitch, float roll, float dt) {
    	return buildInputs(image, pos.x, pos.y, pos.z, heading, pitch, roll, dt);
    }

	public static int[] buildIntArr(int... args) {
		return args;
	}
    
	public static float getEngineZ(AutopilotConfig config) {
		return config.getTailMass() / config.getEngineMass() * config.getTailSize();
	}
	
	public static AutopilotConfig createDefaultConfig() {
        return new AutopilotConfig() {
            public float getGravity() {return 9.81f;}
            public float getWingX() {return 0.25f;}
            public float getTailSize() {return 0.25f;}
            public float getEngineMass() {return 0.125f;}
            public float getWingMass() {return 0.125f;}
            public float getTailMass() {return 0.0625f;}
            public float getMaxThrust() {return 3f;}
            public float getMaxAOA() {return FloatMath.toRadians(45);}
            public float getWingLiftSlope() {return 0.1f;}
            public float getHorStabLiftSlope() {return 0.05f;}
            public float getVerStabLiftSlope() {return 0.05f;}
            public float getHorizontalAngleOfView() {return FloatMath.toRadians(120f);}
            public float getVerticalAngleOfView() {return FloatMath.toRadians(120f);}
            public int getNbColumns() {return 200;}
            public int getNbRows() {return 200;}};
    }
	
}
