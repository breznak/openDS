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

package eu.opends.tools;

import java.util.Comparator;

import com.jme3.math.Vector3f;


/**
 * This class compares instances from class Vector3f by the value of 
 * their z-coordinate, increasing or decreasing.
 * 
 * @author Rafael Math
 */
public class ZCoordinateComparator implements Comparator<Vector3f> 
{
	private boolean increasing;
	
	
	/**
	 * Creates a new comparator, defining whether the sort function 
	 * will sort increasing or decreasing.
	 * 
	 * @param increasing
	 * 			If true, sort function will sort increasing.
	 */
	public ZCoordinateComparator(boolean increasing)
	{
		this.increasing = increasing;
	}
	
	
	/**
	 * Compares two Vector3f instances
	 */
	public int compare(Vector3f arg0, Vector3f arg1) 
	{
		if(increasing)
			return Float.compare(arg0.getZ(), arg1.getZ());
		else
			return -Float.compare(arg0.getZ(), arg1.getZ());
	}

}
