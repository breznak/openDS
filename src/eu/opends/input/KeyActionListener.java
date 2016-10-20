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

import com.jme3.input.controls.ActionListener;

import eu.opends.trigger.TriggerAction;

/**
 * 
 * @author Rafael Math
 */
public class KeyActionListener implements ActionListener 
{
	private List<TriggerAction> triggerActionList;
	private String triggerName;
	
	
	public KeyActionListener(List<TriggerAction> triggerActionList, String triggerName)
	{
		this.triggerActionList = triggerActionList;
		this.triggerName = triggerName;
	}
	
	
	@Override
	public void onAction(String binding, boolean value, float tpf)
	{
		if (binding.equals(triggerName))
		{
			if(value)
			{
				for(TriggerAction triggerAction : triggerActionList)
				{
					triggerAction.performAction();
				}
			}
		}

		
	}

}
