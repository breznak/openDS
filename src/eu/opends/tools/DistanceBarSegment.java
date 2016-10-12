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

public class DistanceBarSegment 
{
	private String name;
	private SegmentType type;
	private float minDistance;
	private float maxDistance;
	
	
	public enum SegmentType 
	{
		RED ("Textures/DistanceBar/red.png"), 
		GREEN ("Textures/DistanceBar/green.png"), 
		REDTOGREEN ("Textures/DistanceBar/red2green.png"), 
		GREENTORED ("Textures/DistanceBar/green2red.png");
		
		
		private String path;

		private SegmentType(String path)
		{
			this.path = path;
		}
		
		public String getPath()
		{
			return path;
		}
	}

	
	public DistanceBarSegment(String name, SegmentType type, float minDistance, float maxDistance) 
	{
		this.name = name;
		this.type = type;
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
	}


	public String getName() 
	{
		return name;
	}	
	
	
	public String getPath()
	{
		return type.getPath();
	}	
	
	
	public float getMinimumDistance() 
	{
		return minDistance;
	}
	
	
	public float getMaximumDistance() 
	{
		return maxDistance;
	}	
	
	
	public String toString()
	{
		return "[" + name + ", " +  type.toString() + ", " + minDistance + ", " + maxDistance + "]";
	}
}
