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

package eu.opends.environment;


import java.io.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import eu.opends.canbus.CANClient;
import eu.opends.car.SteeringCar;
import eu.opends.environment.TrafficLight.*;
import eu.opends.environment.TrafficLightException.InvalidStateCharacterException;
import eu.opends.main.Simulator;
import eu.opends.multiDriver.MultiDriverClient;
import eu.opends.tools.PanelCenter;



/**
 * XMLParser allows XML parsing of strings and files in order to evaluate 
 * traffic light instructions either manually generated or by the external 
 * SUMO traffic simulator. Furthermore this class provides a method to parse 
 * files that contain traffic light rules, traffic light phases and traffic 
 * light position data.
 * 
 * @author Rafael Math
 */
public class XMLParser
{
	private Document doc;
	private String xmlstring;
	private boolean errorOccured = false;
	
	
	/**
	 * Creates a DOM-object from the given input string. If the input string 
	 * is not a valid XML string, a warning message will be returned.
	 * 
	 * @param xmlstring
	 * 			XML input string to parse
	 */
	public XMLParser(String xmlstring)
	{		
		this.xmlstring = xmlstring;
		
		try {
	        InputSource xmlsource = new InputSource();
	        xmlsource.setCharacterStream(new StringReader(xmlstring));

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			doc = db.parse(xmlsource);

		} catch (Exception e) {
			System.err.println("[WARNING]: Malformed XML input (XMLParser.java): " + xmlstring);
			errorOccured = true;
		}
	}
	
	
	/**
	 * Creates a DOM-object from the given input file. If the input file 
	 * does not contain a valid XML string, a warning message will be returned.
	 * 
	 * @param xmlfile
	 * 			XML input file to parse
	 */
	public XMLParser(File xmlfile) 
	{
		this.xmlstring = xmlfile.getPath();
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			doc = db.parse(xmlfile);

		} catch (Exception e) {
			System.err.println("[WARNING]: Malformed XML input (XMLParser.java): " + xmlstring);
			errorOccured = true;
		}
	}
	

	/**
	 * Searches the given XML string for traffic light instructions. External
	 * instructions from the SUMO traffic simulator and manual instructions
	 * will be distinguished.
	 */
	public void evalTrafficLightInstructions()
	{
		// write instruction to console
		System.out.println("XML-String: " + xmlstring);
		
		if(!errorOccured)
		{		
			// go down to 2nd level of the hierarchy to distinguish between SUMO or manual input
			// <TrafficLightControl><tlsstate .../></TrafficLightControl>            --> SUMO
			// <TrafficLightControl><TrafficLight.xx_xx ...></TrafficLightControl>   --> manual
			
			NodeList nodeLst = doc.getElementsByTagName("TrafficLightControl");
			Node node = nodeLst.item(0);
			NodeList trafficLightInstructionList = node.getChildNodes();
	
			for(int i=0; i<trafficLightInstructionList.getLength(); i++)
			{
				Element trafficLightInstruction = (Element) trafficLightInstructionList.item(i);
				
				// if <tlsstate .../> was found --> SUMO instruction
				if(trafficLightInstruction.getNodeName().equals("tlsstate"))				
					evalSUMOInstruction(trafficLightInstruction);
				else
					evalManualInstruction(trafficLightInstruction);
			}
		}
	}
	
	
	/**
	 * Searches the given XML string for CAN-bus instructions like steering 
	 * wheel angle, status of brake and gas pedal. Settings of the car will
	 * be adjusted
	 */
	public void evalCANInstruction(Simulator sim, CANClient canClient) 
	{
		if(!errorOccured)
		{		
			// example CAN-bus instructions:
			//<message><action name="steering">-92</action></message>
			//<message><action name="acceleration">0.5</action></message>
			//<message><action name="brake">0.3</action></message>
			//<message><action name="button">cs</action></message>
			//<message><action name="button">return</action></message>
			
			SteeringCar car = sim.getCar();
			
			NodeList nodeLst = doc.getElementsByTagName("message");
			for(int i=0; i<nodeLst.getLength(); i++)
			{
				Node currentNode = nodeLst.item(i);			
				NodeList actionList = ((Element) currentNode).getElementsByTagName("action");
		
				for(int j=0; j<actionList.getLength(); j++)
				{
					try{
						
						Element currentAction = (Element) actionList.item(j);
						
						String actionID = currentAction.getAttribute("name");	
						String valueString = getCharacterDataFromElement(currentAction);

						// performs a steering input
						if(actionID.equals("steering"))
						{
							float value = Float.parseFloat(valueString);
							System.out.println("Steering: " + value);
							
							// for Sim-TD Smart
							canClient.setSteeringAngle(-value);
							
							// for Mercedes R-class
							//canClient.setSteeringAngle(value);
							
							sim.getSteeringTask().setSteeringIntensity(-0.02f*value);
						}
						
						// performs "cruise forward"-button
						else if(actionID.equals("MFLplus_State") || actionID.equals("MFLtelefoneEnd_State"))	
						{
							int value = Integer.parseInt(valueString);
							System.out.println("Gas: " + value);
							if(value == 0)
								car.setAcceleratorPedalIntensity(0);
								//car.releaseAccel();
							else
								car.setAcceleratorPedalIntensity(-1);
						}
						
						// performs "cruise forward"-button
						else if(actionID.equals("acceleration"))	
						{
							float value = Float.parseFloat(valueString);
							System.out.println("Gas: " + value);
							value = value*6;
							if(value <= 0)
							{
								car.setAcceleratorPedalIntensity(0);
								//car.releaseAccel();
							}
							else
							{
								car.setAcceleratorPedalIntensity(Math.max(-value,-1.0f));
								sim.getSteeringTask().getPrimaryTask().reportGreenLight();
							}
							
							sim.getThreeVehiclePlatoonTask().reportAcceleratorIntensity(Math.abs(value));
						}
						
						// performs "cruise backward"-button
						else if(actionID.equals("MFLminus_State"))	
						{
							int value = Integer.parseInt(valueString);
							System.out.println("Back: " + value);
							if(value == 0)
								car.setAcceleratorPedalIntensity(0);
								//car.releaseAccel();
							else
								car.setAcceleratorPedalIntensity(1);
						}
						
						// performs brake pedal
						else if(actionID.equals("KL54_RM_State"))	
						{
							int value = Integer.parseInt(valueString);
							System.out.println("Brake: " + value);
							if(value == 0)
								//car.setGasPedalIntensity(0);
								car.setBrakePedalIntensity(0);
								//car.releaseAccel();
							else
								car.setBrakePedalIntensity(1); // 1 --> full braking
						}
						
						// performs brake pedal
						else if(actionID.equals("brake"))	
						{
							float value = Float.parseFloat(valueString);
							System.out.println("Brake: " + value);
							if(value <= 0)
							{
								//car.setGasPedalIntensity(0);
								car.setBrakePedalIntensity(0);
								sim.getThreeVehiclePlatoonTask().reportBrakeIntensity(0);
								//car.releaseAccel();
							}
							else
							{
								value = Math.min(value,1.0f);
								car.setBrakePedalIntensity(value); // 1 --> full braking
								sim.getSteeringTask().getPrimaryTask().reportRedLight();
								sim.getThreeVehiclePlatoonTask().reportBrakeIntensity(value);
								car.disableCruiseControlByBrake();
							}
						}
						
						// performs "change view"-button
						else if(actionID.equals("button") && valueString.equals("cs"))	
						{
							System.out.println("Change view");
							sim.getCameraFactory().changeCamera();
						}

						// performs "reset car"-button
						else if(actionID.equals("button") && valueString.equals("return"))	
						{
							System.out.println("Reset car");
							car.setToNextResetPosition();
						}
						
						// shows message on display
						else if(actionID.equals("display"))	
						{
							int duration;
							try{
								duration = Integer.parseInt(currentAction.getAttribute("duration"));
							} catch(Exception e){
								duration = 0;
							}
							PanelCenter.getMessageBox().addMessage(valueString,duration);
						}
						
						
						// channel0 input
						else if(actionID.equals("channel0"))	
						{
							float value = Float.parseFloat(valueString);
							float percentage = (2f * voltToPercentage(value, 0.0f, 5.04f)) - 1f;
							//System.out.println("channel0: " + percentage);
							//System.out.println("steering: " + percentage);

							canClient.setSteeringAngle(percentage);
							sim.getSteeringTask().setSteeringIntensity(0.02f*percentage);
						}
						

						// channel1 input
						else if(actionID.equals("channel1"))	
						{
							float value = Float.parseFloat(valueString);
							float percentage = voltToPercentage(value, 0.1f, 5.0f);
							//System.out.println("channel1: " + percentage);
							//System.out.println(System.currentTimeMillis() + " - accelerate: " + percentage);
							
							car.setAcceleratorPedalIntensity(-percentage);
							sim.getThreeVehiclePlatoonTask().reportAcceleratorIntensity(percentage);
							
							if(percentage > 0)
								sim.getSteeringTask().getPrimaryTask().reportGreenLight();
						}
						

						// channel2 input
						else if(actionID.equals("channel2"))	
						{
							float value = Float.parseFloat(valueString);
							float percentage = voltToPercentage(value, 1.0f, 5.0f);
							//System.out.println("channel2: " + percentage);
							//System.out.println("brake: " + percentage);

							car.setBrakePedalIntensity(percentage); // 1 --> full braking
							sim.getThreeVehiclePlatoonTask().reportBrakeIntensity(percentage);
							
							if(percentage > 0)
							{
								sim.getSteeringTask().getPrimaryTask().reportRedLight();
								car.disableCruiseControlByBrake();
							}
						}

						
						// channel3 input
						else if(actionID.equals("channel3"))	
						{
							float value = Float.parseFloat(valueString);
							float percentage = voltToPercentage(value, 0.2f, 3.7f);
							//System.out.println("channel3: " + percentage);
						}
						
						
					} catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
		
	private float voltToPercentage(float value, float zeroPercent, float hundredPercent) 
	{
		if(zeroPercent <= hundredPercent)
		{
			if(value <= zeroPercent)
				return 0;
			else if (value >= hundredPercent)
				return 1;
			else
			{
				// zero < value < hundred
				return (value - zeroPercent) / (hundredPercent - zeroPercent);
			}
		}
		else
		{
			if(value <= hundredPercent)
				return 1;
			else if (value >= zeroPercent)
				return 0;
			else
			{
				// hundred < value < zero
				return (zeroPercent - value) / (zeroPercent - hundredPercent);
			}
		}
	}


	/**
	 * Evaluates a SUMO instruction and sets the model's traffic light states 
	 * according to the given intersection ID (attribute "id"), the traffic 
	 * light ID (position in attribute "state") and state (value at position 
	 * in attribute "state").
	 * 
	 * <pre> &lt;TrafficLightControl&gt;
	 *     &lt;tlsstate timeR="178.00" id="0" programID="0" phase="6" state="rrrryyggrrrryygg"/&gt;
	 * &lt;/TrafficLightControl&gt;</pre>
	 * 
	 * @param trafficLightInstruction
	 * 			instruction string from SUMO (must match to the string above)
	 */
	private void evalSUMOInstruction(Element trafficLightInstruction)
	{
		try{
			
			// read state string and id string from SUMO instruction
			String stateString = trafficLightInstruction.getAttribute("state");
			String idString    = trafficLightInstruction.getAttribute("id");
			String intersectionID = String.format("%2s", idString).replace(' ', '0');
	
			for(int i=0; i<stateString.length(); i++)
			{
				// get traffic light object from intersection ID and traffic light ID
				String trafficlightID     = String.format("%2s", i).replace(' ', '0');
				String trafficLightName   = "TrafficLight." + intersectionID + "_" + trafficlightID;
				TrafficLight trafficLight = TrafficLightCenter.getTrafficLightByName(trafficLightName);
	
				if(trafficLight != null)
				{
					// assign state to traffic light object
					TrafficLightState state = parseSUMOStateCharacter(stateString.charAt(i));
					trafficLight.setState(state);
				}
			}
			
		} catch (InvalidStateCharacterException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Evaluates a (manual) XML instruction and sets the given traffic lights' 
	 * states to the given state values.
	 * 
	 * <pre> &lt;TrafficLightControl&gt;
	 *     &lt;TrafficLight.00_12&gt;
	 *         &lt;status>GREEN&lt;/status&gt;
	 *     &lt;/TrafficLight.00_12&gt;
	 *     ...
	 * &lt;/TrafficLightControl&gt;</pre>
	 * 
	 * 
	 * @param trafficLightInstruction
	 * 			manual instruction string (must match to the string above)
	 */
	private void evalManualInstruction(Element trafficLightInstruction)
	{
		try{
			
			// get traffic light object from XML
			String trafficLightName = trafficLightInstruction.getNodeName();
			TrafficLight trafficLight = TrafficLightCenter.getTrafficLightByName(trafficLightName);

			if(trafficLight != null)
			{
				// get traffic light state from XML
				NodeList stateList = trafficLightInstruction.getElementsByTagName("status");
				Element stateElement = (Element) stateList.item(0);
				String stateString = getCharacterDataFromElement(stateElement);
				TrafficLightState state = parseManualStateString(stateString);
				
				// assign state to traffic light object
				trafficLight.setState(state);
			}
			
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
	
	
	/**
	 * Transforms SUMO's character representation of states to a value of
	 * TrafficLightState. E.g.  'r' --> TrafficLightState.RED
	 * 
	 * @param stateChar
	 * 			A SUMO state character
	 * 
	 * @return
	 * 			The corresponding traffic light state representation
	 * 
	 * @throws Exception 
	 * 			Exception will be thrown on invalid character input
	 */
	public static TrafficLightState parseSUMOStateCharacter(char stateChar) throws InvalidStateCharacterException
	{
		switch (stateChar){
			case 'G' : return TrafficLightState.GREEN;
			case 'g' : return TrafficLightState.GREEN;
			case 'y' : return TrafficLightState.YELLOW;
			case 'r' : return TrafficLightState.RED;
			case 'x' : return TrafficLightState.YELLOWRED;
			case 'o' : return TrafficLightState.OFF;
			case 'a' : return TrafficLightState.ALL;			
		}
		
		throw new InvalidStateCharacterException("Invalid character data: '" + stateChar + "'");
	}
	
	
	/**
	 * Returns character data from XML element.
	 * E.g. &lt;elem&gt;abc123&lt;/elem&gt;  --> "abc123"
	 * 
	 * @param elem
	 * 			XML Element
	 * 
	 * @return
	 * 			string representation of the given element
	 * 
	 * @throws Exception
	 * 			Exception will be thrown if no character data available
	 */
	public static String getCharacterDataFromElement(Element elem) throws Exception 
	{
		Node child = elem.getFirstChild();
		if (child instanceof CharacterData) 
		{
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		
		throw new Exception("No character data given");
	}
	
	
	/**
	 * Transforms the string representation of states to a value of
	 * TrafficLightState. E.g.  "green" --> TrafficLightState.GREEN
	 * 
	 * @param stateString
	 * 			The string representation of the state
	 * 
	 * @return
	 * 			The corresponding traffic light state representation
	 * 
	 * @throws Exception 
	 * 			Exception will be thrown on invalid string input
	 */
	private TrafficLightState parseManualStateString(String stateString) throws Exception
	{
		try{
			return TrafficLightState.valueOf(stateString.toUpperCase());
		} catch (IllegalArgumentException e){
			throw new Exception("Invalid character data: '" + stateString + "'");
		}
	}

	
	public void evalMultiDriverInstruction(Simulator sim, MultiDriverClient client)
	{
		// on "registered" --> set ID
		// on "update" --> perform changes
		// on "unregistered" --> call method requestStop()
		
		if(!errorOccured)
		{		
			// example multi driver instructions:
			//<multiDriver>
			//	<registered id="1" />
			//</multiDriver>
			//
			//<multiDriver>
			//	<update>
			//		<add id="1" modelPath="test/subfolder/model.scene" driverName="test driver" />
			//		<change id="5" pos="1;2;3" rot="1;2;3;4" heading="358.4" wheel="1;2" />
			//		<remove id="13">
			//	<update>
			//</multiDriver>
			//
			//<multiDriver>
			//	<unregistered id="1" />
			//</multiDriver>
			
			NodeList nodeLst = doc.getElementsByTagName("multiDriver");
			for(int i=0; i<nodeLst.getLength(); i++)
			{
				Node currentNode = nodeLst.item(i);
				
				NodeList registeredList = ((Element) currentNode).getElementsByTagName("registered");
				for(int j=0; j<registeredList.getLength(); j++)
				{
					Element currentRegistered = (Element) registeredList.item(j);
					client.setID(currentRegistered.getAttribute("id"));
				}
				
				NodeList updateList = ((Element) currentNode).getElementsByTagName("update");
				for(int j=0; j<updateList.getLength(); j++)
				{
					Element currentUpdate = (Element) updateList.item(j);
					
					NodeList addList = currentUpdate.getElementsByTagName("add");
					for(int k=0; k<addList.getLength(); k++)
					{
						Element currentAdd = (Element) addList.item(k);
						String vehicleID = currentAdd.getAttribute("id");
						String modelPath = currentAdd.getAttribute("modelPath");
						String driverName = currentAdd.getAttribute("driverName");
						client.addVehicle(vehicleID, modelPath, driverName);
					}
					
					NodeList changeList = currentUpdate.getElementsByTagName("change");
					for(int k=0; k<changeList.getLength(); k++)
					{
						Element currentChange = (Element) changeList.item(k);
						String vehicleID = currentChange.getAttribute("id");
						String position = currentChange.getAttribute("pos");
						String rotation = currentChange.getAttribute("rot");
						String heading = currentChange.getAttribute("heading");
						String wheel = currentChange.getAttribute("wheel");
						client.changeVehicle(vehicleID, position, rotation, heading, wheel);
					}
					
					NodeList removeList = currentUpdate.getElementsByTagName("remove");
					for(int k=0; k<removeList.getLength(); k++)
					{
						Element currentRemove = (Element) removeList.item(k);
						String vehicleID = currentRemove.getAttribute("id");
						client.removeVehicle(vehicleID);
					}
				}
				
				NodeList unregisteredList = ((Element) currentNode).getElementsByTagName("unregistered");
				for(int j=0; j<unregisteredList.getLength(); j++)
				{
					Element currentUnregistered = (Element) unregisteredList.item(j);
					client.requestStop(currentUnregistered.getAttribute("id"));
				}
			}
		}		
	}
	
}