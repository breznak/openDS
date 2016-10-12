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


import eu.opends.car.Car;
import eu.opends.environment.TrafficLight;
import eu.opends.environment.TrafficLight.TrafficLightState;


/**
 * This class represents the presentation of a red traffic light warning. The distance 
 * between the moving car and a red traffic light is measured in order to estimate the 
 * duration of the presentation.
 * 
 * @author Rafael Math
 */
public class RedTrafficLightPresentationModel extends PresentationModel 
{
	private TrafficLight trafficLight;
	
	
	/**
	 * Initializes a red traffic light presentation model by setting the positions 
	 * of the car and traffic light, the minimum distance from the traffic light to 
	 * cancel the presentation and the traffic light object.
	 * 
	 * @param car
	 * 			Car heading towards the traffic light
	 * 
	 * @param trafficLight
	 * 			Traffic light object 
	 */
	public RedTrafficLightPresentationModel(Car car, TrafficLight trafficLight)
	{
		this.car = car;
		this.targetPosition = trafficLight.getLocalPosition();
		this.minimumDistance = 5;
		this.trafficLight = trafficLight;
	}
	

	/**
	 * Condition to stop presentation. Returns true, if approximation of the car is 
	 * less than the given minimum distance, if the car is driving backwards or if
	 * the traffic light state is no longer red.
	 * 
	 * @return
	 * 			True, if stop condition holds
	 */
	@Override
	public boolean stopPresentation()
	{
		return (super.stopPresentation() || (trafficLight.getState() != TrafficLightState.RED));
	}
	
	
	/**
	 * Creates a red traffic light presentation task in the HMI GUI. This
	 * data will only be sent to the HMI bundle once.
	 * 
	 * @return
	 * 			0
	 */
	@Override
	public long createPresentation() 
	{
		// send parameters to HMI bundle
		sendLocalDangerWarningData("start", 40, System.currentTimeMillis());
		return 0;
	}
	
	
	/**
	 * Updates a red traffic light warning presentation model if the time the car takes 
	 * to arrive at the traffic light has changed.
	 */
	@Override
	public void updatePresentation(long presentationID) 
	{
	}
	

	/**
	 * Generates a message containing the red traffic light warning and distance to it.
	 */
	@Override
	public String generateMessage() 
	{
		return "Caution: red traffic light in "+ getRoundedDistanceToTarget(targetPosition) + " m";
	}
	
	
	@Override
	public void stop()
	{
		sendLocalDangerWarningData("stop", 40, System.currentTimeMillis());
		
	}
	
	
	private void sendLocalDangerWarningData(String command, Integer priority, Long timestamp) 
	{
		String message = "<presentation>" +
							"<localDangerWarning id=\"redLightWarning\">";
				
		if(command != null)
			message += 			"<command>" + command + "</command>";
		
		if(priority != null)
			message += 			"<priority>" + priority + "</priority>";
		
		if(timestamp != null)
			message += 			"<timestamp>" + timestamp + "</timestamp>";
	
		message +=			"</localDangerWarning>" +
						"</presentation>";	
						
		HMICenter.sendMsg(message);
	}
}
