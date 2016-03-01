/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2015 Rafael Math
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

import java.util.HashMap;

import com.jme3.math.Vector3f;

import eu.opends.car.Car;


/**
 * This class represents the presentation of a speed limit warning. The distance 
 * between the moving car and a speed limit sign is measured in order to update the 
 * distance information in the presentation. 
 * 
 * @author Rafael Math
 */
public class SpeedLimitPresentationModel extends PresentationModel 
{
	private String speedlimit;
	
	
	/**
	 * Initializes a speed limit presentation model by setting the positions of the 
	 * car and the speed limit sign, the minimum distance from the speed limit sign
	 * to cancel the presentation and the value of the speed limit.
	 * 
	 * @param car
	 * 			Car heading towards the speed limit
	 * 
	 * @param signPosition
	 * 			Position of the speed limit sign
	 * 
	 * @param speedlimit
	 * 			Value of the speed limit
	 */
	public SpeedLimitPresentationModel(Car car, Vector3f signPosition, String speedlimit)
	{
		this.car = car;
		this.targetPosition = signPosition;
		this.speedlimit = speedlimit;
		this.minimumDistance = 10;
	}
	
	
	/**
	 * Creates a speed limit presentation task on the SIM-TD HMI GUI. This
	 * data will only be sent to the HMI bundle once.
	 * 
	 * @return
	 * 			Presentation ID. If an error occurred, a negative value will be returned.
	 */
	@Override
	public long createPresentation() 
	{
		//TODO create SIM-TD presentation task
		return -1;
	}
	
	
	/**
	 * Updates a speed limit presentation model if the time the car takes to arrive at the 
	 * speed limit sign has changed or if the distance to it has changed.
	 */
	@Override
	public void updatePresentation(long presentationID) 
	{
		HashMap<String, Object> parameters = new HashMap<String, Object>();

		// parameters according to "presentationModel.xml"
		parameters.put("timeEnd", System.currentTimeMillis() + (long) getTimeToTarget(targetPosition));
		
		// parameters according to "..."
		//parameters.put("distanceToEvent", getRoundedDistanceToTarget());
		
		// send parameters to HMI bundle
		// TODO controller.update(presentationID, parameters);
	}

	
	/**
	 * Generates a message containing the speed limit warning and distance to the sign.
	 */
	@Override
	public String generateMessage() 
	{
		return "Speed limit " + speedlimit + " km/h in "+ getRoundedDistanceToTarget(targetPosition) + " m";
	}
}
