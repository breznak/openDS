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

import eu.opends.main.Simulator;


/**
 * This class represents a ReportSpeed trigger action. Whenever a collision
 * with a related trigger was detected, the condition will be evaluated
 * and if true, an entry will be written to the log file.
 * 
 * @author Rafael Math
 */
public class ReportSpeedTriggerAction extends TriggerAction 
{
	private String type;
	private float targetSpeed;
	private Simulator sim;
	

	public ReportSpeedTriggerAction(float delay, int maxRepeat, String type, float targetSpeed, Simulator sim) 
	{
		super(delay, maxRepeat);
		this.type = type;
		this.targetSpeed = targetSpeed;
		this.sim = sim;
	}

	
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			float currentSpeed = sim.getCar().getCurrentSpeedKmh();
			
			if(type.equalsIgnoreCase("greaterThan") && (currentSpeed > targetSpeed))
				Simulator.getDrivingTaskLogger().reportText("Car exceeded maximum speed of " + 
						targetSpeed + "km/h (Driven speed: " + currentSpeed + " km/h)");
			else if (type.equalsIgnoreCase("lessThan") && (currentSpeed < targetSpeed))
				Simulator.getDrivingTaskLogger().reportText("Car undershot minimum speed of " + 
						targetSpeed + "km/h (Driven speed: " + currentSpeed + " km/h)");

			updateCounter();
		}
	}

}
