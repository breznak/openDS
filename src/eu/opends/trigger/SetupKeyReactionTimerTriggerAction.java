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
 * 
 * @author Rafael Math
 */
public class SetupKeyReactionTimerTriggerAction extends TriggerAction 
{
	private Simulator sim;
	private String timerID;
	private String reactionGroup; 
	private String correctReaction;
	private String failureReaction;
	private String comment;
	
	
	public SetupKeyReactionTimerTriggerAction(float delay, int maxRepeat, String timerID, String reactionGroup, 
			String correctReaction, String failureReaction, String comment, Simulator sim) 
	{
		super(delay, maxRepeat);
		this.sim = sim;
		this.timerID = timerID;
		this.reactionGroup = reactionGroup;
		this.correctReaction = correctReaction;
		this.failureReaction = failureReaction;
		this.comment = comment;
	}

	
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			sim.getReactionCenter().setupKeyReactionTimer(timerID, reactionGroup, correctReaction, 
					failureReaction, comment);
			
			updateCounter();
		}
	}

}
