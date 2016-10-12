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


package eu.opends.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.jme3.system.AppSettings;

public class StartPropertiesReader
{
	private Properties properties = new Properties();
	private boolean loadDefaults = false;
	private boolean showSettingsScreen = true;
	private String drivingTaskPath = "";
	private String driverName = "";
	
	
	public AppSettings getSettings(Simulator sim) 
	{	
		try {
			FileInputStream inputStream = new FileInputStream("startProperties.properties");
			properties.load(inputStream);
			inputStream.close();
			
			loadDefaults = true;
			System.out.println("Executing Startup Properties");

		} catch (FileNotFoundException e) {

		} catch (IOException e) {

			e.printStackTrace();
		}
		
    	AppSettings settings = new AppSettings(loadDefaults); //false --> remember previous values
        settings.setUseJoysticks(true);
        settings.setSettingsDialogImage("OpenDS.png");
        settings.setTitle("OpenDS");
        
        // set splash screen parameters
        if(loadDefaults)
        {
        	settings.setUseJoysticks(getBooleanProperty("usejoysticks", true));
        	
        	settings.setFullscreen(getBooleanProperty("fullscreen", false));
        
        	int width = getIntegerProperty("width", 2280);
        	int height = getIntegerProperty("height", 720);
        	settings.setResolution(width, height);

	        settings.setSamples(getIntegerProperty("antialias", 0));
	        settings.setBitsPerPixel(getIntegerProperty("colordepth", 24));
	        settings.setVSync(getBooleanProperty("vsync", false));
	        settings.setFrequency(getIntegerProperty("refreshrate", 60));
	        
	        showSettingsScreen = getBooleanProperty("showsettingsscreen", true);
	        
	        drivingTaskPath = getStringProperty("drivingtask", "");
	        
	        driverName = getStringProperty("drivername", "");
        }
        
		return settings;
	}

	
	private String getStringProperty(String propertyName, String defaultValue)
	{
		String propertyValue = properties.getProperty(propertyName);
		
		//System.out.println(propertyName + ": " + propertyValue);
        
        return (propertyValue==null?defaultValue:propertyValue);
	}


	private boolean getBooleanProperty(String propertyName, boolean defaultValue) 
	{
		Boolean propertyValueBool = null;
		String propertyValue = properties.getProperty(propertyName);
		
		if(propertyValue == null)
			return defaultValue;
		
        try {
        	propertyValueBool = Boolean.parseBoolean(propertyValue);
        	//System.out.println(propertyName + ": " + propertyValueBool);
        	
        } catch (Exception e) {

			System.err.println(propertyName + ": '" + propertyValue + "' is not a valid boolean. Using default: " + defaultValue);
		}
        
        return (propertyValueBool==null?defaultValue:propertyValueBool);
	}


	private int getIntegerProperty(String propertyName, int defaultValue) 
	{
		Integer propertyValueInt = null;
		String propertyValue = properties.getProperty(propertyName);
		
		if(propertyValue == null)
			return defaultValue;
		
        try {
        	propertyValueInt = Integer.parseInt(propertyValue);
        	//System.out.println(propertyName + ": " + propertyValueInt);
        	
        } catch (Exception e) {

			System.err.println(propertyName + ": '" + propertyValue + "' is not a valid integer. Using default: " + defaultValue);
		}
        
        return (propertyValueInt==null?defaultValue:propertyValueInt);
	}

	
	public boolean showSettingsScreen() 
	{
		return showSettingsScreen;
	}


	public String getDrivingTaskPath()
	{
		return drivingTaskPath;
	}


	public String getDriverName()
	{
		return driverName;
	}
}
