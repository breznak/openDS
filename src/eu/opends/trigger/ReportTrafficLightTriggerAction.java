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

package eu.opends.trigger;

import eu.opends.environment.TrafficLight;
import eu.opends.environment.TrafficLightCenter;
import eu.opends.main.Simulator;


/**
 * This class represents a ReportTrafficLight trigger action. Whenever a collision
 * with a related trigger was detected, the given condition will be evaluated and
 * and if true, an entry will be written to the log file.
 * 
 * @author Rafael Math
 */
public class ReportTrafficLightTriggerAction extends TriggerAction 
{
	private String trafficLightID;
	private String targetTrafficLightState;
	
	
	public ReportTrafficLightTriggerAction(float delay, int maxRepeat, String trafficLightID, 
			String trafficLightState) 
	{
		super(delay, maxRepeat);
		this.trafficLightID = trafficLightID;
		this.targetTrafficLightState = trafficLightState;
	}

	
	@Override
	protected void execute()
	{
		if(!isExceeded())
		{		
			TrafficLight trafficLight = TrafficLightCenter.getTrafficLightByName(trafficLightID);
			if(trafficLight != null && 
					(targetTrafficLightState.equalsIgnoreCase(trafficLight.getState().toString()))
				)
			{
				Simulator.getDrivingTaskLogger().reportText("Car has passed trigger while traffic light '" + 
						trafficLightID + "' was " + targetTrafficLightState + ".");
			}
			
			updateCounter();
		}
	}

}
