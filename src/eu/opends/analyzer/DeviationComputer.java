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

package eu.opends.analyzer;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;


/**
 * This class computes the deviation of a car's driven track 
 * from a given ideal line. Both (track and ideal line) are 
 * considered as set of two-dimensional points.  
 * 
 * @author Rafael Math
*/	
public class DeviationComputer 
{
	private Vector<Vector2f> idealPoints;
	private Vector<Vector3f> processedIdealPoints;
	private Vector<Vector3f> wayPoints;
	private Vector<Vector3f> deviationPoints;
	private float roadWidth;
	private static final double MAX_DISTANCE = 30.0;
	
	private static final float MAX_DISTANCE_BETWEEN_TWO_IDEAL_POINTS = 0.1f;
	private static final boolean DEBUGMODE = false;
	private SortedMap<String, Vector2f> idealPointMap = new TreeMap<String, Vector2f>();
		
	
	/**
	 * Creates a new deviation computer and sets the width of the road,
	 * i.e. a way point's maximum distance from the ideal line to be 
	 * taken into account (road width = 1/2 distance) and the distance 
	 * of the way point line from ground
	 * 
	 * @param roadWidth
	 * 			Maximum distance of way points to be considered	(road 
	 * 			width = 1/2 distance)
	 */
	public DeviationComputer(float roadWidth)
	{
		idealPoints = new Vector<Vector2f>(100);
		processedIdealPoints = new Vector<Vector3f>(100);
		wayPoints   = new Vector<Vector3f>(100);
		deviationPoints = new Vector<Vector3f>(100);
		this.roadWidth = roadWidth;
	}
	
	
	/**
	 * Adds an ideal point to the preprocessor list. By calling 
	 * processIdealPoints(), this sorted list will be added to the
	 * ideal point list. Used to sort ideal points by their name.
	 * 
	 * @param name
	 * 			Name of the ideal point (for sorting).
	 * 
	 * @param currentIdealPoint
	 * 			Ideal point's coordinates
	 */
	public void addIdealPointToPreprocessor(String name, Vector2f currentIdealPoint)
	{
		if(currentIdealPoint != null)
		{
			idealPointMap.put(name, currentIdealPoint);
		}
	}
	
	
	/**
	 * Copies the sorted ideal points to the ideal points list.
	 */
	public void processIdealPoints()
	{
		for(String key : idealPointMap.keySet())
		{
			addIdealPoint(idealPointMap.get(key));
	    }	
	}

	
	/**
	 * Adds an ideal point to the list. Order is significant for 
	 * computation. If distance to previous ideal point is too large,
	 * further ideal points will be added in between. This allows
	 * a better approximation of the deviation and its visualization.
	 * 
	 * @param currentIdealPoint
	 * 			ideal point
	 */
	public void addIdealPoint(Vector2f currentIdealPoint)
	{
		if(currentIdealPoint != null)
		{
			// If the distance between two ideal points is too large, add (an) additional 
			// ideal point(s) between them in order to allow a better approximation

			// exclude first ideal point which has no predecessor 
			if(!idealPoints.isEmpty())
			{
				// if distance to previous ideal point is too large, add (a) new ideal point(s)
				Vector2f previousIdealPoint = idealPoints.lastElement();
				while(previousIdealPoint.distance(currentIdealPoint) > MAX_DISTANCE_BETWEEN_TWO_IDEAL_POINTS)
				{
					previousIdealPoint = createIdealPoint(previousIdealPoint, currentIdealPoint);
					idealPoints.add(previousIdealPoint);
				}
			}

			// add current ideal point to list
			idealPoints.add(currentIdealPoint);
		}
	}


	/**
	 * Writes all given ideal points to the console
	 */
	public void showAllIdealPoints()
	{
		for(Vector2f idealPoint : idealPoints) 
			System.out.println("IP: "+idealPoint.toString());
	}
	
	
	/**
	 * Returns the list of all given ideal points as 3-dimensional 
	 * vectors (adding height to internal 2-dimensional representation)
	 * 
	 * @return
	 * 			list of ideal points
	 */
	public List<Vector3f> getIdealPoints()
	{
		return processedIdealPoints;
	}
	
	
	/**
	 * Computes the length of the given ideal line by adding the
	 * distances between all neighbored ideal point pairs.
	 * 
	 * @return
	 * 			distance from first to last ideal point passing
	 * 			all intermediate ideal points
	 */
	public float getLengthOfIdealLine()
	{
		float length = 0;
		for(int i=0; i<idealPoints.size()-1; i++)
		{
			Vector2f idealPointA = idealPoints.get(i);
			Vector2f idealPointB = idealPoints.get(i+1);
			
			length += idealPointA.distance(idealPointB);
		}
		return length;
	}
	
	
	/**
	 * Adds a way point to the list. Order is significant for 
	 * computation.
	 * 
	 * @param point
	 * 			way point
	 */
	public void addWayPoint(Vector3f point)
	{
		if(point != null)
			wayPoints.add(point);
	}
	
	
	/**
	 * Writes all given way points to the console
	 */
	public void showAllWayPoints()
	{
		for(Vector3f wayPoint : wayPoints) 
			System.out.println("WP: "+wayPoint.toString());
	}
	
	
	/**
	 * Returns the list of all given way points as 3-dimensional 
	 * vectors
	 * 
	 * @return
	 * 			list of way points
	 */
	public List<Vector3f> getWayPoints()
	{
		return wayPoints;
	}
	
	
	/**
	 * Returns the list of deviation points, i.e. ideal point / 
	 * way point pairs used to draw diagonal lines between ideal
	 * and driven line.
	 * 
	 * @return
	 * 			list of points, outlining the diagonal line
	 */
	public List<Vector3f> getDeviationPoints()
	{
		return deviationPoints;
	}
	
	
	/**
	 * Writes messages to the console if flag DEBUGMODE is set to true.
	 * 
	 * @param message
	 * 			message to write to console
	 */
	public void log(String message)
	{
		if(DEBUGMODE)
			System.out.println(message);
	}
	
	
	/**
	 * This method returns the area between ideal line and driven line by 
	 * splitting up the inner region into quadrangles and triangles.
	 *  
	 * @return
	 *  	Area between ideal line and driven line
	 *  
	 * @throws Exception 
	 * 		If less than 3 ideal points are given.
	*/	
	public float getDeviation() throws Exception
	{
		int nrOfIdealPoints = idealPoints.size();

		if(nrOfIdealPoints >= 3)
		{
			// initialize
			Vector2f prevWP = idealPoints.elementAt(0);
			DeviationQuadrangle quadrangle;
			float area,sum = 0.0f;
			
			// compute areas p_0 - p_n-1
			for(int i = 1; i < nrOfIdealPoints-1; i++)
			{
				// get previous, current and next ideal point
				Vector2f prevIP = idealPoints.elementAt(i-1);
				Vector2f currIP = idealPoints.elementAt(i);
				Vector2f nextIP = idealPoints.elementAt(i+1);
			
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
				area = quadrangle.getArea();
				log("Area: " + area);
				
				// sum up all computed areas
				sum += area;
				
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
			}
			return sum;
		}
		else
			throw new Exception("Not enough ideal points given!");
	}


	/**
	 * Computes a point on the ideal line between previous and current 
	 * ideal point with distance "MAX_DISTANCE_BETWEEN_TWO_IDEAL_POINTS"
	 * from previous ideal point towards current ideal point.
	 * 
	 * @param previousIdealPoint
	 * 			Previous ideal point. The new point will have the distance
	 * 			specified in "MAX_DISTANCE_BETWEEN_TWO_IDEAL_POINTS" from
	 * 			this point.
	 * 
	 * @param currentIdealPoint
	 * 			Current ideal point.
	 * 
	 * @return
	 * 			New point on ideal line (between previousIdealPoint and 
	 * 			currentIdealPoint) with distance MAX_DISTANCE_BETWEEN_TWO_IDEAL_POINTS
	 * 			from previousIdealPoint.
	 */
	private Vector2f createIdealPoint(Vector2f previousIdealPoint,	Vector2f currentIdealPoint)
	{
		// difference in x- and y-coordinates between previous and current ideal point
		float diffX = currentIdealPoint.x - previousIdealPoint.x;
		float diffY = currentIdealPoint.y - previousIdealPoint.y;
		
		// square distance and difference values for lambda computation
		float distanceSquare = FastMath.sqr(MAX_DISTANCE_BETWEEN_TWO_IDEAL_POINTS);
		float diffXSquare = FastMath.sqr(diffX);
		float diffYSquare = FastMath.sqr(diffY);
		
		// lambda is the factor (between 0 and 1) indicating the new point's position 
		// between previous and current ideal point:
		// lambda = 0   --> new point has same position as previous ideal point
		// lambda = 0.5 --> new point in the middle between previous and current ideal point
		// lambda = 1   --> new point has same position as current ideal point
		float lambda = FastMath.sqrt(distanceSquare/(diffXSquare + diffYSquare));
		
		// compute new point's x- and y-coordinates from the previous ideal point's coordinates
		Vector2f newIdealPoint = new Vector2f();
		newIdealPoint.x = lambda * diffX + previousIdealPoint.x;
		newIdealPoint.y = lambda * diffY + previousIdealPoint.y;
		
		return newIdealPoint;
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

	
/*	
 	// Test with given way and ideal points
	public static void main(String[] args)
	{
		DeviationComputer devComp = new DeviationComputer(5.0f,-0.30f);
		
		devComp.addIdealPoint(new Vector2f(3.5f,1));
		devComp.addIdealPoint(new Vector2f(3.5f,3.5f));
		devComp.addIdealPoint(new Vector2f(7,3.5f));
		devComp.addIdealPoint(new Vector2f(7,8));
		devComp.addIdealPoint(new Vector2f(9.5f,10.5f));
		devComp.addIdealPoint(new Vector2f(6,14));
		//devComp.showAllIdealPoints();
		
		devComp.addWayPoint(new Vector2f(2.5f,1.5f));
		devComp.addWayPoint(new Vector2f(2.5f,2.5f));
		devComp.addWayPoint(new Vector2f(3,3.5f));
		devComp.addWayPoint(new Vector2f(3,4.5f));
		devComp.addWayPoint(new Vector2f(4,4.5f));
		devComp.addWayPoint(new Vector2f(5.5f,4));
		devComp.addWayPoint(new Vector2f(5.5f,5));
		devComp.addWayPoint(new Vector2f(6.5f,5));
		devComp.addWayPoint(new Vector2f(6.5f,6));
		
		//devComp.addWayPoint(new Vector2f(6.5f,7.5f));
		//devComp.addWayPoint(new Vector2f(7,8.5f));
		
		devComp.addWayPoint(new Vector2f(8.5f,7));
		devComp.addWayPoint(new Vector2f(9,7.5f));
		
		
		devComp.addWayPoint(new Vector2f(7.5f,9.5f));
		devComp.addWayPoint(new Vector2f(8.5f,10));
		devComp.addWayPoint(new Vector2f(8.5f,11));
		//devComp.showAllWayPoints();
		
		try {
			float area = devComp.getDeviation();
			System.out.println("TOTAL: " + area);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
*/
	
}


/**
 * This exception will be thrown if there are not enough 
 * way points for the given number of ideal points.
 */
@SuppressWarnings("serial")
class NotFinishedException extends Exception
{
	public NotFinishedException(String message) {
		super(message);
	}	
}


