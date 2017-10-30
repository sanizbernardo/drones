package datatypes;
public class AutopilotOutputsWriter {
    
	public static void write(java.io.DataOutputStream stream, AutopilotOutputs value) throws java.io.IOException {
        stream.writeFloat(value.getThrust());
        stream.writeFloat(value.getLeftWingInclination());
        stream.writeFloat(value.getRightWingInclination());
        stream.writeFloat(value.getHorStabInclination());
        stream.writeFloat(value.getVerStabInclination());
    }
}
