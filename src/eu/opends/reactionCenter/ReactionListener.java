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

import com.jme3.input.controls.ActionListener;

/**
 * 
 * @author Rafael Math
 */
public class ReactionListener implements ActionListener
{
	private ReactionCenter reactionCenter;
	
	
    public ReactionListener(ReactionCenter reactionCenter) 
    {
    	this.reactionCenter = reactionCenter;
	}


	public void onAction(String binding, boolean value, float tpf) 
	{
		try {
			
			String[] reactionGroupArray = binding.split("_");
			
			if(reactionGroupArray.length == 3 && reactionGroupArray[0].equalsIgnoreCase("reaction") && 
					reactionGroupArray[1].equalsIgnoreCase("group") && value)
			{
				int index = Integer.parseInt(reactionGroupArray[2]);
				reactionCenter.reportCorrectReaction(index);
				System.out.println("Key: reaction_group_" + index);
			}
			
			else if (reactionGroupArray.length == 3 && reactionGroupArray[0].equalsIgnoreCase("failure") && 
					reactionGroupArray[1].equalsIgnoreCase("group") && value) 
			{
				int index = Integer.parseInt(reactionGroupArray[2]);
				reactionCenter.reportFailureReaction(index);
				System.out.println("Key: failure_group_" + index);
			}
			
		} catch(Exception e) {
			
			e.printStackTrace();
		}
		
	}

}

