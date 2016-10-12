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

package eu.opends.visualization;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

import eu.opends.environment.TrafficLight;


/**
 * This class represents data used if the simulator is connected to Lightning. It contains 
 * methods to compute camera (and traffic light) position and orientation in the Lightning 
 * coordinate system.
 * 
 * @author Rafael Math
 */
public class LightningData 
{
	/**
	 * Computes the position of the given camera in the Lightning coordinate system and
	 * multiplies x-, y- and z-coordinate with the given scaling factor.
	 * 
	 * @param camera
	 * 			Camera to get data from.
	 * 
	 * @param scalingFactor
	 * 			Factor the position data is multiplied with.
	 * 
	 * @return
	 * 			Position string containing scaled x-, y- and z-coordinates in the Lightning 
	 * 			coordinate system.
	 */
	public static String getCameraPosition(Camera camera, float scalingFactor)
	{
		Vector3f position = transformVector(camera.getLocation(), scalingFactor);
		
		String positionString = position.getX() + " " + position.getY() + " " + position.getZ();
		
		return positionString;
	}
	
	
	/**
	 * Computes the orientation of the camera as Euler angles and returns a string 
	 * of the following structure: "heading attitude bank".
	 * 
	 * @param camera
	 * 			Camera to get data from.
	 * 
	 * @return
	 * 			Orientation string containing heading, attitude and bank as Euler angles.
	 */
	public static String getCameraOrientation(Camera camera)
	{
		// get 3 perpendicular vector describing the orientation of the camera
		Vector3f direction = transformVector(camera.getDirection(),1f).normalize();
		Vector3f up = transformVector(camera.getUp(),1f).normalize();
		Vector3f left = transformVector(camera.getLeft(),1f).normalize();			
		
		Matrix3f rotationMatrix = new Matrix3f();

		// build a rotation matrix by columns (column1, column2, column3)
		rotationMatrix.fromAxes(direction, left, up);
		
		// transform rotation matrix to euler angles
		float[] eulerAngles = computeEulerAngles(rotationMatrix);
		
		// convert radian measure to degree 
		float heading = rad2deg(eulerAngles[0]);
		float attitude = rad2deg(eulerAngles[1]);
		float bank = rad2deg(eulerAngles[2]);

		// specific adjustments
		heading = heading - 90;
		attitude = - attitude;
		
		//System.out.println("head: "+heading+"  attitude: "+attitude+"  bank: "+bank);
		String orientationString = heading + " " + attitude + " " + bank;

		return orientationString;
	}
	
	
	/**
	 * Prints traffic light information, such as name, position and orientation.
	 * This method can be used to place traffic lights in Lightning.
	 * 
	 * @param trafficLight
	 * 			Traffic light object to get information about.
	 * 
	 * @param scalingFactor
	 * 			Factor the position data is multiplied with.
	 */
	public static void printTrafficLightInfos(TrafficLight trafficLight, float scalingFactor) 
	{
		// transform quaternion to euler angles
		float[] eulerAngles = trafficLight.getRotation().toAngles(null);
		
		// convert radian measure to degree 
		float heading = (360 + rad2deg(eulerAngles[1])) % 360;
		
		// assuming there is no attitude and bank
		float attitude = 0;
		float bank = 0;

		System.out.println(trafficLight.getName()
				+ ", pos: " + transformVector(trafficLight.getWorldPosition(), scalingFactor) 
				+ ", orientation: (" + heading + ", " + attitude + ", " + bank + ")");
	}
	
	
	/**
	 * Transforms the input vector from the jme coordinate system to the 
	 * lightning coordinate system by (x,y,z) --> (x,z,-y).
	 * 
	 * @param origVector
	 * 			Vector to be transformed in jme coordinates
	 * 
	 * @param scalingFactor
	 * 			Factor used to scale the model
	 * 
	 * @return
	 * 			New vector in lightning coordinates
	 */
	private static Vector3f transformVector(Vector3f origVector, float scalingFactor)
	{
		float internalScalingFactor = scalingFactor;
		
		float newX = -origVector.getZ() * internalScalingFactor;
		float newY = -origVector.getX() * internalScalingFactor;
		float newZ =  origVector.getY() * internalScalingFactor;
		
		return new Vector3f(newX,newY,newZ);
	}
	
	
	/**
	 * Converts radian measure to degree
	 * 
	 * @param radian
	 * 			angle in radians
	 * 
	 * @return
	 * 			angle in degree
	 */
	private static float rad2deg(float radian) 
	{
		return radian * 180 / FastMath.PI;
	}


	/**
	 * Computes the Euler angles from a rotation matrix according to G.G. Slabaugh
	 * http://www.gregslabaugh.name/publications/euler.pdf
	 * 
	 * @param matrix
	 * 			Rotation matrix to compute the Euler angles from
	 * 
	 * @return
	 * 			Euler angles as float array [heading, attitude, bank] in radian 
	 * 			measurement.
	 */
	private static float[] computeEulerAngles(Matrix3f matrix) 
	{
		float[] eulerAngles = new float[3];
		float attitude;
		float bank;
		float heading;
		
		if((matrix.get(2,0) != 1) && ((matrix.get(2,0) != -1)))
		{
			attitude = - FastMath.asin(matrix.get(2,0));
			float cosAttitude = FastMath.cos(attitude);
			bank = FastMath.atan2(matrix.get(2,1)/cosAttitude, matrix.get(2,2)/cosAttitude);
			heading = FastMath.atan2(matrix.get(1,0)/cosAttitude, matrix.get(0,0)/cosAttitude);
		}
		else
		{
			heading = 0;
			if(matrix.get(2,0) == -1)
			{
				attitude = FastMath.HALF_PI;
				bank = heading + FastMath.atan2(matrix.get(0,1), matrix.get(0,2));
			}
			else
			{
				attitude = -FastMath.HALF_PI;
				bank = -heading + FastMath.atan2(-matrix.get(0,1), -matrix.get(0,2));
			}
		}
		
		eulerAngles[0] = heading;
		eulerAngles[1] = attitude;
		eulerAngles[2] = bank;
		
		return eulerAngles;
	}
	

}
