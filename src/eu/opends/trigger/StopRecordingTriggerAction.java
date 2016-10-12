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

import eu.opends.analyzer.DataWriter;
import eu.opends.main.Simulator;
import eu.opends.tools.PanelCenter;

/**
 * 
 * @author Rafael Math
 */
public class StopRecordingTriggerAction extends TriggerAction 
{
	private Simulator sim;
	
	public StopRecordingTriggerAction(float delay, int maxRepeat, Simulator sim)
	{
		super(delay, maxRepeat);
		this.sim = sim;
	}
	
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			DataWriter dataWriter = sim.getMyDataWriter();
			if (dataWriter != null)
			{
				// stop recording drive	
				if (dataWriter.isDataWriterEnabled()) 
				{
					System.out.println("Stop storing Drive-Data");
					dataWriter.setDataWriterEnabled(false);
					PanelCenter.getStoreText().setText(" ");
					
					// print time elapsed since start trigger was hit to screen 
					String timestamp = dataWriter.getElapsedTime();
					PanelCenter.getMessageBox().addMessage(timestamp, 3);
				}
			}
			
			updateCounter();
		}
	}

}
