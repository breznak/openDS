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

package eu.opends.input;

import com.jme3.input.controls.AnalogListener;

import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class SimulatorAnalogListener implements AnalogListener 
{
	private Simulator simulator;
	private float steeringSensitivityFactor;
	private float combinedPedalsSensitivityFactor;
	private float acceleratorSensitivityFactor;
	private float brakeSensitivityFactor;
	private float clutchSensitivityFactor;
	
	
	public SimulatorAnalogListener(Simulator simulator) 
	{
		this.simulator = simulator;
		simulator.getInputManager().setAxisDeadZone(0);
		
		steeringSensitivityFactor = Simulator.getSettingsLoader().getSetting(Setting.Joystick_steeringSensitivityFactor, 1.0f);
		combinedPedalsSensitivityFactor = Simulator.getSettingsLoader().getSetting(Setting.Joystick_combinedPedalsSensitivityFactor, 1.0f);
		acceleratorSensitivityFactor = Simulator.getSettingsLoader().getSetting(Setting.Joystick_acceleratorSensitivityFactor, 1.0f);
		brakeSensitivityFactor = Simulator.getSettingsLoader().getSetting(Setting.Joystick_brakeSensitivityFactor, 1.0f);
		clutchSensitivityFactor = Simulator.getSettingsLoader().getSetting(Setting.Joystick_clutchSensitivityFactor, 1.0f);
	}

	
	@Override
	public void onAnalog(String binding, float value, float tpf) 
	{
		// haptic technology: start rumbling
		//simulator.getInputManager().getJoysticks()[0].rumble(1.0f);
		
		// haptic technology: stop rumbling
		//simulator.getInputManager().getJoysticks()[0].rumble(0.0f);
		
		
		if (binding.equals("SteeringWheelLeft")) 
		{
			float steeringValue =  (value*steeringSensitivityFactor)/tpf;
			
			//System.out.println("left: " + Math.round(steeringValue*100000)/1000f);

			simulator.getSteeringTask().setSteeringIntensity(-2.6f*steeringValue);
			
			/*
			if(Math.abs(steeringValue) <= 0.002f)
				simulator.getCar().unsteer();
			else*/
				simulator.getCar().steer(steeringValue/2.3f);
				//System.out.println("left: " + Math.round((steeringValue/2.3f)*100000)/1000f);
		}
		
		else if (binding.equals("SteeringWheelRight")) 
		{
			float steeringValue = (-value*steeringSensitivityFactor)/tpf;
			
			//System.out.println("right: " + Math.round(steeringValue*100000)/1000f);

			simulator.getSteeringTask().setSteeringIntensity(-2.6f*steeringValue);
			
			/*
			if(Math.abs(steeringValue) <= 0.002f)
				simulator.getCar().unsteer();
			else*/
				simulator.getCar().steer(steeringValue/2.3f);
				//System.out.println("right: " + Math.round((steeringValue/2.3f)*100000)/1000f);
		} 
		
		else if (binding.equals("Accelerator") || binding.equals("CombinedPedalsAccelerator"))
		{
			float accelerationValue = -value/tpf;
			
			if(binding.equals("Accelerator"))
				accelerationValue *= acceleratorSensitivityFactor;
			else
				accelerationValue *= combinedPedalsSensitivityFactor;
			
			//System.out.println("acc: " + Math.round(accelerationValue*100000)/1000f);
			
			if(Math.abs(accelerationValue) >= 0.5f)
				simulator.getSteeringTask().getPrimaryTask().reportGreenLight();


			if(Math.abs(accelerationValue) <= 0.05f)
			{
				simulator.getCar().resetPedals();
			}
			else
				simulator.getCar().setAcceleratorPedalIntensity(accelerationValue);
			
			simulator.getThreeVehiclePlatoonTask().reportAcceleratorIntensity(Math.abs(accelerationValue));
		}
		
		else if (binding.equals("Brake") || binding.equals("CombinedPedalsBrake"))
		{
			float brakeValue = value/tpf;
			
			if(binding.equals("Brake"))
				brakeValue *= brakeSensitivityFactor;
			else
				brakeValue *= combinedPedalsSensitivityFactor;
			
			//System.out.println("brk: " + Math.round(brakeValue*100000)/1000f);
			
			if(Math.abs(brakeValue) >= 0.5f)
				simulator.getSteeringTask().getPrimaryTask().reportRedLight();
			

			if(Math.abs(brakeValue) <= 0.05f)
				simulator.getCar().resetPedals();
			else
			{
				simulator.getCar().disableCruiseControlByBrake();
				simulator.getCar().setBrakePedalIntensity(brakeValue);
				simulator.getThreeVehiclePlatoonTask().reportBrakeIntensity(brakeValue);
			}
		}
		
		else if (binding.equals("Clutch"))
		{
			float clutchValue = (value*clutchSensitivityFactor)/tpf;
			
			if(Math.abs(clutchValue) <= 0.05f)
				clutchValue = 0;
			
			//System.out.println("clutch: " + Math.round(clutchValue*100000)/1000f);
			
			simulator.getCar().setClutchPedalIntensity(clutchValue);
		}
	}

}
