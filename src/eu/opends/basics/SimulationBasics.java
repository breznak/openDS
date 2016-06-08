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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jme3.bullet.BulletAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Cylinder;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;

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
	protected static Map<String,List<TriggerAction>> remoteTriggerActionListMap = new HashMap<String,List<TriggerAction>>();
	protected static Map<String,List<TriggerAction>> cameraWaypointTriggerActionListMap = new HashMap<String,List<TriggerAction>>();
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
	protected Node coordinateSystem = new Node("coordinateSystem");
	
	
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
	
	
	public Node getCoordinateSystem()
	{
		return coordinateSystem;
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

	
	public static Map<String,List<TriggerAction>> getRemoteTriggerActionListMap() 
	{
		return remoteTriggerActionListMap;
	}
	
	
	public static Map<String,List<TriggerAction>> getCameraWaypointTriggerActionListMap() 
	{
		return cameraWaypointTriggerActionListMap;
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
        
        createCoordinateSystem();
    }

    
	private void createCoordinateSystem()
	{		
		Node xAxisCylinder = createCylinder("x-axis", new ColorRGBA(1,0,0,0));		
		xAxisCylinder.setLocalRotation((new Quaternion()).fromAngles(0, -90*FastMath.DEG_TO_RAD, 0));
		coordinateSystem.attachChild(xAxisCylinder);
		
		Node xAxisCone = createCone("x-cone", new ColorRGBA(1,0,0,1));		
		xAxisCone.setLocalTranslation(10,0,0);
		xAxisCone.setLocalRotation((new Quaternion()).fromAngles(0, -90*FastMath.DEG_TO_RAD, 0));
		coordinateSystem.attachChild(xAxisCone);
		
		
		Node yAxisCylinder = createCylinder("y-axis", new ColorRGBA(0,1,0,1));
        yAxisCylinder.setLocalRotation((new Quaternion()).fromAngles(90*FastMath.DEG_TO_RAD, 0, 0));
        coordinateSystem.attachChild(yAxisCylinder);
		
		Node yAxisCone = createCone("y-cone", new ColorRGBA(0,1,0,1));
		yAxisCone.setLocalTranslation(0,10,0);
		yAxisCone.setLocalRotation((new Quaternion()).fromAngles(90*FastMath.DEG_TO_RAD, 0, 0));
		coordinateSystem.attachChild(yAxisCone);
		

		Node zAxisCylinder = createCylinder("z-axis", new ColorRGBA(0,0,1,1));
		zAxisCylinder.setLocalRotation((new Quaternion()).fromAngles(0, 0, 0));
		coordinateSystem.attachChild(zAxisCylinder);

		Node zAxisCone = createCone("z-cone", new ColorRGBA(0,0,1,1));
		zAxisCone.setLocalTranslation(0,0,10);
		zAxisCone.setLocalRotation((new Quaternion()).fromAngles(0, 180*FastMath.DEG_TO_RAD, 0));
		coordinateSystem.attachChild(zAxisCone);
		
		coordinateSystem.setCullHint(CullHint.Always);
		
		sceneNode.attachChild(coordinateSystem);
	}


	private Node createCone(String name, ColorRGBA color)
	{
		int axisSamples = 5;		
		int radialSamples = 20;		
		float radius = 2;
		float radius2 = 0;
		float height = 5;
		Boolean closed = true;
				
		// create new cylinder
		Cylinder cylinder = new Cylinder(axisSamples, radialSamples, radius, radius2, height, closed, false);
		Geometry geometry = new Geometry(name + "_cylinder", cylinder);
		
		String matDefinition = "Common/MatDefs/Misc/Unshaded.j3md";
		Material material = new Material(this.getAssetManager(), matDefinition);
		material.setColor("Color", color);
		geometry.setMaterial(material);
		
		Node node = new Node(name);
		node.attachChild(geometry);
		return node;
	}


	private Node createCylinder(String name, ColorRGBA color)
	{
		int axisSamples = 5;		
		int radialSamples = 20;		
		float radius = 0.5f;
		float height = 10000;
		Boolean closed = true;
				
		// create new cylinder
		Cylinder cylinder = new Cylinder(axisSamples, radialSamples, radius, height, closed);
		Geometry geometry = new Geometry(name + "_cylinder", cylinder);
		
		String matDefinition = "Common/MatDefs/Misc/Unshaded.j3md";
		Material material = new Material(this.getAssetManager(), matDefinition);
		material.setColor("Color", color);
		geometry.setMaterial(material);
		
		Node node = new Node(name);
		node.attachChild(geometry);
		return node;
	}
	

	private void createSkyBox()
	{
		String skyModelPath = Simulator.getDrivingTask().getSceneLoader().getSkyTexture(SimulationDefaults.skyTexture);
		assetManager.registerLocator("assets", FileLocator.class);
		
		Spatial sky;
		
		try{
			
	        if(skyModelPath.toLowerCase().endsWith(".dds"))
	        {
		        sky = SkyFactory.createSky(assetManager, skyModelPath, EnvMapType.CubeMap);
	        }
	        else if(skyModelPath.toLowerCase().endsWith(".hdr"))
	        {
	        	Quaternion rotation = new Quaternion();
	            rotation.fromAngles(270*FastMath.DEG_TO_RAD, 0, 0);
	            
	            sky = SkyFactory.createSky(assetManager, skyModelPath, EnvMapType.SphereMap);
	            sky.setLocalRotation(rotation);
	        }
	        else
	        {
	        	sky = SkyPropertiesReader.getSettings(assetManager, skyModelPath);
	        }
	        
	        
        } catch (AssetNotFoundException e) {
        	
        	System.err.println("SimulationBasics: Could not find sky texture '" + skyModelPath + 
        			"'. Using default ('" + SimulationDefaults.skyTexture + "').");
        	sky = SkyFactory.createSky(assetManager, SimulationDefaults.skyTexture, EnvMapType.CubeMap);
        	
        } catch (Exception e) {
    	
        	System.err.println("SimulationBasics: Could not load sky texture '" + skyModelPath + 
        			"'. Using default ('" + SimulationDefaults.skyTexture + "').");
        	e.printStackTrace();
        	sky = SkyFactory.createSky(assetManager, SimulationDefaults.skyTexture, EnvMapType.CubeMap);
    	
        }
		
        sky.setShadowMode(ShadowMode.Off);
        sceneNode.attachChild(sky);
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
