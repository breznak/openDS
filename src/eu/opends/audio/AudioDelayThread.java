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

package eu.opends.audio;

/**
 * 
 * @author Rafael Math
 */
public class AudioDelayThread extends Thread
{
	private String soundID;
	private int milliSeconds;
	private String callMethod;
	
	
	public AudioDelayThread(String soundID, int milliSeconds, String callMethod) 
	{
		this.soundID = soundID;
		this.milliSeconds = milliSeconds;
		this.callMethod = callMethod;
	}
	
	
	public void run() 
	{
		try {
			
			sleep(milliSeconds);
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		if(callMethod.equals("playSound"))
		{
			AudioCenter.playSound(soundID);
		}
		else if(callMethod.equals("fadeOut"))
		{
			float volume = AudioCenter.getAudioNode(soundID).getVolume();
			float initialVolume = volume;
			
			while(volume>0)
			{
				try {
					
					sleep(200);
					
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				
				volume = Math.max(AudioCenter.getAudioNode(soundID).getVolume() - 0.1f,0);
				AudioCenter.setVolume(soundID, volume);
				
			}
			
			AudioCenter.stopSound(soundID);
			AudioCenter.setVolume(soundID, initialVolume);
		}
		else
			System.err.println("AudioDelayThread: unknown method");
	}
}
