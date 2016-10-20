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


package eu.opends.basics;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class SkyPropertiesReader
{
	private static Properties properties = new Properties();	
	
	public static Spatial getSettings(AssetManager assetManager, String filename) throws Exception
	{
		File file = new File("assets/" + filename);
		if(!file.exists())
			throw new AssetNotFoundException(filename);
		
		FileInputStream inputStream = new FileInputStream("assets/" + filename);
		properties.load(inputStream);
		inputStream.close();
		
		String relativePath = (new File(filename)).getParentFile() + "\\";
		Texture west = assetManager.loadTexture(relativePath + getStringProperty("west", "west.png"));
        Texture east = assetManager.loadTexture(relativePath + getStringProperty("east", "east.png"));
        Texture north = assetManager.loadTexture(relativePath + getStringProperty("north", "north.png"));
        Texture south = assetManager.loadTexture(relativePath + getStringProperty("south", "south.png"));
        Texture up = assetManager.loadTexture(relativePath + getStringProperty("up", "up.png"));
        Texture down = assetManager.loadTexture(relativePath + getStringProperty("down", "down.png"));

        // vector used to flip textures (as textures will be applied from the outside of the box)
        Vector3f normalScale = new Vector3f(-1, 1, 1);

		return SkyFactory.createSky(assetManager, west, east, north, south, up, down, normalScale);
	}

	
	private static String getStringProperty(String propertyName, String defaultValue)
	{
		String propertyValue = properties.getProperty(propertyName);
		
		//System.out.println(propertyName + ": " + propertyValue);
        
        return (propertyValue==null?defaultValue:propertyValue);
	}


}
