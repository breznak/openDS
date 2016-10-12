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

package eu.opends.knowledgeBase;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import de.dfki.automotive.kapcom.knowledgebase.KAPcomException;
import de.dfki.automotive.kapcom.knowledgebase.ontology.*;
import eu.opends.analyzer.DataUnit;
import eu.opends.car.SteeringCar;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.DriveAnalyzer;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;
import eu.opends.tools.Vector3d;

/**
 * 
 * @author Michael Feld, Rafael Math
 */
public class VehicleKnowledge 
{
	private KnowledgeBase kb;
	private Vehicle vehicle = null;
	//private VehiclePhysicalAttributes carPhys = null;
	//private Engine engine = null;

	VehicleKnowledge(KnowledgeBase kb) 
	{
		this.kb = kb;
		if (kb.isConnected()) {
			// get current vehicle
			try {
				vehicle = kb.getRoot().thisVehicle();
				//carPhys = vehicle.getphysicalAttributes(true);
				//engine = vehicle.getexterior(true).getengineCompartment(true).getengine(true);
			} catch (Exception e) {
				System.err.println("Failed to determine current vehicle instance in knowledge base.");
			}
		} else {
		}
		InitModel();
	}
	
	private void InitModel()
	{
		if (vehicle != null) {
			// ...
		}
	}

	
	private float oldOrientation = 0;
	private float oldRotation = 0;
	private double oldAltitude = 0;
	private double oldRise = 0;
	private float oldSpeed = 0;
	private long oldTime = 0;
	void sendCarData(Simulator sim) throws KAPcomException
	{
		SteeringCar car = sim.getCar();
		
		if (vehicle == null) return;
		
		/*
		Vector3f position = car.getPosition();
		float speed = car.getLinearSpeedInKmhRounded();  // in kph
		float heading = car.getHeadingDegree();       // 0..360 degree
		Vector3f geoPosition = car.getGeoPosition();
		float latitude = geoPosition.getX();          // N-S position in model coordinates
		float longitude = geoPosition.getY();         // W-E position in model coordinates
		//carPhys.setLocation(longitude + ";" + latitude);
		carPhys.setScenarioLocation(position.getX() + ";" + position.getY() + ";" + position.getZ());
		carPhys.setOrientation((double) heading);
		//engine.setActualSpeed((double) speed);
		*/
		
		long time = System.currentTimeMillis();  // in milliseconds
		float timeDiff = ((float) (time - oldTime)) / 1000f; // in seconds

		Vector3d geoPosition = car.getGeoPosition();
		double latitude = geoPosition.getX();  // N-S position in geo coordinates
		double longitude = geoPosition.getY(); // W-E position in geo coordinates
		double altitude = geoPosition.getZ();  // meters above sea level
		
		float orientation = car.getHeadingDegree();  // 0..360 degree
		
		float rotation = (orientation - oldOrientation)/timeDiff; // in degree/s
		if(rotation < -180)
			rotation += 360;
		else if(rotation > 180)
			rotation -= 360;
		
		float rotationAcceleration = (rotation - oldRotation)/timeDiff; // in degree/s^2
		
		float speed = FastMath.abs(car.getCarControl().getCurrentVehicleSpeedKmHour());  // in Km/h
		
		double rise = (altitude - oldAltitude)/timeDiff; // in m/s
		
		double verticalAcceleration = (rise - oldRise)/timeDiff;  // in m/s^2
		
		float acceleration = ((speed - oldSpeed)/3.6f)/timeDiff; // in m/s^2
		
		float gasPedalPress = car.getAcceleratorPedalIntensity(); // in %
		float brakePedalPress = car.getBrakePedalIntensity(); // in %
		
		float maxSteeringAngle = Simulator.getDrivingTask().getSettingsLoader().getSetting(
				Setting.CANInterface_maxSteeringAngle, SimulationDefaults.CANInterface_maxSteeringAngle);
		
		float steeringAngle = -maxSteeringAngle*car.getSteeringWheelState(); // in degree (+ = right, - = left)

		String lightState = car.getLightState();
		
		float fuelConsumption = car.getPowerTrain().getLitersPer100Km();  // current fuel consumption
		
		float maxFuelCapacity = 60; //TODO set max Capacity
		float fuelLeft = maxFuelCapacity - car.getPowerTrain().getTotalFuelConsumption();
		
		int selectedGear = car.getTransmission().getGear();
		
		int engineOn;
		if(car.isEngineOn())
			engineOn = 1;
		else
			engineOn = 0;
		
		int rpm = (int) car.getTransmission().getRPM();
		
		String xml = "<root>" +
						"<thisVehicle>" +
							"<interior>" +
								"<cockpit>" +
									"<pedals>" +
										"<gasPedal>" +
											"<Properties><pressedState>" + gasPedalPress + "</pressedState></Properties>" +
										"</gasPedal>" +
										"<brakePedal>" +
											"<Properties><pressedState>" + brakePedalPress + "</pressedState></Properties>" +
										"</brakePedal>" +
									"</pedals>" +
									"<steeringWheel>" +
										"<Properties><steerAngle>" + steeringAngle + "</steerAngle></Properties>" +
									"</steeringWheel>" +
								"</cockpit>" +
							"</interior>" +
							"<exterior>" +
								"<lights>" +
									"<Properties><headlights>" + lightState + "</headlights></Properties>" +
								"</lights>" +
								"<gearUnit>" +
									"<Properties><currentGear>" + selectedGear + "</currentGear></Properties>" +
								"</gearUnit>" +
								"<engineCompartment>" +
									"<engine><Properties>" +
										"<running>" + engineOn + "</running>" +
										"<actualRpm>" + rpm + "</actualRpm>" +
									"</Properties></engine>" +
								"</engineCompartment>" +
								"<fueling>" +
									"<fuelType>" +
										"<Properties><currentConsumption>" + fuelConsumption + "</currentConsumption></Properties>" +
										"<tank><Properties>" +
											"<maxAmount>" + maxFuelCapacity + "</maxAmount>" +
											"<actualAmount>" + fuelLeft + "</actualAmount>" +
										"</Properties></tank>" +
									"</fuelType>" +
								"</fueling>";

		
		if(sim.getThreeVehiclePlatoonTask() != null)
		{
			Float distanceFromLaneCenter = sim.getThreeVehiclePlatoonTask().getDistanceFromLaneCenter();
			if(distanceFromLaneCenter != null)
			{
				xml +=          "<sensors>" +
								    "<deviationSensor>" +
								    	"<Properties><name>deviationSensor #1</name></Properties>" +
								        "<sensorData>" +
								            "<Properties>" +
							            		"<sensorType>Environmental</sensorType>" +	
							            		"<sensorSubType>Deviation</sensorSubType>" +
								            	"<distanceX>" + distanceFromLaneCenter + "</distanceX>" +
								            "</Properties>" +
								        "</sensorData>" +
								    "</deviationSensor>" +
								"</sensors>";
			}	
		}

		xml +=              "</exterior>" +
							"<physicalAttributes><Properties>" +
								"<latitude>"+latitude+"</latitude>" +
								"<longitude>"+longitude+"</longitude>" +
								"<altitude>"+altitude+"</altitude>" +
								"<orientation>"+orientation+"</orientation>" +
								"<speed>"+ speed +"</speed>" +
								"<rise>"+rise+"</rise>" +
								"<accelerationLateral>"+verticalAcceleration+"</accelerationLateral>" +
								"<rotation>"+rotation+"</rotation>" +
								"<accelerationRotation>"+rotationAcceleration+"</accelerationRotation>" +
								"<acceleration>"+acceleration+"</acceleration>" +
							"</Properties></physicalAttributes>" +
						"</thisVehicle>" +
					"</root>";


		kb.getClient().sendAddInstanceXml("", xml);		
		
		//System.out.println(timeDiff);
		
		oldOrientation = orientation;
		oldRotation = rotation;
		oldAltitude = altitude;
		oldRise = rise;
		oldSpeed = speed;
		oldTime = time;
	}
	
	
	void sendAnalyzerData(DataUnit currentDataUnit) throws KAPcomException
	{
		if (vehicle == null) return;
		
		long time = currentDataUnit.getDate().getTime();  // in milliseconds
		float timeDiff = ((float) (time - oldTime)) / 1000f; // in seconds

		Vector3f carPosition = currentDataUnit.getCarPosition();
		float latitude = carPosition.getX();
		float longitude = carPosition.getY();
		float altitude = carPosition.getZ();
		
		float orientation = getOrientation(currentDataUnit.getCarRotation());  // 0..360 degree
		
		float rotation = (orientation - oldOrientation)/timeDiff; // in degree/s
		if(rotation < -180)
			rotation += 360;
		else if(rotation > 180)
			rotation -= 360;
		
		float rotationAcceleration = (rotation - oldRotation)/timeDiff; // in degree/s^2
		
		float speed = FastMath.abs(currentDataUnit.getSpeed());  // in Km/h
		
		double rise = (altitude - oldAltitude)/timeDiff; // in m/s
		
		double verticalAcceleration = (rise - oldRise)/timeDiff;  // in m/s^2
		
		float acceleration = ((speed - oldSpeed)/3.6f)/timeDiff; // in m/s^2
		
		float gasPedalPress = currentDataUnit.getAcceleratorPedalPos(); // in %
		float brakePedalPress = currentDataUnit.getBrakePedalPos(); // in %
		
		float maxSteeringAngle = DriveAnalyzer.getDrivingTask().getSettingsLoader().getSetting(
				Setting.CANInterface_maxSteeringAngle, SimulationDefaults.CANInterface_maxSteeringAngle);
		
		float steeringAngle = -maxSteeringAngle*currentDataUnit.getSteeringWheelPos(); // in degree (+ = right, - = left)
		
		int engineOn;
		if(currentDataUnit.isEngineOn())
			engineOn = 1;
		else
			engineOn = 0;
		
		String xml = "<root>" +
						"<thisVehicle>" +
							"<interior>" +
								"<cockpit>" +
									"<pedals>" +
										"<gasPedal>" +
											"<Properties><pressedState>" + gasPedalPress + "</pressedState></Properties>" +
										"</gasPedal>" +
										"<brakePedal>" +
											"<Properties><pressedState>" + brakePedalPress + "</pressedState></Properties>" +
										"</brakePedal>" +
									"</pedals>" +
									"<steeringWheel>" +
										"<Properties><steerAngle>" + steeringAngle + "</steerAngle></Properties>" +
									"</steeringWheel>" +
								"</cockpit>" +
							"</interior>" +
							"<exterior>" +
								"<engineCompartment>" +
									"<engine><Properties>" +
										"<running>" + engineOn + "</running>" +
									"</Properties></engine>" +
								"</engineCompartment>" +
							"</exterior>" +
							"<physicalAttributes><Properties>" +
								"<latitude>"+latitude+"</latitude>" +
								"<longitude>"+longitude+"</longitude>" +
								"<altitude>"+altitude+"</altitude>" +
								"<orientation>"+orientation+"</orientation>" +
								"<speed>"+speed+"</speed>" +
								"<rise>"+rise+"</rise>" +
								"<accelerationLateral>"+verticalAcceleration+"</accelerationLateral>" +
								"<rotation>"+rotation+"</rotation>" +
								"<accelerationRotation>"+rotationAcceleration+"</accelerationRotation>" +
								"<acceleration>"+acceleration+"</acceleration>" +
							"</Properties></physicalAttributes>" +
						"</thisVehicle>" +
					"</root>";


		kb.getClient().sendAddInstanceXml("", xml);		
		
		//System.out.println(timeDiff);
		
		oldOrientation = orientation;
		oldRotation = rotation;
		oldAltitude = altitude;
		oldRise = rise;
		oldSpeed = speed;
		oldTime = time;
	}

	
	public float getOrientation(Quaternion carRotation) 
	{
		// get Euler angles from rotation quaternion
		float[] angles = carRotation.toAngles(null);
		
		// heading in radians
		float heading = -angles[1];
		
		// normalize radian angle
		float fullAngle = 2*FastMath.PI;
		float angle_rad = (heading + fullAngle) % fullAngle;
		
		// convert radian to degree
		return angle_rad * 180/FastMath.PI;
	}

}
