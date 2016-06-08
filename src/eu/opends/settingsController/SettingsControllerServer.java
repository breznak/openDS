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

package eu.opends.settingsController;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;

/**
 * 
 * @author Daniel Braun, Rafael Math
 */
public class SettingsControllerServer extends Thread
{
	ServerSocket serverSocket = null;
	Socket clientSocket = null;
	OutputStream out = null;
	DataInputStream in = null;
	
	private Simulator sim;
	private int port = 0;
	private boolean connected = false;
	
	private List<ConnectionHandler> connections = new ArrayList<ConnectionHandler>();
	
	public SettingsControllerServer(Simulator sim)
	{
		this.sim = sim;
		this.port = Simulator.getSettingsLoader().getSetting(Setting.SettingsControllerServer_port, 
				SimulationDefaults.SettingsControllerServer_port);
	}
	
	
	public void run(){		
		try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("SettingsControllerServer: could not listen on port:"+port);
            return;
        }
          
        System.out.println("SettingsControllerServer started at port "+port);
             
        while(!isInterrupted()){        	
			try {
					clientSocket = serverSocket.accept();	//blocking		
					clientSocket.setSoTimeout(100);
	    	      	out = clientSocket.getOutputStream();
	    		  	in = new DataInputStream(clientSocket.getInputStream());
	    		  	ConnectionHandler con = new ConnectionHandler(sim, out, in);
	    		  	connections.add(con);
	    		  	con.start();
			} catch (Exception e) {				
				//e.printStackTrace();
			}
	    }
        
        System.out.println("SettingsControllerServer closed.");		
	}	
	
/*
	public void close()
	{	
		if(connected && clientSocket != null)
		{
			  try {
			    clientSocket.close();
			  } catch (IOException e) {
			    e.printStackTrace();
			  }
		}

		if(serverSocket != null)
		{
			  try {
			    serverSocket.close();
			  } catch (IOException e) { 
			    e.printStackTrace();
			  }
		}

		interrupt();
	}	
*/
	public void close()
	{
		interrupt();

		for(ConnectionHandler con : connections)
		{
			con.interrupt();
		}

		if(connected)
		{
			try {
				if(clientSocket != null)
					clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			if(serverSocket != null)
				serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
        
}