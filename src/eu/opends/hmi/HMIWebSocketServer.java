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


package eu.opends.hmi;


import java.net.InetSocketAddress;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class HMIWebSocketServer extends WebSocketServer
{
	boolean debug = true;
	
	public HMIWebSocketServer(InetSocketAddress address)
	{
		super(address);
		this.start();
	}
	

	// Sender
	public void sendMsg (String msg) 
	{
		if (!this.connections().isEmpty()) 
		{
			System.err.println("#connections: " + this.connections().size());
			for (WebSocket socket : this.connections()) 
			{
				if (socket.isOpen())
					socket.send(msg);
				else 
					System.out.println("[Connection] Failed to send");
			}
		}
	}


	@Override
	public void onClose(WebSocket socket, int code, String reason, boolean remote) 
	{
		if (debug) 
			System.out.println("[Connection] Closed " + socket.toString() + ": "+ reason);
	}

	
	@Override
	public void onError(WebSocket socket, Exception except)
	{
		if (debug) 
			System.out.println("[Connection] Error occured: " + except.getMessage());		
	}

	
	@Override
	public void onMessage(WebSocket sender, String msg) 
	{
		if (debug) 
			System.out.println("[Connection] Received: " + msg + " from " + sender.toString());
	}

	
	@Override
	public void onOpen(WebSocket socket, ClientHandshake handshake) 
	{
		if (debug) 
			System.out.println("[Connection] Connected: " + socket.toString());
	}
	
}