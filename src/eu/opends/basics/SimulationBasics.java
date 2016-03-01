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


package eu.opends.basics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jme3.bullet.BulletAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;

import eu.opends.camera.CameraFactory;
import eu.opends.cameraFlight.CameraFlight;
import eu.opends.drivingTask.DrivingTask;
import eu.opends.drivingTask.interaction.InteractionLoader;
import eu.opends.drivingTask.scenario.ScenarioLoader;
import eu.opends.drivingTask.scene.SceneLoader;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.environment.TrafficLightCenter;
import eu.opends.input.KeyBindingCenter;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;
import eu.opends.niftyGui.InstructionScreenGUI;
import eu.opends.niftyGui.KeyMappingGUI;
import eu.opends.niftyGui.ShutDownGUI;
import eu.opends.oculusRift.StereoCamAppState;
import eu.opends.tools.PropertiesLoader;
import eu.opends.tools.XMLLoader;
import eu.opends.trigger.TriggerAction;

/**
 * 
 * @author Rafael Math
 */
public class SimulationBasics extends SimpleApplication 
{
	protected static DrivingTask drivingTask;
	protected static SceneLoader sceneLoader;
	protected static ScenarioLoader scenarioLoader;
	protected static InteractionLoader interactionLoader;
	protected static SettingsLoader settingsLoader;
	protected static Map<String,List<TriggerAction>> triggerActionListMap = new HashMap<String,List<TriggerAction>>();
	protected BulletAppState bulletAppState;
	protected LightFactory lightFactory;
	protected CameraFactory cameraFactory;
	protected Node sceneNode;
	protected Node triggerNode;
	protected KeyMappingGUI keyMappingGUI;
	protected ShutDownGUI shutDownGUI;
	protected InstructionScreenGUI instructionScreenGUI;
	protected KeyBindingCenter keyBindingCenter;
	protected TrafficLightCenter trafficLightCenter;
	protected boolean debugEnabled = false;
	protected int numberOfScreens;
	protected StereoCamAppState stereoCamAppState;
	protected Spatial observer = new Node("observer");
	
	
	public KeyBindingCenter getKeyBindingCenter()
	{
		return keyBindingCenter;
	}
	
	public TrafficLightCenter getTrafficLightCenter() 
	{
		return trafficLightCenter;
	}
	
	public Node getSceneNode()
	{
		return sceneNode;
	}
	
	
	public Node getTriggerNode()
	{
		return triggerNode;
	}
	
	
    public BulletAppState getBulletAppState() 
    {
        return bulletAppState;
    }
    
    public Spatial getObserver() 
    {
        return observer;
    }
    
    public StereoCamAppState getStereoCamAppState() 
    {
        return stereoCamAppState;
    }
    
    public PhysicsSpace getPhysicsSpace() 
    {
        return bulletAppState.getPhysicsSpace();
    }
    
    
    public float getPhysicsSpeed() 
    {
        return bulletAppState.getSpeed();
    }
    
    
    public boolean isPause() 
    {
        return !bulletAppState.isEnabled();
    }
    
    
    public void setPause(boolean pause) 
    {
    	if(this instanceof Simulator)
    	{
    		CameraFlight camFlight = ((Simulator)this).getCameraFlight();
    		if(camFlight != null && !camFlight.isTerminated())
    		{
    			camFlight.play(); // must be set
    		
    			if(pause)				
    				camFlight.pause();
    		}
    	}
        bulletAppState.setEnabled(!pause);
    }
	
	
	public static DrivingTask getDrivingTask()
	{
		return drivingTask;
	}
	
	
	public static SettingsLoader getSettingsLoader()
	{
		return settingsLoader;
	}

	
	public static Map<String,List<TriggerAction>> getTriggerActionListMap() 
	{
		return triggerActionListMap;
	}

	
	public AppSettings getSettings() 
	{
		return settings;
	}
	
	
	public KeyMappingGUI getKeyMappingGUI() 
	{
		return keyMappingGUI;
	}
	
	
	public ShutDownGUI getShutDownGUI() 
	{
		return shutDownGUI;
	}
	
	
	public InstructionScreenGUI getInstructionScreenGUI() 
	{
		return instructionScreenGUI;
	}
	

	public CameraFactory getCameraFactory() 
	{
		return cameraFactory;
	}
	
	
	public int getNumberOfScreens()
	{
		return numberOfScreens;
	}
	
	
	public void toggleDebugMode()
	{
		debugEnabled = !debugEnabled;
		bulletAppState.setDebugEnabled(debugEnabled);
	}
	

    @Override
    public void simpleInitApp() 
    {    	
    	lookupNumberOfScreens();
    	
    	// OpenDS-Rift - init app state
    	if(Simulator.oculusRiftAttached)
    	{
        	stereoCamAppState = new StereoCamAppState();
        	stateManager.attach(stereoCamAppState);
    	}
    	
    	// init physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
		
        // register loader for *.properties-files
        assetManager.registerLoader(PropertiesLoader.class, "properties");
        assetManager.registerLoader(XMLLoader.class, "xml");
        
		sceneNode = new Node("sceneNode");
		//sceneNode.setShadowMode(ShadowMode.CastAndReceive);
		rootNode.attachChild(sceneNode);
		
		triggerNode = new Node("triggerNode");
		sceneNode.attachChild(triggerNode);
    	
        // apply shadow casting       
        //if (settings.getRenderer().startsWith("LWJGL")) 
        //	sceneNode.setShadowMode(ShadowMode.Receive);
        
        // setup light settings
        lightFactory = new LightFactory(this);
        lightFactory.initLight();
        
        // build sky
        createSkyBox();
        
        keyMappingGUI = new KeyMappingGUI(this);
        shutDownGUI = new ShutDownGUI(this);
        instructionScreenGUI = new InstructionScreenGUI(this);
    }


	private void createSkyBox()
	{
		String skyModelPath = Simulator.getDrivingTask().getSceneLoader().getSkyTexture(SimulationDefaults.skyTexture);
        assetManager.registerLocator("assets", FileLocator.class);
        Spatial sky;
        try{
        	sky = SkyFactory.createSky(assetManager, skyModelPath, false);
        } catch (AssetNotFoundException e) {
        	System.err.println("SimulationBasics: Could not find sky texture '" + skyModelPath + 
        			"'. Using default ('" + SimulationDefaults.skyTexture + "').");
        	sky = SkyFactory.createSky(assetManager, SimulationDefaults.skyTexture, false);
        }
        sky.setShadowMode(ShadowMode.Off);
        sceneNode.attachChild(sky);
        
        /*
        Texture west = assetManager.loadTexture("Textures/Sky/Clouds/west.png");
        Texture east = assetManager.loadTexture("Textures/Sky/Clouds/east.png");
        Texture north = assetManager.loadTexture("Textures/Sky/Clouds/north.png");
        Texture south = assetManager.loadTexture("Textures/Sky/Clouds/south.png");
        Texture up = assetManager.loadTexture("Textures/Sky/Clouds/up.png");
        Texture down = assetManager.loadTexture("Textures/Sky/Clouds/down.png");

        // vector used to flip textures (as textures will be applied from the outside of the box)
        Vector3f normalScale = new Vector3f(-1, 1, 1);
        Spatial skySpatial = SkyFactory.createSky(assetManager, west, east, north, south, up, down, normalScale);
        skySpatial.setShadowMode(ShadowMode.Off);
        rootNode.attachChild(skySpatial);
        */
	}
    

    @Override
    public void simpleUpdate(float tpf) 
    {
    	
    }
    
    
    private void lookupNumberOfScreens()
    {
		numberOfScreens = Simulator.getSettingsLoader().getSetting(Setting.General_numberOfScreens, -1);
		
		if(numberOfScreens < 1)
		{
			int width = getSettings().getWidth();
	    	int height = getSettings().getHeight();
	    	
			if((width == 5040 && height == 1050) || (width == 4200 && height == 1050))
				numberOfScreens = 3;
			else
				numberOfScreens = 1;
		}
    }


}
