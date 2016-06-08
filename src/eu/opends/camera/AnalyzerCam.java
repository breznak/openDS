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

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import eu.opends.main.DriveAnalyzer;

/**
 * 
 * @author Rafael Math
 */
public class AnalyzerCam extends CameraFactory 
{
	public AnalyzerCam(DriveAnalyzer analyzer, Node targetNode) 
	{
		initCamera(analyzer, targetNode);
		setCamMode(CameraMode.CHASE);
	}
	
	
	public void setCamMode(CameraMode mode)
	{		
		switch (mode) 
		{
			case TOP:
				camMode = CameraMode.TOP;
				chaseCam.setEnabled(false);
				updateCamera();
				break;
	
			case CHASE:
				camMode = CameraMode.CHASE;
				chaseCam.setEnabled(true);
				updateCamera();
				break;
				
			default: break;	
		}
	}
	
	
	public void changeCamera() 
	{
		switch (camMode) 
		{
			// CHASE --> TOP --> CHASE --> ...
			case CHASE: setCamMode(CameraMode.TOP); break;
			case TOP:setCamMode(CameraMode.CHASE); break;
			default: break;
		}
	}
	
	
	public void updateCamera()
	{
		if(camMode == CameraMode.CHASE)
		{
			// set camera position
			Vector3f targetPosition = targetNode.localToWorld(new Vector3f(0, 0, 0), null);
			Vector3f camPos = new Vector3f(targetPosition.x, targetPosition.y + 2, targetPosition.z);
			cam.setLocation(camPos);
			
		
			// get rotation of target node
			Quaternion targetRotation = targetNode.getLocalRotation();
			
			// rotate cam direction by 180 degrees, since car is actually driving backwards
			Quaternion YAW180 = new Quaternion().fromAngleAxis(FastMath.PI, new Vector3f(0,1,0));
			targetRotation.multLocal(YAW180);
			
			// set camera rotation
			cam.setRotation(targetRotation);
		}
		
		else if(camMode == CameraMode.TOP)
		{
			// set camera position
			Vector3f targetPosition = targetNode.localToWorld(new Vector3f(0, 0, 0), null);
			Vector3f camPos = new Vector3f(targetPosition.x, targetPosition.y + 30, targetPosition.z);
			cam.setLocation(camPos);

			// set camera direction
			Vector3f left = new Vector3f(-1, 0, 0);
			Vector3f up = new Vector3f(0, 0, -1);
			Vector3f direction = new Vector3f(0, -1f, 0);
			cam.setAxes(left, up, direction);
		}
	}

}
