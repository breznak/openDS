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

import java.util.List;

import com.jme3.animation.LoopMode;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.math.Spline.SplineType;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;

import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class FollowBox 
{
	private Simulator sim;
	private TrafficObject trafficObject;
	private FollowBoxSettings settings;
	private List<Waypoint> waypointList;
	private float minDistance;
	private float maxDistance;
    private MotionPath motionPath;
    private MotionEvent motionControl;
    private Spatial followBox;
	private float carSpeed = 0;
	private int previousWayPointIndex = 0;
	private int targetWayPointIndex = 0;
	private boolean isTargetWayPointAvailable = false;
	private boolean waitForNextUpdate = true;

	
	public FollowBox(Simulator sim, final TrafficObject trafficObject, FollowBoxSettings settings)
	{
		this.sim = sim;
		this.trafficObject = trafficObject;
		this.settings = settings;
		
		waypointList = settings.getWayPoints();
		minDistance = settings.getMinDistance();
		maxDistance = settings.getMaxDistance();
		
		motionPath = new MotionPath();

		motionPath.setCycle(settings.isPathCyclic());
		
		for(Waypoint wayPoint : waypointList)
			motionPath.addWayPoint(wayPoint.getPosition());

	    motionPath.setPathSplineType(SplineType.CatmullRom); // --> default: CatmullRom
	    motionPath.setCurveTension(settings.getCurveTension());
	    
	    if(settings.isPathVisible())
	    	motionPath.enableDebugShape(sim.getAssetManager(), sim.getSceneNode());

/*
		// does not trigger every way point reliably !!!!!
		// implemented own MotionPath listener in method "checkIfWayPointReached()"
        motionPath.addListener(new MotionPathListener() 
        {
            public void onWayPointReach(MotionEvent control, int wayPointIndex) 
            {
            	// set speed limit for next way point
            	int index = wayPointIndex % waypointList.size();
            	float speed = waypointList.get(index).getSpeed();
            	setSpeed(speed);
            	
            	// if last way point reached
                if (motionPath.getNbWayPoints() == wayPointIndex + 1) 
                {
                	// reset traffic object to first way point if not cyclic
                	if(!motionPath.isCycle())
                	{
                		setToWayPoint(0);
                		System.err.print(", reset");
                	}
                }
                
            }
        });
*/
	    
	    followBox = createFollowBox() ;
	    motionControl = new MotionEvent(followBox,motionPath);
	    
	    // get start way point
	    int startWayPointIndex = settings.getStartWayPointIndex();
	    setToWayPoint(startWayPointIndex);	    
        
        // set start speed
	    float initialSpeed = waypointList.get(startWayPointIndex).getSpeed();
	    setSpeed(initialSpeed);

	    // move object along path considering rotation
        motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
        
        // loop movement of object
        motionControl.setLoopMode(LoopMode.Loop);
        
        // rotate moving object
        //motionControl.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y));
        
        // set moving object to position "20 seconds"
        //motionPath.interpolatePath(20, motionControl, tpf);

        // start movement
        motionControl.play(); // already contained in update method
	}

	
	int counter = 0;
	public void update(Vector3f trafficObjectPos)
	{
		// pause movement of follower box if traffic object's distance
		// has exceeded maximum
		/*
		if(maxDistanceExceeded(trafficObjectPos) || sim.isPause())
			//motionControl.setSpeed(0f);
			motionControl.pause();
		else
			//motionControl.setSpeed(0.01f);
			motionControl.play();
		*/
		
		// skip "else"-part during initialization (first 3 update loops)
		if(sim.isPause() || counter<3)
		{
			motionControl.setSpeed(0f);
			counter++;
		}
		else
		{
			float currentDistance = getCurrentDistance(trafficObjectPos);
			
			//if(trafficObject.getName().equals("car1"))
			//	System.err.println(currentDistance);
			
			// set limits
			currentDistance = Math.max(Math.min(maxDistance, currentDistance), minDistance);

			//maxDistance --> 0
			//minDistance --> 1
			float factor = 1.0f - ((currentDistance-minDistance)/(maxDistance-minDistance));
			motionControl.setSpeed(factor);
		}
		
		// if new WP to set traffic object available, wait for NEXT update and set
		if(isTargetWayPointAvailable && (waitForNextUpdate = !waitForNextUpdate))
		{
			// set traffic object to new position
	        performWayPointChange(targetWayPointIndex);
	        isTargetWayPointAvailable = false;
		}
		
		checkIfWayPointReached();
	}


	private void checkIfWayPointReached() 
	{
		int currentWayPointIndex = motionControl.getCurrentWayPoint() % waypointList.size();
		if(currentWayPointIndex != previousWayPointIndex)
		{
			if(waitAtWP(currentWayPointIndex))
				return;
				
        	// set speed limit for next way point
        	float speed = waypointList.get(currentWayPointIndex).getSpeed();
        	setSpeed(speed);
        	
        	// if last WP reached and path not cyclic --> reset traffic object to first WP
            if (currentWayPointIndex == 0 && !motionPath.isCycle())
            	performWayPointChange(0);
            
            previousWayPointIndex = currentWayPointIndex;
            
            //System.err.println("WP " + currentWayPointIndex + ": " + speed);
		}
	}

	
	boolean isSetWaitTimer = false;
	long waitTimer = 0;
	private boolean waitAtWP(int currentWayPointIndex)
	{
		// get waiting time at upcoming way point (if available)
		Integer waitingTime = waypointList.get(currentWayPointIndex).getWaitingTime();
		
		if(waitingTime == null || waitingTime <= 0)
		{
			// no (or invalid waiting time) --> do not wait
			return false;
		}
		else
		{
			// valid waiting time available
			if(!isSetWaitTimer)
			{
				// waiting timer not yet set --> set timer to current time stamp and wait
				waitTimer = System.currentTimeMillis();
				isSetWaitTimer = true;
				
				motionControl.pause();
				//System.err.println("WAIT");
				
				return true;
			}
			else
			{
				// waiting timer already set --> check if elapsed
				if(System.currentTimeMillis()-waitTimer > waitingTime)
				{
					// waiting timer elapsed --> stop waiting and resume motion
					motionControl.play();
					//System.err.println("RESUME");
					
					isSetWaitTimer = false;
					
					return false;
				}
				else 
				{
					// waiting timer not elapsed --> wait
					return true;
				}
			}
		}
	}


	public void setToWayPoint(int index)
	{
		if(0 <= index && index < waypointList.size())
		{
			targetWayPointIndex = index;
			isTargetWayPointAvailable = true;
		}
		else
			System.err.println("Way point " + index + " does not exist");
	}
	
	
	private void performWayPointChange(int index)
	{
		// set follow box to WP
		float traveledDistance = 0;
        for (int i=0; i<index;i++)
        	traveledDistance += motionPath.getSpline().getSegmentsLength().get(i);
        float traveledTime = (traveledDistance/motionPath.getLength()) * motionControl.getInitialDuration();
        motionControl.setTime(traveledTime);
        
        //System.err.println("SET: dist " + traveledDistance + ", time: " + traveledTime + ", index: " + index);
		
		// set position to traffic object
		Vector3f position = waypointList.get(index).getPosition();
		trafficObject.setPosition(position);
		
		// set heading to traffic object
		float heading = getHeadingAtWP(index);
		Quaternion quaternion = new Quaternion().fromAngles(0, heading, 0);
		trafficObject.setRotation(quaternion);
	}
	
	
	public int getIndexOfWP(String wayPointID) 
	{
		for(int i=0; i<waypointList.size(); i++)
			if(waypointList.get(i).getName().equals(wayPointID))
				return i;
		return -1;
	}

	
	public float getHeadingAtWP(int index) 
	{
		float heading = 0;
		Waypoint nextWayPoint = getNextWayPoint(index);
		
		// if next way point available, compute heading towards it
		if(nextWayPoint != null)
		{
			// compute driving direction by looking at next way point from current position 
			Vector3f targetPosition = nextWayPoint.getPosition().clone();
			targetPosition.setY(0);
			
			Vector3f currentPosition = waypointList.get(index).getPosition().clone();
			currentPosition.setY(0);
			
			Vector3f drivingDirection = targetPosition.subtract(currentPosition).normalize();

			// compute heading (orientation) from driving direction vector for
			// angle between driving direction and heading "0"
			float angle0  = drivingDirection.angleBetween(new Vector3f(0,0,-1));
			// angle between driving direction and heading "90"
			float angle90 = drivingDirection.angleBetween(new Vector3f(1,0,0));
			
			// get all candidates for heading
			// find the value from {heading1,heading2} which matches with one of {heading3,heading4}
			float heading1 = (2.0f * FastMath.PI + angle0)  % FastMath.TWO_PI;
			float heading2 = (2.0f * FastMath.PI - angle0)  % FastMath.TWO_PI;
			float heading3 = (2.5f * FastMath.PI + angle90) % FastMath.TWO_PI;
			float heading4 = (2.5f * FastMath.PI - angle90) % FastMath.TWO_PI;
			
			float diff_1_3 = FastMath.abs(heading1-heading3);
			float diff_1_4 = FastMath.abs(heading1-heading4);
			float diff_2_3 = FastMath.abs(heading2-heading3);
			float diff_2_4 = FastMath.abs(heading2-heading4);
			
			if((diff_1_3 < diff_1_4 && diff_1_3 < diff_2_3 && diff_1_3 < diff_2_4) ||
				(diff_1_4 < diff_1_3 && diff_1_4 < diff_2_3 && diff_1_4 < diff_2_4))
			{
				// if diff_1_3 or diff_1_4 are smallest --> the correct heading is heading1
				heading = heading1;
			}
			else
			{
				// if diff_2_3 or diff_2_4 are smallest --> the correct heading is heading2
				heading = heading2;
			}
		}
		return heading;
	}
	
	
	public Waypoint getPreviousWayPoint() 
	{
		int currentIndex = motionControl.getCurrentWayPoint();
		return getPreviousWayPoint(currentIndex);
	}
	

	public Waypoint getCurrentWayPoint() 
	{
		int currentIndex = motionControl.getCurrentWayPoint();
		return waypointList.get(currentIndex);
	}


	public Waypoint getNextWayPoint() 
	{
		int currentIndex = motionControl.getCurrentWayPoint();
		return getNextWayPoint(currentIndex);
	}
	

	public Waypoint getPreviousWayPoint(int index) 
	{
		Waypoint previousWayPoint = null;
		
		if(motionPath.isCycle())
		{
			// if path is cyclic, the predecessor of the first WP will be the last WP
			previousWayPoint = waypointList.get((index-1+waypointList.size()) % waypointList.size());
		}
		else if(motionPath.getNbWayPoints() > index-1 && index-1 >= 0)
		{
			// if not cyclic, only predecessors for way points 1 .. n exist
			previousWayPoint = waypointList.get(index-1);
		}
		
		return previousWayPoint;
	}
	
	
	public Waypoint getNextWayPoint(int index) 
	{
		Waypoint nextWayPoint = null;
		
		if(motionPath.isCycle())
		{
			// if path is cyclic, the successor of the last WP will be the first WP
			nextWayPoint = waypointList.get((index+1) % waypointList.size());
		}
		else if(motionPath.getNbWayPoints() > index+1 && index+1 >= 0)
		{
			// if not cyclic, only successors for way points 0 .. n-1 exist
			nextWayPoint = waypointList.get(index+1);
		}
		
		return nextWayPoint;
	}
    
	
    public float getSpeed()
    {
    	return carSpeed;
    }

    
    public void setSpeed(float speedKmh)
    {
    	carSpeed = speedKmh;
    	
    	if(getFollowBoxSpeed() < speedKmh + 10)
    		setFollowBoxSpeed(speedKmh + 10);
    }

    
    private float getFollowBoxSpeed()
    {
    	float duration = motionControl.getInitialDuration();
    	float distanceMeters = motionPath.getLength();
    	float speed = distanceMeters / duration;
    	return (3.6f * speed);
    }
    
    
    private void setFollowBoxSpeed(float speedKmh)
    {
    	float distanceMeters = motionPath.getLength();
        float speed = speedKmh / 3.6f;
        float duration = distanceMeters / speed;
        motionControl.setInitialDuration(duration);
    }
    
    
	public Vector3f getPosition() 
	{
		return followBox.getWorldTranslation();
	}


	public MotionEvent getMotionControl() 
	{
		return motionControl;
	}

    
    private Spatial createFollowBox() 
    {
		// add spatial representing the position the driving car is steering towards
		Box box = new Box(1f, 1f, 1f);
		Geometry followBox = new Geometry("followBox", box);
		followBox.setLocalTranslation(0, 0, 0);
		Material followBoxMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		followBoxMaterial.setColor("Color", ColorRGBA.Green);
		followBox.setMaterial(followBoxMaterial);
        followBox.setLocalScale(0.4f);
        sim.getSceneNode().attachChild(followBox);
        
        if(!settings.isPathVisible())
        	followBox.setCullHint(CullHint.Always);
        	
        return followBox;
    }
    

	private float getCurrentDistance(Vector3f trafficObjectPos) 
	{
		// get box's position on xz-plane (ignore y component)
		Vector3f followBoxPosition = getPosition();
		followBoxPosition.setY(0);
		
		// get traffic object's position on xz-plane (ignore y component)
		Vector3f trafficObjectPosition = trafficObjectPos;
		trafficObjectPosition.setY(0);
		
		// distance between box and trafficObject
		float currentDistance = followBoxPosition.distance(trafficObjectPosition);
		return currentDistance;
	}
	
	
    /*
	private boolean maxDistanceExceeded(Vector3f trafficObjectPos) 
	{
		float currentDistance = getCurrentDistance(trafficObjectPos);
		
		// report whether maximum distance is exceeded 
		return currentDistance > maxDistance;
	}
     */

	
	public float getReducedSpeed()
	{
		// return a temporarily reduced speed for the traffic car
		// in order to reach next (lower) speed limit in time
		float reducedSpeedInKmh = Float.POSITIVE_INFINITY;
		
		// if next way point with lower speed comes closer --> reduce speed
		int currentIndex = motionControl.getCurrentWayPoint();
		Waypoint nextWP = getNextWayPoint(currentIndex);
		if(nextWP != null)
		{
			// current way point (already passed)
			Waypoint curentWP = waypointList.get(currentIndex);
			
			// speed at current way point
			float currentSpeedInKmh = curentWP.getSpeed();
			float currentSpeed = currentSpeedInKmh / 3.6f;
			
			// speed at next way point
			float targetSpeedInKmh = nextWP.getSpeed();
			float targetSpeed = targetSpeedInKmh / 3.6f;
			
			// if speed at the next WP is lower than at the current WP --> brake traffic object
			if(targetSpeed < currentSpeed)
			{
				// % of traveled distance between current and next way point
				float wayPercentage = motionControl.getCurrentValue();
				
				// distance between current and next way point
				Vector3f currentPos = curentWP.getPosition().clone();
				currentPos.setY(0);
				Vector3f nextPos = nextWP.getPosition().clone();
				nextPos.setY(0);
				float distance = currentPos.distance(nextPos);
				
				// distance (in meters) between follow box and next way point
				float distanceToNextWP = (1 - wayPercentage) * distance;
			
				// speed difference in m/s between current WP's speed and next WP's speed
				float speedDifference = currentSpeed - targetSpeed;
				
				// compute the distance in front of the next WP at what the traffic object has to start 
				// braking with 50% brake force in order to reach the next WP's (lower) speed in time.
				float deceleration50Percent = 50f * trafficObject.getMaxBrakeForce()/trafficObject.getMass();
				
				// time in seconds needed for braking process
				float time = speedDifference / deceleration50Percent;
				
				// distance covered during braking process
				float coveredDistance = 0.5f * -deceleration50Percent * time * time + currentSpeed * time;

				// start braking in x meters
				float distanceToBrakingPoint = distanceToNextWP - coveredDistance;
				
				if(distanceToBrakingPoint < 0)
				{
					// reduce speed linearly beginning from braking point
					
					// % of traveled distance between braking point and next way point
					float speedPercentage = -distanceToBrakingPoint/coveredDistance;
					
					//   0% traveled: reduced speed = currentSpeed
					//  50% traveled: reduced speed = (currentSpeed+targetSpeed)/2
					// 100% traveled: reduced speed = targetSpeed
					float reducedSpeed = currentSpeed - (speedPercentage * speedDifference);
					reducedSpeedInKmh = reducedSpeed * 3.6f;
					
					/*
					if(trafficObject.getName().equals("car1"))
					{
						float trafficObjectSpeedInKmh = trafficObject.getLinearSpeedInKmh();
						System.out.println(curentWP.getName() + " : " + speedPercentage + " : " + 
								reducedSpeedInKmh + " : " + trafficObjectSpeedInKmh + " : " + targetSpeedInKmh);
					}
					*/
				}
			}
		}
		return reducedSpeedInKmh;
	}

}
