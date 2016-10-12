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

package eu.opends.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import eu.opends.drivingTask.scenario.IdealTrackContainer;
import eu.opends.main.DriveAnalyzer;


/**
 * This class computes the deviation of a car's driven track 
 * from a given ideal line. Both (track and ideal line) are 
 * considered as set of two-dimensional points.  
 * 
 * @author Rafael Math
*/	
public class DeviationComputer 
{
	private ArrayList<Vector3f> wayPoints;
	private Map<String, IdealTrackContainer> idealTrackMap;

	
	/**
	 * Creates a new deviation computer
	 * 
	 * @param wayPoints
	 * 			list of way points
	 */
	public DeviationComputer(ArrayList<Vector3f> wayPoints)
	{
		this.wayPoints = wayPoints;
		this.idealTrackMap = DriveAnalyzer.getDrivingTask().getScenarioLoader().getIdealTrackMap();
	}
	
		
	/**
	 * Writes all given way points to the console
	 */
	public void showAllWayPoints()
	{
		for(Vector3f wayPoint : wayPoints) 
			System.out.println("WP: "+wayPoint.toString());
	}
	
	
	/**
	 * Returns the list of all given way points as 3-dimensional vectors
	 * 
	 * @return
	 * 			list of way points
	 */
	public List<Vector3f> getWayPoints()
	{
		return wayPoints;
	}

	
	/**
	 * This method returns all ideal lines
	 * 
	 * @return
	 * 			list of all ideal lines
	*/	
	public ArrayList<IdealLine> getIdealLines()
	{
		ArrayList<IdealLine> idealLineList = new ArrayList<IdealLine>();
		
		for(Map.Entry<String, IdealTrackContainer> entry : idealTrackMap.entrySet())
		{
			String id = entry.getKey();
			IdealTrackContainer idealTrackContainer = entry.getValue();
			Float roadWidth = idealTrackContainer.getRoadWidth();
			ArrayList<Vector2f> idealPoints = idealTrackContainer.getIdealPoints();
			
			try {
				
				idealLineList.add(new IdealLine(id, roadWidth, idealPoints, wayPoints));
				
			} catch (Exception e) {
	
				System.out.println("Idealline '" + id + "': " + e.getMessage());
			}
		}
		
		return idealLineList;
	}	
}



