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

import java.util.ArrayList;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public interface TrafficObject 
{
	public Vector3f getPosition();
	
	public void setPosition(Vector3f position);

	public void setRotation(Quaternion quaternion);

	public float getMaxBrakeForce();

	public float getMass();

	public String getName();

	public void setToWayPoint(String wayPointID);
	
	public void update(float tpf, ArrayList<TrafficObject> vehicleList);
}
