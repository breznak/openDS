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
		
					if(key.startsWith("KEY_") || key.startsWith("BUTTON_") || key.startsWith("JOY_"))
					{
						if(key.startsWith("KEY_"))
						{
							// keyboard assignment
							Field field = KeyInput.class.getField(key);
							int keyNumber = field.getInt(KeyInput.class);
							inputManager.addMapping(keyMapping.getID(), new KeyTrigger(keyNumber));
							inputManager.addListener(inputListener, keyMapping.getID());
						}
						else if(key.startsWith("BUTTON_"))
						{
							int buttonNumber = Integer.parseInt(key.replace("BUTTON_", ""));
							inputManager.addMapping(keyMapping.getID(), new JoyButtonTrigger(0,buttonNumber));
							inputManager.addListener(inputListener, keyMapping.getID());
						}
						else
						{
							// e.g. JOY_1:BUTTON_2
							String[] stringArray = key.split(":");
							if(stringArray.length == 2)
							{
								int joyID = Integer.parseInt(stringArray[0].replace("JOY_", ""));
								int buttonNumber = Integer.parseInt(stringArray[1].replace("BUTTON_", ""));
								inputManager.addMapping(keyMapping.getID(), new JoyButtonTrigger(joyID,buttonNumber));
								inputManager.addListener(inputListener, keyMapping.getID());
							}
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
        int steeringControllerID = settingsLoader.getSetting(Setting.Joystick_steeringControllerID, 0);
        int steeringAxis = settingsLoader.getSetting(Setting.Joystick_steeringAxis, 1);
        boolean invertSteeringAxis = settingsLoader.getSetting(Setting.Joystick_invertSteeringAxis, false);
        int combinedPedalsControllerID = settingsLoader.getSetting(Setting.Joystick_combinedPedalsControllerID, 0);
        int combinedPedalsAxis = settingsLoader.getSetting(Setting.Joystick_combinedPedalsAxis, 2);
        boolean invertCombinedPedalsAxis = settingsLoader.getSetting(Setting.Joystick_invertCombinedPedalsAxis, false);
        int acceleratorControllerID = settingsLoader.getSetting(Setting.Joystick_acceleratorControllerID, 0);
        int acceleratorAxis = settingsLoader.getSetting(Setting.Joystick_acceleratorAxis, 6);
        boolean invertAcceleratorAxis = settingsLoader.getSetting(Setting.Joystick_invertAcceleratorAxis, true);
        int brakeControllerID = settingsLoader.getSetting(Setting.Joystick_brakeControllerID, 0);
        int brakeAxis = settingsLoader.getSetting(Setting.Joystick_brakeAxis, 5);
        boolean invertBrakeAxis = settingsLoader.getSetting(Setting.Joystick_invertBrakeAxis, true);
        int clutchControllerID = settingsLoader.getSetting(Setting.Joystick_clutchControllerID, 0);
        int clutchAxis = settingsLoader.getSetting(Setting.Joystick_clutchAxis, 7);
        boolean invertClutchAxis = settingsLoader.getSetting(Setting.Joystick_invertClutchAxis, true);
        boolean dumpJoystickList = settingsLoader.getSetting(Setting.Joystick_dumpJoystickList, false);
        
        inputManager.addMapping("SteeringWheelRight", new JoyAxisTrigger(steeringControllerID, steeringAxis, invertSteeringAxis));
    	inputManager.addMapping("SteeringWheelLeft", new JoyAxisTrigger(steeringControllerID, steeringAxis, !invertSteeringAxis));
        inputManager.addMapping("CombinedPedalsBrake", new JoyAxisTrigger(combinedPedalsControllerID, combinedPedalsAxis, invertCombinedPedalsAxis));
    	inputManager.addMapping("CombinedPedalsAccelerator", new JoyAxisTrigger(combinedPedalsControllerID, combinedPedalsAxis, !invertCombinedPedalsAxis));
    	inputManager.addMapping("AcceleratorUp", new JoyAxisTrigger(acceleratorControllerID, acceleratorAxis, invertAcceleratorAxis));
    	inputManager.addMapping("AcceleratorDown", new JoyAxisTrigger(acceleratorControllerID, acceleratorAxis, !invertAcceleratorAxis));
    	inputManager.addMapping("BrakeUp", new JoyAxisTrigger(brakeControllerID, brakeAxis, invertBrakeAxis));
    	inputManager.addMapping("BrakeDown", new JoyAxisTrigger(brakeControllerID, brakeAxis, !invertBrakeAxis));
    	inputManager.addMapping("ClutchUp", new JoyAxisTrigger(clutchControllerID, clutchAxis, invertClutchAxis));
    	inputManager.addMapping("ClutchDown", new JoyAxisTrigger(clutchControllerID, clutchAxis, !invertClutchAxis));
    	
        inputManager.addListener(simulatorAnalogListener, "SteeringWheelLeft", "SteeringWheelRight", 
        		"CombinedPedalsAccelerator", "CombinedPedalsBrake", "AcceleratorUp", "AcceleratorDown", 
        		"BrakeUp", "BrakeDown", "ClutchUp", "ClutchDown");
        		
        /*
        inputManager.addMapping("0_0", new JoyAxisTrigger(0, 0, false));
        inputManager.addMapping("0_1", new JoyAxisTrigger(0, 1, false));
        inputManager.addMapping("0_2", new JoyAxisTrigger(0, 2, false));
        inputManager.addMapping("0_3", new JoyAxisTrigger(0, 3, false));
        inputManager.addMapping("0_4", new JoyAxisTrigger(0, 4, false));
        inputManager.addMapping("0_5", new JoyAxisTrigger(0, 5, false));
        inputManager.addMapping("0_6", new JoyAxisTrigger(0, 6, false));
        inputManager.addMapping("0_7", new JoyAxisTrigger(0, 7, false));
        inputManager.addMapping("0_8", new JoyAxisTrigger(0, 8, false));
        inputManager.addMapping("0_9", new JoyAxisTrigger(0, 9, false));
        
        inputManager.addMapping("0_0n", new JoyAxisTrigger(0, 0, true));
        inputManager.addMapping("0_1n", new JoyAxisTrigger(0, 1, true));
        inputManager.addMapping("0_2n", new JoyAxisTrigger(0, 2, true));
        inputManager.addMapping("0_3n", new JoyAxisTrigger(0, 3, true));
        inputManager.addMapping("0_4n", new JoyAxisTrigger(0, 4, true));
        inputManager.addMapping("0_5n", new JoyAxisTrigger(0, 5, true));
        inputManager.addMapping("0_6n", new JoyAxisTrigger(0, 6, true));
        inputManager.addMapping("0_7n", new JoyAxisTrigger(0, 7, true));
        inputManager.addMapping("0_8n", new JoyAxisTrigger(0, 8, true));
        inputManager.addMapping("0_9n", new JoyAxisTrigger(0, 9, true));
        
        inputManager.addMapping("1_0", new JoyAxisTrigger(1, 0, false));
        inputManager.addMapping("1_1", new JoyAxisTrigger(1, 1, false));
        inputManager.addMapping("1_2", new JoyAxisTrigger(1, 2, false));
        inputManager.addMapping("1_3", new JoyAxisTrigger(1, 3, false));
        inputManager.addMapping("1_4", new JoyAxisTrigger(1, 4, false));
        inputManager.addMapping("1_5", new JoyAxisTrigger(1, 5, false));
        inputManager.addMapping("1_6", new JoyAxisTrigger(1, 6, false));
        inputManager.addMapping("1_7", new JoyAxisTrigger(1, 7, false));
        inputManager.addMapping("1_8", new JoyAxisTrigger(1, 8, false));
        inputManager.addMapping("1_9", new JoyAxisTrigger(1, 9, false));
        
        inputManager.addMapping("1_0n", new JoyAxisTrigger(1, 0, true));
        inputManager.addMapping("1_1n", new JoyAxisTrigger(1, 1, true));
        inputManager.addMapping("1_2n", new JoyAxisTrigger(1, 2, true));
        inputManager.addMapping("1_3n", new JoyAxisTrigger(1, 3, true));
        inputManager.addMapping("1_4n", new JoyAxisTrigger(1, 4, true));
        inputManager.addMapping("1_5n", new JoyAxisTrigger(1, 5, true));
        inputManager.addMapping("1_6n", new JoyAxisTrigger(1, 6, true));
        inputManager.addMapping("1_7n", new JoyAxisTrigger(1, 7, true));
        inputManager.addMapping("1_8n", new JoyAxisTrigger(1, 8, true));
        inputManager.addMapping("1_n", new JoyAxisTrigger(1, 9, true));
        
        inputManager.addListener(simulatorAnalogListener, "0_0", "0_1", "0_2", "0_3", "0_4", "0_5", "0_6", "0_7", "0_8", "0_9", 
        		"1_0", "1_1", "1_2", "1_3", "1_4", "1_5", "1_6", "1_7", "1_8", "1_9", "0_0n", "0_1n", "0_2n", "0_3n", "0_4n", "0_5n", 
        		"0_6n", "0_7n", "0_8n", "0_9n", "1_0n", "1_1n", "1_2n", "1_3n", "1_4n", "1_5n", "1_6n", "1_7n", "1_8n", "1_9n");
        */

        if(dumpJoystickList)
        	dumpJoysticks();
	}
	

	private void dumpJoysticks() 
	{
        Joystick[] joysticks = inputManager.getJoysticks();
        if (joysticks == null)
        {
        	System.out.println("Joystick dump: No joysticks available!");

        }
        else try 
        {
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
