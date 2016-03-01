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

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import com.jme3.math.Vector3f;

import eu.opends.car.Car;
import eu.opends.tools.SpeedControlCenter;


/**
 * This class represents the presentation of a construction site warning. The distance 
 * between the moving car and a construction site is measured in order to update the 
 * distance information in the presentation. 
 * 
 * @author Rafael Math
 */
public class RoadWorksInformationPresentationModel extends PresentationModel 
{
	private Vector3f roadWorksStartPosition;
	private int appId;
	private int currentPrioritisation;
	private boolean reachedRoadWorksStartPosition;
	private float previousDistanceToStart;
	private int lengthOfRoadWorks;
	private int speedLimit;
	private Document roadWorksGeometry;
	private String detailedInfo;
	private Calendar beginOfConstructionWorks;
	private Calendar endOfConstructionWorks;
	
	
	private static final int F_122_ROADWORKS_INFORMATION_SYSTEM = 1;
	
	/**
	 * Initializes a construction site presentation model by setting the positions of the 
	 * car and the end of the construction site, the minimum distance from the construction
	 * site's end to cancel the presentation and the ID of the construction site.
	 * 
	 * @param car
	 * 			Car heading towards the construction site
	 * 
	 * @param roadWorksEndPosition
	 * 			Position of the end of the construction site
	 * 
	 * @param geometryFile 
	 * 			Path of geometry file
	 */
	public RoadWorksInformationPresentationModel(Car car, Vector3f roadWorksStartPosition, 
			Vector3f roadWorksEndPosition, String geometryFile)
	{
		// fixed parameters
		this.car = car;
		this.targetPosition = roadWorksEndPosition;
		this.roadWorksStartPosition = roadWorksStartPosition;
		this.minimumDistance = 10;
		this.appId = F_122_ROADWORKS_INFORMATION_SYSTEM;
		this.reachedRoadWorksStartPosition = false;
		this.previousDistanceToStart = Float.MAX_VALUE;
		
		// individual parameters
		// TODO: set these parameters for each road works instance
		this.currentPrioritisation = 65;
		this.lengthOfRoadWorks = 700;//5250;
		this.speedLimit = 80;
		this.roadWorksGeometry = getGeometry(geometryFile);
		this.detailedInfo = "Caution road works";
		this.beginOfConstructionWorks = new GregorianCalendar(2010,Calendar.DECEMBER,10,8,15,0);
		this.endOfConstructionWorks = new GregorianCalendar(2010,Calendar.DECEMBER,30,16,0,0);
	}
	
	
	/**
	 * Creates a road works presentation task on the SIM-TD HMI GUI. This
	 * data will only be sent to the HMI bundle once.
	 * 
	 * @return
	 * 			Presentation ID. If an error occurred, a negative value will be returned.
	 */
	@Override
	public long createPresentation()
	{
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		int reaminingSeconds = (int) ((lengthOfRoadWorks * 3.6f)/speedLimit);
		
		// parameters according to "presentationModel.xml"
		parameters.put("timeStart", System.currentTimeMillis() + 5500);
		parameters.put("timeEnd", System.currentTimeMillis() + (long) getTimeToTarget(targetPosition));
		parameters.put("currentPrioritisation", currentPrioritisation);
	
		// parameters according to "F122_model.xml"
		parameters.put("distanceToRoadworks", getRoundedDistanceToTarget(roadWorksStartPosition));
		parameters.put("presentationType", 0);
		parameters.put("lengthOfRoadworks", lengthOfRoadWorks);
		parameters.put("speedLimit", speedLimit);
		parameters.put("roadworksGeometry", roadWorksGeometry);
		parameters.put("detailedInfo", detailedInfo);
		parameters.put("beginOfConstructionWorks", beginOfConstructionWorks);
		parameters.put("endOfConstructionWorks", endOfConstructionWorks);
		parameters.put("egoPosition", 0);
		parameters.put("timeRemaining", reaminingSeconds);

		// send parameters to HMI bundle
		// TODO return controller.createPresentationTask(1, appId, parameters);
		return 0;
	}
	
	
	/**
	 * Loads the road works geometry from the given XML-file
	 * 
	 * @return
	 * 			
	 */
	private Document getGeometry(String filename) 
	{
		File xmlfile = new File(filename);
		Document doc = null;
		
		try {
			
			 SAXBuilder parser = new SAXBuilder();
			 doc = parser.build(xmlfile);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return doc;
	}


	/**
	 * Updates a road works presentation model if the time the car takes to arrive at the 
	 * construction site's end has changed or if the distance to the end of the site has 
	 * changed.
	 */
	@Override
	public void updatePresentation(long presentationID) 
	{
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		int distanceToRoadWorksStart = getRoundedDistanceToTarget(roadWorksStartPosition);
		long timeToRoadWorksEnd = (long) getTimeToTarget(targetPosition);
		int remainingSeconds = (int)(timeToRoadWorksEnd/1000);
		
		// parameters according to "presentationModel.xml"
		parameters.put("timeEnd", System.currentTimeMillis() + timeToRoadWorksEnd);
		
		speedLimit = SpeedControlCenter.getUpcomingSpeedlimit();
		parameters.put("speedLimit", speedLimit);
		
		// parameters according to "F122_model.xml"
		if(!reachedRoadWorks())
		{
			// car has not yet reached road works
			parameters.put("distanceToRoadworks", distanceToRoadWorksStart);
			parameters.put("presentationType", 0);
			parameters.put("egoPosition", 0);
			//parameters.put("timeRemaining",  remainingSeconds);
		}
		else
		{
			// car has reached road works
			parameters.put("distanceToRoadworks", 0);
			parameters.put("presentationType", 1);
			parameters.put("egoPosition", distanceToRoadWorksStart);
			parameters.put("timeRemaining",  remainingSeconds);
		}
		
		// send parameters to HMI bundle
		// TODO controller.update(presentationID, parameters);
	}

	
	/**
	 * Checks whether the moving car has already reached the beginning of the road works 
	 * site by comparing the distance to the start position with the distance from the 
	 * previous request. If the distance increased since last request, the start position
	 * lies behind the car and true will be returned for every following request.
	 * 
	 * @return
	 * 			True, if the moving car has already reached the start position of the
	 * 			road works site.
	 */
	private boolean reachedRoadWorks() 
	{
		if(reachedRoadWorksStartPosition)
			return true;
		else
		{
			float currentDistanceToStart = getExactDistanceToTarget(roadWorksStartPosition);
			if(previousDistanceToStart < currentDistanceToStart)
				reachedRoadWorksStartPosition = true;
			
			previousDistanceToStart = currentDistanceToStart;
			
			return reachedRoadWorksStartPosition;
		}
	}


	/**
	 * Generates a message containing the construction site warning and distance to it 
	 * or its end, respectively.
	 */
	@Override
	public String generateMessage() 
	{
		if(!reachedRoadWorks())
			return "Road works in " + getRoundedDistanceToTarget(roadWorksStartPosition) + " m";
		else
			return "Road works remaining for " + getRoundedDistanceToTarget(targetPosition) + " m";
	}

}