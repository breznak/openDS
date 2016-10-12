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

package eu.opends.reactionCenter;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import com.jme3.input.InputManager;

import eu.opends.jasperReport.ReactionLogger;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class ReactionCenter
{
	private Simulator sim;
	private InputManager inputManager;
	private ReactionListener reactionListener;
	private ReactionLogger reactionLogger = new ReactionLogger();
	private long experimentStartTime;
	
	private boolean isRunning = false;
	private List<ReactionTimer> reactionTimerList = new ArrayList<ReactionTimer>();
	
	
	public ReactionCenter(Simulator sim)
	{
		this.sim = sim;
		this.inputManager = sim.getInputManager();
	}

	
	public void start()
	{
		if(!isRunning)
		{			
			reactionListener = new ReactionListener(this);
			
			experimentStartTime = new GregorianCalendar().getTimeInMillis();
			isRunning = true;
			
			System.err.println("Start");
			Simulator.getDrivingTaskLogger().reportText("trial;vpn;age;gender;task;task_detail;condition_num;" +
					"condition_string;track;accuracy;additional_false_r;RT_brake1;RT_brake2;RT_brake3;RT_brake4;" +
					"RT_change1;RT_change2;RT_change3;RT_change4");
		}
	}

	
	public void setupKeyReactionTimer(String timerID, String reactionGroupID, String correctReaction, 
			String failureReaction,	String comment)
	{
		if(isRunning)
		{
			// if timerID is not contained in reactionTimerList
			ReactionTimer reactionTimer = getReactionTimer(timerID);
			if(reactionTimer == null)
			{
				// create new timer with increased index
				int index = reactionTimerList.size();
				reactionTimer = new KeyReactionTimer(sim, inputManager, reactionListener,
						reactionLogger, experimentStartTime, timerID, index);

				// add timer to timer list
				reactionTimerList.add(reactionTimer);
			}
			
			if(!(reactionTimer instanceof KeyReactionTimer))
			{
				// get index of previous reaction timer
				int index = reactionTimer.getIndex();
				
				// close previous reaction timer and remove from list
				reactionTimer.close();
				reactionTimerList.remove(reactionTimer);
				
				//convert to KeyReactionTimer
				reactionTimer = new KeyReactionTimer(sim, inputManager, reactionListener,
						reactionLogger, experimentStartTime, timerID, index);
				reactionTimerList.add(reactionTimer);
			}
			
			// setup reaction timer
			((KeyReactionTimer)reactionTimer).setup(reactionGroupID, correctReaction, failureReaction, comment);
		}
		else
			System.err.println("Make sure ReactionCenter has been started");
	}
	
	
	public void setupLaneChangeReactionTimer(String timerID, String reactionGroupID, String startLane, 
			String targetLane, float minSteeringAngle, float taskCompletionAfterTime, 
			float taskCompletionAfterDistance, boolean allowBrake, float holdLaneFor, String failSound, 
			String successSound, String comment)
	{
		if(isRunning)
		{
			// if timerID is not contained in reactionTimerList
			ReactionTimer reactionTimer = getReactionTimer(timerID);
			if(reactionTimer == null)
			{
				// create new timer with increased index
				int index = reactionTimerList.size();
				reactionTimer = new LaneChangeReactionTimer(sim, reactionLogger, experimentStartTime, timerID, index);

				// add timer to timer list
				reactionTimerList.add(reactionTimer);
			}
			
			if(!(reactionTimer instanceof LaneChangeReactionTimer))
			{
				// get index of previous reaction timer
				int index = reactionTimer.getIndex();
				
				// close previous reaction timer and remove from list
				reactionTimer.close();
				reactionTimerList.remove(reactionTimer);
				
				//convert to LaneChangeReactionTimer
				reactionTimer = new LaneChangeReactionTimer(sim, reactionLogger, experimentStartTime, timerID, index);
				reactionTimerList.add(reactionTimer);
			}

			// setup reaction timer
			((LaneChangeReactionTimer)reactionTimer).setup(reactionGroupID, startLane, targetLane, minSteeringAngle,
					taskCompletionAfterTime, taskCompletionAfterDistance, allowBrake, holdLaneFor, failSound, 
					successSound, comment);
		}
		else
			System.err.println("Make sure ReactionCenter has been started");
	}
	
	
	public void setupBrakeReactionTimer(String timerID, String reactionGroupID,	float startSpeed, 
			float targetSpeed, boolean mustPressBrakePedal,	float taskCompletionAfterTime, 
			float taskCompletionAfterDistance, boolean allowLaneChange, float holdSpeedFor, 
			String failSound, String successSound, String comment) 
	{
		if(isRunning)
		{
			// if timerID is not contained in reactionTimerList
			ReactionTimer reactionTimer = getReactionTimer(timerID);
			if(reactionTimer == null)
			{
				// create new timer with increased index
				int index = reactionTimerList.size();
				reactionTimer = new BrakeReactionTimer(sim, reactionLogger, experimentStartTime, timerID, index);

				// add timer to timer list
				reactionTimerList.add(reactionTimer);
			}
			
			if(!(reactionTimer instanceof BrakeReactionTimer))
			{
				// get index of previous reaction timer
				int index = reactionTimer.getIndex();
				
				// close previous reaction timer and remove from list
				reactionTimer.close();
				reactionTimerList.remove(reactionTimer);
				
				//convert to LaneChangeReactionTimer
				reactionTimer = new BrakeReactionTimer(sim, reactionLogger, experimentStartTime, timerID, index);
				reactionTimerList.add(reactionTimer);
			}
			
			// setup reaction timer
			((BrakeReactionTimer)reactionTimer).setup(reactionGroupID, startSpeed, targetSpeed, 
					mustPressBrakePedal, taskCompletionAfterTime, taskCompletionAfterDistance,
					allowLaneChange, holdSpeedFor, failSound, successSound, comment);
		}
		else
			System.err.println("Make sure ReactionCenter has been started");
	}

	
	private ReactionTimer getReactionTimer(String timerID) 
	{
		for(int index=0; index<reactionTimerList.size(); index++)
			if(reactionTimerList.get(index).getTimerID().equals(timerID))
				return reactionTimerList.get(index);
		return null;
	}
	
	
	public void update()
	{		
		if(isRunning)
		{
			for(ReactionTimer reactionTimer : reactionTimerList)
				reactionTimer.update();
		}
	}
	
	
	public void reportCorrectReaction(int index)
	{
		if(reactionTimerList.size() > index)
			reactionTimerList.get(index).reportCorrectReaction();
	}
	
	
	public void reportCorrectReaction(String timerID)
	{
		ReactionTimer reactionTimer = getReactionTimer(timerID);
		if(reactionTimer != null)
			reactionTimer.reportCorrectReaction();
		else
			System.err.println("No reaction timer '" + timerID + "' found!");
	}
	
	
	public void reportFailureReaction(int index)
	{
		if(reactionTimerList.size() > index)
			reactionTimerList.get(index).reportFailureReaction();
	}
	
	
	public void reportFailureReaction(String timerID)
	{
		ReactionTimer reactionTimer = getReactionTimer(timerID);
		if(reactionTimer != null)
			reactionTimer.reportFailureReaction();
		else
			System.err.println("No reaction timer '" + timerID + "' found!");
	}
	
	
	public void reportMissedReaction(String timerID)
	{
		ReactionTimer reactionTimer = getReactionTimer(timerID);
		if(reactionTimer != null)
			reactionTimer.reportMissedReaction();
		else
			System.err.println("No reaction timer '" + timerID + "' found!");
	}
	
	
	public void close()
	{
		if(isRunning)
		{
			for(ReactionTimer reactionTimer : reactionTimerList)
				reactionTimer.close();
		}
		
		isRunning = false;
		reactionLogger.close();
	}

}
