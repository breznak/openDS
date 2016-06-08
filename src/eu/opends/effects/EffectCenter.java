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
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;

import eu.opends.camera.CameraFactory;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class EffectCenter 
{
	private static float snowingPercentage = 0;
	private static float rainingPercentage = 0;
	private static float fogPercentage = 0;
	private static boolean snowingPercentageHasChanged = false;
	private static boolean rainingPercentageHasChanged = false;
	private static boolean fogPercentageHasChanged = false;
	
	private Simulator sim;
	private SnowParticleEmitter snowParticleEmitter;
	private RainParticleEmitter rainParticleEmitter;
	private boolean isSnowing;
	private boolean isRaining;
	private boolean isFog;
	private boolean isBloom;
	private boolean isShadow;
	private ArrayList<FogFilter> fogFilterList = new ArrayList<FogFilter>();

	
	public EffectCenter(Simulator sim) 
	{
		this.sim = sim;
		AssetManager assetManager = sim.getAssetManager();
		
		DirectionalLight sun = new DirectionalLight();
		Vector3f sunLightDirection = new Vector3f(-0.2f, -0.9f, 0.2f); // TODO make adjustable
		sun.setDirection(sunLightDirection.normalizeLocal());
		
		WeatherSettings weatherSettings = Simulator.getDrivingTask().getScenarioLoader().getWeatherSettings();
		snowingPercentage = Math.max(weatherSettings.getSnowingPercentage(),-1);  // use -1 to suppress construction of SnowParticleEmitter
		rainingPercentage = Math.max(weatherSettings.getRainingPercentage(),-1);  // use -1 to suppress construction of RainParticleEmitter
		fogPercentage = Math.max(weatherSettings.getFogPercentage(),-1);          // use -1 to suppress construction of FogFilter
		isSnowing = (snowingPercentage >= 0);
		isRaining = (rainingPercentage >= 0);
		isFog = (fogPercentage >= 0);
		
		// switch off bloom filter when Oculus Rift is used
		isBloom = Simulator.oculusRiftAttached ? false : Simulator.getDrivingTask().getScenarioLoader().isBloomFilter(); 
		isShadow = Simulator.getDrivingTask().getScenarioLoader().isShadowFilter();
		
		if(isSnowing)
		{
			// init snow
			snowParticleEmitter = new SnowParticleEmitter(assetManager, snowingPercentage);
			sim.getSceneNode().attachChild(snowParticleEmitter);
		}
		
		if(isRaining)
		{
			// init rain
			rainParticleEmitter = new RainParticleEmitter(assetManager, rainingPercentage);
			sim.getSceneNode().attachChild(rainParticleEmitter);
		}
		
		if(isFog || isBloom || isShadow)
		{
			for(ViewPort viewPort : CameraFactory.getViewPortList())
			{
			    FilterPostProcessor processor = new FilterPostProcessor(assetManager);
			    
		        int numSamples = sim.getContext().getSettings().getSamples();
		        if( numSamples > 0 )
		        	processor.setNumSamples(numSamples); 
		            
			    if(isFog)
			    {
				    FogFilter fogFilter = new FogFilter();
			        fogFilter.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
			        fogFilter.setFogDistance(50); // TODO make adjustable
			        fogFilter.setFogDensity(4.0f * (fogPercentage/100f));
			        fogFilterList.add(fogFilter);
			        processor.addFilter(fogFilter);
			    }
			    
			    if(isBloom)
			    {
			    	// usage e.g. car chassis:
			    	// chassis.getMaterial().setColor("GlowColor", ColorRGBA.Orange);
			    	
			    	BloomFilter bloom = new BloomFilter(GlowMode.Objects);
			    	processor.addFilter(bloom);
			    }
			    
			    if(isShadow)
			    {					
			    	DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 1024, 3); // TODO make adjustable
					dlsf.setLight(sun);
					dlsf.setLambda(1f);
					dlsf.setShadowIntensity(0.3f); // TODO make adjustable
					dlsf.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
					dlsf.setEnabled(true);

					processor.addFilter(dlsf);
			    }
			    
			    viewPort.addProcessor(processor);
			}	
		}

		/*
		// DirectionalLightShadowFilter looks more realistic!!! 
		
		if(isShadow)
		{	
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
		*/
	}


	public void update(float tpf)
	{
		if(isSnowing)
		{
			snowParticleEmitter.setLocalTranslation(sim.getCar().getPosition());
			
			if(snowingPercentageHasChanged)
			{
				snowParticleEmitter.setPercentage(snowingPercentage);
				System.out.println("snowing intensity: " + snowingPercentage);
				snowingPercentageHasChanged = false;
			}
		}
		
		if(isRaining)
		{
			rainParticleEmitter.setLocalTranslation(sim.getCar().getPosition());
			
			if(rainingPercentageHasChanged)
			{
				rainParticleEmitter.setPercentage(rainingPercentage);
				System.out.println("raining intensity: " + rainingPercentage);
				rainingPercentageHasChanged = false;
			}
		}
		
		if(isFog)
		{
			if(fogPercentageHasChanged)
			{
				for(FogFilter fogFilter : fogFilterList)
					fogFilter.setFogDensity(4.0f * (fogPercentage/100f));
				System.out.println("fog intensity: " + fogPercentage);
				fogPercentageHasChanged = false;
			}
		}
	}

	
	public static float getSnowingPercentage() 
	{
		return snowingPercentage;
	}


	public static void setSnowingPercentage(float percentage) 
	{
		snowingPercentage = Math.max(percentage, 0);
		snowingPercentageHasChanged = true;
	}
	
	
	public static float getRainingPercentage() 
	{
		return rainingPercentage;
	}


	public static void setRainingPercentage(float percentage) 
	{
		rainingPercentage = Math.max(percentage, 0);
		rainingPercentageHasChanged = true;
	}

	
	public static float getFogPercentage() 
	{
		return fogPercentage;
	}


	public static void setFogPercentage(float percentage) 
	{
		fogPercentage = Math.max(percentage, 0);
		fogPercentageHasChanged = true;
	}
}
