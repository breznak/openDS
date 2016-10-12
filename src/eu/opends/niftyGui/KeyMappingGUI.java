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
import com.jme3.scene.Spatial.CullHint;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import eu.opends.basics.SimulationBasics;


/**
 * This class represents the key mapping and graphic settings GUI. It
 * provides methods to open and close, initialize and destroy the GUI 
 * and to open a given tab of the GUI.
 * 
 * @author Rafael Math
 */
public class KeyMappingGUI 
{
	/**
	 * This enum contains all tabs of the key mapping and graphic 
	 * settings GUI. Each tab is connected to a certain layer and 
	 * button.
	 */
	public enum GuiLayer 
	{
		KEYMAPPING_PAGE1("keyMappingLayer1", "keyMappingButton1"), 
		KEYMAPPING_PAGE2("keyMappingLayer2", "keyMappingButton2"), 
		GRAPHICSETTINGS("graphicSettingsLayer", "graphicSettingsButton");
		
		private String layerName;
		private String button;
		
		GuiLayer(String layerName, String button)
		{
			this.layerName = layerName;
			this.button = button;
		}
		
		public String getLayerName()
		{
			return layerName;
		}
		
		public String getButton()
		{
			return button;
		}
	}

	private Nifty nifty;
	private SimulationBasics sim;
	private boolean keyMappingHidden = true;
	private boolean initiallyPaused = false;
	private AssetManager assetManager;
	private InputManager inputManager;
	private AudioRenderer audioRenderer;
	private ViewPort guiViewPort;
	private FlyByCamera flyCam;
	
	
	/**
	 * Creates a new instance of the key mapping and graphic settings GUI.
	 * 
	 * @param sim
	 * 			SimulationBasics class.
	 */
	public KeyMappingGUI(SimulationBasics sim)
	{
		this.sim = sim;
		this.assetManager = sim.getAssetManager();
		this.inputManager = sim.getInputManager();
		this.audioRenderer = sim.getAudioRenderer();
		this.guiViewPort = sim.getGuiViewPort();
		this.flyCam = sim.getFlyByCamera();
	}

	
	/**
	 * Returns Nifty element of the key mapping and graphic settings GUI.
	 * 
	 * @return
	 * 			Nifty Element.
	 */
	public Nifty getNifty()
	{
		return nifty;
	}
	
	
	/**
	 * Toggles visibility of the key mapping and graphic settings GUI.
	 */
	public void toggleDialog() 
	{
		if(keyMappingHidden)
			showDialog();
		else
			hideDialog();
	}

	
	/**
	 * Shows key mapping and graphic settings GUI.
	 */
	public void showDialog()
	{
		if(keyMappingHidden)
		{
			sim.getShutDownGUI().hideDialog();
			sim.getInstructionScreenGUI().hideDialog();
			initiallyPaused = sim.isPause();
			sim.setPause(true);
			sim.getGuiNode().setCullHint(CullHint.Always);
			initKeyMappingGUI();
			keyMappingHidden = false;
		}
	}
	

	/**
	 * Hides key mapping and graphic settings GUI.
	 */
	public void hideDialog() 
	{
		if(!keyMappingHidden)
		{
			closeKeyMappingGUI();
			keyMappingHidden = true;
			sim.getGuiNode().setCullHint(CullHint.Inherit);
			sim.setPause(initiallyPaused);
		}
	}


	/**
	 * Initializes key mapping and graphic settings GUI.
	 */
	private void initKeyMappingGUI()
	{		
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
    	
    	// Create a new NiftyGUI object
    	nifty = niftyDisplay.getNifty();
    		
    	String xmlPath = "Interface/KeyMappingGUI.xml";
    	
    	// Read XML and initialize custom ScreenController
    	nifty.fromXml(xmlPath, "start", new KeyMappingGUIController(sim, this));
    		
    	// attach the Nifty display to the GUI view port as a processor
    	guiViewPort.addProcessor(niftyDisplay);
    	
    	// disable fly camera, since mouse pointer is needed for user input
    	flyCam.setEnabled(false);
	}


	/**
	 * Changes over to the given layer of the (already opened)
	 * key mapping and graphic settings GUI.
	 * 
	 * @param selectedLayer
	 * 			Layer name to show.
	 */
	public void openLayer(GuiLayer selectedLayer)
	{
		Screen screen = nifty.getCurrentScreen();
		
		// show given layer, hide all others (except "menuLayer" which contains menu buttons)
		for(Element layer : screen.getLayerElements())
			if(layer.getId().equals(selectedLayer.getLayerName()) || layer.getId().equals("menuLayer"))
				layer.show();
			else
				layer.hide();
		
		// set focus to button related to the selected layer
		Button button = (Button) screen.findNiftyControl(selectedLayer.getButton(), Button.class);
		button.setFocus();
	}
	
	
	/**
	 * Close key mapping and graphic settings GUI.
	 */
	private void closeKeyMappingGUI() 
	{
		nifty.exit();
        inputManager.setCursorVisible(false);
        flyCam.setEnabled(true);
	}
	
}
