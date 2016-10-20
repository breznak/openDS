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


package eu.opends.traffic;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

import eu.opends.environment.TrafficLightCenter;
import eu.opends.main.Simulator;
import eu.opends.tools.Util;

import java.util.ArrayList;

/**
 * @author Tommi S.E. Laukkanen, Rafael Math
 */
public class Pedestrian implements AnimationListener, TrafficObject
{
	private Simulator sim;
	private Node personNode = new Node();
	private BetterCharacterControl characterControl;
	private AnimationController animationController;
	private FollowBox followBox;
	private String name;
	private float mass = 5f;
	private float airTime = 0;
	
	// the animation commands
	private String animationStandCommand = "Stand";
	private String animationWalkCommand = "WalkBaked";
	
	// safety distances // TODO get from scenario.xml
	private float minLateralSafetyDistance = 2;
	private float minForwardSafetyDistance = 5;

	// walking speed of the character
	private float walkingSpeedKmh = 4f;
	private boolean walkingSpeedChanged = true;
	private boolean enabled = true;

	PedestrianData pedestrianData;
	boolean initialized = false;
    public Pedestrian(Simulator sim, PedestrianData pedestrianData) 
    {
    	this.sim = sim;
    	this.pedestrianData = pedestrianData;
    
    	name = pedestrianData.getName();
    	enabled = pedestrianData.getEnabled();
    	
    	animationStandCommand = pedestrianData.getAnimationStand();
    	animationWalkCommand = pedestrianData.getAnimationWalk();

		AssetManager assetManager = sim.getAssetManager();
		Node person = (Node) assetManager.loadModel(pedestrianData.getModelPath());
		personNode.attachChild(person);
		person.setLocalScale(pedestrianData.getLocalScale()); // adjust scale of model
		person.setLocalTranslation(pedestrianData.getLocalTranslation()); // adjust position to ensure collisions occur correctly
		person.setLocalRotation(pedestrianData.getLocalRotation()); // adjust rotation of model
		
		// TODO adjust ambient light in OgreXML file
		AmbientLight light = new AmbientLight();
		light.setColor(ColorRGBA.White.mult(0.7f));
		person.addLight(light);
		
		// shadow of character
		person.setShadowMode(ShadowMode.Cast);
		
		// construct character (if character bounces, try increasing height and weight)
		mass = pedestrianData.getMass();
		characterControl = new BetterCharacterControl(0.3f, 1.8f, mass); // TODO get from scenario.xml
		personNode.addControl(characterControl);
    }
    
    
    private void init()
    {
		// add to physics state
		sim.getBulletAppState().getPhysicsSpace().add(characterControl); 
		sim.getBulletAppState().getPhysicsSpace().addAll(personNode); 
		sim.getSceneNode().attachChild(personNode);
		  
		animationController = new AnimationController(personNode);
		animationController.setAnimationListener(this);
		
		//printAvailableAnimations("Body");
		
		followBox = new FollowBox(sim, this, pedestrianData.getFollowBoxSettings());
		
		initialized = true;
    }
    
    
    @Override
	public void update(float tpf, ArrayList<TrafficObject> vehicleList) 
    {
    	// prevent pedestrians from high jump when adding to the physics engine
    	if(tpf < 1.0f && !initialized)
    		init();
    	
    	if(initialized)
    	{
			if(!sim.isPause())
			{
		    	// update speed for current way point segment
		    	float nextWalkingSpeedKmh = Math.max(followBox.getSpeed(),0);
		    	
		    	if(!enabled)
		    		nextWalkingSpeedKmh = 0;
		    	
		    	if(nextWalkingSpeedKmh != walkingSpeedKmh)
		    	{
		    		walkingSpeedKmh = nextWalkingSpeedKmh;
		    		walkingSpeedChanged = true;
		    	}
		    	
		        if (!characterControl.isOnGround()) 
		            airTime += tpf;
		        else
		            airTime = 0;
		        
		        // compute view direction (towards car) in upright walking position (y = 0)
		        Vector3f viewDirection = followBox.getPosition().subtract(personNode.getLocalTranslation());
		        viewDirection.setY(0);
		
		        float distance = viewDirection.length();
		        if (distance != 0)
		        	characterControl.setViewDirection(viewDirection);
		       
		        
		        if (distance < 0.1f || obstaclesInTheWay(vehicleList))
		        { 
		        	if (!animationStandCommand.equals(animationController.getAnimationName())) 
		        		animationController.animate(animationStandCommand, 1f, 1f, 0);
		
		        	characterControl.setWalkDirection(new Vector3f(0,0,0)); // stop walking
		        } 
		        else 
		        {
		            if (airTime > 0.3f)
		            {
		            	if (!animationStandCommand.equals(animationController.getAnimationName()))
		            		animationController.animate(animationStandCommand, 1f, 1f, 0);
		            }
		            else if (!animationWalkCommand.equals(animationController.getAnimationName()) || walkingSpeedChanged)
		            {
		            		animationController.animate(animationWalkCommand, (walkingSpeedKmh/3.6f)*2.0f, 0.7f, 0);
		            		walkingSpeedChanged = false;
		            }
		            
		            // the use of the multiplier is to control the rate of movement for character walk speed (in m/s)
		            characterControl.setWalkDirection(viewDirection.normalize().multLocal((walkingSpeedKmh/3.6f)));
		        }
		
		        //System.err.println("Current speed of character '" + name + "': " + getCurrentSpeedKmh());
		        
		    	animationController.update(tpf);   	
		    }
			
			// update movement of follow box according to pedestrians's position (not affected by sim.isPause())
			followBox.update(personNode.getLocalTranslation());
    	}
    }
    
    
    public void setEnabled(boolean enabled)
    {
    	this.enabled = enabled;
    }
    
    
    public float getCurrentSpeedKmh()
    {
    	return characterControl.getVelocity().length() * 3.6f;
    }
    
    
    /*
	private void printAvailableAnimations(String mainMeshName) 
	{
		System.out.println("Animated spatials: " + animationController.getSpatialNamesWithAnimations());
		
		String mainSpatialName = null;
		for (final String spatialName : animationController.getSpatialNamesWithAnimations()) 
		{
		    if (spatialName.startsWith(mainMeshName))
		        mainSpatialName =  spatialName;
		}
		
		if (mainSpatialName != null)
		{		
			System.out.println("Main mesh: " + mainSpatialName);
			
			final AnimControl control = animationController.getAnimControl(mainSpatialName);
			if (control != null)
			{
				ArrayList<String> animations = new ArrayList<String>(control.getAnimationNames());
			    System.out.println("Available animation commands: " + animations);
			}
		}
		else
			System.out.println("No animation commands available");
	}
    */
    
	
	public void setToWayPoint(String wayPointID) 
	{
		if(initialized)
    	{
			int index = followBox.getIndexOfWP(wayPointID);
			if(index != -1)
				followBox.setToWayPoint(index);
			else
				System.err.println("Invalid way point ID: " + wayPointID);
    	}
	}
	
	
	public void setToWayPoint(int index)
	{
		if(initialized)
    	{
			followBox.setToWayPoint(index);
    	}
	}
	
	
    @Override
    public void onAnimCycleDone(final String animationName)
    {
    	
    }

    
	@Override
	public Vector3f getPosition()
	{
		return personNode.getLocalTranslation();
	}
	

	@Override
	public void setPosition(Vector3f position) 
	{
		characterControl.warp(position);
	}


	@Override
	public void setRotation(Quaternion quaternion) 
	{
		// automatic orientation in next update()
	}


	@Override
	public float getMaxBrakeForce() 
	{
		// needed for follow box reduced speed computation
		// not relevant for pedestrians --> return 0
		return 0;
	}


	@Override
	public float getMass() 
	{
		return mass;
	}


	@Override
	public String getName() 
	{
		return name;
	}
	
	
	private boolean obstaclesInTheWay(ArrayList<TrafficObject> trafficObjectList)
	{		
		// check distance from user-controlled car
		if(obstacleTooClose(sim.getCar().getPosition()))
			return true;

		// check distance from other cars (exclude pedestrians)
		for(TrafficObject vehicle : trafficObjectList)
		{
			if(!vehicle.getName().equals(name) && vehicle instanceof TrafficCar)		
				if(obstacleTooClose(vehicle.getPosition()))
					return true;
		}
		
		// check if red traffic light ahead
		Waypoint nextWayPoint = followBox.getNextWayPoint();
		if(TrafficLightCenter.hasRedTrafficLight(nextWayPoint))
			if(obstacleTooClose(nextWayPoint.getPosition()))
				return true;
		
		return false;
	}


	private boolean obstacleTooClose(Vector3f obstaclePos)
	{
		float distanceToObstacle = obstaclePos.distance(getPosition());
		
		// angle between view direction of pedestrian and direction towards obstacle
		// (consider 3D space, because obstacle could be located on a bridge above pedestrian)
		Vector3f viewDirection = characterControl.getViewDirection().normalize();
		Vector3f obstacleDirection = obstaclePos.subtract(this.getPosition()).normalize();
		
		float angle = viewDirection.angleBetween(obstacleDirection);
			
		//if(name.equals("pedestrian01"))
		//	System.out.println(angle * FastMath.RAD_TO_DEG);
		
		if(belowSafetyDistance(angle, distanceToObstacle))
			return true;

		// considering direction towards next way point (if available)
		Waypoint nextWP = followBox.getNextWayPoint();
		if(nextWP != null)
		{
			// angle between direction towards next WP and direction towards obstacle
			// (consider 3D space, because obstacle could be located on a bridge above pedestrian)
			angle = Util.getAngleBetweenPoints(nextWP.getPosition(), this.getPosition(), obstaclePos, false);			
			if(belowSafetyDistance(angle, distanceToObstacle))
				return true;
		}

		return false;
	}
	
	
	private boolean belowSafetyDistance(float angle, float distance) 
	{	
		float lateralDistance = distance * FastMath.sin(angle);
		float forwardDistance = distance * FastMath.cos(angle);
		
		//if(name.equals("pedestrian01"))
		//	System.out.println(lateralDistance + " *** " + forwardDistance);
		
		if((lateralDistance < minLateralSafetyDistance) && (forwardDistance > 0) && (forwardDistance < minForwardSafetyDistance))
		{
			return true;
		}
		
		return false;
	}

  
}