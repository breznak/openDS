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

package eu.opends.trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jme3.collision.CollisionResults;
//import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

//import eu.opends.audio.AudioCenter;
//import eu.opends.basics.MapObject;
import eu.opends.basics.SimulationBasics;
import eu.opends.car.Car;
import eu.opends.environment.TrafficLightCenter.TriggerType;
import eu.opends.main.Simulator;
import eu.opends.tools.Util;


/**
 * This class is responsible for anything related to triggers on the streets.
 * 
 * @author Saied Tehrani, Rafael Math
 */
public class TriggerCenter 
{
	private CollisionResults resultCollision;

	private Simulator sim;

	private String triggerName;

	private static Map<String,Spatial> trafficLightTriggerList, trafficLightPhaseTriggerList;

	private LinkedList<Spatial> roadObjectsTriggerList;
	public static ArrayList<String> triggerReportList = new ArrayList<String>(5);

	
	public static void addToTrafficLightTriggerList(String trafficLightName, Spatial trafficLightTriggerObject)
	{
		trafficLightTriggerList.put(trafficLightName, trafficLightTriggerObject);
	}
	
	
	public static void addToTrafficLightPhaseTriggerList(String trafficLightName, Spatial trafficLightTriggerObject)
	{
		trafficLightPhaseTriggerList.put(trafficLightName, trafficLightTriggerObject);
	}
	
	
	public TriggerCenter(Simulator sim) 
	{
		this.sim = sim;

		trafficLightTriggerList = new HashMap<String,Spatial>();
		trafficLightPhaseTriggerList = new HashMap<String,Spatial>();
		roadObjectsTriggerList = new LinkedList<Spatial>();
	}

	
	public void setup() 
	{
		resultCollision = new CollisionResults();

		Spatial tempSpatial;

		String tempSpatialName;

		//-----------------------
		List<Spatial> tempList = sim.getTriggerNode().getChildren();
		//List<Spatial> tempList = Util.getAllSpatials(sim.getTriggerNode());
		//-----------------------
		
		for (Iterator<Spatial> it = tempList.iterator(); it.hasNext();) 
		{
			tempSpatial = it.next();

			tempSpatialName = tempSpatial.getName();
			
			if (SimulationBasics.getTriggerActionListMap().containsKey(tempSpatialName))
			{
				roadObjectsTriggerList.add(tempSpatial);
			}

		}
	}

	
	public void doTriggerChecks() 
	{
		handleTrafficLightCollision(trafficLightTriggerList);
		handleTrafficLightPhaseCollision(trafficLightPhaseTriggerList);
		handleRoadObjectsCollision(roadObjectsTriggerList);
		//computeContactWithCar();
	}

	
	/**
	 * This method handles a collision of the car with a traffic light trigger. 
	 * The TrafficLightTrigger which recognizes cars located close to a traffic 
	 * light (up to 40 meters) and requests green light.
	 * A collision will be forwarded to the traffic light center.
	 * 
	 * @param triggerList
	 * 			list of all traffic light triggers in order to monitor approximation 
	 * 			to traffic lights
	 */
	private void handleTrafficLightCollision(Map<String,Spatial> triggerList)
	{
		for (Entry<String, Spatial> trigger : triggerList.entrySet())
		{
			resultCollision.clear();
			triggerName = trigger.getValue().getName();
			
			// calculate collision of the car with a road object trigger
			Spatial triggerObject = sim.getTriggerNode().getChild(triggerName);
			sim.getCar().getCarNode().collideWith(triggerObject.getWorldBound(), resultCollision);
			
			if(resultCollision.size() > 0) 
				sim.getTrafficLightCenter().reportCollision(trigger.getKey(), TriggerType.REQUEST);
		}
	}
	
	
	/**
	 * This method handles a collision of the car with a traffic light phase trigger.
	 * The TrafficLightPhaseTrigger is a long range trigger (up to 150 meters) 
	 * which controls the SIM-TD traffic light phase assistant</p>
	 * A collision will be forwarded to the traffic light center.
	 * 
	 * @param triggerList
	 * 			list of all traffic light triggers in order to monitor approximation 
	 * 			to traffic lights
	 */
	private void handleTrafficLightPhaseCollision(Map<String,Spatial> triggerList)
	{
		for (Entry<String, Spatial> trigger : triggerList.entrySet())
		{
			resultCollision.clear();
			triggerName = trigger.getValue().getName();
			
			// calculate collision of the car with a road object trigger
			Spatial triggerObject = sim.getTriggerNode().getChild(triggerName);
			sim.getCar().getCarNode().collideWith(triggerObject.getWorldBound(), resultCollision);
			
			if(resultCollision.size() > 0) 
			{
				sim.getTrafficLightCenter().reportCollision(trigger.getKey(), TriggerType.PHASE);
			}
		}
	}
	
	
	/**
	 * This method handles a collision of the car with a road object trigger. This can be:
	 * SpeedLimitTrigger, CautionSignTrigger or BlindTrigger. A collision will be forwarded 
	 * to the HMI center.
	 * 
	 * @param triggerList
	 * 			list of all road object triggers in order to monitor approximation 
	 * 			to such an object
	 */
	private void handleRoadObjectsCollision(LinkedList<Spatial> triggerList)
	{
		Car car = sim.getCar();
		
		for (Spatial trigger : triggerList) 
		{	
			// TODO: caution! trigger center may be farther away than 10 meters when hitting
			if(trigger.getWorldTranslation().distance(car.getCarNode().getWorldTranslation()) < 10)
			{
				resultCollision.clear();
				String triggerName = trigger.getName();
					
				// calculate collision of the car with a road object trigger

				Spatial triggerObject = sim.getTriggerNode().getChild(triggerName);
				car.getCarNode().collideWith(triggerObject.getWorldBound(), resultCollision);
				
				// if car has collided with a trigger --> report trigger to HMI Center
				if(resultCollision.size() > 0)
				{
					if(SimulationBasics.getTriggerActionListMap().containsKey(triggerName))
						TriggerCenter.performTriggerAction(triggerName, car);
				}
			}
		}
	}


	Map<String,Integer> collisionMap = new HashMap<String,Integer>();
	float suspensionForce[] = {0,0,0,0};
	
	/**
	 * Computes the IDs of the road objects that are in touch with any of the car's wheels
	 * and forwards them to class VisualSimulatorState for ecoDrive project.
	 */
	/*
	private void computeContactWithCar()
	{
		Car car = sim.getCar();

		for (Spatial roadObject : ((Node)sim.getSceneNode()).getChildren()) 
		{	
			if(roadObject.getName() != null)
			{
				resultCollision.clear();
				//String[] roadObjectName = roadObject.getName().split("\\.");
				
				// calculate collision of the car with a road object
				car.getCarNode().collideWith(roadObject.getWorldBound(), resultCollision);
	
				if(resultCollision.size() > 0)
				{
					// if car has collided with a road object --> report collision
					//System.out.println("Collided with: " + roadObjectName[0]);
					
					// report collision with chassis --> play sound
					if(resultCollision.getClosestCollision().getGeometry().getName().startsWith("Car-geom-1"))
						playCollisionSound(car, roadObject.getName());
				}
			}
		}
		
		// decrease frame counter of every road object contained in collisionMap by 1
		decreaseCounter();
		
		// report collision with wheels --> play sound
    	for(int i=0; i<=3; i++)
    	{
			float currentSuspensionForce = car.getCarControl().getWheel(i).getWheelInfo().wheelsSuspensionForce;
			if(suspensionForce[i] - currentSuspensionForce > 2000)
				AudioCenter.playSound("potHole");
			suspensionForce[i] = currentSuspensionForce;
    	}
	}
	

	private void playCollisionSound(Car car, String roadObjectName)
	{
		MapObject currentMapObject = getMapObject(roadObjectName);
		
		// if object is collidable (world-node-objects are considered to be always collidable)
		if((currentMapObject == null) || (!currentMapObject.getCollisionShape().equalsIgnoreCase("none")))
		{
			if(!collisionMap.containsKey(roadObjectName))
			{
				if(car.getCurrentSpeedKmh() > 50)
				{
					//AudioCenter.playSound("crash");
					//car.setEnginOn(false);
					//car.setBrakePedalPressIntensity(1f);
				}
				else
				{
					// TODO exclude initial sound
					if(!roadObjectName.equalsIgnoreCase("sb_smaf_bordstein"))
					{
						String soundFile = "collision";
						if((currentMapObject != null) && (!currentMapObject.getCollisionSound().isEmpty()))
							soundFile = currentMapObject.getCollisionSound();
						AudioCenter.playSound(soundFile);
					}
				}							
			}
			
			// lock road object at least for the next 5 frames
			collisionMap.put(roadObjectName, 5);
		}
	}


	private void decreaseCounter()
	{
		Iterator<Entry<String, Integer>> iterator = collisionMap.entrySet().iterator();
		
		while(iterator.hasNext()) 
		{
			Entry<String, Integer> entry = (Entry<String, Integer>)iterator.next();

			if(entry.getValue() > 1)
				entry.setValue(entry.getValue() - 1);
			else
				iterator.remove();
		}
	}
	

	private MapObject getMapObject(String currentCollision)
	{
		List<MapObject> mapObjects = Simulator.getDrivingTask().getSceneLoader().getMapObjects();
		for(MapObject mapObject : mapObjects)
		{
			if(mapObject.getName().equals(currentCollision))
				return mapObject;
		}
		return null;
	}
	*/
	

	/**
	 * Reports the collision of the car with a free hand placed trigger 
	 * and performs the specified action (e.g. send a text to the screen)
	 * 
	 * @param triggerID
	 * 			name of the trigger (needed to look up action)
	 * 
	 * @param car
	 * 			user-controlled car of simulator 
	 */
	public static void performTriggerAction(String triggerID, Car car) 
	{
		if(!triggerReportList.contains(triggerID))
		{
			System.err.println("Trigger hit: " + triggerID);
			// add trigger to report list
			triggerReportList.add(triggerID);
			
			// remove trigger from report list after 2 seconds
			int seconds = 2;
		
			List<TriggerAction> triggerActionList = SimulationBasics.getTriggerActionListMap().get(triggerID);
			for(TriggerAction triggerAction : triggerActionList)
			{
				triggerAction.performAction();
				
				// extend two seconds by time of pause duration, if pause trigger was contained in list
				if(triggerAction instanceof PauseTriggerAction)
					seconds += ((PauseTriggerAction)triggerAction).getDuration();
			}
			
			RemoveFromReportListThread removeThread = new RemoveFromReportListThread(triggerID, seconds);
			removeThread.start();
		}
	}
	
	
	/**
	 * Every time a trigger is reported it will be added to trigger report 
	 * list in order to avoid multiple instances of the event (e.g. if car 
	 * still hits the trigger after 10 milliseconds). This method removes 
	 * the given trigger from this list again.
	 *  
	 * @param objectID
	 * 			ID of the trigger to be removed from the report list
	 */
	public static void removeTriggerReport(String objectID)
	{
		if(!triggerReportList.remove(objectID))
			System.err.println("Could not remove '" + objectID + "' from trigger report list!");
	}

}
