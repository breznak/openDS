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

package eu.opends.camera;


import java.util.ArrayList;

import com.jme3.input.ChaseCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.ui.Picture;

import eu.opends.basics.SimulationBasics;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.Simulator;


/**
 * 
 * @author Rafael Math
 */
public abstract class CameraFactory 
{
	protected SimulationBasics sim;
	protected SettingsLoader settingsLoader;
	protected CameraMode camMode = CameraMode.EGO;
	protected boolean topViewEnabled = false;
	protected MirrorMode mirrorMode = MirrorMode.OFF;
	protected ChaseCamera chaseCam;
	protected CameraNode topViewCamNode;
	protected CameraNode mainCameraNode = new CameraNode();
	
	protected Node targetNode;
	protected Camera cam;
	protected static ViewPort topViewPort;
	protected static ViewPort backViewPort;
	protected static ViewPort leftBackViewPort;
	protected static ViewPort rightBackViewPort;
	protected Picture topViewFrame;
	protected Picture backMirrorFrame;
	protected Picture leftMirrorFrame;
	protected Picture rightMirrorFrame;
	protected float topViewVerticalDistance;
	protected float topViewcarOffset;
	
	// if false: TOP camera will always show north at the top of the screen
	// if true: car is pointing to the top of the screen (moving map)
	protected boolean isCarPointingUp = true;
	
	protected Vector3f outsideCamPos = new Vector3f(-558f, 22f, -668f);

	private float angleBetweenAdjacentCameras;
    
	private int width;
	private int height;	
	private float aspectRatio;
	private float frameOfView;
	
	private static ArrayList<ViewPort> viewPortList = new ArrayList<ViewPort>();
	public static ArrayList<ViewPort> getViewPortList()
	{
		return viewPortList;
	}
	
	public static ViewPort getTopViewPort()
	{
		return topViewPort;
	}
	
	public static ViewPort getBackViewPort()
	{
		return backViewPort;
	}
	
	public static ViewPort getLeftBackViewPort()
	{
		return leftBackViewPort;
	}
	
	public static ViewPort getRightBackViewPort()
	{
		return rightBackViewPort;
	}
	

	/**
	 * Get main camera node which contains all scene cameras.
	 * 
	 * @return
	 * 		Node containing all scene cameras.
	 */
	public CameraNode getMainCameraNode()
	{
		return mainCameraNode;
	}
	
	
	/**
	 * Camera views that can be activated while driving 
	 */
	public enum CameraMode 
	{
		CHASE, TOP, EGO, STATIC_BACK, OUTSIDE, OFF
	}

	
	/**
	 * Camera views that can be activated while driving 
	 */
	public enum MirrorMode
	{
		OFF, BACK_ONLY, ALL, SIDE_ONLY
	}
	
	
	/**
	 * Get the current camera view.
	 * 
	 * @return
	 * 		Current camera view.
	 */
	public CameraMode getCamMode() 
	{
		return camMode;
	}
	
	
	public boolean isTopViewEnabled() 
	{
		return topViewEnabled;
	}

	
	public void setTopViewEnabled(boolean enabled)
	{
		topViewEnabled = enabled;
	}
	
	
	/**
	 * Get the current mirror mode.
	 * 
	 * @return
	 * 		Current mirror mode.
	 */
	public MirrorMode getMirrorMode() 
	{
		return mirrorMode;
	}

	
	/**
	 * Set which mirror is visible or not
	 * 
	 * @param showMirror
	 * 		Boolean indicating visibility of rear view mirror
	 */
	public void setMirrorMode(MirrorMode mode)
	{
		// user may only change mirror mode in ego camera mode
		if(camMode == CameraMode.EGO)
			mirrorMode = mode;
	}
	
	
	/**
	 * Setup all scene cameras.
	 * 
	 * @param sim
	 *
	 * @param targetNode
	 */
	public void initCamera(SimulationBasics sim, Node targetNode) 
	{
		this.sim = sim;
		this.targetNode = targetNode;
		this.cam = sim.getCamera();
		this.settingsLoader = SimulationBasics.getSettingsLoader();
		
		isCarPointingUp = settingsLoader.getSetting(Setting.General_topView_carPointingUp, true);
		
		float outsideCamPosX = settingsLoader.getSetting(Setting.General_outsideCamPosition_x, -558f);
		float outsideCamPosY = settingsLoader.getSetting(Setting.General_outsideCamPosition_y, 22f);
		float outsideCamPosZ = settingsLoader.getSetting(Setting.General_outsideCamPosition_z, -668f);
		outsideCamPos = new Vector3f(outsideCamPosX, outsideCamPosY, outsideCamPosZ);
		
		// only render scene node (child of root node)
		// as root node contains for instance the map marker
		sim.getViewPort().detachScene(sim.getRootNode());
		sim.getViewPort().attachScene(sim.getSceneNode());
		
    	this.width = sim.getSettings().getWidth();
    	this.height = sim.getSettings().getHeight();
    	this.aspectRatio = (float)width/(float)height;
    	this.frameOfView = settingsLoader.getSetting(Setting.General_frameOfView, 30.5f);
    	//this.frameOfView = 30.5f; //62.5f; //25.63f; //(40*3.0f)/aspectRatio;  //25; //13.2f; //30.5f; // 23.2f; // 40/aspectRatio;
	    
    	// set initial mirror state
    	String mirrorModeString = settingsLoader.getSetting(Setting.General_mirrorMode, "off");
    	if(mirrorModeString.isEmpty())
    		mirrorModeString = "off";
    	this.mirrorMode = MirrorMode.valueOf(mirrorModeString.toUpperCase());
    	    	
    	angleBetweenAdjacentCameras = settingsLoader.getSetting(Setting.General_angleBetweenAdjacentCameras, 40);
    	if(angleBetweenAdjacentCameras > 90 || angleBetweenAdjacentCameras < 0)
    	{
    		System.err.println("Angle between adjacent cameras must be within 0 to 90 degrees. Set to default: 40 degrees.");
    		angleBetweenAdjacentCameras = 40;
    	}        
        
    	int numberOfScreens = sim.getNumberOfScreens();
	    if(numberOfScreens > 1)
	    {
	    	// clear default cam
	    	sim.getRenderManager().getMainView("Default").clearScenes();

	    	// add one camera for each screen
	    	for(int i = 1; i<=numberOfScreens; i++)
	    		setupCamera(i,numberOfScreens);
	    }
	    else
	    	setupCenterCamera();
	    
	    if(sim instanceof Simulator)
	    {
	    	setupTopView();
	    	setupBackCamera();
	    	setupLeftBackCamera();
	    	setupRightBackCamera();
	    }
	    
		setupChaseCamera();
	}
	
	
	public abstract void setCamMode(CameraMode mode);
	
	
	public abstract void changeCamera();

	
	public abstract void updateCamera();

	
	private void setupCamera(int index, int totalInt) 
	{
		float total = totalInt;
		Camera cam = new Camera(width, height);
		cam.setFrustumPerspective(frameOfView, aspectRatio/total, 1f, 2000);
		
		float additionalPixel = 1f/width;
		float viewPortLeft = (index-1)/total;
		float viewPortRight = (index)/total + additionalPixel;
		cam.setViewPort(viewPortLeft, viewPortRight, 0f, 1f);
		
		ViewPort viewPort = sim.getRenderManager().createMainView("View"+index, cam);
		viewPort.attachScene(sim.getSceneNode());
		viewPort.setBackgroundColor(ColorRGBA.Black);

		viewPortList.add(viewPort);
		
		// add camera to main camera node
		CameraNode camNode = new CameraNode("CamNode"+index, cam);
		camNode.setControlDir(ControlDirection.SpatialToCamera);
		mainCameraNode.attachChild(camNode);
		camNode.setLocalTranslation(new Vector3f(0, 0, 0));
		
		float angle = (((totalInt+1)/2)-index) * angleBetweenAdjacentCameras;
		camNode.setLocalRotation(new Quaternion().fromAngles(0, (180+angle)*FastMath.DEG_TO_RAD, 0));
	}

	
	/**
	 * 	Setup center camera (always on)
	 */
	private void setupCenterCamera() 
	{
		// add center camera to main camera node
		CameraNode centerCamNode = new CameraNode("CamNode1", cam);	
		centerCamNode.setControlDir(ControlDirection.SpatialToCamera);
		mainCameraNode.attachChild(centerCamNode);
		centerCamNode.setLocalTranslation(new Vector3f(0, 0, 0));
		centerCamNode.setLocalRotation(new Quaternion().fromAngles(0, 180*FastMath.DEG_TO_RAD, 0));
		
		viewPortList.add(sim.getViewPort());
	}


	/**
	 *	Setup top view
	 */
	private void setupTopView() 
	{
		Camera topViewCam = cam.clone();
		
		float left = settingsLoader.getSetting(Setting.General_topView_viewPortLeft, 0.05f);
		float right = settingsLoader.getSetting(Setting.General_topView_viewPortRight, 0.45f);
		float bottom = settingsLoader.getSetting(Setting.General_topView_viewPortBottom, 0.58f);
		float top = settingsLoader.getSetting(Setting.General_topView_viewPortTop, 0.98f);
		topViewVerticalDistance = settingsLoader.getSetting(Setting.General_topView_verticalDistance, 200f);
		topViewcarOffset = settingsLoader.getSetting(Setting.General_topView_carOffset, 40f);
		
		/*
		if(sim.getNumberOfScreens() > 1)
		{
			left = 0.15f;
			right = 0.35f;
			bottom = 0.78f;
			top = 0.98f;
		}
		*/
		
		topViewFrame = createMirrorFrame("topViewFrame",left,right,bottom,top);
		sim.getGuiNode().attachChild(topViewFrame);
		
		float aspect = ((right-left)*width)/((top-bottom)*height);
		
		topViewCam.setFrustumPerspective(30.0f, aspect, 1, 2000);
		//topViewCam.setFrustum(1, 2000, -20, 20, 20, -20);
		topViewCam.setViewPort(left, right, bottom, top);
		//topViewCam.setParallelProjection(true);
			
		// set view port (needed to show/hide top view)
	    topViewPort = sim.getRenderManager().createMainView("TopView", topViewCam);
	    topViewPort.setClearFlags(true, true, true);
	    topViewPort.attachScene(sim.getRootNode()); // use root node to visualize map marker
	    topViewPort.setEnabled(false);
	    
	    // add top view camera to main camera node
    	topViewCamNode = new CameraNode("topViewCamNode", topViewCam);
    	sim.getRootNode().attachChild(topViewCamNode);
	}
	
	
	/**
	 *	Setup rear view mirror
	 */
	private void setupBackCamera() 
	{
		Camera backCam = cam.clone();
		
		float left = settingsLoader.getSetting(Setting.General_rearviewMirror_viewPortLeft, 0.3f);
		float right = settingsLoader.getSetting(Setting.General_rearviewMirror_viewPortRight, 0.7f);
		float bottom = settingsLoader.getSetting(Setting.General_rearviewMirror_viewPortBottom, 0.78f);
		float top = settingsLoader.getSetting(Setting.General_rearviewMirror_viewPortTop, 0.98f);
		float horizontalAngle = settingsLoader.getSetting(Setting.General_rearviewMirror_horizontalAngle, 0f);
		float verticalAngle = settingsLoader.getSetting(Setting.General_rearviewMirror_verticalAngle, 0f);

		if(sim.getNumberOfScreens() > 1)
		{
			left = 0.4f;
			right = 0.6f;
			bottom = 0.78f;
			top = 0.98f;
		}
		
		backMirrorFrame =createMirrorFrame("backViewFrame",left,right,bottom,top);
		sim.getGuiNode().attachChild(backMirrorFrame);
		
		float aspect = ((right-left)*width)/((top-bottom)*height);
		
		backCam.setFrustumPerspective(30.0f, aspect, 1, 2000);
		backCam.setViewPort(left, right, bottom, top);
		
		// inverse back view cam (=> back view mirror)
		Matrix4f matrix = backCam.getProjectionMatrix().clone();
		matrix.m00 = - matrix.m00;
		backCam.setProjectionMatrix(matrix);
		
		// set view port (needed to show/hide mirror)
	    backViewPort = sim.getRenderManager().createMainView("BackView", backCam);
	    backViewPort.setClearFlags(true, true, true);
	    backViewPort.attachScene(sim.getSceneNode());
	    backViewPort.setEnabled(false);
	    
	    // add back camera to main camera node
    	CameraNode backCamNode = new CameraNode("BackCamNode", backCam);
    	backCamNode.setControlDir(ControlDirection.SpatialToCamera);
    	backCamNode.setLocalRotation(new Quaternion().fromAngles(verticalAngle*FastMath.DEG_TO_RAD, horizontalAngle*FastMath.DEG_TO_RAD, 0));
    	mainCameraNode.attachChild(backCamNode);
	}
	
	
	/**
	 *	Setup left rear view mirror
	 */
	private void setupLeftBackCamera() 
	{
		float left = settingsLoader.getSetting(Setting.General_leftMirror_viewPortLeft, 0.02f);
		float right = settingsLoader.getSetting(Setting.General_leftMirror_viewPortRight, 0.2f);
		float bottom = settingsLoader.getSetting(Setting.General_leftMirror_viewPortBottom, 0.3f);
		float top = settingsLoader.getSetting(Setting.General_leftMirror_viewPortTop, 0.6f);
		float horizontalAngle = settingsLoader.getSetting(Setting.General_leftMirror_horizontalAngle, -45f);
		float verticalAngle = settingsLoader.getSetting(Setting.General_leftMirror_verticalAngle, 10f);

		Camera leftBackCam = cam.clone();
		
		leftMirrorFrame = createMirrorFrame("leftBackViewFrame",left,right,bottom,top);
		sim.getGuiNode().attachChild(leftMirrorFrame);
		
		float aspect = ((right-left)*width)/((top-bottom)*height);
		
		leftBackCam.setFrustumPerspective(45.0f, aspect, 1, 2000);
		leftBackCam.setViewPort(left, right, bottom, top);
		
		// inverse left back view cam (=> left back view mirror)
		Matrix4f matrix = leftBackCam.getProjectionMatrix().clone();
		matrix.m00 = - matrix.m00;
		leftBackCam.setProjectionMatrix(matrix);
		
		// set view port (needed to show/hide mirror)
	    leftBackViewPort = sim.getRenderManager().createMainView("LeftBackView", leftBackCam);
	    leftBackViewPort.setClearFlags(true, true, true);
	    leftBackViewPort.attachScene(sim.getSceneNode());
	    leftBackViewPort.setEnabled(false);	    
	    
	    // add left back camera to main camera node
    	CameraNode leftBackCamNode = new CameraNode("LeftBackCamNode", leftBackCam);
    	leftBackCamNode.setControlDir(ControlDirection.SpatialToCamera);
    	
		leftBackCamNode.setLocalRotation(new Quaternion().fromAngles(verticalAngle*FastMath.DEG_TO_RAD, horizontalAngle*FastMath.DEG_TO_RAD, 0));
		leftBackCamNode.setLocalTranslation(new Vector3f(-1, 0, -1)); // 1m to the left (x=-1), 1m to the front (z=-1)
    	mainCameraNode.attachChild(leftBackCamNode);
	}
	
	
	/**
	 *	Setup right rear view mirror
	 */
	private void setupRightBackCamera() 
	{
		float left = settingsLoader.getSetting(Setting.General_rightMirror_viewPortLeft, 0.8f);
		float right = settingsLoader.getSetting(Setting.General_rightMirror_viewPortRight, 0.98f);
		float bottom = settingsLoader.getSetting(Setting.General_rightMirror_viewPortBottom, 0.3f);
		float top = settingsLoader.getSetting(Setting.General_rightMirror_viewPortTop, 0.6f);
		float horizontalAngle = settingsLoader.getSetting(Setting.General_rightMirror_horizontalAngle, 45f);
		float verticalAngle = settingsLoader.getSetting(Setting.General_rightMirror_verticalAngle, 10f);
		
		Camera rightBackCam = cam.clone();

		rightMirrorFrame = createMirrorFrame("rightBackViewFrame",left,right,bottom,top);
		sim.getGuiNode().attachChild(rightMirrorFrame);
		
		float aspect = ((right-left)*width)/((top-bottom)*height);
		
		rightBackCam.setFrustumPerspective(45.0f, aspect, 1, 2000);
		rightBackCam.setViewPort(left, right, bottom, top);
		
		// inverse right back view cam (=> right back view mirror)
		Matrix4f matrix = rightBackCam.getProjectionMatrix().clone();
		matrix.m00 = - matrix.m00;
		rightBackCam.setProjectionMatrix(matrix);
		
		// set view port (needed to show/hide mirror)
	    rightBackViewPort = sim.getRenderManager().createMainView("RightBackView", rightBackCam);
	    rightBackViewPort.setClearFlags(true, true, true);
	    rightBackViewPort.attachScene(sim.getSceneNode());
	    rightBackViewPort.setEnabled(false);
	    
	    // add right back camera to main camera node
    	CameraNode rightBackCamNode = new CameraNode("RightBackCamNode", rightBackCam);
    	rightBackCamNode.setControlDir(ControlDirection.SpatialToCamera);
    	
		rightBackCamNode.setLocalRotation(new Quaternion().fromAngles(verticalAngle*FastMath.DEG_TO_RAD, horizontalAngle*FastMath.DEG_TO_RAD, 0));
		rightBackCamNode.setLocalTranslation(new Vector3f(1, 0, -1));  // 1m to the right (x=1), 1m to the front (z=-1)
    	mainCameraNode.attachChild(rightBackCamNode);
	}
	
	
	private Picture createMirrorFrame(String name, float left, float right, float bottom, float top)
	{
		Picture mirrorImage = new Picture(name);
        mirrorImage.setImage(sim.getAssetManager(), "Textures/Misc/mirrorFrame.png", true);
        
        float imageWidth = (right-left) * width * 1.15f;
        mirrorImage.setWidth(imageWidth);
        
        float imageHeight = (top-bottom) * height * 1.15f;
        mirrorImage.setHeight(imageHeight);
        
        float x = left * width - (imageWidth * 0.055f);
        float y = bottom * height - (imageHeight * 0.055f);
        mirrorImage.setPosition(x, y);
        
        return mirrorImage;
	}
	

	/**
	 *	Setup free camera (can be controlled with mouse)
	 */
	private void setupChaseCamera() 
	{
		chaseCam = new ChaseCamera(cam, targetNode, sim.getInputManager());
        chaseCam.setUpVector(new Vector3f(0, 1, 0));
        chaseCam.setEnabled(false);
        
        // set visual parameters        
        float minDistance = settingsLoader.getSetting(Setting.Mouse_minScrollZoom, 1f);
        chaseCam.setMinDistance(minDistance);
        
        float maxDistance = settingsLoader.getSetting(Setting.Mouse_maxScrollZoom, 40f);
        chaseCam.setMaxDistance(maxDistance);
        
        float zoomSensitivity = settingsLoader.getSetting(Setting.Mouse_scrollSensitivityFactor, 5f);
        chaseCam.setZoomSensitivity(zoomSensitivity);
	}
}
