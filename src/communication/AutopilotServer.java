package communication;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import Autpilot.Autopilot;
import datatypes.*;

public class AutopilotServer<T extends Autopilot> implements Runnable{
	
	public static final int SIMULATION_STARTED = 0;
	public static final int TIME_PASSED = 1;
	public static final int SIMULATION_ENDED = 2;	
	
	private final int port;
	private T autopilot;
	
	public AutopilotServer(int port, Class<T> autopilotClass) {
		this.port = port;
		try {
			this.autopilot = autopilotClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket(port);
			
			System.out.println("Autopilotserver for " + autopilot.getClass() +
					" running on port: " + server.getLocalPort());
			
			int methodId;
			AutopilotInputs inputs;
			AutopilotConfig config;
			AutopilotOutputs outputs;
			
			while (true) {
				Socket client = server.accept();
				System.out.println("accepted connection from: " + client.getInetAddress());
				
				DataInputStream inputStream = new DataInputStream(client.getInputStream());
				DataOutputStream outputStream = new DataOutputStream(client.getOutputStream()); 
				
				methodId = inputStream.read();
				
				System.out.println("executing method " + methodId);
				
				 switch (methodId) {
				case SIMULATION_STARTED:
					config = AutopilotConfigReader.read(inputStream);
					inputs = AutopilotInputsReader.read(inputStream);
					
					outputs = autopilot.simulationStarted(config, inputs);
					
					AutopilotOutputsWriter.write(outputStream, outputs);
					break;

				case TIME_PASSED:
					inputs = AutopilotInputsReader.read(inputStream);
					
					outputs = autopilot.timePassed(inputs);
					
					AutopilotOutputsWriter.write(outputStream, outputs);
					break;
				
				case SIMULATION_ENDED:
					autopilot.simulationEnded();
					System.out.println("closing server");
					server.close();
					return;
					
				default:
					
					break;
					
				}
				 
				 config = null;
				 inputs = null;
				 outputs = null;
				 
				 
//				 while (!client.isClosed()) {
//					try {
//						TimeUnit.NANOSECONDS.sleep(1);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
			System.out.println("connection closed");
				 
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
