package communication;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import autopilot.Autopilot;
import datatypes.*;

public class AutopilotClient<T extends Autopilot> implements Autopilot{
	
	private final int portNb;
	private final String hostname;
	
	
	/**
	 * Do NOT use this class in this contructor recursively.
	 */
	public AutopilotClient(String hostname, int portNb, Class<T>  autopilotClass) throws Exception {
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
}
