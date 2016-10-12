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

import de.dfki.automotive.kapcom.knowledgebase.KAPcomException;
import de.dfki.automotive.kapcom.knowledgebase.NetClient;
import de.dfki.automotive.kapcom.knowledgebase.PropertyValue;
import de.dfki.automotive.kapcom.knowledgebase.ontology.*;
import eu.opends.basics.SimulationBasics;
import eu.opends.main.DriveAnalyzer;
import eu.opends.main.Simulator;

/**
 * 
 * @author Michael Feld, Rafael Math
 */
final public class KnowledgeBase extends Thread
{
	public final static String CULTURE_GERMAN = "de-DE";
	public final static String CULTURE_ENGLISH = "en-US";
	
	/** Singleton KB instance */
	public static KnowledgeBase KB = new KnowledgeBase(true, true);

	private boolean isRunning = true;
	private boolean connect = false;
	private boolean fallback = true;
	private int outgoingUpdateIntervalMsec = 100;
	private long lastOutgoingUpdate = 0;
	private String culture = CULTURE_GERMAN;
	private NetClient client = null;
	private Root root = null;
	
	private UserKnowledge user = null;
	private VehicleKnowledge vehicle = null;
	
	private SimulationBasics sim;
	
	public KnowledgeBase(boolean connect, boolean fallback)
	{
		this.connect = connect;
		this.fallback = fallback;
	}

	/**
	 * Sets whether the knowledge base tries to connect to a KAPcom server running on the local machine and default port.
	 * If false, then the knowledge is initialized with off-line defaults.
	 * 
	 * @param value
	 * 			If false, then the knowledge is initialized with off-line defaults.
	 */
	public void setConnect(boolean value) {connect = value;}
	public boolean getConnect() {return connect;}

	/**
	 * If set to true, it is silently ignored if KAPcom is not available, and the off-line model is used instead.
	 * 
	 * @param value
	 * 			If set to true, it is silently ignored if KAPcom is not available, and the off-line model is used instead.
	 */
	public void setFallback(boolean value) {fallback = value;}
	public boolean getFallback() {return fallback;}

	/**
	 * Sets the language for all knowledge management related functions. Use <b>de-DE</b> for German and <b>en-US</b> for English. 
	 * The default is German.
	 * 
	 * @param value
	 * 			Set language.
	 */
	public void setCulture(String value) {culture = value;}
	public String getCulture() {return culture;}
	
	/**
	 * Sets the interval (in milliseconds) in which position (and other) is sent to KAPcom for use by other services. 0 to disable.
	 * @param value
	 * 			Set interval.
	 */
	public void setOutgoingUpdateIntervalMillis(int value) {outgoingUpdateIntervalMsec = value;}
	public int getOutgoingUpdateIntervalMillis() {return outgoingUpdateIntervalMsec;}

	/**
	 * Initializes the knowledge store. This might establish a connection to KAPcom.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param host
	 * 			IP of host machine.
	 * 
	 * @param port
	 * 			Port used for connection.
	 */
	public void Initialize(SimulationBasics sim, String host, int port)
	{
		this.sim = sim;
		
		if (connect) {
			System.out.println("Connecting to KAPcom knowledge base on host '" + host + "' (port: " + port + ")");
			client = new NetClient("OpenDS");
			try {
				client.connect(host, port);
				client.connect();
				client.setCulture(culture);
				root = new Root(client);
			} catch (Exception e) {
				if (fallback) {
					// this is not an error
					client = null;
					System.out.println("KAPcom is not available, running in offline mode.");
					//e.printStackTrace();
				} else {
					System.err.println("Failed to connect to KAPcom.");
					e.printStackTrace();
					return;
				}
			}
			// Set map name
			try {
				root.thisVehicle().getphysicalAttributes(true).setScenarioName(Simulator.getDrivingTask().getFileName());
			} catch (Exception e) {
			}
		}
		if (!connect || client == null) {
			// not connected
			//client = null;
		}
		user = new UserKnowledge(this);
		vehicle = new VehicleKnowledge(this);
	}
	
	/**
	 * Returns whether KAPcom access is currently enabled. It does not test the actual connectivity.
	 * 
	 * @return
	 * 			True, if KAPcom access is currently enabled.
	 */
	public boolean isConnected()
	{
		return this.client != null;
	}
	
	public UserKnowledge getUser() { return user; }
	public static UserKnowledge User() { return KB.getUser(); }

	public VehicleKnowledge getVehicle() { return vehicle; }
	public static VehicleKnowledge Vehicle() { return KB.getVehicle(); }

	Root getRoot() { return root; }

	NetClient getClient() { return client; }
	
	public static String getString(String id)
	{
		if (!KB.isConnected()) return id;
		try {
			return KB.getClient().sendGetResString(id);
		} catch (KAPcomException e) {
			return id;
		}
	}
	public static String expandString(String str)
	{
		if (!KB.isConnected()) return str;
		try {
			return KB.getClient().sendExpandStringReferences(str);
		} catch (KAPcomException e) {
			return str;
		}
	}
	
	
	public void sendSetProperty(String path, String propertyName, String propertyValue, String propertyType)
	{	
		if (!KB.isConnected()) 
		{
			System.out.println("[sendSingleValue] Not Connected to KB. Could not set " + 
					path + "::" + propertyName + " to value '" + propertyValue + "'");
			return;
		}
		
		try {
			PropertyValue valueObject;
			
			if(propertyType.equalsIgnoreCase("short") || propertyType.equalsIgnoreCase("byte"))
				valueObject = new PropertyValue(new Short(propertyValue));
			else if(propertyType.equalsIgnoreCase("int"))
				valueObject = new PropertyValue(new Integer(propertyValue));
			else if(propertyType.equalsIgnoreCase("long"))
				valueObject = new PropertyValue(new Long(propertyValue));
			else if(propertyType.equalsIgnoreCase("boolean"))
				valueObject = new PropertyValue(new Boolean(propertyValue));
			else if(propertyType.equalsIgnoreCase("float"))
				valueObject = new PropertyValue(new Float(propertyValue));
			else if(propertyType.equalsIgnoreCase("double"))
				valueObject = new PropertyValue(new Double(propertyValue));
			else
				// for char, String and all others types different from types above
				valueObject = new PropertyValue(propertyValue);
			
			// create path with sub folders (if not existing)
			String parent = "";
			for(String folder : path.split("/"))
			{
				if(!folder.isEmpty())
				{
					KB.getClient().sendGetInstanceByShortID(parent, folder, true, propertyType);
					parent += "/" + folder;
				}			
			}
			
			// write value to property of given path
			KB.getClient().sendSetProperty(path, propertyName, valueObject, true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Sends information about the current vehicle status to KAPcom for use by other applications.
	 * This may be called many times a second (update rate is limited internally).
	 */
	//public synchronized void sendCarData(Car car)
	@Override
    public void run()
	{
		while(isRunning)
		{
			if (outgoingUpdateIntervalMsec <= 0 || !isConnected()) return;
			if (System.currentTimeMillis() - lastOutgoingUpdate > outgoingUpdateIntervalMsec) {
				lastOutgoingUpdate = System.currentTimeMillis();
				try{
					if(sim instanceof Simulator)
						getVehicle().sendCarData(((Simulator)sim));
					else if(sim instanceof DriveAnalyzer)
						getVehicle().sendAnalyzerData(((DriveAnalyzer)sim).getCurrentDataUnit());
				} catch (Exception ex) {
					ex.printStackTrace();
					System.err.println("Failed to send update to KAPcom. Will stop sending updates for 60 seconds.");
					lastOutgoingUpdate += (1000 * 60);
					return;
				}
			}
		}
		if(KB.isConnected())
		{
			KB.getClient().disconnect();
			System.out.println("Closed connection to KAPcom");
		}
	}

	
	public synchronized void disconnect()
	{
		isRunning = false;
	}
}
