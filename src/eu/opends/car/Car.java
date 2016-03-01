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

package eu.opends.car;

import java.io.File;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

import eu.opends.audio.AudioCenter;
import eu.opends.car.LightTexturesContainer.TurnSignalState;
import eu.opends.environment.GeoPosition;
import eu.opends.main.Simulator;
import eu.opends.tools.PanelCenter;
import eu.opends.tools.SpeedControlCenter;
import eu.opends.tools.Vector3d;

/**
 * 
 * @author Rafael Math
 */
public abstract class Car
{
	protected Simulator sim;
	protected Vector3f initialPosition;
	protected Quaternion initialRotation;
	
	protected CarModelLoader carModel;
    protected VehicleControl carControl;
    protected Node carNode;
    protected LightTexturesContainer lightTexturesContainer;
    
    private float steeringWheelState;
    protected float gasPedalPressIntensity;
    protected float brakePedalPressIntensity;
    protected int resetPositionCounter;
    protected Vector3f previousPosition;
    private float distanceOfCurrentFrame = 0;
    protected float mileage;
    protected boolean engineOn;

    protected float mass;
    protected boolean isAutoAcceleration = true;
    protected float targetSpeedCruiseControl = 0;
    protected boolean isCruiseControl = false;
    protected float minSpeed;
    protected float maxSpeed;
    protected float acceleration;
    protected float accelerationForce;
    protected float decelerationBrake;
    protected float maxBrakeForce;
    protected float decelerationFreeWheel;
    protected float maxFreeWheelBrakeForce;
    
    protected Transmission transmission;
    protected PowerTrain powerTrain;
    
    protected SpotLight leftHeadLight;
    protected SpotLight rightHeadLight;
    protected float lightIntensity = 0;
    protected String modelPath = "Test";

    
    protected void init()
    {
		previousPosition = initialPosition;
		resetPositionCounter = 0;
		mileage = 0;
		
        // load car model
		carModel = new CarModelLoader(sim, modelPath, mass);
		carControl = carModel.getCarControl();
		carNode = carModel.getCarNode();
		carNode.setShadowMode(ShadowMode.Cast);
		
		// generate path to light textures from model path
		File modelFile = new File(modelPath);
		String lightTexturesPath = modelFile.getPath().replace(modelFile.getName(), "lightTextures.xml");
		
		// load light textures
		lightTexturesContainer = new LightTexturesContainer(sim, carNode, lightTexturesPath);
		//lightTexturesContainer.printAllContent();
		
        // add car node to rendering node
        sim.getSceneNode().attachChild(carNode);
        
        // add car to physics node
        sim.getPhysicsSpace().add(carControl);

		// setup head light
        setupHeadlight(sim);

        // set initial position and orientation
        setPosition(initialPosition);
        setRotation(initialRotation);

        // apply continuous braking (simulates friction when free wheeling)
        resetPedals();
    }
	
	
	private void setupHeadlight(Simulator sim) 
	{
		leftHeadLight = new SpotLight();
        leftHeadLight.setColor(ColorRGBA.White.mult(lightIntensity));
        leftHeadLight.setSpotRange(100);
        leftHeadLight.setSpotInnerAngle(11*FastMath.DEG_TO_RAD);
        leftHeadLight.setSpotOuterAngle(25*FastMath.DEG_TO_RAD);
        sim.getSceneNode().addLight(leftHeadLight);
        
        rightHeadLight = new SpotLight();
        rightHeadLight.setColor(ColorRGBA.White.mult(lightIntensity));
        rightHeadLight.setSpotRange(100);
        rightHeadLight.setSpotInnerAngle(11*FastMath.DEG_TO_RAD);
        rightHeadLight.setSpotOuterAngle(25*FastMath.DEG_TO_RAD);
        sim.getSceneNode().addLight(rightHeadLight);
	}
	
	
	public float getMass()
	{
		return mass;
	}
	
	public float getMinSpeed()
	{
		return minSpeed;
	}
	
	public void setMinSpeed(float minSpeed)
	{
		this.minSpeed = minSpeed;
	}
	
	public float getMaxSpeed()
	{
		return maxSpeed;
	}
	
	public void setMaxSpeed(float maxSpeed)
	{
		this.maxSpeed = maxSpeed;
	}
	
	public float getAcceleration()
	{
		return acceleration;
	}
	
	public float getMaxBrakeForce()
	{
		return maxBrakeForce;
	}
	
	public float getDecelerationFreeWheel()
	{
		return decelerationFreeWheel;
	}
	
	
	public Vector3f getEgoCamPos()
	{
		return carModel.getEgoCamPos();
	}
	
	
	public Vector3f getStaticBackCamPos()
	{
		return carModel.getStaticBackCamPos();
	}
	
	
	public Node getCarNode()
	{
		return carNode;
	}
	
	
	public VehicleControl getCarControl()
	{
		return carControl;
	}
	
	
	public Transmission getTransmission()
	{
		return transmission;
	}
	
	
	public PowerTrain getPowerTrain()
	{
		return powerTrain;
	}
	
	
	public void setToNextResetPosition() 
	{
		int numberOfResetPoints = Simulator.getResetPositionList().size();
		
		setToResetPosition(resetPositionCounter);
		resetPositionCounter = (resetPositionCounter + 1) % numberOfResetPoints;
	}

	
	public void setToResetPosition(int keyNumber) 
	{
		int numberOfResetPoints = Simulator.getResetPositionList().size();
		
		if (keyNumber < numberOfResetPoints) 
		{
			ResetPosition reset = Simulator.getResetPositionList().get(keyNumber);
			
			Vector3f location = reset.getLocation();
			Quaternion rotation = reset.getRotation();
			
			setPosition(location);
			setRotation(rotation);
		}
	}
	
	public void setPosition(Vector3f v) 
	{
		setPosition(v.x, v.y, v.z);
	}
	
	
	public void setPosition(float x, float y, float z) 
	{
		previousPosition = new Vector3f(x,y,z);
		
		carControl.setPhysicsLocation(previousPosition);
		carControl.setLinearVelocity(Vector3f.ZERO);
		carControl.setAngularVelocity(Vector3f.ZERO);
		carControl.resetSuspension();
	}

	
	public Vector3f getPosition() 
	{
		return carControl.getPhysicsLocation();
	}
	
	
	public Vector3d getGeoPosition() 
	{
		return GeoPosition.modelToGeo(getPosition());
	}

	
	public float getHeadingDegree() 
	{
		// get Euler angles from rotation quaternion
		float[] angles = carControl.getPhysicsRotation().toAngles(null);
		
		// heading in radians
		float heading = -angles[1];
		
		// normalize radian angle
		float fullAngle = 2*FastMath.PI;
		float angle_rad = (heading + fullAngle) % fullAngle;
		
		// convert radian to degree
		return angle_rad * 180/FastMath.PI;
	}
	
	
	public float getSlope()
	{
		// get Euler angles from rotation quaternion
		float[] angles = carControl.getPhysicsRotation().toAngles(null);
		
		// slope in radians (with correction due to different suspension heights)
		return angles[0] - 0.031765f;
	}
	
	
	public float getSlopeDegree()
	{
		// convert radian to degree and round to one decimal
		return ((int)(getSlope() * 180/FastMath.PI *10f))/10f;
	}
	
	
	public void setRotation(Quaternion q) 
	{	
		setRotation(q.getX(), q.getY(), q.getZ(), q.getW());
	}
	
	
	public void setRotation(float x, float y, float z, float w) 
	{
		Quaternion rotation = new Quaternion(x,y,z,w);
		
		// compensate that car is actually driving backwards
		float[] angles = rotation.toAngles(null);
		angles[1] = -angles[1];
		rotation = new Quaternion().fromAngles(angles);
		
		carControl.setPhysicsRotation(rotation);
		carControl.setLinearVelocity(Vector3f.ZERO);
		carControl.setAngularVelocity(Vector3f.ZERO);
		carControl.resetSuspension();
	}
	
	
	public Quaternion getRotation() 
	{
		return carControl.getPhysicsRotation();
	}
	
	
	/**
	 * Accelerates the car forward or backwards. Does it by accelerating both
	 * suspensions (4WD). If you want a front wheel drive, comment out the
	 * rearSuspension.accelerate(direction) line. If you want a rear wheel drive
	 * car comment out the other one.
	 * 
	 * @param intensity
	 *            -1 for full ahead and 1 for full backwards
	 */
	// will be called, whenever UP or DOWN arrow key is pressed
	public void setGasPedalIntensity(float intensity) 
	{
		gasPedalPressIntensity = intensity;
	}

	
	public float getGasPedalPressIntensity() 
	{
		return Math.abs(gasPedalPressIntensity);
	}


	/**
	 * Brake
	 * @param intensity
	 *            1 for full brake, 0 no brake at all
	 */
	// will be called, whenever SPACE key is pressed
	public void setBrakePedalPressIntensity(float intensity) 
	{
		brakePedalPressIntensity = intensity;
		SpeedControlCenter.stopBrakeTimer();
	}
	
	
	public float getBrakePedalPressIntensity() 
	{
		return brakePedalPressIntensity;
	}


	/**
	 * Free wheel
	 */
	public void resetPedals()
	{
		// reset pedals to initial position
		gasPedalPressIntensity = 0;
		brakePedalPressIntensity = 0;
	}
	
	
	/**
	 * Steers the front wheels.
	 * 
	 * @param direction
	 *            1 for right and -1 for left
	 */
	public void steer(final float direction) 
	{
		carControl.steer(direction);
		setSteeringWheelState(direction);
	}

	
	public void setSteeringWheelState(float steeringWheelState) 
	{
		this.steeringWheelState = steeringWheelState;
	}

	
	public float getSteeringWheelState() 
	{
		return steeringWheelState;
	}
	

	/**
	 * Unsteer the front wheels
	 */
	public void unsteer() 
	{
		carControl.steer(0);
		setSteeringWheelState(0);
	}

	
	/**
	 * To get the car speed for using in a HUD
	 * 
	 * @return velocity of the car
	 */
	public float getCurrentSpeedMs() 
	{
		return (getCurrentSpeedKmh()/3.6f);
	}
	

	public float getCurrentSpeedMsRounded()
	{
		return ((int)(getCurrentSpeedMs() * 100)) / 100f;
	}
	
	
	public float getCurrentSpeedKmh()
	{
		return FastMath.abs(carControl.getCurrentVehicleSpeedKmHour());
	}
	
	
	public float getCurrentSpeedKmhRounded()
	{
		return ((int)(getCurrentSpeedKmh() * 100)) / 100f;
	}
	
	
	public float getMileage()
	{
		updateDistanceOfCurrentFrame();
		
		if(distanceOfCurrentFrame > 0.001f)
			mileage += distanceOfCurrentFrame;
		
		return mileage;
	}

	
	private void updateDistanceOfCurrentFrame()
	{
		// compute distance since last measurement
		Vector3f currentPosition = getPosition();
		distanceOfCurrentFrame = previousPosition.distance(currentPosition);
		
		// update values
		previousPosition = currentPosition;
	}
	
	
	public float getDistanceOfCurrentFrameInKm()
	{
		return distanceOfCurrentFrame/1000f;
	}
	
	
	public String getMileageString()
	{
		float mileage = getMileage();
		if(mileage < 1000)
			return ((int)mileage) + " m";
		else
			return ((int)(mileage/10f))/100f + " km";
	}
	
	
	public void resetMileage()
	{
		mileage = 0;
	}


	public Vector3f getInitialPosition() 
	{
		return initialPosition;
	}

	
	public Quaternion getInitialRotation() 
	{
		return initialRotation;
	}
	
	
	public void toggleLight() 
	{
		if(lightIntensity < 1)
			lightIntensity = 1;
		else if(lightIntensity < 2)
			lightIntensity = 2;
		else
			lightIntensity = 0;
	}
	
	
	public boolean isLightOn()
	{
		return (lightIntensity != 0);
	}
	

	public boolean isEngineOn() 
	{
		return engineOn;
	}
	
	
	public void setEnginOn(boolean engineOn) 
	{
		this.engineOn = engineOn;
		resetPedals();
		
		showEngineStatusMessage(engineOn);
		
		if(engineOn)
			AudioCenter.startEngine();
		else
			AudioCenter.stopEngine();
	}


	protected void showEngineStatusMessage(boolean engineOn) 
	{
		if(engineOn)
			PanelCenter.getMessageBox().addMessage("Engin on", 2);
		else
			PanelCenter.getMessageBox().addMessage("Engin off. Press 'e' to start.", 0);
	}

	
	public void setAutoAcceleration(boolean isAutoAcceleration) 
	{
		this.isAutoAcceleration = isAutoAcceleration;
	}
	
	
	public boolean isAutoAcceleration() 
	{
		return isAutoAcceleration;
	}

	
	public void setCruiseControl(boolean isCruiseControl) 
	{
		this.targetSpeedCruiseControl = getCurrentSpeedKmh();
		this.isCruiseControl = isCruiseControl;
	}
	
	
	public boolean isCruiseControl() 
	{
		return isCruiseControl;
	}
	
	
	public Simulator getSimulator() 
	{
		return sim;
	}


	public String getLightState() 
	{
		if(lightIntensity == 2)
			return "HighBeam";
		else if(lightIntensity == 1)
			return "LowBeam";
		else
			return "Off";
	}
	
	
	public void setBrakeLight(boolean setToOn)
	{
		lightTexturesContainer.setBrakeLight(setToOn);
	}
	
	
	public boolean isBrakeLightOn()
	{
		return lightTexturesContainer.isBrakeLightOn();
	}

	
	public void setTurnSignal(TurnSignalState turnSignalState)
	{
		lightTexturesContainer.setTurnSignal(turnSignalState);
	}
	

	public TurnSignalState getTurnSignal() 
	{
		return lightTexturesContainer.getTurnSignal();
	}
	
	
	public void close()
	{
		lightTexturesContainer.close();
	}
}
