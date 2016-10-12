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

package eu.opends.drivingTask.scenario;

import java.util.LinkedList;
import java.util.List;

import eu.opends.environment.TrafficLight;
import eu.opends.environment.TrafficLightPhase;

public class Intersection 
{
	private String intersectionID;
	private String trafficLightMode;
	private LinkedList<TrafficLightPhase> trafficLightPhaseList;
	private List<TrafficLight> trafficLightList;
	
	
	public Intersection(String intersectionID, String trafficLightMode,	LinkedList<TrafficLightPhase> trafficLightPhaseList,
			List<TrafficLight> trafficLightList) 
	{
		this.intersectionID = intersectionID;
		this.trafficLightMode = trafficLightMode;
		this.trafficLightPhaseList = trafficLightPhaseList;
		this.trafficLightList = trafficLightList;
	}


	public String getIntersectionID() 
	{
		return intersectionID;
	}


	public String getTrafficLightMode() 
	{
		return trafficLightMode;
	}


	public LinkedList<TrafficLightPhase> getTrafficLightPhaseList() 
	{
		return trafficLightPhaseList;
	}


	public List<TrafficLight> getTrafficLightList()
	{
		return trafficLightList;
	}

}
