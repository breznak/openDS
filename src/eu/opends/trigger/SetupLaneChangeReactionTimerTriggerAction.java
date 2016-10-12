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
public class SetupLaneChangeReactionTimerTriggerAction extends TriggerAction
{
	private Simulator sim;
	private String timerID;
	private String reactionGroupID;
	private String startLane;
	private String targetLane;
	private float minSteeringAngle;
	private float taskCompletionAfterTime;
	private float taskCompletionAfterDistance;
	private boolean allowBrake;
	private float holdLaneFor;
	private String failSound;
	private String successSound;
	private String comment;
	
	
	public SetupLaneChangeReactionTimerTriggerAction(float delay, int maxRepeat, String timerID, String reactionGroupID, 
			String startLane, String targetLane, float minSteeringAngle, float taskCompletionAfterTime, 
			float taskCompletionAfterDistance, boolean allowBrake, float holdLaneFor, String failSound,
			String successSound, String comment, Simulator sim) 
	{
		super(delay, maxRepeat);
		
		this.timerID = timerID;
		this.reactionGroupID = reactionGroupID;
		this.startLane = startLane;
		this.targetLane = targetLane;
		this.minSteeringAngle = minSteeringAngle;
		this.taskCompletionAfterTime = taskCompletionAfterTime;
		this.taskCompletionAfterDistance = taskCompletionAfterDistance;
		this.allowBrake = allowBrake;
		this.holdLaneFor = holdLaneFor;
		this.failSound = failSound;
		this.successSound = successSound;
		this.comment = comment;
		this.sim = sim;
	}

	
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			sim.getReactionCenter().setupLaneChangeReactionTimer(timerID, reactionGroupID, startLane, targetLane,
					minSteeringAngle, taskCompletionAfterTime, taskCompletionAfterDistance, allowBrake, holdLaneFor,
					failSound, successSound, comment);
			
			updateCounter();
		}
	}

}
