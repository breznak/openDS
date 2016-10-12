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

package eu.opends.camera;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Cylinder;

import eu.opends.car.Car;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.Simulator;
import eu.opends.tools.PanelCenter;
import eu.opends.tools.Util;

/**
 * 
 * @author Rafael Math
 */
public class SimulatorCam extends CameraFactory 
{	
	private Car car;
	private Node carNode;
	private Geometry geoCone;
	
	
	public SimulatorCam(Simulator sim, Car car) 
	{	    
		this.car = car;
		carNode = car.getCarNode();
		
		initCamera(sim, carNode);	
		
		String cameraModeString = settingsLoader.getSetting(Setting.General_cameraMode, "ego").toUpperCase();
		if(cameraModeString == null || cameraModeString.isEmpty())
			cameraModeString = "EGO";
		CameraMode cameraMode = CameraMode.valueOf(cameraModeString);
		setCamMode(cameraMode);
		initMapMarker();
	}


	private void initMapMarker()
	{
		Cylinder cone = new Cylinder(10, 10, 3f, 0.1f, 9f, true, false);
		cone.setLineWidth(4f);
		geoCone = new Geometry("TopViewMarker", cone);
		
	    Material coneMaterial = new Material(sim.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
	    coneMaterial.setColor("Color", ColorRGBA.Red);
		geoCone.setMaterial(coneMaterial);
		
		geoCone.setCullHint(CullHint.Always);
		
		sim.getRootNode().attachChild(geoCone);
	}


	public void setCamMode(CameraMode mode)
	{
		switch (mode)
		{
			case EGO:
				camMode = CameraMode.EGO;
				sim.getRootNode().detachChild(mainCameraNode);
				carNode.attachChild(mainCameraNode);
				chaseCam.setEnabled(false);
				setCarVisible(false);// FIXME --> false  // used for oculus rift internal car environment
				((CameraControl) frontCameraNode.getChild("CamNode1").getControl(0)).setEnabled(true);
				frontCameraNode.setLocalTranslation(car.getCarModel().getEgoCamPos());
				frontCameraNode.setLocalRotation(new Quaternion().fromAngles(0, 0, 0));
				break;
	
			case CHASE:
				camMode = CameraMode.CHASE;
				sim.getRootNode().detachChild(mainCameraNode);
				carNode.attachChild(mainCameraNode);
				chaseCam.setEnabled(true);
				chaseCam.setDragToRotate(false);
				setCarVisible(true);
				((CameraControl) frontCameraNode.getChild("CamNode1").getControl(0)).setEnabled(false);
				break;
	
			case TOP:
				camMode = CameraMode.TOP;
				// camera detached from car node in TOP-mode to make the camera movement more stable
				carNode.detachChild(mainCameraNode);
				sim.getRootNode().attachChild(mainCameraNode);
				chaseCam.setEnabled(false);
				setCarVisible(true);
				((CameraControl) frontCameraNode.getChild("CamNode1").getControl(0)).setEnabled(true);
				break;
				
			case OUTSIDE:
				camMode = CameraMode.OUTSIDE;
				// camera detached from car node in OUTSIDE-mode
				carNode.detachChild(mainCameraNode);
				sim.getRootNode().attachChild(mainCameraNode);
				chaseCam.setEnabled(false);
				setCarVisible(true);
				((CameraControl) frontCameraNode.getChild("CamNode1").getControl(0)).setEnabled(true);
				break;
	
			case STATIC_BACK:
				camMode = CameraMode.STATIC_BACK;
				sim.getRootNode().detachChild(mainCameraNode);
				carNode.attachChild(mainCameraNode);
				chaseCam.setEnabled(false);
				setCarVisible(true);
				((CameraControl) frontCameraNode.getChild("CamNode1").getControl(0)).setEnabled(true);
				frontCameraNode.setLocalTranslation(car.getCarModel().getStaticBackCamPos());
				frontCameraNode.setLocalRotation(new Quaternion().fromAngles(0, 0, 0));
				break;
				
			case OFF:
				camMode = CameraMode.OFF;
				chaseCam.setEnabled(false);
				setCarVisible(false);
				break;
		}
	}
	
	
	public void changeCamera() 
	{
		// STATIC_BACK --> EGO (--> CHASE, only if 1 screen) --> TOP --> OUTSIDE --> STATIC_BACK --> ...
		switch (camMode)
		{
			case STATIC_BACK: setCamMode(CameraMode.EGO); break;
			case EGO: 
					if(sim.getNumberOfScreens() == 1)
						setCamMode(CameraMode.CHASE);
					else
						setCamMode(CameraMode.TOP);
					break;
			case CHASE: setCamMode(CameraMode.TOP); break;
			case TOP: setCamMode(CameraMode.OUTSIDE); break;
			case OUTSIDE: setCamMode(CameraMode.STATIC_BACK); break;
			default: break;
		}
	}
	
	
	public void updateCamera()
	{
		if(camMode == CameraMode.EGO)
		{
			if(mirrorMode == MirrorMode.ALL)
			{
				backViewPort.setEnabled(true);
				leftBackViewPort.setEnabled(true);
				rightBackViewPort.setEnabled(true);
				backMirrorFrame.setCullHint(CullHint.Dynamic);
				leftMirrorFrame.setCullHint(CullHint.Dynamic);
				rightMirrorFrame.setCullHint(CullHint.Dynamic);
			}
			else if(mirrorMode == MirrorMode.BACK_ONLY)
			{
				backViewPort.setEnabled(true);
				leftBackViewPort.setEnabled(false);
				rightBackViewPort.setEnabled(false);
				backMirrorFrame.setCullHint(CullHint.Dynamic);
				leftMirrorFrame.setCullHint(CullHint.Always);
				rightMirrorFrame.setCullHint(CullHint.Always);
			}
			else if(mirrorMode == MirrorMode.SIDE_ONLY)
			{
				backViewPort.setEnabled(false);
				leftBackViewPort.setEnabled(true);
				rightBackViewPort.setEnabled(true);
				backMirrorFrame.setCullHint(CullHint.Always);
				leftMirrorFrame.setCullHint(CullHint.Dynamic);
				rightMirrorFrame.setCullHint(CullHint.Dynamic);
			}
			else
			{
				backViewPort.setEnabled(false);
				leftBackViewPort.setEnabled(false);
				rightBackViewPort.setEnabled(false);
				backMirrorFrame.setCullHint(CullHint.Always);
				leftMirrorFrame.setCullHint(CullHint.Always);
				rightMirrorFrame.setCullHint(CullHint.Always);
			}			
		}
		else
		{
			backViewPort.setEnabled(false);
			leftBackViewPort.setEnabled(false);
			rightBackViewPort.setEnabled(false);
			
			backMirrorFrame.setCullHint(CullHint.Always);
			leftMirrorFrame.setCullHint(CullHint.Always);
			rightMirrorFrame.setCullHint(CullHint.Always);
		}
		
		if(camMode == CameraMode.TOP)
		{
			// camera detached from car node --> update position and rotation separately
			Vector3f targetPosition = carNode.localToWorld(new Vector3f(0, 0, 0), null);
			Vector3f camPos = new Vector3f(targetPosition.x, targetPosition.y + 30, targetPosition.z);
			frontCameraNode.setLocalTranslation(camPos);
			
			float upDirection = 0;
			if(isCarPointingUp)
			{
				float[] angles = new float[3];
				carNode.getLocalRotation().toAngles(angles);
				upDirection = angles[1];
			}
			frontCameraNode.setLocalRotation(new Quaternion().fromAngles(-FastMath.HALF_PI, upDirection, 0));
		}
		
		if(camMode == CameraMode.OUTSIDE)
		{
			// camera detached from car node --> update position and rotation separately
			frontCameraNode.setLocalTranslation(outsideCamPos);

			Vector3f carPos = carNode.getWorldTranslation();

			Vector3f direction = carPos.subtract(outsideCamPos);
			direction.normalizeLocal();
			direction.negateLocal();
			
			Vector3f up = new Vector3f(0, 1, 0);
			
			Vector3f left = up.cross(direction);
			left.normalizeLocal();

	        if (left.equals(Vector3f.ZERO)) {
	            if (direction.x != 0) {
	                left.set(direction.y, -direction.x, 0f);
	            } else {
	                left.set(0f, direction.z, -direction.y);
	            }
	        }
	        up.set(direction).crossLocal(left).normalizeLocal();
			frontCameraNode.setLocalRotation(new Quaternion().fromAxes(left, up, direction));
		
		}
		
		// additional top view window ("map")		
		if(topViewEnabled)
		{			
			topViewPort.setEnabled(true);
			topViewFrame.setCullHint(CullHint.Dynamic);
			geoCone.setCullHint(CullHint.Dynamic);
			
			// camera detached from car node --> update position and rotation separately
			float upDirection = 0;
			float addLeft = 0;
			float addRight = 0;
			if(isCarPointingUp)
			{
				float[] angles = new float[3];
				carNode.getLocalRotation().toAngles(angles);
				upDirection = angles[1] + FastMath.PI;
				
				// allow to place car in lower part of map (instead of center)
				addLeft = topViewcarOffset * FastMath.sin(upDirection);
				addRight = topViewcarOffset * FastMath.cos(upDirection);
			}
			Quaternion camRot = new Quaternion().fromAngles(FastMath.HALF_PI, upDirection, 0);
			topViewCamNode.setLocalRotation(camRot);
			topViewCamNode.detachChildNamed("TopViewMarker");

			Vector3f targetPosition = carNode.localToWorld(new Vector3f(0, 0, 0), null);
			float left = targetPosition.x + addLeft;
			float up = targetPosition.y + topViewVerticalDistance;
			float ahead = targetPosition.z + addRight;
			Vector3f camPos = new Vector3f(left, up, ahead);
			topViewCamNode.setLocalTranslation(camPos);
			
			// set cone position
			geoCone.setLocalTranslation(targetPosition.x, targetPosition.y + 3, targetPosition.z);
			geoCone.setLocalRotation(carNode.getLocalRotation());
		}
		else
		{
			topViewPort.setEnabled(false);
			topViewFrame.setCullHint(CullHint.Always);
			geoCone.setCullHint(CullHint.Always);
		}
	}
	
	
	public void setCarVisible(boolean setVisible) 
	{
		if(setVisible)
		{
			// e.g. outside car

			//carNode.setCullHint(CullHint.Never);

			// show everything except sub-geometries of node interior
			Node interior = Util.findNode(carNode, "interior");
			for(Geometry g : Util.getAllGeometries(carNode))
				if(g.hasAncestor(interior))
					g.setCullHint(CullHint.Always);  //interior
				else
					g.setCullHint(CullHint.Dynamic); //rest (or interior == null)
					
		}
		else
		{
			// e.g. inside car
			
			//carNode.setCullHint(CullHint.Always);

			// cull everything except sub-geometries of node interior
			Node interior = Util.findNode(carNode, "interior");
			for(Geometry g : Util.getAllGeometries(carNode))
				if(g.hasAncestor(interior))
					g.setCullHint(CullHint.Dynamic); //interior
				else
					g.setCullHint(CullHint.Always);  //rest (or interior == null)
					
		}
		
		PanelCenter.showHood(!setVisible);
	}
}
