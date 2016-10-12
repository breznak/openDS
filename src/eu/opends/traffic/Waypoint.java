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

package eu.opends.traffic;

import com.jme3.math.Vector3f;

/**
 * This class represents a way point for traffic participants. Each
 * way point consists of a name, a speed value that the traffic object 
 * is trying to reach (by accelerating/braking) beyond this point and 
 * the position of the way point.
 * 
 * @author Rafael Math
 */
public class Waypoint
{
	private String name;
	private float speed;
	private Vector3f position;
	private String trafficLightID;
	private Float headLightIntensity;
	private String turnSignal;
	private Integer waitingTime;
	
	
	/**
	 * Creates a new way point. Needed parameters
	 * 
	 * @param name
	 * 			Name of the way point.
	 * 
	 * @param position
	 * 			Position of the way point.
	 * 
	 * @param speed
	 * 			Desired speed of traffic object after passing the way point.
	 * 
	 * @param trafficLightID
	 * 			ID of related traffic light (if available, else null)
	 * 
	 * @param turnSignal 
	 * 			state change of turn signal (if available, else null)
	 * 
	 * @param headLightIntensity
	 * 			intensity change of head light (if available, else null) 
	 * 	 
	 * @param waitingTime
	 * 			amount of milliseconds to wait at this waypoint (if available, else null) 
	 */
	public Waypoint(String name, Vector3f position, float speed, String trafficLightID, 
			Float headLightIntensity, String turnSignal, Integer waitingTime) 
	{
		this.name = name;
		this.speed = speed;
		this.position = position;
		this.trafficLightID = trafficLightID;
		this.headLightIntensity = headLightIntensity;
		this.turnSignal = turnSignal;
		this.waitingTime = waitingTime;
	}


	/**
	 * Getter method for the name of the way point
	 * 
	 * @return
	 * 			Name of the way point
	 */
	public String getName() 
	{
		return name;
	}

	
	/**
	 * Getter method for the speed value of the way point
	 * 
	 * @return
	 * 			Speed value that a traffic object is accelerating 
	 * 			or decelerating to beyond this point.
	 */
	public float getSpeed() 
	{
		return speed;
	}

	
	/**
	 * Getter method for the position of the way point
	 * 
	 * @return
	 * 			Position of the way point
	 */
	public Vector3f getPosition() 
	{
		return position;
	}
	

	/**
	 * Getter method for the ID of the related traffic light
	 * 
	 * @return
	 * 			ID of the related traffic light
	 */
	public String getTrafficLightID() 
	{
		return trafficLightID;
	}
	
	
	/**
	 * Getter method for the intensity of head light
	 * 
	 * @return
	 * 			intensity of head light
	 */
	public Float getHeadLightIntensity() 
	{
		return headLightIntensity;
	}
	
	
	/**
	 * Getter method for the state of turn signal
	 * 
	 * @return
	 * 			state of turn signal
	 */
	public String getTurnSignal() 
	{
		return turnSignal;
	}
	
	
	/**
	 * Getter method for the rest time at this way point
	 * 
	 * @return
	 * 			amount of milliseconds to wait at this waypoint
	 */
	public Integer getWaitingTime()
	{
		return waitingTime;
	}
	
	
	/**
	 * String representation of a way point
	 * 
	 * @return
	 * 			String consisting of "name: position"
	 */
	@Override
	public String toString()
	{
		return name + ": " + position;
	}

}
