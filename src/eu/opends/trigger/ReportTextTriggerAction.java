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

import java.util.Date;

import eu.opends.main.Simulator;


/**
 * This class represents a ReportText trigger action. Whenever a collision
 * with a related trigger was detected, the condition will be evaluated
 * and if true, an given text will be written to the log file.
 * 
 * @author Rafael Math
 */
public class ReportTextTriggerAction extends TriggerAction 
{
	private String text;
	private boolean timestamp;
	

	public ReportTextTriggerAction(float delay, int maxRepeat, String text, boolean timestamp) 
	{
		super(delay, maxRepeat);
		this.text = text;
		this.timestamp = timestamp;
	}

	
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			if(timestamp)
				Simulator.getDrivingTaskLogger().reportText(text, new Date());
			else
				Simulator.getDrivingTaskLogger().reportText(text);

			updateCounter();
		}
	}

}