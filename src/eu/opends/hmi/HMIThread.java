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

package eu.opends.hmi;


import eu.opends.basics.SimulationBasics;
import eu.opends.trigger.TriggerCenter;


/**
 * Once activated, HMIThread sends continuously updates to the HMI 
 * controller, until the approximation is below the given minimum.
 * 
 * @author Rafael Math
 */
public class HMIThread extends Thread
{
	private SimulationBasics sim;
	private PresentationModel presentationModel;
	private String triggerID;
	private long presentationID;

	
	/**
	 * Creates a new HMI update thread that updates a presentation task 
	 * every time, a parameter has changed (at most 10 times a second).
	 * 
	 * @param sim
	 * 			Simulator
	 * 
	 * @param presentationModel
	 * 			Presentation model containing parameter changes
	 * 
	 * @param triggerID
	 * 			Trigger ID (from the trigger report list) which is related
	 * 			to the presentation task
	 * 
	 * @param presentationID
	 * 			Presentation ID
	 */
	public HMIThread(SimulationBasics sim, PresentationModel presentationModel, String triggerID, long presentationID) 
	{
		super("HMIThread");
		this.sim = sim;
		this.presentationModel = presentationModel;
		this.triggerID = triggerID;
		this.presentationID = presentationID;
	}


	/**
	 * This method updates presentations to the HMI, every time a parameter
	 * (distance, time, ...) has changed. Once activated, it will only stop
	 * if the stop condition will be true. Loop will repeat every 100 ms.
	 */
	@Override
	public void run() 
	{
		// initialize "previous" and "current" parameters
		presentationModel.computePreviousParameters();
		presentationModel.computeCurrentParameters();
		
		System.out.println(presentationModel.generateMessage());
		
		while(!presentationModel.stopPresentation()) 
		{
			// if parameters have changed (difference between "previous" and "current" parameters)
			if(presentationModel.hasChangedParameter() || sim.isPause())
			{
				// if no error occurred --> update presentation
				if(presentationID >= 0)
					presentationModel.updatePresentation(presentationID);
				
				// generate also a text message (for debug reasons) and send it to screen
				System.out.println(presentationModel.generateMessage());
			}
			
			// assign "current" parameters to "previous" parameters
			presentationModel.shiftCurrentToPreviousParameters();
			
			// update "current" parameters
			presentationModel.computeCurrentParameters();
			
			// prevent loop from running to fast
			try {Thread.sleep(100);} 
			catch (InterruptedException e){}
		}
		
		// remove trigger from report list
		if(triggerID != null)
			TriggerCenter.removeTriggerReport(triggerID);
		
		// cancel presentation
		if(presentationID >= 0)
		{
			presentationModel.stop();
			System.out.println("\nCancel presentation task");
		}
	}
	
}
