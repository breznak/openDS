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

package eu.opends.traffic;

import java.util.ArrayList;

import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class PhysicalTraffic extends Thread
{
	private static ArrayList<TrafficCarData> vehicleDataList = new ArrayList<TrafficCarData>();
    private static ArrayList<TrafficCar> vehicleList = new ArrayList<TrafficCar>();
	private boolean isRunning = true;
	private int updateIntervalMsec = 20;
	private long lastUpdate = 0;

       
	public PhysicalTraffic(Simulator sim)
	{
		for(TrafficCarData vehicleData : vehicleDataList)
		{
			// build and add traffic car
			vehicleList.add(new TrafficCar(sim, vehicleData));
		}
	}
	
	
    public static ArrayList<TrafficCarData> getVehicleDataList()
    {
    	return vehicleDataList;
    }

    
	public static ArrayList<TrafficCar> getVehicleList() 
	{
		return vehicleList;		
	}

	
	public TrafficCar getTrafficCar(String trafficCarName) 
	{
		for(TrafficCar vehicle : vehicleList)
		{
			if(vehicle.getName().equals(trafficCarName))
				return vehicle;
		}
		
		return null;
	}
	
	
	public void run()
	{
		if(vehicleList.size() >= 1)
		{
			/*
			for(TrafficCar vehicle : vehicleList)
				vehicle.showInfo();
			*/
			
			while (isRunning) 
			{
				long elapsedTime = System.currentTimeMillis() - lastUpdate;
				
				if (elapsedTime > updateIntervalMsec) 
				{
					lastUpdate = System.currentTimeMillis();
					
					// update every vehicle
					for(TrafficCar vehicle : vehicleList)
						vehicle.update(vehicleList);
				}
				else
				{
					// sleep until update interval has elapsed
					try {
						Thread.sleep(updateIntervalMsec - elapsedTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			//System.out.println("PhysicalTraffic closed");
		}
	}
	
	
	// TODO use thread instead
	public void update()
	{
		for(TrafficCar vehicle : vehicleList)
			vehicle.update(vehicleList);	
	}


	public synchronized void close() 
	{
		isRunning = false;
		
		// close all traffic cars
		for(TrafficCar vehicle : vehicleList)
			vehicle.close();
	}


}
