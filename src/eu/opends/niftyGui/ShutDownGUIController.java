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

package eu.opends.niftyGui;

import de.lessvoid.nifty.Nifty;
//import de.lessvoid.nifty.controls.button.ButtonControl;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import eu.opends.basics.SimulationBasics;

/**
 * 
 * @author Rafael Math
 */
public class ShutDownGUIController implements ScreenController 
{
	private SimulationBasics sim;
	private ShutDownGUI shutDownGUI;
	//private Nifty nifty;
	
	
	public ShutDownGUIController(SimulationBasics sim, ShutDownGUI shutDownGUI) 
	{
		this.sim = sim;
		this.shutDownGUI = shutDownGUI;
		//this.nifty = shutDownGUI.getNifty();
	}

	
	@Override
	public void bind(Nifty arg0, Screen arg1) 
	{
		
	}

	
	@Override
	public void onEndScreen() 
	{

	}


	@Override
	public void onStartScreen() 
	{
		//nifty.getCurrentScreen().findControl("closeButton", ButtonControl.class).setFocusable(false);
	}
	
	
	public void clickCancelButton()
	{
		shutDownGUI.toggleDialog();
	}
	
	
	public void clickCloseButton()
	{
		sim.stop();
	}

}
