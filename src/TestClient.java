import java.net.*;
import java.io.*;

public class TestClient {
	public static void main(String[] args) {
		
		String hostName = "localhost";
		int portNb = 56840;
		
		try {
			Socket server = new Socket(hostName, portNb);
			
			PrintWriter out = new PrintWriter(server.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
			
			BufferedReader keyIn = new BufferedReader(new InputStreamReader(System.in));
			
			String userInput;
			
			while ((userInput = keyIn.readLine()) != null) {
				out.println(userInput);
				String input = in.readLine();
				System.out.println(input);
				if (input.equalsIgnoreCase("cya"))
					break;
			}
			
			server.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
}
