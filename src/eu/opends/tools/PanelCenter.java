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

package eu.opends.tools;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TreeMap;
import java.util.Date;
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
import eu.opends.car.SteeringCar;
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

	private static Picture speedometer, RPMgauge, logo, hood, warningFrame, leftTurnSignal, 
							leftTurnSignalOff, rightTurnSignal, rightTurnSignalOff, handBrakeIndicator, handBrakeIndicatorOff;
	private static Node RPMIndicator, speedIndicator, cruiseControlIndicator;
	private static BitmapText reverseText, neutralText, manualText, driveText, currentGearText, odometerText;	
	private static BitmapText speedText, mileageText, markerText, storeText, deviationText, engineSpeedText, gearText;
	private static BitmapText fuelConsumptionPer100KmText, fuelConsumptionPerHourText, totalFuelConsumptionText;
	private static Node analogIndicators = new Node("AnalogIndicators");
	private static boolean showWarningFrame = false;
	private static int flashingInterval = 500;
	
	// OpenDS-Rift - digital rift panel
	private static BitmapText riftSpeedText;
	private static BitmapText riftRpmText;
	private static BitmapText riftKmText;
	private static String riftRpm = "";
	private static String riftGear = "";
	
	// OpenDS-Maritime - digital maritime panel ***EXPERIMENTAL***
	private static MaritimeDisplayMode maritimeDisplayMode = MaritimeDisplayMode.Off;
	private static BitmapText maritimeHeadingText;
	private static BitmapText maritimeSpeedText;
	private static BitmapText maritimeDepthText;
	private static BitmapText maritimeWindText;
	private static BitmapText maritimeTimeText;
	private static BitmapText maritimeDistanceText;
	private static BitmapText maritimeScenarioText;
	private static BitmapText maritimeLatitudeText;
	private static BitmapText maritimeLongitudeText;
	private static long startTime = 0;
	private static Node compassIndicator = new Node("compassIndicator");
	private static BitmapText cogText, sogText, magText, latText, longText, timeText, depthText;
	
	public enum MaritimeDisplayMode 
	{
		Off, Panel, Compass, MultiFunctionDisplay, All
	}
	
	
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
		
		// OpenDS-Rift - deactivate other panels
		if (Simulator.oculusRiftAttached) {
			showDigital = false;
			showAnalog = false;
			showFuel = false;
		}

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
        int imageWidth = 1023; //998;
        int imageHeight = 207; //294
        int width = sim.getSettings().getWidth();
        int height = width/imageWidth*imageHeight;
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
        
        handBrakeIndicator = new Picture("handBrakeIndicator");
        handBrakeIndicator.setImage(sim.getAssetManager(), "Textures/Gauges/handBrakeIndicatorSmall.png", true);
        handBrakeIndicator.setWidth(28);
        handBrakeIndicator.setHeight(21);
        handBrakeIndicator.setLocalTranslation(70, 65, 0);
        handBrakeIndicator.setCullHint(CullHint.Always);
        analogIndicators.attachChild(handBrakeIndicator);
        
        handBrakeIndicatorOff = new Picture("handBrakeIndicatorOff");
        handBrakeIndicatorOff.setImage(sim.getAssetManager(), "Textures/Gauges/handBrakeIndicatorSmallOff.png", true);
        handBrakeIndicatorOff.setWidth(28);
        handBrakeIndicatorOff.setHeight(21);
        handBrakeIndicatorOff.setLocalTranslation(70, 65, 0);
        handBrakeIndicatorOff.setCullHint(CullHint.Inherit);
        analogIndicators.attachChild(handBrakeIndicatorOff);        
        
        leftTurnSignal = new Picture("leftTurnSignal");
        leftTurnSignal.setImage(sim.getAssetManager(), "Textures/Gauges/greenArrowSmall.png", true);
        leftTurnSignal.setWidth(25);
        leftTurnSignal.setHeight(28);
        leftTurnSignal.setLocalTranslation(188, 60, 0);
        leftTurnSignal.rotate(0, FastMath.PI, 0);
        leftTurnSignal.setCullHint(CullHint.Always);
        analogIndicators.attachChild(leftTurnSignal);
        
        leftTurnSignalOff = new Picture("leftTurnSignalOff");
        leftTurnSignalOff.setImage(sim.getAssetManager(), "Textures/Gauges/greenArrowSmallOff.png", true);
        leftTurnSignalOff.setWidth(25);
        leftTurnSignalOff.setHeight(28);
        leftTurnSignalOff.setLocalTranslation(188, 60, 0);
        leftTurnSignalOff.rotate(0, FastMath.PI, 0);
        leftTurnSignal.setCullHint(CullHint.Inherit);
        analogIndicators.attachChild(leftTurnSignalOff);
        
        rightTurnSignal = new Picture("rightTurnSignal");
        rightTurnSignal.setImage(sim.getAssetManager(), "Textures/Gauges/greenArrowSmall.png", true);
        rightTurnSignal.setWidth(25);
        rightTurnSignal.setHeight(28);
        rightTurnSignal.setLocalTranslation(200, 60, 0);
        leftTurnSignal.setCullHint(CullHint.Always);
        analogIndicators.attachChild(rightTurnSignal);
        
        rightTurnSignalOff = new Picture("rightTurnSignalOff");
        rightTurnSignalOff.setImage(sim.getAssetManager(), "Textures/Gauges/greenArrowSmallOff.png", true);
        rightTurnSignalOff.setWidth(25);
        rightTurnSignalOff.setHeight(28);
        rightTurnSignalOff.setLocalTranslation(200, 60, 0);
        leftTurnSignal.setCullHint(CullHint.Inherit);
        analogIndicators.attachChild(rightTurnSignalOff);
        
        Picture cruiseControlNeedle = new Picture("cruiseControlNeedle");
        cruiseControlNeedle.setImage(sim.getAssetManager(), "Textures/Gauges/cruiseControlIndicator.png", true);
        cruiseControlNeedle.setWidth(100);
        cruiseControlNeedle.setHeight(70);
        cruiseControlNeedle.setLocalTranslation(-13,-13,0); // set pivot of needle
        cruiseControlIndicator = new Node("cruiseControlIndicator");
        cruiseControlIndicator.setLocalTranslation(193, 108, 0);
        cruiseControlIndicator.attachChild(cruiseControlNeedle);
        analogIndicators.attachChild(cruiseControlIndicator);
        
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
        if(Simulator.oculusRiftAttached)
        	analogIndicators.scale(2*analogIndicatorsScale, analogIndicatorsScale, analogIndicatorsScale);
        else
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
        
        
        // OpenDS-Rift - calc screen center
        int screenWidth = sim.getSettings().getWidth();
        int screenHeight = sim.getSettings().getHeight();

        int startX = screenWidth / 2 + 200;
        int startY = screenHeight / 2;
        int line = 1;
        final float BOX_WIDTH = 220;
        
        // OpenDS-Rift - default font for line space calculation
        BitmapText defaultText = new BitmapText(guiFont, false);
        defaultText.setSize(guiFont.getCharSet().getRenderedSize());
        defaultText.setLocalScale(2, 1, 1);
        float defaultLineHeight = defaultText.getLineHeight();
        
        // OpenDS-Rift - speed
        riftSpeedText = new BitmapText(guiFont, false);          
        riftSpeedText.setSize(guiFont.getCharSet().getRenderedSize());
        riftSpeedText.setColor(ColorRGBA.White);
        riftSpeedText.setText("");
        riftSpeedText.setLocalScale(2, 1, 1);
        riftSpeedText.setLocalTranslation(startX, startY, 0);
        
        // OpenDS-Rift - rpm / gear
        riftRpmText = new BitmapText(guiFont, false);          
        riftRpmText.setSize(guiFont.getCharSet().getRenderedSize());
        riftRpmText.setColor(ColorRGBA.White);
        riftRpmText.setText("");
        riftRpmText.setLocalScale(2, 1, 1);
        riftRpmText.setLocalTranslation(startX, startY - defaultLineHeight * line++, 0);
        
//        // OpenDS-Rift - gear
//        riftGearText = new BitmapText(guiFont, false);          
//        riftGearText.setSize(guiFont.getCharSet().getRenderedSize());
//        riftGearText.setColor(ColorRGBA.White);
//        riftGearText.setText("");
//        riftGearText.setLocalScale(2, 1, 1);
//        riftGearText.setLocalTranslation(startX, startY - defaultLineHeight * line++, 0);
        
        // OpenDS-Rift - km
        riftKmText = new BitmapText(guiFont, false);          
        riftKmText.setSize(guiFont.getCharSet().getRenderedSize());
        riftKmText.setColor(ColorRGBA.White);
        riftKmText.setText("");
        riftKmText.setLocalScale(2, 1, 1);
        riftKmText.setLocalTranslation(startX, startY - defaultLineHeight * line++, 0);
        
        // test box
        float paddingX = 16;
        float paddingY = 10;
        float boxWidth = BOX_WIDTH + paddingX;
        float boxHeight = defaultLineHeight * line + paddingY;
        
        
//        Box b = new Box(boxWidth, boxHeight, 0);
//        Geometry geom = new Geometry("Box", b);
        
//        Material mat = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setTexture("ColorMap", 
//                sim.getAssetManager().loadTexture("Textures/OculusRift/transparency.png"));
//        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
//        geom.setQueueBucket(Bucket.Transparent);
//        mat.setColor("Color", ColorRGBA.Gray);
//        geom.setMaterial(mat);
        
//        geom.setLocalTranslation(startX + boxWidth - paddingX, startY - boxHeight + paddingY, 0);
        
        // test

        
        Picture panelBackground = new Picture("PanelBackground");
        panelBackground.setImage(sim.getAssetManager(), "Textures/OculusRift/transparency.png", true);
        
        panelBackground.setWidth(boxWidth);
        panelBackground.setHeight(boxHeight);
        panelBackground.setPosition(startX - paddingX / 2, startY - boxHeight + paddingY / 2);
        
        
        // OpenDS-Rift - attach riftPanel to GuiNode 
        if (Simulator.oculusRiftAttached) {
            Node riftPanel = new Node("riftPanel");
        	riftPanel.attachChild(riftSpeedText);
        	riftPanel.attachChild(riftRpmText);
//        	riftPanel.attachChild(riftGearText);
        	riftPanel.attachChild(riftKmText);
//        	guiNode.attachChild(geom);
        	guiNode.attachChild(panelBackground);
        	guiNode.attachChild(riftPanel);
        }
        
        
        String maritimeDisplayModeString = settingsLoader.getSetting(Setting.Maritime_displayMode, "Off");
        if(!maritimeDisplayModeString.isEmpty())
        	maritimeDisplayMode = MaritimeDisplayMode.valueOf(maritimeDisplayModeString);
        initMaritimePanel();
        
		resetPanelPosition(true);
	}
	
	
	private static void initMaritimePanel()
	{
        // OpenDS-Maritime - calc screen center
        int screenWidth = sim.getSettings().getWidth();
        int screenHeight = sim.getSettings().getHeight();

        BitmapFont guiFont = sim.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        BitmapFont gaugeFont = sim.getAssetManager().loadFont("Interface/Fonts/Arial32px.fnt");
        Node guiNode = sim.getGuiNode();
        
        if(maritimeDisplayMode == MaritimeDisplayMode.Panel || maritimeDisplayMode == MaritimeDisplayMode.All)
        {
	        // OpenDS-Maritime - default font for line space calculation
	        BitmapText defaultText = new BitmapText(guiFont, false);
	        defaultText.setSize(guiFont.getCharSet().getRenderedSize());
	        defaultText.setLocalScale(1, 1, 1);
	        float defaultLineHeight = defaultText.getLineHeight();
	        
	        float partialWidth = screenWidth / 5.0f;
	        float startX1 = 0;
	        float startX2 = 1 * partialWidth;
	        float startX3 = 2 * partialWidth;
	        float startX4 = 3 * partialWidth;
	        float startX5 = 4 * partialWidth;
	        float startY1 = screenHeight;
	        float startY2 = screenHeight - 0.5f*defaultLineHeight;
	        float startY3 = screenHeight - defaultLineHeight;
	        final float BOX_WIDTH = screenWidth;
	        
	        // OpenDS-Maritime - heading
	        maritimeHeadingText = new BitmapText(guiFont, false);          
	        maritimeHeadingText.setSize(guiFont.getCharSet().getRenderedSize());
	        maritimeHeadingText.setColor(ColorRGBA.White);
	        maritimeHeadingText.setText("");
	        maritimeHeadingText.setLocalScale(1, 1, 1);
	        maritimeHeadingText.setLocalTranslation(startX1, startY1, 0);
	        
	        // OpenDS-Maritime - speed
	        maritimeSpeedText = new BitmapText(guiFont, false);          
	        maritimeSpeedText.setSize(guiFont.getCharSet().getRenderedSize());
	        maritimeSpeedText.setColor(ColorRGBA.White);
	        maritimeSpeedText.setText("");
	        maritimeSpeedText.setLocalScale(1, 1, 1);
	        maritimeSpeedText.setLocalTranslation(startX1, startY3, 0);
	        
	        // OpenDS-Maritime - depth
	        maritimeDepthText = new BitmapText(guiFont, false);          
	        maritimeDepthText.setSize(guiFont.getCharSet().getRenderedSize());
	        maritimeDepthText.setColor(ColorRGBA.White);
	        maritimeDepthText.setText("");
	        maritimeDepthText.setLocalScale(1, 1, 1);
	        maritimeDepthText.setLocalTranslation(startX2, startY1, 0);
	        
	        // OpenDS-Maritime - wind
	        maritimeWindText = new BitmapText(guiFont, false);          
	        maritimeWindText.setSize(guiFont.getCharSet().getRenderedSize());
	        maritimeWindText.setColor(ColorRGBA.White);
	        maritimeWindText.setText("");
	        maritimeWindText.setLocalScale(1, 1, 1);
	        maritimeWindText.setLocalTranslation(startX2, startY3, 0);
	        
	        // OpenDS-Maritime - time
	        maritimeTimeText = new BitmapText(guiFont, false);          
	        maritimeTimeText.setSize(guiFont.getCharSet().getRenderedSize());
	        maritimeTimeText.setColor(ColorRGBA.White);
	        maritimeTimeText.setText("");
	        maritimeTimeText.setLocalScale(1, 1, 1);
	        maritimeTimeText.setLocalTranslation(startX3, startY1, 0);
	        
	        // OpenDS-Maritime - distance
	        maritimeDistanceText = new BitmapText(guiFont, false);          
	        maritimeDistanceText.setSize(guiFont.getCharSet().getRenderedSize());
	        maritimeDistanceText.setColor(ColorRGBA.White);
	        maritimeDistanceText.setText("");
	        maritimeDistanceText.setLocalScale(1, 1, 1);
	        maritimeDistanceText.setLocalTranslation(startX3, startY3, 0);
	        
	        // OpenDS-Maritime - latitude
	        maritimeLatitudeText = new BitmapText(guiFont, false);          
	        maritimeLatitudeText.setSize(guiFont.getCharSet().getRenderedSize());
	        maritimeLatitudeText.setColor(ColorRGBA.White);
	        maritimeLatitudeText.setText("");
	        maritimeLatitudeText.setLocalScale(1, 1, 1);
	        maritimeLatitudeText.setLocalTranslation(startX4, startY1, 0);
	        
	        // OpenDS-Maritime - latitude
	        maritimeLongitudeText = new BitmapText(guiFont, false);          
	        maritimeLongitudeText.setSize(guiFont.getCharSet().getRenderedSize());
	        maritimeLongitudeText.setColor(ColorRGBA.White);
	        maritimeLongitudeText.setText("");
	        maritimeLongitudeText.setLocalScale(1, 1, 1);
	        maritimeLongitudeText.setLocalTranslation(startX4, startY3, 0);
	        
	        // OpenDS-Maritime - scenario
	        maritimeScenarioText = new BitmapText(guiFont, false);          
	        maritimeScenarioText.setSize(guiFont.getCharSet().getRenderedSize());
	        maritimeScenarioText.setColor(ColorRGBA.White);
	        maritimeScenarioText.setText("");
	        maritimeScenarioText.setLocalScale(1, 1, 1);
	        maritimeScenarioText.setLocalTranslation(startX5, startY2, 0);
	           
	        // test box
	        float paddingX = 16;
	        float paddingY = 10;
	        float boxWidth = BOX_WIDTH + paddingX;
	        float boxHeight = defaultLineHeight * 2 + paddingY;
	               
	        Picture panelBackground = new Picture("PanelBackground");
	        panelBackground.setImage(sim.getAssetManager(), "Textures/OculusRift/transparency.png", true);
	        
	        panelBackground.setWidth(boxWidth);
	        panelBackground.setHeight(boxHeight);
	        panelBackground.setPosition(startX1 - paddingX / 2, startY1 - boxHeight + paddingY / 2);
	        
	        
	        // OpenDS-Maritime - attach riftPanel to GuiNode       
	        Node maritimePanel = new Node("maritimePanel");
	        maritimePanel.attachChild(maritimeHeadingText);
	        maritimePanel.attachChild(maritimeSpeedText);
	        maritimePanel.attachChild(maritimeDepthText);
	        maritimePanel.attachChild(maritimeWindText);
	        maritimePanel.attachChild(maritimeTimeText);
	        maritimePanel.attachChild(maritimeDistanceText);
	        maritimePanel.attachChild(maritimeLatitudeText);
	        maritimePanel.attachChild(maritimeLongitudeText);
	        maritimePanel.attachChild(maritimeScenarioText);
	    	guiNode.attachChild(panelBackground);
	    	guiNode.attachChild(maritimePanel);
        }
        
    	
        if(maritimeDisplayMode == MaritimeDisplayMode.Compass || maritimeDisplayMode == MaritimeDisplayMode.All)
        {
	    	// setup compass
	    	float compassScalingFactor = 0.3f;
	    	Node compassNode = new Node("compassNode");
	    	
	        Picture compassRose = new Picture("compassRose");
	        compassRose.setImage(sim.getAssetManager(), "Textures/Gauges/compassRose.png", true);
	        compassRose.setWidth(757);
	        compassRose.setHeight(756);
	        compassRose.setLocalTranslation(-378.5f,-378,0); // set pivot of compass rose   
	        //compassNode.attachChild(compassRose);
	        compassIndicator.attachChild(compassRose);
	                
	        Picture compassNeedle = new Picture("compassNeedle");
	        compassNeedle.setImage(sim.getAssetManager(), "Textures/Gauges/compassNeedle.png", true);
	        compassNeedle.setWidth(42);
	        compassNeedle.setHeight(552);
	        compassNeedle.setLocalTranslation(-21,-276,0); // set pivot of needle        
	        compassIndicator.attachChild(compassNeedle);
	        compassIndicator.setLocalTranslation(378.5f, 378, 0);
	        compassNode.attachChild(compassIndicator);
	        
	        Picture headingIndicator = new Picture("headingIndicator");
	        headingIndicator.setImage(sim.getAssetManager(), "Textures/Gauges/headingIndicator.png", true);
	        headingIndicator.setWidth(777);
	        headingIndicator.setHeight(776);
	        headingIndicator.setLocalTranslation(-10,-10,0); // set pivot of heading indicator
	        compassNode.attachChild(headingIndicator);
	        
	        compassNode.setLocalTranslation(screenWidth - (757*compassScalingFactor) - 50, 50, 0);
	        compassNode.scale(compassScalingFactor);
	        guiNode.attachChild(compassNode);
        }
        
        
        if(maritimeDisplayMode == MaritimeDisplayMode.MultiFunctionDisplay || maritimeDisplayMode == MaritimeDisplayMode.All)
        {
	    	// setup displays
	        // ==============
	    	float displayScalingFactor = 0.16f;
	    	Node displaysNode = new Node("displaysNode");
	    	
	    	// setup left display
	        Picture displayLeftPicture = new Picture("displayLeft");
	        displayLeftPicture.setImage(sim.getAssetManager(), "Textures/Gauges/displayLeft.png", true);
	        displayLeftPicture.setWidth(1428);
	        displayLeftPicture.setHeight(1484);
	        
	        Node displayLeftNode = new Node("displayLeftNode");
	        displayLeftNode.setLocalTranslation(-1500,0,0);
	        displayLeftNode.attachChild(displayLeftPicture);
	        
	        // course over ground text
	        cogText = new BitmapText(gaugeFont, false);          
	        cogText.setSize(gaugeFont.getCharSet().getRenderedSize());
	        cogText.setColor(ColorRGBA.Black);
	        cogText.setText("360°");
	        cogText.setLocalScale(5.5f, 5.5f, 1);
	        cogText.setLocalTranslation(910,1170,0);
	        displayLeftNode.attachChild(cogText);
	        
	        // speed over ground text
	        sogText = new BitmapText(gaugeFont, false);          
	        sogText.setSize(gaugeFont.getCharSet().getRenderedSize());
	        sogText.setColor(ColorRGBA.Black);
	        sogText.setText("0.0");
	        sogText.setLocalScale(6, 6, 1);
	        sogText.setLocalTranslation(930,770,0);
	        displayLeftNode.attachChild(sogText);
	
	        displaysNode.attachChild(displayLeftNode);
	        
	        
	        // setup center display
	        Picture displayCenterPicture = new Picture("displayCenterPicture");
	        displayCenterPicture.setImage(sim.getAssetManager(), "Textures/Gauges/displayCenter.png", true);
	        displayCenterPicture.setWidth(1428);
	        displayCenterPicture.setHeight(1484);
	
	        Node displayCenterNode = new Node("displayCenterNode");
	        displayCenterNode.setLocalTranslation(0,0,0);
	        displayCenterNode.attachChild(displayCenterPicture);
	        
	        // magnetic track text
	        magText = new BitmapText(gaugeFont, false);          
	        magText.setSize(gaugeFont.getCharSet().getRenderedSize());
	        magText.setColor(ColorRGBA.Black);
	        magText.setText("360");
	        magText.setLocalScale(17, 17, 1);
	        magText.setLocalTranslation(280,1120,0);
	        displayCenterNode.attachChild(magText);
	        
	        displaysNode.attachChild(displayCenterNode);
	        
	        
	        // setup right display
	        Picture displayRightPicture = new Picture("displayRightPicture");
	        displayRightPicture.setImage(sim.getAssetManager(), "Textures/Gauges/displayRight.png", true);
	        displayRightPicture.setWidth(1428);
	        displayRightPicture.setHeight(1484);
	        
	        Node displayRightNode = new Node("displayRightNode");
	        displayRightNode.setLocalTranslation(1500,0,0);
	        displayRightNode.attachChild(displayRightPicture);
	        
	        // latitude text
	        latText = new BitmapText(gaugeFont, false);          
	        latText.setSize(gaugeFont.getCharSet().getRenderedSize());
	        latText.setColor(ColorRGBA.Black);
	        latText.setText("N");
	        latText.setLocalScale(5, 5, 1);
	        latText.setLocalTranslation(220,1110,0);
	        displayRightNode.attachChild(latText);
	        
	        // longitude text
	        longText = new BitmapText(gaugeFont, false);          
	        longText.setSize(gaugeFont.getCharSet().getRenderedSize());
	        longText.setColor(ColorRGBA.Black);
	        longText.setText("E");
	        longText.setLocalScale(5, 5, 1);
	        longText.setLocalTranslation(220,720,0);
	        displayRightNode.attachChild(longText);
	        
	        // time text
	        timeText = new BitmapText(gaugeFont, false);          
	        timeText.setSize(gaugeFont.getCharSet().getRenderedSize());
	        timeText.setColor(ColorRGBA.Black);
	        timeText.setText("hh:mm:ss");
	        timeText.setLocalScale(5, 5, 1);
	        timeText.setLocalTranslation(890,1110,0);
	        displayRightNode.attachChild(timeText);
	        
	        // depth text
	        depthText = new BitmapText(gaugeFont, false);          
	        depthText.setSize(gaugeFont.getCharSet().getRenderedSize());
	        depthText.setColor(ColorRGBA.Black);
	        depthText.setText("0 m");
	        depthText.setLocalScale(5, 5, 1);
	        depthText.setLocalTranslation(920,720,0);
	        displayRightNode.attachChild(depthText);
	        
	        displaysNode.attachChild(displayRightNode);
	        
	        
	        // move and scale all displays at once
	        displaysNode.setLocalTranslation(screenWidth/2.0f - (714*displayScalingFactor), 10, 0);
	        displaysNode.scale(displayScalingFactor);
	        guiNode.attachChild(displaysNode);
        }
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
		SteeringCar car = ((Simulator)sim).getCar();
		
		updateSpeedText(car);
		
		updateMilageText(car);
		
		// OpenDS-Rift - update connected panel strings
		riftRpmText.setText(riftRpm + " rpm / " + riftGear);
		
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
		
		updateMaritimePanel(car);
	}
	
	
	private static void updateMaritimePanel(SteeringCar car) 
	{
		DecimalFormat df = new DecimalFormat("#0.0");
		DecimalFormat df2 = new DecimalFormat("#0.00");
		DecimalFormat df3 = new DecimalFormat("#0.00000");
		DecimalFormat df4 = new DecimalFormat("#000");
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
		
		
        if(maritimeDisplayMode == MaritimeDisplayMode.Panel || maritimeDisplayMode == MaritimeDisplayMode.All)
        {
			if(startTime == 0)
		    	startTime = System.currentTimeMillis();

	        // OpenDS-Maritime - heading
			maritimeHeadingText.setText("Kurs: " + df.format(car.getHeadingDegree()) + " Grad");
			
			// OpenDS-Maritime - speed
	        maritimeSpeedText.setText("Geschw.: " + df.format(car.getCurrentSpeedKmh()/1.852f) + " kn");
	        
	        // OpenDS-Maritime - depth
	        maritimeDepthText.setText("Tiefe: " + df.format(car.getDistanceToRoadSurface()) + " m");
	        
	        // OpenDS-Maritime - wind
	        maritimeWindText.setText("Wind: 0,0 Grad");
	        
	        // OpenDS-Maritime - time
	        long elapsedTime = System.currentTimeMillis() - startTime - 3600000;
	        String elapsedTimeString = sdf.format(new Date(elapsedTime));
	        maritimeTimeText.setText("Zeit: " + elapsedTimeString);
	        
	        // OpenDS-Maritime - distance
	        maritimeDistanceText.setText("Distanz: " + df2.format(car.getMileage()/1852f) + " nm");
	        
	        // OpenDS-Maritime - latitude
	        maritimeLatitudeText.setText("Breite: " + df3.format(car.getGeoPosition().getX()) + " N");
	        
	        // OpenDS-Maritime - longitude
	        maritimeLongitudeText.setText("Länge: " + df3.format(car.getGeoPosition().getY()) + " O");
	        
	        // OpenDS-Maritime - scenario
	        maritimeScenarioText.setText("Szenario: " + SimulationBasics.getDrivingTask().getFileName().replace(".xml", ""));
        }
        
        if(maritimeDisplayMode == MaritimeDisplayMode.Compass || maritimeDisplayMode == MaritimeDisplayMode.All)
        {
	        // OpenDS-Maritime - compass
	        Quaternion quaternion = new Quaternion();
	        quaternion.fromAngles(0, 0, car.getHeadingDegree() * FastMath.DEG_TO_RAD);
	        compassIndicator.setLocalRotation(quaternion);
        }
        
        if(maritimeDisplayMode == MaritimeDisplayMode.MultiFunctionDisplay || maritimeDisplayMode == MaritimeDisplayMode.All)
        {
	        cogText.setText(df4.format(Math.round(car.getHeadingDegree()))+"°");
	        sogText.setText(df.format(car.getCurrentSpeedKmh()/1.852f));
	        magText.setText(df4.format((Math.round(car.getHeadingDegree())+3)%360)+"°");
	        latText.setText(getDegreeText(car.getGeoPosition().getX()));
	        longText.setText(getDegreeText(car.getGeoPosition().getY()));
	        timeText.setText(sdf2.format(new Date()));
	        depthText.setText(df.format(car.getDistanceToRoadSurface()));
        }
	}

	
    private static String getDegreeText(double decimalValue)
    {
    	DecimalFormat df = new DecimalFormat("#0.000");
    	
        int degree = (int) decimalValue;
        double minutes = ((decimalValue-degree) * 60);
        String minutesFormatted = df.format(Math.round(minutes*1000f)/1000f);

        return degree + "°" + minutesFormatted + "'";
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
		DecimalFormat df = new DecimalFormat("#0.0");
		odometerText.setText(df.format(odometer) + " km");
		
		// OpenDS-Rift
		riftKmText.setText(df.format(odometer) + " km");
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
		
		// OpenDS-Rift
		riftRpm = ((Integer) (int) Math.floor(rpm)).toString();
	}
	
	
	public static void setCruiseControlIndicator(float speed) 
	{
		// bounds of cruise control indicator
		speed = Math.min(Math.max(speed, 0), 260);
		
		// compute cruise control indicator's rotation
		// zero-point of scale is 192 degrees to the left
		// 1 speed unit per degree
		float degree =  192f - (speed/1f);
		float radians = FastMath.PI/180f * degree;
		
		// set speed indicator's rotation
		Quaternion rotation = new Quaternion();
		rotation.fromAngles(0, 0, radians);
		cruiseControlIndicator.setLocalRotation(rotation);
		cruiseControlIndicator.setCullHint(CullHint.Inherit);
	}
	
	
	public static void unsetCruiseControlIndicator() 
	{
		cruiseControlIndicator.setCullHint(CullHint.Always);
	}
	
	
	public static void setHandBrakeIndicator(boolean isOn)
	{
		if(isOn)
		{
			handBrakeIndicator.setCullHint(CullHint.Inherit);
			handBrakeIndicatorOff.setCullHint(CullHint.Always);
		}
		else
		{
			handBrakeIndicatorOff.setCullHint(CullHint.Inherit);
			handBrakeIndicator.setCullHint(CullHint.Always);
		}
	}
	
	
	public static void setLeftTurnSignalArrow(boolean isOn)
	{
		if(isOn)
		{
			leftTurnSignal.setCullHint(CullHint.Inherit);
			leftTurnSignalOff.setCullHint(CullHint.Always);
		}
		else
		{
			leftTurnSignalOff.setCullHint(CullHint.Inherit);
			leftTurnSignal.setCullHint(CullHint.Always);
		}
	}
	
	
	public static void setRightTurnSignalArrow(boolean isOn)
	{
		if(isOn)
		{
			rightTurnSignal.setCullHint(CullHint.Inherit);
			rightTurnSignalOff.setCullHint(CullHint.Always);
		}
		else
		{
			rightTurnSignalOff.setCullHint(CullHint.Inherit);
			rightTurnSignal.setCullHint(CullHint.Always);
		}
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
			// OpenDS-Rift
			riftSpeedText.setText("0.0 km/h");
			setSpeedIndicator(0);		
		}
		else
		{
			speedText.setText("" + carSpeed + " km/h");
			// OpenDS-Rift
			riftSpeedText.setText("" + carSpeed + " km/h");
			setSpeedIndicator(carSpeed);
		}
		
		if((currentSpeedLimit != 0) && ((carSpeed > currentSpeedLimit+10) || (carSpeed < upcomingSpeedLimit-10)))
		{
			speedText.setColor(ColorRGBA.Red);
			// OpenDS-Rift
			riftSpeedText.setColor(ColorRGBA.Red);
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
			// OpenDS-Rift
			riftSpeedText.setColor(ColorRGBA.White);
		}
	}


	public static void setGearIndicator(Integer gear, boolean isAutomaticTransmission) 
	{
		// OpenDS-Rift - set gear text
		if(isAutomaticTransmission) {
			gearText.setText("Gear: A" + gear);
			riftGear = "A" + gear;
		} else if (gear == 0) {
			gearText.setText("Gear: N");
			riftGear = "N";
		} else if (gear == -1) {
			gearText.setText("Gear: R");
			riftGear = "R";
		} else {
			gearText.setText("Gear: M" + gear);
			riftGear = "M" + gear;
		}
		
		
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
