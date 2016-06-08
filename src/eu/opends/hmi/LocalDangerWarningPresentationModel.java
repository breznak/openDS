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


import com.jme3.math.Vector3f;

import eu.opends.car.Car;

/**
 * This class represents the presentation of a local danger warning. The distance 
 * between the moving car and a danger area is measured in order to update the distance 
 * information in the presentation.
 * 
 * @author Rafael Math
 */
public class LocalDangerWarningPresentationModel extends PresentationModel 
{
	private String type;
	private int currentPrioritisation;
	
	
	/**
	 * Initializes a local danger warning presentation model by setting the positions 
	 * of the car and danger area, the minimum distance from the danger area to cancel 
	 * the presentation and the name of the warning.
	 * 
	 * @param car
	 * 			Car heading towards the danger area
	 * 
	 * @param targetPosition
	 * 			Position of the danger area's center
	 * 
	 * @param type
	 * 			Name of the local danger warning (internal representation)
	 * 
	 */
	public LocalDangerWarningPresentationModel(Car car, Vector3f targetPosition, String type)
	{
		this.car = car;
		this.targetPosition = targetPosition;
		this.minimumDistance = 10;
		this.type = type;
	}
	
	
	/**
	 * Creates a local danger warning presentation task on the HMI GUI. This
	 * data will only be sent to the HMI bundle once.
	 * 
	 * @return
	 * 			0
	 */
	@Override
	public long createPresentation() 
	{	
		// send parameters to HMI bundle
		currentPrioritisation = 20;
		int length = getRoundedDistanceToTarget(targetPosition);
		sendLocalDangerWarningData("start", currentPrioritisation, System.currentTimeMillis(), null, type, length, length);
		return 0;
	}

	
	/**
	 * Updates a local danger warning presentation model if the time the car takes 
	 * to arrive at the danger area has changed or if the distance to the danger 
	 * area has changed.
	 */
	@Override
	public void updatePresentation(long presentationID) 
	{
		// send parameters to HMI bundle
		currentPrioritisation = 20;
		int remainingDistance = getRoundedDistanceToTarget(targetPosition);
		sendLocalDangerWarningData("update", currentPrioritisation, System.currentTimeMillis(), null, type, null, remainingDistance);
	}


	/**
	 * Generates a message containing type of warning and distance to the danger area.
	 */
	@Override
	public String generateMessage() 
	{
		return "Caution: " + type + " in " + getRoundedDistanceToTarget(targetPosition) + " m";
	}
	
	
	private void sendLocalDangerWarningData(String command, Integer priority, Long timestamp, String infoText, 
			String type, Integer length, Integer distance) 
	{
		// split type, e.g. "localDangerWarning/obstacleWarning/persons"
		String[] typeArray = type.split("/");
		
		if(typeArray.length > 1 && typeArray[1] != null)
		{
			String message = "<presentation>" +
								"<localDangerWarning id=\"" + typeArray[1] + "\">";
					
			if(command != null)
				message += 			"<command>" + command + "</command>";
			
			if(priority != null)
				message += 			"<priority>" + priority + "</priority>";
			
			if(timestamp != null)
				message += 			"<timestamp>" + timestamp + "</timestamp>";
			
			if(infoText != null)
				message += 			"<infoText>" + infoText + "</infoText>";
			
			if(typeArray.length > 2 && typeArray[2] != null)
				message += 			"<type>" + typeArray[2] + "</type>";
			
			if(length != null)
				message += 			"<length>" + length + "</length>";
			
			if(distance != null)
				message += 			"<distance>" + distance + "</distance>";
		
			message +=			"</localDangerWarning>" +
							"</presentation>";	
							
			HMICenter.sendMsg(message);
		}
		else
			System.err.println("LocalDangerWarningPresentationModel: type does not exist (" + type + ").");
	}
	
	
	@Override
	public void stop()
	{
		sendLocalDangerWarningData("stop", 20, System.currentTimeMillis(), null, type, null, null);
	}

}