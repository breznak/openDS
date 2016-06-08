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


import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLParser
{
	private Document doc;
	private boolean errorOccured = false;
	
	private String modelPath = null;
	private String driverName = null;
	private String updateID = null;
	private Double posX = null;
	private Double posY = null;
	private Double posZ = null;
	private Float rotW = null;
	private Float rotX = null;
	private Float rotY = null;
	private Float rotZ = null;
	private Float heading = null;
	private Float wheelSteering = null;
	private Float wheelPosition = null;
	private String unregisterID = null;
	
	
	/**
	 * Creates a DOM-object from the given input string. If the input string 
	 * is not a valid XML string, a warning message will be returned.
	 * 
	 * @param xmlstring
	 * 			XML input string to parse
	 */
	public XMLParser(String xmlstring)
	{
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

		
		// on "registered" --> set ID
		// on "update" --> perform changes
		// on "unregistered" --> call method requestStop()
		
		if(!errorOccured)
		{		
			// example multi driver instructions:
			//<multiDriver>
			//	<register>
			//		<modelPath>foo/far/test.scene</modelPath>
			//		<driverName>TestDriver</driverName>
			//	<register>
			//</multiDriver>
			//
			//<multiDriver>
			//	<update id="mdv_11">
			//		<position x="1.1" y="2.2" z="3.3" />
			//		<rotation w="1.1" x="2.2" y="3.3" z="4.4" />
			//		<heading>358.4</heading>
			//		<wheel steering="1.1" position="2.2" />
			//	<update>
			//</multiDriver>
			//
			//<multiDriver>
			//	<unregister>mdv_14</unregister>
			//</multiDriver>
			
			NodeList nodeLst = doc.getElementsByTagName("multiDriver");
			for(int i=0; i<nodeLst.getLength(); i++)
			{
				Node currentNode = nodeLst.item(i);
				
				NodeList registerList = ((Element) currentNode).getElementsByTagName("register");
				for(int j=0; j<registerList.getLength(); j++)
				{
					Element currentRegister = (Element) registerList.item(j);
					
					NodeList modelPathList = currentRegister.getElementsByTagName("modelPath");
					for(int k=0; k<modelPathList.getLength(); k++)
					{
						Element currentModelPath = (Element) modelPathList.item(k);
						modelPath = getCharacterDataFromElement(currentModelPath);
					}

					NodeList driverNameList = currentRegister.getElementsByTagName("driverName");
					for(int k=0; k<driverNameList.getLength(); k++)
					{
						Element currentDriverName = (Element) driverNameList.item(k);
						driverName = getCharacterDataFromElement(currentDriverName);
					}
				}
				
				NodeList updateList = ((Element) currentNode).getElementsByTagName("update");
				for(int j=0; j<updateList.getLength(); j++)
				{					
					Element currentUpdate = (Element) updateList.item(j);
					updateID = currentUpdate.getAttribute("id");

					NodeList positionList = currentUpdate.getElementsByTagName("position");
					for(int k=0; k<positionList.getLength(); k++)
					{
						Element currentPosition = (Element) positionList.item(k);
						posX = Double.parseDouble(currentPosition.getAttribute("x"));
						posY = Double.parseDouble(currentPosition.getAttribute("y"));
						posZ = Double.parseDouble(currentPosition.getAttribute("z"));
					}
					
					NodeList rotationList = currentUpdate.getElementsByTagName("rotation");
					for(int k=0; k<rotationList.getLength(); k++)
					{
						Element currentRotation = (Element) rotationList.item(k);
						rotW = Float.parseFloat(currentRotation.getAttribute("w"));
						rotX = Float.parseFloat(currentRotation.getAttribute("x"));
						rotY = Float.parseFloat(currentRotation.getAttribute("y"));
						rotZ = Float.parseFloat(currentRotation.getAttribute("z"));
					}
					
					NodeList headingList = currentUpdate.getElementsByTagName("heading");
					for(int k=0; k<headingList.getLength(); k++)
					{
						Element currentHeading = (Element) headingList.item(k);
						heading = Float.parseFloat(getCharacterDataFromElement(currentHeading));
					}
					
					NodeList wheelList = currentUpdate.getElementsByTagName("wheel");
					for(int k=0; k<wheelList.getLength(); k++)
					{
						Element currentWheel = (Element) wheelList.item(k);
						wheelSteering = Float.parseFloat(currentWheel.getAttribute("steering"));
						wheelPosition = Float.parseFloat(currentWheel.getAttribute("position"));
					}
				}
				
				NodeList unregisterList = ((Element) currentNode).getElementsByTagName("unregister");
				for(int j=0; j<unregisterList.getLength(); j++)
				{
					Element currentUnregister = (Element) unregisterList.item(j);
					unregisterID = getCharacterDataFromElement(currentUnregister);
				}
			}
		}
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
	 */
	public static String getCharacterDataFromElement(Element elem)
	{
		Node child = elem.getFirstChild();
		if (child instanceof CharacterData) 
		{
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		
		return null;
	}

	
	public boolean isRegister()
	{
		return (modelPath != null && driverName != null);
	}


	public boolean isUpdate()
	{
		return (updateID != null && posX != null && posY != null && posZ != null 
				&& ((rotW != null && rotX != null && rotY != null && rotZ != null) || heading != null)
				&& wheelSteering != null && wheelPosition != null);
	}
	
	
	public boolean isUnregister()
	{
		return (unregisterID != null);
	}
	
	
	public String getModelPath() 
	{
		return modelPath;
	}


	public String getDriverName() 
	{
		return driverName;
	}

	
	public Object getUpdateID() 
	{
		return updateID;
	}
	
	
	public Double getPosX() 
	{
		return posX;
	}

	
	public Double getPosY() 
	{
		return posY;
	}
	
	
	public Double getPosZ() 
	{
		return posZ;
	}
	
	
	public Float getRotW() 
	{
		return rotW;
	}
	
	
	public Float getRotX() 
	{
		return rotX;
	}

	
	public Float getRotY() 
	{
		return rotY;
	}
	
	
	public Float getRotZ() 
	{
		return rotZ;
	}
	
	
	public Float getHeading()
	{
		return heading;
	}
	
	
	public Float getWheelSteering() 
	{
		return wheelSteering;
	}
	
	
	public Float getWheelPosition() 
	{
		return wheelPosition;
	}
	
	public String getUnregisterID() 
	{
		return unregisterID;
	}

	
}