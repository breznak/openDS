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

import java.util.ArrayList;
import java.util.List;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import eu.opends.main.Simulator;
import eu.opends.tools.Util;
import eu.opends.tools.Vector3d;

public class ChangeUpdate implements Update 
{
	private Simulator sim;
	private String vehicleID;
	private String positionString;
	private String rotationString;
	private String headingString;
	private String wheelString;
	
	
	public ChangeUpdate(Simulator sim, String vehicleID, String positionString, String rotationString, String headingString, String wheelString) 
	{
		this.sim = sim;
		this.vehicleID = vehicleID;
		this.positionString = positionString;
		this.rotationString = rotationString;
		this.headingString = headingString;
		this.wheelString = wheelString;
	}

	public void performUpdate()
	{
		//System.err.println("changeVehicle() --> vehicleID: " + vehicleID + ", positionString: " + positionString + " , rotationString: " + rotationString + " , wheelString: " + wheelString);
		Vector3f position = new Vector3f(0,0,0);
		
		String[] arrayPos = positionString.split(";");
		if(arrayPos.length == 3)
		{
			double xPos = Double.parseDouble(arrayPos[0]);
			double yPos = Double.parseDouble(arrayPos[1]);
			double zPos = Double.parseDouble(arrayPos[2]);
			//position = GeoPosition.geoToModel(new Vector3d(xPos, yPos, zPos));
			position = (new Vector3d(xPos, yPos, zPos)).toVector3f();
			setPosition(vehicleID, position);
		}
		
		String[] arrayRot = rotationString.split(";");
		if(arrayRot.length == 4)
		{
			float wRot = Float.parseFloat(arrayRot[0]);
			float xRot = Float.parseFloat(arrayRot[1]);
			float yRot = Float.parseFloat(arrayRot[2]);
			float zRot = Float.parseFloat(arrayRot[3]);
			Quaternion rotation = new Quaternion(xRot, yRot, zRot, wRot);
			setRotation(vehicleID, rotation);
		}
		
		if(!headingString.isEmpty())
		{
			float heading = -Float.parseFloat(headingString) * FastMath.DEG_TO_RAD;
			Quaternion rotation = adjustRotation(position, heading);
			setRotation(vehicleID, rotation);
		}
		
		String[] arrayWheel = wheelString.split(";");
		if(arrayWheel.length == 2)
		{
			float steering = Float.parseFloat(arrayWheel[0]);
			float wheelPosition = Float.parseFloat(arrayWheel[1]);
			setWheels(vehicleID, steering, wheelPosition);
		}		
	}

	
	private Quaternion adjustRotation(Vector3f position, float yaw) 
	{
		Node carNode = (Node) sim.getSceneNode().getChild(vehicleID);
				
		// adjust height
		Vector3f centerContactPoint = castPerpendicularRay(position);
		if(centerContactPoint != null)
		{
			position.setY(centerContactPoint.getY() + 0.075f);
			setPosition(vehicleID, position);
		}
		
		// adjust pitch angle
		float pitch = 0;
		Node frontPoint = (Node) carNode.getChild("frontPoint");
		Vector3f frontContactPoint = castPerpendicularRay(frontPoint.getWorldTranslation());

		Node backPoint = (Node) carNode.getChild("backPoint");
		Vector3f backContactPoint = castPerpendicularRay(backPoint.getWorldTranslation());
		
		if(frontContactPoint != null && backContactPoint != null)
		{
			float heightDiff = backContactPoint.getY() - frontContactPoint.getY();
			float zDistance = backPoint.getLocalTranslation().getZ() - frontPoint.getLocalTranslation().getZ();
			
			pitch = -FastMath.atan(heightDiff/zDistance); // pos pitch: front up / neg pitch: back up
		}
		
		// adjust roll angle
		float roll = 0;
		Node leftPoint = (Node) carNode.getChild("leftPoint");
		Vector3f leftContactPoint = castPerpendicularRay(leftPoint.getWorldTranslation());
	
		Node rightPoint = (Node) carNode.getChild("rightPoint");
		Vector3f rightContactPoint = castPerpendicularRay(rightPoint.getWorldTranslation());
		
		if(leftContactPoint != null && rightContactPoint != null)
		{
			float heightDiff = rightContactPoint.getY() - leftContactPoint.getY();
			float xDistance = rightPoint.getLocalTranslation().getX() - leftPoint.getLocalTranslation().getX();
			
			roll = FastMath.atan(heightDiff/xDistance); // pos roll: right up / neg roll: left up
		}

		return new Quaternion().fromAngles(pitch, yaw, roll);
	}

	
	private Vector3f castPerpendicularRay(Vector3f origin) 
	{
		// reset collision results list
		CollisionResults results = new CollisionResults();
		
		// direction towards ground (from car)
		Vector3f down = new Vector3f(0,-1,0);

		// aim a ray from the given point towards ground (perpendicular)
		Ray ray = new Ray(origin, down);

		// collect intersections between ray and scene elements in results list.
		sim.getSceneNode().collideWith(ray, results);
		
		/*
		// use the results
		if (results.size() > 0) 
		{			
			// the closest collision point is what was truly hit
			return results.getClosestCollision().getContactPoint();
		}
		*/
		
		for(int i=0; i<results.size(); i++)
		{
			CollisionResult collisionResult = results.getCollision(i);
			
			if(!isGeometryOfMultiDriverVehicle(collisionResult.getGeometry()))
				return collisionResult.getContactPoint();
		}
		
		return null;
	}
	
	
	private boolean isGeometryOfMultiDriverVehicle(Geometry geometry)
	{
		ArrayList<String> registeredVehicles = new ArrayList<String>();
		
		if(sim.getMultiDriverClient() != null)
			registeredVehicles = sim.getMultiDriverClient().getRegisteredVehicles();
		
		for(String vehicleID : registeredVehicles)
		{
			Node carNode = (Node) sim.getSceneNode().getChild(vehicleID);
			
			if(carNode != null)
			{			
				// all geometries of current multi-driver car
				List<Geometry> carGeometries = Util.getAllGeometries(carNode);
				for(Geometry g : carGeometries)
				{
					// if hit geometry is part of current car, return true
					if(g.equals(geometry))
						return true;
				}
			}
		}
		
		return false;
	}
	
	
	private void setPosition(String vehicleID, Vector3f position)
	{		
		try {
			
			Spatial object = sim.getSceneNode().getChild(vehicleID);
			VehicleControl control = (VehicleControl) object.getControl(0);
			control.setPhysicsLocation(position);

		} catch(Exception e) {
			System.err.println("Could not set position of vehicle '" + vehicleID + "'!");
		}
	}
	
	
	private void setRotation(String vehicleID, Quaternion rotation)
	{
		try {
			
			Spatial object = sim.getSceneNode().getChild(vehicleID);
			VehicleControl control = (VehicleControl) object.getControl(0);
			control.setPhysicsRotation(rotation);
			
		} catch(Exception e) {
			System.err.println("Could not set rotation of vehicle '" + vehicleID + "'!");
		}
	}
	

	private void setWheels(String vehicleID, float steering, float position)
	{
		try {
			
			//System.out.println("steering: " + steering + " - position: " + position);
						
			Spatial object = sim.getSceneNode().getChild(vehicleID);
			VehicleControl control = (VehicleControl) object.getControl(0);
			if(steering < 0)
				control.steer((3.1415927f + steering)/1.5f);
			else
				control.steer((-3.1415927f + steering)/1.5f);
			
			/*
			// TODO does not work !!!!
			control.getWheel(0).getWheelSpatial().setLocalRotation(new Quaternion().fromAngles(position, steering, 0));
			control.getWheel(0).getWheelSpatial().getWorldRotation().fromAngles(position, steering, 0);
			control.getWheel(1).getWheelSpatial().rotate(position, steering, 0);
			control.getWheel(2).getWheelSpatial().rotate(position, 0, 0);
			control.getWheel(3).getWheelSpatial().rotate(position, 0, 0);
			*/

	
		} catch(Exception e) {
			System.err.println("Could not set wheels of vehicle '" + vehicleID + "'!");
		}
	}
}
