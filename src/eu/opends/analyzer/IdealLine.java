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
import java.util.ArrayList;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class IdealLine 
{
	public enum IdealLineStatus
	{
		Complete, 					// no way points missing throughout the whole ideal line
		IncompleteBeginning, 		// way points missing at the beginning of ideal line only
		IncompleteEnd,  			// way points missing at the end of ideal line only
		IncompleteBeginningAndEnd,  // way points missing at the beginning and end of ideal line only
		Incomplete, 				// way points missing somewhere after beginning and before end of ideal line
		Unavailable					// all way points missing throughout the whole ideal line
	}

 
	private static final float MAX_DISTANCE = 10.0f; // max. allowed distance from any point on halfway vector
	private static final boolean DEBUGMODE = false;
	
	private String id;
	private float roadWidth = 15.0f; // length of halfway vectors (= max deviation from ideal line)
	private ArrayList<Vector3f> wayPoints;
	private float area = 0.0f; 
	private float length = 0.0f;
	private IdealLineStatus status = IdealLineStatus.Complete;

	private ArrayList<Vector3f> processedIdealPoints = new ArrayList<Vector3f>();
	private ArrayList<Vector3f> deviationPoints = new ArrayList<Vector3f>();
	

	public IdealLine(String id, Float roadWidth, ArrayList<Vector2f> idealPoints, ArrayList<Vector3f> wayPoints) throws Exception
	{
		this.id = id;
		this.wayPoints = wayPoints;
		
		if(roadWidth != null)
			this.roadWidth = roadWidth;
		
		int nrOfIdealPoints = idealPoints.size();

		if(nrOfIdealPoints >= 3)
		{
			// initialize
			Vector2f prevWP = idealPoints.get(0);
			DeviationQuadrangle quadrangle;
			
			// distance between first two ideal points (only added if beginning is available)
			float firstSegmentLength = idealPoints.get(0).distance(idealPoints.get(1));
			
			// compute areas p_0 - p_n-1
			for(int i = 1; i < nrOfIdealPoints-1; i++)
			{				
				// get previous, current and next ideal point
				Vector2f prevIP = idealPoints.get(i-1);
				Vector2f currIP = idealPoints.get(i);
				Vector2f nextIP = idealPoints.get(i+1);
										
				try {
					
					// compute the line which divides the angle at currIP in two equal halves
					Line2D.Float crossLine = getHalfwayVector(prevIP, currIP, nextIP);
					log("Line through IP " + currIP + " from (" + crossLine.getX1() + "," + crossLine.getY1() + ")" +
							" to (" + crossLine.getX2() + "," + crossLine.getY2() + ")");
					
					// get way point on or next to the line
					Vector3f currWP3f = getPointOnLine(crossLine);
					Vector2f currWP = new Vector2f(currWP3f.getX(), currWP3f.getZ());
					log("Point on line: " + currWP);
					
					// compute area of current quadrangle with the given corners
					quadrangle = new DeviationQuadrangle(prevWP, currWP, currIP, prevIP);
					float segmentArea = quadrangle.getArea();
					log("Area of current segment: " + segmentArea);
					
					// sum up all computed areas
					area += segmentArea;
					
					// add distance between current and next ideal point
					length += currIP.distance(nextIP);
					
					// store ideal point with adjusted height information
					// use height value of corresponding way point (only for visualization)
					Vector3f currIP3f = new Vector3f(currIP.getX(),currWP3f.getY(), currIP.getY());
					processedIdealPoints.add(currIP3f);
					
					// add ideal and way point to deviation point list for diagonal lines
					if(i%2==0)
					{
						deviationPoints.add(currIP3f.add(new Vector3f(0,-0.01f,0)));
						deviationPoints.add(currWP3f.add(new Vector3f(0,-0.01f,0)));
					}
					else
					{
						deviationPoints.add(currWP3f.add(new Vector3f(0,-0.01f,0)));
						deviationPoints.add(currIP3f.add(new Vector3f(0,-0.01f,0)));
					}
					
					// store current way point as corner for next quadrangle
					prevWP = currWP;
					
					if(status == IdealLineStatus.IncompleteEnd || status == IdealLineStatus.IncompleteBeginningAndEnd)
					{
						// way points missing somewhere before end of ideal line
						status = IdealLineStatus.Incomplete;
					}
				
				} catch(NotFinishedException e) {
					
					if(status != IdealLineStatus.Incomplete)
					{
						if(processedIdealPoints.size() == 0)
						{
							// way points missing at beginning of ideal line 
							status = IdealLineStatus.IncompleteBeginning;
						}
						else
						{
							if(status == IdealLineStatus.IncompleteBeginning || status == IdealLineStatus.IncompleteBeginningAndEnd)
							{
								// way points missing at beginning and end of ideal line 
								status = IdealLineStatus.IncompleteBeginningAndEnd;
							}
							else
							{
								// way points missing at end of ideal line 
								status = IdealLineStatus.IncompleteEnd;
							}
						}	
					}
					continue;
				}
			}
			
			if(processedIdealPoints.size() == 0)
			{
				// all way points missing throughout the whole ideal line 
				status = IdealLineStatus.Unavailable;
			}
			
			if(status == IdealLineStatus.Complete || status == IdealLineStatus.IncompleteEnd)
				length += firstSegmentLength;
		}
		else
			throw new Exception("Not enough ideal points given!");
	}
	
	
	public String getId() 
	{
		return id;
	}

	public float getArea() 
	{
		return area;
	}
	
	public float getLength() 
	{
		return length;
	}


	public ArrayList<Vector3f> getIdealPoints() 
	{
		return processedIdealPoints;
	}


	public ArrayList<Vector3f> getDeviationPoints() 
	{
		return deviationPoints;
	}
	
	
	public IdealLineStatus getStatus() 
	{
		return status;
	}
	
	
	/**
	 * This method returns a line of given length (see "float roadWidth") 
	 * through point A, dividing the angle between A-->B and A-->C into 
	 * two equal halves.
	 *  
	 * @param B
	 *  	previous ideal point
	 *  
	 * @param A
	 *  	current ideal point
	 *  
	 * @param C
	 * 		next ideal point
	 * 
	 * @return
	 *  	line with start and end point
	 *  
	 * @throws
	 *  	exception if B and C are identical
	*/	
	private Line2D.Float getHalfwayVector(Vector2f B,Vector2f A,Vector2f C) throws Exception
	{
		// compute unit vectors A-->B and A-->C
		Vector2f AB = B.subtract(A).normalize();
		Vector2f AC = C.subtract(A).normalize();
		
		// compute halfway vector
		Vector2f halfwayVector = AB.add(AC);
		
		// if AB and AC direct into opposite directions, halfwayVector will be the
		// zero vector. In this case compute a vector perpendicular to line BC
		if(halfwayVector.equals(new Vector2f(0,0)))
		{
			// compute a vector that is perpendicular to BC
			Vector2f BC = C.subtract(B).normalize();
			float x = BC.getX();
			float y = BC.getY();
			
			if(x != 0)
			{
				halfwayVector = new Vector2f(-y/x, 1);
			}
			else if(y != 0)
			{
				halfwayVector = new Vector2f(1, -x/y);
			}
			else
			{
				// if BC == (0,0) --> B and C are identical
				throw new Exception("Identical ideal points given!");
			}
		}
		
		// scale halfway vector to length "roadWidth"
		halfwayVector.normalizeLocal();
		halfwayVector.multLocal(roadWidth/2);
		
		// get start and end point of line (defined by halfway vector and point A)
		Vector2f startVector = A.add(halfwayVector);
		Vector2f endVector = A.subtract(halfwayVector);
		
		// convert Vector2f to Point2D.Float, as needed for Line2D.Float
		Point2D.Float startPoint = new Point2D.Float(startVector.getX(),startVector.getY());
		Point2D.Float endPoint   = new Point2D.Float(endVector.getX(),endVector.getY());

		// return line of given length through point A, dividing the angle 
		// between A-->B and A-->C into two equal halves
		return new Line2D.Float(startPoint,endPoint);
	}
	
	
	/**
	 * This method returns that point on the given line, which has to be crossed 
	 * in order to connect the nearest left-hand way point with the nearest 
	 * right-hand way point (concerning the line).
	 *  
	 * @param line
	 *  	 line to be checked for crossing point
	 *  
	 * @return
	 *  	point on given line
	 *  
	 * @throws
	 *  	exception if no way points can be found on any side
	*/	
	private Vector3f getPointOnLine(Line2D.Float line) throws Exception
	{		
		// initialization
		Vector3f leftValue = null;
		Vector3f rightValue = null;
		float leftDistance = 0;
		float rightDistance = 0;
		boolean leftValueFound = false;
		boolean rightValueFound = false;
		
		// loop is ended as soon as points on the left and right could be found 
		for(Vector3f wayPoint : wayPoints)
		{
			// get coordinates of current way point
			float x = wayPoint.getX();
			float z = wayPoint.getZ();
			Point2D point = new Point2D.Float(x,z);
			
			// distance of current point from line segment
			double distance = line.ptSegDist(point);
			
			// ignore points, that are located too far away from the line
			if(distance > MAX_DISTANCE)
				continue;

			// if point is already located on the line --> return this point
			if(line.relativeCCW(point) == 0)
			{
				return wayPoint;
			}

			// store distance and coordinates of the nearest point left of the line
			if(line.relativeCCW(point) == -1)
			{
				leftValue = wayPoint;
				leftDistance = (float) line.ptLineDist(point);
				leftValueFound = true;
			}
			
			// store distance and coordinates of the nearest point right of the line
			if(line.relativeCCW(point) == 1)
			{
				rightValue = wayPoint;
				rightDistance = (float) line.ptLineDist(point);
				rightValueFound = true;
			}
			
			// if points on both sides were found --> end loop
			if(leftValueFound && rightValueFound)
				break;
		}
		
		// compute the point in the middle of both points, scaled by their distances  
		// from the line, which results in a point on the given line
		if(leftValueFound && rightValueFound)
		{
			float sumDistance = leftDistance + rightDistance;
			leftValue  =  leftValue.mult(rightDistance/sumDistance);
			rightValue = rightValue.mult(leftDistance/sumDistance);
			
			return leftValue.add(rightValue);
		}
		
		// if no points on or near the line found --> throw exception
		throw new NotFinishedException("No waypoints on both sides of the line");		
	}
	
	
	/**
	 * Writes messages to the console if flag DEBUGMODE is set to true.
	 * 
	 * @param message
	 * 			message to write to console
	 */
	private void log(String message)
	{
		if(DEBUGMODE)
			System.out.println(message);
	}
	
	
	/**
	 * This exception will be thrown if there are not enough 
	 * way points for the given number of ideal points.
	 */
	@SuppressWarnings("serial")
	class NotFinishedException extends Exception
	{
		public NotFinishedException(String message) 
		{
			super(message);
		}	
	}

}
