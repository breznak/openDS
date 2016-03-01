/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2015 Rafael Math
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

package eu.opends.traffic;

/**
 * 
 * @author Rafael Math
 */
public class PedestrianData 
{
	private String name;
	private float mass;
	private String animationStand;
	private String animationWalk;
	private float scale;
	private String modelPath;
	private FollowBoxSettings followBoxSettings;
	
	
	public PedestrianData(String name, float mass, String animationStand, String animationWalk, 
			float scale, String modelPath, FollowBoxSettings followBoxSettings) 
	{
		this.name = name;
		this.mass = mass;
		this.animationStand = animationStand;
		this.animationWalk = animationWalk;
		this.scale = scale;
		this.modelPath = modelPath;
		this.followBoxSettings = followBoxSettings;
	}


	public String getName() {
		return name;
	}
	

	public float getMass() {
		return mass;
	}


	public String getAnimationStand() {
		return animationStand;
	}


	public String getAnimationWalk() {
		return animationWalk;
	}


	public float getScale() {
		return scale;
	}


	public String getModelPath() {
		return modelPath;
	}


	public FollowBoxSettings getFollowBoxSettings() {
		return followBoxSettings;
	}
	
	

}
