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

package eu.opends.input;

import com.jme3.input.controls.ActionListener;

import eu.opends.audio.AudioCenter;
import eu.opends.camera.CameraFactory;
import eu.opends.camera.CameraFactory.MirrorMode;
import eu.opends.canbus.CANClient;
import eu.opends.car.SteeringCar;
import eu.opends.car.LightTexturesContainer.TurnSignalState;
import eu.opends.effects.EffectCenter;
import eu.opends.main.Simulator;
import eu.opends.niftyGui.MessageBoxGUI;
import eu.opends.tools.PanelCenter;
import eu.opends.tools.Util;
import eu.opends.trigger.TriggerCenter;

/**
 * 
 * @author Rafael Math
 */
public class SimulatorActionListener implements ActionListener
{
	private float steeringValue = 0;
	private float accelerationValue = 0;
	private Simulator sim;
	private SteeringCar car;
	private boolean isWireFrame = false;
	
	
    public SimulatorActionListener(Simulator sim) 
    {
    	this.sim = sim;
    	this.car = sim.getCar();
	}


	public void onAction(String binding, boolean value, float tpf) 
	{
		if (binding.equals(KeyMapping.STEER_LEFT.getID())) 
		{
			if (value) {
				steeringValue += .3f;
				//sim.getPhysicalTraffic().getTrafficCar("car2").setTurnSignal(TurnSignalState.LEFT);
			} else {
				steeringValue += -.3f;
			}
			
			// if CAN-Client is running suppress external steering
			CANClient canClient = Simulator.getCanClient();
			if(canClient != null)
				canClient.suppressSteering();
			
			sim.getSteeringTask().setSteeringIntensity(-3*steeringValue);
			car.steer(steeringValue);
		} 
		
		else if (binding.equals(KeyMapping.STEER_RIGHT.getID())) 
		{
			if (value) {
				steeringValue += -.3f;
				//sim.getPhysicalTraffic().getTrafficCar("car2").setTurnSignal(TurnSignalState.RIGHT);
			} else {
				steeringValue += .3f;
			}
			
			// if CAN-Client is running suppress external steering
			CANClient canClient = Simulator.getCanClient();
			if(canClient != null)
				canClient.suppressSteering();
			
			sim.getSteeringTask().setSteeringIntensity(-3*steeringValue);
			car.steer(steeringValue);
		}
		
		// note that our fancy car actually goes backwards..
		else if (binding.equals(KeyMapping.ACCELERATE.getID())) 
		{
			if (value) {
				sim.getSteeringTask().getPrimaryTask().reportGreenLight();
				accelerationValue -= 1;
				//sim.getPhysicalTraffic().getTrafficCar("car2").setBrakeLight(false);
			} else {
				accelerationValue += 1;
			}
			
			sim.getThreeVehiclePlatoonTask().reportAcceleratorIntensity(Math.abs(accelerationValue));
			car.setAcceleratorPedalIntensity(accelerationValue);
		} 
		
		else if (binding.equals(KeyMapping.ACCELERATE_BACK.getID())) 
		{
			if (value) {
				sim.getSteeringTask().getPrimaryTask().reportRedLight();
				accelerationValue += 1;
				//sim.getPhysicalTraffic().getTrafficCar("car2").setBrakeLight(true);
			} else {
				accelerationValue -= 1;
			}
			car.setAcceleratorPedalIntensity(accelerationValue);
		} 
		
		else if (binding.equals(KeyMapping.BRAKE.getID())) 
		{
			if (value) {
				car.setBrakePedalIntensity(1f);		
				sim.getThreeVehiclePlatoonTask().reportBrakeIntensity(1f);
				car.disableCruiseControlByBrake();
			} else {
				car.setBrakePedalIntensity(0f);
				sim.getThreeVehiclePlatoonTask().reportBrakeIntensity(0f);
			}
		}

		else if (binding.equals(KeyMapping.TOGGLE_HANDBRAKE.getID())) 
		{
			if (value) {
				car.applyHandBrake(!car.isHandBrakeApplied());
			}
		}
		
		else if (binding.equals(KeyMapping.TURN_LEFT.getID())) 
		{
			if (value) 
			{
				if(car.getTurnSignal() == TurnSignalState.LEFT)
					car.setTurnSignal(TurnSignalState.OFF);
				else
					car.setTurnSignal(TurnSignalState.LEFT);
			}
		}
		
		else if (binding.equals(KeyMapping.TURN_RIGHT.getID())) 
		{
			if (value) 
			{
				if(car.getTurnSignal() == TurnSignalState.RIGHT)
					car.setTurnSignal(TurnSignalState.OFF);
				else
					car.setTurnSignal(TurnSignalState.RIGHT);
			}
		}
		
		else if (binding.equals(KeyMapping.HAZARD_LIGHTS.getID())) 
		{
			if (value) 
			{
				if(car.getTurnSignal() == TurnSignalState.BOTH)
					car.setTurnSignal(TurnSignalState.OFF);
				else
					car.setTurnSignal(TurnSignalState.BOTH);
			}
		}
		
		else if (binding.equals(KeyMapping.REPORT_LANDMARK.getID())) 
		{
			if (value) {
				sim.getSteeringTask().getSecondaryTask().reportLandmark();
			}
		}
		
		else if (binding.equals(KeyMapping.REPORT_REACTION.getID())) 
		{
			if (value) {
				sim.getThreeVehiclePlatoonTask().reportReactionKeyPressed();
			}
		}
		
		else if (binding.equals(KeyMapping.REPORT_LEADINGCARBRAKELIGHT_REACTION.getID())) 
		{
			if (value) {
				sim.getThreeVehiclePlatoonTask().reportReaction("LCBL");
			}
		}
		
		else if (binding.equals(KeyMapping.REPORT_LEADINGCARTURNSIGNAL_REACTION.getID())) 
		{
			if (value) {
				sim.getThreeVehiclePlatoonTask().reportReaction("LCTS");
			}
		}
		
		else if (binding.equals(KeyMapping.REPORT_FOLLOWERCARTURNSIGNAL_REACTION.getID())) 
		{
			if (value) {
				sim.getThreeVehiclePlatoonTask().reportReaction("FCTS");
			}
		}

		else if (binding.equals(KeyMapping.SET_MARKER.getID())) 
		{
			if (value) {
				sim.getThreeVehiclePlatoonTask().setMarker();
			}
		}

		else if (binding.equals(KeyMapping.TOGGLE_CAM.getID())) 
		{
			if (value) {
				// toggle camera
				sim.getCameraFactory().changeCamera();
			}
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_WIREFRAME.getID())) 
		{
			if (value) {
				isWireFrame = !isWireFrame;
				Util.setWireFrame(sim.getSceneNode(), isWireFrame);
			}
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_ENGINE.getID())) 
		{
			if (value)
			{
				car.setEngineOn(!car.isEngineOn());
			}
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_PAUSE.getID())) 
		{
			if (value)
				sim.setPause(!sim.isPause());
		}
		
		else if (binding.equals(KeyMapping.START_PAUSE.getID())) 
		{
			if (value && (!sim.isPause()))
				sim.setPause(true);
		}
		
		else if (binding.equals(KeyMapping.STOP_PAUSE.getID())) 
		{
			if (value && sim.isPause())
				sim.setPause(false);
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_TRAFFICLIGHTMODE.getID())) 
		{
			if (value)
			{
				sim.getTrafficLightCenter().toggleMode();
				//sim.getPhysicalTraffic().getTrafficCar("car1").loseCargo();
			}
				
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_MESSAGEBOX.getID())) 
		{
			if (value)
			{
				MessageBoxGUI messageBoxGUI = PanelCenter.getMessageBox();
				messageBoxGUI.toggleDialog();
			}
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_RECORD_DATA.getID())) 
		{
			if (value)
			{
				if (sim.getMyDataWriter() == null) {
					sim.initializeDataWriter(-1);
				}
	
				if (sim.getMyDataWriter().isDataWriterEnabled() == false) {
					System.out.println("Start storing Drive-Data");
					sim.getMyDataWriter().setDataWriterEnabled(true);
					PanelCenter.getStoreText().setText("S");
				} else {
					System.out.println("Stop storing Drive-Data");
					sim.getMyDataWriter().setDataWriterEnabled(false);
					PanelCenter.getStoreText().setText(" ");
				}
			}
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_TOPVIEW.getID())) 
		{
			if (value)
			{
				CameraFactory camFactory = sim.getCameraFactory();
				
				if(camFactory.isTopViewEnabled())
					camFactory.setTopViewEnabled(false);
				else
					camFactory.setTopViewEnabled(true);
			}
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_BACKMIRROR.getID())) 
		{
			if (value)
			{
				CameraFactory camFactory = sim.getCameraFactory();
				MirrorMode mirrorState = camFactory.getMirrorMode();
				
				if(mirrorState == MirrorMode.OFF)
					camFactory.setMirrorMode(MirrorMode.BACK_ONLY);
				else if(mirrorState == MirrorMode.BACK_ONLY)
					camFactory.setMirrorMode(MirrorMode.ALL);
				else if(mirrorState == MirrorMode.ALL)
					camFactory.setMirrorMode(MirrorMode.SIDE_ONLY);
				else
					camFactory.setMirrorMode(MirrorMode.OFF);
			}
		}
		
		else if (binding.equals(KeyMapping.RESET_CAR.getID())) 
		{
			if (value)
				car.setToNextResetPosition();
		}
		
		else if (binding.equals(KeyMapping.RESET_CAR_POS1.getID())) 
		{
			if (value)
			{
				sim.getSteeringTask().getPrimaryTask().reportBlinkingLeft();
				car.setToResetPosition(0);
			}
		}
		
		else if (binding.equals(KeyMapping.RESET_CAR_POS2.getID())) 
		{
			if (value)
			{
				sim.getSteeringTask().getPrimaryTask().reportBlinkingRight();
				car.setToResetPosition(1);
			}
		}
		
		else if (binding.equals(KeyMapping.RESET_CAR_POS3.getID())) 
		{
			if (value)
			{
				car.setToResetPosition(2);
				TriggerCenter.performRemoteTriggerAction("resume");
			}
			
		}
		
		else if (binding.equals(KeyMapping.RESET_CAR_POS4.getID())) 
		{
			if (value)
			{
				car.setToResetPosition(3);
				TriggerCenter.performRemoteTriggerAction("speed");
			}
		}
		
		else if (binding.equals(KeyMapping.RESET_CAR_POS5.getID())) 
		{
			if (value)
			{
				car.setToResetPosition(4);
				TriggerCenter.performRemoteTriggerAction("shutDown");
			}
		}
		
		else if (binding.equals(KeyMapping.RESET_CAR_POS6.getID())) 
		{
			if (value)
				car.setToResetPosition(5);
		}
		
		else if (binding.equals(KeyMapping.RESET_CAR_POS7.getID())) 
		{
			if (value)
			{
				//sim.getObjectManipulationCenter().setPosition("RoadworksSign1", new Vector3f(-740,0,-41));
				car.setToResetPosition(6);
			}
		}
		
		else if (binding.equals(KeyMapping.RESET_CAR_POS8.getID())) 
		{
			if (value)
			{
				//sim.getObjectManipulationCenter().setPosition("RoadworksSign1", new Vector3f(-740,0,-40));
				car.setToResetPosition(7);
			}
		}
		
		else if (binding.equals(KeyMapping.RESET_CAR_POS9.getID())) 
		{
			if (value)
			{
				//sim.getObjectManipulationCenter().setRotation("RoadworksSign1", new float[]{0,0,0});
				car.setToResetPosition(8);
			}
		}
		
		else if (binding.equals(KeyMapping.RESET_CAR_POS10.getID())) 
		{
			if (value)
			{
				//sim.getObjectManipulationCenter().setRotation("RoadworksSign1", new float[]{0,90,0});
				car.setToResetPosition(9);
			}
		}
		
		else if (binding.equals(KeyMapping.SHIFT_UP.getID())) 
		{
			if (value)
			{
				sim.getSteeringTask().getPrimaryTask().reportDoubleGreenLight();
				car.getTransmission().shiftUp(false);
			}
		}
		
		else if (binding.equals(KeyMapping.SHIFT_DOWN.getID())) 
		{
			if (value)
			{
				sim.getSteeringTask().getPrimaryTask().reportDoubleRedLight();
				car.getTransmission().shiftDown(false);
			}
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_AUTOMATIC.getID())) 
		{
			if (value)
			{
				car.getTransmission().setAutomatic(!car.getTransmission().isAutomatic());
			}
		}
		
		else if (binding.equals(KeyMapping.HORN.getID())) 
		{
			if (value)
				AudioCenter.playSound("horn");
			else
				AudioCenter.stopSound("horn");
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_KEYMAPPING.getID())) 
		{
			if (value)
				sim.getKeyMappingGUI().toggleDialog();
		}
		
		else if (binding.equals(KeyMapping.SHUTDOWN.getID())) 
		{
			if (value) 
			{
				if(Simulator.oculusRiftAttached)
					sim.stop();
				else
					sim.getShutDownGUI().toggleDialog();
			}
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_MIN_SPEED.getID())) 
		{
			if (value)
				car.setAutoAcceleration(!car.isAutoAcceleration());
		}
		
		else if (binding.equals(KeyMapping.CRUISE_CONTROL.getID())) 
		{
			if (value)
				car.setCruiseControl(!car.isCruiseControl());
		}
		
		else if (binding.equals(KeyMapping.RESET_FUEL_CONSUMPTION.getID())) 
		{
			if (value)
				car.getPowerTrain().resetTotalFuelConsumption();
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_STATS.getID()))
		{
			if (value)
				sim.toggleStats();
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_CINEMATIC.getID()))
		{
			if (value)
			{
				if(sim.getCameraFlight() != null)
					sim.getCameraFlight().toggleStop();
				
				sim.getSteeringTask().start();
			}
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_HEADLIGHT.getID())) 
		{
			if (value)
			{
				car.toggleLight();
			}
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_PHYSICS_DEBUG.getID())) 
		{
			if (value)
			{
				sim.toggleDebugMode();
			}
		}
		
		else if (binding.equals(KeyMapping.CLOSE_INSTRUCTION_SCREEN.getID())) 
		{
			if (value)
			{
				sim.getInstructionScreenGUI().hideDialog();
			}
		}
		
		else if (binding.equals(KeyMapping.OBJECT_ROTATE_LEFT_FAST.getID())) 
		{
			if (value)
			{
				((SteeringCar)sim.getCar()).getObjectLocator().rotateThingNode(-30);
			}
		}
		
		else if (binding.equals(KeyMapping.OBJECT_ROTATE_RIGHT_FAST.getID())) 
		{
			if (value)
			{
				((SteeringCar)sim.getCar()).getObjectLocator().rotateThingNode(30);
			}
		}
		
		else if (binding.equals(KeyMapping.OBJECT_ROTATE_LEFT.getID())) 
		{
			if (value)
			{
				((SteeringCar)sim.getCar()).getObjectLocator().rotateThingNode(-1);
			}
		}
		
		else if (binding.equals(KeyMapping.OBJECT_ROTATE_RIGHT.getID())) 
		{
			if (value)
			{
				((SteeringCar)sim.getCar()).getObjectLocator().rotateThingNode(1);
			}
		}
		
		else if (binding.equals(KeyMapping.OBJECT_SET.getID())) 
		{
			if (value)
			{
				((SteeringCar)sim.getCar()).getObjectLocator().placeThingNode();
			}
		}
		
		else if (binding.equals(KeyMapping.OBJECT_TOGGLE.getID())) 
		{
			if (value)
			{
				((SteeringCar)sim.getCar()).getObjectLocator().toggleThingNode();
			}
		}
		
		else if (binding.equals(KeyMapping.CC_INC5.getID())) 
		{
			if (value)
			{
				((SteeringCar)sim.getCar()).increaseCruiseControl(5);
			}
		}
		
		else if (binding.equals(KeyMapping.CC_DEC5.getID())) 
		{
			if (value)
			{
				((SteeringCar)sim.getCar()).decreaseCruiseControl(5);
			}
		}
		
		else if (binding.equals(KeyMapping.SNOW_INC5.getID())) 
		{
			if (value)
			{
				float percentage = EffectCenter.getSnowingPercentage() + 5;
				EffectCenter.setSnowingPercentage(percentage);
			}
		}
		
		else if (binding.equals(KeyMapping.SNOW_DEC5.getID())) 
		{
			if (value)
			{
				float percentage = EffectCenter.getSnowingPercentage() - 5;
				EffectCenter.setSnowingPercentage(percentage);
			}
		}
		
		else if (binding.equals(KeyMapping.RAIN_INC5.getID())) 
		{
			if (value)
			{
				float percentage = EffectCenter.getRainingPercentage() + 5;
				EffectCenter.setRainingPercentage(percentage);
			}
		}
		
		else if (binding.equals(KeyMapping.RAIN_DEC5.getID())) 
		{
			if (value)
			{
				float percentage = EffectCenter.getRainingPercentage() - 5;
				EffectCenter.setRainingPercentage(percentage);
			}
		}
		
		else if (binding.equals(KeyMapping.FOG_INC5.getID())) 
		{
			if (value)
			{
				float percentage = EffectCenter.getFogPercentage() + 5;
				EffectCenter.setFogPercentage(percentage);
			}
		}
		
		else if (binding.equals(KeyMapping.FOG_DEC5.getID())) 
		{
			if (value)
			{
				float percentage = EffectCenter.getFogPercentage() - 5;
				EffectCenter.setFogPercentage(percentage);
			}
		}
		
		else if (binding.equals(KeyMapping.TIMESTAMP.getID())) 
		{
			if (value)
			{
				System.err.println(System.currentTimeMillis());
			}
		}

		
		else if (binding.equals(KeyMapping.GEARR.getID())) 
		{
			if(value)
				car.getTransmission().setGear(-1, false, true);
			else
				car.getTransmission().setGear(0, false, true);
		}
		
		else if (binding.equals(KeyMapping.GEAR1.getID())) 
		{
			if(value)
				car.getTransmission().setGear(1, false, true);
			else
				car.getTransmission().setGear(0, false, true);
		}
		
		else if (binding.equals(KeyMapping.GEAR2.getID())) 
		{
			if(value)
				car.getTransmission().setGear(2, false, true);
			else
				car.getTransmission().setGear(0, false, true);
		}
		
		else if (binding.equals(KeyMapping.GEAR3.getID())) 
		{
			if(value)
				car.getTransmission().setGear(3, false, true);
			else
				car.getTransmission().setGear(0, false, true);
		}
		
		else if (binding.equals(KeyMapping.GEAR4.getID())) 
		{
			if(value)
				car.getTransmission().setGear(4, false, true);
			else
				car.getTransmission().setGear(0, false, true);
		}

		else if (binding.equals(KeyMapping.GEAR5.getID())) 
		{
			if(value)
				car.getTransmission().setGear(5, false, true);
			else
				car.getTransmission().setGear(0, false, true);
		}
		
		else if (binding.equals(KeyMapping.GEAR6.getID())) 
		{
			if(value)
				car.getTransmission().setGear(6, false, true);
			else
				car.getTransmission().setGear(0, false, true);
		}
		
		else if (binding.equals(KeyMapping.INC_CAM_ANGLE.getID())) 
		{
			if(value)
				sim.getCameraFactory().setAngleBetweenAdjacentCameras(sim.getCameraFactory().getAngleBetweenAdjacentCameras()+1);
		}
		
		else if (binding.equals(KeyMapping.DEC_CAM_ANGLE.getID())) 
		{
			if(value)
				sim.getCameraFactory().setAngleBetweenAdjacentCameras(sim.getCameraFactory().getAngleBetweenAdjacentCameras()-1);
		}
		
		else if (binding.equals(KeyMapping.TOGLE_DISTANCEBAR.getID())) 
		{
			if(value)
				sim.getMotorwayTask().setVisibilityDistanceBar(!sim.getMotorwayTask().getVisibilityDistanceBar());
		}
	}
}
