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

package eu.opends.settingsController;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.jme3.math.FastMath;

import eu.opends.car.Car;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;
import eu.opends.tools.Vector3d;

/**
 * 
 * @author Daniel Braun
 */
public class APIData {
	
	private Car car;
	private String dataSchema;
	private Map<String, Boolean> dataMap = new HashMap<String, Boolean>();
	
	
	public APIData(Car car){
		this.car = car;
		
		dataSchema =  "<root>"+
				"<thisVehicle>"+
				"<interior>"+
					"<cockpit>"+
						"<pedals>"+
							"<gasPedal>"+
								"<Properties><pressedState></pressedState></Properties>"+
							"</gasPedal>"+
							"<brakePedal>"+
								"<Properties><pressedState></pressedState></Properties>"+ 
							"</brakePedal>"+ 
						"</pedals>"+ 
						"<steeringWheel>"+ 
							"<Properties><steerAngle></steerAngle></Properties>"+ 
						"</steeringWheel>"+ 
					"</cockpit>"+ 
				"</interior>"+ 
				"<exterior>"+ 
					"<lights>"+ 
						"<Properties><headlights></headlights></Properties>"+ 
					"</lights>"+ 
					"<gearUnit>"+ 
						"<Properties><currentGear></currentGear></Properties>"+ 
					"</gearUnit>"+ 
					"<engineCompartment>"+ 
						"<engine><Properties>"+ 
							"<running></running>"+ 
							"<actualRpm></actualRpm>"+ 
						"</Properties></engine>"+ 
					"</engineCompartment>"+ 
					"<fueling>"+ 
						"<fuelType>"+ 
							"<Properties><currentConsumption></currentConsumption></Properties>"+
							"<tank><Properties>"+ 
								"<maxAmount></maxAmount>"+ 
								"<actualAmount></actualAmount>"+
							"</Properties></tank>"+
						"</fuelType>"+
					"</fueling>"+
				"</exterior>"+
				"<physicalAttributes><Properties>"+
					"<latitude></latitude>"+
					"<longitude></longitude>"+
					"<altitude></altitude>"+
					"<orientation></orientation>"+
					"<speed></speed>"+
					"<rise></rise>"+
					"<accelerationLateral></accelerationLateral>"+
					"<rotation></rotation>"+
					"<accelerationRotation></accelerationRotation>"+
					"<acceleration></acceleration>"+
				"</Properties></physicalAttributes>"+
			"</thisVehicle>"+
		"</root>";
		
		Boolean init = true;
		
		//interior
		dataMap.put("/root/thisVehicle/interior/cockpit/pedals/gasPedal/Properties/pressedState", init);
		dataMap.put("/root/thisVehicle/interior/cockpit/pedals/brakePedal/Properties/pressedState", init);
		dataMap.put("/root/thisVehicle/interior/cockpit/steeringWheel/Properties/steerAngle", init);
		
		//exterior
		dataMap.put("/root/thisVehicle/exterior/lights/Properties/headlights", init);
		dataMap.put("/root/thisVehicle/exterior/gearUnit/Properties/currentGear", init);
		dataMap.put("/root/thisVehicle/exterior/engineCompartment/engine/Properties/running", init);
		dataMap.put("/root/thisVehicle/exterior/engineCompartment/engine/Properties/actualRpm", init);
		dataMap.put("/root/thisVehicle/exterior/fueling/fuelType/Properties/currentConsumption", init);
		dataMap.put("/root/thisVehicle/exterior/fueling/fuelType/tank/Properties/maxAmount", init);
		dataMap.put("/root/thisVehicle/exterior/fueling/fuelType/tank/Properties/actualAmount", init);
		
		//physicalAttributes
		dataMap.put("/root/thisVehicle/physicalAttributes/Properties/latitude", init);
		dataMap.put("/root/thisVehicle/physicalAttributes/Properties/longitude", init);
		dataMap.put("/root/thisVehicle/physicalAttributes/Properties/altitude", init);
		dataMap.put("/root/thisVehicle/physicalAttributes/Properties/orientation", init);
		dataMap.put("/root/thisVehicle/physicalAttributes/Properties/speed", init);
		dataMap.put("/root/thisVehicle/physicalAttributes/Properties/rise", init);
		dataMap.put("/root/thisVehicle/physicalAttributes/Properties/accelerationLateral", init);
		dataMap.put("/root/thisVehicle/physicalAttributes/Properties/rotation", init);
		dataMap.put("/root/thisVehicle/physicalAttributes/Properties/accelerationRotation", init);
		dataMap.put("/root/thisVehicle/physicalAttributes/Properties/acceleration", init);
	}
	
	private String getValue(String var){
		String value = "";
		
		//interior
		if(var.equals("/root/thisVehicle/interior/cockpit/pedals/gasPedal/Properties/pressedState")){
			float gasPedalPress = car.getAcceleratorPedalIntensity(); // in %
			value = String.valueOf(gasPedalPress);
		}
		else if(var.equals("/root/thisVehicle/interior/cockpit/pedals/brakePedal/Properties/pressedState")){
			float brakePedalPress = car.getBrakePedalIntensity(); // in %	
			value = String.valueOf(brakePedalPress);
		}
		else if(var.equals("/root/thisVehicle/interior/cockpit/steeringWheel/Properties/steerAngle")){
			float maxSteeringAngle = Simulator.getDrivingTask().getSettingsLoader().getSetting(
					Setting.CANInterface_maxSteeringAngle, SimulationDefaults.CANInterface_maxSteeringAngle);			
			float steeringAngle = -maxSteeringAngle*car.getSteeringWheelState(); // in degree (+ = right, - = left)
			
			value = String.valueOf(steeringAngle);					
		}
		
		//exterior
		else if(var.equals("/root/thisVehicle/exterior/lights/Properties/headlights")){
			String lightState = car.getLightState();
			value = lightState;			
		}
		else if(var.equals("/root/thisVehicle/exterior/gearUnit/Properties/currentGear")){
			int selectedGear = car.getTransmission().getGear();
			value = String.valueOf(selectedGear);			
		}
		else if(var.equals("/root/thisVehicle/exterior/engineCompartment/engine/Properties/running")){
			int engineOn;
			if(car.isEngineOn())
				engineOn = 1;
			else
				engineOn = 0;
			value = String.valueOf(engineOn);			
		}
		else if(var.equals("/root/thisVehicle/exterior/engineCompartment/engine/Properties/actualRpm")){
			int rpm = (int) car.getTransmission().getRPM();
			value = String.valueOf(rpm);			
		}
		else if(var.equals("/root/thisVehicle/exterior/fueling/fuelType/Properties/currentConsumption")){
			float fuelConsumption = car.getPowerTrain().getLitersPer100Km();  // current fuel consumption
			value = String.valueOf(fuelConsumption);
		}
		else if(var.equals("/root/thisVehicle/exterior/fueling/fuelType/tank/Properties/maxAmount")){
			float maxFuelCapacity = 60; //TODO set max Capacity
			value = String.valueOf(maxFuelCapacity);
		}
		else if(var.equals("/root/thisVehicle/exterior/fueling/fuelType/tank/Properties/actualAmount")){
			float fuelLeft = 60 - car.getPowerTrain().getTotalFuelConsumption(); //TODO set max Capacity
			value = String.valueOf(fuelLeft);			
		}
		
		//physicalAttributes
		else if(var.equals("/root/thisVehicle/physicalAttributes/Properties/latitude")){
			Vector3d geoPosition = car.getGeoPosition();
			double latitude = geoPosition.getX();  // N-S position in geo coordinates
			value = String.valueOf(latitude);
		}
		else if(var.equals("/root/thisVehicle/physicalAttributes/Properties/longitude")){
			Vector3d geoPosition = car.getGeoPosition();
			double longitude = geoPosition.getY(); // W-E position in geo coordinates
			value = String.valueOf(longitude);		
		}
		else if(var.equals("/root/thisVehicle/physicalAttributes/Properties/altitude")){
			Vector3d geoPosition = car.getGeoPosition();
			double altitude = geoPosition.getZ();  // meters above sea level
			value = String.valueOf(altitude);
		}
		else if(var.equals("/root/thisVehicle/physicalAttributes/Properties/orientation")){
			float orientation = car.getHeadingDegree();  // 0..360 degree
			value = String.valueOf(orientation);
		}
		else if(var.equals("/root/thisVehicle/physicalAttributes/Properties/speed")){
			float speed = FastMath.abs(car.getCarControl().getCurrentVehicleSpeedKmHour());  // in Km/h
			value = String.valueOf(speed);
		}
		else if(var.equals("/root/thisVehicle/physicalAttributes/Properties/rise")){
			//TODO how to calc rise?
		}
		else if(var.equals("/root/thisVehicle/physicalAttributes/Properties/accelerationLateral")){
			//TODO how to calc?			
		}
		else if(var.equals("/root/thisVehicle/physicalAttributes/Properties/rotation")){
			//TODO how to calc?
		}
		else if(var.equals("/root/thisVehicle/physicalAttributes/Properties/accelerationRotation")){
			//TODO how to calc?			
		}
		else if(var.equals("/root/thisVehicle/physicalAttributes/Properties/acceleration")){
			//TODO how to calc?			
		}
		
		return value;
	}
	
	public synchronized String getValues(String[] list, boolean nameOnly){
		String value ="";
		
		List <String> arrList = new ArrayList<String>();
		
		for (Map.Entry<String,Boolean> entry: dataMap.entrySet()) {
			for (int i = 0; i < list.length; i++) {
				if(entry.getKey().contains(list[i])){
					arrList.add(entry.getKey());
					break;
				}
			}			
		}	
		
		String[] varList = arrList.toArray(new String[arrList.size()]); 
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
				
			Element rootElement = document.createElement("root");
			
			
			for (int j = 0; j < varList.length; j++) {				
					String path = varList[j];
					String[] nodes = path.split("/");
					
					Element e = rootElement;
					
					for (int i = 2; i < nodes.length; i++) {
						NodeList nL = e.getElementsByTagName(nodes[i]);	
						
						if(nL.getLength() > 0)
							e = (Element) nL.item(0);
						else{
							Element e2 = document.createElement(nodes[i]);
							e.appendChild(e2);
							e = e2;
						}
					}
					
					if(!nameOnly)
						e.setTextContent(getValue(path));				
			}				
	
					
			
			document.appendChild(rootElement);		
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
			
			value = output;		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return value;
	}
			
	public String getSchema(){
		return dataSchema;
	}
	
	public synchronized void subscribe(String s){
		for (Map.Entry<String,Boolean> entry: dataMap.entrySet()) {
			if(entry.getKey().contains(s))
				entry.setValue(true);
		}		
	}
	
	public synchronized void unsubscribe(String s){
		for (Map.Entry<String,Boolean> entry: dataMap.entrySet()) {
			if(entry.getKey().contains(s))
				entry.setValue(false);
		}				
	}
	
	public synchronized String getAllSubscribedValues(boolean nameOnly){
		List<String> subscribedValues = new ArrayList<String>();
		
		for (Map.Entry<String,Boolean> entry: dataMap.entrySet()) {
			if(entry.getValue())
				subscribedValues.add(entry.getKey());
		}			
				
		String[] arr = new String[subscribedValues.size()];
		arr = subscribedValues.toArray(arr);
		
		return getValues(arr, nameOnly);
	}

}
