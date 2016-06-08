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

package eu.opends.environment;

import java.util.ArrayList;

import com.jme3.asset.TextureKey;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;

import eu.opends.main.Simulator;
import eu.opends.trigger.TriggerCenter;


/**
 * This class represents a single traffic light which can be switched between 
 * different states. A traffic light may be equipped with arrow lights or just
 * with a circular light.
 * 
 * @author Rafael Math
 */
public class TrafficLight
{
	/**
	 * TrafficLightState indicates the combination of illuminated lights.
	 */
	public enum TrafficLightState
	{
		RED,GREEN,YELLOW,YELLOWRED,OFF,ALL;
		
		// builds state-specific part of the texture file name
		public String getStateString()
		{
			return "_" + this.toString().toLowerCase();
		}
	}

	
	/**
	 * TrafficLightDirection indicates whether the the traffic light has 
	 * arrow-shaped lights and to which direction they are pointing. 
	 */
	public enum TrafficLightDirection
	{
		LEFT,RIGHT,UP,NONE;
		
		// builds direction-specific part of the texture file name
		public String getDirectionString()
		{
			if(this == NONE)
				return "";
			else
				return "_" + this.toString().toLowerCase();
		}
	}
	
	
	private Simulator sim;
	private Spatial trafficLightObject;
	private String name;
	private String intersectionID;
	private TrafficLightState state;
	private TrafficLightDirection direction;
	private int phasePosition;
	private ArrayList<TrafficLight> requiresRedList = null;
	private ArrayList<String> requiresRedStringList;
	private TrafficLightPositionData positionData;
	

	public TrafficLight(Simulator sim, String trafficLightID, String trafficLightTriggerID,
			String trafficLightPhaseTriggerID, String trafficLightGroupID, 
			TrafficLightState initialState, TrafficLightDirection direction, int phasePosition,
			ArrayList<String> requiresRedStringList, TrafficLightPositionData positionData)
	{
		this.sim = sim;
		this.name = trafficLightID;
		this.trafficLightObject = sim.getSceneNode().getChild(trafficLightID);
		this.intersectionID = trafficLightGroupID;
		this.state = initialState;
		this.direction = direction;
		this.phasePosition = phasePosition;
		this.requiresRedStringList = requiresRedStringList;
		this.positionData = positionData;
		updateTexture();
		
		if(sim instanceof Simulator)
		{
			// check whether specified scene object is available
			// if available, move it to trigger node (sub node of scene node)
			Spatial trafficLightTriggerObject = sim.getSceneNode().getChild(trafficLightTriggerID);
			if(trafficLightTriggerObject == null)
			{
				// if not available, attach default trigger to trigger node
				trafficLightTriggerObject = generateTrafficLightTrigger();
			}
			sim.getTriggerNode().attachChild(trafficLightTriggerObject);
			TriggerCenter.addToTrafficLightTriggerList(name, trafficLightTriggerObject);
			
			
			// check whether specified scene object is available
			// if available, move it to trigger node (sub node of scene node)
			Spatial trafficLightPhaseTriggerObject = sim.getSceneNode().getChild(trafficLightPhaseTriggerID);
			if(trafficLightPhaseTriggerObject == null)
			{
				// if not available, attach default phase trigger to trigger node
				trafficLightPhaseTriggerObject = generateTrafficLightPhaseTrigger();
			}
			sim.getTriggerNode().attachChild(trafficLightPhaseTriggerObject);
			TriggerCenter.addToTrafficLightPhaseTriggerList(name, trafficLightPhaseTriggerObject);
		}
	}
	
	
	public void activateTrafficLightRules()
	{
		requiresRedList = stringListToTrafficLightList(requiresRedStringList);
	}
	
	
	/**
	 * Returns the name of the traffic light
	 * 
	 * @return
	 * 			name of traffic light
	 */
	public String getName()
	{
		return name;
	}
	
	
	/**
	 * Returns the local position of the traffic light
	 * 
	 * @return
	 * 			Local position of traffic light
	 */
	public Vector3f getLocalPosition()
	{
		return trafficLightObject.getLocalTranslation();
	}
	
	
	/**
	 * Returns the world position of the traffic light
	 * 
	 * @return
	 * 			World position of traffic light
	 */
	public Vector3f getWorldPosition()
	{
		return trafficLightObject.getWorldTranslation();
	}
	

	/**
	 * Returns the rotation of the traffic light
	 * 
	 * @return
	 * 			rotation of traffic light
	 */
	public Quaternion getRotation() 
	{
		return trafficLightObject.getWorldRotation();
	}
	
	
	/**
	 * Returns the ID of the intersection the traffic light is associated to
	 * 
	 * @return
	 * 			ID of the intersection where the traffic light is located
	 */
	public String getIntersectionID()
	{
		return intersectionID;
	}
	
	
	/**
	 * Returns the traffic light state, e.g. TrafficLightState.RED
	 * 
	 * @return
	 * 			traffic light state
	 */
	public TrafficLightState getState()
	{
		return state;
	}
	
	
	/**
	 * Changes the traffic light state immediately to the given value 
	 * (e.g. TrafficLightState.RED) by loading the corresponding texture
	 * file for the traffic light. 
	 * 
	 * @param state
	 * 			state to change to immediately
	 */
	public void setState(TrafficLightState state)
	{
		if(this.state != state)
		{
			this.state = state;
			
			updateTexture();
			
			// creates a string containing all current traffic light states
			// this string will be sent to external programs, i.e. Lightning
			if(sim.getTrafficLightCenter() != null)
				sim.getTrafficLightCenter().updateGlobalStatesString();
		}
	}
	
	
	/**
	 * Returns the traffic light direction, e.g. TrafficLightDirection.RIGHT
	 * 
	 * @return
	 * 			traffic light direction
	 */
	public TrafficLightDirection getDirection()
	{
		return direction;
	}
	
	
	/**
	 * Changes the traffic light direction immediately to the given value 
	 * (e.g. TrafficLightDirection.RIGHT) by loading the corresponding texture
	 * file for the traffic light. 
	 * 
	 * @param direction
	 * 			direction to change to immediately
	 */
	public void setDirection(TrafficLightDirection direction)
	{
		if(this.direction != direction)
		{
			this.direction = direction;

			updateTexture();
		}
	}
	
	
	/**
	 * Returns a list of traffic lights which have to be red before 
	 * the current traffic light may be switched to green
	 * 
	 * @return
	 * 			List of traffic lights that are required to be red
	 */
	public ArrayList<TrafficLight> getTrafficLightRules()
	{
		return requiresRedList;
	}
	
	
	/**
	 * Returns the traffic light position data
	 * 
	 * @return
	 * 			traffic light position data
	 */
	public TrafficLightPositionData getPositionData()
	{
		return positionData;
	}
	
	
	/**
	 * Sets the traffic light position data
	 * 
	 * @param positionData
	 * 			traffic light position data
	 */
	public void setPositionData(TrafficLightPositionData positionData) 
	{
		this.positionData = positionData; 
	}
	

	public int getPhasePosition() 
	{
		return phasePosition;
	}

	
	/**
	 * Updates the texture of the current traffic light according to the given state 
	 * and direction.
	 */
	private void updateTexture()
	{
		Spatial textureSpatial = ((Node) trafficLightObject).getChild(0);
		Material mat = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		TextureKey textureKey = new TextureKey(getTrafficLightTexture(), true); //TODO edit texture files and set flip to false
	    mat.setTexture("ColorMap",sim.getAssetManager().loadTexture(textureKey));
	    mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
	    textureSpatial.setMaterial(mat);
	}

	
	/**
	 * Returns texture of the current traffic light according to the given state
	 * and direction
	 * @return
	 * 			texture to use with current traffic light
	 */
	private String getTrafficLightTexture()
	{
		// basic file path
		String filepath = "Textures/TrafficLight/trafficlight";
		
		// state specific extension
		String stateString = state.getStateString();
		
		// direction specific extension
		String directionString = "";
		
		// if traffic light is switched off, no distinction between arrow-shaped textures necessary
		if(state != TrafficLightState.OFF)
			directionString = direction.getDirectionString();		

        return filepath + stateString + directionString + ".tga";
	}
	

	/**
	 * This method converts a string list of traffic light names into an object list 
	 * containing the corresponding traffic lights by iteratively looking up traffic 
	 * light objects by name.
	 * 
	 * @param stringList
	 * 			string list containing names of traffic lights to look up
	 *  
	 * @return
	 * 			traffic light list containing all traffic lights as specified in input list
	 */
	private ArrayList<TrafficLight> stringListToTrafficLightList(ArrayList<String> stringList)
	{
		ArrayList<TrafficLight> trafficLightList = new ArrayList<TrafficLight>(10);
		
		for(String string : stringList)
		{
			TrafficLight trafficLight = TrafficLightCenter.getTrafficLightByName(string);
			if(trafficLight != null)
				trafficLightList.add(trafficLight);
		}
		
		return trafficLightList;
	}
	
	
	/**
	 * Generates traffic light triggers 20 meters before a traffic light.
	 * 
	 * @param blenderObjectsList
	 */
	private Spatial generateTrafficLightTrigger()
	{
		Vector3f relativePos;
		
		// get rotation of traffic light in origin (0,0,0) 
		Quaternion localRotation = trafficLightObject.getWorldRotation();
		
		// get translation from origin to traffic light
		Vector3f localTranslation = trafficLightObject.getLocalTranslation();
		
		
		// traffic light for green trigger
		// *******************************
		
		// create trigger in origin
		Box trigger = new Box(0.4f, 0.2f, 12f);
		Spatial triggerBox = new Geometry("TrafficLightTriggerGeometry:" + name, trigger);
		Material mat = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Green);
		triggerBox.setMaterial(mat);
		
		// set relative position of trigger to the traffic light
		if(direction == TrafficLightDirection.LEFT || direction == TrafficLightDirection.UP)
			relativePos = new Vector3f(-4f, 1.5f, 12f);
		else
			relativePos = new Vector3f(-2.7f, 1.5f, 12f);
		triggerBox.setLocalTranslation(relativePos);

		// set properties of trigger
		triggerBox.setModelBound(new BoundingBox());
		triggerBox.setCullHint(CullHint.Always);
		triggerBox.updateModelBound();
		
		Node triggerNode = new Node("TrafficLightTrigger:" + name);
		triggerNode.attachChild(triggerBox);
		
		// rotate trigger in the same way as the traffic light in origin
		triggerNode.setLocalRotation(localRotation);
		
		// move rotated trigger from origin to a location next to the traffic light
		triggerNode.setLocalTranslation(localTranslation);
		
		return triggerNode;
	}


	/**
	 * Generates traffic light triggers 20 meters before a traffic light.
	 * 
	 * @param blenderObjectsList
	 */
	private Spatial generateTrafficLightPhaseTrigger()
	{	
		Vector3f relativePos;
		
		// get rotation of traffic light in origin (0,0,0) 
		Quaternion localRotation = trafficLightObject.getWorldRotation();
		
		// get translation from origin to traffic light
		Vector3f localTranslation = trafficLightObject.getLocalTranslation();
		
		
		// traffic light phase trigger
		// ***************************

		// create trigger in origin
		Box trigger = new Box(0.4f, 0.2f, 50f);
		Spatial triggerBox = new Geometry("TrafficLightPhaseTriggerGeometry:" + name, trigger);
		Material mat = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Red);
		triggerBox.setMaterial(mat);
		
		// set relative position of trigger to the traffic light
		if(direction == TrafficLightDirection.LEFT || direction == TrafficLightDirection.UP)
			relativePos = new Vector3f(-4f, 1.5f, 60f);
		else
			relativePos = new Vector3f(-2.7f, 1.5f, 60f);
		triggerBox.setLocalTranslation(relativePos);
				
		// set properties of trigger
		triggerBox.setModelBound(new BoundingBox());
		triggerBox.setCullHint(CullHint.Always);
		triggerBox.updateModelBound();
		
		Node triggerNode = new Node("TrafficLightPhaseTrigger:" + name);
		triggerNode.attachChild(triggerBox);
		
		// rotate trigger in the same way as the traffic light in origin
		triggerNode.setLocalRotation(localRotation);
		
		// move rotated trigger from origin to a location next to the traffic light
		triggerNode.setLocalTranslation(localTranslation);
		
		return triggerNode;
	}


}