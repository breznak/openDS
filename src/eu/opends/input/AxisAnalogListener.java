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

import java.util.List;

import com.jme3.input.controls.AnalogListener;

import eu.opends.trigger.TriggerAction;

/**
 * 
 * @author Rafael Math
 */
public class AxisAnalogListener implements AnalogListener 
{
	private List<TriggerAction> triggerActionList;
	private String triggerName;
	private float triggeringThreshold;
	private float sensitivityFactor;
	private boolean triggerAvailable = true;
	
	
	public AxisAnalogListener(List<TriggerAction> triggerActionList, String triggerName, float triggeringThreshold, float sensitivityFactor)
	{
		this.triggerActionList = triggerActionList;
		this.triggerName = triggerName;
		this.triggeringThreshold = triggeringThreshold;
		this.sensitivityFactor = sensitivityFactor;
	}
	
	
	@Override
	public void onAnalog(String binding, float axisValue, float tpf)
	{
		if (binding.equals(triggerName + "Up") || binding.equals(triggerName + "Down"))
		{
			float value = axisValue/tpf;
			
			if(binding.equals(triggerName + "Up"))
				value = 0.5f + (0.5f*value);
			else if(binding.equals(triggerName + "Down"))
				value = 0.5f - (0.5f*value);
			
			value *= sensitivityFactor;
			
			//System.out.println("analog value: " + Math.round(value*100000)/1000f);
			
			
			if(value > triggeringThreshold && triggerAvailable)
			{
				for(TriggerAction triggerAction : triggerActionList)
				{
					triggerAction.performAction();
				}
				
				triggerAvailable = false;
			}
			else if (value <= Math.max((triggeringThreshold - 0.05f),0))
				triggerAvailable = true;
		}
	}
}