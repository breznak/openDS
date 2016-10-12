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

import eu.opends.car.Car;;


/**
 * This class represents the presentation of a construction site warning. The distance 
 * between the moving car and a construction site is measured in order to update the 
 * distance information in the presentation. 
 * 
 * @author Rafael Math
 */
public class RoadWorksInformationPresentationModel extends PresentationModel 
{
	private Vector3f roadWorksStartPosition;
	private boolean reachedRoadWorksStartPosition;
	private float previousDistanceToStart;

	
	/**
	 * Initializes a construction site presentation model by setting the positions of the 
	 * car and the end of the construction site, the minimum distance from the construction
	 * site's end to cancel the presentation and the ID of the construction site.
	 * 
	 * @param car
	 * 			Car heading towards the construction site
	 * 
	 * @param roadWorksStartPosition
	 *  		Position where of the construction site begins
	 *  
	 * @param roadWorksEndPosition
	 * 			Position where the construction site ends
	 */
	public RoadWorksInformationPresentationModel(Car car, Vector3f roadWorksStartPosition, 
			Vector3f roadWorksEndPosition)
	{
		// fixed parameters
		this.car = car;
		this.targetPosition = roadWorksEndPosition;
		this.roadWorksStartPosition = roadWorksStartPosition;
	}
	
	
	/**
	 * Creates a road works presentation task on the HMI GUI. This
	 * data will only be sent to the HMI bundle once.
	 * 
	 * @return
	 * 			0
	 */
	@Override
	public long createPresentation()
	{
		return 0;
	}


	/**
	 * Updates a road works presentation model if the time the car takes to arrive at the 
	 * construction site's end has changed or if the distance to the end of the site has 
	 * changed.
	 */
	@Override
	public void updatePresentation(long presentationID) 
	{
	}

	
	/**
	 * Checks whether the moving car has already reached the beginning of the road works 
	 * site by comparing the distance to the start position with the distance from the 
	 * previous request. If the distance increased since last request, the start position
	 * lies behind the car and true will be returned for every following request.
	 * 
	 * @return
	 * 			True, if the moving car has already reached the start position of the
	 * 			road works site.
	 */
	private boolean reachedRoadWorks() 
	{
		if(reachedRoadWorksStartPosition)
			return true;
		else
		{
			float currentDistanceToStart = getExactDistanceToTarget(roadWorksStartPosition);
			if(previousDistanceToStart < currentDistanceToStart)
				reachedRoadWorksStartPosition = true;
			
			previousDistanceToStart = currentDistanceToStart;
			
			return reachedRoadWorksStartPosition;
		}
	}


	/**
	 * Generates a message containing the construction site warning and distance to it 
	 * or its end, respectively.
	 */
	@Override
	public String generateMessage() 
	{
		if(!reachedRoadWorks())
			return "Road works in " + getRoundedDistanceToTarget(roadWorksStartPosition) + " m";
		else
			return "Road works remaining for " + getRoundedDistanceToTarget(targetPosition) + " m";
	}

}