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

package eu.opends.trigger;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;

import eu.opends.basics.SimulationBasics;
import eu.opends.tools.Util;


/**
 * This class represents a ManipulateObject trigger action. Whenever a collision
 * with a related trigger was detected, the given object will be manipulated in 
 * the specified way.
 * 
 * @author Rafael Math
 */
public class ManipulateObjectTriggerAction extends TriggerAction 
{
	private SimulationBasics sim;
	private String objectID;
	
	private Float[] translation;
	private Float[] addTranslation;
	private Float[] rotation;
	private Float[] addRotation;
	private Float[] scale;
	private Float[] addScale;
	private CullHint visibility;
	
	private boolean updateLocation;
	private boolean updateRotation;
	private boolean updateScale;
	private boolean updateVisibility;

	
	/**
	 * Creates a new ManipulateObject trigger action instance, providing maximum
	 * number of repetitions and the object to manipulate. 
	 * 
	 * @param sim
	 * 			Simulator
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param maxRepeat
	 * 			Maximum number how often the trigger can be hit (0 = infinite).
	 * 
	 * @param objectID
	 * 			ID of the object to manipulate.
	 */
	public ManipulateObjectTriggerAction(SimulationBasics sim, float delay, int maxRepeat, String objectID) 
	{
		super(delay, maxRepeat);
		this.sim = sim;
		this.objectID = objectID;
		this.updateLocation = false;
		this.updateRotation = false;
		this.updateScale = false;
		this.updateVisibility = false;
	}

	
	public void setTranslation(Float[] translation)
	{
		this.translation = translation;
		this.updateLocation = true;
	}

	
	public void addTranslation(Float[] addTranslation) 
	{
		this.addTranslation = addTranslation;
		this.updateLocation = true;
	}
	
	
	public void setRotation(Float[] rotation)
	{
		this.rotation = rotation;
		this.updateRotation = true;
	}
	

	public void addRotation(Float[] addRotation) 
	{
		this.addRotation = addRotation;
		this.updateRotation = true;
	}
	
	
	public void setScale(Float[] scale)
	{
		this.scale = scale;
		this.updateScale = true;
	}
	

	public void addScale(Float[] addScale) 
	{
		this.addScale = addScale;
		this.updateScale = true;
	}
	
	
	public void setVisibility(boolean isVisible)
	{
		if(isVisible)
			this.visibility = CullHint.Dynamic;
		else
			this.visibility = CullHint.Always;
		this.updateVisibility = true;
	}
		
	
	/**
	 * Manipulates the given object by applying a translation, rotation, 
	 * scaling or visibility change. 
	 */
	@Override
	protected void execute()
	{
		if(!isExceeded())
		{
			
			try {
				
				// get "visual" or "physical" spatial
				// search in all sub-nodes of root node (scene node, trigger node, ...)
				Spatial object = Util.findNode(sim.getRootNode(), objectID);
				RigidBodyControl control = null;
				
				try {
					control = (RigidBodyControl) object.getControl(0);
				}
				catch(IndexOutOfBoundsException e2)
				{
					System.err.println("Could not manipulate physics of '" + objectID + "'!");
				}
				
				
				if(updateLocation)
				{
					if(control != null)
					{
						Vector3f previousLocation = control.getPhysicsLocation().clone();
						
						if(translation != null)
							previousLocation = mergeVector(translation, previousLocation);
						
						if(addTranslation != null)
							previousLocation = addVector(addTranslation, previousLocation);
						
						control.setPhysicsLocation(previousLocation);
					}
					else
					{
						Vector3f previousLocation = object.getLocalTranslation().clone();
						
						if(translation != null)
							previousLocation = mergeVector(translation, previousLocation);
						
						if(addTranslation != null)
							previousLocation = addVector(addTranslation, previousLocation);
						
						object.setLocalTranslation(previousLocation);
					}
				}
				

				if(updateRotation)
				{
					if(control != null)
					{
						float[] previousRotation = radToDeg(control.getPhysicsRotation().clone().toAngles(null));
						
						if(rotation != null)
							previousRotation = mergeVector(rotation, previousRotation).toArray(null);
						
						if(addRotation != null)
							previousRotation = addVector(addRotation, previousRotation).toArray(null);
						
						Quaternion rot = new Quaternion().fromAngles(degToRad(previousRotation));
						control.setPhysicsRotation(rot);
					}
					else
					{
						float[] previousRotation = radToDeg(object.getLocalRotation().clone().toAngles(null));

						if(rotation != null)
							previousRotation = mergeVector(rotation, previousRotation).toArray(null);
						
						if(addRotation != null)
							previousRotation = addVector(addRotation, previousRotation).toArray(null);
						
						Quaternion rot = new Quaternion().fromAngles(degToRad(previousRotation));
						object.setLocalRotation(rot);
					}
				}
				
				
				if(updateScale)
				{
					Vector3f previousScale = object.getLocalScale().clone();
					
					if(scale != null)
						previousScale = mergeVector(scale, previousScale);
					
					if(addScale != null)
						previousScale = addVector(addScale, previousScale);
					
					object.setLocalScale(previousScale);
				}
					
				
				if(updateVisibility)
					object.setCullHint(visibility);
		
				
			} catch (Exception e){
				e.printStackTrace();
				System.err.println("Could not manipulate object '" + objectID + "'! Maybe it does not exist.");
			}
		
			updateCounter();
		}
	}	
	
	
	/**
	 * Transforms degree to radian angles
	 * 
	 * @param degreeArray
	 * 			Angle in degree to transform.
	 * 
	 * @return
	 * 			Angle in radians.
	 */
	private float[] degToRad(float[] degreeArray) 
	{
		float[] radianArray = new float[degreeArray.length];
		
		for(int i=0; i<degreeArray.length; i++)
			radianArray[i] = degreeArray[i] * (FastMath.PI/180);
		
		return radianArray;
	}
	
	
	private float[] radToDeg(float[] radianArray) 
	{
		float[] degreeArray = new float[radianArray.length];
		
		for(int i=0; i<radianArray.length; i++)
			degreeArray[i] = radianArray[i] * (180/FastMath.PI);
		
		return degreeArray;
	}
	
	
	private Vector3f mergeVector(Float[] nextVector, float[] previousVector)
	{
		return mergeVector(nextVector, new Vector3f(previousVector[0],previousVector[1],previousVector[2]));
	}
	
	
	private Vector3f mergeVector(Float[] nextVector, Vector3f previousVector)
	{
		if(nextVector[0] != null)
			previousVector.setX(nextVector[0]);
		
		if(nextVector[1] != null)
			previousVector.setY(nextVector[1]);
		
		if(nextVector[2] != null)
			previousVector.setZ(nextVector[2]);
		
		return previousVector;
	}
	
	
	private Vector3f addVector(Float[] nextVector, float[] previousVector)
	{
		return addVector(nextVector, new Vector3f(previousVector[0],previousVector[1],previousVector[2]));
	}
	
	
	private Vector3f addVector(Float[] nextVector, Vector3f previousVector)
	{	
		if(nextVector[0] != null)
			previousVector.setX(previousVector.getX() + nextVector[0]);
		
		if(nextVector[1] != null)
			previousVector.setY(previousVector.getY() + nextVector[1]);
		
		if(nextVector[2] != null)
			previousVector.setZ(previousVector.getZ() + nextVector[2]);
		
		return previousVector;
	}


	/**
	 * Returns a String of the object that will be manipulated.
	 */
	@Override
	public String toString()
	{
		return "Manipulate object: " + objectID;
		
	}


}
