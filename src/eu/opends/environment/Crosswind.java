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

import com.jme3.math.FastMath;

public class Crosswind 
{
	private long startTime;
	private String direction;
	private float force;
	private int duration;
	

	public Crosswind(String direction, float force, int duration)
	{
		startTime = System.currentTimeMillis();
		this.direction = direction;
		this.force = Math.max(Math.min(force, 1.0f), 0.0f);
		this.duration = Math.abs(duration);
	}

	
	// called every frame
	public float getCurrentSteeringInfluence() 
	{
		if(duration == 0)
			return 0;
		
		long currentTime = System.currentTimeMillis();
		int timeElapsed = (int) (currentTime - startTime);
		float timeElapsedPercent = (float) (timeElapsed/(float)duration);
		timeElapsedPercent =  Math.max(Math.min(timeElapsedPercent, 1.0f), 0.0f);
		
		// factor of force (linear growth)
		//   0% --> 0.0 * force
		//  25% --> 0.5 * force
		//  50% --> 1.0 * force
		//  75% --> 0.5 * force
		// 100% --> 0.0 * force
		float factor = 1 - (2*FastMath.abs(timeElapsedPercent - 0.5f));
		
		// factor of force (sinus-shaped growth)
		//float factor = FastMath.sin(percentage*FastMath.PI);

		if(direction.equalsIgnoreCase("left"))
			return -factor * force;
				
		else
			return factor * force;
	}


}
