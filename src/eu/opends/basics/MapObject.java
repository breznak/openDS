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

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;



/**
 * This class represents an additional map object as it is used within 
 * class MapFactory in order to place spatial objects on the map.
 * 
 * @author Rafael Math
 */
public class MapObject 
{
	private String name;
	private Spatial spatial;
	private Vector3f location;
	private Quaternion rotation;
	private Vector3f scale;
	private boolean isVisible;
	private String collisionShape;
	private float mass;
	private String modelPath;
	private String collisionSound;
	

	/**
	 * Creates a new map object which can either be a static or 
	 * dynamic map object (c.f. related sub classes) and provides 
	 * setter and getter methods for all fields.
	 * 
	 * @param name
	 * 			Name of the map object.
	 * 
	 * @param spatial
	 * 			Spatial nodes to add to the scene graph.
	 * 
	 * @param location
	 * 			Location where the nodes should be added.
	 * 
	 * @param rotation
	 * 			Rotation of the spatial node.
	 * 
	 * @param scale
	 * 			Scaling vector of the spatial node.
	 * 
	 * @param isVisible
	 * 			Defines whether the object is visible to the driver.
	 * 
	 * @param collisionShape
	 * 			Defines whether the car can collide with the object.
	 * 
	 * @param mass
	 * 			Mass of the dynamic map object.
	 * 
	 * @param modelPath 
	 * 			Path to model files
	 * 
	 * @param collisionSound 
	 * 			Sound played when driver car collides with object
	 */
	public MapObject(String name, Spatial spatial, Vector3f location, Quaternion rotation, Vector3f scale,
			boolean isVisible, String collisionShape, float mass, String modelPath, String collisionSound)
	{
		this.name = name;
		this.spatial = spatial;
		this.location = location;
		this.rotation = rotation;
		this.scale = scale;
		this.isVisible = isVisible;
		this.collisionShape = collisionShape;
		this.mass = mass;
		this.modelPath = modelPath;
		this.collisionSound = collisionSound;
	}

	
	/**
	 * Returns the name of the map object
	 * 
	 * @return 
	 * 			The name of the map object
	 */
	public String getName() 
	{
		return name;
	}
	
	
	/**
	 * Sets the name of the map object
	 * 
	 * @param name 
	 * 			The name of the map object to set
	 */
	public void setName(String name) 
	{
		this.name = name;
	}

	
	/**
	 * Returns the spatial of the map object
	 * 
	 * @return 
	 * 			The spatial of the map object
	 */
	public Spatial getSpatial()
	{
		return spatial;
	}

	
	/**
	 * Sets the spatial of the map object
	 * 
	 * @param spatial 
	 * 			The spatial of the map object to set
	 */
	public void setSpatial(Spatial spatial) 
	{
		this.spatial = spatial;
	}

	/**
	 * Returns the location of the map object
	 * 
	 * @return 
	 * 			The location of the map object
	 */
	public Vector3f getLocation() 
	{
		return location;
	}

	
	/**
	 * Sets the location of the map object
	 * 
	 * @param location 
	 * 			The location of the map object to set
	 */
	public void setLocation(Vector3f location) 
	{
		this.location = location;
	}
	

	/**
	 * Returns the rotation of the map object
	 * 
	 * @return 
	 * 			The rotation of the map object
	 */
	public Quaternion getRotation() 
	{
		return rotation;
	}

	
	/**
	 * Sets the rotation of the map object
	 * 
	 * @param rotation 
	 * 			The rotation of the map object to set
	 */
	public void setRotation(Quaternion rotation) 
	{
		this.rotation = rotation;
	}

	
	/**
	 * Returns the scale of the map object
	 * 
	 * @return 
	 * 			The scale of the map object
	 */
	public Vector3f getScale() 
	{
		return scale;
	}

	
	/**
	 * Sets the scale of the map object
	 * 
	 * @param scale 
	 * 			The scale of the map object to set
	 */
	public void setScale(Vector3f scale) 
	{
		this.scale = scale;
	}


	/**
	 * Returns whether the map object will be visible
	 * 
	 * @return 
	 * 			True, if the map object is visible
	 */
	public boolean isVisible() 
	{
		return isVisible;
	}

	
	/**
	 * Sets whether the map object will be visible
	 * 
	 * @param isVisible 
	 * 			If true, the map object will be visible
	 */
	public void setVisible(boolean isVisible) 
	{
		this.isVisible = isVisible;
	}

	
	/**
	 * Returns whether the map object will be collidable
	 * 
	 * @return 
	 * 			True, if the map object is collidable
	 */
	public String getCollisionShape() 
	{
		return collisionShape;
	}


	public void setCollisionShape(String collisionShape) 
	{
		this.collisionShape = collisionShape;
	}
	
	
	/**
	 * Returns the mass of the map object
	 * 
	 * @return 
	 * 			The mass of the map object
	 */
	public float getMass() 
	{
		return mass;
	}

	
	/**
	 * Sets the mass of the map object
	 * 
	 * @param mass 
	 * 			The mass of the map object to set
	 */
	public void setMass(float mass) 
	{
		this.mass = mass;
	}
	
	
	/**
	 * Returns the model path of the map object
	 * 
	 * @return 
	 * 			The model path of the map object
	 */
	public String getModelPath() 
	{
		return modelPath;
	}
	
	
	/**
	 * Sets the model path of the map object
	 * 
	 * @param modelPath 
	 * 			The model path of the map object to set
	 */
	public void setModelPath(String modelPath) 
	{
		this.modelPath = modelPath;
	}

	
	/**
	 * Returns the collision sound of the map object
	 * 
	 * @return 
	 * 			The collision sound of the map object
	 */
	public String getCollisionSound() 
	{
		return collisionSound;
	}
	
	
	/**
	 * Sets the collision sound of the map object
	 * 
	 * @param collisionSound 
	 * 			The collision sound of the map object to set
	 */
	public void setCollisionSound(String collisionSound) 
	{
		this.collisionSound = collisionSound;
	}

}
