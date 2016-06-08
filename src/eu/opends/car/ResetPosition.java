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

package eu.opends.car;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


/**
 * This class represents a single reset position. Reset positions can be 
 * placed in the map model. Their location and rotation will be stored
 * and can be requested by these methods.
 * 
 * @author Rafael Math
 */
public class ResetPosition 
{
	private String name;
	private Vector3f location;
	private Quaternion rotation;
	
	
	/**
	 * Creates a new reset position containing location and rotation.
	 * 
	 * @param location
	 * 			Location of the reset point, where the car will be placed.
	 * 
	 * @param rotation
	 * 			Rotation of the reset point, which will be applied to the car.
	 */
	public ResetPosition(Vector3f location, Quaternion rotation) 
	{
		this.name = "";
		this.location = location;
		this.rotation = rotation;
	}

	
	/**
	 * Creates a new reset position containing name, location and rotation.
	 * 
	 * @param name
	 * 			Name of the reset point. Used to retrieve a certain point.
	 * 
	 * @param location
	 * 			Location of the reset point, where the car will be placed.
	 * 
	 * @param rotation
	 * 			Rotation of the reset point, which will be applied to the car.
	 */
	public ResetPosition(String name, Vector3f location, Quaternion rotation) 
	{
		this.name = name;
		this.location = location;
		this.rotation = rotation;
	}


	/**
	 * Returns the name of this reset point.
	 * 
	 * @return
	 * 			Name of the reset point.
	 */
	public String getName()
	{
		return name;
	}
	
	
	/**
	 * Returns the location of this reset point as vector.
	 * 
	 * @return
	 * 			Location of the reset point.
	 */
	public Vector3f getLocation()
	{
		return location;
	}
	
	
	/**
	 * Returns the rotation of this reset point as quaternion.
	 * 
	 * @return
	 * 			Rotation of the reset point.
	 */
	public Quaternion getRotation()
	{
		return rotation;
	}
	
	
	/**
	 * String representation, containing location and rotation
	 */
	public String toString()
	{
		return "loc: " + location + ", rot: " + rotation;
	}
}
