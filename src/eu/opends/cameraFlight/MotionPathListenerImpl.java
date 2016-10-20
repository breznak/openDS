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

import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Vector3f;

import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class MotionPathListenerImpl implements MotionPathListener 
{
	private Simulator sim;
	

	public MotionPathListenerImpl(Simulator sim) 
	{
		this.sim = sim;
	}

	@Override
	public void onWayPointReach(MotionEvent motionTrack, int index) 
	{
		Vector3f pos = motionTrack.getPath().getWayPoint(index);
		sim.getSteeringTask().reportWaypoint(pos);
		System.err.println("reached WP" + index);
	}

}
