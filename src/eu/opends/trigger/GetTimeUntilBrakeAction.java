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

import eu.opends.tools.SpeedControlCenter;

/**
 * 
 * @author Rafael Math
 */
public class GetTimeUntilBrakeAction extends TriggerAction 
{
	private String triggerName;
	
	
	public GetTimeUntilBrakeAction(float delay, int maxRepeat, String triggerName) 
	{
		super(delay, maxRepeat);
		this.triggerName = triggerName;
	}
	

	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			SpeedControlCenter.startBrakeTimer(triggerName);
				
			updateCounter();
		}
	}

}
