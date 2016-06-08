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

/**
 * 
 * @author Rafael Math
 */
public class TrafficCarData 
{
	private String name;
	private float mass;
	private float acceleration;
	private float decelerationBrake;
	private float decelerationFreeWheel;
	private boolean engineOn;
	private String modelPath;
	private FollowBoxSettings followBoxSettings;
	
	
	public TrafficCarData(String name, float mass, float acceleration,	float decelerationBrake, 
			float decelerationFreeWheel, boolean engineOn, String modelPath, FollowBoxSettings followBoxSettings) 
	{
		this.name = name;
		this.mass = mass;
		this.acceleration = acceleration;
		this.decelerationBrake = decelerationBrake;
		this.decelerationFreeWheel = decelerationFreeWheel;
		this.engineOn = engineOn;
		this.modelPath = modelPath;
		this.followBoxSettings = followBoxSettings;
	}


	public String getName() {
		return name;
	}
	

	public float getMass() {
		return mass;
	}


	public float getAcceleration() {
		return acceleration;
	}


	public float getDecelerationBrake() {
		return decelerationBrake;
	}


	public float getDecelerationFreeWheel() {
		return decelerationFreeWheel;
	}


	public boolean isEngineOn() {
		return engineOn;
	}


	public String getModelPath() {
		return modelPath;
	}


	public FollowBoxSettings getFollowBoxSettings() {
		return followBoxSettings;
	}
	
	

}
