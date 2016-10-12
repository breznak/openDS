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

package eu.opends.hmi;


import eu.opends.car.Car;
import eu.opends.environment.TrafficLight;
import eu.opends.environment.TrafficLightCenter;
import eu.opends.environment.TrafficLightInternalProgram;
import eu.opends.environment.TrafficLightException.*;
import eu.opends.main.Simulator;


/**
 * This class represents the presentation of a traffic light phase assistant. The 
 * distance between the moving car and a traffic light is measured in order to update 
 * the recommended speed information in the presentation. When red, the time of the 
 * remaining red will be computed from the traffic light data.
 * 
 * @author Rafael Math
 */
public class TrafficLightPresentationModel extends PresentationModel 
{
	private Simulator sim;
	
	// maximum value for recommended speed to be sent to the HMI GUI
	private static final int maxRecommendedSpeed = 70;
	
	// traffic light whose trigger was hit
	private TrafficLight triggeredTrafficLight;
	
	// triggered traffic light and neighbors by lanes
	private TrafficLight trafficLight0;
	private TrafficLight trafficLight1;
	private TrafficLight trafficLight2;
	
	// computed traffic light info of previous and current measurement
	// values can be negative, zero or positive:
	// -15 --> traffic light will be red for 15 seconds
	//   0 --> traffic light does not exist
	//  50 --> recommended speed 50, to pass traffic light when green
	private int previousInfoTrafficLight0;
	private int previousInfoTrafficLight1;
	private int previousInfoTrafficLight2;
	private int currentInfoTrafficLight0;
	private int currentInfoTrafficLight1;
	private int currentInfoTrafficLight2;
	
	
	/**
	 * Initializes a traffic light phase assistant presentation model by setting the 
	 * positions of the car and the traffic light, the minimum distance from the 
	 * traffic light to cancel the presentation and the triggered traffic light.
	 * 
	 * @param sim 
	 * 			Simulator
	 * 
	 * @param car
	 * 			Car heading toward traffic light
	 * 
	 * @param triggeredTrafficLight
	 * 			Traffic light whose trigger was hit
	 */
	public TrafficLightPresentationModel(Simulator sim, Car car, TrafficLight triggeredTrafficLight)
	{
		this.sim = sim;
		this.car = car;
		this.targetPosition = triggeredTrafficLight.getLocalPosition();
		this.minimumDistance = 5;
		this.triggeredTrafficLight = triggeredTrafficLight;

		try{
			
			// read the positioning data of the triggered traffic light
			String roadID = triggeredTrafficLight.getPositionData().getRoadID();
			String intersectionID = triggeredTrafficLight.getIntersectionID();
			
			// if positioning data of the triggered traffic light exists, request its neighbors
			TrafficLightCenter trafficLightCenter = sim.getTrafficLightCenter();
			this.trafficLight0 = trafficLightCenter.getTrafficLightByLocation(intersectionID, roadID, 0);
			this.trafficLight1 = trafficLightCenter.getTrafficLightByLocation(intersectionID, roadID, 1);
			this.trafficLight2 = trafficLightCenter.getTrafficLightByLocation(intersectionID, roadID, 2);
		
		} catch(NullPointerException e) {}
	}
	
	
	/**
	 * Computes the traffic light info value for the given traffic light and 
	 * maximum recommended speed value. Results and their meaning:<br>
	 * -15 --> traffic light will be red for 15 seconds<br>
	 *   0 --> traffic light does not exist<br>
	 *  50 --> recommended speed 50, to pass traffic light when green
	 * 
	 * @param trafficLight
	 * 			Traffic light to compute remaining red or recommended speed for
	 * 
	 * @param maxRecommendedSpeed
	 * 			Maximum value the recommended speed may have
	 * 
	 * @return
	 * 			Traffic light info value
	 */
	private int getTrafficLightInfo(TrafficLight trafficLight, int maxRecommendedSpeed) 
	{
		// abort, if traffic light does not exist
		if(trafficLight == null)
			return 0;

		TrafficLightInternalProgram internalProgram = null;
		
		try 
		{
			// request remaining red from the traffic light's internal program
			internalProgram = sim.getTrafficLightCenter().getInternalProgram(trafficLight);
			int remainingRed = internalProgram.getRemainingRed(trafficLight);
			return -remainingRed;
		}
		catch (NoInternalProgramException e) 
		{
			// no internal program --> allows no prediction of a remaining red phase
			return 0;
		}
		catch (NeverGreenException e)
		{
			// traffic light will never be green --> return maximum remaining red time (300 s)
			return -300;
		}
		catch (IsGreenException e)
		{
			// traffic light is currently green --> request remaining green to compute 
			// recommended speed
			try 
			{
				// request remaining green from the traffic light's internal program
				int remainingGreen = internalProgram.getRemainingGreen(trafficLight);
				
				// avoid division by zero
				if(remainingGreen > 0)
				{
					// distance between the car and the green traffic light 
					float distance = getExactDistanceToTarget(targetPosition);
					
					// compute minimum speed (in km/h) to pass the traffic light when still green
					// speed = distance/time
					float minimumSpeed = (distance/(float)remainingGreen) * 3.6f;
					
					// if the maximum recommended speed does not have to be exceeded in 
					// order to pass the traffic light when still green
					if(minimumSpeed <= maxRecommendedSpeed)
					{
						// rounds minimum speed to a multiple of 10
						int roundedSpeed = Math.round(minimumSpeed/10f)*10;
						
						// return at least a recommended speed of 10 km/h
						return Math.max(10, roundedSpeed);
					}
					else
						return 0;
				}
				else
					return 0;
			}
			catch (AlwaysGreenException e2) 
			{
				// traffic light is always green --> return recommended speed 50 km/h
				return 50;
			}
			catch (IsNotGreenException e2) 
			{
				// traffic light is currently not green (and not red also)
				return 0;
			}
		}
	}
	
	
	/**
	 * Sets previous parameters relevant for updates
	 */
	@Override
	public void computePreviousParameters() 
	{
		super.computePreviousParameters();
		previousInfoTrafficLight0 = getTrafficLightInfo(trafficLight0, maxRecommendedSpeed);
		previousInfoTrafficLight1 = getTrafficLightInfo(trafficLight1, maxRecommendedSpeed);
		previousInfoTrafficLight2 = getTrafficLightInfo(trafficLight2, maxRecommendedSpeed);
	}
	
	
	/**
	 * Sets current parameters relevant for updates
	 */
	@Override
	public void computeCurrentParameters() 
	{
		super.computeCurrentParameters();
		currentInfoTrafficLight0 = getTrafficLightInfo(trafficLight0, maxRecommendedSpeed);
		currentInfoTrafficLight1 = getTrafficLightInfo(trafficLight1, maxRecommendedSpeed);
		currentInfoTrafficLight2 = getTrafficLightInfo(trafficLight2, maxRecommendedSpeed);
	}
	
	
	/**
	 * Compares previous to current parameters
	 * 
	 * @return
	 * 			True, if any previous parameter differs from the corresponding 
	 * 			current parameter
	 */
	@Override
	public boolean hasChangedParameter() 
	{
		return (   
				   super.hasChangedParameter() 
				|| (currentInfoTrafficLight0 != previousInfoTrafficLight0) 
				|| (currentInfoTrafficLight1 != previousInfoTrafficLight1) 
				|| (currentInfoTrafficLight2 != previousInfoTrafficLight2)
				);
	}
	
	
	/**
	 * Assign the values of all current parameters to the corresponding previous 
	 * parameters
	 */
	@Override
	public void shiftCurrentToPreviousParameters()
	{
		super.shiftCurrentToPreviousParameters();
		previousInfoTrafficLight0 = currentInfoTrafficLight0;
		previousInfoTrafficLight1 = currentInfoTrafficLight1;
		previousInfoTrafficLight2 = currentInfoTrafficLight2;
	}

	
	/**
	 * Creates a traffic light phase assistant presentation task on the HMI GUI. 
	 * This data will only be sent to the HMI bundle once.
	 * 
	 * @return
	 * 			Presentation ID. If an error occurred, a negative value will be returned.
	 */
	@Override
	public long createPresentation() 
	{
		// check if positioning data can be found (external file) for this traffic light
		if(triggeredTrafficLight.getPositionData() != null)
		{		
			// compute parameters
			int arrowConfiguration = triggeredTrafficLight.getPositionData().getArrowType();
			int infoTrafficLight0 = getTrafficLightInfo(trafficLight0, maxRecommendedSpeed);
			int infoTrafficLight1 = getTrafficLightInfo(trafficLight1, maxRecommendedSpeed);
			int infoTrafficLight2 = getTrafficLightInfo(trafficLight2, maxRecommendedSpeed);
			
			// send parameters to HMI bundle
			sendTrafficLightData("start", 10, System.currentTimeMillis(), null, arrowConfiguration, infoTrafficLight0, infoTrafficLight1 , infoTrafficLight2, false);
System.err.println("START");
			return 7;
		}
		else
			return -1;
	}
	
	
	/**
	 * Updates a traffic light phase assistant presentation model if the time the car 
	 * takes to arrive at the traffic light has changed or if the traffic light info
	 * of any traffic light has changed.
	 */
	@Override
	public void updatePresentation(long presentationID) 
	{
		if(presentationID >= 0)
		{			
			// send parameters to HMI bundle
			sendTrafficLightData("update", 10, System.currentTimeMillis(), null, null, currentInfoTrafficLight0, currentInfoTrafficLight1 , currentInfoTrafficLight2, false);
			System.err.println("UPDATE");
		}
	}

	
	/**
	 * Generates a message containing the traffic light warning and distance to it.
	 */
	@Override
	public String generateMessage()
	{
		return "Traffic light in "+ getRoundedDistanceToTarget(targetPosition) + " m";
	}
	
	
	private void sendTrafficLightData(String command, Integer priority, Long timestamp, String infoText, Integer arrowConfiguration, 
			Integer infoTrafficLight1, Integer infoTrafficLight2, Integer infoTrafficLight3, Boolean greenArrow) 
	{
		String message = "<presentation>" +
							"<trafficLightAssistant id=\"trafficLightAssistant\">";
				
		if(command != null)
			message += 			"<command>" + command + "</command>";
		
		if(priority != null)
			message += 			"<priority>" + priority + "</priority>";
		
		if(timestamp != null)
			message += 			"<timestamp>" + timestamp + "</timestamp>";
		
		if(infoText != null)
			message += 			"<infoText>" + infoText + "</infoText>";
		
		if(arrowConfiguration != null)
			message += 			"<crossing>" + getCrossingType(arrowConfiguration) + "</crossing>";
		
		if(infoTrafficLight1 != null && infoTrafficLight1 != 0)
			message += 			"<infoTrafficLight id=\"1\">" + infoTrafficLight1 + "</infoTrafficLight>";
		
		if(infoTrafficLight2 != null && infoTrafficLight2 != 0)
			message += 			"<infoTrafficLight id=\"2\">" + infoTrafficLight2 + "</infoTrafficLight>";
		
		if(infoTrafficLight3 != null && infoTrafficLight3 != 0)
			message += 			"<infoTrafficLight id=\"3\">" + infoTrafficLight3 + "</infoTrafficLight>";
		
		if(arrowConfiguration != null)
		{
			String[] arrowCrossing = getArrowCrossing(arrowConfiguration);
			message += 			"<arrowCrossing id=\"1\">" + arrowCrossing[0] + "</arrowCrossing>" +
								"<arrowCrossing id=\"2\">" + arrowCrossing[1] + "</arrowCrossing>" +
								"<arrowCrossing id=\"3\">" + arrowCrossing[2] + "</arrowCrossing>";
		}
		
		if(greenArrow != null)
			message += 			"<greenArrow>" + greenArrow + "</greenArrow>";
		
		message +=			"</trafficLightAssistant>" +
						"</presentation>";	
						
		HMICenter.sendMsg(message);
	}
	
		
	private String[] getArrowCrossing(Integer arrowConfiguration)
	{
		switch (arrowConfiguration)
		{
			case 3 : return new String[] {"000", "010", "100"};
			case 4 : return new String[] {"001", "000", "100"};
			case 5 : return new String[] {"001", "010", "000"};
			case 9 : return new String[] {"000", "011", "100"};
			default : return new String[] {"000", "011", "100"};
		}
	}


	private String getCrossingType(Integer arrowConfiguration) 
	{
		switch (arrowConfiguration)
		{
			case 3 : return "leftT";
			case 4 : return "straightT";
			case 5 : return "rightT";
			case 9 : return "X";
			default : return "X";
		}
	}


	@Override
	public void stop()
	{
		sendTrafficLightData("stop", 10, System.currentTimeMillis(), null, null, null, null , null, false);
		System.err.println("STOP");
	}
}
