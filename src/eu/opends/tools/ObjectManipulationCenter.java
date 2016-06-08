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

package eu.opends.tools;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;

import eu.opends.basics.SimulationBasics;
import eu.opends.environment.GeoPosition;

/**
 * 
 * @author Rafael Math
 */
public class ObjectManipulationCenter 
{
	private SimulationBasics sim;

	
	public ObjectManipulationCenter(SimulationBasics sim)
	{
		this.sim = sim;
	}
	
	
	public void setGeoPosition(String objectID, Vector3d geoPosition)
	{
		Vector3f modelPosition = GeoPosition.geoToModel(geoPosition);
		setPosition(objectID, modelPosition);
	}
	
	
	public void setPosition(String objectID, Vector3f position)
	{		
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
		
		
		if(control != null)
			control.setPhysicsLocation(position);
		else
			object.setLocalTranslation(position);
	}
	
	
	public void setOrientation(String objectID, float orientation)
	{
		setRotation(objectID, new float[] {0,orientation,0});
	}
	
			
	public void setRotation(String objectID, float[] rotation)
	{
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
		
		if(control != null)
		{
			Quaternion rot = new Quaternion().fromAngles(degToRad(rotation));
			control.setPhysicsRotation(rot);
		}
		else
		{
			Quaternion rot = new Quaternion().fromAngles(degToRad(rotation));
			object.setLocalRotation(rot);
		}
	}
			
	
	public void setScale(String objectID, Vector3f scale)
	{
		Spatial object = Util.findNode(sim.getRootNode(), objectID);
		object.setLocalScale(scale);

	}
		
	
	public void setVisibility(String objectID, boolean isVisible)
	{
		Spatial object = Util.findNode(sim.getRootNode(), objectID);
		
		if(isVisible)
			object.setCullHint(CullHint.Dynamic);
		else
			object.setCullHint(CullHint.Always);
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
}
