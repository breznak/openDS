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

import eu.opends.basics.SimulationBasics;
import eu.opends.main.Simulator;
import eu.opends.traffic.Pedestrian;
import eu.opends.traffic.PhysicalTraffic;
import eu.opends.traffic.TrafficCar;
import eu.opends.traffic.TrafficObject;


/**
 * This class represents a MoveTraffic trigger action. Whenever a collision
 * with a related trigger was detected, the given traffic object will be moved
 * to the given way point
 * 
 * @author Rafael Math
 */
public class MoveTrafficTriggerAction extends TriggerAction 
{
	private SimulationBasics sim;
	private String trafficObjectName;
	private String wayPointID;
	private Boolean engineOn;
	private Boolean pedestrianEnabled;
	
	
	/**
	 * Creates a new MoveTraffic trigger action instance, providing traffic 
	 * object's name and way point's ID.
	 * 
	 * @param sim
	 * 			Simulator
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param maxRepeat
	 * 			Number of maximum recurrences
	 * 
	 * @param trafficObjectName
	 * 			Name of the traffic object to move.
	 * 
	 * @param wayPointID
	 * 			ID of the way point to move the traffic object to.
	 * 
	 * @param engineOn
	 * 			Set engine on/off.
	 * 
	 * @param pedestrianEnabled
	 * 			Set pedestrian enabled/disabled.
	 */
	public MoveTrafficTriggerAction(SimulationBasics sim, float delay, int maxRepeat, String trafficObjectName, String wayPointID,
			Boolean engineOn, Boolean pedestrianEnabled) 
	{
		super(delay, maxRepeat);
		this.sim = sim;
		this.trafficObjectName = trafficObjectName;
		this.wayPointID = wayPointID;
		this.engineOn = engineOn;
		this.pedestrianEnabled = pedestrianEnabled;
	}

	
	/**
	 * Moves the given traffic object to the given way point
	 */
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{		
			if(sim instanceof Simulator)
			{
				PhysicalTraffic physicalTraffic = ((Simulator)sim).getPhysicalTraffic();
				TrafficObject trafficObject = physicalTraffic.getTrafficObject(trafficObjectName);
				
				if(trafficObject != null)
				{
					if(wayPointID != null && !wayPointID.equals(""))
						trafficObject.setToWayPoint(wayPointID);
				
					if(trafficObject instanceof TrafficCar && engineOn != null)
						((TrafficCar)trafficObject).setEngineOn(engineOn);
					
					if(trafficObject instanceof Pedestrian && pedestrianEnabled != null)
						((Pedestrian)trafficObject).setEnabled(pedestrianEnabled);
				}
				
				updateCounter();
			}
		}
	}
}
