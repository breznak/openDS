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

package eu.opends.environment;


import java.net.*;

import eu.opends.main.Simulator;

/**
 * This class connects to an external traffic light program via an UDP socket.
 * 
 * @author Rafael Math
 */
public class TrafficLightExternalConnector extends Thread 
{
	private Simulator sim;
	private boolean stoprequested;
	private DatagramPacket packet;
	private DatagramSocket incomingSocket;

	
	/**
	 * Creates a new connection to an external traffic light controller.
	 * 
	 * @param sim
	 * 			The simulator
	 * 
	 * @param port
	 * 			Number of the port to use for this connection
	 * 
	 * @param packetsize
	 * 			Maximum size of an incoming XML-String
	 */
	public TrafficLightExternalConnector(Simulator sim, int port, int packetsize)
	{
		super("TrafficLightExternalConnectorThread");
		this.sim = sim;
		stoprequested = false;

		try {
			
			// open socket connection and listen for incoming packets
			incomingSocket = new DatagramSocket(port);
			byte data[] = new byte[packetsize];
			packet = new DatagramPacket(data, packetsize);
			
			// set time to wait after an unsuccessful receive attempt
			incomingSocket.setSoTimeout(300);
			
			//System.out.println("UDP connection established");

		} catch (SocketException e) {
			System.err.println("TrafficLightExternalConnector_Constructor: " + e.toString());
		}
	}

	
	/**
	 * Stops the traffic light program by exiting the loop
	 */
	public synchronized void requestStop()
	{
		stoprequested = true;
	}

	
	/**
	 * This method contains a loop that listens to the given port. Incoming
	 * XML-files will be processed. Each execution of this loop takes at 
	 * least 300 milliseconds.
	 */
	@Override
	public void run() 
	{
		int packetsize;
		byte[] packetdata;
		String datastring = "";

		while (!stoprequested)
		{
			try {
				// read data and get length
				incomingSocket.receive(packet);
				packetsize = packet.getLength();
				packetdata = packet.getData();
				
			} catch (SocketTimeoutException e) {
				// suppress error output if no data available at incomingSocket
				// wait 300ms as defined in SoTimeout and try again
				continue;
			} catch(NullPointerException e){
				// suppress error output if no data available at incomingSocket
				continue;
			} catch (Exception e) {
				System.err.println("TrafficLightExternalConnector_run(): " + e.toString());
				continue;
			}
			
			// transform incoming data to string of variable length
			datastring = new String(packetdata, 0, packetsize);

			//System.out.println(datastring);
			sim.getTrafficLightCenter().evaluateInstructionString(datastring);
		}


		// close socket connection
		try {
			if (null != incomingSocket)
				incomingSocket.close();
		} catch (Exception ex) {
		}

		//System.out.println("connection terminated");
	}	
}