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

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;

import de.lessvoid.nifty.Nifty;
import eu.opends.basics.SimulationBasics;

/**
 * 
 * @author Rafael Math
 */
public class ShutDownGUI 
{
	private Nifty nifty;
	private SimulationBasics sim;
	private boolean shutDownDialogHidden = true;
	private boolean initiallyPaused = false;
	private AssetManager assetManager;
	private InputManager inputManager;
	private AudioRenderer audioRenderer;
	private ViewPort guiViewPort;
	private FlyByCamera flyCam;

	
	public ShutDownGUI(SimulationBasics sim) 
	{
		this.sim = sim;
		this.assetManager = sim.getAssetManager();
		this.inputManager = sim.getInputManager();
		this.audioRenderer = sim.getAudioRenderer();
		this.guiViewPort = sim.getGuiViewPort();
		this.flyCam = sim.getFlyByCamera();
	}

	
	public void toggleDialog() 
	{
		if (shutDownDialogHidden)
			showDialog();
		else 
			hideDialog();
	}


	public void showDialog() 
	{
		if(shutDownDialogHidden)
		{
			sim.getKeyMappingGUI().hideDialog();
			sim.getInstructionScreenGUI().hideDialog();
			initiallyPaused = sim.isPause();
			sim.setPause(true);
			//sim.getGuiNode().setCullHint(CullHint.Always);
			initShutDownGUI();
			shutDownDialogHidden = false;
		}
	}

	
	public void hideDialog() 
	{
		if(!shutDownDialogHidden)
		{
			closeShutDownGUI();
			shutDownDialogHidden = true;
			//sim.getGuiNode().setCullHint(CullHint.Inherit);
			sim.setPause(initiallyPaused);
		}
	}
	
	
	private void initShutDownGUI() 
	{
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
				inputManager, audioRenderer, guiViewPort);

		// Create a new NiftyGUI object
		nifty = niftyDisplay.getNifty();

		String xmlPath = "Interface/ShutDownGUI.xml";

		// Read XML and initialize custom ScreenController
		nifty.fromXml(xmlPath, "start",	new ShutDownGUIController(sim, this));

		// attach the Nifty display to the gui view port as a processor
		guiViewPort.addProcessor(niftyDisplay);

		// disable fly cam
		flyCam.setEnabled(false);
	}
	

	private void closeShutDownGUI() 
	{
		nifty.exit();
		inputManager.setCursorVisible(false);
		flyCam.setEnabled(true);
	}


	public Nifty getNifty() 
	{
		return nifty;
	}

}
