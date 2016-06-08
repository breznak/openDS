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

import eu.opends.audio.AudioCenter;
import eu.opends.camera.CameraFactory.CameraMode;
import eu.opends.car.LightTexturesContainer.LightState;
import eu.opends.car.LightTexturesContainer.TurnSignalState;
import eu.opends.main.Simulator;
import eu.opends.tools.PanelCenter;

/**
 * This thread changes the light textures in the given interval in order
 * to simulate flashing lights.
 * 
 * @author Rafael Math
 */
public class TurnSignalThread extends Thread 
{
	private int lightChangeInterval = 375;
	private int threadUpdateInterval = 25;
	private LightTexturesContainer lightTexturesContainer;
	private Simulator sim;
	private Car car;
	private TurnSignalState targetState;
	private boolean targetStateHasChanged = false;
	private boolean stopRequested = false;
	
	
	public TurnSignalThread(LightTexturesContainer lightTexturesContainer, Simulator sim, Car car)
	{
		super("TurnSignalThread");
		this.lightTexturesContainer = lightTexturesContainer;
		this.sim = sim;
		this.car = car;
	}
	
	
	public void run()
	{
		TurnSignalState currentState = targetState;
		long previousBlinkerUpdate = System.currentTimeMillis();
		
		while(!stopRequested)
		{
			if(targetStateHasChanged || previousBlinkerUpdate <= (System.currentTimeMillis() - lightChangeInterval))
			{
				if(targetStateHasChanged)
				{
					currentState = targetState;
					targetStateHasChanged = false;
				}
				
				if(car instanceof SteeringCar)
				{
					// set current state to turn signal arrows
					applyTurnArrows(currentState);
				}
				
				// set current state to turn signal lights (change textures) if not already set
				applyTurnSignal(currentState);
				
				// switch to next signal state (on-off-on-off-...)
				if(currentState != TurnSignalState.OFF)
					currentState = TurnSignalState.OFF;
				else
					currentState = targetState;
				
				previousBlinkerUpdate = System.currentTimeMillis();
			}
			
			try {
				Thread.sleep(threadUpdateInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void applyTurnArrows(TurnSignalState turnSignalState)
	{
		boolean leftIsOn = false;
		boolean rightIsOn = false;
		
		switch(turnSignalState)
		{
			case LEFT : leftIsOn = true; break;
			case RIGHT : rightIsOn = true; break;
			case BOTH : leftIsOn = true; rightIsOn = true; break;
			default:  break;
		}
		
		PanelCenter.setLeftTurnSignalArrow(leftIsOn);
		PanelCenter.setRightTurnSignalArrow(rightIsOn);	
		
		if(sim.getCameraFactory().getCamMode().equals(CameraMode.EGO))
		{
			if(leftIsOn || rightIsOn)
			{
				// play turn signal sound
				AudioCenter.setVolume("turnSignal", 0.25f);
				AudioCenter.playSound("turnSignal");
			}
		}
		else
			AudioCenter.setVolume("turnSignal", 0f);
	}


	public synchronized void setTurnSignalState(TurnSignalState targetState)
	{
		this.targetState = targetState;
		targetStateHasChanged = true;
	}
	
	
	public synchronized void requestStop()
	{
		stopRequested = true;
	}
	
	
	private void applyTurnSignal(TurnSignalState turnSignalState)
	{
		LightState targetLightState = null;
		
		if(lightTexturesContainer.isBrakeLightOn())
		{
			switch(turnSignalState)
			{
				case LEFT : targetLightState = LightState.LeftTurnBrakeLights; break;
				case RIGHT : targetLightState = LightState.RightTurnBrakeLights; break;
				case BOTH : targetLightState = LightState.HazardLightsBrakeLights; break;
				case OFF : targetLightState = LightState.BrakeLights; break;
			}
		}
		else
		{
			switch(turnSignalState)
			{
				case LEFT : targetLightState = LightState.LeftTurn; break;
				case RIGHT : targetLightState = LightState.RightTurn; break;
				case BOTH : targetLightState = LightState.HazardLights; break;
				case OFF : targetLightState = LightState.AllOff; break;
			}
		}
		
		if((targetLightState != null) && (targetLightState != lightTexturesContainer.getLightState()))
			lightTexturesContainer.setLightState(targetLightState);
	}


	public TurnSignalState getTurnSignalState() 
	{
		return targetState;		
	}
	
}
