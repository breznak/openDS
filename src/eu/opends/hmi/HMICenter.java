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

package eu.opends.hmi;

import java.net.InetSocketAddress;

import eu.opends.car.Car;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.environment.TrafficLight;
import eu.opends.main.Simulator;
import eu.opends.trigger.TriggerCenter;


/**
 * This class reports collisions with triggers placed in the 
 * model to the HMI.
 * 
 * @author Rafael Math
 */
public class HMICenter 
{	
	private static Simulator sim;
	private static boolean sendDataToHMI = false;
	private static HMIWebSocketServer server;
	
	/**
	 * Initializes the HMICenter by setting the simulator field to the current simulator instance.
	 * 
	 * @param simulator
	 * 			The simulator
	 */
	public static void init(Simulator simulator)
	{
		sim = simulator;

		sendDataToHMI = Simulator.getDrivingTask().getSettingsLoader().getSetting(Setting.HMI_enableConnection, false);
		
		if(sendDataToHMI)
		{
			String ip = Simulator.getDrivingTask().getSettingsLoader().getSetting(Setting.HMI_ip, "localhost");
			int port = Simulator.getDrivingTask().getSettingsLoader().getSetting(Setting.HMI_port, 2111);
			
			InetSocketAddress address = new InetSocketAddress(ip, port);
			server = new HMIWebSocketServer(address);
		}
	}
	
	
	public static HMIWebSocketServer getHMIWebSocketServer()
	{
		return server;
	}
		
	
	/**
	 * Reports the collision of the car with a traffic light trigger 
	 * to the HMI. By the given car and traffic light position a permanent 
	 * distance update will be sent to the HMI screen.
	 * 
	 * @param trafficLight
	 * 			traffic light object
	 * 
	 * @param car
	 * 			user-controlled car of simulator 
	 */
	public static void reportTrafficLightCollision(TrafficLight trafficLight, Car car) 
	{
		String triggerName = getTriggerName(trafficLight);
		
		if(!TriggerCenter.triggerReportList.contains(triggerName) && sendDataToHMI)
		{			
			// add traffic light to report list
			TriggerCenter.triggerReportList.add(triggerName);
		
			// create new presentation model
			TrafficLightPresentationModel presentationModel = new TrafficLightPresentationModel(sim, car, trafficLight);

			// create presentation
			long presentationID = presentationModel.createPresentation();
			
			// send permanent messages with distance to HMI GUI and screen
			HMIThread thread = new HMIThread(sim, presentationModel, triggerName, presentationID);
			thread.start();
		}
	}

	
	/**
	 * Reports the collision of the car with a traffic light trigger 
	 * to the HMI if the corresponding traffic light is red. By the 
	 * given car and traffic light position a permanent distance update 
	 * will be sent to the HMI screen in order to cancel the 
	 * presentation, if the car passes the traffic light
	 * 
	 * @param trafficLight
	 * 			traffic light object
	 * 
	 * @param car
	 * 			user-controlled car of simulator 
	 */
	public static void reportRedTrafficLightCollision(TrafficLight trafficLight, Car car) 
	{
		String trafficLightName = "isRed_" + trafficLight.getName();
		
		if(!TriggerCenter.triggerReportList.contains(trafficLightName) && sendDataToHMI)
		{			
			// add traffic light to report list
			TriggerCenter.triggerReportList.add(trafficLightName);
		
			// create new presentation model
			RedTrafficLightPresentationModel presentationModel = new RedTrafficLightPresentationModel(car,trafficLight);

			// create presentation
			long presentationID = presentationModel.createPresentation();

			// send permanent messages with distance to HMI GUI and screen
			HMIThread thread = new HMIThread(sim, presentationModel, trafficLightName, presentationID);
			thread.start();
		}
	}
	


	/**
	 * This method summarizes different triggers to one. In PROGRAM mode only
	 * one trigger for all lanes of a road leading to an intersection is wanted 
	 * instead of an individual trigger for each traffic light. Therefore the
	 * triggers of other lanes in the same road will be suppressed while a 
	 * trigger of one lane is already active.
	 * 
	 * @param trafficLight
	 * 			Traffic light that actually was triggered
	 * 
	 * @return
	 * 			A concatenation of all traffic light names of the related road.
	 * 			This includes the input traffic light and all neighboring traffic
	 * 			lights (ordered by lane). If no traffic light position data 
	 * 			available, the input traffic light name will be returned instead.
	 */
	private static String getTriggerName(TrafficLight trafficLight) 
	{
		try{
			// get position (intersection and road) of the given traffic light
			String intersectionID = trafficLight.getIntersectionID();
			String roadID = trafficLight.getPositionData().getRoadID();
			
			String name = "";
			for(int i=0; i<=2; i++)
			{
				// go through all lanes of the considered road and - if available - concatenate 
				// the names of the corresponding traffic lights (e.g. "TrafficLight.11_04TrafficLight.11_06")
				TrafficLight currentTrafficLight = sim.getTrafficLightCenter().getTrafficLightByLocation(intersectionID, roadID, i);
				if(currentTrafficLight != null)
					name += currentTrafficLight.getName();
			}
			return name;
			
		}catch(NullPointerException e){
			
			// if no traffic light position data available --> return input 
			// traffic light's name. E.g. "TrafficLight.11_06"
			return trafficLight.getName();
		}
	}



	public static void close() 
	{
		try {
			if(server != null)
				server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void sendMsg(String message) 
	{
		if(server != null)
			server.sendMsg(message);		
	}

}