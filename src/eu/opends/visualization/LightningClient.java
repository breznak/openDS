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

package eu.opends.visualization;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;


/**
 * This class provides a TCP client that connects to a given IP address
 * and port in order to transmit the position and orientation of the current
 * camera view. The required frame rate can be set in the constructor. 
 * 
 * @author Rafael Math
 */
public class LightningClient
{
	enum Target
	{
		LIGHTNING, PCL;
	}
	
	private Calendar timeOfLastFire = new GregorianCalendar();
	private Socket serverSocket;
	private int framerate;
	private PrintWriter printWriter;
	private float scalingFactor;
	private boolean sendPosOriAsOneString;
	private Target target = Target.PCL;
	
	
	/**
	 * The constructor
	 */
	public LightningClient() 
	{
		SettingsLoader settingsLoader = Simulator.getDrivingTask().getSettingsLoader();
		String IP = settingsLoader.getSetting(Setting.ExternalVisualization_ip, SimulationDefaults.Lightning_ip);
		int port = settingsLoader.getSetting(Setting.ExternalVisualization_port, SimulationDefaults.Lightning_port);
		framerate = settingsLoader.getSetting(Setting.ExternalVisualization_updateRate, SimulationDefaults.Lightning_updateRate);
		scalingFactor = settingsLoader.getSetting(Setting.ExternalVisualization_scalingFactor, SimulationDefaults.Lightning_scalingFactor);
		sendPosOriAsOneString = settingsLoader.getSetting(Setting.ExternalVisualization_sendPosOriAsOneString, SimulationDefaults.sendPosOriAsOneString);
		
		try {

			// open socket connection
			serverSocket = new Socket(IP,port);

		} catch (Exception e) {
			System.err.println("No TCP connection possible to Lightning at " + IP + ":" + port);
		}
	}

	
	public void sendCameraData(Camera camera) 
	{
		if(target == Target.PCL)
			sendCameraDataToPCL(camera);
		else if(target == Target.LIGHTNING)
			sendCameraDataToLightning(camera);
	}
	
	
	/**
	 * This method sends the given camera data to the server, regarding the frame 
	 * rate. If not enough time has passed by since last fire, the camera data 
	 * might be rejected.<br>
	 * The data will be sent as one string:<br>
	 * <code>ltupdate .remotemotionsensor -posoriIn "12 30 0;90 120 180"</code><br>
	 * or as two strings:<br>
	 * <code>ltupdate .remotemotionsensor -positionIn "12 30 0"</code><br>
	 * <code>ltupdate .remotemotionsensor -orientationIn "90 120 180"</code>
	 * 
	 * @param camera
	 * 			The current camera view
	 * 
	 * @param sendOneString
	 * 			sends position and orientation data in one string (if true), otherwise
	 * 			each string will be send separately
	 */
	private void sendCameraDataToLightning(Camera camera) 
	{	
		// break, if no connection established
		if(serverSocket == null)
			return;
		
		// generate time stamp
		Calendar currentTime = new GregorianCalendar();
		
		// if enough time has passed by since last fire, the event will be forwarded
		if(forwardEvent(currentTime))
		{			
			
			try { 	

				if(sendPosOriAsOneString)
				{
					String positionString = LightningData.getCameraPosition(camera, scalingFactor);
					String orientationString = LightningData.getCameraOrientation(camera);
					
					String sendString = "ltupdate .remotemotionsensor -posoriIn \"" + positionString + ";" 
											+ orientationString + "\"\n";
					
				 	// send position and orientation to lightning
					printWriter = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
				 	printWriter.print(sendString);
				 	printWriter.flush();
				}
				else
				{
				 	// send position to lightning
					String positionString = LightningData.getCameraPosition(camera, scalingFactor);
					String sendStringPos = "ltupdate .remotemotionsensor -positionIn \"" + positionString + "\"\n";
					printWriter = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
				 	printWriter.print(sendStringPos);
				 	printWriter.flush();

				 	//try {Thread.sleep(10);} 
					//catch (InterruptedException e){}
				 	
				 	// send position to lightning
				 	String orientationString = LightningData.getCameraOrientation(camera);
				 	String sendStringOri = "ltupdate .remotemotionsensor -orientationIn \"" + orientationString + "\"\n";
					printWriter = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
				 	printWriter.print(sendStringOri);
				 	printWriter.flush();
				}
			 				 	
			} catch (IOException e) {
				System.err.println("LightningClient_sendCameraData(): " + e.toString());
			}
		}
	}

	
	private void sendCameraDataToPCL(Camera camera) 
	{	
		// break, if no connection established
		if(serverSocket == null)
			return;
		
		// generate time stamp
		Calendar currentTime = new GregorianCalendar();
		
		// if enough time has passed by since last fire, the event will be forwarded
		if(forwardEvent(currentTime))
		{			
			
			try { 	

				Vector3f location = camera.getLocation();
				String positionString = location.getX()+";"+(-1*location.getZ())+";"+(location.getY());
				
				Vector3f direction = camera.getDirection();
				float x = location.getX() + direction.getX();
				float y = location.getY() + direction.getY();
				float z = location.getZ() + direction.getZ();
				String directionString = x+";"+(-1*z)+";"+y;
				
				Vector3f up = camera.getUp();
				String upString = up.getX()+";"+(-1*up.getZ())+";"+up.getY();

				String sendString = "["+positionString+";"+directionString+";"+upString+"]";
					
				// send position and orientation to lightning
				printWriter = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
				printWriter.print(sendString);
				printWriter.flush();
			 				 	
			} catch (IOException e) {
				System.err.println("LightningClient_sendCameraData(): " + e.toString());
			}
		}
	}


	/**
 	 * This method sends the given traffic light data to the server immediately 
 	 * even if not enough time has passed by since last traffic light data was sent.
 	 * This method sends two strings (E.g. if there are three traffic lights):<br>
 	 * <code>ltupdate .remotemotionsensor -trafficlightmodesIn {"red_left" "green_up" "red_right"}</code><br>
 	 * and<br>
 	 * <code>ltupdate .remotemotionsensor -switchtrafficlightsIn 1</code>
 	 * 
	 * @param instructionString
	 * 				String containing traffic light data to be sent to Lightning
	 */
	public synchronized void sendTrafficLightData(String instructionString)
	{
		// break, if no connection established
		if(serverSocket == null)
			return;
		
		try { 
			
			// set states of all traffic lights
			printWriter = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
		 	printWriter.print(instructionString);
		 	printWriter.flush();
		 	
		 	try {Thread.sleep(50);} 
			catch (InterruptedException e){}
		 	
		 	// submit states for all traffic lights
		 	printWriter = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
		 	printWriter.print("ltupdate .remotemotionsensor -switchtrafficlightsIn 1\n");
		 	printWriter.flush();
		 	
		} catch (IOException e) {
			System.err.println("LightningClient_sendTrafficLightData(): " + e.toString());
		}
	
	}

	
	/**
	 * Closes the TCP connection to the server (if a connection could be established).
	 */
	public void close()
	{
		try {
			if((serverSocket != null) && (printWriter != null))
			{
				try {Thread.sleep(100);} 
				catch (InterruptedException e){}

				printWriter.close();
				serverSocket.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * This method checks whether the incoming camera information should 
	 * be sent to the server at the current time complying with the given 
	 * frame rate
	 * 
	 * @param now
	 * 			The current time stamp
	 * 
	 * @return true if enough time has passed by since last fire, false otherwise
	 */
    private boolean forwardEvent(Calendar now)
    {
        // fire an event every x milliseconds
    	int fireInterval = 1000 / framerate;

        // subtract time of last event from current time to get time elapsed since last fire
        long elapsedMillisecs = timeDiff(now,timeOfLastFire);
        
        if (elapsedMillisecs >= fireInterval)
        {
            // update time of last fire
            timeOfLastFire.add(Calendar.MILLISECOND, fireInterval);

            //fire
            return true;
        }
        else
            // do not fire
            return false;
    }
    
    
	/**
	 * This method computes the difference between two given time stamps
	 * 
	 * @param timestamp1
	 * 			First time stamp value to compare
	 * 
	 * @param timestamp2
	 * 			Second time stamp value to compare
	 * 
	 * @return difference between the given time stamps in milliseconds (long)
	 */
    private static long timeDiff(Calendar timestamp1, Calendar timestamp2)
    {
        return Math.abs(timestamp1.getTimeInMillis() - timestamp2.getTimeInMillis());
    }
}
