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

package eu.opends.effects;

import java.util.ArrayList;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.BloomFilter.GlowMode;
import com.jme3.renderer.ViewPort;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;

import eu.opends.camera.CameraFactory;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class EffectCenter 
{
	private Simulator sim;
	private SnowParticleEmitter snowParticleEmitter;
	private RainParticleEmitter rainParticleEmitter;
	private boolean isSnowing;
	private boolean isRaining;
	private boolean isFog;
	private boolean isBloom;
	private boolean isShadow;

	
	public EffectCenter(Simulator sim) 
	{
		this.sim = sim;
		AssetManager assetManager = sim.getAssetManager();
		
		WeatherSettings weatherSettings = Simulator.getDrivingTask().getScenarioLoader().getWeatherSettings();
		isSnowing = (weatherSettings.getSnowingPercentage() > 0);
		isRaining = (weatherSettings.getRainingPercentage() > 0);
		isFog = (weatherSettings.getFogPercentage() > 0);
		isBloom = true;
		isShadow = true;
		
		if(isSnowing)
		{
			// init snow
			float percentage = Math.max(weatherSettings.getSnowingPercentage(),0);
			snowParticleEmitter = new SnowParticleEmitter(assetManager, percentage);
			sim.getSceneNode().attachChild(snowParticleEmitter);
		}
		
		if(isRaining)
		{
			// init snow
			float percentage = Math.max(weatherSettings.getRainingPercentage(),0);
			rainParticleEmitter = new RainParticleEmitter(assetManager, percentage);
			sim.getSceneNode().attachChild(rainParticleEmitter);
		}
		
		if(isFog || isBloom)
		{
		    FilterPostProcessor processor = new FilterPostProcessor(assetManager);
		    
	        int numSamples = sim.getContext().getSettings().getSamples();
	        if( numSamples > 0 )
	        	processor.setNumSamples(numSamples); 
	            
		    if(isFog)
		    {
		    	float percentage = Math.max(weatherSettings.getFogPercentage(),0);
			    FogFilter fog = new FogFilter();
		        fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
		        fog.setFogDistance(155);
		        fog.setFogDensity(2.0f * (percentage/100f));
		        processor.addFilter(fog);
		    }
		    
		    if(isBloom)
		    {
		    	// ensure any object is set to glow, e.g. car chassis:
		    	// chassis.getMaterial().setColor("GlowColor", ColorRGBA.Orange);
		    	
		    	BloomFilter bloom = new BloomFilter(GlowMode.Objects);
		    	processor.addFilter(bloom);
		    }
		    
	        sim.getViewPort().addProcessor(processor);
		}
		
		if(isShadow)
		{
			DirectionalLight sun = new DirectionalLight();
			Vector3f sunLightDirection = new Vector3f(0.5f, -1.0f, 0.5f); //TODO get from DT files
			sun.setDirection(sunLightDirection.normalizeLocal());
			
			ArrayList<ViewPort> viewPortList = CameraFactory.getViewPortList();
			
			int shadowMapSize = 4096;
			if(viewPortList.size() > 1)
				shadowMapSize = 1024;
	    	
			for(ViewPort viewPort : viewPortList)
			{
		    	DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, shadowMapSize, 1);
		    	dlsr.setLight(sun);
		    	dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
		    	viewPort.addProcessor(dlsr);
			}
			
			shadowMapSize = 1024;
	    	DirectionalLightShadowRenderer dlsrBack = new DirectionalLightShadowRenderer(assetManager, shadowMapSize, 1);
	    	dlsrBack.setLight(sun);
	    	CameraFactory.getBackViewPort().addProcessor(dlsrBack);
	    	
	    	DirectionalLightShadowRenderer dlsrLeft = new DirectionalLightShadowRenderer(assetManager, shadowMapSize, 1);
	    	dlsrLeft.setLight(sun);
	    	CameraFactory.getLeftBackViewPort().addProcessor(dlsrLeft);
	    	
	    	DirectionalLightShadowRenderer dlsrRight = new DirectionalLightShadowRenderer(assetManager, shadowMapSize, 1);
	    	dlsrRight.setLight(sun);
	    	CameraFactory.getRightBackViewPort().addProcessor(dlsrRight);
		}
	}

	
	public void update(float tpf)
	{
		if(isSnowing)
			snowParticleEmitter.setLocalTranslation(sim.getCar().getPosition());
		
		if(isRaining)
			rainParticleEmitter.setLocalTranslation(sim.getCar().getPosition());
	}

}
