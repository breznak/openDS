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

import java.util.Calendar;
import java.util.GregorianCalendar;

import eu.opends.car.Car;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class SpeedControlCenter 
{
	static Simulator sim;
	private static SpeedDifferenceComputer speedDifferenceComputer;
	private static int currentSpeedLimit = 0;
	private static int upcomingSpeedLimit = 0;
	private static String triggerNameBrakeTimer;
	private static Calendar brakeTimer = null;
	private static String triggerNameSpeedChangeTimer;
	private static int speedChangeValue;
	private static float initialSpeedValue;
	private static Calendar speedChangeTimer = null;
	
	
	public static void init (Simulator sim)
	{
		SpeedControlCenter.sim = sim;
		speedDifferenceComputer = new SpeedDifferenceComputer();
	}
	
	
	public static void update() 
	{
		Car car = sim.getCar();

		speedDifferenceComputer.update(car.getCurrentSpeedKmh());
		stopSpeedChangeTimer(car);
	}
	

	public static int getCurrentSpeedlimit() 
	{
		return currentSpeedLimit;
	}
	
	
	public static void setCurrentSpeedlimit(int speedLimit) 
	{
		currentSpeedLimit = speedLimit;
		
		speedDifferenceComputer.setSpeedLimit(speedLimit);
		
		if(speedDifferenceComputer.isReportAvailable())
		{
			float averageDifference = speedDifferenceComputer.getAverageDifference();
			float standardDeviation = speedDifferenceComputer.getStandardDeviation();
			
			Simulator.getDrivingTaskLogger().reportSpeedDifference(averageDifference, standardDeviation);
			
			speedDifferenceComputer.reset();
		}
	}
	

	public static int getUpcomingSpeedlimit() 
	{
		return upcomingSpeedLimit;
	}

	
	public static void setUpcomingSpeedlimit(int speedLimit) 
	{
		upcomingSpeedLimit = speedLimit;
	}
	

	
	public static void startBrakeTimer(String triggerName) 
	{
		triggerNameBrakeTimer = triggerName;
		brakeTimer = new GregorianCalendar();
	}
	
	
	public static void stopBrakeTimer() 
	{
		if(brakeTimer != null)
		{
			Simulator.getDrivingTaskLogger().reportReactionTime(triggerNameBrakeTimer, brakeTimer);
			brakeTimer = null;
		}
	}

	
	public static void startSpeedChangeTimer(String triggerName, int speedChange, Car car) 
	{
		triggerNameSpeedChangeTimer = triggerName;
		speedChangeValue = speedChange;
		initialSpeedValue = car.getCurrentSpeedKmh();
		speedChangeTimer = new GregorianCalendar();
	}
	
	
	public static void stopSpeedChangeTimer(Car car)
	{
		if(speedChangeTimer != null)
		{
			float currentSpeedValue = car.getCurrentSpeedKmh();
			if(((speedChangeValue >= 0) &&  (currentSpeedValue > initialSpeedValue+speedChangeValue))
				||
			   ((speedChangeValue < 0) &&  (currentSpeedValue < initialSpeedValue+speedChangeValue)))
			{
				Simulator.getDrivingTaskLogger().reportReactionTime(triggerNameSpeedChangeTimer, speedChangeTimer);
				speedChangeTimer = null;
			}
		}
	}

}
