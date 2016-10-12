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

package eu.opends.trafficObjectLocator;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * 
 * @author Rafael Math
 */
public class TrafficObject 
{
	private String name;
	private String path; 
	private Vector3f translation;
	private Quaternion rotation; 
	private Vector3f scale;
	private int counter = 0;
	
	
	public TrafficObject(String name, String path, Vector3f translation, Quaternion rotation, Vector3f scale) 
	{
		this.name = name;
		this.path = path;
		this.translation = translation;
		this.rotation = rotation;
		this.scale = scale;
	}


	/**
	 * @return the name
	 */
	public String getName() 
	{
		return name;
	}


	/**
	 * @return the path
	 */
	public String getPath() 
	{
		return path;
	}


	/**
	 * @return the translation
	 */
	public Vector3f getTranslation() 
	{
		return translation;
	}


	/**
	 * @return the rotation
	 */
	public Quaternion getRotation() 
	{
		return rotation;
	}


	/**
	 * @return the scale
	 */
	public Vector3f getScale() 
	{
		return scale;
	}
	
	
	/**
	 * @return the counter
	 */
	public int incCounter() 
	{
		return ++counter;
	}
	

}
