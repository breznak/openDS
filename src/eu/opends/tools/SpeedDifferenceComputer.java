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

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.FastMath;

/**
 * This class represents a computer for differences between the car's speed and 
 * the maximum allowed speed. The mean deviation (arithmetic average) and standard
 * deviation can be computed.
 * 
 * @author Rafael Math
 */
public class SpeedDifferenceComputer 
{
	private int speedLimit = 0;
	private boolean setSpeedLimitToZeroAgain = false;
	private List<Float> speedList = new ArrayList<Float>();
	private boolean reportSent = false;
	
	
	/**
	 * Report, when the speed limit has changed. If the speed limit is changed 
	 * again to unlimited (=0), a report to the log file can be performed.
	 * 
	 * @param speedLimit
	 * 			Speed value to compute difference with car's speed.
	 */
	public void setSpeedLimit(int speedLimit) 
	{
		if((this.speedLimit != speedLimit) && (speedLimit == 0))
			setSpeedLimitToZeroAgain = true;
		else
			setSpeedLimitToZeroAgain = false;
			
		this.speedLimit = speedLimit;
	}

	
	/**
	 * On every simulation step, the difference between the car's speed and the 
	 * allowed speed will be added to a list.
	 * 
	 * @param carSpeed
	 * 			Speed value of the car.
	 */
	public void update(float carSpeed) 
	{
		if(speedLimit>0)
			speedList.add(carSpeed - speedLimit);
	}

	
	/**
	 * Checks whether a speed difference report for the log file is available.
	 * 
	 * @return
	 * 			True, if a speed difference report is available.
	 */
	public boolean isReportAvailable() 
	{
		if(!reportSent && setSpeedLimitToZeroAgain && (speedList.size()>0))
		{
			reportSent = true;
			return true;
		}
		else
			return false;
	}

	
	/**
	 * Computes the mean deviation (arithmetic average) of all recorded speed 
	 * differences.
	 * 
	 * @return
	 * 			Mean deviation (arithmetic average) of all speed difference values.
	 */
	public float getAverageDifference() 
	{
		int size = speedList.size();
		
		if(size>0)
		{
			float sum = 0;
			for(float difference : speedList)
			{
				sum += difference;
			}
			return sum/(float)size;
		}
		else 
			return 0;
	}
	

	/**
	 * Computes the standard deviation of all recorded speed differences.
	 * 
	 * @return
	 * 			Standard deviation of all speed difference values.
	 */
	public float getStandardDeviation() 
	{
		int size = speedList.size();
		
		if(size>0)
		{
			float avg = getAverageDifference();
			
			float sum = 0;
			for(float difference : speedList)
			{
				sum += FastMath.sqr(difference-avg);
			}
			
			return FastMath.sqrt(sum/(float)size);
		}
		else 
			return 0;
	}
	
	
	/**
	 * Resets the speed difference computer for the next speed limit segment.
	 */
	public void reset()
	{
		speedLimit = 0;
		setSpeedLimitToZeroAgain = false;
		speedList.clear();
		reportSent = false;
	}

}
