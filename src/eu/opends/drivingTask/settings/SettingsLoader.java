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

package eu.opends.drivingTask.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.NodeList;

import eu.opends.drivingTask.DrivingTaskDataQuery;
import eu.opends.drivingTask.DrivingTaskDataQuery.Layer;
import eu.opends.input.KeyMapping;

/**
 * 
 * @author Rafael Math
 */
@SuppressWarnings("unchecked")
public class SettingsLoader
{
	private DrivingTaskDataQuery dtData;
	private Map<String,String[]> keyAssignmentMap = new HashMap<String,String[]>();

	public enum Setting
	{
		General_driverName("settings:general/settings:driverName"),
		General_mirrorMode("settings:general/settings:mirrorMode"),
		General_rearviewMirror_viewPortLeft("settings:general/settings:rearviewMirror/settings:viewPortLeft"),
		General_rearviewMirror_viewPortRight("settings:general/settings:rearviewMirror/settings:viewPortRight"),
		General_rearviewMirror_viewPortTop("settings:general/settings:rearviewMirror/settings:viewPortTop"),
		General_rearviewMirror_viewPortBottom("settings:general/settings:rearviewMirror/settings:viewPortBottom"),
		General_rearviewMirror_horizontalAngle("settings:general/settings:rearviewMirror/settings:horizontalAngle"),
		General_rearviewMirror_verticalAngle("settings:general/settings:rearviewMirror/settings:verticalAngle"),
		General_leftMirror_viewPortLeft("settings:general/settings:leftMirror/settings:viewPortLeft"),
		General_leftMirror_viewPortRight("settings:general/settings:leftMirror/settings:viewPortRight"),
		General_leftMirror_viewPortTop("settings:general/settings:leftMirror/settings:viewPortTop"),
		General_leftMirror_viewPortBottom("settings:general/settings:leftMirror/settings:viewPortBottom"),
		General_leftMirror_horizontalAngle("settings:general/settings:leftMirror/settings:horizontalAngle"),
		General_leftMirror_verticalAngle("settings:general/settings:leftMirror/settings:verticalAngle"),
		General_rightMirror_viewPortLeft("settings:general/settings:rightMirror/settings:viewPortLeft"),
		General_rightMirror_viewPortRight("settings:general/settings:rightMirror/settings:viewPortRight"),
		General_rightMirror_viewPortTop("settings:general/settings:rightMirror/settings:viewPortTop"),
		General_rightMirror_viewPortBottom("settings:general/settings:rightMirror/settings:viewPortBottom"),
		General_rightMirror_horizontalAngle("settings:general/settings:rightMirror/settings:horizontalAngle"),
		General_rightMirror_verticalAngle("settings:general/settings:rightMirror/settings:verticalAngle"),
		General_numberOfScreens("settings:general/settings:numberOfScreens"),
		General_angleBetweenAdjacentCameras("settings:general/settings:angleBetweenAdjacentCameras"),
		General_frustumNear("settings:general/settings:frustumNear"),
		General_frustumFar("settings:general/settings:frustumFar"),
		General_showStats("settings:general/settings:showStats"),
		General_pauseAfterStartup("settings:general/settings:pauseAfterStartup"),
		General_showHood("settings:general/settings:showHood"),
		General_showAnalogIndicators("settings:general/settings:showAnalogIndicators"),
		General_showDigitalIndicators("settings:general/settings:showDigitalIndicators"),
		General_showFuelConsumption("settings:general/settings:showFuelConsumption"),
		General_analogIndicatorsLeft("settings:general/settings:analogIndicators/settings:left"),
		General_analogIndicatorsBottom("settings:general/settings:analogIndicators/settings:bottom"),
		General_analogIndicatorsRight("settings:general/settings:analogIndicators/settings:right"),
		General_analogIndicatorsTop("settings:general/settings:analogIndicators/settings:top"),
		General_analogIndicatorsScale("settings:general/settings:analogIndicators/settings:scale"),
		General_outsideCamPosition_x("settings:general/settings:outsideCamPosition/settings:x"),
		General_outsideCamPosition_y("settings:general/settings:outsideCamPosition/settings:y"),
		General_outsideCamPosition_z("settings:general/settings:outsideCamPosition/settings:z"),
		General_topView_carPointingUp("settings:general/settings:topView/settings:carPointingUp"),
		General_topView_viewPortLeft("settings:general/settings:topView/settings:viewPortLeft"),
		General_topView_viewPortRight("settings:general/settings:topView/settings:viewPortRight"),
		General_topView_viewPortBottom("settings:general/settings:topView/settings:viewPortBottom"),
		General_topView_viewPortTop("settings:general/settings:topView/settings:viewPortTop"),
		General_topView_verticalDistance("settings:general/settings:topView/settings:verticalDistance"),
		General_topView_carOffset("settings:general/settings:topView/settings:carOffset"),
		General_captureVideo("settings:general/settings:captureVideo"),
		General_cameraMode("settings:general/settings:cameraMode"),
		Analyzer_fileName("settings:analyzer/settings:fileName"),
		Analyzer_suppressPDFPopup("settings:analyzer/settings:suppressPDFPopup"), 
		ObjectLocator_enable("settings:objectLocator/settings:enable"),
		ObjectLocator_fileName("settings:objectLocator/settings:fileName"),
		HMI_enableConnection("settings:HMI/settings:enableConnection"),
		HMI_ip("settings:HMI/settings:ip"),
		HMI_port("settings:HMI/settings:port"),
		ExternalVisualization_enableConnection("settings:externalVisualization/settings:enableConnection"),
		ExternalVisualization_ip("settings:externalVisualization/settings:ip"),
		ExternalVisualization_port("settings:externalVisualization/settings:port"),
		ExternalVisualization_updateRate("settings:externalVisualization/settings:updateRate"),
		ExternalVisualization_scalingFactor("settings:externalVisualization/settings:scalingFactor"),
		ExternalVisualization_sendPosOriAsOneString("settings:externalVisualization/settings:sendPosOriAsOneString"),
		KnowledgeManager_enableConnection("settings:knowledgeManager/settings:enableConnection"),
		KnowledgeManager_ip("settings:knowledgeManager/settings:ip"),
		KnowledgeManager_port("settings:knowledgeManager/settings:port"),
		Simphynity_enableConnection("settings:simphynity/settings:enableConnection"),
		Simphynity_ip("settings:simphynity/settings:ip"),
		Simphynity_port("settings:simphynity/settings:port"),
		CANInterface_enableConnection("settings:CANInterface/settings:enableConnection"),
		CANInterface_ip("settings:CANInterface/settings:ip"),
		CANInterface_port("settings:CANInterface/settings:port"),
		CANInterface_updateRate("settings:CANInterface/settings:updateRate"),
		CANInterface_maxSteeringAngle("settings:CANInterface/settings:maxSteeringAngle"),
		MultiDriver_enableConnection("settings:multiDriver/settings:enableConnection"),
		MultiDriver_ip("settings:multiDriver/settings:ip"),
		MultiDriver_port("settings:multiDriver/settings:port"),
		MultiDriver_updateRate("settings:multiDriver/settings:updateRate"),
		VsimrtiServer_startServer("settings:vsimrtiServer/settings:startServer"),
		VsimrtiServer_port("settings:vsimrtiServer/settings:port"),
		SettingsControllerServer_startServer("settings:settingsControllerServer/settings:startServer"),
		SettingsControllerServer_port("settings:settingsControllerServer/settings:port"),
		ReactionMeasurement_groupRed("settings:reactionMeasurement/settings:groupRed"),
		ReactionMeasurement_groupYellow("settings:reactionMeasurement/settings:groupYellow"),
		ReactionMeasurement_groupGreen("settings:reactionMeasurement/settings:groupGreen"),
		ReactionMeasurement_groupCyan("settings:reactionMeasurement/settings:groupCyan"),
		ReactionMeasurement_groupBlue("settings:reactionMeasurement/settings:groupBlue"),
		ReactionMeasurement_groupMagenta("settings:reactionMeasurement/settings:groupMagenta"),
		Joystick_dumpJoystickList("settings:controllers/settings:joystick/settings:dumpJoystickList"),
		Joystick_steeringControllerID("settings:controllers/settings:joystick/settings:steering/@controllerID"),
		Joystick_steeringAxis("settings:controllers/settings:joystick/settings:steering/@axisID"),
		Joystick_invertSteeringAxis("settings:controllers/settings:joystick/settings:steering/@invert"),
		Joystick_steeringSensitivityFactor("settings:controllers/settings:joystick/settings:steering/@sensitivity"),
		Joystick_combinedPedalsControllerID("settings:controllers/settings:joystick/settings:combinedPedals/@controllerID"),
		Joystick_combinedPedalsAxis("settings:controllers/settings:joystick/settings:combinedPedals/@axisID"),
		Joystick_invertCombinedPedalsAxis("settings:controllers/settings:joystick/settings:combinedPedals/@invert"),
		Joystick_combinedPedalsSensitivityFactor("settings:controllers/settings:joystick/settings:combinedPedals/@sensitivity"),
		Joystick_acceleratorControllerID("settings:controllers/settings:joystick/settings:accelerator/@controllerID"),
		Joystick_acceleratorAxis("settings:controllers/settings:joystick/settings:accelerator/@axisID"),
		Joystick_invertAcceleratorAxis("settings:controllers/settings:joystick/settings:accelerator/@invert"),
		Joystick_acceleratorSensitivityFactor("settings:controllers/settings:joystick/settings:accelerator/@sensitivity"),
		Joystick_brakeControllerID("settings:controllers/settings:joystick/settings:brake/@controllerID"),
		Joystick_brakeAxis("settings:controllers/settings:joystick/settings:brake/@axisID"),
		Joystick_invertBrakeAxis("settings:controllers/settings:joystick/settings:brake/@invert"),
		Joystick_brakeSensitivityFactor("settings:controllers/settings:joystick/settings:brake/@sensitivity"),
		Joystick_clutchControllerID("settings:controllers/settings:joystick/settings:clutch/@controllerID"),
		Joystick_clutchAxis("settings:controllers/settings:joystick/settings:clutch/@axisID"),
		Joystick_invertClutchAxis("settings:controllers/settings:joystick/settings:clutch/@invert"),
		Joystick_clutchSensitivityFactor("settings:controllers/settings:joystick/settings:clutch/@sensitivity"),
		Joystick_enableForceFeedback("settings:controllers/settings:joystick/settings:enableForceFeedback"),
		Joystick_springForce("settings:controllers/settings:joystick/settings:springForce"),
		Joystick_damperForce("settings:controllers/settings:joystick/settings:damperForce"),
		Mouse_scrollSensitivityFactor("settings:controllers/settings:mouse/settings:scrollSensitivityFactor"),
		Mouse_minScrollZoom("settings:controllers/settings:mouse/settings:minScrollZoom"),
		Mouse_maxScrollZoom("settings:controllers/settings:mouse/settings:maxScrollZoom"), 
		Eyetracker_enableConnection("settings:eyetracker/settings:enableConnection"),
		Eyetracker_port("settings:eyetracker/settings:port"),
		Eyetracker_smoothingFactor("settings:eyetracker/settings:smoothingFactor"),
		Eyetracker_crossHairs_show("settings:eyetracker/settings:crossHairs/settings:show"),
		Eyetracker_crossHairs_color("settings:eyetracker/settings:crossHairs/settings:color"),
		Eyetracker_crossHairs_scalingFactor("settings:eyetracker/settings:crossHairs/settings:scalingFactor"),
		Eyetracker_gazeSphere_show("settings:eyetracker/settings:gazeSphere/settings:show"),
		Eyetracker_gazeSphere_color("settings:eyetracker/settings:gazeSphere/settings:color"),
		Eyetracker_highlightObjects_mode("settings:eyetracker/settings:highlightObjects/settings:mode"),
		Eyetracker_highlightObjects_color("settings:eyetracker/settings:highlightObjects/settings:color"),
		Eyetracker_warningFrame_show("settings:eyetracker/settings:warningFrame/settings:show"),
		Eyetracker_warningFrame_threshold("settings:eyetracker/settings:warningFrame/settings:threshold"),
		Eyetracker_warningFrame_flashingInterval("settings:eyetracker/settings:warningFrame/settings:flashingInterval"),
		OculusRift_isAttached("settings:oculusRift/settings:isAttached"),
		Maritime_displayMode("settings:maritime/settings:displayMode");
		
		
		private String path;
		
		Setting(){
			path = null;
		}
		
		Setting(String p){
			path = p;
		}
		
		public String getXPathQuery()
		{
			if(path!=null)
			{
				return "/settings:settings/"+path;
			}
			else
			{
				String[] array = this.toString().split("_");
				return "/settings:settings/settings:"+array[0]+"/settings:"+array[1];	
			}
		}
	}

	
	public SettingsLoader(DrivingTaskDataQuery dtData) 
	{
		this.dtData = dtData;
		loadKeyAssignments();
		loadJoystickKeyAssignments();
	}
	

	private void loadKeyAssignments() 
	{
		String path = "/settings:settings/settings:controllers/settings:keyboard/settings:keyAssignments/settings:keyAssignment";
		NodeList keyAssignmentNodes = (NodeList) dtData.xPathQuery(Layer.SETTINGS, 
				path, XPathConstants.NODESET);

		for (int k = 1; k <= keyAssignmentNodes.getLength(); k++) 
		{
			String function = dtData.getValue(Layer.SETTINGS, 
					path + "["+k+"]/@function", String.class);
			
			String keyList = dtData.getValue(Layer.SETTINGS, 
					path + "["+k+"]/@key", String.class).toUpperCase();
			
			if(!function.isEmpty())
			{
				if(!keyAssignmentMap.containsKey(function))
				{
					// insert key pair to keyAssignmentMap
					if(keyList.isEmpty())
					{
						// do not assign any key and remove default assignment 
						keyAssignmentMap.put(function, new String[]{});
						//System.err.println("A:" + function);
					}
					else
					{
						// assign a comma-separated list of keys
						String[] newKeys = keyList.split(",");
						
						for(int i = 0; i<newKeys.length; i++)
							newKeys[i] = "KEY_" + newKeys[i].replace("KEY_", "");
						
						keyAssignmentMap.put(function, newKeys);
					}
				}
				else
				{
					// append key pair to keyAssignmentMap
					if(!keyList.isEmpty())
					{
						// assign a comma-separated list of keys
						String[] originalKeys = keyAssignmentMap.get(function);
						String[] newKeys = keyList.split(",");
						
						for(int i = 0; i<newKeys.length; i++)
							newKeys[i] = "KEY_" + newKeys[i].replace("KEY_", "");
						
						String[] allKeys = joinArrays(originalKeys, newKeys);					    
						keyAssignmentMap.put(function, allKeys);
					}
				}
			}
		}		
	}

	
	private void loadJoystickKeyAssignments() 
	{
		String path = "/settings:settings/settings:controllers/settings:joystick/settings:keyAssignments/settings:keyAssignment";
		NodeList keyAssignmentNodes = (NodeList) dtData.xPathQuery(Layer.SETTINGS, 
				path, XPathConstants.NODESET);

		for (int k = 1; k <= keyAssignmentNodes.getLength(); k++) 
		{
			String function = dtData.getValue(Layer.SETTINGS, 
					path + "["+k+"]/@function", String.class);
			
			String keyList = dtData.getValue(Layer.SETTINGS, 
					path + "["+k+"]/@key", String.class).toUpperCase();
			
			if(!function.isEmpty())
			{
				if(!keyAssignmentMap.containsKey(function))
				{
					// insert key pair to keyAssignmentMap
					if(keyList.isEmpty())
					{
						// do not assign any key and remove default assignment 
						keyAssignmentMap.put(function, new String[]{});
						//System.err.println("A:" + function);
					}
					else
					{
						// assign a comma-separated list of keys
						String[] newKeys = keyList.split(",");
						
						for(int i = 0; i<newKeys.length; i++)
							newKeys[i] = "BUTTON_" + newKeys[i].replace("BUTTON_", "");
						
						keyAssignmentMap.put(function, newKeys);
					}
				}
				else
				{
					// append key pair to keyAssignmentMap
					if(!keyList.isEmpty())
					{
						// assign a comma-separated list of keys
						String[] originalKeys = keyAssignmentMap.get(function);
						String[] newKeys = keyList.split(",");
						
						for(int i = 0; i<newKeys.length; i++)
							newKeys[i] = "BUTTON_" + newKeys[i].replace("BUTTON_", "");
						
						String[] allKeys = joinArrays(originalKeys, newKeys);					    
						keyAssignmentMap.put(function, allKeys);
					}
				}
			}
		}		
	}
	
	
	private static String[] joinArrays(String [] ... arrays) 
	{
		// calculate size of target array
		int size = 0;
		for (String[] array : arrays) 
		  size += array.length;
		
		String[] result = new String[size];
		
		int j = 0;
		for (String[] array : arrays) 
		{
			for (String s : array)
				result[j++] = s;
		}
		
		return result;
	}
	

	/**
	 * Looks up the sub node (specified in parameter name) of the given element node
	 * and writes the data to the global variable with the same name. If this was 
	 * successful, the global variable "isSet_&lt;name&gt;" will be set to true. 
	 * 
	 * @param <T>
	 * 			Type of property to look up.
	 * 
	 * @param setting
	 * 			Property to look up.
	 * 
	 * @param defaultValue
	 * 			Default value (will be returned if no valid property could be found).
	 * 
	 * @return
	 * 			Value of the property.
	 */
	public <T> T getSetting(Setting setting, T defaultValue)
	{		
		try {
			
			Class<T> cast = (Class<T>) defaultValue.getClass();
			T returnvalue = (T) dtData.getValue(Layer.SETTINGS, setting.getXPathQuery(), cast);
			
			if(returnvalue == null)
				returnvalue = defaultValue;
			
			return returnvalue;

		} catch (Exception e2) {
			dtData.reportInvalidValueError(setting.toString(), dtData.getSettingsPath());
		}
		
		return defaultValue;
	}
	
	
	public List<KeyMapping> lookUpKeyMappings(ArrayList<KeyMapping> keyMappingList)
	{
		for(KeyMapping keyMapping : keyMappingList)
		{
			String function = keyMapping.getID();
			if(keyAssignmentMap.containsKey(function))
				keyMapping.setKeys(keyAssignmentMap.get(function));
		}
		
		return keyMappingList;
	}

}
