package communication;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import Autpilot.Autopilot;
import datatypes.*;

public class AutopilotClient<T extends Autopilot> implements Autopilot{
	
	private final int portNb;
	private final String hostname;
	
	
	/**
	 * Do NOT use this class in this contructor recursively.
	 */
	public AutopilotClient(String hostname, int portNb, Class<T>  autopilotClass) throws Exception {
		if (autopilotClass == this.getClass())
			throw new Exception("Preventing thread leak due to recursion");
		this.hostname = hostname;
		this.portNb = portNb;
		Thread serverThread = new Thread(new AutopilotServer<T>(portNb, autopilotClass), "Autopilot-Thread");
		serverThread.start();
	}

	@Override
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		try {
			Socket connection = new Socket(hostname, portNb);
			
			DataOutputStream output = new DataOutputStream(connection.getOutputStream());
			DataInputStream input = new DataInputStream(connection.getInputStream());
			
			output.writeByte(AutopilotServer.SIMULATION_STARTED);
			
			AutopilotConfigWriter.write(output, config);
			AutopilotInputsWriter.write(output, inputs);
			
			AutopilotOutputs outputs = AutopilotOutputsReader.read(input);
			
			connection.close();
			System.out.println("closed");

			return outputs;			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {
		try {
			Socket connection = new Socket(hostname, portNb);
			
			DataOutputStream output = new DataOutputStream(connection.getOutputStream());
			DataInputStream input = new DataInputStream(connection.getInputStream());
			
			output.writeByte(AutopilotServer.TIME_PASSED);
			
			AutopilotInputsWriter.write(output, inputs);
			
			AutopilotOutputs outputs = AutopilotOutputsReader.read(input);
			
			connection.close();
			System.out.println("closed");
			
			return outputs;			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void simulationEnded() {
		try {
			Socket connection = new Socket(hostname, portNb);
			
			DataOutputStream output = new DataOutputStream(connection.getOutputStream());
			
			output.writeByte(AutopilotServer.SIMULATION_ENDED);
			
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		AutopilotClient<?> client = new AutopilotClient<Dummypilot>("localhost", 7, Dummypilot.class);
		AutopilotOutputs outputs = client.simulationStarted(
				new AutopilotConfig(){
					public float getGravity() {return 10f;}
					public float getWingX() {return 2.5f;}
					public float getTailSize() {return 5f;}
					public float getEngineMass() {return 70f;}
					public float getWingMass() {return 25f;}
					public float getTailMass() {return 30f;}
					public float getMaxThrust() {return -1f;}
					public float getMaxAOA() {return -1f;}
					public float getWingLiftSlope() {return 0f;}
					public float getHorStabLiftSlope() {return 0f;}
					public float getVerStabLiftSlope() {return 0f;}
					public float getHorizontalAngleOfView() {return -1f;}
					public float getVerticalAngleOfView() {return -1f;}
					public int getNbColumns() {return -1;}
					public int getNbRows() {return -1;}},
				new AutopilotInputs() {
					public float getZ() {return 0;}
					public float getY() {return 0;}
					public float getX() {return 0;}
					public float getRoll() {return 0;}
					public float getPitch() {return 0;}
					public byte[] getImage() {return new byte[] {0,0,0,0};}
					public float getHeading() {return 0;}
					public float getElapsedTime() {return 0;}});
		
		System.out.println(outputs.getHorStabInclination() + " " + outputs.getThrust());
		outputs = client.timePassed(new AutopilotInputs() {
					public float getZ() {return 0;}
					public float getY() {return 0;}
					public float getX() {return 0;}
					public float getRoll() {return 0;}
					public float getPitch() {return 0;}
					public byte[] getImage() {return new byte[] {0,0,0,0};}
					public float getHeading() {return 0;}
					public float getElapsedTime() {return 12.3f;}});
		
		System.out.println(outputs.getLeftWingInclination() + " " + outputs.getRightWingInclination());
		
		client.simulationEnded();
	}

}
