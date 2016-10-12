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
 * This class represents a SendNumberToParallelPort trigger action. Whenever a collision
 * with a related trigger was detected, the given number will be sent to the parallel
 * port for the given number of milliseconds.
 * 
 * @author Rafael Math
 */
public class SendNumberToParallelPortTriggerAction extends TriggerAction 
{
	int number;
	int duration;
	
	
	/**
	 * Creates a new SendNumberToParallelPort trigger action instance, providing number 
	 * and duration.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param maxRepeat
	 * 			Maximum number how often the trigger can be hit (0 = infinite).
	 * 
	 * @param number
	 * 			Number to sent to the parallel port.
	 * 
	 * @param duration
	 * 			Number of milliseconds until "0" will be sent.
	 */
	public SendNumberToParallelPortTriggerAction(float delay, int maxRepeat, int number, int duration)
	{
		super(delay, maxRepeat);
		this.number = number;
		this.duration = duration;
		
		System.loadLibrary("libParPortDLL");
	}

	
	/**
	 * Sends the given number to the parallel port followed by "0" after the given
	 *  amount of milliseconds
	 */
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			sendNumberToParallelPort(number);
			
			try {
				
				Thread.sleep(duration);
				
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			
			sendNumberToParallelPort(0);
			
			updateCounter();
		}
	}
	
	
	public static native void CtoParPort0();
	public static native void CtoParPort1();
	public static native void CtoParPort2();
	public static native void CtoParPort4();
	public static native void CtoParPort8();
	public static native void CtoParPort16();
	public static native void CtoParPort32();
	public static native void CtoParPort64();
	public static native void CtoParPort128();

	
	public static void sendNumberToParallelPort(int number)
	{
		System.out.println("Sending to parallel port: " + number);
		
		if (number == 1) 
			CtoParPort1();
		else if (number == 2) 
			CtoParPort2();
		else if (number == 4) 
			CtoParPort4();
		else if (number == 8) 
			CtoParPort8();
		else if (number == 16) 
			CtoParPort16();
		else if (number == 32) 
			CtoParPort32();
		else if (number == 64) 
			CtoParPort64();
		else if (number == 128) 
			CtoParPort128();
		else
			CtoParPort0();
	}
	
	
	@Override
	public String toString()
	{
		return "SendNumberToParallelPortTriggerAction: number: \'" + number + "\', duration: " + duration;
	}
}
