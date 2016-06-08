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

package eu.opends.drivingTask.interaction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.JoyAxisTrigger;
import com.jme3.input.controls.JoyButtonTrigger;
import com.jme3.input.controls.KeyTrigger;

import eu.opends.basics.SimulationBasics;
import eu.opends.drivingTask.DrivingTaskDataQuery;
import eu.opends.drivingTask.DrivingTaskDataQuery.Layer;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.input.AxisAnalogListener;
import eu.opends.input.KeyActionListener;
import eu.opends.main.Simulator;
import eu.opends.trigger.TriggerAction;

/**
 * 
 * @author Rafael Math
 */
public class InteractionLoader 
{
	private DrivingTaskDataQuery dtData;
	private SimulationBasics sim;
	private Map<String,List<ActionDescription>> activityMap;
	private List<TriggerDescription> triggerList;
	private SettingsLoader settingsLoader;
	
	
	public InteractionLoader(DrivingTaskDataQuery dtData, SimulationBasics sim, SettingsLoader settingsLoader) 
	{
		this.dtData = dtData;
		this.sim = sim;
		this.settingsLoader = settingsLoader;
		this.activityMap = new HashMap<String,List<ActionDescription>>();
		this.triggerList = new ArrayList<TriggerDescription>();
		readActivities();
		readTriggers();
		if(sim instanceof Simulator)
			evaluateTriggers();
	}


	public void readActivities()
	{
		NodeList activityNodes = (NodeList) dtData.xPathQuery(Layer.INTERACTION, 
				"/interaction:interaction/interaction:activities/interaction:activity", XPathConstants.NODESET);

		for (int i = 1; i <= activityNodes.getLength(); i++) 
		{
			Node currentNode = activityNodes.item(i-1);
			extractActivity(currentNode);
			//extractActivity("/interaction:interaction/interaction:activities/interaction:activity["+i+"]");
		}
	}

	
	private String extractActivity(Node currentNode) 
	{
		// get activity name
		//String activityName = dtData.getValue(Layer.INTERACTION, 
		//		path + "/@id", String.class);
		String activityName = currentNode.getAttributes().getNamedItem("id").getNodeValue();
	
		List<ActionDescription> actionList = new ArrayList<ActionDescription>();
		
		//NodeList actionNodes = (NodeList) dtData.xPathQuery(Layer.INTERACTION, 
		//		path + "/interaction:action", XPathConstants.NODESET);

		NodeList actionNodes = currentNode.getChildNodes();
		
		for (int j = 1; j <= actionNodes.getLength(); j++) 
		{
			if(actionNodes.item(j-1).getNodeName().equals("action"))
			{
				// get action name
				//String actionName = dtData.getValue(Layer.INTERACTION, 
				//		path + "/interaction:action["+j+"]/@id", String.class);
				String actionName = actionNodes.item(j-1).getAttributes().getNamedItem("id").getNodeValue();
				
				// get delay
				//float delay = dtData.getValue(Layer.INTERACTION, 
				//		path + "/interaction:action["+j+"]/@delay", Float.class);
				float delay = 0;
				
				Node delayNode = actionNodes.item(j-1).getAttributes().getNamedItem("delay");
				if(delayNode != null)
				{
					String delayString = delayNode.getNodeValue();
					if(delayString != null && !delayString.isEmpty())
						delay = Float.parseFloat(delayString);
				}
				
				
				// get repeat
				//int repeat = dtData.getValue(Layer.INTERACTION, 
				//		path + "/interaction:action["+j+"]/@repeat", Integer.class);
				int repeat = 0;
				
				Node repeatNode = actionNodes.item(j-1).getAttributes().getNamedItem("repeat");
				
				if(repeatNode != null)
				{
					String repeatString = repeatNode.getNodeValue();
					if(repeatString != null && !repeatString.isEmpty())
						repeat = Integer.parseInt(repeatString);
				}
				
				
				Properties parameterList = new Properties();
				
				//NodeList parameterNodes = (NodeList) dtData.xPathQuery(Layer.INTERACTION, 
				//		path + "/interaction:action["+j+"]/interaction:parameter", XPathConstants.NODESET);
				
				NodeList parameterNodes = actionNodes.item(j-1).getChildNodes();
				
				for (int k = 1; k <= parameterNodes.getLength(); k++) 
				{
					if(parameterNodes.item(k-1).getNodeName().equals("parameter"))
					{
						// get parameter name
						//String parameterName = dtData.getValue(Layer.INTERACTION, 
						//		path + "/interaction:action["+j+"]/interaction:parameter["+k+"]/@name", String.class);
						String parameterName = parameterNodes.item(k-1).getAttributes().getNamedItem("name").getNodeValue();
						
						// get parameter value
						//String parameterValue = dtData.getValue(Layer.INTERACTION, 
						//		path + "/interaction:action["+j+"]/interaction:parameter["+k+"]/@value", String.class);
						String parameterValue = parameterNodes.item(k-1).getAttributes().getNamedItem("value").getNodeValue();
						
						parameterList.setProperty(parameterName, parameterValue);
					}
				}
				
				actionList.add(new ActionDescription(actionName, delay, repeat, parameterList));
			}
		}
		
		if(activityMap.containsKey(activityName))
			System.err.println("Caution: overwriting activity '" + activityName + "' in file: " + dtData.getInteractionPath());
		
		activityMap.put(activityName, actionList);
		
		return activityName;
	}
	
	
	private void readTriggers() 
	{
		NodeList triggerNodes = (NodeList) dtData.xPathQuery(Layer.INTERACTION, 
				"/interaction:interaction/interaction:triggers/interaction:trigger", XPathConstants.NODESET);

		for (int i = 1; i <= triggerNodes.getLength(); i++) 
		{
			Node currentNode = triggerNodes.item(i-1);
			
			// get trigger name
			//String triggerName = dtData.getValue(Layer.INTERACTION, 
			//		"/interaction:interaction/interaction:triggers/interaction:trigger["+i+"]/@id", String.class);
			String triggerName = currentNode.getAttributes().getNamedItem("id").getNodeValue();
			
			// get trigger priority
			//int triggerPriority = dtData.getValue(Layer.INTERACTION, 
			//		"/interaction:interaction/interaction:triggers/interaction:trigger["+i+"]/@priority", Integer.class);
			String triggerPriorityString = currentNode.getAttributes().getNamedItem("priority").getNodeValue();
			int triggerPriority = Integer.parseInt(triggerPriorityString);
			
			
			NodeList childnodes = currentNode.getChildNodes();
			
			String triggerCondition = null;
			List<String> activityRefList = new ArrayList<String>();
			
			for (int j = 1; j <= childnodes.getLength(); j++) 
			{
				Node currentChild = childnodes.item(j-1);
				
				// get trigger condition
				//String triggerCondition = dtData.getValue(Layer.INTERACTION, 
				//		"/interaction:interaction/interaction:triggers/interaction:trigger["+i+"]/interaction:condition", String.class);
				
				if(currentChild.getNodeName().equals("condition"))
				{
					triggerCondition = currentChild.getTextContent();
				}
				
				else if(currentChild.getNodeName().equals("activities"))
				{
					//NodeList activityNodes = (NodeList) dtData.xPathQuery(Layer.INTERACTION, 
					//		"/interaction:interaction/interaction:triggers/interaction:trigger["+i+"]/interaction:activities/interaction:activity", XPathConstants.NODESET);
					NodeList activityNodes = currentChild.getChildNodes();
					
					for (int k = 1; k <= activityNodes.getLength(); k++) 
					{
						if(activityNodes.item(k-1).getNodeName().equals("activity"))
						{
							// get activity reference
							//String activityRef = dtData.getValue(Layer.INTERACTION, 
							//		"/interaction:interaction/interaction:triggers/interaction:trigger["+i+"]/interaction:activities/interaction:activity["+k+"]/@ref", String.class);
							Node refNode = activityNodes.item(k-1).getAttributes().getNamedItem("ref");
							String activityRef = null;
							
							if(refNode != null)
								activityRef = refNode.getNodeValue();

							if(activityRef != null && !activityRef.isEmpty())
							{
								if(activityMap.containsKey(activityRef))
									activityRefList.add(activityRef);
								else
									System.err.println("Reference to activity '" + activityRef + "' could not be found (Trigger: '" + triggerName + "')!");
							}
							else
							{
								// try to extract local activity declaration
								activityRef = extractActivity(activityNodes.item(k-1));//"/interaction:interaction/interaction:triggers/interaction:trigger["+i+"]/interaction:activities/interaction:activity["+k+"]");
								if(!activityRef.isEmpty())
									activityRefList.add(activityRef);
								else
									System.err.println("Activity in trigger '" + triggerName + "' could not be assigned!");
							}
						}
					}
				}
			}
			
			if(!activityRefList.isEmpty())
			{
				triggerList.add(new TriggerDescription(triggerName,triggerPriority,triggerCondition,activityRefList));
			}
			else
				System.err.println("Discarded trigger '" + triggerName + "' because of missing activity assignment!");
		
		}
	}
	
	
	private void evaluateTriggers() 
	{		
		for(TriggerDescription triggerDescription : triggerList)
		{
			if(triggerDescription.getCondition().startsWith("collideWith:"))
			{
				String[] array = triggerDescription.getCondition().split(":");
				String objectName = array[1];
				
				List<TriggerAction> triggerActionList = getTriggerActionList(triggerDescription);
				
				if(!triggerActionList.isEmpty())
					SimulationBasics.getTriggerActionListMap().put(objectName, triggerActionList);
			}
			else if(triggerDescription.getCondition().startsWith("pressKey:"))
			{
				// trigger name
				String triggerName = triggerDescription.getName();
					
				// trigger key
				String[] array = triggerDescription.getCondition().split(":");
				String key = array[1].toUpperCase();
				
				try {
					
					if(!key.startsWith("KEY_"))
						key = "KEY_" + key;
					
					Field field = KeyInput.class.getField(key);
					int keyNumber = field.getInt(KeyInput.class);
					
					// trigger action
					List<TriggerAction> triggerActionList = getTriggerActionList(triggerDescription);
						
					if(!triggerActionList.isEmpty())
					{
						InputManager inputManager = sim.getInputManager();
						inputManager.addMapping(triggerName, new KeyTrigger(keyNumber));
						inputManager.addListener(new KeyActionListener(triggerActionList, triggerName), triggerName);
					}
				
				} catch (Exception e) {
					System.err.println("Invalid key '" + key + "' for trigger '" + triggerName + "'");
				}
			}
			else if(triggerDescription.getCondition().startsWith("pressButton:"))
			{
				// trigger name
				String triggerName = triggerDescription.getName();
					
				// trigger button
				String[] array = triggerDescription.getCondition().split(":");
				String button = array[1].toUpperCase();
				
				try {
					
					if(button.startsWith("BUTTON_"))
						button.replace("BUTTON_", "");
					
					int buttonNumber = Integer.parseInt(button);
					
					// trigger action
					List<TriggerAction> triggerActionList = getTriggerActionList(triggerDescription);
						
					if(!triggerActionList.isEmpty())
					{
						InputManager inputManager = sim.getInputManager();
						inputManager.addMapping(triggerName, new JoyButtonTrigger(0,buttonNumber));
						inputManager.addListener(new KeyActionListener(triggerActionList, triggerName), triggerName);
					}
				
				} catch (Exception e) {
					System.err.println("Invalid button '" + button + "' for trigger '" + triggerName + "'");
				}
			}
			else if(triggerDescription.getCondition().startsWith("pressPedal:"))
			{
				float triggeringThreshold = 0.2f;
				int controllerID;
				int axis;
				boolean invertAxis;
				float sensitivityFactor;
				
				// trigger name
				String triggerName = triggerDescription.getName();
					
				// trigger pedal
				String[] array = triggerDescription.getCondition().split(":");
				String pedal = array[1].toUpperCase();
				
				
				try {
					
					if(array.length >=3)
						triggeringThreshold = Float.parseFloat(array[2]);
				
				} catch (Exception e) {
					System.err.println("Invalid threshold '" + array[2] + "' for trigger '" + triggerName + "'");
				}
			
				
				try {
					
					if(pedal.startsWith("PEDAL_"))
						pedal.replace("PEDAL_", "");
					
					
					//SettingsLoader settingsLoader = Simulator.getDrivingTask().getSettingsLoader();
					if(pedal.equalsIgnoreCase("combinedPedals"))
					{ 
						controllerID = settingsLoader.getSetting(Setting.Joystick_combinedPedalsControllerID, 0);
						axis = settingsLoader.getSetting(Setting.Joystick_combinedPedalsAxis, 2);
						invertAxis = settingsLoader.getSetting(Setting.Joystick_invertCombinedPedalsAxis, false);
						sensitivityFactor = settingsLoader.getSetting(Setting.Joystick_combinedPedalsSensitivityFactor, 1.0f);
					}
					else if(pedal.equalsIgnoreCase("accelerator"))
					{ 
						controllerID = settingsLoader.getSetting(Setting.Joystick_acceleratorControllerID, 0);
						axis = settingsLoader.getSetting(Setting.Joystick_acceleratorAxis, 6);
						invertAxis = settingsLoader.getSetting(Setting.Joystick_invertAcceleratorAxis, true);
						sensitivityFactor = settingsLoader.getSetting(Setting.Joystick_acceleratorSensitivityFactor, 1.0f);
					}
					else if(pedal.equalsIgnoreCase("brake"))
					{ 
						controllerID = settingsLoader.getSetting(Setting.Joystick_brakeControllerID, 0);
						axis = settingsLoader.getSetting(Setting.Joystick_brakeAxis, 5);
						invertAxis = settingsLoader.getSetting(Setting.Joystick_invertBrakeAxis, true);
						sensitivityFactor = settingsLoader.getSetting(Setting.Joystick_brakeSensitivityFactor, 1.0f);
					}
					else if(pedal.equalsIgnoreCase("clutch"))
					{ 
						controllerID = settingsLoader.getSetting(Setting.Joystick_clutchControllerID, 0);
						axis = settingsLoader.getSetting(Setting.Joystick_clutchAxis, 7);
						invertAxis = settingsLoader.getSetting(Setting.Joystick_invertClutchAxis, true);
						sensitivityFactor = settingsLoader.getSetting(Setting.Joystick_clutchSensitivityFactor, 1.0f);
					}
					else
						throw new Exception();
					
					// trigger action
					List<TriggerAction> triggerActionList = getTriggerActionList(triggerDescription);
						
					if(!triggerActionList.isEmpty())
					{
						InputManager inputManager = sim.getInputManager();
						inputManager.addMapping(triggerName + "Up", new JoyAxisTrigger(controllerID, axis, invertAxis));
				    	inputManager.addMapping(triggerName + "Down", new JoyAxisTrigger(controllerID, axis, !invertAxis));
						inputManager.addListener(new AxisAnalogListener(triggerActionList, triggerName, triggeringThreshold, sensitivityFactor), 
								triggerName + "Up", triggerName + "Down");
					}
				
				} catch (Exception e) {
					System.err.println("Invalid pedal '" + array[1] + "' for trigger '" + triggerName + "'");
				}
			}
			else if(triggerDescription.getCondition().startsWith("remote:"))
			{
				String[] array = triggerDescription.getCondition().split(":");
				String objectName = array[1];
				
				List<TriggerAction> triggerActionList = getTriggerActionList(triggerDescription);
				
				if(!triggerActionList.isEmpty())
					SimulationBasics.getRemoteTriggerActionListMap().put(objectName, triggerActionList);
			}
			else if(triggerDescription.getCondition().startsWith("cameraWaypoint:"))
			{
				String[] array = triggerDescription.getCondition().split(":");
				String objectName = array[1];
				
				List<TriggerAction> triggerActionList = getTriggerActionList(triggerDescription);
				
				if(!triggerActionList.isEmpty())
					SimulationBasics.getCameraWaypointTriggerActionListMap().put(objectName, triggerActionList);
			}
		}
	}


	private List<TriggerAction> getTriggerActionList(TriggerDescription triggerDescription) 
	{
		List<TriggerAction> triggerActionList = new ArrayList<TriggerAction>();
		List<String> activityRefList = triggerDescription.getActivityRefList();
		for(String activityRef : activityRefList)
		{
			List<ActionDescription> actionDescriptionList = activityMap.get(activityRef);
			for(ActionDescription actionDescription : actionDescriptionList)
			{
				TriggerAction triggerAction = createTriggerAction(actionDescription);
				if(triggerAction != null)
					triggerActionList.add(triggerAction);
			}
		}
		return triggerActionList;
	}
    
	
	private TriggerAction createTriggerAction(ActionDescription actionDescription) 
	{
		String name = actionDescription.getName();
		float delay = actionDescription.getDelay();
		int repeat = actionDescription.getRepeat();
		Properties parameterList = actionDescription.getParameterList();

		TriggerAction triggerAction = null;
		
		// reflection to corresponding method
		try {

			// argument list with corresponding types
			Object argumentList[] = new Object[] {sim, delay, repeat, parameterList};
			Class<?> parameterTypes[] = new Class[] {SimulationBasics.class, Float.TYPE, Integer.TYPE, Properties.class};
			
			// get method to call
			Class<?> interactionMethodsClass = Class.forName("eu.opends.drivingTask.interaction.InteractionMethods");
			Method method = interactionMethodsClass.getMethod(name, parameterTypes);
			
			// call method and get return value
			triggerAction = (TriggerAction) method.invoke(new InteractionMethods(), argumentList);

		} catch (Throwable e) {
			e.printStackTrace();
		}

		
		return triggerAction;
	}


}
