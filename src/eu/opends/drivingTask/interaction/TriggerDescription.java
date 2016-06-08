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

import java.util.List;

/**
 * 
 * @author Rafael Math
 */
public class TriggerDescription 
{
	private String name;
	private int priority;
	private String condition;
	private List<String> activityRefList;
	
	
	public TriggerDescription(String name, int priority, String condition, List<String> activityRefList) 
	{
		this.name = name;
		this.priority = priority;
		this.condition = condition;
		this.activityRefList = activityRefList;
	}


	/**
	 * @return the name
	 */
	public String getName() 
	{
		return name;
	}


	/**
	 * @return the priority
	 */
	public int getPriority() 
	{
		return priority;
	}


	/**
	 * @return the condition
	 */
	public String getCondition() 
	{
		return condition;
	}


	/**
	 * @return the activityRefList
	 */
	public List<String> getActivityRefList() 
	{
		return activityRefList;
	}

	
}
