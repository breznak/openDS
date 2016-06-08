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
 * This abstract class represents a trigger action, which will be 
 * performed whenever a collision with the related trigger was detected.
 * 
 * @author Rafael Math
 */
public abstract class TriggerAction 
{
	private float delay;
	private int maxRepeatCounter;
	private boolean unlimitedRepeat;
	
	public TriggerAction()
	{
		delay = 0;
		maxRepeatCounter = 0;
		unlimitedRepeat = true;
	}
	
	
	public TriggerAction(float delay, int maxRepeat)
	{
		this.delay = delay;
		maxRepeatCounter = maxRepeat;
		unlimitedRepeat = (maxRepeat == 0);
	}
	
	
	/**
	 * Method will be called on trigger collision, await delay and perform some action.
	 */
	public void performAction()
	{
		TriggerActionDelayThread triggerActionDelayThread = new TriggerActionDelayThread(this, delay);
		triggerActionDelayThread.start();
	}
	
	

	protected abstract void execute();
	
	
	protected void updateCounter()
	{
		// can be updated at most once per second
		if(!unlimitedRepeat)
			maxRepeatCounter--;
	}
	
	
	protected boolean isExceeded()
	{
		return ((!unlimitedRepeat) && (maxRepeatCounter == 0));
	}
	
	
	protected float getDelay()
	{
		return delay;
	}
}
