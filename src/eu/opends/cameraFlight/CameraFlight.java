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

package eu.opends.cameraFlight;

import java.util.List;

import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.cinematic.Cinematic;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.PlayState;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FadeFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;

//import eu.opends.camera.CameraFactory.CameraMode;
import eu.opends.main.Simulator;


/**
 * 
 * @author Rafael Math
 */
public class CameraFlight
{
	private Simulator sim;
    private Cinematic cinematic;
    private FadeFilter fade;
	private Camera cam;
	private ViewPort viewPort;
	private AssetManager assetManager;
	private MotionEvent cameraMotionTrack;
	private CameraNode mainCamNode;
	private float speedKmPerHour = 50f;
	private boolean isTerminated = true;
	private CameraFlightSettings settings;
	
	
    public CameraFlight(Simulator sim) throws NotEnoughWaypointsException
    {
    	settings = Simulator.getDrivingTask().getScenarioLoader().getCameraFlightSettings();
    	
    	this.sim = sim;
    	this.cam = sim.getCamera();
    	this.viewPort = sim.getViewPort();
    	this.assetManager = sim.getAssetManager();
    	this.speedKmPerHour = settings.getSpeed();
    	
    	List<Vector3f> wayPointList = settings.getWayPointList();
    	
        if(wayPointList.size() >= 2)
        {
        	// create shadow filter and fading filter
        	createFilters();

        	// get camera motion track
            MotionPath path = getCameraPath(wayPointList);
            
        	// calculate duration for traveling along the path at the given speed
        	float distanceMeters = path.getLength();
            float speed = speedKmPerHour / 3.6f;
            float duration = distanceMeters / speed;
            
            cinematic = new Cinematic(sim.getSceneNode(), duration);
            sim.getStateManager().attach(cinematic);
            
            MotionEvent cameraMotionTrack = createCameraMotion(path, duration);
        	cinematic.addCinematicEvent(0, cameraMotionTrack);
        	cinematic.activateCamera(0, "aroundCam");

        	// fade in and out
	        cinematic.addCinematicEvent(0, new FadeInEvent(fade));
	        cinematic.addCinematicEvent(duration - 1, new FadeOutEvent(fade));

	        // listener for play, pause and stop events
	        cinematic.addListener(new CameraFlightEventListener(sim, fade, speedKmPerHour));
	        
	        //TODO (uncomment)
			// set camera to ego mode (does not work with ConTRe Task)
			//sim.getCameraFactory().setCamMode(CameraMode.EGO);
			
	        //if(settings.isAutomaticStart())
	        //	play();
        } 
        else
        {
        	throw new NotEnoughWaypointsException();        		
        }
    }
    
    
    private int counter = 0;
	public void update()
	{
		// play need some "reaction" time of at least 1 frame
        if(settings.isAutomaticStart() && counter == 1)
        	play();

        counter++;
	}
	
    
	public void toggleStop()
	{
		if(cinematic != null)
		{
			if (cinematic.getPlayState() == PlayState.Playing)
	            stop();
	        else
	            play();
		}
	}
	
	
	public void togglePause()
	{
		if(cinematic != null)
		{
			if (cinematic.getPlayState() == PlayState.Playing)
	            pause();
	        else
	            play();
		}
	}
	
	
	public void stop()
	{
		if(cinematic != null)
	        cinematic.stop();
		
		isTerminated = true;
	}

	
	public void pause()
	{
		if(cinematic != null)
	        cinematic.pause();
		
		isTerminated = false;
	}
	
	
	public void play()
	{
		if(cinematic != null)
	        cinematic.play();
		
		isTerminated = false;
	}
	
	
	public boolean isTerminated() 
	{
		return isTerminated ;
	}
	
	
	public void setTerminated(boolean terminated) 
	{
		isTerminated = terminated;		
	}
	
    
    private void createFilters() 
    {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        //fpp.setNumSamples(4);
        fade = new FadeFilter();
        fpp.addFilter(fade);
        viewPort.addProcessor(fpp);
    }
    
    
	private MotionPath getCameraPath(List<Vector3f> wayPoints) 
	{
		MotionPath motionPath = new MotionPath();

		motionPath.setCycle(false); 
		
        for(Vector3f wayPoint : wayPoints)
        	motionPath.addWayPoint(wayPoint);
        
        //TODO (comment)
        motionPath.setPathSplineType(Spline.SplineType.Linear); // --> default: CatmullRom

        motionPath.addListener(new MotionPathListenerImpl(sim));
        
		return motionPath;
	}

	
	private MotionEvent createCameraMotion(MotionPath path, float duration)
    {
		Node virtualCarNode = cinematic.bindCamera("aroundCam", cam);

		mainCamNode = sim.getCameraFactory().getMainCameraNode();
		virtualCarNode.attachChild(mainCamNode);
    	mainCamNode.setLocalRotation(new Quaternion().fromAngles(0, FastMath.PI, 0));
		
        cameraMotionTrack = new MotionEvent(virtualCarNode, path, duration);
        cameraMotionTrack.setLoopMode(LoopMode.Loop);
        cameraMotionTrack.setDirectionType(MotionEvent.Direction.Path);

        return cameraMotionTrack;
    }
	
	
	public Vector3f getCamPosition()
	{
		if(cinematic != null)
			return cinematic.getCamera("aroundCam").getWorldTranslation();
		else
			return new Vector3f(0,0,0);
	}
	
	
	public Vector3f getCamDirection()
	{
		if(cameraMotionTrack != null)
			return cameraMotionTrack.getDirection();
		else
			return new Vector3f(0,0,0);
	}


	public float getSpeed()
	{
		return speedKmPerHour;
	}


	public void updateLateralCamPos(float lateralCamPos)
	{
		if(mainCamNode != null)
			mainCamNode.setLocalTranslation(lateralCamPos, 0, 0);
	}


}
