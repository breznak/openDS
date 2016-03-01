/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2014 Rafael Math
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

import com.jme3.math.ColorRGBA;

import eu.opends.basics.SimulationBasics;
import eu.opends.drivingTask.DrivingTask;
import eu.opends.drivingTask.scenario.ScenarioLoader;
import eu.opends.drivingTask.scenario.ScenarioLoader.CarProperty;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;
import eu.opends.trafficObjectLocator.TrafficObjectLocator;

/**
 * Driving Car
 * 
 * @author Rafael Math
 */
public class SteeringCar extends Car 
{
    private TrafficObjectLocator trafficObjectLocator;
    
    
	public SteeringCar(Simulator sim) 
	{		
		this.sim = sim;
		
		DrivingTask drivingTask = SimulationBasics.getDrivingTask();
		ScenarioLoader scenarioLoader = drivingTask.getScenarioLoader();
		
		initialPosition = scenarioLoader.getStartLocation();
		if(initialPosition == null)
			initialPosition = SimulationDefaults.initialCarPosition;
		
		this.initialRotation = scenarioLoader.getStartRotation();
		if(this.initialRotation == null)
			this.initialRotation = SimulationDefaults.initialCarRotation;
			
		// add start position as reset position
		Simulator.getResetPositionList().add(new ResetPosition(initialPosition,initialRotation));
		
		mass = scenarioLoader.getChassisMass();
		
		minSpeed = scenarioLoader.getCarProperty(CarProperty.engine_minSpeed, SimulationDefaults.engine_minSpeed);
		maxSpeed = scenarioLoader.getCarProperty(CarProperty.engine_maxSpeed, SimulationDefaults.engine_maxSpeed);
			
		decelerationBrake = scenarioLoader.getCarProperty(CarProperty.brake_decelerationBrake, 
				SimulationDefaults.brake_decelerationBrake);
		maxBrakeForce = 0.004375f * decelerationBrake * mass;
		
		decelerationFreeWheel = scenarioLoader.getCarProperty(CarProperty.brake_decelerationFreeWheel, 
				SimulationDefaults.brake_decelerationFreeWheel);
		maxFreeWheelBrakeForce = 0.004375f * decelerationFreeWheel * mass;
		
		engineOn = scenarioLoader.getCarProperty(CarProperty.engine_engineOn, SimulationDefaults.engine_engineOn);
		showEngineStatusMessage(engineOn);
		
		Float lightIntensityObj = scenarioLoader.getCarProperty(CarProperty.light_intensity, SimulationDefaults.light_intensity);
		if(lightIntensityObj != null)
			lightIntensity = lightIntensityObj;
		
		transmission = new Transmission(this);
		powerTrain = new PowerTrain(this);
		
		modelPath = scenarioLoader.getModelPath();
		
		init();

        // allows to place objects at current position
        trafficObjectLocator = new TrafficObjectLocator(sim, this);
	}

	
	public TrafficObjectLocator getObjectLocator()
	{
		return trafficObjectLocator;
	}
	
	
	// will be called, in every frame
	public void update(float tpf)
	{
		// accelerate
		float pAccel = 0;
		if(!engineOn)
		{
			// apply 0 acceleration when engine not running
			pAccel = powerTrain.getPAccel(tpf, 0) * 30f;
		}
		else if(isAutoAcceleration && (getCurrentSpeedKmh() < minSpeed))
		{
			// apply maximum acceleration (= -1 for forward) to maintain minimum speed
			pAccel = powerTrain.getPAccel(tpf, -1) * 30f;
		}
		else if(isCruiseControl && (getCurrentSpeedKmh() < targetSpeedCruiseControl))
		{
			// apply maximum acceleration (= -1 for forward) to maintain target speed
			pAccel = powerTrain.getPAccel(tpf, -1) * 30f;
		}
		else
		{
			// apply acceleration according to gas pedal state
			pAccel = powerTrain.getPAccel(tpf, gasPedalPressIntensity) * 30f;
		}
		transmission.performAcceleration(pAccel);
		
		// brake lights
		setBrakeLight(brakePedalPressIntensity > 0);
		
		// brake	
		float appliedBrakeForce = brakePedalPressIntensity * maxBrakeForce;
		float currentFriction = powerTrain.getFrictionCoefficient() * maxFreeWheelBrakeForce;
		carControl.brake(appliedBrakeForce + currentFriction);
		
		//lights
		leftHeadLight.setColor(ColorRGBA.White.mult(lightIntensity));
        leftHeadLight.setPosition(carModel.getLeftLightPosition());
        leftHeadLight.setDirection(carModel.getLeftLightDirection());
        
        rightHeadLight.setColor(ColorRGBA.White.mult(lightIntensity));
        rightHeadLight.setPosition(carModel.getRightLightPosition());
        rightHeadLight.setDirection(carModel.getRightLightDirection());
        
        trafficObjectLocator.update();
	}

}
