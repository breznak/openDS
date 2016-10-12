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

package eu.opends.tools;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.ui.Picture;

import eu.opends.basics.SimulationBasics;

public class DistanceBar 
{
	public enum Pivot {
		BOTTOM, CENTER, TOP
	}


	// default extent, position and rotation
	private ArrayList<DistanceBarSegment> segmentsList = new ArrayList<DistanceBarSegment>();
	private float distanceBarWidth = 20;
	private float distanceBarHeight = 200;
	private float distanceBarLeft = 10;
	private float distanceBarBottom = 10;
	private float distanceBarRotation = 0;
	private boolean showText = false;

	// dependent variables
	private float globalMinDistance = Float.MAX_VALUE;
	private float globalMaxDistance = Float.MIN_VALUE;
	private float longestDistance = 0;
	
	// indicator width = 5/4 of distance bar width
	private float indicatorWidth = 1.25f * distanceBarWidth;
    
    // indicator height = 3/5 of indicator width
	private float indicatorHeight = 0.6f * indicatorWidth;
	
    // indicator left offset = 1/10 of indicator width
	private float indicatorLeftOffset = 0.1f * indicatorWidth;
    
    // indicator bottom offset = 1/2 of indicator height
	private float indicatorBottomOffset = 0.5f * indicatorHeight;

	private Node distanceBarNode;
	private Picture indicator;
	private BitmapText distanceText;
	private SimulationBasics sim = null;
	
	
	public DistanceBar(ArrayList<DistanceBarSegment> segmentsList, 
			float width, float height, float left, float bottom, float rotation, boolean showText)
	{
		this.segmentsList = segmentsList;
		this.distanceBarWidth = width;
		this.distanceBarHeight = height;
		this.distanceBarLeft = left;
		this.distanceBarBottom = bottom;
		this.distanceBarRotation = rotation;
		this.showText = showText;
	}
	
	
	public void init(SimulationBasics sim)
	{
		this.sim = sim;
		
		distanceBarNode = new Node("distanceBar");
		//ArrayList<DistanceBarSegment> segmentsList = initSegments();		
				
		// compute difference between minimum and maximum distance values
		longestDistance = getLongestDistance(segmentsList);
		
		// create colored segments
		for(DistanceBarSegment segment : segmentsList)
		{			
			// get minimum and maximum distance value of segment to compute position and extend
			float minDist = segment.getMinimumDistance();
			float maxDist = segment.getMaximumDistance();
			float diffDist = maxDist - minDist;
			
			// compute height of segment from its minimum and maximum distance value
			float segmentHeight = distanceToPixel(diffDist);
			
			// compute bottom of segment from its minimum distance value
			float segmentBottom = distanceToPixel(minDist - globalMinDistance);
			
			// create segment picture
	        Picture segmentPicture = new Picture("segment_" + segment.getName());
	        segmentPicture.setImage(sim.getAssetManager(), segment.getPath(), true);
	        segmentPicture.setWidth(distanceBarWidth);
	        segmentPicture.setHeight(segmentHeight);
	        segmentPicture.setPosition(0, segmentBottom);
	        distanceBarNode.attachChild(segmentPicture);
		}
		
		// create indicator
        indicator = new Picture("distance_indicator");
        indicator.setImage(sim.getAssetManager(), "Textures/DistanceBar/indicator.png", true);
        indicator.setWidth(indicatorWidth);
        indicator.setHeight(indicatorHeight);
        distanceBarNode.attachChild(indicator);
        
        // create distance text
		BitmapFont guiFont = sim.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        distanceText = new BitmapText(guiFont, false);
        distanceText.setName("distanceText");
        distanceText.setText("not initialized");
        distanceText.scale(1f);
        distanceText.setSize(guiFont.getCharSet().getRenderedSize());
        distanceText.setColor(ColorRGBA.White);
        Quaternion q = (new Quaternion()).fromAngles(0, 0, distanceBarRotation * FastMath.DEG_TO_RAD);
        distanceText.setLocalRotation(q);
        distanceBarNode.attachChild(distanceText);

        // disable text if set
        if(!showText)
        	distanceText.setCullHint(CullHint.Always);        
        
        // attach distance bar node to GUI node
		sim.getGuiNode().attachChild(distanceBarNode);
		
        // set initial distance
        setDistance(0);
		
		// changes pivot point for rotation from lower left to lower right
		//distanceBarBottom += FastMath.sin(distanceBarRotation * FastMath.DEG_TO_RAD) * distanceBarWidth;
		//distanceBarLeft -= FastMath.cos(distanceBarRotation * FastMath.DEG_TO_RAD) * distanceBarWidth;
		
		// set global left and bottom
		distanceBarNode.setLocalTranslation(distanceBarLeft, distanceBarBottom, 0);
		
		// set rotation
		Quaternion quaternion = (new Quaternion()).fromAngles(0, 0, - distanceBarRotation * FastMath.DEG_TO_RAD);
		distanceBarNode.setLocalRotation(quaternion);	
	}
	


	public void remove()
	{
		if(sim != null)
			sim.getGuiNode().detachChild(distanceBarNode);
	}
	
	
	ArrayList<Node> dynamicNodeList = new ArrayList<Node>();
	public void addIcon(String path, float width, float height, Vector3f translation, 
			float rotation, float scale, boolean dynamic, Pivot pivotPosition)
	{
		Picture icon = new Picture("icon");
		icon.setImage(sim.getAssetManager(), path, true);
		
		// scale Picture
		icon.setWidth(width);
		icon.setHeight(height);
		
		// center picture in topIconNode
		if(pivotPosition == Pivot.BOTTOM)
			icon.setPosition(-width/2.0f,0);
		else if(pivotPosition == Pivot.TOP)
			icon.setPosition(-width/2.0f,-height);
		else
			icon.setPosition(-width/2.0f,-height/2.0f);
		
		Node iconNode = new Node("iconNode");
		iconNode.attachChild(icon);
		
		iconNode.setLocalTranslation(translation);
		
		Quaternion qq = (new Quaternion()).fromAngles(0, 0, rotation * FastMath.DEG_TO_RAD);
		iconNode.setLocalRotation(qq);
		
		iconNode.setLocalScale(scale);
		
		if(dynamic)
			dynamicNodeList.add(iconNode);
		
        distanceBarNode.attachChild(iconNode);
	}
	
	
	public void setDistance(float distance)
	{
		// convert distance to pixel (subtract distance offset)
		float distance_px = distanceToPixel(distance - globalMinDistance);
		
		// indicator must be below upper end of distance bar
		distance_px = Math.min(distance_px, distanceBarHeight - indicatorBottomOffset);
		
		// indicator must be above lower end of distance bar
		distance_px = Math.max(distance_px, indicatorBottomOffset);
		
		// set position of indicator
        indicator.setPosition(0 - indicatorLeftOffset, distance_px - indicatorBottomOffset);
        
        for(Node dynamicNode : dynamicNodeList)
        {
        	float x = dynamicNode.getLocalTranslation().getX();
        	dynamicNode.setLocalTranslation(x, distance_px, 0);
        }
        
        // set text of distance bitmap text
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        distanceText.setText(decimalFormat.format(distance) + " m");
        
        // set position of distance bitmap text
        distanceText.setLocalTranslation(0 - indicatorLeftOffset, distance_px - indicatorBottomOffset, 0);
	}
	
	
	public void setCullHint(CullHint hint)
	{
		distanceBarNode.setCullHint(hint);
	}

	
	public void setCullHintIndicator(CullHint hint)
	{
		indicator.setCullHint(hint);
	}
	
	
	private float distanceToPixel(float distance)
	{
		return distanceBarHeight * (distance / longestDistance);
	}
	

	private float getLongestDistance(ArrayList<DistanceBarSegment> list) 
	{		
		for(DistanceBarSegment segment : list)
		{
			if(segment.getMinimumDistance() < globalMinDistance)
				globalMinDistance = segment.getMinimumDistance();
			
			if(segment.getMaximumDistance() > globalMaxDistance)
				globalMaxDistance = segment.getMaximumDistance();
		}
		
		return globalMaxDistance - globalMinDistance;
	}
	
	
	public String toString()
	{

		return "[" + distanceBarWidth + ", " +  distanceBarHeight + ", " + distanceBarLeft + ", " + distanceBarBottom + ", " + distanceBarRotation + ", " + showText + ", " + segmentsList.toString() + "]";
	}
}
