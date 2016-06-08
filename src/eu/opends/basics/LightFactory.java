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


import java.util.List;

import com.jme3.light.Light;

/**
 * 
 * @author Rafael Math
 */
public class LightFactory 
{
	private SimulationBasics sim;
	
	
	public LightFactory(SimulationBasics sim)
	{
		this.sim = sim;
	}
	
	
	public void initLight() 
	{
		List<Light> lightList = SimulationBasics.getDrivingTask().getSceneLoader().getLightList();
		
		for(Light light : lightList)
			sim.getSceneNode().addLight(light);     
		

		// LIGHT EXAMPLES
/*
        DirectionalLight dlA = new DirectionalLight();
        dlA.setDirection(new Vector3f(0.5f, -0.5f, 0.5f).normalizeLocal());
        dlA.setColor(ColorRGBA.White.mult(0.7f));
        sim.getSceneNode().addLight(dlA);
        
        DirectionalLight dlB = new DirectionalLight();
        dlB.setDirection(new Vector3f(-0.5f, -0.5f, 0.5f).normalizeLocal());
        dlB.setColor(ColorRGBA.White.mult(0.7f));
        sim.getSceneNode().addLight(dlB);
        
        DirectionalLight dlC = new DirectionalLight();
        dlC.setDirection(new Vector3f(0.5f, -0.5f, -0.5f).normalizeLocal());
        dlC.setColor(ColorRGBA.White.mult(0.7f));
        sim.getSceneNode().addLight(dlC);
        
        DirectionalLight dlD = new DirectionalLight();
        dlD.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
        dlD.setColor(ColorRGBA.White.mult(0.7f));
        sim.getSceneNode().addLight(dlD);
*/       
        
/*
		Vector3f sunPosition = new Vector3f(-1200, 400, 0);
        PointLight pointLight01 = new PointLight();
		pointLight01.setPosition(sunPosition);
		pointLight01.setRadius(10000);
		sim.getSceneNode().addLight(pointLight01);
*/
		
/*
        AmbientLight a = new AmbientLight();
        a.setColor(ColorRGBA.White.mult(1.5f));
        sim.getSceneNode().addLight(a);
*/  
		
/*
        SpotLight spotLight = new SpotLight();
        spotLight.setPosition(new Vector3f(0,50,0));
        spotLight.setColor(ColorRGBA.White.mult(2));
        spotLight.setDirection(new Vector3f(0,-1,0));
        spotLight.setSpotRange(1000);
        spotLight.setSpotInnerAngle(5*FastMath.DEG_TO_RAD);
        spotLight.setSpotOuterAngle(10*FastMath.DEG_TO_RAD);        
        sim.getSceneNode().addLight(spotLight);
*/
	}

}
