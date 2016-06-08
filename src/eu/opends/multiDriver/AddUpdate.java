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

package eu.opends.multiDriver;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Sphere;

import eu.opends.car.CarModelLoader;
import eu.opends.main.Simulator;

public class AddUpdate implements Update
{
	private Simulator sim;
	private String vehicleID;
	private String modelPath;
	//private String driverName;
	
	
	public AddUpdate(Simulator sim, String vehicleID, String modelPath, String driverName) 
	{
		this.sim = sim;
		this.vehicleID = vehicleID;
		this.modelPath = modelPath;
		//this.driverName = driverName;
	}

	public void performUpdate()
	{
		//System.err.println("addVehicle() --> vehicleID: " + vehicleID + ", modelPath: " + modelPath + " , driverName: " + driverName);
		
		try {
			
			// load new car with the CarModelLoader
			CarModelLoader carModel = new CarModelLoader(sim, null, modelPath, 0);
			VehicleControl carControl = carModel.getCarControl();
			Node carNode = carModel.getCarNode();
			carNode.setName(vehicleID);	
			
			// add bounding sphere to a multi-driver car which can be hit by the eye gaze ray
			Sphere sphere = new Sphere(20, 20, 4);
			Geometry boundingSphere = new Geometry(vehicleID + "_boundingSphere", sphere);
			Material boundingSphereMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
			boundingSphereMaterial.setColor("Color", ColorRGBA.Green);
			boundingSphere.setMaterial(boundingSphereMaterial);
	        boundingSphere.setCullHint(CullHint.Always);
			carNode.attachChild(boundingSphere);
			
			sim.getPhysicsSpace().add(carControl);		
			sim.getSceneNode().attachChild(carNode);
			
			sim.getMultiDriverClient().addRegisteredVehicle(vehicleID);
			
		} catch(Exception e) {
			System.err.println("Could not create vehicle '" + vehicleID + "'!");
		}
		
	}

}
