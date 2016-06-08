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

import Jama.Matrix;

import com.jme3.math.Vector3f;

import eu.opends.basics.SimulationBasics;
import eu.opends.tools.Vector3d;


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
	private static Matrix getModelToGeoMatrix()
	{
		// load matrix from scenario.xml
		Matrix modelToGeoMatrix = SimulationBasics.getDrivingTask().getScenarioLoader().getModelToGeoMatrix();

		if(modelToGeoMatrix == null)
		{
			// default conversion matrix
			modelToGeoMatrix = new Matrix(4,4);
			modelToGeoMatrix.set(0, 0, -1.78355088340735E-07);
			modelToGeoMatrix.set(0, 1, -5.43081511229155E-06);
			modelToGeoMatrix.set(0, 2, 0);
			modelToGeoMatrix.set(0, 3, 49.2358655481218);
			modelToGeoMatrix.set(1, 0, 9.00234704847944E-06);
			modelToGeoMatrix.set(1, 1, -3.80856478467656E-07);
			modelToGeoMatrix.set(1, 2, 0);
			modelToGeoMatrix.set(1, 3, 7.0048602281113);
			modelToGeoMatrix.set(2, 0, 0);
			modelToGeoMatrix.set(2, 1, 0);
			modelToGeoMatrix.set(2, 2, 1);
			modelToGeoMatrix.set(2, 3, 0);
			modelToGeoMatrix.set(3, 0, 0);
			modelToGeoMatrix.set(3, 1, 0);
			modelToGeoMatrix.set(3, 2, 0);
			modelToGeoMatrix.set(3, 3, 1);
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
	private static Matrix getGeoToModelMatrix()
	{
		// load matrix from scenario.xml
		Matrix geoToModelMatrix = SimulationBasics.getDrivingTask().getScenarioLoader().getGeoToModelMatrix();

		if(geoToModelMatrix == null)
		{
			// default conversion matrix
			geoToModelMatrix = new Matrix(4,4);
			geoToModelMatrix.set(0, 0, -7779.24751811492);
			geoToModelMatrix.set(0, 1, 110928.019797943);
			geoToModelMatrix.set(0, 2, 0);
			geoToModelMatrix.set(0, 3, -394017.289198288);
			geoToModelMatrix.set(1, 0, -183878.941001237);
			geoToModelMatrix.set(1, 1, -3643.0216019961);
			geoToModelMatrix.set(1, 2, 0);
			geoToModelMatrix.set(1, 3, 9078957.67339789);
			geoToModelMatrix.set(2, 0, 0);
			geoToModelMatrix.set(2, 1, 0);
			geoToModelMatrix.set(2, 2, 1);
			geoToModelMatrix.set(2, 3, 0);
			geoToModelMatrix.set(3, 0, 0);
			geoToModelMatrix.set(3, 1, 0);
			geoToModelMatrix.set(3, 2, 0);
			geoToModelMatrix.set(3, 3, 1);
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
	public static Vector3d modelToGeo(Vector3f modelPosition)
	{
		// model coordinates to convert
		Matrix modelCoordinates = new Matrix(4,1);
		modelCoordinates.set(0, 0, modelPosition.getX()); // latitude (e.g. -964.2952f)
		modelCoordinates.set(1, 0, modelPosition.getZ()); // longitude (e.g. -28.074038f)
		modelCoordinates.set(2, 0, modelPosition.getY()); // altitude (e.g. 20f)
		modelCoordinates.set(3, 0, 1);
		
		// compute geo coordinates
		Matrix geoCoordinates = getModelToGeoMatrix().times(modelCoordinates);
		
		// geo coordinates
		double latitude = geoCoordinates.get(0, 0);    // latitude (e.g. 49.238415f)
		double longitude = geoCoordinates.get(1, 0);   // longitude (e.g. 7.007393f)
		double altitude = geoCoordinates.get(2, 0);    // altitude (e.g. 213.04912f)
		
		//System.out.println(geoCoordinates);
		
		return new Vector3d(latitude,longitude,altitude);
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
	public static Vector3f geoToModel(Vector3d geoPosition)
	{
		// geo coordinates to convert
		Matrix geoCoordinates = new Matrix(4,1);
		geoCoordinates.set(0, 0, geoPosition.getX()); // latitude (e.g. 49.238415f)
		geoCoordinates.set(1, 0, geoPosition.getY()); // longitude (e.g. 7.007393f)
		geoCoordinates.set(2, 0, geoPosition.getZ()); // altitude (e.g. 213.04912f)
		geoCoordinates.set(3, 0, 1);
		
		// compute model coordinates
		Matrix modelCoordinates = getGeoToModelMatrix().times(geoCoordinates);
		
		// model coordinates
		float latitude = (float) modelCoordinates.get(0, 0);    // latitude (e.g. -964.2952f)
		float longitude = (float) modelCoordinates.get(1, 0);   // longitude (e.g. -28.074038f)
		float altitude = (float) modelCoordinates.get(2, 0);    // altitude (e.g. 20f)
		
		//System.out.println(modelCoordinates);
		
		return new Vector3f(latitude,altitude,longitude); 
	}
	
}
