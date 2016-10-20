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
 * Abstract class used to compute the distance between the moving car and any 
 * target position. The target position is further described in its subclasses.
 * 
 * @author Rafael Math
 */
public abstract class PresentationModel 
{
	protected Car car;
	protected Vector3f targetPosition;
	protected int minimumDistance;
	protected int previousRoundedDist;
	protected int currentRoundedDist;
	protected static final int precisionFactor = 1;
	
	
	/**
	 * Computes the exact distance between the car and the given target 
	 * position (sign, obstacle, traffic light, ...) with float precision.
	 * 
	 * @param targetPosition
	 * 			Position of the target.
	 * 
	 * @return
	 * 			Exact distance between car and target in meters.
	 */
	public float getExactDistanceToTarget(Vector3f targetPosition)
	{
		Vector3f carPosition  = car.getPosition();
		float distance   = targetPosition.subtract(carPosition).length();
		
		return distance;
	}
	
	
	/**
	 * Computes the rounded distance between the car and the given target 
	 * position (sign, obstacle, traffic light, ...) as multiple of 
	 * precisionFactor.
	 *
	 * @param targetPosition
	 * 			Position of the target.
	 * 
	 * @return
	 * 			Rounded distance between car and target in meters
	 */
	public int getRoundedDistanceToTarget(Vector3f targetPosition)
	{
		float exactDistance = getExactDistanceToTarget(targetPosition);
		
		// round exact distance to a multiple of precisionFactor	
		int roundedDistance  = Math.round((exactDistance/precisionFactor))*precisionFactor;
		
		return roundedDistance;
	}
	
	
	/**
	 * Computes the time (in milliseconds) which is needed by the car to arrive at the 
	 * target position.
	 * 
	 * @param targetPosition
	 * 			Position of the target.
	 * 
	 * @return
	 * 			Time to target in milliseconds
	 */
	public float getTimeToTarget(Vector3f targetPosition)
	{
		// get distance in meters
		float exactDistance = getExactDistanceToTarget(targetPosition);
		
		// get speed in meters per second
		float speed = car.getCurrentSpeedMs();
		
		// avoid division by zero
		if(speed == 0f)
			speed = 0.000001f;
		
		// time = distance/speed
		float secondsToTarget = exactDistance/speed;

		// multiply with 1000 for milliseconds
		float millisecondsToTarget = secondsToTarget * 1000;
		
		// prevent a negative time value
		return Math.max(0, millisecondsToTarget);
	}

	
	/**
	 * Sets variable car to the given value.
	 * 
	 * @param car
	 * 			Car to generate a presentation task.
	 */
	public void setCar(Car car)
	{
		this.car = car;
	}

	
	/**
	 * Condition to stop presentation. Returns true, if approximation of the car is 
	 * less than the given minimum distance or if the car is driving backwards.
	 * 
	 * @return
	 * 			True, if stop condition holds
	 */
	public boolean stopPresentation() 
	{
		boolean minimumDistanceReached = (currentRoundedDist <= minimumDistance);
		boolean isDrivingBackwards     = (currentRoundedDist > previousRoundedDist);
		
		return (minimumDistanceReached || isDrivingBackwards);
	}


	/**
	 * Sets previous parameters relevant for updates
	 */
	public void computePreviousParameters() 
	{
		previousRoundedDist = getRoundedDistanceToTarget(targetPosition);
	}


	/**
	 * Sets current parameters relevant for updates
	 */
	public void computeCurrentParameters() 
	{
		currentRoundedDist = getRoundedDistanceToTarget(targetPosition);
	}


	/**
	 * Compares previous to current parameters
	 * 
	 * @return
	 * 			True, if any previous parameter differs from the corresponding 
	 * 			current parameter
	 */
	public boolean hasChangedParameter() 
	{
		return (currentRoundedDist != previousRoundedDist);
	}


	/**
	 * Assign the values of all current parameters to the corresponding previous 
	 * parameters
	 */
	public void shiftCurrentToPreviousParameters() 
	{
		previousRoundedDist = currentRoundedDist;
	}

	
	/**
	 * Creates a presentation task on the HMI GUI. The data will only be 
	 * sent to the HMI bundle once.
	 * 
	 * @return
	 * 			Presentation ID. If an error occurred, a negative value will be 
	 * 			returned.
	 */
	public abstract long createPresentation();
	
	
	/**
	 * Updates a presentation task on the HMI GUI.
	 * 
	 * @param presentationID
	 * 			ID of presentation task to update.
	 */
	public abstract void updatePresentation(long presentationID);

	
	/**
	 * Generates a message containing type of warning and distance to the target
	 * 
	 * @return
	 * 			Message containing type of warning and distance to the target.
	 */
	public abstract String generateMessage();


	public void stop() 
	{
	}
	
}
