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

package eu.opends.analyzer;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Vector;

import com.jme3.math.Vector2f;


/**
 * This class represents a quadrangular area between two way points 
 * and two ideal points. It provides a method to calculate this area
 * and a further method to check whether the quadrangular is complex
 * or simple by looking for an intersection of two opposing sides.
 * 
 * @author Rafael Math
 */
public class DeviationQuadrangle 
{
	private Vector<Vector2f> points = new Vector<Vector2f>(4);
	private Vector2f intersection = new Vector2f();
	
	
	/**
	 * Creates a new quadrangle with the given vectors as corners.
	 * 
	 * @param WP0
	 * 			first way point
	 * 
	 * @param WP1
	 * 			second way point
	 * 
	 * @param IP1
	 * 			first ideal point
	 * 
	 * @param IP0
	 * 			second ideal point
	 */
	public DeviationQuadrangle(Vector2f WP0, Vector2f WP1, Vector2f IP1, Vector2f IP0)
	{
		points.add(WP0);
		points.add(WP1);
		points.add(IP1);
		points.add(IP0);
	}
	
	
	/**
	 * Returns the area of the quadrangle. If the quadrangle is a 
	 * complex one, the areas of its two triangles will be summed up.
	 * 
	 * @return
	 * 			Area of the quadrangle
	 */
	public float getArea()
	{
		float area;
		
		// if intersection point exists, variable "intersection" will be set by method "existsIntersection()"
		if(existsIntersection())
		{
			// complex quadrangle --> compute area of two triangles
			
			//System.out.println("Intersection point: " + intersection.toString());
			
			// compute area of triangle WP0-->intersectionPt-->IP0		
			Vector2f iWP0 = points.elementAt(0).subtract(intersection);
			Vector2f iIP0 = points.elementAt(3).subtract(intersection);
			float area1 = 0.5f * Math.abs((iWP0.getX() * iIP0.getY()) - (iWP0.getY() * iIP0.getX()));
			
			// compute area of triangle WP1-->intersectionPt-->IP1
			Vector2f iWP1 = points.elementAt(1).subtract(intersection);
			Vector2f iIP1 = points.elementAt(2).subtract(intersection);
			float area2 = 0.5f * Math.abs((iWP1.getX() * iIP1.getY()) - (iWP1.getY() * iIP1.getX()));
						
			area = area1 + area2;
		}
		else
		{
			// simple quadrangle --> compute area of one quadrangle
			area = 0.5f * Math.abs(
					((points.elementAt(0).getY() - points.elementAt(2).getY()) * 
					 (points.elementAt(3).getX() - points.elementAt(1).getX())) +
					((points.elementAt(1).getY() - points.elementAt(3).getY()) * 
					 (points.elementAt(0).getX() - points.elementAt(2).getX()))
			       );
		}
		
		return area;
	}
	
	
	/**
	 * This method checks whether two way points WP0 and WP1 are positioned 
	 * on opposite sides of the given section of the ideal line IP0-->IP1. 
	 * If an intersection exists, this value will be written to variable 
	 * "intersection".
	 * 
	 * @return
	 * 		True, if two sides of the quadrangle have an intersection point.
	 */
	private boolean existsIntersection()
	{		
		// convert Vector2f to Point2D.Float, as needed for Line2D.Float
		Point2D.Float IP0 = new Point2D.Float(points.elementAt(3).getX(),points.elementAt(3).getY());
		Point2D.Float IP1 = new Point2D.Float(points.elementAt(2).getX(),points.elementAt(2).getY());
		Point2D.Float WP0 = new Point2D.Float(points.elementAt(0).getX(),points.elementAt(0).getY());
		Point2D.Float WP1 = new Point2D.Float(points.elementAt(1).getX(),points.elementAt(1).getY());
		
		// line between IP0 and IP1
		Line2D.Float line = new Line2D.Float(IP0,IP1);

		// position of WP0 and WP1 relative to line IP0-->IP1 (values: -1,0,1)
		int relPosWP0 = line.relativeCCW(WP0);
		int relPosWP1 = line.relativeCCW(WP1);
		
		//System.out.println("relative position: " + relPosWP0 + " / " + relPosWP1);
		
		// if WP0 and WP1 are positioned on opposite sides of ideal line section IP0-->IP1
		if((relPosWP0 == 1 && relPosWP1 == -1) || (relPosWP0 == -1 && relPosWP1 == 1))
		{
			// intersection exists --> compute intersection point
			
			// compute distance of both way points from ideal line
			Vector2f WP0vector = points.elementAt(0);
			Vector2f WP1vector = points.elementAt(1);
			float WP0Distance = (float) line.ptLineDist(WP0);
			float WP1Distance = (float) line.ptLineDist(WP1);
			
			// compute point of intersection
			float sumDistance = WP0Distance + WP1Distance;
			WP0vector = WP0vector.mult(WP1Distance/sumDistance);
			WP1vector = WP1vector.mult(WP0Distance/sumDistance);
			intersection =  WP0vector.add(WP1vector);
			
			return true;
		}
		else
			// no intersection
			return false;
	}
	
}

