import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


public class MultiDriverServer 
{
	private ConcurrentHashMap<String, CarData> clientData = new ConcurrentHashMap<String, CarData>();
	private ArrayList<ClientThread> threadList = new ArrayList<ClientThread>();
	private ServerSocket serverSocket;
    private boolean running = true;
    private MultiDriverServer server;
    private int counter = 0;
    
    // maximum frame rate: frame rate can be set individually in each client, 
    // however, this number will never be exceeded.
    private int maxFramerate = 20;
    
    
    public ArrayList<ClientThread> getThreadList()
    {
    	return threadList;
    }
    
	
	public synchronized String registerNewClient(String modelPath, String driverName)
	{
		// generate non-existing ID
		counter++;
		String id = "mdv_" + counter;
		
		clientData.put(id, new CarData(modelPath, driverName));
		
		return id;
	}
	

	public synchronized void unregisterClient(String id)
	{
		clientData.remove(id);
	}


	public ConcurrentHashMap<String, CarData> getClientData() 
	{
		return clientData;		
	}
    
	
    public MultiDriverServer(int port)
	{
		server = this;
 
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(1000/maxFramerate);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }
 
        
        ( new Thread() { 
        	public void run()
	        {
	        	try {
	        		
			    	while (running)
			        {
			    		update();
			    		
			    		try {			    			
				        	Socket clientSocket = serverSocket.accept();
			
				        	ClientThread thread = new ClientThread(server, clientSocket);
				        	thread.start();
				        	threadList.add(thread);
				        	
			    		} catch (SocketTimeoutException timeOutException) {
			    		}
			        }
			    	
			        serverSocket.close();
			        
				} catch (IOException e) {
			
					e.printStackTrace();
				}
        	}
        } ).start();
        
        /*
        ( new Thread() { 
        	public void run()
	        {
		    	while (running)
		        {
		    		try {
		    			update();
		    		
						Thread.sleep(1000/maxFramerate);
					} catch (Exception e) {
						e.printStackTrace();
					}
		        }
        	}
        } ).start();
        */
	}
    
    
    public void stopServer()
    {
    	running = false;
    }
	
	
    public void update() 
	{
		/*
		EXAMPLE:
		
		<update>
			<add id="mdv_1" modelPath="" driverName="" />
			<change id="mdv_1" pos="1;2;3" rot="1;2;3;4" heading="358.4"  wheel="1;2" />
			<remove id="mdv_1">
		<update>
		*/
    	
		Iterator<ClientThread> threadIterator = threadList.iterator();
		while(threadIterator.hasNext()) 
		{
			ClientThread thread = threadIterator.next();
			
			if(!thread.isAlive())
			{
				threadIterator.remove();
				break;
			}
			
			String threadID = thread.getID();
			
			// prevent to process a thread that has not been created properly 
			// (parallel threads: create vs. update !!!)
			if(threadID == null)
				break;
			
			String addString = "";
			String changeString = "";
			String removeString = "";
			
			ArrayList<String> clientList;
			
			try {
				clientList = clientData.get(threadID).getKnownClients();
			} catch (Exception e) {
				break;
			}
			
			// iterate over all registered clients
			for(Entry<String, CarData> cd : clientData.entrySet())
			{
				String carID = cd.getKey();
				CarData carData = cd.getValue();
				
				// exclude own client
				if(!carID.equals(threadID))
				{
					if(!clientList.contains(carID))
					{
						// add new client (id + model path + name)
						addString += "<add id=\"" + carID + 
										"\" modelPath=\"" + carData.getModelPath() + 
										"\" driverName=\""	+ carData.getDriverName() + "\" />";
						
						clientList.add(carID);
					}
					
					// update position and rotation data (id + pos + rot|heading) if update available
					if(!carData.isUpdateSent(threadID))
					{
						String rotationString;
						
						if (carData.getHeading() != null)
							rotationString = "heading=\"" + carData.getHeading() + "\" ";
						else if(carData.getRotW() != null && carData.getRotX() != null && carData.getRotY() != null && carData.getRotZ() != null)
							rotationString = "rot=\""	+ carData.getRotW() + ";" + carData.getRotX() + ";" + carData.getRotY() + ";" + carData.getRotZ() + "\" ";
						else
							rotationString = "";
						
						changeString += "<change id=\"" + carID + "\" " +
							"pos=\"" + carData.getPosX() + ";" + carData.getPosY() + ";" + carData.getPosZ() + "\" " +
							rotationString +
							"wheel=\"" + carData.getWheelSteering() + ";" + carData.getWheelPos() + "\" " +
							"/>";
						carData.setUpdateSent(threadID);
					}
				}
			}
			
			// search for deletes
			Iterator<String> iterator = clientList.iterator();
			while(iterator.hasNext()) 
			{
			    String carID = iterator.next();
				if(!clientData.containsKey(carID))
				{
					// remove client (id)
					removeString += "<remove id=\"" + carID + "\" />";
					
					iterator.remove();
				}
			}
					
			//System.out.println("<update>" + addString + changeString + removeString + "</update>");
			
			// generate output string
			if(!addString.isEmpty() || !changeString.isEmpty() || !removeString.isEmpty())
			{
				String outputString = "<update>" + addString + changeString + removeString + "</update>";
				thread.update(outputString);
			}
		}
	}

    
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
			
		MultiDriverServer server = new MultiDriverServer(port);
		
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
