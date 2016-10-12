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

package eu.opends.drivingTask.interaction;

import java.util.Properties;

/**
 * 
 * @author Rafael Math
 */
public class ActionDescription 
{
	private String name;
	private float delay;
	private int repeat;
	private Properties parameterList;
	
	
	public ActionDescription(String name, float delay, int repeat, Properties parameterList) 
	{
		this.name = name;
		this.delay = delay;
		this.repeat = repeat;
		this.parameterList = parameterList;
	}

	
	/**
	 * @return the name
	 */
	public String getName() 
	{
		return name;
	}
	

	/**
	 * @return the delay
	 */
	public float getDelay() 
	{
		return delay;
	}


	/**
	 * @return the repeat
	 */
	public int getRepeat() 
	{
		return repeat;
	}


	/**
	 * @return the parameterList
	 */
	public Properties getParameterList() 
	{
		return parameterList;
	}
	
	
	public String toString()
	{
		return name + " - " + delay + " - " + repeat + " - [" + parameterList.toString() + "]"; 
	}

}
