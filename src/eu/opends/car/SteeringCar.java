/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2015 Rafael Math
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

package eu.opends.car;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

import eu.opends.basics.SimulationBasics;
import eu.opends.car.LightTexturesContainer.TurnSignalState;
import eu.opends.drivingTask.DrivingTask;
import eu.opends.drivingTask.scenario.ScenarioLoader;
import eu.opends.drivingTask.scenario.ScenarioLoader.CarProperty;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.environment.Crosswind;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;
import eu.opends.tools.PanelCenter;
import eu.opends.tools.Util;
import eu.opends.traffic.PhysicalTraffic;
import eu.opends.traffic.TrafficCar;
import eu.opends.trafficObjectLocator.TrafficObjectLocator;

/**
 * Driving Car
 * 
 * @author Rafael Math
 */
public class SteeringCar extends Car 
{
	// minimum steering percentage to be reached for switching off the turn signal automatically
	// when moving steering wheel back towards neutral position
	private float turnSignalThreshold = 0.25f;
	
    private TrafficObjectLocator trafficObjectLocator;
    private boolean handBrakeApplied = false;
    
    // Simphynity Motion Seat
    private DatagramSocket socket;
	private int gameTime = 0;
	private long oldTime = 0;
	private Vector3f localSpeedVector = new Vector3f(0,0,0);
    
    // adaptive cruise control
	private boolean isAdaptiveCruiseControl = false;
	private float minLateralSafetyDistance;
	private float minForwardSafetyDistance;
	private float emergencyBrakeDistance;
	private boolean suppressDeactivationByBrake = false;
	
	// crosswind (will influence steering angle)
	private Crosswind crosswind = new Crosswind("left", 0, 0);
	
    
	public SteeringCar(Simulator sim) 
	{		
		this.sim = sim;
		
		DrivingTask drivingTask = SimulationBasics.getDrivingTask();
		ScenarioLoader scenarioLoader = drivingTask.getScenarioLoader();
		
		initialPosition = scenarioLoader.getStartLocation();
		if(initialPosition == null)
			initialPosition = SimulationDefaults.initialCarPosition;
		
		this.initialRotation = scenarioLoader.getStartRotation();
		if(this.initialRotation == null)
			this.initialRotation = SimulationDefaults.initialCarRotation;
			
		// add start position as reset position
		Simulator.getResetPositionList().add(new ResetPosition(initialPosition,initialRotation));
		
		mass = scenarioLoader.getChassisMass();
		
		minSpeed = scenarioLoader.getCarProperty(CarProperty.engine_minSpeed, SimulationDefaults.engine_minSpeed);
		maxSpeed = scenarioLoader.getCarProperty(CarProperty.engine_maxSpeed, SimulationDefaults.engine_maxSpeed);
			
		decelerationBrake = scenarioLoader.getCarProperty(CarProperty.brake_decelerationBrake, 
				SimulationDefaults.brake_decelerationBrake);
		maxBrakeForce = 0.004375f * decelerationBrake * mass;
		
		decelerationFreeWheel = scenarioLoader.getCarProperty(CarProperty.brake_decelerationFreeWheel, 
				SimulationDefaults.brake_decelerationFreeWheel);
		maxFreeWheelBrakeForce = 0.004375f * decelerationFreeWheel * mass;
		
		engineOn = scenarioLoader.getCarProperty(CarProperty.engine_engineOn, SimulationDefaults.engine_engineOn);
		if(!engineOn)
			showEngineStatusMessage(engineOn);
		
		Float lightIntensityObj = scenarioLoader.getCarProperty(CarProperty.light_intensity, SimulationDefaults.light_intensity);
		if(lightIntensityObj != null)
			lightIntensity = lightIntensityObj;
		
		transmission = new Transmission(this);
		powerTrain = new PowerTrain(this);
		
		modelPath = scenarioLoader.getModelPath();
		
		init();

        // allows to place objects at current position
        trafficObjectLocator = new TrafficObjectLocator(sim, this);
        
        // load settings of adaptive cruise control
        isAdaptiveCruiseControl = scenarioLoader.getCarProperty(CarProperty.cruiseControl_acc, SimulationDefaults.cruiseControl_acc);
    	minLateralSafetyDistance = scenarioLoader.getCarProperty(CarProperty.cruiseControl_safetyDistance_lateral, SimulationDefaults.cruiseControl_safetyDistance_lateral);
    	minForwardSafetyDistance = scenarioLoader.getCarProperty(CarProperty.cruiseControl_safetyDistance_forward, SimulationDefaults.cruiseControl_safetyDistance_forward);
    	emergencyBrakeDistance = scenarioLoader.getCarProperty(CarProperty.cruiseControl_emergencyBrakeDistance, SimulationDefaults.cruiseControl_emergencyBrakeDistance);
    	suppressDeactivationByBrake = scenarioLoader.getCarProperty(CarProperty.cruiseControl_suppressDeactivationByBrake, SimulationDefaults.cruiseControl_suppressDeactivationByBrake);
    	
    	// if initialSpeed > 0 --> cruise control will be on at startup
    	targetSpeedCruiseControl = scenarioLoader.getCarProperty(CarProperty.cruiseControl_initialSpeed, SimulationDefaults.cruiseControl_initialSpeed);
		isCruiseControl = (targetSpeedCruiseControl > 0);
    	
    	try {
			socket = new DatagramSocket(20778);
		} catch (SocketException e) {
			e.printStackTrace(); 
		}
	}


	public TrafficObjectLocator getObjectLocator()
	{
		return trafficObjectLocator;
	}
	
	
	public boolean isHandBrakeApplied()
	{
		return handBrakeApplied;
	}
	
	
	public void applyHandBrake(boolean applied)
	{
		handBrakeApplied = applied;
	}

	
	// start applying crosswind and return to 0 (computed in update loop)
	public void setupCrosswind(String direction, float force, int duration)
	{
		crosswind = new Crosswind(direction, force, duration);
	}
	
	
	Vector3f lastVelocity = new Vector3f(0,0,0);
	long m_nLastChangeTime = 0;
	
	// will be called, in every frame
	public void update(float tpf)
	{
		// accelerate
		float pAccel = 0;
		if(!engineOn)
		{
			// apply 0 acceleration when engine not running
			pAccel = powerTrain.getPAccel(tpf, 0) * 30f;
		}
		else if(isAutoAcceleration && (getCurrentSpeedKmh() < minSpeed))
		{
			// apply maximum acceleration (= -1 for forward) to maintain minimum speed
			pAccel = powerTrain.getPAccel(tpf, -1) * 30f;
		}
		else if(isCruiseControl && (getCurrentSpeedKmh() < targetSpeedCruiseControl))
		{
			// apply maximum acceleration (= -1 for forward) to maintain target speed
			pAccel = powerTrain.getPAccel(tpf, -1) * 30f;
			
			if(isAdaptiveCruiseControl)
			{
				// lower speed if leading car is getting to close
				pAccel = getAdaptivePAccel(pAccel);
			}
		}
		else
		{
			// apply acceleration according to gas pedal state
			pAccel = powerTrain.getPAccel(tpf, acceleratorPedalIntensity) * 30f;
		}
		transmission.performAcceleration(pAccel);
		
		// brake lights
		setBrakeLight(brakePedalIntensity > 0);
		
		if(handBrakeApplied)
		{
			// hand brake
			carControl.brake(maxBrakeForce);
			PanelCenter.setHandBrakeIndicator(true);
		}
		else
		{
			// brake	
			float appliedBrakeForce = brakePedalIntensity * maxBrakeForce;
			float currentFriction = powerTrain.getFrictionCoefficient() * maxFreeWheelBrakeForce;
			carControl.brake(appliedBrakeForce + currentFriction);
			PanelCenter.setHandBrakeIndicator(false);
		}
		
		
		// lights
		leftHeadLight.setColor(ColorRGBA.White.mult(lightIntensity));
        leftHeadLight.setPosition(carModel.getLeftLightPosition());
        leftHeadLight.setDirection(carModel.getLeftLightDirection());
        
        rightHeadLight.setColor(ColorRGBA.White.mult(lightIntensity));
        rightHeadLight.setPosition(carModel.getRightLightPosition());
        rightHeadLight.setDirection(carModel.getRightLightDirection());
        
        // cruise control indicator
        if(isCruiseControl)
        	PanelCenter.setCruiseControlIndicator(targetSpeedCruiseControl);
        else
        	PanelCenter.unsetCruiseControlIndicator();
        
        trafficObjectLocator.update();
        
        // switch off turn signal after turn        
        if(hasFinishedTurn())
        {
        	lightTexturesContainer.setTurnSignal(TurnSignalState.OFF);
        }
        
        lightTexturesContainer.update();
        
		steeringInfluenceByCrosswind = crosswind.getCurrentSteeringInfluence();

        updateFrictionSlip();
        
        updateWheel();
        
        SettingsLoader settingsLoader = SimulationBasics.getSettingsLoader();
        if(settingsLoader.getSetting(Setting.Simphynity_enableConnection, SimulationDefaults.Simphynity_enableConnection))
		{
			String ip = settingsLoader.getSetting(Setting.Simphynity_ip, SimulationDefaults.Simphynity_ip);
			if(ip == null || ip.isEmpty())
				ip = "127.0.0.1";
			int port = settingsLoader.getSetting(Setting.Simphynity_port, SimulationDefaults.Simphynity_port);

        	sendSimphynityInstructions(ip, port);
		}
	}


	private void sendSimphynityInstructions(String ip, int port)
	{
		long time = System.currentTimeMillis();  // in milliseconds
		float timeDiff = ((float) (time - oldTime)) / 1000f; // in seconds
		
		// send updates at most 40 times per second (acceleration changes more stable)
		if(timeDiff > 0.025f)
		{			
		    Vector3f globalSpeedVector = this.getCarControl().getLinearVelocity();
		    float heading = this.getHeadingDegree() * FastMath.DEG_TO_RAD;
		    float speedForward = FastMath.sin(heading) * globalSpeedVector.x - FastMath.cos(heading) * globalSpeedVector.z;
		    float speedLateral = FastMath.cos(heading) * globalSpeedVector.x + FastMath.sin(heading) * globalSpeedVector.z;
		    float speedVertical = globalSpeedVector.y;
		    Vector3f currentLocalSpeedVector = new Vector3f(speedForward, speedLateral, speedVertical); // in m/s
		    Vector3f currentLocalAccelerationVector = currentLocalSpeedVector.subtract(localSpeedVector).divide(timeDiff); // in m/s^2
		    
		    if(getCurrentSpeedKmh() < 3 && this.getGasPedalPressIntensity() < 0.1f)
		    	currentLocalAccelerationVector.x = 0;
		    	
		    //System.err.println(currentLocalAccelerationVector.x);
		    
		    oldTime = time;
		    localSpeedVector = currentLocalSpeedVector;
		   	    
		    try
		    {
		    	InetAddress adress = InetAddress.getByName(ip);
		    	
		        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        final DataOutputStream daos = new DataOutputStream(baos);
		        
		        // useLocalVals            
		        daos.writeBoolean(true);
		        daos.writeBoolean(false);
		        daos.writeBoolean(false);
		        daos.writeBoolean(false);
		        
		        // localAccel
		        float ONE_G_MS = 9.80665f;
		       	daos.writeFloat(convertFloat(Math.max(Math.min(-currentLocalAccelerationVector.x, ONE_G_MS), -ONE_G_MS))); // -1G - +1G            
		        daos.writeFloat(convertFloat(Math.max(Math.min(-currentLocalAccelerationVector.y, ONE_G_MS), -ONE_G_MS)));  // -1G - +1G
		        daos.writeFloat(convertFloat(ONE_G_MS - Math.max(Math.min(-currentLocalAccelerationVector.z, ONE_G_MS), -ONE_G_MS))); // 0 - 2G
		        
		        // localVel
		        daos.writeFloat(convertFloat(Math.max(Math.min(currentLocalSpeedVector.x / 40f, 1f), 0f))); // 0.0 - 1.0
		        daos.writeFloat(convertFloat(Math.max(Math.min(currentLocalSpeedVector.y / 40f, 1f), 0f))); // 0.0 - 1.0
		        daos.writeFloat(convertFloat(Math.max(Math.min(-currentLocalSpeedVector.z / 40f, 1f), 0f))); // 0.0 - 1.0
		     
		        // globalVel
		        Vector3f globalVelocity = getCarControl().getLinearVelocity().divide(40f);
		        daos.writeFloat(convertFloat(globalVelocity.x));
		        daos.writeFloat(convertFloat(globalVelocity.y));
		        daos.writeFloat(convertFloat(globalVelocity.z));
		        
		        // rotationMatrix
		        Matrix3f rotationMatrix = getRotation().toRotationMatrix();
		        daos.writeFloat(convertFloat(rotationMatrix.get(0, 0)));
		        daos.writeFloat(convertFloat(rotationMatrix.get(0, 1)));
		        daos.writeFloat(convertFloat(rotationMatrix.get(0, 2)));
		        daos.writeFloat(convertFloat(rotationMatrix.get(1, 0)));
		        daos.writeFloat(convertFloat(rotationMatrix.get(1, 1)));
		        daos.writeFloat(convertFloat(rotationMatrix.get(1, 2)));
		        daos.writeFloat(convertFloat(rotationMatrix.get(2, 0)));
		        daos.writeFloat(convertFloat(rotationMatrix.get(2, 1)));
		        daos.writeFloat(convertFloat(rotationMatrix.get(2, 2)));
		           
		        // packetTimeMillis
		        if(!sim.isPause())
		        	gameTime += (timeDiff * 1000);
		        daos.writeInt(convertInt(gameTime));
		        
		        daos.close();
		        
		        final byte[] bytes = baos.toByteArray();            
		
		    	DatagramPacket packet = new DatagramPacket(bytes, bytes.length, adress, port);
		    	socket.send(packet);
		    	
		    	System.err.println("Sim: " + ip + port);
		    }
		    catch (Exception e)
		    {
		    	e.printStackTrace();
		    }
		}
	}
	
	
	private float convertFloat(float in)
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream daos = new DataOutputStream(baos);
        
        try {
			daos.writeFloat(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
        byte[] byteArray = baos.toByteArray();
        
		ByteBuffer bb = ByteBuffer.wrap(byteArray);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getFloat();
	}
	
	
	private int convertInt(int in)
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream daos = new DataOutputStream(baos);
        
        try {
			daos.writeFloat(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
        byte[] byteArray = baos.toByteArray();
        
		ByteBuffer bb = ByteBuffer.wrap(byteArray);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}
	
	
    float leftWheelsPos = 2.2f;
    float backAxleHeight = -3.0f;
    float backAxlePos = 2.45f;
    long prevTime = 0;
	private void updateWheel() 
	{     
		long time = System.currentTimeMillis();
		if(time - prevTime > 1000)
		{/*
			Vector3f wheelDirection = new Vector3f(0, -1, 0);
			Vector3f wheelAxle = new Vector3f(-1, 0, 0);
			float wheelRadius = 0.5f;
			float suspensionLenght = 0.2f;
		
			carControl.removeWheel(3);
		
			backAxlePos += 0.05f;
		
			// add back left wheel
			Geometry geom_wheel_fl = Util.findGeom(carNode, "WheelBackLeft");
			geom_wheel_fl.setLocalScale(wheelRadius*2);
			geom_wheel_fl.center();
			BoundingBox box = (BoundingBox) geom_wheel_fl.getModelBound();
			carControl.addWheel(geom_wheel_fl.getParent(), 
        		box.getCenter().add(leftWheelsPos, backAxleHeight, backAxlePos),
                wheelDirection, wheelAxle, suspensionLenght, wheelRadius, true);

			System.out.println("backAxlePos: " + backAxlePos);
			
			prevTime = time;
			*/
		}
		//System.out.println("prevTime: " + prevTime + "  time: " + time);
	}


	private void updateFrictionSlip() 
	{
		/*
        // ice
        carControl.setRollInfluence(0, 0.5f); 
        carControl.setRollInfluence(1, 0.5f); 
        carControl.setRollInfluence(2, 0.5f); 
        carControl.setRollInfluence(3, 0.5f);
        
        carControl.setFrictionSlip(0, 1f); 
        carControl.setFrictionSlip(1, 1f); 
        carControl.setFrictionSlip(2, 1f); 
        carControl.setFrictionSlip(3, 1f); 
        */
	}


	private boolean hasStartedTurning = false;
	private boolean hasFinishedTurn() 
	{
		TurnSignalState turnSignalState = lightTexturesContainer.getTurnSignal();
		float steeringWheelState = getSteeringWheelState();
		
		if(turnSignalState == TurnSignalState.LEFT)
		{
			if(steeringWheelState > turnSignalThreshold)
				hasStartedTurning = true;
			else if(hasStartedTurning)
			{
				hasStartedTurning = false;
				return true;
			}
		}
		
		if(turnSignalState == TurnSignalState.RIGHT)
		{
			if(steeringWheelState < -turnSignalThreshold)
				hasStartedTurning = true;
			else if(hasStartedTurning)
			{
				hasStartedTurning = false;
				return true;
			}
		}
		
		return false;
	}


	// Adaptive Cruise Control ***************************************************	
	
	private float getAdaptivePAccel(float pAccel)
	{
		brakePedalIntensity = 0f;

		// check distance from traffic vehicles
		for(TrafficCar vehicle : PhysicalTraffic.getVehicleList())
		{
			if(belowSafetyDistance(vehicle.getPosition()))
			{
				pAccel = 0;
			
				if(vehicle.getPosition().distance(getPosition()) < emergencyBrakeDistance)
					brakePedalIntensity = 1f;
			}
		}
		
		return pAccel;
	}

	
	private boolean belowSafetyDistance(Vector3f obstaclePos) 
	{	
		float distance = obstaclePos.distance(getPosition());
		
		// angle between driving direction of traffic car and direction towards obstacle
		// (consider 3D space, because obstacle could be located on a bridge above traffic car)
		Vector3f carFrontPos = frontGeometry.getWorldTranslation();
		Vector3f carCenterPos = centerGeometry.getWorldTranslation();
		float angle = Util.getAngleBetweenPoints(carFrontPos, carCenterPos, obstaclePos, false);
		
		float lateralDistance = distance * FastMath.sin(angle);
		float forwardDistance = distance * FastMath.cos(angle);
		
		if((lateralDistance < minLateralSafetyDistance) && (forwardDistance > 0) && 
				(forwardDistance < Math.max(0.5f * getCurrentSpeedKmh(), minForwardSafetyDistance)))
		{
			return true;
		}
		
		return false;
	}


	public void increaseCruiseControl(float diff) 
	{
		targetSpeedCruiseControl = Math.min(targetSpeedCruiseControl + diff, 260.0f);	
	}


	public void decreaseCruiseControl(float diff) 
	{
		targetSpeedCruiseControl = Math.max(targetSpeedCruiseControl - diff, 0.0f);
	}

	
	public void disableCruiseControlByBrake() 
	{
		if(!suppressDeactivationByBrake)
			setCruiseControl(false);
	}
	// Adaptive Cruise Control ***************************************************

}
