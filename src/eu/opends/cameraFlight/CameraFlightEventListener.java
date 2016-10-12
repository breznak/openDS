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

package eu.opends.cameraFlight;

import com.jme3.cinematic.events.CinematicEvent;
import com.jme3.cinematic.events.CinematicEventListener;
import com.jme3.post.filters.FadeFilter;

import eu.opends.basics.SimulationBasics;
import eu.opends.camera.CameraFactory.CameraMode;
import eu.opends.main.Simulator;
import eu.opends.tools.PanelCenter;

/**
 * 
 * @author Rafael Math
 */
public class CameraFlightEventListener implements CinematicEventListener 
{
	private SimulationBasics sim;
	private FadeFilter fade;
	private float speedKmPerHour;
	private CameraMode previousCamMode = CameraMode.EGO;
	
	public CameraFlightEventListener(SimulationBasics sim, FadeFilter fade, float speedKmPerHour)
	{
		this.sim = sim;
		this.fade = fade;
		this.speedKmPerHour = speedKmPerHour;
	}
	
	
	public void onPlay(CinematicEvent cinematic) 
	{
		previousCamMode = sim.getCameraFactory().getCamMode();
		
		// switch camera off (car invisible and no back view mirror)
		sim.getCameraFactory().setCamMode(CameraMode.OFF);
		
        // set speed and RPM indicator
        PanelCenter.setFixSpeed(speedKmPerHour);
        PanelCenter.setFixRPM(2500);
        //PanelCenter.setGearIndicator(3, false);
	}
	
	
	public void onPause(CinematicEvent cinematic) 
	{
		sim.getCameraFactory().setCamMode(previousCamMode);
	}
	
	
	public void onStop(CinematicEvent cinematic) 
	{          
		fade.setValue(1);
		
		sim.getCameraFactory().setCamMode(previousCamMode);
        
        // reset speed and RPM indicator
        PanelCenter.setFixSpeed(0);
        PanelCenter.setFixRPM(0);
        
        if(sim instanceof Simulator)
        {
        	CameraFlight camFlight = ((Simulator)sim).getCameraFlight();
        	camFlight.setTerminated(true);
        }
        
        
		CameraFlightSettings settings = Simulator.getDrivingTask().getScenarioLoader().getCameraFlightSettings();
		if(settings.isAutomaticStop())
			sim.stop();
	}
}