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


package eu.opends.input;

import java.util.ArrayList;

import at.wisch.joystick.*;
import at.wisch.joystick.event.*;
import at.wisch.joystick.exception.*;
import at.wisch.joystick.ffeffect.*;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.Simulator;


/**
 * 
 * @author Rafael Math
 */
public class ForceFeedbackJoystickController implements FeatureNotSupportedEventListener 
{
	private Simulator sim;
	private boolean enabled = false;
	private FFJoystick primaryJoystick;
	private SpringEffect springEffect;
	//private DamperEffect damperEffect;
	private float springForceFactor = 1.0f;
	//private float damperForceFactor = 1.0f;


	public ForceFeedbackJoystickController(Simulator sim)
	{
		enabled = Simulator.getSettingsLoader().getSetting(Setting.Joystick_enableForceFeedback, false);
		springForceFactor = Simulator.getSettingsLoader().getSetting(Setting.Joystick_springForce, 1.0f);
		//damperForceFactor = Simulator.getSettingsLoader().getSetting(Setting.Joystick_damperForce, 1.0f);
		
		if(enabled)
		{
			this.sim = sim;
			
			ArrayList<FFJoystick> joysticks;
			
			// init and get-joystick methods have to be done within a try-catch-block
			// (these are fatal errors and we need to deal with them)
			try {
				JoystickManager.init();
				joysticks = JoystickManager.getAllFFJoysticks();
				
				if(!joysticks.isEmpty())
				{
					int steeringControllerID = Simulator.getSettingsLoader().getSetting(Setting.Joystick_steeringControllerID, 0);
					if(0 <= steeringControllerID && steeringControllerID < joysticks.size())
						primaryJoystick = joysticks.get(steeringControllerID);
					else
						primaryJoystick = joysticks.get(0);
				}
				
			} catch (FFJoystickException e) {
				e.printErrorMessage();
			}
			
			FeatureNotSupportedEventManager.addFeatureNotSupportedEventListener(this);
			
			
			if(primaryJoystick != null)
			{
				System.out.println("Supported effects of " + primaryJoystick.getName() + ": " 
								+ primaryJoystick.getSupportedEffects());
		
				//System.out.println(" creating effects ...");
				springEffect = new SpringEffect();
				springEffect.setEffectLength(10000); // 10 seconds
				
				//damperEffect = new DamperEffect();
				//damperEffect.setEffectLength(10000); // 10 second
				
				//System.out.println(" uploading effects ...");
				//upload the effects to the joystick
				primaryJoystick.newEffect(springEffect);
				//primaryJoystick.newEffect(damperEffect);
				
				//System.out.println(" playing effects ...");
				//play the effect infinite times
				primaryJoystick.playEffect(springEffect, FFJoystick.INFINITE_TIMES);
				//primaryJoystick.playEffect(damperEffect, FFJoystick.INFINITE_TIMES);
			}	
		}
	}
	
	
	public void update(float tpf)
	{
		if(enabled && primaryJoystick != null)
		{
			float speed = sim.getCar().getCurrentSpeedKmh();
			
			//update spring effect
			float springForce = Math.min(Math.max(speed/200f, 0), 0.15f);
			//System.out.println("Spring force: " + springForce);
			springEffect.setStrength((int) (Effect.MAX_LEVEL * springForce * springForceFactor)); // % of full strength
			primaryJoystick.updateEffect(springEffect);
			
			//update damper effect
			//float damperForce = 0.01f/speed;
			//System.out.println("Damper force: " + damperForce);
			//damperEffect.setStrength((int) (Effect.MAX_LEVEL * damperForce * damperForceFactor)); // % of full strength
			//primaryJoystick.updateEffect(damperEffect);
		}
	}
	
	
	public void close() 
	{
		if(enabled)
		{
			if(primaryJoystick != null)
				primaryJoystick.stopAll();
			
			JoystickManager.close();
		}
	}

	
	/* output errors that are not fatal. if these happen e.g. during a game, there
	 is no need for output or stopping. but during development we should always
	 be aware that something went wrong! */ 
	@Override
	public void featureNotSupportedEventOccured(FeatureNotSupportedEvent event) 
	{
		//System.out.println(event);
	}

}

