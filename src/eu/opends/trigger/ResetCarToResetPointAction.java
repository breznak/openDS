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

package eu.opends.trigger;

import java.util.List;

import eu.opends.car.Car;
import eu.opends.car.ResetPosition;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class ResetCarToResetPointAction extends TriggerAction 
{
	private Simulator sim;
	private int resetPosition = -1;
	
	
	public ResetCarToResetPointAction(float delay, int maxRepeat, String resetPointName, Simulator sim) 
	{
		super(delay, maxRepeat);
		this.sim = sim;
		List<ResetPosition> resetList = Simulator.getResetPositionList();
		
		for(int i = 0; i<resetList.size(); i++)
		{
			if(resetList.get(i).getName().equalsIgnoreCase(resetPointName))
				this.resetPosition = i;
		}
		
		if(resetPosition == -1)
			System.err.println("Reset point '" + resetPointName + "' could not be found!");
	}
	

	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			Car car = sim.getCar();
			
			if((resetPosition != -1) && (car != null))
				car.setToResetPosition(resetPosition);
			
			updateCounter();
		}
	}

}
