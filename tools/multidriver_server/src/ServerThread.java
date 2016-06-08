/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2016 Rafael Math
*
*  OpenDS is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  OpenDS is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with OpenDS. If not, see <http://www.gnu.org/licenses/>.
*/


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JTextArea;


public class ServerThread 
{
	private ConcurrentHashMap<String, CarData> clientData = new ConcurrentHashMap<String, CarData>();
	private ArrayList<ClientThread> threadList = new ArrayList<ClientThread>();
	private ServerSocket serverSocket;
    private boolean running = true;
    private ServerThread server;
    private int counter = 0;
    private JTextArea textArea;
    
    
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
    
	
    public ServerThread(int maxFramerate, int port, JTextArea textArea)
	{
		server = this;
		this.textArea = textArea;
 
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

			    	// unregister all vehicles
			    	unregisterAllVehicles();
			    	
			    	// close all client sockets
			    	for(ClientThread thread : threadList)
			    		thread.closeClientSocket();
			    	
			    	// close server socket
			        serverSocket.close();
			        
				} catch (IOException e) {
			
					e.printStackTrace();
				}
        	}


        } ).start();
	}

	
	public void stopServer()
    {
    	running = false;
    }
	
	
	private void unregisterAllVehicles() 
	{
		Iterator<ClientThread> threadIterator = threadList.iterator();
		while(threadIterator.hasNext()) 
		{
			ClientThread thread = threadIterator.next();		
			String removeString = "";
			
			ArrayList<String> clientList = clientData.get(thread.getID()).getKnownClients();
			
			// search for deletes
			Iterator<String> clientIterator = clientList.iterator();
			while(clientIterator.hasNext()) 
			{
				// remove client (id)
				removeString += "<remove id=\"" + clientIterator.next() + "\" />";
			}
			
			// generate output string
			if(!removeString.isEmpty())
			{
				String outputString = "<update>" + removeString + "</update>";
				thread.update(outputString);
			}
		}
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
    	
    	if(textArea != null)
    		textArea.replaceRange("", 0, textArea.getText().length());
    	
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
				else if(textArea != null)
				{
					// update list of currently available multi-driver vehicles
					DecimalFormat df = new DecimalFormat("0.00");
			        textArea.append(threadID + "   " + padLeft(carData.getDriverName(),30) + ":    [" + 
			        		padLeft(df.format(carData.getPosX()),20) + "   " + 
			        		padLeft(df.format(carData.getPosY()),20) + "   " + 
			        		padLeft(df.format(carData.getPosZ()),20) + "   ]\n\r");					
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
    
    
    private String padLeft(String s, int n) 
    {
    	int addSpaces = Math.max(0, n - 2*s.length());
    	
    	String spaces = "";
    	for(int i=0; i<addSpaces; i++)
    		spaces += " ";
    	
        return spaces + s;  
    }
}
