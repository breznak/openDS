/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2015 Rafael Math
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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.input.JoystickButton;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.JoyAxisTrigger;
import com.jme3.input.controls.JoyButtonTrigger;
import com.jme3.input.controls.KeyTrigger;

import eu.opends.basics.SimulationBasics;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.DriveAnalyzer;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class KeyBindingCenter 
{
	private List<KeyBindingEntry> keyBindingList = new ArrayList<KeyBindingEntry>();
	
	private InputManager inputManager;
	
    public KeyBindingCenter(SimulationBasics sim) 
    {
    	inputManager = sim.getInputManager();
    	
    	// disable shutdown by ESCAPE button
    	inputManager.deleteMapping("SIMPLEAPP_Exit");
    	
    	if(sim instanceof Simulator)
	    	assignSimulatorKeys((Simulator)sim);

    	else if(sim instanceof DriveAnalyzer)
    		assignDriveAnalyzerKeys((DriveAnalyzer)sim);
	}

    
    public List<KeyBindingEntry> getKeyBindingList()
    {
    	return keyBindingList;
    }
    
    
    private void addKeyMapping(KeyMapping keyMapping, InputListener inputListener)
    {
    	String[] keys = keyMapping.getKeys();
    	
    	if((keys != null) && (keys.length >= 1))
    	{
        	String returnString = null;
        	for(KeyBindingEntry entry : keyBindingList)
        	{
        		if(entry.getDescription().equals(keyMapping.getDescription()))
        		{
        			returnString = entry.getKeyList();
        			break;
        		}
        	}

	    	for(String key : keys)
	    	{
	    		//System.err.println(key);
				try {
		
					if(key.startsWith("KEY_") || key.startsWith("BUTTON_"))
					{
						if(key.startsWith("KEY_"))
						{
							// keyboard assignment
							Field field = KeyInput.class.getField(key);
							int keyNumber = field.getInt(KeyInput.class);
							inputManager.addMapping(keyMapping.getID(), new KeyTrigger(keyNumber));
							inputManager.addListener(inputListener, keyMapping.getID());
						}
						else
						{
							int buttonNumber = Integer.parseInt(key.replace("BUTTON_", ""));
							inputManager.addMapping(keyMapping.getID(), new JoyButtonTrigger(0,buttonNumber));
							inputManager.addListener(inputListener, keyMapping.getID());
						}
						
						if(returnString == null)
							returnString = key;
						else
							returnString += ", " + key;
					}
					else
						System.err.println("Invalid key '" + key + "' for key binding '" + keyMapping.getID() + "'");
					
				} catch (Exception e) {
					System.err.println("Invalid key '" + key + "' for key binding '" + keyMapping.getID() + "'");
				}
	    	}
	
	    	keyBindingList.add(new KeyBindingEntry(keyMapping.getDescription(), returnString));
    	}
    }
    

	private void assignSimulatorKeys(Simulator simulator) 
	{
		// ACTION
		InputListener simulatorActionListener = new SimulatorActionListener(simulator);
		for(KeyMapping keyMapping : KeyMapping.getSimulatorActionKeyMappingList())
			addKeyMapping(keyMapping, simulatorActionListener);
		
		
		// ANALOG (keys and joystick buttons)
		InputListener simulatorAnalogListener = new SimulatorAnalogListener(simulator);
		for(KeyMapping keyMapping : KeyMapping.getSimulatorAnalogKeyMappingList())
			addKeyMapping(keyMapping, simulatorAnalogListener);
		
		// ANALOG (joystick axes)
        SettingsLoader settingsLoader = Simulator.getSettingsLoader();
        int controllerID = settingsLoader.getSetting(Setting.Joystick_controllerID, 0);
        int pedalAxis = settingsLoader.getSetting(Setting.Joystick_pedalAxis, 2);
        boolean invertPedalAxis = settingsLoader.getSetting(Setting.Joystick_invertPedalAxis, false);
        int steeringAxis = settingsLoader.getSetting(Setting.Joystick_steeringAxis, 1);
        boolean invertSteeringAxis = settingsLoader.getSetting(Setting.Joystick_invertSteeringAxis, false);
        boolean dumpJoystickList = settingsLoader.getSetting(Setting.Joystick_dumpJoystickList, false);
        
        inputManager.addMapping("Joy Up", new JoyAxisTrigger(controllerID, pedalAxis, invertPedalAxis));
    	inputManager.addMapping("Joy Down", new JoyAxisTrigger(controllerID, pedalAxis, !invertPedalAxis));
    	inputManager.addMapping("Joy Right", new JoyAxisTrigger(controllerID, steeringAxis, invertSteeringAxis));
    	inputManager.addMapping("Joy Left", new JoyAxisTrigger(controllerID, steeringAxis, !invertSteeringAxis));
    	
        inputManager.addListener(simulatorAnalogListener, "Joy Left", "Joy Right", "Joy Down", "Joy Up");
        
        
        if(dumpJoystickList)
        	dumpJoysticks();
	}
	

	private void dumpJoysticks() 
	{
        Joystick[] joysticks = inputManager.getJoysticks();
        if (joysticks == null)
            throw new IllegalStateException("Cannot find any joysticks!");

        try {
        	
            PrintWriter out = new PrintWriter(new FileWriter("joystickDump.txt"));
            
            Date timestamp = new Date();
            out.println("Creation Date: " + new SimpleDateFormat("yyyy-MM-dd").format(timestamp));
            out.println("Creation Time: " + new SimpleDateFormat("HH:mm:ss").format(timestamp));
            out.println("");
            
            if(joysticks.length == 0)
            	out.println("No joysticks found!");
            	
            for(Joystick joystick : joysticks) 
    	    {
    	        out.println("Joystick[" + joystick.getJoyId() + "]:" + joystick.getName());
    	        
    	        out.println("  buttons:" + joystick.getButtonCount());
    	        for(JoystickButton button : joystick.getButtons())
    	            out.println("   " + button);
    	        
    	        out.println("  axes:" + joystick.getAxisCount());
    	        for(JoystickAxis axis : joystick.getAxes()) 
    	            out.println("   " + axis);
    	    }            
            
            out.close();
            
        } catch(IOException e) {
        	
            throw new RuntimeException("Error writing joystick dump", e);
        }
	}
	
	
	private void assignDriveAnalyzerKeys(DriveAnalyzer analyzer) 
	{
		//remove arrow key's mapping for chase camera
		if(inputManager.hasMapping("FLYCAM_Left"))
			inputManager.deleteTrigger("FLYCAM_Left", new KeyTrigger(KeyInput.KEY_LEFT));
		
		if(inputManager.hasMapping("FLYCAM_Right"))
			inputManager.deleteTrigger("FLYCAM_Right", new KeyTrigger(KeyInput.KEY_RIGHT));
		
		if(inputManager.hasMapping("FLYCAM_Up"))
			inputManager.deleteTrigger("FLYCAM_Up", new KeyTrigger(KeyInput.KEY_UP));
		
		if(inputManager.hasMapping("FLYCAM_Down"))
			inputManager.deleteTrigger("FLYCAM_Down", new KeyTrigger(KeyInput.KEY_DOWN));
		
		
		// ACTION
		InputListener driveAnalyzerActionListener = new DriveAnalyzerActionListener(analyzer);
		for(KeyMapping keyMapping : KeyMapping.getDriveAnalyzerActionKeyMappingList())
			addKeyMapping(keyMapping, driveAnalyzerActionListener);


		// ANALOG
		InputListener driveAnalyzerAnalogListener = new DriveAnalyzerAnalogListener(analyzer);
		for(KeyMapping keyMapping : KeyMapping.getDriveAnalyzerAnalogKeyMappingList())
			addKeyMapping(keyMapping, driveAnalyzerAnalogListener);
	}
}
