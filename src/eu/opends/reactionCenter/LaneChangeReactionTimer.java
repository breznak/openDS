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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import eu.opends.audio.AudioCenter;
import eu.opends.environment.LaneLimit;
import eu.opends.jasperReport.ReactionLogger;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class LaneChangeReactionTimer extends ReactionTimer
{
	private float halfCarWidth = 0.75f;
	
	private long timer;
	private boolean timerSet = false;
	private String targetLane; 
	private float minSteeringAngle;
	private float steeringAngle;
	private long startTime;
	private float taskCompletionTime;
	private Vector3f startPosition;
	private float taskCompletionDistance;
	private boolean allowBrake;
	private float holdLaneFor;
	private String failSound;
	private String successSound;
	
	private boolean soundTimerIsActive = false;
	
	
	public LaneChangeReactionTimer(Simulator sim, ReactionLogger reactionLogger, long experimentStartTime, 
			String timerID, int index)
	{
		super(sim, reactionLogger, experimentStartTime, timerID, index);
	}

	
	public void setup(String newReactionGroupID, String startLane, String targetLane, 
			float minSteeringAngle, float taskCompletionTime, float taskCompletionDistance, 
			boolean allowBrake, float holdLaneFor, String failSound, String successSound, String newComment)
	{
		// check pre-condition
		if(startLane.equals(getCurrentLane()))
		{
			super.setup(newReactionGroupID, newComment);
			
			this.targetLane = targetLane;
			this.minSteeringAngle = minSteeringAngle;
			this.startTime = System.currentTimeMillis();
			this.taskCompletionTime = taskCompletionTime;
			this.startPosition = sim.getCar().getPosition();
			this.taskCompletionDistance = taskCompletionDistance;
			this.allowBrake = allowBrake;
			this.holdLaneFor = holdLaneFor;
			this.failSound = failSound;
			this.successSound = successSound;
			this.steeringAngle = 0;
			
			if(targetLane.equals("1") || targetLane.equals("3"))
				trialLogger.setTask(2);
			else if(targetLane.equals("0") || targetLane.equals("4"))
				trialLogger.setTask(3);
			
			timerIsActive = true;
			soundTimerIsActive = false;
		}
		else
		{
			System.err.println("Not in start lane " + startLane + "! Currently: " + getCurrentLane());
			
			// play sound when time/distance has been passed
			this.startTime = System.currentTimeMillis();
			this.taskCompletionTime = taskCompletionTime;
			this.startPosition = sim.getCar().getPosition();
			this.taskCompletionDistance = taskCompletionDistance;
			this.failSound = failSound;
			
			soundTimerIsActive = true;
		}
	}


	public void reportMissedReaction()
	{
		super.reportMissedReaction();
		//System.err.println("MISSED");

		// play fail sound
		AudioCenter.playSound(failSound);
	}
	

	public void update()
	{		
		//super.update();
		
		if(timerIsActive)
		{
			long currentTime = System.currentTimeMillis();
			
			float currentSteeringAngle = FastMath.abs(sim.getCar().getSteeringWheelState());
			steeringAngle = Math.max(steeringAngle, currentSteeringAngle);
			//System.err.println("steering angle: " + steeringAngle);
			
			if(isBraking())
				trialLogger.setAdditional_reaction(1);
			
			if(currentSteeringAngle >= 0.004444f)
				trialLogger.setLaneChangeRT_2angle((int)(currentTime - startTime));
			
			if(currentSteeringAngle >= 0.006666f)
				trialLogger.setLaneChangeRT_3angle((int)(currentTime - startTime));
			
			if(enteringTargetLane())
				trialLogger.setLaneChangeRT_enterLane((int)(currentTime - startTime));
			
			if(timeExceeded() || distanceExceeded())
			{
				reportMissedReaction();
			}
			else if(isBrakingWithoutPermission())
			{
				reportFailureReaction();
			}
			else if(targetLane.equals(getCurrentLane()))
			{
				if(!timerSet)
				{
					timer = System.currentTimeMillis();
					timerSet = true;
				}
				
				//System.err.println("-----------------------hold lane: " + (currentTime-timer));
				if((currentTime-timer >= holdLaneFor) && (steeringAngle >= minSteeringAngle))
				{
					reportCorrectReaction();
				}
			}
			else
			{
				timerSet = false;
			}
			
			long relativeStartTime = startTime - experimentStartTime;
			
			long holdLaneOffset = 0l;
			if(timerSet)
				holdLaneOffset = currentTime-timer;
			long reactionTime = currentTime - startTime - holdLaneOffset;
			
			if(correctReactionReported)
			{
				//System.err.println("CORRECT");
				
				trialLogger.setLaneChangeRT_success((int)reactionTime);
				
				reactionLogger.add(reactionGroupID, 1, reactionTime, startTime, relativeStartTime, comment);
				
				reactionTimer = null;
				comment = "";
				
				trialLogger.setReaction(1);
				trialLogger.writeLog();
				
				// play success sound
				AudioCenter.playSound(successSound);
				
				timerIsActive = false;
			}
			else if(failureReactionReported)
			{
				//System.err.println("FAILED");
				
				reactionLogger.add(reactionGroupID, -1, reactionTime, startTime, relativeStartTime, comment);

				reactionTimer = null;
				comment = "";
				
				trialLogger.setReaction(0);
				trialLogger.writeLog();
				
				// play fail sound
				AudioCenter.playSound(failSound);
				
				timerIsActive = false;
			}
		}
		
		if(soundTimerIsActive)
		{
			if(timeExceeded() || distanceExceeded())
			{
				AudioCenter.playSound(failSound);
				soundTimerIsActive = false;
			}
		}
	}

	
	private boolean isBrakingWithoutPermission()
	{
		if(allowBrake)
			return false;
		else
			return isBraking();
	}
	
	
	private boolean isBraking() 
	{
		return (sim.getCar().getBrakePedalIntensity() > 0);
	}
	
	
	private boolean distanceExceeded()
	{
		// if task completion distance is 0 or less --> no distance limit
		if(taskCompletionDistance<=0)
			return false;
		else
		{
			Vector3f currentPosition = sim.getCar().getPosition();
			//System.err.println("Distance: " + currentPosition.distance(startPosition));
			return (currentPosition.distance(startPosition) > taskCompletionDistance);
		}
	}


	private boolean timeExceeded()
	{
		// if task completion time is 0 or less --> no time limit
		if(taskCompletionTime<=0)
			return false;
		else
		{
			long currentTime = System.currentTimeMillis();
			//System.err.println("Time: " + (currentTime-startTime));
			return (currentTime-startTime > taskCompletionTime);
		}
	}


	private String getCurrentLane()
	{
		float currentX = sim.getCar().getPosition().getX();
				
		Map<String, LaneLimit> laneList = Simulator.getDrivingTask().getScenarioLoader().getLaneList();
		Iterator<Entry<String, LaneLimit>> it = laneList.entrySet().iterator();
	    while(it.hasNext()) 
	    {
	        Entry<String, LaneLimit> pairs = (Entry<String, LaneLimit>)it.next();
	        String laneID = pairs.getKey();
	        LaneLimit laneLimit = pairs.getValue();
	     
	        float xMinReduced = laneLimit.getXMin() + halfCarWidth;
			float xMaxReduced = laneLimit.getXMax() - halfCarWidth;
			
	        if(xMinReduced <= currentX && currentX <= xMaxReduced)
	        	return laneID;
	    }
	    
		return null;
	}
	
	
	private boolean enteringTargetLane() 
	{
		float currentX = sim.getCar().getPosition().getX();
		
		Map<String, LaneLimit> laneList = Simulator.getDrivingTask().getScenarioLoader().getLaneList();
		LaneLimit target = laneList.get(targetLane);
		
		float xMinExtended = target.getXMin() - halfCarWidth;
		float xMaxExtended = target.getXMax() + halfCarWidth;
		
		return (xMinExtended <= currentX && currentX <= xMaxExtended);
	}
	
}
