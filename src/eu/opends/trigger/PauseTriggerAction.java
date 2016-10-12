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

import eu.opends.basics.SimulationBasics;

/**
 * 
 * @author Rafael Math
 */
public class PauseTriggerAction extends TriggerAction 
{
	private SimulationBasics sim;
	private int duration;
	
	
	public PauseTriggerAction(SimulationBasics sim, float delay, int maxRepeat, int duration)
	{
		super(delay, maxRepeat);
		this.sim = sim;
		this.duration = duration;
	}
	
	
	public int getDuration() 
	{
		return duration;
	}
	
	
	@Override
	protected void execute() 
	{
		if(!sim.isPause())
		{
			if(!isExceeded())
			{
				sim.setPause(true);
				
				if(duration > 0)
				{
					StopPauseThread st = new StopPauseThread(duration);
					st.start();
				}
				
				updateCounter();
			}
		}
	}
	
	
	class StopPauseThread extends Thread 
	{
        int duration;
        
        StopPauseThread(int duration) 
        {
            this.duration = duration;
        }

        public void run() 
        {
        	try {Thread.sleep(1000*duration);} 
        	catch (InterruptedException e){}

    		sim.setPause(false);
        }
    }

	
}
