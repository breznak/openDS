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


import java.net.*;
import java.io.*;
 
public class ClientThread extends Thread 
{
	private ServerThread server;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
	private boolean isRunning;
	private String id;
	private boolean removeFromTheadList = false;
	private boolean close = false;

 
    public ClientThread(ServerThread server, Socket socket) 
    {
    	super("MultiDriver_ClientThread");
    	this.server = server;
    	this.socket = socket;
    }
 
    
    public void run() 
    {
	    try {
	    	output = new PrintWriter(socket.getOutputStream(), true);
	    	input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    	isRunning = true;
	    	
	        while(!close) 
	        {	        	
	        	String message = readMessage();
	        	
	        	boolean shutDown = evalMessage(message);
	        	
	        	if(shutDown)
	        		break;
	        }
	        
	        isRunning = false;
	        
	        output.close();
	        input.close();
	        socket.close();
	        
	        // removed by isAlive() check in update method of MultiDriverServer class
	        //server.getThreadList().remove(this);
	 
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    }

    
	private boolean evalMessage(String message) 
	{
		//System.out.println("msg: '" + message + "'");
		XMLParser parser = new XMLParser("<multiDriver>" + message + "</multiDriver>");
		
		if(parser.isRegister())
		{
			String modelPath = parser.getModelPath();
			String driverName = parser.getDriverName();			
	    	id = server.registerNewClient(modelPath, driverName);
	    	System.out.println("Client '" + id + "' registered");
	    	
	    	try {
	    		   		
	    		output = new PrintWriter(socket.getOutputStream(), true);
	    		output.println("<registered id=\"" + id + "\" />");
	    	
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		else if(parser.isUnregister())
		{
			String unregisterID = parser.getUnregisterID();
	    	server.unregisterClient(unregisterID);
	    	System.out.println("Client '" + unregisterID + "' unregistered");
	    	
	    	try {
	    		   		
	    		output = new PrintWriter(socket.getOutputStream(), true);
	    		output.println("<unregistered id=\"" + id + "\" />");
	    	
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
	    	
	    	return true;
		}
		else if(parser.isUpdate())
		{
			CarData carData = server.getClientData().get(parser.getUpdateID());
			
			carData.setPosX(parser.getPosX());
			carData.setPosY(parser.getPosY());
			carData.setPosZ(parser.getPosZ());
			
			carData.setRotW(parser.getRotW());
			carData.setRotX(parser.getRotX());
			carData.setRotY(parser.getRotY());
			carData.setRotZ(parser.getRotZ());
			
			carData.setHeading(parser.getHeading());
			
			carData.setWheelSteering(parser.getWheelSteering());
			carData.setWheelPos(parser.getWheelPosition());
			
			carData.setUpdateNotSent();
		}
		
		return false;
	}


	public String getID()
	{
		return id;
	}
	
	
	public void update(String outputLine)
	{
		if(isRunning)
		{
			output.println(outputLine);
		}
	}
	
	
	public boolean removeFromThreadList()
	{
		return removeFromTheadList;
	}
	
	
    private String readMessage() 
    {
    	String message = "";
    	
		try {
		 	char[] buffer = new char[10000];
		 	int numberOfChars = input.read(buffer, 0, 10000);
		 	message = new String(buffer, 0, numberOfChars);
		 	
	 	} catch (Exception e) {
			//e.printStackTrace();
		}
	 	
	 	return message;		 	
    }


	public void closeClientSocket() 
	{
		close = true;		
	}
}