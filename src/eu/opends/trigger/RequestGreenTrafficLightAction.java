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

import eu.opends.environment.TrafficLightCenter.TriggerType;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class RequestGreenTrafficLightAction extends TriggerAction 
{
	private Simulator sim;
	private String trafficLightName;
	
	
	public RequestGreenTrafficLightAction(float delay, int maxRepeat, Simulator sim, String trafficLightID)
	{
		super(delay, maxRepeat);
		
		this.sim = sim;
		this.trafficLightName = trafficLightID;
	}
	
	
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			sim.getTrafficLightCenter().reportCollision(trafficLightName, TriggerType.REQUEST);
				
			updateCounter();
		}
	}

}
