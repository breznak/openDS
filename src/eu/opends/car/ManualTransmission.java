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

import com.jme3.math.FastMath;

import eu.opends.drivingTask.scenario.ScenarioLoader;
import eu.opends.drivingTask.scenario.ScenarioLoader.CarProperty;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;
import eu.opends.tools.PanelCenter;

/**
 * 
 * @author Rafael Math
 */
public class ManualTransmission 
{
	// for speed percentage computation
	private static final float speedAt100PercentMarker = 140f; //FIXME 140f;  --> 80f for trucks
	
	// gears with transmission values
	private static int numberOfGears;
	private static Float[] forwardGears;
	private static float neutralGear;
	private static float reverseGear;
	private static final float wheelCircumference = 380.0f;
	
	// rotation per minute settings
	private static float maxRPM = 7500f;
	private static float minRPM = 0f;
	
	private Car car;
	private int gear;
	private Integer rememberGearShiftPosition = null;
	private boolean isAutomaticTransmission;
	private float selectedTransmission;
	private float currentRPM = 0;
	private float previousRPM = 0;
	
	
	public ManualTransmission(Car car)
	{
		this.car = car;
		
		// load settings from driving task file
		ScenarioLoader scenarioLoader = Simulator.getDrivingTask().getScenarioLoader();
		isAutomaticTransmission = scenarioLoader.isAutomaticTransmission(SimulationDefaults.transmission_automatic);
		reverseGear = scenarioLoader.getReverseGear(SimulationDefaults.transmission_reverseGear);
		neutralGear = 0.0f;
		forwardGears = scenarioLoader.getForwardGears(SimulationDefaults.transmission_forwardGears);
		numberOfGears = forwardGears.length;
		minRPM = 0; //scenarioLoader.getCarProperty(CarProperty.engine_minRPM, SimulationDefaults.engine_minRPM);
		maxRPM = scenarioLoader.getCarProperty(CarProperty.engine_maxRPM, SimulationDefaults.engine_maxRPM);
		
		setGear(1, isAutomaticTransmission, false);
	}
	
	
	public float getPowerPercentage(int gear, float currentSpeed)
	{	
		float x = currentSpeed;
		float x2 = x * currentSpeed;
		float x3 = x2 * currentSpeed;
		float x4 = x3 * currentSpeed;
		float x5 = x4 * currentSpeed;
		float x6 = x5 * currentSpeed;
		
		float powerPercentage = 0;
		
		switch (gear)
		{
			case 1  : powerPercentage = -2892.78f*x5+678.61f*x4+558.43f*x3-216.84f*x2+17.78f*x+0.58f; break;
			case 2  : powerPercentage = -4206.38f*x6+4837.65f*x5-1897.61f*x4+310.7f*x3-34.7f*x2+3.82f*x+0.43f; break;
			case 3  : powerPercentage = -3.17f*x3 + 1.44f*x2 + 0.12f*x + 0.27f; break;
			case 4  : powerPercentage = -1.27f*x3 + 0.64f*x2 + 0.24f*x + 0.20f; break;
			case 5  : powerPercentage = -0.47f*x3 + 0.12f*x2 + 0.35f*x + 0.11f; break;
			case 6  : powerPercentage = -0.27f*x3 + 0.24f*x2 + 0.18f*x + 0.01f; break;
			case -1 : powerPercentage = -1154.84f*x4 + 134.09f*x3 + 21.63f*x2 + 0.5f*x + 0.57f; break;
			case 0  : powerPercentage = 0; break;
		}
		
		return Math.min(1.0f, Math.max(0.0f,powerPercentage*(1.0f - car.getClutchPedalIntensity())));
	}
	
	
	public float getRPMPercentage()
	{
		return Math.min(getRPM()/maxRPM, 1f);
	}
	
	
	public boolean isAutomatic() 
	{
		return isAutomaticTransmission;
	}
	
	
	public void setAutomatic(boolean isAutomatic)
	{
		isAutomaticTransmission = isAutomatic;
		
		if(isAutomatic == false && rememberGearShiftPosition != null)
			gear = rememberGearShiftPosition;
	}

	
	public int getGear()
	{
		return gear;
	}
	
	
	public void updateRPM(float tpf)
	{
		if(gear == 0)
		{
			currentRPM = (0.2f + car.getAcceleratorPedalIntensity())*maxRPM*0.5f;
		}
		else
		{
			currentRPM = Math.min(Math.abs(car.getCarControl().getCurrentVehicleSpeedKmHour() * 
					selectedTransmission / (wheelCircumference * 0.00006f)), maxRPM);
			
			// clutch
			float traction = car.getTraction();
			if(traction < 1.0f)
				currentRPM = (traction * currentRPM) + ((1.0f-traction) * (0.2f + car.getAcceleratorPedalIntensity())*maxRPM*0.5f);
			
			System.err.println("traction: " + car.getTraction());
		}
			
		//if(car.isEngineOn())
		//	currentRPM = Math.max(currentRPM,minRPM);
		
		// do not allow rpm changes of more than 5000 rpm in one second
		float rpmChange = 5000f * tpf;
		if((previousRPM  - currentRPM) > rpmChange)
			currentRPM = previousRPM - rpmChange;
		else if((currentRPM - previousRPM) > rpmChange)
			currentRPM = previousRPM + rpmChange;
		
		previousRPM = currentRPM;
		
		if(currentRPM < 500 && car.isEngineOn())
			car.setEngineOn(false);
	}
	
	
	public float getRPM()
	{
		return currentRPM;
	}


	public void shiftUp(boolean isAutomatic) 
	{
		setGear(getGear()+1, isAutomatic, false);
	}
	
	
	public void shiftDown(boolean isAutomatic) 
	{
		int gear;
		
		// if automatic transmission --> do not shift down to N and R automatically
		if(isAutomatic)
			gear = Math.max(1, getGear()-1);
		else	
			gear = Math.max(-1, getGear()-1);
		
		setGear(gear, isAutomatic, false);
	}


	public void performAcceleration(float pAccel) 
	{
		float currentEngineSpeed = getRPM();
		float currentVehicleSpeed = FastMath.abs(car.getCarControl().getCurrentVehicleSpeedKmHour());
		float speedPercentage = currentVehicleSpeed/speedAt100PercentMarker;
		
		int gear = getGear();		
		
		// change gear if necessary (only in automatic mode)
		if(isAutomaticTransmission)
		{
			int bestGear = findBestPowerGear(speedPercentage);
			setGear(bestGear, isAutomaticTransmission, false);
		}
		
		// apply power model for selected gear
		float powerPercentage = getPowerPercentage(gear, speedPercentage);
		
		// cap if max speed was reached
		float limitedSpeed = car.getMaxSpeed();
		if((currentVehicleSpeed >= limitedSpeed-1))
			powerPercentage = powerPercentage * (limitedSpeed - currentVehicleSpeed);
		
		// accelerate
		if(car.getBrakePedalIntensity()>0)
			car.getCarControl().accelerate((pAccel) * powerPercentage * Math.signum(gear) * car.getTraction());
		else
			car.getCarControl().accelerate((pAccel+0.4f) * powerPercentage * Math.signum(gear) * car.getTraction());
		
		// output texts
		PanelCenter.setGearIndicator(gear, isAutomaticTransmission);
		PanelCenter.getEngineSpeedText().setText((int) currentEngineSpeed + " rpm");
		//TextCenter.getEngineSpeedText().setText((int) (powerPercentage * 100) + " %");
	}
	
	
	public void setGear(int gear, boolean isAutomatic, boolean rememberGear)
	{
		isAutomaticTransmission = isAutomatic;
		
		if(rememberGear)
			rememberGearShiftPosition = gear;
		
		this.gear = Math.min(numberOfGears, Math.max(-1,gear));
		
		switch (this.gear)
		{
			case -1 : selectedTransmission = reverseGear; break;
			case 0  : selectedTransmission = neutralGear; break;
			default : selectedTransmission = forwardGears[this.gear-1]; break;
		}
	}


	private int findBestPowerGear(float speedPercentage) 
	{
		float bestPower = 0;
		int bestGear = 1;
		
		for(int currentGear=1; currentGear<=numberOfGears; currentGear++)
		{
			float currentPower = getPowerPercentage(currentGear, speedPercentage);
			if(currentPower > bestPower)
			{
				bestPower = currentPower;
				bestGear = currentGear;
			}
		}
		
		return bestGear;
	}


	public float getMinRPM() 
	{
		return minRPM;
	}
}
