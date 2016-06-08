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


/**
 * This class represents a thread which will cause the removal of a given
 * trigger from the simulators's trigger report list after a given amount 
 * of time has passed by. As long as a trigger is contained in the 
 * simulators's trigger report list it cannot be triggered again. That 
 * list locks triggers which can be unlocked by the methods of this class.
 * 
 * @author Rafael Math
 */
public class RemoveFromReportListThread extends Thread 
{
	private String triggerID;
	private int duration;
    
	
	/**
	 * Creates a new RemoveFromReportList thread for the specified trigger.
	 * 
	 * @param triggerID
	 * 			ID of the trigger to be removed after the given amount of time.
	 * 
	 * @param duration
	 * 			Amount of time which must pass by, before the given trigger 
	 * 			will be removed.
	 */
    public RemoveFromReportListThread(String triggerID, int duration) 
    {
    	super("RemoveFromReportListThread");
    	this.triggerID = triggerID;
        this.duration = duration;
    }

    
    /**
     * Removes the given trigger from the simulators's trigger report list 
     * after the given amount of time.
     */
    public void run() 
    {
    	try {Thread.sleep(1000*duration);} 
    	catch (InterruptedException e){}
    	
    	// remove trigger from report list
    	TriggerCenter.removeTriggerReport(triggerID);
    }
}
