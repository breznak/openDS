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

package eu.opends.drivingTask.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jme3.math.Vector3f;

import eu.opends.drivingTask.DrivingTask;
import eu.opends.drivingTask.DrivingTaskDataQuery;
import eu.opends.drivingTask.DrivingTaskDataQuery.Layer;
import eu.opends.drivingTask.scene.SceneLoader;
import eu.opends.taskDescription.contreTask.SteeringTaskSettings;
import eu.opends.taskDescription.tvpTask.TVPTaskSettings;
import eu.opends.tools.DistanceBar;
import eu.opends.tools.DistanceBarSegment;
import eu.opends.tools.DistanceBarSegment.SegmentType;


/**
 * 
 * @author Rafael Math
 */
public class TaskLoader 
{
	private DrivingTaskDataQuery dtData;
	private SceneLoader sceneLoader;
	private SteeringTaskSettings contreTaskSettings;
	private TVPTaskSettings tvpTaskSettings;
	private HashMap<String, DistanceBar> distanceBarMap = new HashMap<String, DistanceBar>();
	

	public TaskLoader(DrivingTaskDataQuery dtData, DrivingTask drivingTask) 
	{
		this.dtData = dtData;
		this.sceneLoader = drivingTask.getSceneLoader();
		extractDistanceBars();
		extractContreTaskSettings();
		extractTVPTaskSettings();
	}
	
	
	private Vector3f getPointRef(String pointRef)
	{
		Map<String, Vector3f> pointMap = sceneLoader.getPointMap();
		
		if((pointRef != null) && (pointMap.containsKey(pointRef)))
			return pointMap.get(pointRef);
		else 
			return null;
	}
	
	
	private void extractDistanceBars() 
	{
		NodeList distanceBarNodes = (NodeList) dtData.xPathQuery(Layer.TASK, 
				"/task:task/task:distanceBars/task:distanceBar", XPathConstants.NODESET);

		for (int k = 1; k <= distanceBarNodes.getLength(); k++) 
		{
			String distanceBarId = dtData.getValue(Layer.TASK, 
					"/task:task/task:distanceBars/task:distanceBar["+k+"]/@id", String.class);
			
			Float width = dtData.getValue(Layer.TASK, 
					"/task:task/task:distanceBars/task:distanceBar["+k+"]/task:width", Float.class);
			
			Float height = dtData.getValue(Layer.TASK, 
					"/task:task/task:distanceBars/task:distanceBar["+k+"]/task:height", Float.class);
			
			Float left = dtData.getValue(Layer.TASK, 
					"/task:task/task:distanceBars/task:distanceBar["+k+"]/task:left", Float.class);
			
			Float bottom = dtData.getValue(Layer.TASK, 
					"/task:task/task:distanceBars/task:distanceBar["+k+"]/task:bottom", Float.class);
			
			Float rotation = dtData.getValue(Layer.TASK, 
					"/task:task/task:distanceBars/task:distanceBar["+k+"]/task:rotation", Float.class);

			Boolean showText = dtData.getValue(Layer.TASK, 
					"/task:task/task:distanceBars/task:distanceBar["+k+"]/task:showText", Boolean.class);
			
			
			NodeList segmentsNodes = (NodeList) dtData.xPathQuery(Layer.TASK, 
					"/task:task/task:distanceBars/task:distanceBar["+k+"]/task:segments/task:segment", XPathConstants.NODESET);

			ArrayList<DistanceBarSegment> distanceBarSegmentList = new ArrayList<DistanceBarSegment>();
			for (int l = 1; l <= segmentsNodes.getLength(); l++) 
			{
				String name = dtData.getValue(Layer.TASK, 
						"/task:task/task:distanceBars/task:distanceBar["+k+"]/task:segments/task:segment["+l+"]/@name", String.class);
				
				String type = dtData.getValue(Layer.TASK, 
						"/task:task/task:distanceBars/task:distanceBar["+k+"]/task:segments/task:segment["+l+"]/@type", String.class);
				SegmentType segmentType = SegmentType.valueOf(type.toUpperCase());
				
				Float minValue = dtData.getValue(Layer.TASK, 
						"/task:task/task:distanceBars/task:distanceBar["+k+"]/task:segments/task:segment["+l+"]/@minValue", Float.class);
				
				Float maxValue = dtData.getValue(Layer.TASK, 
						"/task:task/task:distanceBars/task:distanceBar["+k+"]/task:segments/task:segment["+l+"]/@maxValue", Float.class);
				
				distanceBarSegmentList.add(new DistanceBarSegment(name, segmentType, minValue, maxValue));
			}
			
			if(distanceBarId != null)
			{
				DistanceBar db = new DistanceBar(distanceBarSegmentList, width, height, left, bottom, rotation, showText);
				distanceBarMap.put(distanceBarId, db);			
			}
		}
	}
	
	
	private void extractContreTaskSettings()
	{		
		try {

			String steeringTaskPath = "/task:task/task:steeringTask";
			
			String startPointLoggingId = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:startPoint/@ref", String.class);
			Vector3f startPointLogging = getPointRef(startPointLoggingId);
			
			String endPointLoggingId = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:endPoint/@ref", String.class);
			Vector3f endPointLogging = getPointRef(endPointLoggingId);
			
			String steeringTaskType = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:steeringTaskType", String.class);
			
			Float distanceToObjects = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:distanceToObjects", Float.class);
			
			Float objectOffset = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:objectOffset", Float.class);
			
			Float heightOffset = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:heightOffset", Float.class);
			
			String targetObjectId = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:targetObject/@id", String.class);
			
			Float targetObjectSpeed = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:targetObject/@speed", Float.class);
			
			Float targetObjectMaxLeft = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:targetObject/@maxLeft", Float.class);
			
			Float targetObjectMaxRight = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:targetObject/@maxRight", Float.class);
			
			String steeringObjectId = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:steeringObject/@id", String.class);
			
			Float steeringObjectSpeed = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:steeringObject/@speed", Float.class);
			
			Float steeringObjectMaxLeft = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:steeringObject/@maxLeft", Float.class);
			
			Float steeringObjectMaxRight = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:steeringObject/@maxRight", Float.class);
			
			String trafficLightObjectId = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:trafficLightObject/@id", String.class);
			
			Integer pauseAfterTargetSet = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:pauseAfterTargetSet", Integer.class);
			
			Integer blinkingInterval = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:blinkingInterval", Integer.class);
			
			
			String databaseUrl = "";
			String databaseUser = "";
			String databasePassword = "";
			String databaseTable = "";
			Boolean writeToDB = false;
			
			// check whether DB node exists
			Node databaseNode = (Node) dtData.xPathQuery(Layer.TASK, 
					steeringTaskPath + "/task:database", XPathConstants.NODE);

			if(databaseNode != null)
			{
				databaseUrl = dtData.getValue(Layer.TASK, 
						steeringTaskPath + "/task:database/@url", String.class);
				
				databaseUser = dtData.getValue(Layer.TASK, 
						steeringTaskPath + "/task:database/@user", String.class);
				
				databasePassword = dtData.getValue(Layer.TASK, 
						steeringTaskPath + "/task:database/@password", String.class);
				
				databaseTable = dtData.getValue(Layer.TASK, 
						steeringTaskPath + "/task:database/@table", String.class);
				
				writeToDB = true;
			}
			
			String conditionName = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:conditionName", String.class);
			
			Long conditionNumber = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:conditionNumber", Long.class);
			
			String ptStartPointId = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:primaryTask/task:startPoint/@ref", String.class);
			Vector3f ptStartPoint = getPointRef(ptStartPointId);
			
			String ptEndPointId = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:primaryTask/task:endPoint/@ref", String.class);
			Vector3f ptEndPoint = getPointRef(ptEndPointId);

			Boolean isPeripheralMode = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:primaryTask/task:isPeripheralMode", Boolean.class);
			
			Integer ptIconWidth = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:primaryTask/task:iconWidth", Integer.class);
			
			Integer ptIconHeight = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:primaryTask/task:iconHeight", Integer.class);
			
			Integer ptIconDistFromLeftFrameBorder = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:primaryTask/task:iconDistFromLeftFrameBorder", Integer.class);
			
			Integer ptIconDistFromRightFrameBorder = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:primaryTask/task:iconDistFromRightFrameBorder", Integer.class);
			
			Integer ptLightMinPause = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:primaryTask/task:lightMinPause", Integer.class);
			
			Integer ptLightMaxPause = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:primaryTask/task:lightMaxPause", Integer.class);

			Integer ptLightDuration = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:primaryTask/task:lightDuration", Integer.class);
			
			Float ptBlinkingThreshold = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:primaryTask/task:blinkingThreshold", Float.class);
			
			Integer ptMinimumBlinkingDuration = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:primaryTask/task:minBlinkingDuration", Integer.class);
			
			String stStartPointId = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:secondaryTask/task:startPoint/@ref", String.class);
			Vector3f stStartPoint = getPointRef(stStartPointId);
			
			String stEndPointId = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:secondaryTask/task:endPoint/@ref", String.class);
			Vector3f stEndPoint = getPointRef(stEndPointId);
			
			Integer stWaitForNextLandmark = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:secondaryTask/task:waitForNextLandmark", Integer.class);
			
			Integer stMinTimeOfAppearance = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:secondaryTask/task:minTimeOfAppearance", Integer.class);
			
			Float stMaxVisibilityDistance = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:secondaryTask/task:maxVisibilityDistance", Float.class);
			
			Float stMaxSelectionDistance = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:secondaryTask/task:maxSelectionDistance", Float.class);
			
			Float stMaxAngle = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:secondaryTask/task:maxAngle", Float.class);
			
			
			List<String> stLandmarkObjectsList = new ArrayList<String>();
			NodeList landmarkObjectNodes = (NodeList) dtData.xPathQuery(Layer.TASK, 
					steeringTaskPath + "/task:secondaryTask/task:landmarkObjects/task:landmarkObject", XPathConstants.NODESET);

			for (int k = 1; k <= landmarkObjectNodes.getLength(); k++) 
			{
				String landmarkObjectId = dtData.getValue(Layer.TASK, 
						steeringTaskPath + "/task:secondaryTask/task:landmarkObjects/task:landmarkObject["+k+"]/@id", String.class);
				
				if(landmarkObjectId != null)
					stLandmarkObjectsList.add(landmarkObjectId);
				else 
					throw new Exception("Error in landmark objects list");
			}
			
			
			List<String> stLandmarkTexturesList = new ArrayList<String>();
			NodeList landmarkTextureNodes = (NodeList) dtData.xPathQuery(Layer.TASK, 
					steeringTaskPath + "/task:secondaryTask/task:landmarkTextures/task:landmarkTexture", XPathConstants.NODESET);

			for (int k = 1; k <= landmarkTextureNodes.getLength(); k++) 
			{
				String landmarkTexturesUrl = dtData.getValue(Layer.TASK, 
						steeringTaskPath + "/task:secondaryTask/task:landmarkTextures/task:landmarkTexture["+k+"]/@url", String.class);
				
				if(landmarkTexturesUrl != null)
					stLandmarkTexturesList.add(landmarkTexturesUrl);
				else 
					throw new Exception("Error in landmark textures list");
			}
			
			
			List<String> stDistractorTexturesList = new ArrayList<String>();
			NodeList distractorTextureNodes = (NodeList) dtData.xPathQuery(Layer.TASK, 
					steeringTaskPath + "/task:secondaryTask/task:distractorTextures/task:distractorTexture", XPathConstants.NODESET);

			for (int k = 1; k <= distractorTextureNodes.getLength(); k++) 
			{
				String distractorTexturesUrl = dtData.getValue(Layer.TASK, 
						steeringTaskPath + "/task:secondaryTask/task:distractorTextures/task:distractorTexture["+k+"]/@url", String.class);
				
				if(distractorTexturesUrl != null)
					stDistractorTexturesList.add(distractorTexturesUrl);
				else 
					throw new Exception("Error in distractor textures list");
			}
			
			Boolean additionalTable = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:additionalTable", Boolean.class);
			
			Float maxDeviation = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:maxDeviation", Float.class);
			
			contreTaskSettings = new SteeringTaskSettings(startPointLogging, endPointLogging, steeringTaskType, 
					distanceToObjects, objectOffset, heightOffset, targetObjectId, targetObjectSpeed, 
					targetObjectMaxLeft, targetObjectMaxRight, steeringObjectId, steeringObjectSpeed, 
					steeringObjectMaxLeft, steeringObjectMaxRight, trafficLightObjectId, pauseAfterTargetSet, 
					blinkingInterval, writeToDB, databaseUrl, databaseUser, databasePassword, databaseTable, 
					conditionName, conditionNumber, ptStartPoint, ptEndPoint, isPeripheralMode, ptIconWidth, ptIconHeight, 
					ptIconDistFromLeftFrameBorder, ptIconDistFromRightFrameBorder, ptLightMinPause, ptLightMaxPause, 
					ptLightDuration, ptBlinkingThreshold, ptMinimumBlinkingDuration, stStartPoint, stEndPoint, 
					stWaitForNextLandmark, stMinTimeOfAppearance, stMaxVisibilityDistance, stMaxSelectionDistance, 
					stMaxAngle, stLandmarkObjectsList, stLandmarkTexturesList, stDistractorTexturesList, additionalTable, 
					maxDeviation);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public SteeringTaskSettings getSteeringTaskSettings() 
	{
		return contreTaskSettings;
	}
	
	
	private void extractTVPTaskSettings()
	{		
		try {
			String steeringTaskPath = "/task:task/task:threeVehiclePlatoon";
			
			String leadingCarName = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:leadingCar/@id", String.class);

			Float minDistanceToLeadingCar = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:leadingCar/@minDistance", Float.class);
			
			Float maxDistanceToLeadingCar = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:leadingCar/@maxDistance", Float.class);
			
			String followerCarName = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:followerCar/@id", String.class);
			
			Float minDistanceToFollowerCar = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:followerCar/@minDistance", Float.class);
			
			Float maxDistanceToFollowerCar = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:followerCar/@maxDistance", Float.class);
			
			Float laneOffsetX = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:laneOffset/@x", Float.class);
			
			Integer brakeLightMinDuration = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:brakeLight/@minDuration", Integer.class);
			
			String resetBrakeLightOnReactionString = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:brakeLight/@resetOnReaction", String.class);
			Boolean resetBrakeLightOnReaction = resetBrakeLightOnReactionString.equalsIgnoreCase("")?null:Boolean.parseBoolean(resetBrakeLightOnReactionString);
			
			Integer turnSignalDuration = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:turnSignal/@duration", Integer.class);
			
			String resetTurnSignalOnReactionString = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:turnSignal/@resetOnReaction", String.class);
			Boolean resetTurnSignalOnReaction = resetTurnSignalOnReactionString.equalsIgnoreCase("")?null:Boolean.parseBoolean(resetTurnSignalOnReactionString);
			
			Float leadingCarLowerSpeedLimitSpeedReduction = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:speedReduction/@targetSpeedMinRegular", Float.class);
			
			Float leadingCarUpperSpeedLimitSpeedReduction = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:speedReduction/@targetSpeedMaxRegular", Float.class);
			
			Float leadingCarLowerSpeedLimitEmergencyBrake = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:speedReduction/@targetSpeedMinEmergency", Float.class);
			
			Float leadingCarUpperSpeedLimitEmergencyBrake = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:speedReduction/@targetSpeedMaxEmergency", Float.class);
			
			Float minSpeedForSpeedReduction = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:speedReduction/@minSpeed", Float.class);
			
			Integer speedReductionDuration = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:speedReduction/@duration", Integer.class);
			
			Integer minTimeAllConditionsMet = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:speedReduction/@minTimeAllConditionsMet", Integer.class);
			
			String resetSpeedReductionOnReactionString = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:speedReduction/@resetOnReaction", String.class);
			Boolean resetSpeedReductionOnReaction = resetSpeedReductionOnReactionString.equalsIgnoreCase("")?null:Boolean.parseBoolean(resetSpeedReductionOnReactionString);

			Integer maxReactionTime = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:maxReactionTime", Integer.class);
			
			Float longitudinalToleranceLowerBound = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:deviationTolerance/task:longitudinal/@lowerBound", Float.class);
			
			Float longitudinalToleranceUpperBound = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:deviationTolerance/task:longitudinal/@upperBound", Float.class);
			
			Float lateralToleranceLowerBound = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:deviationTolerance/task:lateral/@lowerBound", Float.class);
			
			Float lateralToleranceUpperBound = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:deviationTolerance/task:lateral/@upperBound", Float.class);

			Float startPositionZ = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:logging/task:startPosition/@z", Float.class);
			
			String shutDownAfterXSecondsString = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:logging/task:startPosition/@shutDownAfterXSeconds", String.class);
			Integer shutDownAfterXSeconds = shutDownAfterXSecondsString.equalsIgnoreCase("")?null:Integer.parseInt(shutDownAfterXSecondsString);
			
			Float endPositionZ = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:logging/task:endPosition/@z", Float.class);
			
			String shutDownAtEndString = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:logging/task:endPosition/@shutDownWhenReached", String.class);
			Boolean shutDownAtEnd = shutDownAtEndString.equalsIgnoreCase("")?null:Boolean.parseBoolean(shutDownAtEndString);
			
			Float hideDistanceTextPositionZ = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:distanceIndicator/task:hideAtPosition/@z", Float.class);
			
			Float distanceTextScale = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:distanceIndicator/task:showText/@scale", Float.class);
			
			Integer distanceTextTop = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:distanceIndicator/task:showText/@top", Integer.class);
			
			Integer distanceTextBottom = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:distanceIndicator/task:showText/@bottom", Integer.class);
			
			Integer distanceTextLeft = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:distanceIndicator/task:showText/@left", Integer.class);
			
			Integer distanceTextRight = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:distanceIndicator/task:showText/@right", Integer.class);
			
			String longitudinalDistanceBarId = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:distanceIndicator/task:showLongitudinalDistanceBar/@id", String.class);
			DistanceBar longitudinalDistanceBar = distanceBarMap.get(longitudinalDistanceBarId);
			
			String lateralDeviationBarId = dtData.getValue(Layer.TASK,
					steeringTaskPath + "/task:distanceIndicator/task:showLateralDeviationBar/@id", String.class);         
			DistanceBar lateralDeviationBar = distanceBarMap.get(lateralDeviationBarId);
			
			Integer loggingRate = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:logging/task:loggingRate", Integer.class);
	
			
			String databaseUrl = "";
			String databaseUser = "";
			String databasePassword = "";
			String databaseTable = "";
			Boolean writeToDB = false;
			
			// check whether DB node exists
			Node databaseNode = (Node) dtData.xPathQuery(Layer.TASK, 
					steeringTaskPath + "/task:logging/task:database", XPathConstants.NODE);

			if(databaseNode != null)
			{
				databaseUrl = dtData.getValue(Layer.TASK, 
						steeringTaskPath + "/task:logging/task:database/@url", String.class);
				
				databaseUser = dtData.getValue(Layer.TASK, 
						steeringTaskPath + "/task:logging/task:database/@user", String.class);
				
				databasePassword = dtData.getValue(Layer.TASK, 
						steeringTaskPath + "/task:logging/task:database/@password", String.class);
				
				databaseTable = dtData.getValue(Layer.TASK, 
						steeringTaskPath + "/task:logging/task:database/@table", String.class);
				
				writeToDB = true;
			}
			
			String conditionName = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:logging/task:condition/@name", String.class);
			
			Integer conditionNumber = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:logging/task:condition/@number", Integer.class);

			String reportTemplate = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:logging/task:reportTemplate", String.class);
			
			Boolean additionalTable = dtData.getValue(Layer.TASK, 
					steeringTaskPath + "/task:logging/task:additionalTable", Boolean.class);
			
			
			tvpTaskSettings = new TVPTaskSettings(leadingCarName, leadingCarLowerSpeedLimitSpeedReduction,
					leadingCarUpperSpeedLimitSpeedReduction, leadingCarLowerSpeedLimitEmergencyBrake, 
					leadingCarUpperSpeedLimitEmergencyBrake, minSpeedForSpeedReduction, speedReductionDuration, 
					minTimeAllConditionsMet, resetBrakeLightOnReaction, resetTurnSignalOnReaction, 
					resetSpeedReductionOnReaction, minDistanceToLeadingCar, maxDistanceToLeadingCar, 
					followerCarName, minDistanceToFollowerCar, maxDistanceToFollowerCar, laneOffsetX, 
					brakeLightMinDuration, turnSignalDuration, maxReactionTime, longitudinalToleranceLowerBound, 
					longitudinalToleranceUpperBound, lateralToleranceLowerBound, lateralToleranceUpperBound, 
					startPositionZ, endPositionZ, shutDownAfterXSeconds, shutDownAtEnd, hideDistanceTextPositionZ, distanceTextScale, 
					distanceTextTop, distanceTextBottom, distanceTextLeft, distanceTextRight, longitudinalDistanceBar,
					lateralDeviationBar, loggingRate, writeToDB, databaseUrl, databaseUser, databasePassword, 
					databaseTable, conditionName, conditionNumber, reportTemplate, additionalTable);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public TVPTaskSettings getTVPTaskSettings() 
	{
		return tvpTaskSettings;
	}

	
}
