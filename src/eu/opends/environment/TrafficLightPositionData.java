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

package eu.opends.environment;


/**
 * This class provides information of a traffic light about the positioning 
 * at an intersection. It extends the assignment of a traffic light to an 
 * intersectionID an assignment to a more specific roadID. Each road of an 
 * intersection may have a certain type of crossing and arrow configuration.
 * Furthermore the lane (starting with 0 from the right to the middle of the 
 * road) a traffic light is positioned in can be described.
 * 
 * @author Rafael Math
 */
public class TrafficLightPositionData 
{
	private String roadID;
	private int crossingType;
	private int arrowType;
	private int lane;
	
	
	/**
	 * Creates a new position data set for a traffic light.
	 * 
	 * @param roadID
	 * 			Unique ID of a road leading to an intersection
	 * 
	 * @param crossingType
	 * 			Type of crossing from this road's point of view
	 * 
	 * @param arrowType
	 * 			Arrow configuration of the given road
	 * 
	 * @param lane
	 * 			Position of the lane in the given road where the traffic light is located
	 */
	public TrafficLightPositionData(String roadID, int crossingType, int arrowType, int lane)
	{
		this.roadID = roadID;
		this.crossingType = crossingType;
		this.arrowType = arrowType;
		this.lane = lane;
	}

	
	/**
	 * Returns the unique ID of a road leading to an intersection
	 * 
	 * @return
	 * 			Road's unique ID
	 */
	public String getRoadID() 
	{
		return roadID;
	}

	
	/**
	 * Returns the type of crossing from this road's point of view
	 * 
	 * @return
	 * 			Crossing type
	 */
	public int getCrossingType() 
	{
		return crossingType;
	}

	
	/**
	 * Returns the arrow configuration of the given road
	 * 
	 * @return 
	 * 			Arrow type
	 */
	public int getArrowType() 
	{
		return arrowType;
	}

	
	/**
	 * Returns the position of the lane in the given road where the traffic light is located
	 * 
	 * @return 
	 * 			Lane in road
	 */
	public int getLane() 
	{
		return lane;
	}
	
}
