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

package eu.opends.analyzer;

import java.io.Serializable;
import java.util.Date;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * Data object containing position and speed of the car, as well as the current
 * date. Based on <code>AnalyzationData.java</code> from the CARS-project.
 * 
 * @author Marco Mueller, Rafael Math
 */
public class DataUnit implements Serializable 
{
	private static final long serialVersionUID = -8293989514037755782L;
	private float xpos, ypos, zpos, speed, steeringWheelPos, gasPedalPos, brakePedalPos,
			xrot, yrot, zrot, wrot, traveledDistance;
	private boolean isEngineOn;
	private Date date;
	

	/**
	 * @param date
	 * 			The date, when the data set was taken.
	 * 
	 * @param xpos
	 * 			The position of the car on the x axis.
	 * 
	 * @param ypos
	 * 			The position of the car on the y axis.
	 * 
	 * @param zpos
	 * 			The position of the car on the z axis.
	 * 
	 * @param xrot
	 * 			The rotation of the car (x component of quaternion)
	 * 
	 * @param yrot
	 * 			The rotation of the car (y component of quaternion)
	 * 
	 * @param zrot
	 * 			The rotation of the car (z component of quaternion)
	 * 
	 * @param wrot
	 * 			The rotation of the car (w component of quaternion)
	 * 
	 * @param speed
	 * 			The current speed of the car in kilometers per hour.
	 * 
	 * @param steeringWheelPos
	 * 			The position of the steering wheel: -1 full left, 0 centered,
	 *          1 full right.
	 *            
	 * @param gasPedalPos
	 * 			The position of the gas pedal: 0 no acceleration, 1 full acceleration
	 * 
	 * @param brakePedalPos
	 * 			The position of the brake pedal: -1 full break/negative acceleration, 0 no acceleration
	 * 
	 * @param isEngineOn
	 * 			Engine state
	 */
	public DataUnit(Date date, float xpos, float ypos, float zpos,
			float xrot, float yrot, float zrot, float wrot, float speed,
			float steeringWheelPos, float gasPedalPos, float brakePedalPos,
			boolean isEngineOn) 
	{
		setDate(date);
		setSpeed(speed);
		setXpos(xpos);
		setYpos(ypos);
		setZpos(zpos);
		setXrot(xrot);
		setYrot(yrot);
		setZrot(zrot);
		setWrot(wrot);
		setSteeringWheelPos(steeringWheelPos);
		setAcceleratorPedalPos(gasPedalPos);
		setBrakePedalPos(brakePedalPos);
		setEngineOn(isEngineOn);
	}
	

	/**
	 * @param date
	 * 			The date, when the data set was taken.
	 * 
	 * @param carPosition
	 * 			The position of the car
	 * 			
	 * @param carRotation
	 * 			The rotation of the car
	 * 
	 * @param speed
	 * 			The current speed of the car in kilometers per hour.
	 * 
	 * @param steeringWheelPos
	 * 			The position of the steering wheel: -1 full left, 0 centered,
	 *          1 full right.
	 *          
	 * @param gasPedalPos
	 * 			The position of the gas pedal: 0 no acceleration, 1 full acceleration
	 * 
	 * @param brakePedalPos
	 * 			The position of the brake pedal: -1 full break/negative acceleration, 0 no acceleration
	 * 
	 * @param isEngineOn
	 * 			Engine state
	 * 
	 * @param traveledDistance
	 * 			traveled distance
	 */
	public DataUnit(Date date, Vector3f carPosition, Quaternion carRotation,
			float speed, float steeringWheelPos, float gasPedalPos, float brakePedalPos,
			boolean isEngineOn, float traveledDistance) 
	{
		setDate(date);
		setSpeed(speed);
		setCarPosition(carPosition);
		setCarRotation(carRotation);
		setSteeringWheelPos(steeringWheelPos);
		setAcceleratorPedalPos(gasPedalPos);
		setBrakePedalPos(brakePedalPos);
		setEngineOn(isEngineOn);
		setTraveledDistance(traveledDistance);
	}
	
	
	public Vector3f getCarPosition()
	{
		return new Vector3f(xpos, ypos, zpos);
	}

	
	public void setCarPosition(Vector3f carPosition)
	{
		setXpos(carPosition.getX());
		setYpos(carPosition.getY());
		setZpos(carPosition.getZ());
	}
	
	
	public Quaternion getCarRotation()
	{
		return new Quaternion(xrot, yrot, zrot, wrot);
	}

	
	public void setCarRotation(Quaternion carRotation)
	{
		setXrot(carRotation.getX());
		setYrot(carRotation.getY());
		setZrot(carRotation.getZ());
		setWrot(carRotation.getW());
	}
	
	
	public float getXrot() {
		return xrot;
	}

	private void setXrot(float xrot) {
		this.xrot = xrot;
	}

	public float getYrot() {
		return yrot;
	}

	private void setYrot(float yrot) {
		this.yrot = yrot;
	}

	public float getZrot() {
		return zrot;
	}

	private void setZrot(float zrot) {
		this.zrot = zrot;
	}

	public float getWrot() {
		return wrot;
	}

	private void setWrot(float wrot) {
		this.wrot = wrot;
	}

	/**
	 * 
	 * @return The position of the car on the x axis.
	 */
	public float getXpos() {
		return xpos;
	}

	/**
	 * @param xpos
	 *            The position of the car on the x axis.
	 */
	private void setXpos(float xpos) {
		this.xpos = xpos;
	}

	/**
	 * 
	 * @return The position of the car on the y axis.
	 */
	public float getYpos() {
		return ypos;
	}

	/**
	 * 
	 * @param ypos
	 *            The position of the car on the y axis.
	 */
	private void setYpos(float ypos) {
		this.ypos = ypos;
	}

	/**
	 * 
	 * @return The speed of the car in kilometers per hour.
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * 
	 * @param speed
	 *            The speed of the car in kilometers per hour.
	 */
	private void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * @return The date set for the data set.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * 
	 * @param date
	 *            The date of the data set.
	 */
	private void setDate(Date date) {
		this.date = date;
	}

	/**
	 * 
	 * @return The position of the car on the z axis.
	 */
	public float getZpos() {
		return zpos;
	}

	/**
	 * 
	 * @param zpos
	 *            The position of the car on the z axis.
	 */
	private void setZpos(float zpos) {
		this.zpos = zpos;
	}

	/**
	 * @return The position of the steering wheel: -1 full left, 0 centered, 1
	 *         full right.
	 */
	public float getSteeringWheelPos() {
		return steeringWheelPos;
	}

	/**
	 * 
	 * @param steeringWheelPos
	 *            The position of the steering wheel: -1 full left, 0 centered,
	 *            1 full right.
	 */
	private void setSteeringWheelPos(float steeringWheelPos) {
		this.steeringWheelPos = steeringWheelPos;
	}

	/**
	 * 
	 * @return The position of the pedals: -1 full break/negative acceleration,
	 *         0 no acceleration, 1 full acceleration
	 */
	public float getAcceleratorPedalPos() {
		return gasPedalPos;
	}

	/**
	 * 
	 * @param pedalPos
	 *            The position of the pedals: -1 full break/negative
	 *            acceleration, 0 no acceleration, 1 full acceleration
	 */
	private void setAcceleratorPedalPos(float pedalPos) {
		this.gasPedalPos = pedalPos;
	}


	/**
	 * 
	 * @return true if the car is breaking, else false
	 */
	public float getBrakePedalPos() {
		return brakePedalPos;
	}

	/**
	 * 
	 * @param brakePedalPos
	 *            true if the car is breaking, else false
	 */
	private void setBrakePedalPos(float brakePedalPos) {
		this.brakePedalPos = brakePedalPos;
	}

	
	/**
	 * 
	 * @return true if the car's engine is running
	 */
	public boolean isEngineOn() {
		return isEngineOn;
	}

	/**
	 * 
	 * @param isEngineOn
	 *            true if the car's engine is running
	 */
	private void setEngineOn(boolean isEngineOn) {
		this.isEngineOn = isEngineOn;
	}

	
	/**
	 * 
	 * @return distance car has traveled since start of recording
	 */
	public float getTraveledDistance() {
		return traveledDistance;
	}

	/**
	 * 
	 * @param traveledDistance
	 *            distance car has traveled since start of recording
	 */
	private void setTraveledDistance(float traveledDistance) {
		this.traveledDistance = traveledDistance;
	}
	
	
	public static DataUnit interpolate(DataUnit previousDataUnit, DataUnit nextDataUnit, long currentRecordingTime) 
	{
		// time at previous recorded data unit
		long timeAtPreviousTarget = previousDataUnit.getDate().getTime();
		
		// time at next recorded data unit
		long timeAtNextTarget = nextDataUnit.getDate().getTime();
		
		// current progress (%) between previous and next recorded data unit
		long timeElapsedSincePreviousTarget = currentRecordingTime - timeAtPreviousTarget;
		long totalTimeBetweenTargets = timeAtNextTarget -  timeAtPreviousTarget;
		float percentage = (float) timeElapsedSincePreviousTarget/totalTimeBetweenTargets;
		
		
		// save interpolated time stamp
		Date date = new Date(currentRecordingTime);
		
		
		// interpolate position
		Vector3f previousCarPos = previousDataUnit.getCarPosition();
		Vector3f nextCarPos = nextDataUnit.getCarPosition();
		Vector3f track = nextCarPos.subtract(previousCarPos);
		Vector3f position = previousCarPos.add(track.multLocal(percentage));


		// interpolate rotation
		Quaternion previousCarRot = previousDataUnit.getCarRotation();
		Quaternion nextCarRot = nextDataUnit.getCarRotation();
		
		float[] previousAngles = previousCarRot.toAngles(null);
		float[] nextAngles = nextCarRot.toAngles(null);
		float[] currentAngles = new float [3];
		
		for(int i=0; i<3; i++)
		{
			// normalize all values to range 0 - 2*PI
			previousAngles[i] = (previousAngles[i] + FastMath.TWO_PI) % FastMath.TWO_PI;
			nextAngles[i] = (nextAngles[i] + FastMath.TWO_PI) % FastMath.TWO_PI;
			
			// If one angle smaller than 2*PI (360 degrees) and the other greater
			// add 2*PI (360 degrees) to the smaller angle. This avoids jumping.
			if(Math.abs(nextAngles[i] - previousAngles[i]) > FastMath.PI)
				if(nextAngles[i] < previousAngles[i])
					nextAngles[i] += FastMath.TWO_PI;
				else
					previousAngles[i] += FastMath.TWO_PI;
			
			// intermediate angle according to progress between previous and next angle
			currentAngles[i] = previousAngles[i] + ((nextAngles[i] - previousAngles[i]) * percentage);
		}	
		Quaternion rotation = (new Quaternion()).fromAngles(currentAngles);
		
		
		// interpolate speed		
		float previousSpeed = previousDataUnit.getSpeed();
		float nextSpeed = nextDataUnit.getSpeed();
		float speedDiff = nextSpeed - previousSpeed;
		float speed = previousSpeed + (speedDiff * percentage);
		
		
		// interpolate steering wheel position
		float previousSWPos = previousDataUnit.getSteeringWheelPos();
		float nextSWPos = nextDataUnit.getSteeringWheelPos();
		float sWPosDiff = nextSWPos - previousSWPos;
		float steeringWheelPos = previousSWPos + (sWPosDiff * percentage);
		

		// interpolate accelerator pedal position
		float previousAccPos = previousDataUnit.getAcceleratorPedalPos();
		float nextAccPos = nextDataUnit.getAcceleratorPedalPos();
		float accPosDiff = nextAccPos - previousAccPos;
		float gasPedalPos = previousAccPos + (accPosDiff * percentage);

		
		// interpolate brake pedal position
		float previousBrakePos = previousDataUnit.getBrakePedalPos();
		float nextBrakePos = nextDataUnit.getBrakePedalPos();
		float brakePosDiff = nextBrakePos - previousBrakePos;
		float brakePedalPos = previousBrakePos + (brakePosDiff * percentage);
		
		
		// pass previous engine state
		boolean isEngineOn = previousDataUnit.isEngineOn();
		
		
		// interpolate traveled distance
		float previousTraveledDistance = previousDataUnit.getTraveledDistance();
		float nextTraveledDistance = nextDataUnit.getTraveledDistance();
		float traveledDistanceDiff = nextTraveledDistance - previousTraveledDistance;
		float traveledDistance = previousTraveledDistance + (traveledDistanceDiff * percentage);		
		
		return new DataUnit(date, position, rotation, speed, steeringWheelPos, 
				gasPedalPos, brakePedalPos, isEngineOn, traveledDistance);
	}
}
