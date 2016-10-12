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

import eu.opends.car.Car;
import eu.opends.main.Simulator;
import eu.opends.tools.SpeedControlCenter;

/**
 * 
 * @author Rafael Math
 */
public class GetTimeUntilSpeedChangeAction extends TriggerAction 
{
	private String triggerName;
	private int speedChange;
	private Simulator sim;
	
	public GetTimeUntilSpeedChangeAction(float delay, int maxRepeat, String triggerName, int speedChange, Simulator sim)
	{
		super(delay, maxRepeat);
		this.triggerName = triggerName;
		this.speedChange = speedChange;
		this.sim = sim;
	}

	
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			Car car = sim.getCar();
			SpeedControlCenter.startSpeedChangeTimer(triggerName, speedChange, car);

			updateCounter();
		}

	}

}
