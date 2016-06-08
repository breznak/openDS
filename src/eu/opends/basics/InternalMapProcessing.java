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

import java.util.ArrayList;
import java.util.List;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;

import eu.opends.main.Simulator;

/**
 * This class is used to further process the elements on the map.
 * 
 * @author Rafael Math
 */
public class InternalMapProcessing
{
	private SimulationBasics sim;
	private Node sceneNode;
	private PhysicsSpace physicsSpace;
	private List<Spatial> triggerList = new ArrayList<Spatial>();
	
	
	public InternalMapProcessing(SimulationBasics sim)
	{
		this.sim = sim;
		this.sceneNode = sim.getSceneNode();
		this.physicsSpace = sim.getPhysicsSpace();
		
		// get list of additional objects (generated from XML file)
		addMapObjectsToScene(Simulator.getDrivingTask().getSceneLoader().getMapObjects());

		System.out.println("MapModelList:  [" + listToString(sceneNode) + "]");

		// apply triggers to certain visible objects
		if (sim instanceof Simulator) 
		{		
			//generateTrafficLightTriggers();
			generateDrivingTaskTriggers();
			addTriggersToTriggerNode();
		}
	}


	private String listToString(Node sceneNode) 
	{
		String output = "";
        boolean isFirstChild = true;
        for(Spatial child : sceneNode.getChildren())
        {
        	if(isFirstChild)
        	{
        		output += child.getName();
        		isFirstChild = false;
        	}
        	else
        		output += ", " + child.getName();
        }
		return output;
	}
	
	
	/**
	 * Converts a list of map objects into a list of spatial objects which 
	 * can be added to the simulators scene graph.
	 * 
	 * @param mapObjects
	 * 			List of map objects to convert
	 * 
	 * @return
	 * 			List of spatial objects
	 */
	private void addMapObjectsToScene(List<MapObject> mapObjects)
	{			
		for(MapObject mapObject : mapObjects)
		{	
			Node node = new Node(mapObject.getName());
			
			Spatial spatial = mapObject.getSpatial();
			
        	// set FaceCullMode of spatial's geometries to off
			// no longer needed, as FaceCullMode.Off is default setting
			//Util.setFaceCullMode(spatial, FaceCullMode.Off);
			
	    	node.attachChild(spatial);
	    	
	    	node.setLocalScale(mapObject.getScale());

	        node.updateModelBound();
	        
			// if marked as invisible then cull always else cull dynamic
			if(!mapObject.isVisible())
				node.setCullHint(CullHint.Always);
			
			String collisionShapeString = mapObject.getCollisionShape();
			if(collisionShapeString == null)
				collisionShapeString = "meshShape";
			
			node.setLocalTranslation(mapObject.getLocation());
	        node.setLocalRotation(mapObject.getRotation());
	        
			if((collisionShapeString.equalsIgnoreCase("boxShape") || collisionShapeString.equalsIgnoreCase("meshShape")))
			{
		        node.setLocalTranslation(mapObject.getLocation());
		        node.setLocalRotation(mapObject.getRotation());
				
		        CollisionShape collisionShape;
		        float mass = mapObject.getMass();

		        if(mass == 0)
		        {
		        	// mesh shape for static objects
			        if(collisionShapeString.equalsIgnoreCase("meshShape"))
			        	collisionShape = CollisionShapeFactory.createMeshShape(node);
			        else
			        	collisionShape = CollisionShapeFactory.createBoxShape(node);
		        }
		        else
		        {
			        // set whether triangle accuracy should be applied
			        if(collisionShapeString.equalsIgnoreCase("meshShape"))
			        	collisionShape = CollisionShapeFactory.createDynamicMeshShape(node);
			        else
			        	collisionShape = CollisionShapeFactory.createBoxShape(node);
		        }		        
		        
		        RigidBodyControl physicsControl = new RigidBodyControl(collisionShape, mass);
		        node.addControl(physicsControl);

		        physicsControl.setPhysicsLocation(mapObject.getLocation());
		        physicsControl.setPhysicsRotation(mapObject.getRotation());
		        
		        //physicsControl.setFriction(100);
		        
		        // add additional map object to physics space
		        physicsSpace.add(physicsControl);
			}

			
	        // attach additional map object to scene node
			sceneNode.attachChild(node);
		}
	}
	
	
	/**
	 * Generates blind triggers which replace the original boxes.
	 * 
	 * @param blenderObjectsList
	 */
	private void generateDrivingTaskTriggers()
	{		
		for (Spatial object : sceneNode.getChildren()) 
		{
			if (SimulationBasics.getTriggerActionListMap().containsKey(object.getName())) 
			{
				// add trigger to trigger list
				triggerList.add(object);
			}
		}
	}
	
	
	private void addTriggersToTriggerNode()
	{
		for(Spatial object : triggerList)
		{
			// add trigger to trigger node
			sim.getTriggerNode().attachChild(object);
		}
	}	
}
