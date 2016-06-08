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

import java.util.TreeMap;
import java.util.Map.Entry;

import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.ui.Picture;

import eu.opends.basics.SimulationBasics;
import eu.opends.tools.PanelCenter;


/**
 * This class represents a ManipulatePicture trigger action. Whenever a collision
 * with a related trigger was detected, the given picture will be manipulated in 
 * the specified way.
 * 
 * @author Rafael Math
 */
public class ManipulatePictureTriggerAction extends TriggerAction 
{
	private SimulationBasics sim;
	private String pictureID;
	private boolean isVisible;
	
	
	/**
	 * Creates a new ManipulatePicture trigger action instance, providing maximum
	 * number of repetitions and the picture to manipulate. 
	 * 
	 * @param sim
	 * 			Simulator
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param maxRepeat
	 * 			Maximum number how often the trigger can be hit.
	 * 
	 * @param pictureID
	 * 			ID of the picture to manipulate.
	 * 
	 * @param isVisible
	 * 			Visibility of the picture to manipulate.
	 */
	public ManipulatePictureTriggerAction(SimulationBasics sim, float delay, int maxRepeat, 
			String pictureID, boolean isVisible) 
	{
		super(delay, maxRepeat);
		this.sim = sim;
		this.pictureID = pictureID;
		this.isVisible = isVisible;
	}

	
	/**
	 * Manipulates the given picture by applying a visibility change. 
	 */
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			CullHint visibility;
			if(isVisible)
				visibility = CullHint.Dynamic;
			else
				visibility = CullHint.Always;
			
			
			// set all pictures to ...
			if(pictureID.equalsIgnoreCase("all"))
			{
				TreeMap<String, Picture> pictureMap = PanelCenter.getPictureMap();
		        for(Entry<String,Picture> entry : pictureMap.entrySet())
		        	entry.getValue().setCullHint(visibility);
			}
			
			// set only given picture to ...
			Spatial spatial = sim.getGuiNode().getChild(pictureID);
			if(spatial instanceof Picture)
			{
				Picture picture = (Picture) spatial;
					picture.setCullHint(visibility);
			}
			
			updateCounter();
		}
	}

}
