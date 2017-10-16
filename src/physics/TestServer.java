package physics;

import java.io.*;
import java.net.*;

public class TestServer {
	
	public static void main(String[] args) {
		
		int portNb = 0;
		
		try {
			ServerSocket server = new ServerSocket(portNb);
			System.out.println("port: " + server.getLocalPort());
			
			Socket client = server.accept();
			
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));			
			
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.equalsIgnoreCase("bye")) {
					out.println("cya");
				} else {
					out.println("echo: " + inputLine);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("exit");
		
	}
}
