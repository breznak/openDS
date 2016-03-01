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

package eu.opends.tools;

import java.text.DecimalFormat;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.font.BitmapFont.Align;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.ui.Picture;

import eu.opends.basics.SimulationBasics;
import eu.opends.car.Car;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.DriveAnalyzer;
import eu.opends.main.Simulator;
import eu.opends.niftyGui.MessageBoxGUI;
import eu.opends.niftyGui.KeyMappingGUI.GuiLayer;

/**
 * 
 * @author Rafael Math
 */
public class PanelCenter
{
	private static SimulationBasics sim;

	private static Picture speedometer, RPMgauge, logo, hood, warningFrame;
	private static Node RPMIndicator, speedIndicator;
	private static BitmapText reverseText, neutralText, manualText, driveText, currentGearText, odometerText;	
	private static BitmapText speedText, mileageText, markerText, storeText, deviationText, engineSpeedText, gearText;
	private static BitmapText fuelConsumptionPer100KmText, fuelConsumptionPerHourText, totalFuelConsumptionText;
	private static Node analogIndicators = new Node("AnalogIndicators");
	private static boolean showWarningFrame = false;
	private static int flashingInterval = 500;
	
	// message box
	private static MessageBoxGUI messageBoxGUI;
	private static boolean resolutionHasChanged = false;
	private static int updateDelayCounter = 0;
	
	private static boolean reportedExceeding = false;
	private static SettingsLoader settingsLoader;
	
	private static TreeMap<String, Picture> pictureMap;
	public static TreeMap<String, Picture> getPictureMap() 
	{
		return pictureMap;
	}
	
	
	public static BitmapText getStoreText() 
	{
		return storeText;
	}
	
	
	public static MessageBoxGUI getMessageBox()
	{
		return messageBoxGUI;
	}
	
	
	public static void resetMessageBox()
	{
		messageBoxGUI.close();
		messageBoxGUI = new MessageBoxGUI(sim);
	}
	
	
	public static BitmapText getEngineSpeedText() 
	{
		return engineSpeedText;
	}

	
	public static void init(DriveAnalyzer analyzer)
	{
		sim = analyzer;
		messageBoxGUI = new MessageBoxGUI(sim);
	}
	
	
	public static void showHood(boolean locallyEnabled)
	{
		Simulator.getSettingsLoader();
		boolean globallyEnabled = settingsLoader.getSetting(Setting.General_showHood, false);
		boolean showHood = globallyEnabled && locallyEnabled;
		hood.setCullHint(showHood? CullHint.Dynamic : CullHint.Always);
	}
	
	
	public static void init(Simulator simulator)
	{
		sim = simulator;
		messageBoxGUI = new MessageBoxGUI(sim);

		settingsLoader = Simulator.getSettingsLoader();
		
		String showAnalogString = settingsLoader.getSetting(Setting.General_showAnalogIndicators, "true");
		
		boolean showAnalog;
		if(showAnalogString.isEmpty())
			showAnalog = true;
		else
			showAnalog = showAnalogString.equalsIgnoreCase("true");
		
		boolean showDigital = settingsLoader.getSetting(Setting.General_showDigitalIndicators, false);
		boolean showFuel = settingsLoader.getSetting(Setting.General_showFuelConsumption, false);
				
		CullHint showAnalogIndicators = (showAnalog ? CullHint.Dynamic : CullHint.Always);
		CullHint showDigitalIndicators = (showDigital ? CullHint.Dynamic : CullHint.Always);
		CullHint showFuelConsumption = (showFuel ? CullHint.Dynamic : CullHint.Always);
		CullHint showBrandLogo = CullHint.Always;
		
		float analogIndicatorsScale = settingsLoader.getSetting(Setting.General_analogIndicatorsScale, 1.0f);
		
        // Display a line of text with a default font
		//guiNode.detachAllChildren();
	    BitmapFont guiFont = sim.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        Node guiNode = sim.getGuiNode();
        
        hood = new Picture("hood");
        hood.setImage(sim.getAssetManager(), "Textures/Misc/hood.png", true);
        int width = sim.getSettings().getWidth();
        int height = width/1023*207;
        hood.setWidth(width);
        hood.setHeight(height);
        hood.setPosition(0, 0);
        guiNode.attachChild(hood);

        warningFrame = new Picture("warningFrame");
        warningFrame.setImage(sim.getAssetManager(), "Textures/Misc/warningFrame.png", true);
        warningFrame.setWidth(sim.getSettings().getWidth());
        warningFrame.setHeight(sim.getSettings().getHeight());
        warningFrame.setPosition(0, 0);
        warningFrame.setCullHint(CullHint.Always);
        guiNode.attachChild(warningFrame);
        
        RPMgauge = new Picture("RPMgauge");
        RPMgauge.setImage(sim.getAssetManager(), "Textures/Gauges/RPMgauge.png", true);
        RPMgauge.setWidth(184);
        RPMgauge.setHeight(184);
        RPMgauge.setPosition(0, 15);
        analogIndicators.attachChild(RPMgauge);

        Picture RPMNeedle = new Picture("RPMNeedle");
        RPMNeedle.setImage(sim.getAssetManager(), "Textures/Gauges/indicator.png", true);
        RPMNeedle.setWidth(79);
        RPMNeedle.setHeight(53);
        RPMNeedle.setLocalTranslation(-13,-13,0); // set pivot of needle
        RPMIndicator = new Node("RPMIndicator");        
        RPMIndicator.attachChild(RPMNeedle);
        RPMIndicator.setLocalTranslation(93, 108, 0);
        analogIndicators.attachChild(RPMIndicator);
        
        speedometer = new Picture("speedometer");
        speedometer.setImage(sim.getAssetManager(), "Textures/Gauges/speedometer.png", true);
        speedometer.setWidth(184);
        speedometer.setHeight(184);
        speedometer.setPosition(100, 15);
        analogIndicators.attachChild(speedometer);
        
        Picture speedNeedle = new Picture("speedNeedle");
        speedNeedle.setImage(sim.getAssetManager(), "Textures/Gauges/indicator.png", true);
        speedNeedle.setWidth(79);
        speedNeedle.setHeight(53);
        speedNeedle.setLocalTranslation(-13,-13,0); // set pivot of needle
        speedIndicator = new Node("speedIndicator");  
        speedIndicator.setLocalTranslation(193, 108, 0);
        speedIndicator.attachChild(speedNeedle);        
        analogIndicators.attachChild(speedIndicator);
        
        reverseText = new BitmapText(guiFont, false);
        reverseText.setName("reverseText");
        reverseText.setText("R");
        reverseText.setSize(guiFont.getCharSet().getRenderedSize());
        reverseText.setColor(ColorRGBA.Gray);
        reverseText.setLocalTranslation(50, 65, 0);
        analogIndicators.attachChild(reverseText);
        
        neutralText = new BitmapText(guiFont, false);
        neutralText.setName("neutralText");
        neutralText.setText("N");
        neutralText.setSize(guiFont.getCharSet().getRenderedSize());
        neutralText.setColor(ColorRGBA.Gray);
        neutralText.setLocalTranslation(65, 65, 0);
        analogIndicators.attachChild(neutralText);
        
        manualText = new BitmapText(guiFont, false);
        manualText.setName("manualText");
        manualText.setText("M");
        manualText.setSize(guiFont.getCharSet().getRenderedSize());
        manualText.setColor(ColorRGBA.Gray);
        manualText.setLocalTranslation(80, 65, 0);
        analogIndicators.attachChild(manualText);
        
        driveText = new BitmapText(guiFont, false);
        driveText.setName("driveText");
        driveText.setText("D");
        driveText.setSize(guiFont.getCharSet().getRenderedSize());
        driveText.setColor(ColorRGBA.Gray);
        driveText.setLocalTranslation(97, 65, 0);
        analogIndicators.attachChild(driveText);
        
        currentGearText = new BitmapText(guiFont, false);
        currentGearText.setName("currentGearText");
        currentGearText.setText("1");
        currentGearText.setSize(guiFont.getCharSet().getRenderedSize());
        currentGearText.setColor(ColorRGBA.Green);
        analogIndicators.attachChild(currentGearText);
        
        odometerText = new BitmapText(guiFont, false);
        odometerText.setName("odometerText");
        odometerText.setText("");
        odometerText.setSize(guiFont.getCharSet().getRenderedSize());
        odometerText.setColor(ColorRGBA.LightGray);
        odometerText.setBox(new Rectangle(0, 0, 100, 10));
        odometerText.setAlignment(Align.Right);
        odometerText.setLocalTranslation(120, 60, 0);
        analogIndicators.attachChild(odometerText);
        
        analogIndicators.setCullHint(showAnalogIndicators);
        analogIndicators.scale(analogIndicatorsScale);
        
        guiNode.attachChild(analogIndicators);
        
        markerText = new BitmapText(guiFont, false);
        markerText.setName("markerText");
        markerText.setText("");
        markerText.setCullHint(CullHint.Always);
        markerText.setSize(guiFont.getCharSet().getRenderedSize());
        markerText.setColor(ColorRGBA.LightGray);
        guiNode.attachChild(markerText);

		storeText = new BitmapText(guiFont, false);
		storeText.setName("storeText");
		storeText.setText("");
		storeText.setCullHint(CullHint.Dynamic);
		storeText.setSize(guiFont.getCharSet().getRenderedSize());
		storeText.setColor(ColorRGBA.LightGray);
        guiNode.attachChild(storeText);
        

        speedText = new BitmapText(guiFont, false);
        speedText.setName("speedText");
        speedText.setText("test");
        speedText.setCullHint(showDigitalIndicators);
        speedText.setSize(guiFont.getCharSet().getRenderedSize());
        speedText.setColor(ColorRGBA.LightGray);
        guiNode.attachChild(speedText);
        
        
        mileageText = new BitmapText(guiFont, false);
        mileageText.setName("mileageText");
        mileageText.setText("");
        mileageText.setCullHint(showDigitalIndicators);
        mileageText.setSize(guiFont.getCharSet().getRenderedSize());
        mileageText.setColor(ColorRGBA.LightGray);
        guiNode.attachChild(mileageText);
		
        deviationText = new BitmapText(guiFont, false);
        deviationText.setName("deviationText");
        deviationText.setText("");
        deviationText.setCullHint(CullHint.Always);
        deviationText.setSize(guiFont.getCharSet().getRenderedSize());
        deviationText.setColor(ColorRGBA.Yellow);
        guiNode.attachChild(deviationText);
        
        engineSpeedText = new BitmapText(guiFont, false);
        engineSpeedText.setName("engineSpeedText");
        engineSpeedText.setText("engineSpeedText");
        engineSpeedText.setCullHint(showDigitalIndicators);
        engineSpeedText.setSize(guiFont.getCharSet().getRenderedSize());
        engineSpeedText.setColor(ColorRGBA.LightGray);
        guiNode.attachChild(engineSpeedText);
        
        gearText = new BitmapText(guiFont, false);
        gearText.setName("gearText");
        gearText.setText("gearText");
        gearText.setCullHint(showDigitalIndicators);
        gearText.setSize(guiFont.getCharSet().getRenderedSize());
        gearText.setColor(ColorRGBA.LightGray);
        guiNode.attachChild(gearText);
		
        fuelConsumptionPer100KmText = new BitmapText(guiFont, false);
        fuelConsumptionPer100KmText.setName("fuelConsumptionText");
        fuelConsumptionPer100KmText.setText("fuelConsumptionText");
        fuelConsumptionPer100KmText.setCullHint(showFuelConsumption);
        fuelConsumptionPer100KmText.setSize(guiFont.getCharSet().getRenderedSize());
        fuelConsumptionPer100KmText.setColor(ColorRGBA.LightGray);
        guiNode.attachChild(fuelConsumptionPer100KmText);
        
        fuelConsumptionPerHourText = new BitmapText(guiFont, false);
        fuelConsumptionPerHourText.setName("fuelConsumptionPerHourText");
        fuelConsumptionPerHourText.setText("fuelConsumptionPerHourText");
        fuelConsumptionPerHourText.setCullHint(showFuelConsumption);
        fuelConsumptionPerHourText.setSize(guiFont.getCharSet().getRenderedSize());
        fuelConsumptionPerHourText.setColor(ColorRGBA.LightGray);
        guiNode.attachChild(fuelConsumptionPerHourText);
        	
        totalFuelConsumptionText = new BitmapText(guiFont, false);
        totalFuelConsumptionText.setName("totalFuelConsumptionText");
        totalFuelConsumptionText.setText("totalFuelConsumptionText");
        totalFuelConsumptionText.setCullHint(showFuelConsumption);
        totalFuelConsumptionText.setSize(guiFont.getCharSet().getRenderedSize());
        totalFuelConsumptionText.setColor(ColorRGBA.LightGray);
        guiNode.attachChild(totalFuelConsumptionText);

        logo = new Picture("DFKIlogo");
        logo.setImage(sim.getAssetManager(), "Textures/Logo/DFKI.jpg", true);
        logo.setWidth(98);
        logo.setHeight(43);
        logo.setCullHint(showBrandLogo);
        guiNode.attachChild(logo);
        
        
        pictureMap = Simulator.getDrivingTask().getSceneLoader().getPictures();
        for(Entry<String,Picture> entry : pictureMap.entrySet())     	
        	guiNode.attachChild(entry.getValue());
        
		resetPanelPosition(true);
	}
	
	
	public static void resetPanelPosition(boolean isAutomaticTransmission)
	{
		int rightmostPos = getRightmostPosition();	
		int maxHeight = sim.getSettings().getHeight();
		
        resetGearTextPosition(isAutomaticTransmission);
        
		int analogIndicatorsLeft = getAnalogIndicatorsLeft();
		int analogIndicatorsBottom = getAnalogIndicatorsBottom();
        analogIndicators.setLocalTranslation(analogIndicatorsLeft, analogIndicatorsBottom, 0);
        
        markerText.setLocalTranslation(0, 35, 0);
		storeText.setLocalTranslation(0, 50, 0);
        speedText.setLocalTranslation(rightmostPos - 90, 20, 0);
        mileageText.setLocalTranslation(0, 20, 0);
        deviationText.setLocalTranslation(0, 80, 0);
        engineSpeedText.setLocalTranslation(rightmostPos / 4f , 20, 0);
        gearText.setLocalTranslation(rightmostPos / 2f , 20, 0);
        fuelConsumptionPer100KmText.setLocalTranslation(rightmostPos / 2f , 20, 0);
        fuelConsumptionPerHourText.setLocalTranslation(rightmostPos / 4f , 20, 0);
        totalFuelConsumptionText.setLocalTranslation(20 , 20, 0);
        
        logo.setLocalTranslation(0, maxHeight-43 ,0); 
	}


	private static int getAnalogIndicatorsBottom() 
	{
		int top = settingsLoader.getSetting(Setting.General_analogIndicatorsTop, -1);
		int bottom = settingsLoader.getSetting(Setting.General_analogIndicatorsBottom, -1);
		
		int analogIndicatorsBottom = getBottommostPosition();
		if(bottom != -1)
			analogIndicatorsBottom = bottom;
		else if (top != -1)
			analogIndicatorsBottom = sim.getSettings().getHeight() - top;
		
		return analogIndicatorsBottom;
	}


	private static int getAnalogIndicatorsLeft() 
	{
		int left = settingsLoader.getSetting(Setting.General_analogIndicatorsLeft, -1);
		int right = settingsLoader.getSetting(Setting.General_analogIndicatorsRight, -1);
		
		int analogIndicatorsLeft = getRightmostPosition() - 300;
		if(left != -1)
			analogIndicatorsLeft = left;
		else if (right != -1)
			analogIndicatorsLeft = sim.getSettings().getWidth() - right;
		
		return analogIndicatorsLeft;
	}


	private static int getRightmostPosition() 
	{
		// moves position of gauges to center screen if more than 1 screen available
		if(sim.getNumberOfScreens() == 1)
			return sim.getSettings().getWidth();
		else
			return (int) (sim.getSettings().getWidth()*1.85f/3.0f);
		
		//return 1100;
	}
	
	
	private static int getBottommostPosition() 
	{
		return 0;
		//return 200;
	}
	
	
	public static void resetGearTextPosition(boolean isAutomaticTransmission)
	{		
		if(isAutomaticTransmission)
			currentGearText.setLocalTranslation(97, 48, 0);
		else			
			currentGearText.setLocalTranslation(80, 48, 0);
	}

	
	public static void reportResolutionChange()
	{
		resolutionHasChanged = true;
	}
	

	private static long lastTime = 0;
	private static boolean frameVisible = false;
	
	
	public static void update() 
	{
		Car car = ((Simulator)sim).getCar();
		
		updateSpeedText(car);
		
		updateMilageText(car);
		
		// update message on screen
		messageBoxGUI.update();
		
		if(fixRPM != 0)
			setRPMIndicator(fixRPM);
		else
			setRPMIndicator(car.getTransmission().getRPM()); 
		
		if(resolutionHasChanged && (++updateDelayCounter%2==0))
		{
			resetPanelPosition(car.getTransmission().isAutomatic());
			resetMessageBox();
			
			// restore change resolution menu
			sim.getKeyMappingGUI().showDialog();
			sim.getKeyMappingGUI().openLayer(GuiLayer.GRAPHICSETTINGS);
			
			resolutionHasChanged = false;
		}
		
		if(showWarningFrame)
		{
			long currentTime = System.currentTimeMillis();
			if(currentTime - lastTime > flashingInterval)
			{
				lastTime = currentTime;

				frameVisible = !frameVisible;
				warningFrame.setCullHint(frameVisible ? CullHint.Dynamic : CullHint.Always);
			}
		}
		else 
			warningFrame.setCullHint(CullHint.Always);
	}
	
	
	private static void updateMilageText(Car car) 
	{
		float mileage = car.getMileage();
		String mileageString;
		
		if(mileage < 1000)
			mileageString = ((int)mileage) + " m";
		else
			mileageString = ((int)(mileage/10f))/100f + " km";
		
		mileageText.setText(mileageString);
		
		float odometer = ((int)mileage)/1000f;
		DecimalFormat df = new DecimalFormat("#0.000");
		odometerText.setText(df.format(odometer) + " km");
	}


	private static void setSpeedIndicator(float speed) 
	{
		// bounds of speed indicator
		speed = Math.min(Math.max(speed, 0), 260);
		
		// compute speed indicator's rotation
		// zero-point of scale is 192 degrees to the left
		// 1 speed unit per degree
		float degree =  192f - (speed/1f);
		float radians = FastMath.PI/180f * degree;
		
		// set speed indicator's rotation
		Quaternion rotation = new Quaternion();
		rotation.fromAngles(0, 0, radians);
		speedIndicator.setLocalRotation(rotation);		
	}

	
	private static void setRPMIndicator(float rpm) 
	{
		// bounds of speed indicator
		rpm = Math.min(Math.max(rpm, 0), 7500);
		
		// compute RPM indicator's rotation
		// zero-point of scale is 192 degrees to the left
		// 50 RPM units per degree
		float degree = 192f - (rpm/50f);
		float radians = FastMath.PI/180f * degree;
		
		// set RPM indicator's rotation
		Quaternion rotation = new Quaternion();
		rotation.fromAngles(0, 0, radians);
		RPMIndicator.setLocalRotation(rotation);
	}
	
	
	private static float fixSpeed = 0;
	public static void setFixSpeed(float speed)
	{
		fixSpeed = speed;
	}
	
	
	private static float fixRPM = 0;
	public static void setFixRPM(float rpm)
	{
		fixRPM = rpm;
	}
	
	
	private static void updateSpeedText(Car car) 
	{
		float carSpeed;
		
		if(fixSpeed != 0)
			carSpeed = fixSpeed;
		else
			carSpeed = Math.round(car.getCurrentSpeedKmh() * 10)/10f;
		
		float currentSpeedLimit = SpeedControlCenter.getCurrentSpeedlimit();
		float upcomingSpeedLimit = SpeedControlCenter.getUpcomingSpeedlimit();
		
		if(Math.abs(carSpeed) <= 0.7f)
		{
			speedText.setText("0.0 km/h");
			setSpeedIndicator(0);		
		}
		else
		{
			speedText.setText("" + carSpeed + " km/h");
			setSpeedIndicator(carSpeed);		
		}
		
		if((currentSpeedLimit != 0) && ((carSpeed > currentSpeedLimit+10) || (carSpeed < upcomingSpeedLimit-10)))
		{
			speedText.setColor(ColorRGBA.Red);
			if(!reportedExceeding)
			{
				if(carSpeed > currentSpeedLimit+10)
					Simulator.getDrivingTaskLogger().reportSpeedLimitExceeded();
				else
					Simulator.getDrivingTaskLogger().reportSpeedLimitUnderExceeded();
				reportedExceeding = true;
			}
		}
		else
		{
			if(reportedExceeding)
			{
				Simulator.getDrivingTaskLogger().reportSpeedNormal();
				reportedExceeding = false;
			}
			speedText.setColor(ColorRGBA.LightGray);
		}
	}


	public static void setGearIndicator(Integer gear, boolean isAutomaticTransmission) 
	{
		if(isAutomaticTransmission)
			gearText.setText("Gear: A" + gear);
		else if (gear == 0)
			gearText.setText("Gear: N");
		else if (gear == -1)
			gearText.setText("Gear: R");
		else
			gearText.setText("Gear: M" + gear);
		
		
		// set indicator in RPM gauge
		reverseText.setColor(ColorRGBA.Gray);
		neutralText.setColor(ColorRGBA.Gray);
		manualText.setColor(ColorRGBA.Gray);
		driveText.setColor(ColorRGBA.Gray);
		
		if(isAutomaticTransmission)
		{
			driveText.setColor(ColorRGBA.Red);
			currentGearText.setText(gear.toString());
		}
		else if (gear == 0)
		{
			neutralText.setColor(ColorRGBA.Red);
			currentGearText.setText("");
		}
		else if (gear == -1)
		{
			reverseText.setColor(ColorRGBA.Red);
			currentGearText.setText("");
		}
		else
		{
			manualText.setColor(ColorRGBA.Red);
			currentGearText.setText(gear.toString());
		}
		
		resetGearTextPosition(isAutomaticTransmission);
	}


	public static void setLitersPer100Km(float litersPer100Km) 
	{
		if(litersPer100Km < 0)
			fuelConsumptionPer100KmText.setText("-- L/100km");
		else
		{
			// round fuel consumption value to 2 decimal places
			DecimalFormat f = new DecimalFormat("#0.00");
			fuelConsumptionPer100KmText.setText(f.format(litersPer100Km) + " L/100km");
		}
	}

	
	public static void setLitersPerHour(float litersPerHour) 
	{
		// round fuel consumption per hour to 2 decimal places
		DecimalFormat f = new DecimalFormat("#0.00");
		fuelConsumptionPerHourText.setText(f.format(litersPerHour) + " L/h");
	}
	

	public static void setTotalFuelConsumption(float totalFuelConsumption) 
	{
		// round total fuel consumption per 100 Km to 3 decimal places
		DecimalFormat f = new DecimalFormat("#0.000");
		totalFuelConsumptionText.setText(f.format(totalFuelConsumption) + " L");
	}

	
	public static void showWarningFrame(boolean showWarningFrame) 
	{
		PanelCenter.showWarningFrame = showWarningFrame;
		PanelCenter.flashingInterval = 500;
	}
	

	public static void showWarningFrame(boolean showWarningFrame, int flashingInterval) 
	{
		PanelCenter.showWarningFrame = showWarningFrame;
		PanelCenter.flashingInterval = flashingInterval;
	}
	
}
