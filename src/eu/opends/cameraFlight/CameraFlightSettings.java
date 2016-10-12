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

package eu.opends.cameraFlight;


import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;

/**
 * 
 * @author Rafael Math
 */
public class CameraFlightSettings 
{
	private float speed = 50;
	private boolean automaticStart = true;
	private boolean automaticStop = true;
	private List<Vector3f> wayPointList = new ArrayList<Vector3f>();
	
	public CameraFlightSettings(Float speed, Boolean automaticStart, 
			Boolean automaticStop, List<Vector3f> wayPointList)
	{	
		if(speed != null)
			this.speed = speed;
		
		if(automaticStart != null)
			this.automaticStart = automaticStart;
		
		if(automaticStop != null)
			this.automaticStop = automaticStop;
		
		if(wayPointList != null)
			this.wayPointList = wayPointList;
	}

	
	/**
	 * @return the speed
	 */
	public float getSpeed() 
	{
		return speed;
	}

	
	/**
	 * @return the automaticStart
	 */
	public boolean isAutomaticStart() 
	{
		return automaticStart;
	}

	
	/**
	 * @return the automaticStop
	 */
	public boolean isAutomaticStop() 
	{
		return automaticStop;
	}

	
	/**
	 * @return the wayPointList
	 */
	public List<Vector3f> getWayPointList()
	{
		return wayPointList;
	}
	
	
}
