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

package eu.opends.car;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class LightTexturesContainer
{
	private Simulator sim;
	private Node carNode;
	private LightState lightState;
	private HashMap<LightState,HashMap<Spatial,Material>> lightTexturesContainer;
	private TurnSignalThread turnSignalThread;
	private boolean applyTexture = false;
	
	
	public enum LightState
	{
		AllOff, LeftTurn, RightTurn, HazardLights, BrakeLights, 
		LeftTurnBrakeLights, RightTurnBrakeLights, HazardLightsBrakeLights;
	}
	
	
	public enum TurnSignalState
	{
		LEFT, RIGHT, BOTH, OFF;
	}
	
	
	public LightTexturesContainer(Simulator sim, Car car, String lightTexturesPath) 
	{
		this.sim = sim;
		this.carNode = car.getCarNode();
		
		// init light textures container
		lightTexturesContainer = new HashMap<LightState,HashMap<Spatial,Material>>();
		for(LightState lightState : LightState.values())
			lightTexturesContainer.put(lightState, new HashMap<Spatial,Material>());
		
		// load lights texture file
		processLightTexturesFile(lightTexturesPath);
		
		// init turn signal thread
		turnSignalThread = new TurnSignalThread(this, sim, car);
		
		// init light state
		//lightState = LightState.AllOff;
		setLightState(LightState.AllOff);
	}
	
	
	public void printAllContent()
	{
		for(LightState lightState : LightState.values())
		{
			HashMap<Spatial,Material> map = lightTexturesContainer.get(lightState);
			
			Iterator<Entry<Spatial,Material>> it = map.entrySet().iterator();
		    while(it.hasNext()) 
		    {
		        Entry<Spatial,Material> pairs = (Entry<Spatial,Material>)it.next();
		        System.out.println(carNode + ": " + lightState.toString() + ": " + pairs.getKey() + 
		        		" = " + pairs.getValue());
		    }
		}
	}
	
	
	public void setBrakeLight(boolean setToOn)
	{
		LightState targetLightState = null;
		
		if(setToOn)
		{
			switch(lightState)
			{
				case AllOff : targetLightState = LightState.BrakeLights; break;
				case LeftTurn : targetLightState = LightState.LeftTurnBrakeLights; break;
				case RightTurn : targetLightState = LightState.RightTurnBrakeLights; break;
				case HazardLights : targetLightState = LightState.HazardLightsBrakeLights; break;
				default: break;
			}
		}
		else
		{
			switch(lightState)
			{
				case BrakeLights : targetLightState = LightState.AllOff; break;
				case LeftTurnBrakeLights : targetLightState = LightState.LeftTurn; break;
				case RightTurnBrakeLights : targetLightState = LightState.RightTurn; break;
				case HazardLightsBrakeLights : targetLightState = LightState.HazardLights; break;
				default: break;
			}
		}	
		
		if((targetLightState != null) && (targetLightState != getLightState()))
			setLightState(targetLightState);
	}
	
	
	public void setTurnSignal(TurnSignalState turnSignalState)
	{
		//start turn signal thread if not running
		if(!turnSignalThread.isAlive())
			turnSignalThread.start();

		// set requested turn signal
		turnSignalThread.setTurnSignalState(turnSignalState);
	}

	
	public TurnSignalState getTurnSignal() 
	{
		// if not running
		if(!turnSignalThread.isAlive())
			return TurnSignalState.OFF;
		else
			return turnSignalThread.getTurnSignalState();
	}
	
	
	public void setLightState(LightState lightState)
	{
		this.lightState = lightState;
		applyTexture = true;
	}
	
	
	public void update()
	{
		if(applyTexture)
		{
			applyTexture(lightState);
			applyTexture = false;
		}
	}
	
	
	public LightState getLightState() 
	{
		return lightState;
	}
	
	
	public boolean isBrakeLightOn() 
	{
		switch(lightState)
		{
			case AllOff :
			case LeftTurn :
			case RightTurn :
			case HazardLights : return false;
			case BrakeLights :
			case LeftTurnBrakeLights :
			case RightTurnBrakeLights :
			case HazardLightsBrakeLights : return true;
		}
		return false;
	}
	
	
	public void close() 
	{
		turnSignalThread.requestStop();
	}
	
	
	private void applyTexture(LightState lightState)
	{
		HashMap<Spatial,Material> map = lightTexturesContainer.get(lightState);
		
		Iterator<Entry<Spatial,Material>> it = map.entrySet().iterator();
	    while(it.hasNext()) 
	    {
	        Entry<Spatial,Material> pairs = (Entry<Spatial,Material>)it.next();
	        Spatial spatial = pairs.getKey();
	        Material material = pairs.getValue();
	        
	        spatial.setMaterial(material);
	    }
	}
	
	
	private void processLightTexturesFile(String lightTexturesPath) 
	{
		File lightTexturesFile = new File(lightTexturesPath);
		String parentDirectory = lightTexturesFile.getParent();

		try{
			Document document = (Document) sim.getAssetManager().loadAsset(lightTexturesPath);
			
			NodeList rootNodeList = document.getChildNodes();
			for(int i = 0; i<rootNodeList.getLength(); i++)
			{
				org.w3c.dom.Node rootNode = rootNodeList.item(i);
				NodeList lightTextureList = rootNode.getChildNodes();
				
				for(int j = 0; j<lightTextureList.getLength(); j++)
				{
					org.w3c.dom.Node lightTextureNode = lightTextureList.item(j);
					
					if(lightTextureNode.getNodeName().equalsIgnoreCase("position"))
					{
						String path = lightTextureNode.getAttributes().getNamedItem("path").getNodeValue();
						NodeList stateList = lightTextureNode.getChildNodes();
						
						for(int k = 0; k<stateList.getLength(); k++)
						{
							org.w3c.dom.Node stateNode = stateList.item(k);
							
							for(LightState state : LightState.values())
							{
								if(stateNode.getNodeName().equalsIgnoreCase(state.toString()))
								{
									org.w3c.dom.Node texture = stateNode.getAttributes().getNamedItem("texture");
									if(texture != null)
									{
										String textureString = texture.getNodeValue();
										if(!textureString.isEmpty())
											addTexture(path, state, parentDirectory + "/" + textureString);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e){
			System.err.println("Could not process file: " + lightTexturesPath);
		}
	}


	private void addTexture(String spatialPath, LightState lightState, String texturePath)
	{
		Spatial partOfCar = lookupInCarNode(spatialPath);
		Material material = loadMaterial(texturePath);
		
		if(partOfCar != null && material != null)
		{
			//System.err.println("SET: " + spatialPath + " - " + lightState.toString() + " - " + texturePath);
			
			// add assignment to hash map
			HashMap<Spatial,Material> map = lightTexturesContainer.get(lightState);
			if(map.put(partOfCar, material) != null)
				System.err.println("Caution: old assignment of " + spatialPath + 
						" in node " + carNode + " has been overwritten.");
		}
	}


	private Material loadMaterial(String texturePath)
	{
		Material material = null;
		
		try{
			TextureKey textureKey = new TextureKey(texturePath, false);
			Texture texture = sim.getAssetManager().loadTexture(textureKey);
			material = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
			material.setTexture("ColorMap",texture);
			
		} catch (Exception e){
			e.printStackTrace();
			System.err.println("Error loading texture file " + texturePath);
		}
		
	    return material;
	}


	private Spatial lookupInCarNode(String path) 
	{
		return lookupInSpatial(carNode, path);
	}
	
	
    private Spatial lookupInSpatial(Spatial spatial, String path)
    {
    	String[] pathElements = path.split("/");
    	
        if (spatial instanceof Node) 
        {        	
        	if(pathElements.length>0)
        	{
        		String pathElementsTail = "";
        		
        		for(int i = 1; i<pathElements.length; i++)
        		{
        			if(pathElementsTail.isEmpty())
        				pathElementsTail = pathElements[i];
        			else
        				pathElementsTail += "/" + pathElements[i];
        		}
        		
        		Node node = (Node) spatial;
        		return lookupInSpatial(node.getChild(pathElements[0]),pathElementsTail);
        	}
        	else
        		return spatial;
        } 
        else if (spatial instanceof Geometry) 
        {
            if (path.isEmpty())
            	return spatial;
            else
                return null;
        }
        return null;
    }

}
