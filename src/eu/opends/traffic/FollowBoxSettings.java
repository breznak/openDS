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

package eu.opends.traffic;

import java.util.List;

/**
 * 
 * @author Rafael Math
 */
public class FollowBoxSettings 
{
	private List<Waypoint> wayPoints;
	private float minDistance;
	private float maxDistance;
	private float curveTension;
	private boolean pathCyclic;
	private boolean pathVisible;
	private String startWayPointID;
	
	
	public FollowBoxSettings(List<Waypoint> wayPoints, float minDistance, float maxDistance, float curveTension,
			boolean pathCyclic, boolean pathVisible, String startWayPointID) 
	{
		this.wayPoints = wayPoints;
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.curveTension = curveTension;
		this.pathCyclic = pathCyclic;
		this.pathVisible = pathVisible;
		this.startWayPointID = startWayPointID;
	}


	/**
	 * @return the wayPoints
	 */
	public List<Waypoint> getWayPoints() 
	{
		return wayPoints;
	}


	/**
	 * @return the curveTension
	 */
	public float getCurveTension()
	{
		return curveTension;
	}


	/**
	 * @return the pathCyclic
	 */
	public boolean isPathCyclic() 
	{
		return pathCyclic;
	}


	/**
	 * @return the pathVisible
	 */
	public boolean isPathVisible() 
	{
		return pathVisible;
	}

	
	public String getStartWayPointID() 
	{
		return startWayPointID;
	}
	

	public int getStartWayPointIndex() 
	{
		for(int i=0; i<wayPoints.size(); i++)
			if(wayPoints.get(i).getName().equals(startWayPointID))
				return i;
		
		return -1;
	}


	public float getMinDistance() 
	{
		return minDistance;
	}
	

	public float getMaxDistance()
	{
		return maxDistance;
	}

}
