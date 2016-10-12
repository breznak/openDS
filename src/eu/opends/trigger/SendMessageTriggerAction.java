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

import eu.opends.tools.PanelCenter;


/**
 * This class represents a SendMessage trigger action. Whenever a collision
 * with a related trigger was detected, a specified message will be displayed
 * on the simulator screen for the given number of seconds.
 * 
 * @author Rafael Math
 */
public class SendMessageTriggerAction extends TriggerAction 
{
	String message;
	int duration;
	
	
	/**
	 * Creates a new SendMessage trigger action instance, providing message 
	 * text and duration.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param maxRepeat
	 * 			Maximum number how often the trigger can be hit (0 = infinite).
	 * 
	 * @param message
	 * 			Message text which will be displayed on trigger collision.
	 * 
	 * @param duration
	 * 			Number of seconds the message will be displayed.
	 */
	public SendMessageTriggerAction(float delay, int maxRepeat, String message, int duration) 
	{
		super(delay, maxRepeat);
		this.message = message;
		this.duration = duration;
	}

	
	/**
	 * Displays the specified message on the simulator screen for the 
	 * given number of seconds.
	 */
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			PanelCenter.getMessageBox().addMessage(message,duration);
			updateCounter();
		}
	}
	
	
	@Override
	public String toString()
	{
		return "SendMessageTriggerAction: \'" + message + "\', duration: " + duration;
	}
}
