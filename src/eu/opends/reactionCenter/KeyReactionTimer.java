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

package eu.opends.reactionCenter;

import java.lang.reflect.Field;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.JoyButtonTrigger;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;

import eu.opends.jasperReport.ReactionLogger;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class KeyReactionTimer extends ReactionTimer
{
	private InputManager inputManager;
	private ReactionListener reactionListener;
	
	
	public KeyReactionTimer(Simulator sim, InputManager inputManager, ReactionListener reactionListener, 
			ReactionLogger reactionlogger, long experimentStartTime, String timerID, int index)
	{
		super(sim, reactionlogger, experimentStartTime, timerID, index);
		
		this.inputManager = inputManager;
		this.reactionListener = reactionListener;
	}

	
	public void setup(String newReactionGroupID, String correctReaction, 
			String failureReaction, String newComment)
	{
		super.setup(newReactionGroupID, newComment);
		
		addMapping("reaction_group_" + index, correctReaction);
		addMapping("failure_group_" + index, failureReaction);			
		inputManager.addListener(reactionListener, "reaction_group_" + index, "failure_group_" + index);
		
		timerIsActive = true;
	}


	public void reportMissedReaction()
	{
		super.reportMissedReaction();
		
		inputManager.deleteMapping("reaction_group_" + index);
		inputManager.deleteMapping("failure_group_" + index);
	}
	

	public void update()
	{		
		super.update();
		
		if(timerIsActive && (correctReactionReported || failureReactionReported))
		{
			inputManager.deleteMapping("reaction_group_" + index);
			inputManager.deleteMapping("failure_group_" + index);
		}
	}
	
	
	private void addMapping(String mappingID, String buttonString) 
	{
		String[] buttonArray = buttonString.split(",");
		for(String button : buttonArray)
		{
			button = button.toUpperCase().trim();
			Trigger trigger = getTrigger(button);
			if(trigger != null)
				inputManager.addMapping(mappingID, trigger);
		}
	}
	
	
	private Trigger getTrigger(String buttonName)
	{
		try {
			
			if(buttonName.startsWith("KEY_"))
			{
				// prefix "KEY_"
				Field field = KeyInput.class.getField(buttonName);
				int keyNumber = field.getInt(KeyInput.class);
				return new KeyTrigger(keyNumber);
				
			}
			else if(buttonName.startsWith("JOY_"))
			{
				// prefix "JOY_"
				int buttonNr = Integer.parseInt(buttonName.replace("JOY_", ""))-1;
				return new JoyButtonTrigger(0, buttonNr);
			}
			else
			{
				// no prefix
				String keyString = "KEY_" + buttonName;
				Field field = KeyInput.class.getField(keyString);
				int keyNumber = field.getInt(KeyInput.class);
				return new KeyTrigger(keyNumber);
			}
			
		} catch (Exception e) {
			
			if(!buttonName.isEmpty())
				System.err.println("Invalid key '" + buttonName + "'! Use prefix 'KEY_' or 'JOY_'");
			return null;
		}
	}
	
}
