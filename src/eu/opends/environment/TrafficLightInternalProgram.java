/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2015 Rafael Math
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

package eu.opends.environment;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import eu.opends.environment.TrafficLight.*;
import eu.opends.environment.TrafficLightCenter.*;
import eu.opends.environment.TrafficLightException.*;
import eu.opends.main.Simulator;


/**
 * This class represents the internal traffic light program. It provides 
 * rules to switch traffic lights of a specific intersection. Each intersection
 * requires an individual instance which runs as thread. Before a traffic light
 * will be switched to green, all traffic lights as given in the *-tlr.xml file 
 * will be switched to red first. If no rules file available, all other traffic 
 * lights will be switched to red. Traffic lights to switch green will be queued 
 * in a list (trafficLightsListForGreen) and be processed every 5 seconds.
 * 
 * @author Rafael Math
 */
public class TrafficLightInternalProgram extends Thread
{
	private Simulator sim;
	private TrafficLightCenter trafficLightCenter;
	private boolean stoprequested;
	private String intersectionID;
	private List<TrafficLight> trafficLightsListForGreen = new LinkedList<TrafficLight>();
	private List<TrafficLight> intersectionTrafficLightsList = new LinkedList<TrafficLight>();
	private LinkedList<TrafficLightPhase>intersectionPhasesList = new LinkedList<TrafficLightPhase>();
	private TrafficLightMode previousMode;
	private TrafficLightPhase phase;
	private ListIterator<TrafficLightPhase> iterator;
	private boolean allTrafficLightsOff;

	
	/**
	 * Creates a new traffic light program and initializes it by setting the 
	 * intersectionID and filtering the traffic lights of this intersection 
	 * from the list of all traffic lights.
	 * 
	 * @param intersectionID
	 * 			ID of the intersection that runs this instance of the traffic 
	 * 			light program
	 * 
	 * @param allTrafficLightsList
	 * 			List of all traffic lights in the model
	 */
	public TrafficLightInternalProgram(Simulator sim, TrafficLightCenter trafficLightCenter, String intersectionID, 
			List<TrafficLight> allTrafficLightsList, LinkedList<TrafficLightPhase> intersectionPhasesList) 
	{
		super();
		this.sim = sim;
		this.trafficLightCenter = trafficLightCenter;
		this.intersectionID = intersectionID;
		this.intersectionTrafficLightsList = filterTrafficLightsOfIntersection(intersectionID,allTrafficLightsList);
		this.intersectionPhasesList = intersectionPhasesList;
		stoprequested = false;
		allTrafficLightsOff = false;
		
		if(intersectionPhasesList != null)
		{
			iterator = intersectionPhasesList.listIterator(0);
			phase = gotoNextPhase();
			phase.activate(System.currentTimeMillis());
			if(trafficLightCenter.getMode() == TrafficLightMode.PROGRAM)
				setPhaseToTrafficLights();
		}
	}
	
	
	/**
	 * Walks through the traffic light list of the model and returns a list of 
	 * those which match to the given intersectionID.
	 *  
	 * @param intersectionID
	 * 			ID of the current intersection
	 * 
	 * @param allTrafficLightsList
	 * 			List of all traffic lights contained in the model
	 * 
	 * @return
	 * 			Subset of the input list, containing only traffic lights matching 
	 * 			to the given intersectionID
	 */
	public List<TrafficLight> filterTrafficLightsOfIntersection(String intersectionID, 
			List<TrafficLight> allTrafficLightsList)
	{
		List<TrafficLight> intersectionTrafficLightsList = new LinkedList<TrafficLight>();

		for (TrafficLight trafficLight : allTrafficLightsList) 
		{
			if (trafficLight.getIntersectionID().equals(intersectionID))
			{
				intersectionTrafficLightsList.add(trafficLight);
			}
		}
		return intersectionTrafficLightsList;
	}
	
	
	/**
	 * This method adds a traffic light to the queue of traffic lights waiting 
	 * to turn green, if it is not contained yet.
	 * 
	 * @param trafficLight
	 * 			Traffic light to be added to the green-light-queue
	 */
	public synchronized void requestGreen(TrafficLight trafficLight)
	{
		if(!trafficLightsListForGreen.contains(trafficLight))
			trafficLightsListForGreen.add(trafficLight);
	}
	

	/**
	 * Stops the traffic light program by exiting the loop
	 */
	public synchronized void requestStop()
	{
		stoprequested = true;
	}
	
	
	/**
	 * Returns the ID of the intersection which has been assigned to this internal program
	 * 
	 * @return
	 * 			intersectionID
	 */
	public synchronized String getIntersectionID()
	{
		return intersectionID;
	}
	
	
	/**
	 * Computes the number of seconds that a given 
	 * traffic light will be in a different state than "green" according to 
	 * the internal program schedule.<br>
	 * Note: Red means every non-green state in this context
	 * 
	 * @param trafficLight
	 * 			Traffic light to check for remaining red
	 * 
	 * @return
	 * 			Number of steps, the given traffic light will not be green
	 * 
	 * @throws NeverGreenException
	 * 			If given traffic light will never turn to green
	 * 
	 * @throws IsGreenException
	 * 			If given traffic light has already turned to green
	 */
	public synchronized int getRemainingRed(TrafficLight trafficLight) throws NeverGreenException, IsGreenException
	{
		// if traffic light is green --> exception, since no remaining red
		if(phase.getState(trafficLight) == TrafficLightState.GREEN)
			throw new IsGreenException("Traffic light is green");
		
		// get remaining time (usually in seconds) for current phase
		long remainingRed = phase.timeToExpiration(System.currentTimeMillis());
		
		// remember start phase to avoid cycles
		String startPhaseID = phase.getID();
		
		// compute next phase
		TrafficLightPhase nextPhase = getNextPhase(phase);
		
		// go through phases until next green-phase is reached
		while(nextPhase.getState(trafficLight) != TrafficLightState.GREEN)
		{
			// if start phase was reached again --> break the cycle, since no green at all
			if(nextPhase.getID().equals(startPhaseID))
				throw new NeverGreenException("Traffic light will never be green");

			// add duration of this red-phase and continue with next one
			remainingRed += nextPhase.getDuration();
			nextPhase = getNextPhase(nextPhase);
		}

		// convert ms to seconds
		return (int) (remainingRed/1000);
	}
	
	
	/**
	 * Computes the number of seconds that a given 
	 * traffic light will be green according to the internal program schedule.
	 * 
	 * @param trafficLight
	 * 			Traffic light to check for remaining green
	 * 
	 * @return
	 * 			Number of steps, the given traffic light will be green
	 * 
	 * @throws AlwaysGreenException
	 * 			If given traffic light will always be green
	 * 
	 * @throws IsNotGreenException
	 * 			If given traffic light is not green
	 */
	public synchronized int getRemainingGreen(TrafficLight trafficLight) throws AlwaysGreenException, IsNotGreenException
	{
		// if traffic light is not green --> exception, since no remaining green
		if(phase.getState(trafficLight) != TrafficLightState.GREEN)
			throw new IsNotGreenException("Traffic light is not green");
		
		// get remaining time (usually in seconds) for current phase
		long remainingGreen = phase.timeToExpiration(System.currentTimeMillis());
		
		// remember start phase to avoid cycles
		String startPhaseID = phase.getID();
		
		// compute next phase
		TrafficLightPhase nextPhase = getNextPhase(phase);
		
		// go through phases until next non-green-phase is reached
		while(nextPhase.getState(trafficLight) == TrafficLightState.GREEN)
		{
			// if start phase was reached again --> break the cycle, since all phases are green
			if(nextPhase.getID().equals(startPhaseID))
				throw new AlwaysGreenException("Traffic light is always green");

			// add duration of this green-phase and continue with next one
			remainingGreen += nextPhase.getDuration();
			nextPhase = getNextPhase(nextPhase);
		}
		
		// convert ms to seconds
		return (int) (remainingGreen/1000);
	}
	
	
	/**
	 * This method contains a loop with the basic rules to switch the traffic lights
	 * of a certain intersection. Instructions will only be carried out in TRIGGER,
	 * PROGRAMM or BLINKING mode. Each execution of this loop takes at least 1 second.
	 */
	@Override
	public void run() 
	{		
		while (!stoprequested) 
		{
			TrafficLightMode currentMode = trafficLightCenter.getMode();

			if(!sim.isPause())
			{
				if(currentMode == TrafficLightMode.TRIGGER)
					runTriggerMode();
	
				else if(currentMode == TrafficLightMode.BLINKING)
					runBlinkingMode();
				
				else if(currentMode == TrafficLightMode.PROGRAM)
					runProgramMode();
			}

			previousMode = currentMode;
			
			try {
				// halt thread for 1 second to provide a clock for BLINKING mode; 
				// otherwise prevent loop from repeating 
				// to fast in OFF or EXTERNAL mode 
				Thread.sleep(1000);
				
			} catch (InterruptedException e){}
		}

		// if loop was left: traffic light program will be terminated
		//System.out.println("Program terminated");
	}


	/**
	 * Traffic light program for TRIGGER mode. If there is a traffic light in the 
	 * list to be switched to green, all required traffic lights at this intersection
	 * have to be switched to red first. The traffic lights required to turn red will 
	 * be loaded from an external source; if not available, all other traffic lights 
	 * will be switched to red. After this, the selected traffic light will be switched
	 * to yellow-red and finally to green. After processing, it will be removed from queue. 
	 */
	private void runTriggerMode() 
	{
		
		// if mode has changed to TRIGGER mode --> initialize first
		if(previousMode != TrafficLightMode.TRIGGER)
		{
			// clear queue of traffic lights waiting to be switched to green
			trafficLightsListForGreen.clear();
			
			// switch all traffic lights of current intersection to red
			requestIntersectionRed(null);
		}
		
		
		// if there are traffic lights waiting to be switched to green --> process queue
		if(!trafficLightsListForGreen.isEmpty())
		{
			// get first traffic light in queue
			TrafficLight trafficLight = trafficLightsListForGreen.get(0);
				
			// if traffic light belongs to current intersection and is not green yet
			if((trafficLight.getIntersectionID().equals(intersectionID)) && 
					(trafficLight.getState() != TrafficLightState.GREEN))
			{
				// switch all those traffic lights of the current intersection to red, 
				// that are required to switch the selected traffic light to green and 
				// wait a second
				requestIntersectionRed(trafficLight);
				wait(1);
				
				// if all required traffic lights are red
				if(isIntersectionRed(trafficLight))
				{
					// switch selected traffic light to YELLOWRED and wait 1 second
					trafficLight.setState(TrafficLightState.YELLOWRED);
					wait(1);
					
					// switch selected traffic light to GREEN and wait 3 seconds
					trafficLight.setState(TrafficLightState.GREEN);
					wait(3);
				}
			}
			
			// remove processed traffic light from queue
			trafficLightsListForGreen.remove(0);
		}
	}


	/**
	 * Traffic light program for BLINKING mode. Since this method is called every 
	 * second (when active), all traffic lights of a certain intersection will be 
	 * changed from OFF to YELLOW in the odd calls, and from YELLOW to OFF in the 
	 * even calls.
	 */
	private void runBlinkingMode() 
	{
		for(TrafficLight trafficLight : intersectionTrafficLightsList)
		{
			if(trafficLight.getState() == TrafficLightState.OFF)
				trafficLight.setState(TrafficLightState.YELLOW);
			else
				trafficLight.setState(TrafficLightState.OFF);
		}
	}	
	
	
	/**
	 * Traffic light program for PROGRAM mode. Traffic light states will be scheduled
	 * according to a given external XML file. Every step (usually 1 second) this method
	 * checks whether the current phase has changed and if so, the traffic light states
	 * will be changed.
	 */
	private void runProgramMode() 
	{
		// if no external phases list available 
		// --> switch all traffic lights off (only once)
		if(intersectionPhasesList == null)
		{
			switchAllTrafficLightsOff();
			return;
		}
		
		// if changed from different mode --> initialize PROGRAM mode
		if(previousMode != TrafficLightMode.PROGRAM)
			setPhaseToTrafficLights();

		// if current phase has expired, set next phase to traffic lights
		long currentTime = System.currentTimeMillis();
		if(phase.hasExpired(currentTime))
		{
			phase = gotoNextPhase();
			phase.activate(currentTime);
			setPhaseToTrafficLights();
		}
		
		// increment clock
		currentTime++;
	}


	/**
	 * Changes all traffic light states to off (if not already done)
	 */
	private void switchAllTrafficLightsOff() 
	{
		if(!allTrafficLightsOff)
		{
			for(TrafficLight trafficLight : intersectionTrafficLightsList)
				trafficLight.setState(TrafficLightState.OFF);
			
			allTrafficLightsOff = true;
		}
	}
	

	/**
	 * Computes the traffic light phase that follows the given one. If the given 
	 * phase is the last one in the list, the first phase will be returned instead.
	 * 
	 * @param phase
	 * 			Traffic light phase to determine the successor of
	 * @return
	 * 			successor phase of the given phase
	 */
	private TrafficLightPhase getNextPhase(TrafficLightPhase phase) 
	{
		int index = intersectionPhasesList.lastIndexOf(phase);		
		int size  = intersectionPhasesList.size();
		
		int nextIndex = (index+1) % size;
		
		return intersectionPhasesList.get(nextIndex);
	}
	
	
	/**
	 * Computes the next traffic light phase AND moves the iterator to the next phase. 
	 * If the last phase in the list is reached, the first one will be returned instead.
	 * @return
	 */
	private TrafficLightPhase gotoNextPhase() 
	{
		if(!iterator.hasNext())
			iterator = intersectionPhasesList.listIterator(0);
		
		return iterator.next();
	}

	
	/**
	 * Sets the traffic light statuses of the current phase to the traffic lights of 
	 * the intersection which is related to this internal traffic light program.
	 */
	private void setPhaseToTrafficLights()
	{
		// for each traffic light at this intersection
		for(TrafficLight trafficLight : intersectionTrafficLightsList)
		{
			// read individual state from the current phase
			TrafficLightState state = phase.getState(trafficLight);
			
			// set this state to traffic light
			trafficLight.setState(state);
		}
	}
	
	
	/**
	 * Switches all traffic lights to red which are required to switch the 
	 * given one to green. Green traffic lights will first be switched to
	 * yellow, after a second they will be switched to red.
	 * 
	 * @param trafficLightForGreen
	 *  		Traffic light waiting for green, while all other interfering 
	 *  		traffic lights at this intersection will be switched to red.
	 *  		If null, all traffic lights at this intersection will be 
	 *  		switched to red.
	 */
	private void requestIntersectionRed(TrafficLight trafficLightForGreen)
	{
		// shift rules: 1. GREEN --> YELLOW
		//				2. wait 1 second
		//				3. YELLOW --> RED
		
		// shift one step (all yellow lights to red and all green lights to yellow) 
		shiftIntersectionToRed(trafficLightForGreen);
		
		if(!isIntersectionRed(trafficLightForGreen))
		{
			wait(1);
			// shift one further step (all remaining yellow lights to red)
			shiftIntersectionToRed(trafficLightForGreen);
		}
	}
	
	
	/**
	 * Walks through the list of all traffic lights of the current intersection
	 * and shifts the light status one step closer to red for all traffic lights
	 * that need to be red before the given one (trafficLightForGreen) may be 
	 * switched to green. 
	 * 
	 * @param trafficLightForGreen
	 * 			Traffic light waiting for green, while all other interfering 
	 *  		traffic lights at this intersection will be shifted one step 
	 *  		closer to red. If null, all traffic lights at this intersection 
	 *  		will be shifted one step closer to red.
	 */
	private void shiftIntersectionToRed(TrafficLight trafficLightForGreen)
	{
		// a traffic light from the list "intersectionTrafficLightsList" will be processed if:
		//  - no traffic light waiting for green ("trafficLightForGreen") is given, 
		//  - no rules for the given traffic light waiting for green are given,
		//  - the traffic light is contained in the must-be-red-list of the traffic light waiting for green.
		for(TrafficLight trafficLight : intersectionTrafficLightsList)
		{
			if(
					(trafficLightForGreen == null) || 
					(trafficLightForGreen.getTrafficLightRules() == null) ||
					(trafficLightForGreen.getTrafficLightRules().contains(trafficLight))
			   )
			{
				if(trafficLight.getState() == TrafficLightState.RED)
				{
					// do nothing
				}
				else if(trafficLight.getState() == TrafficLightState.YELLOW)
				{
					// switch status: YELLOW --> RED
					trafficLight.setState(TrafficLightState.RED);
				}
				else
				{
					// switch status: ALL OTHER --> YELLOW
					trafficLight.setState(TrafficLightState.YELLOW);
				}
			}
		}
	}
	
	
	/**
	 * Walks through the list of all traffic lights of the current intersection
	 * and checks whether all traffic lights, which are required to be red before
	 * the given one (trafficLightForGreen) may be switched to green, are red.
	 * 
	 * @param trafficLightForGreen
	 * 			Traffic light to switch to green. Needed to compute all interfering 
	 * 			traffic lights.
	 * 
	 * @return
	 * 			True, if all required traffic lights are red
	 */
	private boolean isIntersectionRed(TrafficLight trafficLightForGreen)
	{		
		boolean allRed = true;
		
		// a traffic light from the list "intersectionTrafficLightsList" will be processed if:
		//  - no traffic light waiting for green ("trafficLightForGreen") is given, 
		//  - no rules for the given traffic light waiting for green are given,
		//  - the traffic light is contained in the must-be-red-list of the traffic light waiting for green.
		for (TrafficLight trafficLight : intersectionTrafficLightsList)
		{
			if(
					(trafficLightForGreen == null) || 
					(trafficLightForGreen.getTrafficLightRules() == null) ||
					(trafficLightForGreen.getTrafficLightRules().contains(trafficLight))
			  )
			{
				allRed = allRed && (trafficLight.getState() == TrafficLightState.RED);
			}
		}
		
		return allRed;
	}

	
	/**
	 * Stops the loop for the given number of seconds (for INTERNAL mode only)
	 * 
	 * @param seconds
	 * 			number of seconds to stop the thread
	 */
	private void wait(int seconds)
	{
		// wait only if INTERNAL mode is still running
		if(sim.getTrafficLightCenter().getMode() == TrafficLightMode.TRIGGER)
		{
			try {
				Thread.sleep(seconds*1000);
			} catch (InterruptedException e){}
		}
	}

	
}
