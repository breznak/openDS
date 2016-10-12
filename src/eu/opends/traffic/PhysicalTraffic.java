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

import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class PhysicalTraffic extends Thread
{
	private static ArrayList<TrafficCarData> vehicleDataList = new ArrayList<TrafficCarData>();
	private static ArrayList<PedestrianData> pedestrianDataList = new ArrayList<PedestrianData>();
    private static ArrayList<TrafficObject> trafficObjectList = new ArrayList<TrafficObject>();
	private boolean isRunning = true;
	private int updateIntervalMsec = 20;
	private long lastUpdate = 0;

       
	public PhysicalTraffic(Simulator sim)
	{
		for(TrafficCarData vehicleData : vehicleDataList)
		{
			// build and add traffic cars
			trafficObjectList.add(new TrafficCar(sim, vehicleData));
		}

		for(PedestrianData pedestrianData : pedestrianDataList)
		{
			// build and add pedestrians
			trafficObjectList.add(new Pedestrian(sim, pedestrianData));
		}
	}
	
	
    public static ArrayList<TrafficCarData> getVehicleDataList()
    {
    	return vehicleDataList;
    }
    
    
    public static ArrayList<PedestrianData> getPedestrianDataList()
    {
    	return pedestrianDataList;
    }

    
	public static ArrayList<TrafficObject> getTrafficObjectList() 
	{
		return trafficObjectList;		
	}

	
	public TrafficObject getTrafficObject(String trafficObjectName) 
	{
		for(TrafficObject trafficObject : trafficObjectList)
		{
			if(trafficObject.getName().equals(trafficObjectName))
				return trafficObject;
		}
		
		return null;
	}
	
	
	public void run()
	{
		if(trafficObjectList.size() >= 1)
		{
			/*
			for(TrafficObject trafficObject : trafficObjectList)
				trafficObject.showInfo();
			*/
			
			while (isRunning) 
			{
				long elapsedTime = System.currentTimeMillis() - lastUpdate;
				
				if (elapsedTime > updateIntervalMsec) 
				{
					lastUpdate = System.currentTimeMillis();
					
					float tpf = elapsedTime/1000f;
					// update every traffic object
					for(TrafficObject trafficObject : trafficObjectList)
						trafficObject.update(tpf, trafficObjectList);
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
	public void update(float tpf)
	{
		for(TrafficObject trafficObject : trafficObjectList)
			trafficObject.update(tpf, trafficObjectList);	
	}


	public synchronized void close() 
	{
		isRunning = false;
		
		// close all traffic objects
		for(TrafficObject trafficObject : trafficObjectList)
			if(trafficObject instanceof TrafficCar)
				((TrafficCar) trafficObject).close();
	}


}
