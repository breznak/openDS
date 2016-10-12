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
import com.jme3.scene.Spatial;

import eu.opends.main.Simulator;

public class RemoveUpdate implements Update 
{
	private Simulator sim;
	private String vehicleID;

	
	public RemoveUpdate(Simulator sim, String vehicleID) 
	{
		this.sim = sim;
		this.vehicleID = vehicleID;
	}
	
	
	@Override
	public void performUpdate() 
	{
		//System.err.println("removeVehicle() --> vehicleID: " + vehicleID);

		try {
			
			Spatial object = sim.getSceneNode().getChild(vehicleID);
			VehicleControl control = (VehicleControl) object.getControl(0);
			sim.getPhysicsSpace().remove(control);
			sim.getSceneNode().detachChild(object);
			
			sim.getMultiDriverClient().removeRegisteredVehicle(vehicleID);
			
		} catch(Exception e) {
			System.err.println("Could not delete vehicle '" + vehicleID + "'!");
		}
	}

}
