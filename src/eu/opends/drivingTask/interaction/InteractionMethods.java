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

package eu.opends.drivingTask.interaction;

import java.util.Properties;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import eu.opends.basics.SimulationBasics;
import eu.opends.drivingTask.DrivingTaskDataQuery.Layer;
import eu.opends.hmi.LocalDangerWarningPresentationModel;
import eu.opends.hmi.PresentationModel;
import eu.opends.hmi.RoadWorksInformationPresentationModel;
import eu.opends.main.Simulator;
import eu.opends.trigger.GetTimeUntilBrakeAction;
import eu.opends.trigger.GetTimeUntilSpeedChangeAction;
import eu.opends.trigger.ManipulateObjectTriggerAction;
import eu.opends.trigger.ManipulatePictureTriggerAction;
import eu.opends.trigger.MoveTrafficTriggerAction;
import eu.opends.trigger.OpenInstructionsScreenTriggerAction;
import eu.opends.trigger.PauseTriggerAction;
import eu.opends.trigger.PlaySoundAction;
import eu.opends.trigger.PresentationTaskAction;
import eu.opends.trigger.ReportSpeedTriggerAction;
import eu.opends.trigger.ReportTrafficLightTriggerAction;
import eu.opends.trigger.RequestGreenTrafficLightAction;
import eu.opends.trigger.ResetCarToResetPointAction;
import eu.opends.trigger.SendMessageTriggerAction;
import eu.opends.trigger.SetCrosswindTriggerAction;
import eu.opends.trigger.SetSpeedLimitAction;
import eu.opends.trigger.SetTVPTStimulusTriggerAction;
import eu.opends.trigger.SetupBrakeReactionTimerTriggerAction;
import eu.opends.trigger.SetupLaneChangeReactionTimerTriggerAction;
import eu.opends.trigger.SetupKeyReactionTimerTriggerAction;
import eu.opends.trigger.StartReactionMeasurementTriggerAction;
import eu.opends.trigger.StartRecordingTriggerAction;
import eu.opends.trigger.StopRecordingTriggerAction;
import eu.opends.trigger.TriggerAction;
import eu.opends.trigger.WriteToKnowledgeBaseTriggerAction;

/**
 * 
 * @author Rafael Math
 */
public class InteractionMethods 
{  

	@Action(
		name = "sendMessage", 
		layer = Layer.INTERACTION, 
		description = "Outputs text to the screen for the given amount of seconds",
		defaultDelay = 0,
		defaultRepeat = 0,
		param = {@Parameter(name="text", type="String", defaultValue="hello world", 
							description="Text to display on screen"),
				 @Parameter(name="duration", type="Integer", defaultValue="1", 
						 	description="Amount of seconds to show text (0 = infinite)")
				}
	)
	public TriggerAction sendMessage(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		try {

			// read message text
			parameter = "text";
			String message = parameterList.getProperty(parameter);
			if(message == null)
				throw new Exception();
				
			// read duration of display
			parameter = "duration";
			String durationString = parameterList.getProperty(parameter);
			if(durationString == null)
				durationString = setDefault("sendMessage", parameter, "1");
			int duration = Integer.parseInt(durationString);
			
			return new SendMessageTriggerAction(delay, repeat, message, duration);
			
		} catch (Exception e) {

			reportError("sendMessage", parameter);
			return null;
		}
	}
	
	
	
	@Action(
			name = "manipulateObject", 
			layer = Layer.SCENE, 
			description = "Manipulates translation, rotation, scale and/or visibility of the given model",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="id", type="String", defaultValue="model01", 
								description="ID of the model to manipulate"),
					 @Parameter(name="setTranslationX", type="Float", defaultValue="0.0", 
							 	description="Translate model to this x-coordinate"),
					 @Parameter(name="setTranslationY", type="Float", defaultValue="0.0", 
							 	description="Translate model to this y-coordinate"),
					 @Parameter(name="setTranslationZ", type="Float", defaultValue="0.0", 
							 	description="Translate model to this z-coordinate"),
					 @Parameter(name="setRotationX", type="Float", defaultValue="0.0", 
							 	description="Rotate model around x-axis"),
					 @Parameter(name="setRotationY", type="Float", defaultValue="0.0", 
							 	description="Rotate model around y-axis"),
					 @Parameter(name="setRotationZ", type="Float", defaultValue="0.0", 
							 	description="Rotate model around z-axis"),
					 @Parameter(name="setScaleX", type="Float", defaultValue="1.0", 
							 	description="Scale model to this x-coordinate"),
					 @Parameter(name="setScaleY", type="Float", defaultValue="1.0", 
							 	description="Scale model to this y-coordinate"),
					 @Parameter(name="setScaleZ", type="Float", defaultValue="1.0", 
							 	description="Scale model to this z-coordinate"),
					 @Parameter(name="addTranslationX", type="Float", defaultValue="0.0", 
							 	description="Adds this value to the models x-coordinate"),
					 @Parameter(name="addTranslationY", type="Float", defaultValue="0.0", 
							 	description="Adds this value to the models y-coordinate"),
					 @Parameter(name="addTranslationZ", type="Float", defaultValue="0.0", 
							 	description="Adds this value to the models z-coordinate"),
					 @Parameter(name="addRotationX", type="Float", defaultValue="0.0", 
							 	description="Adds this value to the models rotation around the x-axis"),
					 @Parameter(name="addRotationY", type="Float", defaultValue="0.0", 
							 	description="Adds this value to the models rotation around the y-axis"),
					 @Parameter(name="addRotationZ", type="Float", defaultValue="0.0", 
							 	description="Adds this value to the models rotation around the z-axis"),
					 @Parameter(name="addScaleX", type="Float", defaultValue="1.0", 
							 	description="Adds this value to the models x-coordinate scale"),
					 @Parameter(name="addScaleY", type="Float", defaultValue="1.0", 
							 	description="Adds this value to the models y-coordinate scale"),
					 @Parameter(name="addScaleZ", type="Float", defaultValue="1.0", 
							 	description="Adds this value to the models z-coordinate scale"),
					 @Parameter(name="visible", type="Boolean", defaultValue="true", 
								description="Makes the model (in)visible")
					}
		)
	public TriggerAction manipulateObject(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		Float[] nullArray = new Float[] {null, null, null};
		
		try {

			// look up id of object to manipulate --> if not available, quit
			parameter = "id";
			String id = parameterList.getProperty(parameter);
			if(id == null)
				throw new Exception();
			
			// create ManipulateObjectTriggerAction
			ManipulateObjectTriggerAction manipulateObjectTriggerAction = 
				new ManipulateObjectTriggerAction(sim, delay, repeat, id);

			// set translation, if available
			parameter = "setTranslation";
			String[] translationKeys = new String[] {"setTranslationX", "setTranslationY", "setTranslationZ"};
			Float[] translation = extractFloatValues(parameterList, translationKeys, nullArray);

			if(translation != null)
				manipulateObjectTriggerAction.setTranslation(translation);
				
			// set rotation, if available
			parameter = "setRotation";
			String[] rotationKeys = new String[] {"setRotationX", "setRotationY", "setRotationZ"};
			Float[] rotation = extractFloatValues(parameterList, rotationKeys, nullArray);

			if(rotation != null)
				manipulateObjectTriggerAction.setRotation(rotation);
			
			// set scale, if available
			parameter = "setScale";
			String[] scaleKeys = new String[] {"setScaleX", "setScaleY", "setScaleZ"};
			Float[] scale = extractFloatValues(parameterList, scaleKeys, nullArray);

			if(scale != null)
				manipulateObjectTriggerAction.setScale(scale);	

			// add translation, if available
			parameter = "addTranslation";
			String[] addTranslationKeys = new String[] {"addTranslationX", "addTranslationY", "addTranslationZ"};
			Float[] addTranslation = extractFloatValues(parameterList, addTranslationKeys, nullArray);

			if(addTranslation != null)
				manipulateObjectTriggerAction.addTranslation(addTranslation);
				
			// add rotation, if available
			parameter = "addRotation";
			String[] addRotationKeys = new String[] {"addRotationX", "addRotationY", "addRotationZ"};
			Float[] addRotation = extractFloatValues(parameterList, addRotationKeys, nullArray);

			if(addRotation != null)
				manipulateObjectTriggerAction.addRotation(addRotation);
			
			// add scale, if available
			parameter = "addScale";
			String[] addScaleKeys = new String[] {"addScaleX", "addScaleY", "addScaleZ"};
			Float[] addScale = extractFloatValues(parameterList, addScaleKeys, nullArray);

			if(addScale != null)
				manipulateObjectTriggerAction.addScale(addScale);
			
			// set visibility, if available
			parameter = "visible";
			String visible = parameterList.getProperty(parameter);
			if(visible != null)
				manipulateObjectTriggerAction.setVisibility(Boolean.parseBoolean(visible));
			
			return manipulateObjectTriggerAction;
			
		} catch (Exception e) {

			if(e instanceof NotAFloatException)
				parameter = ((NotAFloatException)e).getVariableName();

			reportError("manipulateObject", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "manipulatePicture", 
			layer = Layer.SCENE, 
			description = "Manipulates visibility of the given picture",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="id", type="String", defaultValue="picture01", 
								description="ID of the picture to manipulate"),
					 @Parameter(name="visible", type="Boolean", defaultValue="true", 
								description="Makes the picture (in)visible")
					}
		)
	public TriggerAction manipulatePicture(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		
		try {

			// look up id of picture to manipulate --> if not available, quit
			parameter = "id";
			String id = parameterList.getProperty(parameter);
			if(id == null)
				throw new Exception();

			// set visibility, if available
			parameter = "visible";
			String visible = parameterList.getProperty(parameter);
			if(visible == null)
				throw new Exception();

			return new ManipulatePictureTriggerAction(sim, delay, repeat, id, Boolean.parseBoolean(visible));
			
		} catch (Exception e) {

			reportError("manipulatePicture", parameter);
			return null;
		}
	}


	private Float[] extractFloatValues(Properties parameterList, String[] keys, 
			Float[] defaultValues) throws NotAFloatException 
	{
		Float[] values = new Float[keys.length];
		boolean keyFound = false;
		
		for(int i=0; i<keys.length; i++)
		{
			try {
				
				String stringValue = parameterList.getProperty(keys[i]);
				if(stringValue != null)
				{
					values[i] = Float.parseFloat(stringValue);
					keyFound = true;
				}
				else
					values[i] = defaultValues[i];
				
			} catch(Exception e) {
				
				throw new NotAFloatException(keys[i]);
			}

		}
		
		if(keyFound)
			return values;
		
		return null;
	}
	
	
	@Action(
			name = "pauseSimulation", 
			layer = Layer.INTERACTION, 
			description = "Stops the simulation for the given amount of time",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="duration", type="Integer", defaultValue="1", 
							 	description="Amount of seconds to pause (0 = infinite)")
					}
		)
	public TriggerAction pauseSimulation(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "duration";
		
		try {
			
			// read duration of pause
			String durationString = parameterList.getProperty(parameter);
			if(durationString == null)
				durationString = setDefault("pauseSimulation", parameter, "1");
			int duration = Integer.parseInt(durationString);
			
			// create PauseTriggerAction
			return new PauseTriggerAction(sim, delay, repeat, duration);
			
		} catch (Exception e) {
	
			reportError("pauseSimulation", parameter);
			return null;
		}
	}
	
	

	@Action(
			name = "startRecording", 
			layer = Layer.INTERACTION, 
			description = "Starts recording driver information",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="track", type="Integer", defaultValue="1", 
							 	description="Provide an ID to identify recording (optional)")
					}
		)
	public TriggerAction startRecording(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "track";
		
		try {
			
			// read duration of pause
			String trackString = parameterList.getProperty(parameter);
			if(trackString == null)
				trackString = setDefault("startRecording", parameter, "1");
			int trackNumber = Integer.parseInt(trackString);
			
			// create StartRecordingTriggerAction
			return new StartRecordingTriggerAction(delay, repeat, (Simulator) sim, trackNumber);
			
		} catch (Exception e) {
	
			reportError("startRecording", parameter);
			return null;
		}
	}
	

	@Action(
			name = "stopRecording", 
			layer = Layer.INTERACTION, 
			description = "Stops recording driver information",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {}
		)
	public TriggerAction stopRecording(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		// create StopRecordingTriggerAction
		return new StopRecordingTriggerAction(delay, repeat,(Simulator)sim);
	}


	/**
	 * Creates a ResetCar trigger action by parsing the node list for the name
	 * of the reset point to place the car when the trigger was hit.
	 * 
	 * @return
	 * 			ResetCar trigger action with the name of the reset point to 
	 * 			move the car to.
	 */
	@Action(
			name = "resetCar",
			layer = Layer.SCENARIO,
			description = "Moves the driving car to the given reset point",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="resetPointID", type="String", defaultValue="resetPoint01", 
								description="ID of the reset point to move the driving car to")
					}
		)
	public TriggerAction resetCar(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "resetPointID";
		
		try {
			
			// read ID of reset point
			String resetPointID = parameterList.getProperty(parameter);
			if(resetPointID == null)
				throw new Exception();
			
			// create ResetCarToResetPointAction
			return new ResetCarToResetPointAction(delay, repeat, resetPointID, (Simulator)sim);
			
		} catch (Exception e) {
	
			reportError("resetCar", parameter);
			return null;
		}
	}
	
	
	/**
	 * Creates a MoveTraffic trigger action list by parsing the node list for the name
	 * of the traffic object to move and the ID of the target way point.
	 * 
	 * 
	 * @return
	 * 			MoveTraffic trigger action list with name of the traffic object(s) and ID 
	 * 			of the way point.
	 */
	@Action(
			name = "moveTraffic",
			layer = Layer.SCENARIO,
			description = "Moves a traffic vehicle to the given way point",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="trafficObjectID", type="String", defaultValue="trafficObject01", 
								description="ID of the traffic vehicle to move"),
					 @Parameter(name="wayPointID", type="String", defaultValue="wayPoint01", 
								description="ID of the way point to move the traffic vehicle to")
					}
		)
	public TriggerAction moveTraffic(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		
		try {
			
			// read ID of traffic object
			parameter = "trafficObjectID";
			String trafficObjectID = parameterList.getProperty(parameter);
			if(trafficObjectID == null)
				throw new Exception();
			
			// read ID of traffic object
			parameter = "wayPointID";
			String wayPointID = parameterList.getProperty(parameter);
			if(wayPointID == null)
				throw new Exception();
			
			// create ResetCarToResetPointAction
			return new MoveTrafficTriggerAction(sim, delay, repeat, trafficObjectID, wayPointID);
			
		} catch (Exception e) {
	
			reportError("moveTraffic", parameter);
			return null;
		}
	}

	
	/**
	 * Creates a PresentationTask trigger action by parsing the node list for SIM-TD
	 * presentation models. As soon as the trigger is hit, the presentation task
	 * will be displayed in the HMI GUI.
	 * 
	 * @return
	 * 			PresentationTask trigger action with the presentation model.
	 */
	public TriggerAction startPresentationTask(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "presentationTaskType";
		
		try {
			
			// read type of presentation task
			String presentationTaskType = parameterList.getProperty(parameter);
			if(presentationTaskType == null)
				throw new Exception();
			
			PresentationModel presentationModel = null;
			
			// check for roadWorksInformation presentation task
			if(presentationTaskType.equalsIgnoreCase("roadWorksInformation"))
			{
				// extract roadWorksInformation presentation task
				presentationModel = extractRoadWorksInformation(parameterList);
			}
			
			// check for localDangerWarning presentation task
			if(presentationTaskType.equalsIgnoreCase("localDangerWarning"))
			{
				// extract localDangerWarning presentation task
				presentationModel = extractLocalDangerWarning(parameterList);
			}
			
			// create PresentationTaskAction
			return new PresentationTaskAction(delay, repeat,presentationModel,(Simulator)sim);
			
		} catch (Exception e) {
	
			reportError("startPresentationTask", parameter);
			return null;
		}
	}
	
	/**
	 * Extracts the parameters of the RoadWorksInformation presentation model from 
	 * the given node list.
	 * 
	 * @param roadWorksInformationList
	 * 			Node list containing parameters for the RoadWorksInformation presentation 
	 * 			model.
	 * 
	 * @return
	 * 			RoadWorksInformation presentation model with the given parameters.
	 */
	private PresentationModel extractRoadWorksInformation(Properties parameterList) 
	{
		Vector3f startPosition = new Vector3f(0,0,0);
		Vector3f endPosition = new Vector3f(0,0,0);
		String geometryFile = "";
		String parameter = "";
		
		try {
			
			// extract start position, if available
			parameter = "startPosition";
			String[] startPositionKeys = new String[] {"startPositionX", "startPositionY", "startPositionZ"};
			Float[] startPositionDefaults = new Float[] {0f,0f,0f};
			Float[] startPositionValues = extractFloatValues(parameterList, startPositionKeys, startPositionDefaults);
	
			if(startPositionValues != null)
				startPosition = new Vector3f(startPositionValues[0], startPositionValues[1], startPositionValues[2]);
			else
				throw new Exception();
			
			
			// extract start position, if available
			parameter = "endPosition";
			String[] endPositionKeys = new String[] {"endPositionX", "endPositionY", "endPositionZ"};
			Float[] endPositionDefaults = new Float[] {0f,0f,0f};
			Float[] endPositionValues = extractFloatValues(parameterList, endPositionKeys, endPositionDefaults);
	
			if(endPositionValues != null)
				endPosition = new Vector3f(endPositionValues[0], endPositionValues[1], endPositionValues[2]);
			else
				throw new Exception();
			
			
			// extract geometry file
			parameter = "geometryFile";
			String geometryFile_String = parameterList.getProperty(parameter);
			if(geometryFile_String != null)
				geometryFile = geometryFile_String;

			
			// Car will be set after it has been created in class "PresentationTaskAction"
			return new RoadWorksInformationPresentationModel(null, startPosition, endPosition, geometryFile);
			
		} catch (Exception e) {
			
			if(e instanceof NotAFloatException)
				parameter = ((NotAFloatException)e).getVariableName();
	
			reportError("startPresentationTask", parameter);
			return null;
		}
	}
	

	/**
	 * Extracts the parameters of the LocalDangerWarning presentation model from 
	 * the given node list.
	 * 
	 * @param localDangerWarningList
	 * 			Node list containing parameters for the LocalDangerWarning presentation 
	 * 			model.
	 * 
	 * @return
	 * 			LocalDangerWarning presentation model with the given parameters.
	 */
	private PresentationModel extractLocalDangerWarning(Properties parameterList) 
	{
		Vector3f targetPosition = new Vector3f(0,0,0);
		String parameter = "";
		
		try {
			
			// extract start position, if available
			parameter = "targetPosition";
			String[] targetPositionKeys = new String[] {"targetPositionX", "targetPositionY", "targetPositionZ"};
			Float[] targetPositionDefaults = new Float[] {0f,0f,0f};
			Float[] targetPositionValues = extractFloatValues(parameterList, targetPositionKeys, targetPositionDefaults);
	
			if(targetPositionValues != null)
				targetPosition = new Vector3f(targetPositionValues[0], targetPositionValues[1], targetPositionValues[2]);
			else
				throw new Exception();
			
			
			// extract name of local danger warning
			parameter = "localDangerWarningName";
			String localDangerWarningName = parameterList.getProperty(parameter);
			if(localDangerWarningName == null)
				throw new Exception();
			
			// extract start timing mode. Note that this is not the same as the "delay", since it depends on the distance and is updated.
			String displayDurationMillisStr = parameterList.getProperty("displayDurationMillis", "0");
			int displayDurationMillis = Integer.parseInt(displayDurationMillisStr);
			
			// Car will be set after it has been created in class "PresentationTaskAction"
			return new LocalDangerWarningPresentationModel(null, targetPosition, localDangerWarningName, displayDurationMillis);
			
		} catch (Exception e) {
			
			if(e instanceof NotAFloatException)
				parameter = ((NotAFloatException)e).getVariableName();
	
			reportError("startPresentationTask", parameter);
			return null;
		}
	}
	
	
	/**
	 * Creates a SetSpeedLimit trigger action by parsing the given node list. This
	 * trigger sets the global variable "currentSpeedLimit" (e.g. used by the 
	 * simulator's speed indicator) to the given value.
	 * 
	 * @return
	 * 			SetSpeedLimit trigger action with the given speed limit value.
	 */
	@Action(
			name = "setCurrentSpeedLimit",
			layer = Layer.SCENARIO,
			description = "Sets the speed limit to the given value",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="speedLimit", type="Integer", defaultValue="0", 
								description="Speed limit in kph (0 = unlimited)")
					}
		)
	public TriggerAction setCurrentSpeedLimit(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "speedLimit";
		
		try {
			
			// read speed limit
			String speedLimitString = parameterList.getProperty(parameter);
			if(speedLimitString == null)
				speedLimitString = setDefault("setCurrentSpeedLimit", parameter, "0");
			int speedLimit = Integer.parseInt(speedLimitString);
			
			// create SetSpeedLimitAction
			return new SetSpeedLimitAction(delay, repeat, speedLimit, true);
			
		} catch (Exception e) {
	
			reportError("setCurrentSpeedLimit", parameter);
			return null;
		}
		
	}

	
	/**
	 * Creates a SetSpeedLimit trigger action by parsing the given node list. This
	 * trigger sets the global variable "upcomingSpeedLimit" (e.g. used by the 
	 * RoadWorksInformation presentation model) to the given value.
	 * @return
	 * 			SetSpeedLimit trigger action with the given speed limit value.
	 */
	@Action(
			name = "setUpcomingSpeedLimit",
			layer = Layer.SCENARIO,
			description = "Sets the upcoming speed limit to the given value",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="speedLimit", type="Integer", defaultValue="0", 
								description="Speed limit in kph (0 = unlimited)")
					}
		)
	public TriggerAction setUpcomingSpeedLimit(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "speedLimit";
		
		try {
			
			// read speed limit
			String speedLimitString = parameterList.getProperty(parameter);
			if(speedLimitString == null)
				speedLimitString = setDefault("setUpcomingSpeedLimit", parameter, "0");
			int speedLimit = Integer.parseInt(speedLimitString);
			
			// create SetSpeedLimitAction
			return new SetSpeedLimitAction(delay, repeat, speedLimit, false);
			
		} catch (Exception e) {
	
			reportError("setUpcomingSpeedLimit", parameter);
			return null;
		}
		
	}
	
	
	
	/**
	 * Creates a GetTimeUntilBrake trigger action by parsing the given node list. 
	 * Time from hitting the trigger until the driver brakes will be recorded.
	 * 
	 * @return
	 * 			GetTimeUntilBrake trigger action.
	 */
	@Action(
			name = "measureTimeUntilBrake",
			layer = Layer.SCENARIO,
			description = "Measures time until brake was applied",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="triggerName", type="String", defaultValue="trigger01", 
								description="ID of trigger for identification in output file")
					}
		)
	public TriggerAction measureTimeUntilBrake(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		
		try {
			
			// extract name of trigger
			parameter = "triggerName";
			String triggerName = parameterList.getProperty(parameter);
			if(triggerName == null)
				throw new Exception();
			
			// create GetTimeUntilBrakeAction
			return new GetTimeUntilBrakeAction(delay, repeat, triggerName);
			
		} catch (Exception e) {
			
			reportError("measureTimeUntilBrake", parameter);
			return null;
		}
	}
	
	
	/**
	 * Creates a GetTimeUntilSpeedChange trigger action by parsing the given node list. 
	 * Time from hitting the trigger until the car reached the given speed change.
	 * 
	 * @return
	 * 			GetTimeUntilSpeedChange trigger action.
	 */
	@Action(
			name = "measureTimeUntilSpeedChange",
			layer = Layer.SCENARIO,
			description = "Measures time until speed was changed by the given amount",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="triggerName", type="String", defaultValue="trigger01", 
								description="ID of trigger for identification in output file"),
					 @Parameter(name="speedChange", type="Integer", defaultValue="20", 
								description="Amount of speed (in kph) that has to be in- or decreased")
					}
		)
	public TriggerAction measureTimeUntilSpeedChange(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract name of trigger
			parameter = "triggerName";
			String triggerName = parameterList.getProperty(parameter);
			if(triggerName == null)
				throw new Exception();
			
			// read speed change
			parameter = "speedChange";
			String speedChangeString = parameterList.getProperty(parameter);
			int speedChange = Integer.parseInt(speedChangeString);
			
			// create GetTimeUntilBrakeAction
			return new GetTimeUntilSpeedChangeAction(delay, repeat, triggerName, speedChange, (Simulator)sim);
			
		} catch (Exception e) {
			
			reportError("measureTimeUntilSpeedChange", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "playSound",
			layer = Layer.SCENE,
			description = "Plays a sound file specified in the scene layer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="soundID", type="String", defaultValue="soundEffect01", 
								description="ID of sound file to play")
					}
		)
	public TriggerAction playSound(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract name of local danger warning
			parameter = "soundID";
			String soundID = parameterList.getProperty(parameter);
			if(soundID == null)
				throw new Exception();

			// create PlaySoundAction
			return new PlaySoundAction(delay, repeat, soundID);
			
		} catch (Exception e) {
			
			reportError("playSound", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "requestGreenTrafficLight",
			layer = Layer.INTERACTION,
			description = "Requests a given traffic light to turn green",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="trafficLightID", type="String", defaultValue="TrafficLight10", 
								description="ID of traffic light to request for green")
					}
		)
	public TriggerAction requestGreenTrafficLight(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of traffic light
			parameter = "trafficLightID";
			String trafficLightID = parameterList.getProperty(parameter);
			if(trafficLightID == null)
				throw new Exception();

			// create RequestGreenTrafficLightAction
			return new RequestGreenTrafficLightAction(delay, repeat, (Simulator)sim, trafficLightID);
			
		} catch (Exception e) {
			
			reportError("requestGreenTrafficLight", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "startReactionMeasurement",
			layer = Layer.INTERACTION,
			description = "Starts the reaction measurement clock",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {}
		)
	public TriggerAction startReactionMeasurement(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		// create StartReactionMeasurementTriggerAction
		return new StartReactionMeasurementTriggerAction(delay, repeat, (Simulator)sim);
	}
	
	
	@Action(
			name = "setupReactionTimer",
			layer = Layer.INTERACTION,
			description = "Sets up a reaction timer (Deprecated)",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="reactionGroup", type="String", defaultValue="timer1", 
								description="ID of the timer for identification in output file"),
					 @Parameter(name="correctReaction", type="String", defaultValue="C", 
								description="list of keys triggering the correct reaction"),
					 @Parameter(name="failureReaction", type="String", defaultValue="F", 
								description="list of keys triggering the failure reaction")
					}
		)
	public TriggerAction setupReactionTimer(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of timer
			parameter = "timerID";
			String timerID = parameterList.getProperty(parameter);
			if(timerID == null)
				timerID = "timer1";
			
			// extract reaction group
			parameter = "reactionGroup";
			String reactionGroup = parameterList.getProperty(parameter);
			if(reactionGroup == null)
				throw new Exception();
			
			// extract list of keys triggering the correct reaction
			parameter = "correctReaction";
			String correctReaction = parameterList.getProperty(parameter);
			if(correctReaction == null)
				throw new Exception();
			
			// extract list of keys triggering the failure reaction
			parameter = "failureReaction";
			String failureReaction = parameterList.getProperty(parameter);
			if(failureReaction == null)
				throw new Exception();
			
			// extract optional comment
			parameter = "comment";
			String comment = parameterList.getProperty(parameter);
			if(comment == null)
				comment = "";
	
			// create SetupKeyReactionTimerTriggerAction
			return new SetupKeyReactionTimerTriggerAction(delay, repeat, timerID, reactionGroup, correctReaction, 
					failureReaction, comment, (Simulator)sim);
			
		} catch (Exception e) {
			
			reportError("setupReactionTimer", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "setupKeyReactionTimer",
			layer = Layer.INTERACTION,
			description = "Sets up a key reaction timer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="reactionGroup", type="String", defaultValue="timer1", 
								description="ID of the timer for identification in output file"),
					 @Parameter(name="correctReaction", type="String", defaultValue="C", 
								description="list of keys triggering the correct reaction"),
					 @Parameter(name="failureReaction", type="String", defaultValue="F", 
								description="list of keys triggering the failure reaction")
					}
		)
	public TriggerAction setupKeyReactionTimer(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of timer
			parameter = "timerID";
			String timerID = parameterList.getProperty(parameter);
			if(timerID == null)
				timerID = "timer1";
			
			// extract reaction group
			parameter = "reactionGroup";
			String reactionGroup = parameterList.getProperty(parameter);
			if(reactionGroup == null)
				throw new Exception();
			
			// extract list of keys triggering the correct reaction
			parameter = "correctReaction";
			String correctReaction = parameterList.getProperty(parameter);
			if(correctReaction == null)
				throw new Exception();
			
			// extract list of keys triggering the failure reaction
			parameter = "failureReaction";
			String failureReaction = parameterList.getProperty(parameter);
			if(failureReaction == null)
				throw new Exception();
			
			// extract optional comment
			parameter = "comment";
			String comment = parameterList.getProperty(parameter);
			if(comment == null)
				comment = "";
	
			// create SetupKeyReactionTimerTriggerAction
			return new SetupKeyReactionTimerTriggerAction(delay, repeat, timerID, reactionGroup, correctReaction, 
					failureReaction, comment, (Simulator)sim);
			
		} catch (Exception e) {
			
			reportError("setupKeyReactionTimer", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "setupLaneChangeReactionTimer",
			layer = Layer.INTERACTION,
			description = "Sets up a lane change reaction timer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="timerID", type="String", defaultValue="timer1", 
								description="ID of the timer for scheduling the measurement"),
					 @Parameter(name="congruenceClass", type="String", defaultValue="groupRed", 
								description="Groups similar measurements to same color in output visualization"),
					 @Parameter(name="startLane", type="String", defaultValue="centerLane", 
								description="Lane where lane change must start from"),
					 @Parameter(name="targetLane", type="String", defaultValue="leftLane", 
								description="Lane where lane change must end"),
					 @Parameter(name="minSteeringAngle", type="Float", defaultValue="0", 
								description="Minimal steering angle that has to be overcome"),
					 @Parameter(name="taskCompletionAfterTime", type="Float", defaultValue="0", 
							 	description="Task must be completed after x milliseconds (0 = no limit)"),
					 @Parameter(name="taskCompletionAfterDistance", type="Float", defaultValue="0", 
								description="Task must be completed after x meters (0 = no limit)"),
					 @Parameter(name="allowBrake", type="Boolean", defaultValue="true", 
								description="Driver may brake while changing lanes? (If false, failure reaction will be reported)"),
					 @Parameter(name="holdLaneFor", type="Float", defaultValue="2000", 
							 	description="Number of milliseconds the target lane must be kept"),
					 @Parameter(name="failSound", type="String", defaultValue="failSound01", 
								description="Sound file that will be played after failed/missed lane change (optional)"),
					 @Parameter(name="successSound", type="String", defaultValue="successSound01", 
								description="Sound file that will be played after successful lane change (optional)"),
					 @Parameter(name="comment", type="String", defaultValue="", 
								description="optional comment")
					}
		)
	public TriggerAction setupLaneChangeReactionTimer(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of timer
			parameter = "timerID";
			String timerID = parameterList.getProperty(parameter);
			if(timerID == null)
				timerID = "timer1";
			
			// extract reaction group
			parameter = "congruenceClass";
			String reactionGroupID = parameterList.getProperty(parameter);
			if(reactionGroupID == null)
				throw new Exception();
			
			// extract lane where lane change must start from
			parameter = "startLane";
			String startLane = parameterList.getProperty(parameter);
			if(startLane == null)
				throw new Exception();
			
			// extract lane where lane change must end
			parameter = "targetLane";
			String targetLane = parameterList.getProperty(parameter);
			if(targetLane == null)
				throw new Exception();
			
			// extract minimal steering angle that has to be overcome
			parameter = "minSteeringAngle";
			Float minSteeringAngle = Float.parseFloat(parameterList.getProperty(parameter));
			if(minSteeringAngle == null)
				minSteeringAngle = 0f;
			
			// task must be completed after x milliseconds (0 = no limit)
			parameter = "taskCompletionAfterTime";
			Float taskCompletionAfterTime = Float.parseFloat(parameterList.getProperty(parameter));
			if(taskCompletionAfterTime == null)
				taskCompletionAfterTime = 0f;
			
			// task must be completed after x meters (0 = no limit)
			parameter = "taskCompletionAfterDistance";
			Float taskCompletionAfterDistance = Float.parseFloat(parameterList.getProperty(parameter));
			if(taskCompletionAfterDistance == null)
				taskCompletionAfterDistance = 0f;
			
			// driver may brake while changing lanes? (if false, failure reaction will be reported) 
			parameter = "allowBrake";
			Boolean allowBrake = Boolean.parseBoolean(parameterList.getProperty(parameter));
			if(allowBrake == null)
				allowBrake = true;
			
			// number of milliseconds the target lane must be kept
			parameter = "holdLaneFor";
			Float holdLaneFor = Float.parseFloat(parameterList.getProperty(parameter));
			if(holdLaneFor == null)
				holdLaneFor = 0f;
			
			// sound file that will be played after failed/missed lane change (optional)
			parameter = "failSound";
			String failSound = parameterList.getProperty(parameter);
			
			// sound file that will be played after successful lane change (optional)
			parameter = "successSound";
			String successSound = parameterList.getProperty(parameter);
			
			// extract optional comment
			parameter = "comment";
			String comment = parameterList.getProperty(parameter);
			if(comment == null)
				comment = "";
	
			// create SetupLaneChangeReactionTimerTriggerAction
			return new SetupLaneChangeReactionTimerTriggerAction(delay, repeat, timerID, reactionGroupID, startLane, 
					targetLane, minSteeringAngle, taskCompletionAfterTime, taskCompletionAfterDistance, allowBrake, 
					holdLaneFor, failSound, successSound, comment, (Simulator)sim);
			
		} catch (Exception e) {
			
			reportError("setupLaneChangeReactionTimer", parameter);
			return null;
		}
	}

	
	
	@Action(
			name = "setupBrakeReactionTimer",
			layer = Layer.INTERACTION,
			description = "Sets up a brake reaction timer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="timerID", type="String", defaultValue="timer1", 
								description="ID of the timer for scheduling the measurement"),
					 @Parameter(name="congruenceClass", type="String", defaultValue="groupRed", 
								description="Groups similar measurements to same color in output visualization"),
					 @Parameter(name="startSpeed", type="Float", defaultValue="80", 
								description="Minimum speed the car must drive to start reaction measurement"),
					 @Parameter(name="targetSpeed", type="Float", defaultValue="60", 
								description="Maximum speed the car must drive to stop reaction measurement"),
					 @Parameter(name="mustPressBrakePedal", type="Boolean", defaultValue="true", 
								description="Driver must press brake pedal for successful reaction"),
					 @Parameter(name="taskCompletionAfterTime", type="Float", defaultValue="0", 
							 	description="Task must be completed after x milliseconds (0 = no limit)"),
					 @Parameter(name="taskCompletionAfterDistance", type="Float", defaultValue="0", 
								description="Task must be completed after x meters (0 = no limit)"),
					 @Parameter(name="allowLaneChange", type="Boolean", defaultValue="true", 
								description="Driver may change lanes while braking? (If false, failure reaction will be reported)"),
					 @Parameter(name="holdSpeedFor", type="Float", defaultValue="2000", 
							 	description="Number of milliseconds the target speed must be kept"),
					 @Parameter(name="failSound", type="String", defaultValue="failSound01", 
								description="Sound file that will be played after failed/missed braking (optional)"),
					 @Parameter(name="successSound", type="String", defaultValue="successSound01", 
								description="Sound file that will be played after successful braking (optional)"),
					 @Parameter(name="comment", type="String", defaultValue="", 
								description="optional comment")
					}
		)
	public TriggerAction setupBrakeReactionTimer(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of timer
			parameter = "timerID";
			String timerID = parameterList.getProperty(parameter);
			if(timerID == null)
				timerID = "timer1";
			
			// extract reaction group
			parameter = "congruenceClass";
			String reactionGroupID = parameterList.getProperty(parameter);
			if(reactionGroupID == null)
				throw new Exception();
			
			// extract speed at which braking must start
			parameter = "startSpeed";
			Float startSpeed = Float.parseFloat(parameterList.getProperty(parameter));
			if(startSpeed == null)
				startSpeed = 80.0f;
			
			// extract speed at which braking must end
			parameter = "targetSpeed";
			Float targetSpeed = Float.parseFloat(parameterList.getProperty(parameter));
			if(targetSpeed == null)
				targetSpeed = 60.0f;
			
			// extract whether brake pedal must be pressed
			parameter = "mustPressBrakePedal";
			Boolean mustPressBrakePedal = Boolean.parseBoolean(parameterList.getProperty(parameter));
			if(mustPressBrakePedal == null)
				mustPressBrakePedal = true;
			
			// task must be completed after x milliseconds (0 = no limit)
			parameter = "taskCompletionAfterTime";
			Float taskCompletionAfterTime = Float.parseFloat(parameterList.getProperty(parameter));
			if(taskCompletionAfterTime == null)
				taskCompletionAfterTime = 0f;
			
			// task must be completed after x meters (0 = no limit)
			parameter = "taskCompletionAfterDistance";
			Float taskCompletionAfterDistance = Float.parseFloat(parameterList.getProperty(parameter));
			if(taskCompletionAfterDistance == null)
				taskCompletionAfterDistance = 0f;
			
			// driver may change lanes while braking? (if false, failure reaction will be reported) 
			parameter = "allowLaneChange";
			Boolean allowLaneChange = Boolean.parseBoolean(parameterList.getProperty(parameter));
			if(allowLaneChange == null)
				allowLaneChange = true;
			
			// number of milliseconds the target speed must be kept
			parameter = "holdSpeedFor";
			Float holdSpeedFor = Float.parseFloat(parameterList.getProperty(parameter));
			if(holdSpeedFor == null)
				holdSpeedFor = 0f;
			
			// sound file that will be played after failed/missed lane change (optional)
			parameter = "failSound";
			String failSound = parameterList.getProperty(parameter);
			
			// sound file that will be played after successful lane change (optional)
			parameter = "successSound";
			String successSound = parameterList.getProperty(parameter);
			
			// extract optional comment
			parameter = "comment";
			String comment = parameterList.getProperty(parameter);
			if(comment == null)
				comment = "";
	
			// create SetupBrakeReactionTimerTriggerAction
			return new SetupBrakeReactionTimerTriggerAction(delay, repeat, timerID, reactionGroupID, startSpeed, 
					targetSpeed, mustPressBrakePedal, taskCompletionAfterTime, taskCompletionAfterDistance, 
					allowLaneChange, holdSpeedFor, failSound, successSound, comment, (Simulator)sim);
			
		} catch (Exception e) {
			
			reportError("setupBrakeReactionTimer", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "reportSpeed",
			layer = Layer.INTERACTION,
			description = "Writes an entry to the log if given speed exceeded or undershot, resp.",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="type", type="String", defaultValue="greaterThan", 
								description="'greaterThan' will report if given speed exceeded;" +
										" 'lessThan' will report if given speed undershot."),
					 @Parameter(name="speed", type="Float", defaultValue="50", 
								description="Speed (in km/h) to compare driving car's speed with.")
					}
		)
	public TriggerAction reportSpeed(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract type of comparison
			parameter = "type";
			String type = parameterList.getProperty(parameter);
			if(type == null || 
				(
					!type.equalsIgnoreCase("greaterThan") && 
					!type.equalsIgnoreCase("lessThan"))
				)
			{
				System.err.println("reportSpeed(): Illegal comparison type '" + 
						type + "'. Changed to 'greaterThan'");
				type = "greaterThan";
			}
			
			// extract speed to compare driving car's speed with
			parameter = "speed";
			Float speed = Float.parseFloat(parameterList.getProperty(parameter));
			if(speed == null)
				speed = 50.0f;
			
			speed = FastMath.abs(speed);
	
			// create ReportSpeedTriggerAction
			return new ReportSpeedTriggerAction(delay, repeat, type, speed, (Simulator)sim);
			
		} catch (Exception e) {
			
			reportError("reportSpeed", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "reportTrafficLight",
			layer = Layer.INTERACTION,
			description = "Writes an entry to the log if given traffic light is in the given state.",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="trafficLightID", type="String", defaultValue="", 
								description="ID of traffic light to check."),
					 @Parameter(name="trafficLightState", type="String", defaultValue="red", 
								description="State required for report.")
					}
		)
	public TriggerAction reportTrafficLight(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract trafficLightID
			parameter = "trafficLightID";
			String trafficLightID = parameterList.getProperty(parameter);
			if(trafficLightID == null)
				throw new Exception();
			
			// extract target state
			parameter = "trafficLightState";
			String trafficLightState = parameterList.getProperty(parameter);
			if(trafficLightState == null || 
				(
					!trafficLightState.equalsIgnoreCase("red") && 
					!trafficLightState.equalsIgnoreCase("yellow") && 
					!trafficLightState.equalsIgnoreCase("green"))
				)
			{
				System.err.println("reportTrafficLight(): Illegal traffic light state '" + 
						trafficLightState + "'. Changed to 'red'");
				trafficLightState = "red";
			}
			
			// create ReportSpeedTriggerAction
			return new ReportTrafficLightTriggerAction(delay, repeat, trafficLightID, trafficLightState);
			
		} catch (Exception e) {
			
			reportError("reportTrafficLight", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "openInstructionsScreen", 
			layer = Layer.INTERACTION, 
			description = "Shows up a screen with instructions",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="instructionID", type="String", defaultValue="", 
							 	description="Provide an ID to identify the instruction to show")
					}
		)
	public TriggerAction openInstructionsScreen(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "instructionID";
		
		try {
			
			// extract instructionID
			String instructionID = parameterList.getProperty(parameter);
			if(instructionID == null)
				throw new Exception();
			
			// create StartRecordingTriggerAction
			return new OpenInstructionsScreenTriggerAction(delay, repeat, (Simulator) sim, instructionID);
			
		} catch (Exception e) {
	
			reportError("openInstructionsScreen", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "setCrosswind", 
			layer = Layer.INTERACTION, 
			description = "applies crosswind to the user-controlled car",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="direction", type="String", defaultValue="", 
							 	description="direction wind is coming from (left, right)"),
					 @Parameter(name="force", type="Float", defaultValue="", 
							 	description="wind force in percent (0.0 .. 1.0)"),
					 @Parameter(name="duration", type="Integer", defaultValue="", 
								description="duration of wind event in milliseconds")
					}
		)
	public TriggerAction setCrosswind(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "direction";
		
		try {
			
			// extract direction
			String direction = parameterList.getProperty(parameter);
			if(direction == null)
				throw new Exception();
			
			// extract force
			parameter = "force";
			String forceString = parameterList.getProperty(parameter);
			if(forceString == null)
				throw new Exception();
			float force = Float.parseFloat(forceString);
			
			// extract duration
			parameter = "duration";
			String durationString = parameterList.getProperty(parameter);
			if(durationString == null)
				throw new Exception();
			int duration = Integer.parseInt(durationString);
						
			// create SetCrosswindTriggerAction
			return new SetCrosswindTriggerAction(delay, repeat, (Simulator) sim, direction, force, duration);
			
		} catch (Exception e) {
	
			reportError("setCrosswind", parameter);
			return null;
		}
	}
		
	
	@Action(
			name = "setTVPTStimulus", 
			layer = Layer.INTERACTION, 
			description = "sets one of the following Three-Vehicle-Platoon-Test-stimuli: CHMSL (Center High-Mount Stoplight), " +
					"FVTS (Follow Vehicle Turn Signal), LVD (Lead Vehicle Deceleration)",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="stimulusID", type="String", defaultValue="", 
							 	description="Provide an ID of the stimulus to trigger")
					}
		)
	public TriggerAction setTVPTStimulus(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "stimulusID";
		
		try {
			
			// extract stimulusID
			String stimulusID = parameterList.getProperty(parameter);
			if(stimulusID == null)
				throw new Exception();
			
			// create SetTVPTStimulusTriggerAction
			return new SetTVPTStimulusTriggerAction(delay, repeat, (Simulator) sim, stimulusID);
			
		} catch (Exception e) {
	
			reportError("setTVPTStimulus", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "writeToKnowledgeBase",
			layer = Layer.INTERACTION,
			description = "Inserts/edits a property in the knowledge base",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="path", type="String", defaultValue="", 
								description="Path of property to insert/edit"),
					 @Parameter(name="propertyName", type="String", defaultValue="", 
								description="Name of property to insert/edit"),
					 @Parameter(name="propertyValue", type="String", defaultValue="", 
								description="Value of property to insert/edit"),
					 @Parameter(name="propertyType", type="String", defaultValue="", 
								description="Type of property to insert/edit")
					}
		)
	public TriggerAction writeToKnowledgeBase(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract path of property
			parameter = "path";
			String path = parameterList.getProperty(parameter);
			if(path == null)
				throw new Exception();
			
			// extract name of property
			parameter = "propertyName";
			String propertyName = parameterList.getProperty(parameter);
			if(propertyName == null)
				throw new Exception();
			
			// extract value of property
			parameter = "propertyValue";
			String propertyValue = parameterList.getProperty(parameter);
			if(propertyValue == null)
				throw new Exception();
			
			// extract type of property
			parameter = "propertyType";
			String propertyType = parameterList.getProperty(parameter);
			if(propertyType == null)
				throw new Exception();
	
			// create WriteToKnowledgeBaseTriggerAction
			return new WriteToKnowledgeBaseTriggerAction(delay, repeat, (Simulator)sim, 
					path, propertyName, propertyValue, propertyType);
			
		} catch (Exception e) {
			
			reportError("writeToKnowledgeBase", parameter);
			return null;
		}
	}
	
	
	private void reportError(String methodName, String parameter)
	{
		System.err.println("Error in action \"" + methodName + "\": check parameter \"" + parameter + "\"");
	}
	
	
	private String setDefault(String methodName, String parameter, String defaultValue)
	{
		System.err.println("Warning in action \"" + methodName + "\": default \"" + defaultValue 
				+ "\" set to parameter \"" + parameter + "\"");
		return defaultValue;
	}
	
}