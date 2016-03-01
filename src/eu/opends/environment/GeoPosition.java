/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2014 Rafael Math
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

import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;

import eu.opends.basics.SimulationBasics;


/**
 * This class contains methods to convert model coordinates to geo coordinates and 
 * vice versa. The given Matrices are specific to the "Stadtmitte am Fluss" model.
 * 
 * @author Rafael Math
 */
public class GeoPosition 
{	
	/**
	 * Conversion matrix to convert model coordinates to geo coordinates in the 
	 * "Stadtmitte am Fluss" model.
	 * 
	 * @return
	 * 			conversion matrix model --> geo
	 */
	private static Matrix4f getModelToGeoMatrix()
	{
		// load matrix from scenario.xml
		Matrix4f modelToGeoMatrix = SimulationBasics.getDrivingTask().getScenarioLoader().getModelToGeoMatrix();
		
		if(modelToGeoMatrix == null)
		{
			// default conversion matrix
			modelToGeoMatrix = new Matrix4f();
			modelToGeoMatrix.m00 = (float) -1.78355088340735E-07;
			modelToGeoMatrix.m01 = (float) -5.43081511229155E-06;
			modelToGeoMatrix.m02 = (float) 0;
			modelToGeoMatrix.m03 = (float) 49.2358655481218;
			modelToGeoMatrix.m10 = (float) 9.00234704847944E-06;
			modelToGeoMatrix.m11 = (float) -3.80856478467656E-07;
			modelToGeoMatrix.m12 = (float) 0;
			modelToGeoMatrix.m13 = (float) 7.0048602281113;
			modelToGeoMatrix.m20 = (float) 0;
			modelToGeoMatrix.m21 = (float) 0;
			modelToGeoMatrix.m22 = (float) 1;
			modelToGeoMatrix.m23 = (float) 0;
			modelToGeoMatrix.m30 = (float) 0;
			modelToGeoMatrix.m31 = (float) 0;
			modelToGeoMatrix.m32 = (float) 0;
			modelToGeoMatrix.m33 = (float) 1;
		}

		return modelToGeoMatrix;
	}
	
	
	/**
	 * Conversion matrix to convert geo coordinates to model coordinates in the 
	 * "Stadtmitte am Fluss" model.
	 * 
	 * @return
	 * 			conversion matrix geo --> model
	 */
	private static Matrix4f getGeoToModelMatrix()
	{
		// load matrix from scenario.xml
		Matrix4f geoToModelMatrix = SimulationBasics.getDrivingTask().getScenarioLoader().getGeoToModelMatrix();
		
		if(geoToModelMatrix == null)
		{
			// default conversion matrix
			geoToModelMatrix = new Matrix4f();
			geoToModelMatrix.m00 = (float) -7779.24751811492;
			geoToModelMatrix.m01 = (float) 110928.019797943;
			geoToModelMatrix.m02 = (float) 0;
			geoToModelMatrix.m03 = (float) -394017.289198288;
			geoToModelMatrix.m10 = (float) -183878.941001237;
			geoToModelMatrix.m11 = (float) -3643.0216019961;
			geoToModelMatrix.m12 = (float) 0;
			geoToModelMatrix.m13 = (float) 9078957.67339789;
			geoToModelMatrix.m20 = (float) 0;
			geoToModelMatrix.m21 = (float) 0;
			geoToModelMatrix.m22 = (float) 1;
			geoToModelMatrix.m23 = (float) 0;
			geoToModelMatrix.m30 = (float) 0;
			geoToModelMatrix.m31 = (float) 0;
			geoToModelMatrix.m32 = (float) 0;
			geoToModelMatrix.m33 = (float) 1;
		}
		
		return geoToModelMatrix;
	}
	
	
	/**
	 * Transforms a position from model space to the corresponding position 
	 * in geo space.
	 * 
	 * @param modelPosition
	 * 			Position as vector (x,y,z) in the "Stadtmitte am Fluss" model.
	 * 
	 * @return
	 * 			Position as vector (latitude,longitude,altitude) in the real world.
	 */
	public static Vector3f modelToGeo(Vector3f modelPosition)
	{
		// model coordinates to convert
		Matrix4f modelCoordinates = new Matrix4f();
		modelCoordinates.m00 = modelPosition.getX(); // latitude (e.g. -964.2952f)
		modelCoordinates.m10 = modelPosition.getZ(); // longitude (e.g. -28.074038f)
		modelCoordinates.m20 = modelPosition.getY(); // altitude (e.g. 20f)
		modelCoordinates.m30 = 1f;
		
		// compute geo coordinates
		Matrix4f geoCoordinates = getModelToGeoMatrix().mult(modelCoordinates);
		
		// geo coordinates
		float latitude = geoCoordinates.m00;    // latitude (e.g. 49.238415f)
		float longitude = geoCoordinates.m10;   // longitude (e.g. 7.007393f)
		float altitude = geoCoordinates.m20;    // altitude (e.g. 213.04912f)
		
		//System.out.println(geoCoordinates);
		
		return new Vector3f(latitude,longitude,altitude); 
	}
	
	
	/**
	 * Transforms a position from geo space to the corresponding position 
	 * in model space.
	 * 
	 * @param geoPosition
	 * 			Position as vector (latitude,longitude,altitude) in the real world.
	 * 
	 * @return
	 * 			Position as vector (x,y,z) in the "Stadtmitte am Fluss" model.
	 */
	public static Vector3f geoToModel(Vector3f geoPosition)
	{
		// geo coordinates to convert
		Matrix4f geoCoordinates = new Matrix4f();
		geoCoordinates.m00 = geoPosition.getX(); // latitude (e.g. 49.238415f)
		geoCoordinates.m10 = geoPosition.getY(); // longitude (e.g. 7.007393f)
		geoCoordinates.m20 = geoPosition.getZ(); // altitude (e.g. 213.04912f)
		geoCoordinates.m30 = 1f;
		
		// compute model coordinates
		Matrix4f modelCoordinates = getGeoToModelMatrix().mult(geoCoordinates);
		
		// model coordinates
		float latitude = modelCoordinates.m00;    // latitude (e.g. -964.2952f)
		float longitude = modelCoordinates.m10;   // longitude (e.g. -28.074038f)
		float altitude = modelCoordinates.m20;    // altitude (e.g. 20f)
		
		//System.out.println(modelCoordinates);
		
		return new Vector3f(latitude,altitude,longitude); 
	}
	
}
