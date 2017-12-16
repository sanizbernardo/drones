package interfaces;
public class AutopilotConfigWriter {

    public static void write(java.io.DataOutputStream stream, AutopilotConfig value) throws java.io.IOException {
        stream.writeFloat(value.getGravity());
        stream.writeFloat(value.getWingX());
        stream.writeFloat(value.getTailSize());
        stream.writeFloat(value.getEngineMass());
        stream.writeFloat(value.getWingMass());
        stream.writeFloat(value.getTailMass());
        stream.writeFloat(value.getMaxThrust());
        stream.writeFloat(value.getMaxAOA());
        stream.writeFloat(value.getWingLiftSlope());
        stream.writeFloat(value.getHorStabLiftSlope());
        stream.writeFloat(value.getVerStabLiftSlope());
        stream.writeFloat(value.getHorizontalAngleOfView());
        stream.writeFloat(value.getVerticalAngleOfView());
        stream.writeInt(value.getNbColumns());
        stream.writeInt(value.getNbRows());
    }
}
