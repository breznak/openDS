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

package eu.opends.multiDriver;


import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.*;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import eu.opends.car.Car;
import eu.opends.drivingTask.scenario.ScenarioLoader;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.environment.XMLParser;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;

/**
 * This class represents the connector to the CAN-Interface. Steering, gas, brake and 
 * control instructions from the real car will be forwarded to the simulator; heading,
 * geo coordinates and speed will be sent back to the CAN-Interface in order to display
 * the position and speed on a in-car display. Furthermore trigger collisions can be 
 * sent to the CAN-Interface.
 * 
 * @author Rafael Math
 */
public class MultiDriverClient extends Thread
{
	private Simulator sim;
	private ArrayList<Update> updateList = new ArrayList<Update>();
	private Car car;
	private int framerate;
	private boolean stoprequested;
	private boolean errorOccurred;
	private Calendar timeOfLastFire;
	private PrintWriter printWriter;
	private Socket socket;
	private String id;
	private ArrayList<String> registeredVehiclesList;
	
	
	/**
	 * Creates a new TCP connection with the multi driver server at the given IP and port
	 * 
	 * @param sim
	 * 			The simulator
	 * 
	 * @param driverName
	 * 			Name of the driver
	 */
	public MultiDriverClient(Simulator sim, String driverName)
    {
		super();
		
		this.sim = sim;
		this.car = sim.getCar();
		stoprequested = false;
		errorOccurred = false;
		timeOfLastFire = new GregorianCalendar();
		registeredVehiclesList = new ArrayList<String>();
		
		ScenarioLoader scenarioLoader = Simulator.getDrivingTask().getScenarioLoader();
		String carModelPath = scenarioLoader.getModelPath();
		
		SettingsLoader settingsLoader = Simulator.getDrivingTask().getSettingsLoader();
		String ip = settingsLoader.getSetting(Setting.MultiDriver_ip, SimulationDefaults.MultiDriver_ip);
		int port = settingsLoader.getSetting(Setting.MultiDriver_port, SimulationDefaults.MultiDriver_port);
		framerate = settingsLoader.getSetting(Setting.MultiDriver_updateRate, SimulationDefaults.MultiDriver_updateRate);
		
		try {

			// connect to Server
			socket = new Socket(ip,port);
			socket.setSoTimeout(10);
			
		 	// send car data (model path and driver name) to multi driver server and flush
			
			String registerString = "<register><modelPath>" + carModelPath + "</modelPath><driverName>"
										+ driverName + "</driverName></register>";
			
			// FIXME
			/*
			String registerString = "<multiDriver> <register id=\"salut\"> <modelPath>" + carModelPath + 
										"</modelPath> <driverName>"	+ driverName + "</driverName> </register> </multiDriver>";
			*/
			printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		 	printWriter.print(registerString);
		 	printWriter.flush();
		 	
		 	//System.out.print(registerString);

		} catch (Exception e) {
			//e.printStackTrace();
			System.err.println("No TCP connection possible to multi driver server at " + ip + ":" + port);
			errorOccurred = true;
		}
    }
	
    
	/**
	 * Listens for incoming MD instructions (as XML), such as position and orientation updates, 
	 * which will be forwarded to the XML-parser
	 */
	@Override
	public void run() 
	{
		String shutDownMessage = "Connection to multi driver server closed";
		
		// when loop is left, connection will be closed
		// loop will be left when requested or error occurred
		while(!stoprequested && !errorOccurred)
		{
			try {

				// delete "NUL" at the end of each line
				String message = readMessage(socket).replace("\0", "");
				
				// print XML instruction
				//System.out.println(message);
				
				// parse and evaluate XML instruction
				// on "registered" --> call method setID();
				// on "update" --> perform changes
				// on "unregistered" --> call method requestStop()
				XMLParser parser = new XMLParser("<multiDriver>" + message + "</multiDriver>");
				parser.evalMultiDriverInstruction(sim, this);

			} catch (SocketException e) {
				
				// will be thrown if e.g. server was shut down
				shutDownMessage = "Multi driver server: connection closed by server";
				errorOccurred = true;
				
			} catch (StringIndexOutOfBoundsException e) {
				
				// will be thrown if e.g. "stop server" button has been clicked
				shutDownMessage = "Multi driver server: connection closed by server";
				errorOccurred = true;
				
			} catch (SocketTimeoutException e) {
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		// close TCP connection to multi driver server if connected at all
		try {
			if ((socket != null) && (printWriter != null))
			{			
				// close socket connection
				printWriter.close();
				socket.close();
					
				System.out.println(shutDownMessage);
			}
		} catch (Exception ex) {
			System.err.println("Could not close connection to multi driver server");
		}
	}

	
	/**
	 * Sends car data to the multi driver server, such as position and rotation.
	 */
	public synchronized void sendCarData()
	{
		// break, if no connection established
		if(socket == null || id == null || errorOccurred)
			return;
		
		// generate time stamp
		Calendar currentTime = new GregorianCalendar();
		
		// if enough time has passed by since last fire, the event will be forwarded
		if(forwardEvent(currentTime))
		{
			Vector3f pos = car.getPosition();
			//Vector3d pos = car.getGeoPosition();
			Quaternion rot = car.getRotation();
			//float heading = car.getHeadingDegree();
			Quaternion wheelRot = car.getCarControl().getWheel(0).getWheelSpatial().getLocalRotation();
			float array[] = new float[3];
			wheelRot.toAngles(array);
			float wheelSteering = array[1];
			float wheelPositon = array[0];

		 	// send car data (ID, position and rotation) to multi driver server and flush
			String positionString = "<update id=\"" + id + "\">" +
										"<position x=\"" + pos.getX() + "\" y=\"" + pos.getY() + "\" z=\"" + pos.getZ() + "\" />" + 
										"<rotation w=\"" + rot.getW() + "\" x=\"" + rot.getX() + "\" y=\"" + rot.getY() + "\" z=\"" + rot.getZ() + "\"/>" +
								/*		"<heading>" + heading + "</heading>" +  */
										"<wheel steering=\"" + wheelSteering + "\" position=\"" + wheelPositon + "\"/>" +
									"</update>";
	
		 	printWriter.print(positionString);
		 	printWriter.flush();
		}
	}
	
	
	public synchronized void setID(String id) 
	{
		this.id = id;		
		
		System.out.println("Connected to multi driver server as '" + id + "'");
	}
	
	
	/**
	 * Requests the connection to close after the current loop
	 * 
	 * @param id
	 * 			Vehicle ID
	 */
	public synchronized void requestStop(String id) 
	{
		if(id.equals(this.id))
			stoprequested = true;
	}
	
	
	public synchronized void close() 
	{
		// break, if no connection established
		if(socket == null || id == null || errorOccurred)	
		{
			stoprequested = true;
			return;
		}

	 	// send unregister string to multi driver server and flush
		String outputString = "<unregister>" + id + "</unregister>";

	 	printWriter.print(outputString);
	 	printWriter.flush();
	}
    
	
	public ArrayList<String> getRegisteredVehicles() 
	{
		return registeredVehiclesList;
	}
	
	
	public synchronized void addRegisteredVehicle(String vehicleID)
	{
		registeredVehiclesList.add(vehicleID);
	}
	
	
	public synchronized void removeRegisteredVehicle(String vehicleID)
	{
		registeredVehiclesList.remove(vehicleID);
	}
	
	
	/**
	 * Reads an incoming message from the socket connection.
	 * 
	 * @param socket
	 * 			Socket connection
	 * 
	 * @return
	 * 			Message string of up to 10,000 characters
	 * 
	 * @throws IOException
	 */
	private String readMessage(Socket socket) throws IOException 
	{
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		char[] buffer = new char[10000];
		int nrOfChars = bufferedReader.read(buffer, 0, buffer.length);
		
		return new String(buffer, 0, nrOfChars);
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

            // fire
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


	public void addVehicle(String vehicleID, String modelPath, String driverName)
	{
		updateList.add(new AddUpdate(sim, vehicleID, modelPath, driverName));
	}
	

	public void changeVehicle(String vehicleID, String positionString, String rotationString, String headingString, 
			String wheelString)
	{
		updateList.add(new ChangeUpdate(sim, vehicleID, positionString, rotationString, headingString, wheelString));
	}
	
	
	public void removeVehicle(String vehicleID)
	{
		updateList.add(new RemoveUpdate(sim, vehicleID));
	}
	

	public void update() 
	{
		updateSceneGraph();
		sendCarData();		
	}
	
	
	public void updateSceneGraph()
	{		
		while(updateList.size() > 0)	
		{
			Update update = updateList.get(0);
			update.performUpdate();
			updateList.remove(0);
		}
	}

}
