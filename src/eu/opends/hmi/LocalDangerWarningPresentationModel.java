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

package eu.opends.hmi;

import java.util.HashMap;

import com.jme3.math.Vector3f;

import eu.opends.car.Car;
import eu.opends.knowledgeBase.KnowledgeBase;

/**
 * This class represents the presentation of a local danger warning. The distance 
 * between the moving car and a danger area is measured in order to update the distance 
 * information in the presentation. Each SIM-TD presentation model has its own appId 
 * and furthermore each local danger warning has a typeId and prioritisation.
 * 
 * @author Rafael Math
 */
public class LocalDangerWarningPresentationModel extends PresentationModel 
{
	private static final int DEFAULT_DISPLAY_DURATION_MILLIS = 10 * 1000;
	private static final int DISPLAY_MIN_DELAY_MILLIS = 5500;
	private static final int F_211_OBSTACLE_WARNING = 1;
	private static final int F_212_CONGESTION_WARNING = 2;
	private static final int F_213_IDENTIFICATION_OF_ROAD_WEATHER = 3;
	
	private static final int F_211_TYPE_OF_WARNING_BROKENVEHICLE = 1;
	private static final int F_211_TYPE_OF_WARNING_ROADWORKS = 2;
	private static final int F_211_TYPE_OF_WARNING_LOSTCARGO = 3;
	private static final int F_211_TYPE_OF_WARNING_MOBILE_ROADWORKS = 4;
	private static final int F_211_TYPE_OF_WARNING_PEDESTRIANS = 5;
	private static final int F_211_TYPE_OF_WARNING_ANIMALS = 6;
	private static final int F_211_TYPE_OF_WARNING_DANGER_AREA = 7;
	private static final int F_211_TYPE_OF_WARNING_ACCIDENT = 8;
	private static final int F_211_TYPE_OF_WARNING_STATIONARY_EMERGENCY_CAR = 9;
	private static final int F_211_TYPE_OF_WARNING_TRAFFIC_JAM = 10;
	private static final int F_213_TYPE_OF_WARNING_SNOW = 11;
	private static final int F_213_TYPE_OF_WARNING_CROSSWIND = 12;
	private static final int F_213_TYPE_OF_WARNING_RAIN = 13;
	private static final int F_213_TYPE_OF_WARNING_FOG = 14;

	
	private String name;
	private int appId;
	private int typeOfWarning;
	private String spokenWarning;
	private int currentPrioritisation;
	private int displayDurationMillis;
	
	
	/**
	 * Initializes a local danger warning presentation model by setting the positions 
	 * of the car and danger area, the minimum distance from the danger area to cancel 
	 * the presentation and the name of the warning. By this name, SIM-TD parameters
	 * like appId, typeOfWarning and currentPrioritisation can be looked up.
	 * 
	 * @param car
	 * 			Car heading towards the danger area
	 * 
	 * @param targetPosition
	 * 			Position of the danger area's center
	 * 
	 * @param localDangerWarningName
	 * 			Name of the local danger warning (internel representation)
	 * 
	 * @param displayDurationMillis
	 *          Determines the start time of the warning. Note that this is not the same as the "delay", since it depends on the distance 
	 *          to the target and may be updated later.
	 * 			A value n > 0 means: "display n seconds prior to the target arrival".
	 *                  0 means: "display immediately" (default to leave behavior like previously).
	 *                  -1 means: "use the default presentation time", which might be a user-adapted/warning-specific setting.
	 * 
	 * @throws Exception
	 * 			Throws exception, if name of local danger warning is unknown
	 */
	public LocalDangerWarningPresentationModel(Car car, Vector3f targetPosition, String localDangerWarningName, int displayDurationMillis)
	{
		this.car = car;
		this.targetPosition = targetPosition;
		this.minimumDistance = 10;
		this.name = localDangerWarningName;
		this.displayDurationMillis = displayDurationMillis;
		
		spokenWarning = "";
		if(localDangerWarningName.startsWith("BreakDown"))
		{
			appId = F_211_OBSTACLE_WARNING;
			typeOfWarning = F_211_TYPE_OF_WARNING_BROKENVEHICLE;
			spokenWarning = "simtd_F21_BrokenVehicle";
			currentPrioritisation = 0;
		}
		else if(localDangerWarningName.startsWith("RoadWorks"))
		{
			appId = F_211_OBSTACLE_WARNING;
			typeOfWarning = F_211_TYPE_OF_WARNING_ROADWORKS;
			spokenWarning = "simtd_F21_Roadworks";
			currentPrioritisation = 0;
		}
		else if(localDangerWarningName.startsWith("LostCargo"))
		{		
			appId = F_211_OBSTACLE_WARNING;
			typeOfWarning = F_211_TYPE_OF_WARNING_LOSTCARGO;
			spokenWarning = "simtd_F21_LostCargo";
			currentPrioritisation = 0;
		}
		else if(localDangerWarningName.startsWith("MobileRWs"))
		{		
			appId = F_211_OBSTACLE_WARNING;
			typeOfWarning = F_211_TYPE_OF_WARNING_MOBILE_ROADWORKS;
			spokenWarning = "simtd_F21_MovingRoadworks";
			currentPrioritisation = 0;
		}
		else if(localDangerWarningName.startsWith("Pedestr"))
		{		
			appId = F_211_OBSTACLE_WARNING;
			typeOfWarning = F_211_TYPE_OF_WARNING_PEDESTRIANS;
			spokenWarning = "simtd_F21_Persons";
			currentPrioritisation = 0;
		}
		else if(localDangerWarningName.startsWith("Animals"))
		{		
			appId = F_211_OBSTACLE_WARNING;
			typeOfWarning = F_211_TYPE_OF_WARNING_ANIMALS;
			spokenWarning = "simtd_F21_Animals";
			currentPrioritisation = 0;
		}
		else if(localDangerWarningName.startsWith("Danger"))
		{		
			appId = F_211_OBSTACLE_WARNING;
			typeOfWarning = F_211_TYPE_OF_WARNING_DANGER_AREA;
			spokenWarning = "simtd_F21_DangerSite";
			currentPrioritisation = 0;
		}
		else if(localDangerWarningName.startsWith("Accident"))
		{		
			appId = F_211_OBSTACLE_WARNING;
			typeOfWarning = F_211_TYPE_OF_WARNING_ACCIDENT;
			spokenWarning = "simtd_F21_Accident";
			currentPrioritisation = 0;
		}
		else if(localDangerWarningName.startsWith("Emergency"))
		{		
			appId = F_211_OBSTACLE_WARNING;
			typeOfWarning = F_211_TYPE_OF_WARNING_STATIONARY_EMERGENCY_CAR;
			spokenWarning = "simtd_F21_StationaryEmergencyVehicle";
			currentPrioritisation = 0;
		}
		else if(localDangerWarningName.startsWith("Congest"))
		{		
			appId = F_212_CONGESTION_WARNING;
			typeOfWarning = F_211_TYPE_OF_WARNING_TRAFFIC_JAM;
			spokenWarning = "simtd_F21_TrafficJam";
			currentPrioritisation = 0;
		}
		else if(localDangerWarningName.startsWith("Snow"))
		{		
			appId = F_213_IDENTIFICATION_OF_ROAD_WEATHER;
			typeOfWarning = F_213_TYPE_OF_WARNING_SNOW;
			spokenWarning = "simtd_F21_Ice";
			currentPrioritisation = 0;
		}
		else if(localDangerWarningName.startsWith("Crosswind"))
		{		
			appId = F_213_IDENTIFICATION_OF_ROAD_WEATHER;
			typeOfWarning = F_213_TYPE_OF_WARNING_CROSSWIND;
			spokenWarning = "simtd_F21_Wind";
			currentPrioritisation = 0;
		}
		else if(localDangerWarningName.startsWith("Rain"))
		{		
			appId = F_213_IDENTIFICATION_OF_ROAD_WEATHER;
			typeOfWarning = F_213_TYPE_OF_WARNING_RAIN;
			spokenWarning = "simtd_F21_Rain";
			currentPrioritisation = 0;
		}
		else if(localDangerWarningName.startsWith("Fog"))
		{		
			appId = F_213_IDENTIFICATION_OF_ROAD_WEATHER;
			typeOfWarning = F_213_TYPE_OF_WARNING_FOG;
			spokenWarning = "simtd_F21_Fog";
			currentPrioritisation = 0;
		}
		else
		{
			System.err.println("Unknown Local Danger Warning '" + localDangerWarningName + 
					"', will use default: 'Danger'");
			appId = F_211_OBSTACLE_WARNING;
			typeOfWarning = F_211_TYPE_OF_WARNING_DANGER_AREA;
			currentPrioritisation = 0;
		}
		spokenWarning = KnowledgeBase.expandString("$simtd_F21_Warning$: $" + spokenWarning + "$");
	}
	
	
	protected void fillPresentationTaskTimes(HashMap<String, Object> parameters, boolean isUpdate)
	{
		long timeNow = System.currentTimeMillis();
		long timeEndMillis = timeNow + (long) getTimeToTarget(targetPosition);
		long timeStartMillis = timeNow;
		if (displayDurationMillis == -1) {
			// default duration (recommended, more realistic)
			timeStartMillis = timeEndMillis - KnowledgeBase.User().getAdaptedDisplayTimeMillis(DEFAULT_DISPLAY_DURATION_MILLIS);
		} else if (displayDurationMillis > 0) {
			// fixed duration (e.g. for experiments)
			timeStartMillis = timeEndMillis - displayDurationMillis;
		}
		if (!isUpdate) timeStartMillis = Math.max(timeStartMillis, timeNow + DISPLAY_MIN_DELAY_MILLIS);
		parameters.put("timeStart", timeStartMillis);
		parameters.put("timeEnd", timeEndMillis);
		System.err.println("LDW presentation start=" + timeStartMillis + " (in " + (timeStartMillis-timeNow) + "ms), end=" + timeEndMillis + " (in " + (timeEndMillis-timeNow) + "ms), duration=" + (timeEndMillis-timeStartMillis) + "ms, dist=" + getRoundedDistanceToTarget(targetPosition));
	}
	
	
	/**
	 * Creates a local danger warning presentation task on the SIM-TD HMI GUI. This
	 * data will only be sent to the HMI bundle once.
	 * 
	 * @return
	 * 			Presentation ID. If an error occurred, a negative value will be returned.
	 */
	@Override
	public long createPresentation() 
	{
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		// parameters according to "presentationModel.xml"
		//parameters.put("timeStart", System.currentTimeMillis() + 5500);
		//parameters.put("timeEnd", System.currentTimeMillis() + (long) getTimeToTarget(targetPosition));
		fillPresentationTaskTimes(parameters, false);
		parameters.put("currentPrioritisation", currentPrioritisation);
	
		// parameters according to "localDangerWarnings.xml"
		parameters.put("distanceToEvent", getRoundedDistanceToTarget(targetPosition));
		parameters.put("presentationType", 0);
		parameters.put("typeOfWarning", typeOfWarning);
		
		// additional simTD+ parameters
		if (KnowledgeBase.User().getAdaptForBadSight()) parameters.put("adaptBadSight", 1);
		if (KnowledgeBase.User().getAdaptAddWarningsSpeech()) {
			parameters.put("ttsMessage", spokenWarning);
			parameters.put("ttsVoice", KnowledgeBase.User().getAdaptedTtsVoice());
			parameters.put("ttsSpeed", KnowledgeBase.User().getAdaptedSpeakingRate());
		}

		// send parameters to HMI bundle
		//TODO return controller.createPresentationTask(1, appId, parameters);
		return 0;
	}

	
	/**
	 * Updates a local danger warning presentation model if the time the car takes 
	 * to arrive at the danger area has changed or if the distance to the danger 
	 * area has changed.
	 */
	@Override
	public void updatePresentation(long presentationID) 
	{
		HashMap<String, Object> parameters = new HashMap<String, Object>();

		// parameters according to "presentationModel.xml" 
		//parameters.put("timeEnd", System.currentTimeMillis() + (long) getTimeToTarget(targetPosition));
		fillPresentationTaskTimes(parameters, true);
		
		// parameters according to "localDangerWarnings.xml"
		parameters.put("distanceToEvent", getRoundedDistanceToTarget(targetPosition));

		// send parameters to HMI bundle
		// TODO controller.update(presentationID, parameters);
	}


	/**
	 * Generates a message containing type of warning and distance to the danger area.
	 */
	@Override
	public String generateMessage() 
	{
		return "Caution: " + name + " in " + getRoundedDistanceToTarget(targetPosition) + " m";
	}

}