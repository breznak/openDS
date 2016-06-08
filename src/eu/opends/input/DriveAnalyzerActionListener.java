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

package eu.opends.input;

import com.jme3.input.controls.ActionListener;

import eu.opends.main.DriveAnalyzer;
import eu.opends.main.DriveAnalyzer.VisualizationMode;
import eu.opends.niftyGui.MessageBoxGUI;
import eu.opends.tools.PanelCenter;

/**
 * 
 * @author Rafael Math
 */
public class DriveAnalyzerActionListener implements ActionListener
{
	private DriveAnalyzer analyzer;
	
    public DriveAnalyzerActionListener(DriveAnalyzer analyzer) 
    {
    	this.analyzer = analyzer;
	}

    @Override
	public void onAction(String binding, boolean value, float tpf) 
	{
		
		if (binding.equals(KeyMapping.GOTO_PREVIOUS_DATAPOINT.getID())) 
		{
			if (value) 
			{
				analyzer.moveFocus(-1);
			}
		} 
		
		else if (binding.equals(KeyMapping.GOTO_NEXT_DATAPOINT.getID())) 
		{
			if (value) 
			{
				analyzer.moveFocus(1);
			}
		}
		

		else if (binding.equals(KeyMapping.TOGGLE_CAM.getID())) 
		{
			if (value) {
				// toggle camera
				analyzer.getCameraFactory().changeCamera();
			}
		}
		
		
		else if (binding.equals(KeyMapping.TOGGLE_POINTS.getID())) 
		{
			if (value) 
			{
				analyzer.toggleVisualization(VisualizationMode.POINT);
			}
		}
		
		
		else if (binding.equals(KeyMapping.TOGGLE_LINE.getID())) 
		{
			if (value) 
			{
				analyzer.toggleVisualization(VisualizationMode.LINE);
			}
		}
		
		
		else if (binding.equals(KeyMapping.TOGGLE_CONE.getID())) 
		{
			if (value) 
			{
				analyzer.toggleVisualization(VisualizationMode.CONE);
			}
		}

		else if (binding.equals(KeyMapping.TOGGLE_KEYMAPPING.getID())) 
		{
			if (value)
				analyzer.getKeyMappingGUI().toggleDialog();
		}

		else if (binding.equals(KeyMapping.SHUTDOWN.getID())) 
		{
			if (value)
				analyzer.getShutDownGUI().toggleDialog();
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_MESSAGEBOX.getID())) 
		{
			if (value)
			{
				MessageBoxGUI messageBoxGUI = PanelCenter.getMessageBox();
				messageBoxGUI.toggleDialog();
				
				analyzer.toggleMessageBoxUpdates();
			}
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_REPLAY.getID())) 
		{
			if (value)
				analyzer.toggleReplay();
		}
		
	}
}
