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

package eu.opends.car;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.jme3.math.FastMath;

import eu.opends.basics.SimulationBasics;
import eu.opends.drivingTask.scenario.ScenarioLoader;
import eu.opends.drivingTask.scenario.ScenarioLoader.CarProperty;
import eu.opends.main.Simulator;
import eu.opends.tools.PanelCenter;

/**
 * 
 * @author Rafael Math
 */
public class PowerTrain 
{
	private Car car;
	private ArrayList<FuelConsumption> fuelConsumptionList = new ArrayList<FuelConsumption>();
		
	// (default) displacement volume of the engine (in cm^3)
	private final float defaultDisplacementVolumeInCCM = 1800f;
	
	// update fuel panel every 500 ms
	private final int fuelConsumptionUpdateInterval = 500;
	
	// compute average fuel consumption over last 1000 ms
	private final int observationPeriod = 2000;
	
	private float totalFuelConsumption = 0;
	private float litersPer100Km = 0;
	private float litersPerHour = 0;
	private float previousVelocity = 0;
	private long lastPanelUpdate = 0;
	private float resultingPower = 0;
	
	
	private class FuelConsumption
	{
		private long timeStamp;
		private float distance;
		private float duration;
		private float fuelBurned;
		
		private FuelConsumption(long timeStamp, float distance, float duration, float fuelBurned) 
		{
			this.timeStamp = timeStamp;
			this.distance = distance;
			this.duration = duration;
			this.fuelBurned = fuelBurned;
		}
		
		private long getTimeStamp()
		{
			return timeStamp;
		}
		
		private float getDistance()
		{
			return distance;
		}
		
		private float getDuration()
		{
			return duration;
		}
		
		private float getFuelBurned()
		{
			return fuelBurned;
		}
	}


	public PowerTrain(Car car)
	{
		this.car = car;
	}

	public float getLitersPer100Km() 
	{
		return litersPer100Km;
	}
	
	
	public float getLitersPerHour() 
	{
		return litersPerHour;
	}
	
	
	public float getTotalFuelConsumption() 
	{
		return totalFuelConsumption;
	}
	
	
	public float getPAccel(float tpf, float gasPedalPressIntensity)
	{
		// engine power needed in current frame (in kJ/s)
		float pEngine = getPEngine(gasPedalPressIntensity);

		// fuel consumption (in L) in current frame
		computeFuelConsumption(tpf, pEngine);
		
		// power needed to overcome all resulting forces (in kJ/s)
		float pLoad = getPLoad(tpf);
		
		// PAccel (in kJ/s)
		// avoid negative values, as they block the brake in case no key is pressed
		float pAccel = Math.max(0, pEngine - pLoad);
		resultingPower = Math.max(0, pLoad - pEngine);
		
		//System.out.println(pAccel);
		
		// apply direction (negative = forward, positive = backward, 0 = none)
		return FastMath.sign(gasPedalPressIntensity) * pAccel;
	}
	
	
	public float getFrictionCoefficient()
	{	
		// return percentage (0.2 - 1.0) of the resulting power
		// as friction coefficient (never 0.0 !!!)
		float percentage = (resultingPower-10f)/50f;
		return Math.max(0.2f, Math.min(1.0f, percentage));
	}
	
	
	public void resetTotalFuelConsumption()
	{
		totalFuelConsumption = 0;
	}
	
	
	private void computeFuelConsumption(float deltaT, float PEngine)
	{
		// current time stamp
		long now = (new Date()).getTime();
		
		// compute distance traveled in current frame
		float distance = car.getDistanceOfCurrentFrameInKm();
		
		// lower heat value of fuel (in kJ/g)
		float lowerHeatValue = 43.7f;
		
		// amount of fuel burned in current frame (in g == kJ/s * s * g/kJ)
		float nettoFuelInGrams = PEngine * deltaT / lowerHeatValue;
		
		// regard additional injection of fuel to cool engine
		// linear fuel usage factor: value between 1 (for RPM <= 3600) and 1.5 (for RPM >= 4800) 
		float bruttoFuelInGrams = nettoFuelInGrams * getExtraFuelFactor();
		
		// convert gram to liter (density of fuel: 0.76 kg/L)
		float fuelInLiters = bruttoFuelInGrams/760f;
		
		// if engine is idle (no fuel consumption, declutched and engine on)
		if(isIdleEngine(fuelInLiters))
			fuelInLiters = getIdleFuelConsumption(deltaT);
		
		totalFuelConsumption += fuelInLiters;
		
		// add fuel consumption (in current frame) to the list
		fuelConsumptionList.add(new FuelConsumption(now, distance, deltaT, fuelInLiters));
		
		// if last panel update longer than specified time ago --> update
		if(lastPanelUpdate <= now - fuelConsumptionUpdateInterval)
		{
			// compute fuel consumption
			float[] fuelConsumption = computeLitersPerX();
			litersPer100Km = fuelConsumption[0];
			litersPerHour = fuelConsumption[1];
			
			// update panels
			PanelCenter.setLitersPer100Km(litersPer100Km);
			PanelCenter.setLitersPerHour(litersPerHour);
			PanelCenter.setTotalFuelConsumption(totalFuelConsumption);
			
			lastPanelUpdate = now;
		}
	}


	private boolean isIdleEngine(float fuelInLiters) 
	{
		// returns true if engine is idle (no fuel consumption, declutched and engine on)
		return (fuelInLiters == 0) && 
					(car.getTransmission().getRPM() <= car.getTransmission().getMinRPM()) &&
					car.isEngineOn();
	}

	
	private float getIdleFuelConsumption(float deltaT) 
	{
		// in idle mode (declutched and engine on) the consumption
		// is 1.08 liter per hour (= 0.0003 liter per second)
		return 0.0003f * deltaT;
	}


	private float getExtraFuelFactor() 
	{
		float rpm = car.getTransmission().getRPM();
		
		// return value between 1 (for RPM <= 3600) and 1.5 (for RPM >= 4800) 
		if(rpm<=3600)
			return 1.0f;
		else if(rpm >= 4800)
			return 1.5f;
		else
			return 1+(((rpm-3600)/1200f)*0.5f);
	}


	private float[] computeLitersPerX()
	{
		//only consider entries which are newer than the observation time stamp
		long observationTimeStamp = (new Date()).getTime() - observationPeriod;
	
		// initialize amount of fuel (in L) burned in observation period
		float totalFuel = 0;
	
		// initialize distance (in km) driven in observation period
		float totalDistance = 0;
		
		// initialize recorded duration in observation period
		float totalDuration = 0;
		
		// compute the sum of traveled distances and burned fuel for the 
		// given observation period; remove out-dated entries
		Iterator<FuelConsumption> iterator = fuelConsumptionList.iterator();
        while(iterator.hasNext()) 
        {
        	FuelConsumption fuelEntry = iterator.next();
            if(fuelEntry.getTimeStamp() <= observationTimeStamp)
            	iterator.remove();
            else
            {
            	totalDistance += fuelEntry.getDistance();
            	totalDuration += fuelEntry.getDuration();
            	totalFuel += fuelEntry.getFuelBurned();
            }
        }
		
        // avoid division by 0
        if(totalDistance == 0)
        	totalDistance = 0.000001f;
        
        float litersPer100Km = -1;
        
        if((car.getTransmission().getGear()!=0) && (car.getCurrentSpeedKmh() > 1))
        {
        	// liters per 100 km
        	litersPer100Km = totalFuel * (100/totalDistance);
        }
        
		// liters per hour
		float litersPerHour = (totalFuel / totalDuration) * 3600f;
		
		return new float[] {litersPer100Km, litersPerHour};
	}
	
	
	private float getPEngine(float gasPedalPressIntensity)
	{
		ScenarioLoader scenarioLoader = SimulationBasics.getDrivingTask().getScenarioLoader();
		float displacementVolumeInCCM = scenarioLoader.getCarProperty(CarProperty.engine_displacement, 
				defaultDisplacementVolumeInCCM);
		
		// rotations per minute in current frame
		float rotationsPerMinute = car.getTransmission().getRPM();
		
		// limit RPM to values between 1500 and 5100 (for bmep computation)
		rotationsPerMinute = Math.min(5100f, Math.max(rotationsPerMinute, 1500f));
		
		// rotations per second in current frame
		float rotationsPerSecond = rotationsPerMinute/60f;
		
		// brake mean effective pressure in current frame (in kN/m^2)
		float bmep = getBmep(rotationsPerSecond);
		
		// displacement volume (in m^3)
		float displacementVolume = displacementVolumeInCCM * 0.000001f;
		
		// maximum engine power in current frame (in kJ/s)
		float pEngine = bmep * rotationsPerSecond * displacementVolume/2f;
		
		//regard gas pedal state
		return pEngine * FastMath.abs(gasPedalPressIntensity);
	}
	
	
	private static float getBmep(float rotationsPerSecond) 
	{
		float a0 = -19950.8f;
		float a1 =  3479.90f;
		float a2 = -231.809f;
		float a3 =  8.25775f;
		float a4 = -0.169919f;
		float a5 =  0.00202259f;
		float a6 = -0.000012921f;
		float a7 =  0.0000000342208f;

		float N1 = rotationsPerSecond;
		float N2 = N1 * rotationsPerSecond;
		float N3 = N2 * rotationsPerSecond;
		float N4 = N3 * rotationsPerSecond;
		float N5 = N4 * rotationsPerSecond;
		float N6 = N5 * rotationsPerSecond;
		float N7 = N6 * rotationsPerSecond;
		
		// bmep (in kPa == kN/m^2)
		float bmep = a0 + a1*N1 + a2*N2 + a3*N3 + a4*N4 + a5*N5 + a6*N6 + a7*N7;
		
		return bmep;
	}
	
/*	
	private static float getBmep(float rotationsPerSecond) 
	{
		float a0 = -1200.51f;
		float a1 =  298.934f;
		float a2 = -17.5860f;
		float a3 =  0.563420f;
		float a4 = -0.0104629f;
		float a5 =  0.000113228f;
		float a6 = -0.000000664513f;
		float a7 =  0.00000000163097f;

		float N1 = rotationsPerSecond;
		float N2 = N1 * rotationsPerSecond;
		float N3 = N2 * rotationsPerSecond;
		float N4 = N3 * rotationsPerSecond;
		float N5 = N4 * rotationsPerSecond;
		float N6 = N5 * rotationsPerSecond;
		float N7 = N6 * rotationsPerSecond;
		
		// bmep (in kPa == kN/m^2)
		float bmep = a0 + a1*N1 + a2*N2 + a3*N3 + a4*N4 + a5*N5 + a6*N6 + a7*N7;
		
		return bmep;
	}
*/	
	
	private float getPLoad(float tpf) 
	{
		// speed of car (in m/s)
		float velocity = car.getCurrentSpeedMs();
		
		// mass of car
		float vehicleMass = car.getCarControl().getMass();
		
		// gravity constant
		float gravityConstant = Simulator.getGravityConstant();
		
		// power to overcome rolling resistance (in kW)
		float PTire = getPTire(velocity, vehicleMass, gravityConstant); 
		
		// power to overcome air resistance (in kW)
		float PAir = getPAir(velocity); 
		
		// power to overcome inertia (in kW)
		float PInertia = getPInertia(tpf, velocity, vehicleMass);
		
		// power to overcome potential energy (in kW)
		float PGrade = getPGrade(velocity, vehicleMass, gravityConstant); 
		
		// power for accessories (in kW)
		float PAccessories = 0.75f;
		
		// power to overcome inner friction of the engine (in kW)
		float PInner = getPInner();

		// PLoad (in kW == kJ/s)
		return PTire + PAir + PInertia + PGrade + PAccessories + PInner;
	}


	private float getPTire(float velocity, float vehicleMass, float gravityConstant) 
	{
		float rollingResistanceCoefficient = 0.008f;

		// power to overcome rolling resistance (in W == kg*m^2/s^3 ==  1 * kg * m/s^2 * m/s)
		float pTire = rollingResistanceCoefficient * vehicleMass * gravityConstant * velocity;
		
		// convert to kW
		return pTire * 0.001f;
	}
	
	
	private static float getPAir(float velocity) 
	{
		// density of air (in kg/m^3)
		float densityOfAir = 1.3f;
		
		// drag coefficient
		float dragCoefficient = 0.3f;
		
		// frontal area of the car (in m^2)
		float frontalArea = 2.0f;
		
		// power to overcome air resistance (in W == kg*m^2/s^3 ==  kg/m^3 * 1 * m^2 * m^3/s^3)
		float pAir = 0.5f * densityOfAir * dragCoefficient * frontalArea * FastMath.pow(velocity,3);
		
		// convert to kW
		return pAir * 0.001f;
	}
	

	private float getPInertia(float deltaT, float currentVelocity, float vehicleMass) 
	{
		// rotating mass (in kg)
		float rotatingMass = 1.03f * vehicleMass;
		
		// change of speed for current frame (in m/s)
		float deltaV = currentVelocity - previousVelocity;
		previousVelocity = currentVelocity;
		
		// power to overcome inertia (in W == kg*m^2/s^3 == kg * m/s * m/s * s^-1)
		float pInertia = 0.5f * rotatingMass * (deltaV * FastMath.abs(deltaV) / deltaT);
		
		// convert to kW
		return pInertia * 0.001f;
	}
	
	
	private float getPGrade(float velocity, float vehicleMass, float gravityConstant)
	{
		// angle of slope (in radians)
		float angle = car.getSlope();
		
		// for rear gear, slope angle is negated
		if(car.getTransmission().getGear() == -1)
			angle = -angle;
		
		// power to overcome potential energy (in W == kg*m^2/s^3 == kg * m/s^2 * m/s * 1)
		float pGrade = vehicleMass * gravityConstant * velocity * FastMath.sin(angle);
		
		// convert to kW
		return pGrade * 0.001f;
	}

	
	private float getPInner() 
	{
		// from Diss_Ulrich_Kramer.pdf, page 163
		float rpm = car.getTransmission().getRPM();
		return (0.0000007f*rpm*rpm - 0.0005f*rpm + 10.9f);
	}

}
