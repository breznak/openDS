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

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.ListBox.SelectionMode;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;
import eu.opends.basics.SimulationBasics;
import eu.opends.niftyGui.listBox.TextListBoxModel;


/**
 * This class represents the message box (several lines of text) which can be displayed 
 * in the simulator screen while driving. Automatic word wrap is supported and an 
 * expiration time for a message can be specified.
 * 
 * @author Rafael Math
 */
public class MessageBoxGUI 
{
	private final int nrOflines = 4;
	
	private Calendar expirationTime = new GregorianCalendar();
	private String message = "";
	private int duration = 0;
	private boolean sentToScreen = true;
	private boolean autoClosed= false;
	private boolean messageBoxDialogHidden = true;
	
	private InputManager inputManager;
	private ViewPort guiViewPort;
	private NiftyJmeDisplay niftyDisplay;
	private ListBox<TextListBoxModel> listBox;
	private Nifty nifty;
	

	/**
	 * Creates a new message box by assigning the lines of text to send all messages to 
	 * and the number of characters a line may have before the next line will be used.
	 * 
	 * @param sim
	 * 			Simulation Basics
	 */
	@SuppressWarnings("unchecked")
	public MessageBoxGUI(SimulationBasics sim) 
	{
		inputManager = sim.getInputManager();
		guiViewPort = sim.getGuiViewPort();
		
		niftyDisplay = new NiftyJmeDisplay(sim.getAssetManager(), inputManager, 
				sim.getAudioRenderer(), guiViewPort);

		// Create a new NiftyGUI object
		nifty = niftyDisplay.getNifty();

		String xmlPath = "Interface/MessageBoxGUI.xml";

		// Read XML and initialize custom ScreenController
		nifty.fromXml(xmlPath, "start",	new MessageBoxGUIController(sim, this));
		
		// init list box
		Screen screen = nifty.getCurrentScreen();
		listBox = (ListBox<TextListBoxModel>) screen.findNiftyControl("messageBox", ListBox.class);
		listBox.changeSelectionMode(SelectionMode.Disabled, false);		
		listBox.setFocusable(false);
	}
	
	
	/**
	 * Closes the message box and destroys all position information (destructor).
	 * After calling this method, creating a new message box instance is required 
	 * (e.g. needed when changing the screen resolution).
	 */
	public void close()
	{
		niftyDisplay.cleanup();
		nifty.exit();
	}
	

	/**
	 * Adds a string to display in the message box to the queue. At next update, the
	 * message will be sent to the textFieldArray
	 * 
	 * @param message
	 * 			String to be enqueued for printing.
	 * 
	 * @param duration
	 * 			Number of seconds, the message will be displayed on the screen.
	 */
	public void addMessage(String message, int duration)
	{
		this.expirationTime = new GregorianCalendar();
		this.expirationTime.add(Calendar.SECOND, duration);
		this.message = message;
		this.duration = duration;
		this.sentToScreen = false;
	}
	
	
	/**
	 * Updates the message box. If a new message was added to the queue, the message
	 * will be forwarded to the screen. If there is no message to show or the last 
	 * message has expired, the screen will be cleared.
	 */
	public void update() 
	{
		// check if duration of the current message has expired
		if(messageHasExpired())
		{
			// expired --> clear screen
			if(!autoClosed)
			{
				clear();
				hideDialog();
				autoClosed = true;
			}
		}
		else
		{
			// not expired --> send message to screen
			sendMessage();
		}
	}


	/**
	 * Clears every line of the message box
	 */
	public void clear() 
	{	
		listBox.clear();
	}


	/**
	 * Toggles the visibility of the message box.
	 */
	public void toggleDialog() 
	{
		if (messageBoxDialogHidden)
			showDialog();
		else 
			hideDialog();
	}


	/**
	 * Sets the visibility of the message box to true.
	 */
	public void showDialog() 
	{
		if(messageBoxDialogHidden)
		{				
			// attach the Nifty display to the gui view port as a processor
			guiViewPort.addProcessor(niftyDisplay);
			
			// if scroll bar available --> set mouse pointer visible
			inputManager.setCursorVisible(listBox.itemCount() > nrOflines);
			
			messageBoxDialogHidden = false;
		}
	}

	
	/**
	 * Sets the visibility of the message box to false.
	 */
	public void hideDialog() 
	{
		if(!messageBoxDialogHidden)
		{
			// detach the Nifty display from the gui view port as a processor
			guiViewPort.removeProcessor(niftyDisplay);
			inputManager.setCursorVisible(false);
			
			messageBoxDialogHidden = true;
		}
	}


	/**
	 * Checks whether the current message has expired
	 * 
	 * @return
	 * 			True, if the current message has exceeded its duration and does 
	 * 			expire (duration != 0).
	 */
	private boolean messageHasExpired() 
	{
		// check whether time of expiration is in the past
		Calendar currentTime = new GregorianCalendar();
		boolean exceededDuration = expirationTime.before(currentTime);
		
		// messages with duration 0 never expire
		boolean canExpire = (duration!=0);
		
		return (exceededDuration && canExpire);
	}
	
	
	/**
	 * Splits message according to maximum characters per line and sends line by
	 * line to the message box's textFieldArray (print on screen).
	 */
	private void sendMessage() 
	{
		if(!sentToScreen)
		{		
			// clear list box
			clear();
			
			int charactersPerLine = (int) (listBox.getWidth()/7f);
			
			// split string word by word
			String[] words = message.trim().split(" ");
			int indexOfCurrentWord = 0;
			
			// fill line by line with words
			while(true)
			{
				// initialize line
				String line = "";
			
				// try to get characters for one line
				try {		

					// fill word by word into a line, until the maximum number of characters 
					// per line has been reached
					
					// length of first word in line
					int length = words[indexOfCurrentWord].length()+1;
					while(length <= charactersPerLine)
					{
						// add current word
						line += words[indexOfCurrentWord] + " ";
						
						// go to next word
						indexOfCurrentWord++;
						
						// add length of next word for next loop
						length += words[indexOfCurrentWord].length()+1;
					}
				
				} catch(Exception e){
					// ArrayIndexOutOfBoundsException will be caught, if not all lines filled
				}
				
				// add line to message box
				line = line.trim();
				if(!line.isEmpty())
					listBox.addItem(new TextListBoxModel(line, line, Color.WHITE));
				else 
					break;
			}

			// set visibility of message box to true
			showDialog();
			autoClosed = false;
			sentToScreen = true;			
		}
	}
	
}
