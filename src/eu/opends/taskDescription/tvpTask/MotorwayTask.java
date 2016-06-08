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


package eu.opends.taskDescription.tvpTask;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;

import eu.opends.car.SteeringCar;
import eu.opends.main.Simulator;
import eu.opends.tools.DistanceBar;
import eu.opends.tools.DistanceBarSegment;
import eu.opends.tools.DistanceBar.Pivot;
import eu.opends.tools.DistanceBarSegment.SegmentType;
import eu.opends.tools.Util;
import eu.opends.traffic.PhysicalTraffic;
import eu.opends.traffic.TrafficCar;
import eu.opends.traffic.TrafficObject;

public class MotorwayTask 
{
	private boolean enabled = false;
	private float maxLateralDistance = 15;
	private float distanceBarHeight = 500;
	private float distanceBarWidth = 20;
	private float distanceBarLeft = 400;
	private float distanceBarBottom = 600;
	private float distancebarRotation = 180;
	
	private Simulator sim;
	private DistanceBar distanceBar = null;
	private MotorwayPosition motorwayPosition = MotorwayPosition.OFF;
	private boolean showDistanceBar = true;
	
	
	private enum MotorwayPosition
	{
		OFF, ENTERING, ON;
	}

	
	public MotorwayTask(Simulator sim)
	{
		this.sim = sim;
		
		if(Util.findNode(sim.getSceneNode(), "HighWayExit300m_1") != null)
			enabled = true;
		
		if(enabled)
		{
			Node city = (Node) ((Node)((Node) ((Node)((Node)((Node) sim.getSceneNode().getChild("City"))).getChild(0)).getChild(0)).getChild(0)).getChild(0);
			city.detachChildNamed("Test01_0-geom-10");
			city.detachChildNamed("Test01_0-geom-13");
			city.detachChildNamed("Test01_0-geom-15");
			
			
			for(TrafficObject trafficCar : PhysicalTraffic.getTrafficObjectList())
			{
				if(trafficCar instanceof TrafficCar)
				{
					((TrafficCar) trafficCar).useSpeedDependentForwardSafetyDistance(false);
					((TrafficCar) trafficCar).setMinForwardSafetyDistance(5);
				}
			}
		}
	}
	
	
	public void update(float tpf)
	{	
		if(enabled)
		{
			updateDistanceBar();
		}
	}

	
	private void updateDistanceBar()
	{
		// remove previous distance bar (if available)
		if(distanceBar != null)
			distanceBar.remove();
		
		// get closest traffic car in front and behind user-controlled car (steeringCar)
		TrafficCar closestLeadingCar = null;
		TrafficCar closestFollowerCar = null;
		
		SteeringCar car = sim.getCar();
		float carX = car.getPosition().getX();
		float carZ = car.getPosition().getZ();
		
		for(TrafficObject trafficCar : PhysicalTraffic.getTrafficObjectList())
		{
			if(trafficCar instanceof TrafficCar && trafficCar.getPosition().getX() < 0)
			{
				float trafficZ = trafficCar.getPosition().getZ();
				if(trafficZ < carZ)
				{
					if(closestFollowerCar == null || trafficZ > closestFollowerCar.getPosition().getZ())
						closestFollowerCar = (TrafficCar) trafficCar;
				}
				else
				{
					if(closestLeadingCar == null || trafficZ < closestLeadingCar.getPosition().getZ())
						closestLeadingCar = (TrafficCar) trafficCar;
				}
			}
		}
		
		// if traffic car in front and behind user-controlled car (steeringCar) available --> create new distance bar
		if(showDistanceBar && closestFollowerCar != null && closestLeadingCar != null && -maxLateralDistance < carX && carX <maxLateralDistance)
		{
			float leadingCarZ = closestLeadingCar.getPosition().getZ();
			float followerCarZ = closestFollowerCar.getPosition().getZ();
			float length = leadingCarZ - followerCarZ;

			// create new distance bar segments	
			ArrayList<DistanceBarSegment> distanceBarSegmentList = new ArrayList<DistanceBarSegment>();
			if(length <= 30)
				distanceBarSegmentList.add(new DistanceBarSegment("red", SegmentType.RED, 0, length));
			else if(length <= 60)
			{
				float center = length/2.0f;
				distanceBarSegmentList.add(new DistanceBarSegment("redFront", SegmentType.RED, 0, 10));
				distanceBarSegmentList.add(new DistanceBarSegment("transitionFront", SegmentType.REDTOGREEN, 10, center));
				distanceBarSegmentList.add(new DistanceBarSegment("transitionBack", SegmentType.GREENTORED, center, length-10));
				distanceBarSegmentList.add(new DistanceBarSegment("redBack", SegmentType.RED, length-10, length));
			} 
			else
			{
				distanceBarSegmentList.add(new DistanceBarSegment("redFront", SegmentType.RED, 0, 10));
				distanceBarSegmentList.add(new DistanceBarSegment("transitionFront", SegmentType.REDTOGREEN, 10, 30));
				distanceBarSegmentList.add(new DistanceBarSegment("green", SegmentType.GREEN, 30, length-30));
				distanceBarSegmentList.add(new DistanceBarSegment("transitionBack", SegmentType.GREENTORED, length-30, length-10));
				distanceBarSegmentList.add(new DistanceBarSegment("redBack", SegmentType.RED, length-10, length));
			}
			
			// create new distance bar
			distanceBar = new DistanceBar(distanceBarSegmentList, distanceBarWidth, distanceBarHeight, distanceBarLeft, 
					distanceBarBottom, distancebarRotation, false);
			distanceBar.init(sim);
			distanceBar.setCullHintIndicator(CullHint.Always);
			
			// get height (pixels) of car image in the correct ratio to distance bar height
			// length m = height px
			//   4.25 m =      ? px
			float targetHeightImage = distanceBarHeight/length * 4.25f;
			
			// get scaling factor of car image (based on 160px = 1.0) multiplied by 2 for double size
			//      160 px = 1.0
			// targetPX px = ?
			float targetScaleImage = 1f/160f * targetHeightImage * 2.0f;
			
			
			// get vertical deviation in the correct ratio to distance bar height
			//       length m = height px
			// verticalDist m =      ? px
			float verticalDist = closestFollowerCar.getPosition().getX() - car.getPosition().getX();
			float verticalDistPX = distanceBarHeight/length * verticalDist;

			// add three car images to the distance bar
			distanceBar.addIcon("Textures/DistanceBar/car.png", 64, 160, new Vector3f(distanceBarWidth/2.0f, -3, 0), 180, targetScaleImage, false, Pivot.BOTTOM);
			distanceBar.addIcon("Textures/DistanceBar/car.png", 64, 160, new Vector3f((distanceBarWidth/2.0f) - verticalDistPX, distanceBarHeight/2.0f, 0), -car.getHeadingDegree(), targetScaleImage, true, Pivot.CENTER);
			distanceBar.addIcon("Textures/DistanceBar/car.png", 64, 160, new Vector3f(distanceBarWidth/2.0f, distanceBarHeight + 3, 0), 180, targetScaleImage, false, Pivot.TOP);
			
			distanceBar.setDistance(leadingCarZ - carZ);
			distanceBar.setCullHint(CullHint.Never);
			
			if(motorwayPosition == MotorwayPosition.ENTERING)
			{
				String newLine = System.getProperty("line.separator");
				String time = new SimpleDateFormat("HH:mm:ss.SSS").format(System.currentTimeMillis());
				DecimalFormat decimalFormat = new DecimalFormat("#0.00");
				String distLeadingCar = decimalFormat.format(Math.abs(leadingCarZ - carZ));
				String distFollowerCar = decimalFormat.format(Math.abs(carZ - followerCarZ));
				float distLaneBeginningFloat = Math.abs(-1194 - carZ);
				String distLaneBeginning = decimalFormat.format(distLaneBeginningFloat);
				String distLaneEnd = decimalFormat.format(Math.abs(-913 - carZ));
				String percentage = decimalFormat.format((distLaneBeginningFloat/281f)*100f);
				
				Simulator.getDrivingTaskLogger().reportText(time + " --> Entering motorway" + newLine + 
						"Distance to leading car:         " + distLeadingCar + " m" + newLine + 
						"Distance to follower car:        " + distFollowerCar + " m" + newLine + 
						"Distance from beginning of lane: " + distLaneBeginning + " m" + newLine + 
						"Distance to end of lane:         " + distLaneEnd + " m" + newLine + 
						"Lane used:                       " + percentage + " %" + newLine);
				
				motorwayPosition = MotorwayPosition.ON;
			}
		}
		
		//if(-maxLateralDistance > carX || carX > maxLateralDistance)
		//	showDistanceBar = true;
	}

	
	public void setVisibilityDistanceBar(boolean isVisible)
	{
		showDistanceBar = isVisible;
	}
	
	
	public boolean getVisibilityDistanceBar()
	{
		return showDistanceBar;
	}
	

	public void setStimulus(String stimulusID)
	{
		if(stimulusID != null)
		{
			if(motorwayPosition == MotorwayPosition.OFF && stimulusID.equalsIgnoreCase("enter"))
				motorwayPosition = MotorwayPosition.ENTERING;
			else if(motorwayPosition == MotorwayPosition.ON && stimulusID.equalsIgnoreCase("exit"))
				motorwayPosition = MotorwayPosition.OFF;
		}	
	}

}
