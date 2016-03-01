import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class MultiDriverServerNoGUI
{
    // maximum frame rate: frame rate can be set individually in each client, 
    // however, this number will never be exceeded.
    private static int maxFramerate = 20;
    
    
	public static void main(String[] args)
	{
		System.out.println();
		System.out.println("-----------------------");
		System.out.println("| Multi-driver Server |");
		System.out.println("-----------------------");
		System.out.println();
		
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		
		int port = 0;
		
		try {
			
			if(args.length > 0)
				port = Integer.parseInt(args[0]);
			else
			{
				System.out.print("Please select port: ");
				line = input.readLine();
				port = Integer.parseInt(line);
			}
		
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
			
		ServerThread server = new ServerThread(maxFramerate, port, null);
		
		System.out.println("Press 'e' + <enter> to terminate: ");
		 	
	 	while (true)
	 	{
	 		
	 		try {
				line = input.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	 		
	 		if(line.equalsIgnoreCase("e"))
	 			break;
	 	}
	 	
	 	server.stopServer();
	}
}
