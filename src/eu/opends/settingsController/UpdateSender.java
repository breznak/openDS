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

/**
 * 
 * @author Daniel Braun
 */
public class UpdateSender extends Thread 
{
	
	private APIData  data;
	ConnectionHandler connectionHandler;
	
	public UpdateSender(APIData data, ConnectionHandler connectionHandler){
		this.data = data;
		this.connectionHandler = connectionHandler;
	}
	
	public void run(){
		while(!isInterrupted()){
			
			String response = "<Message><Event Name=\"SubscribedValues\">\n" + data.getAllSubscribedValues(false) + "\n</Event></Message>\n";
			connectionHandler.sendResponse(response);
						
			try {
				Thread.sleep(connectionHandler.getUpdateInterval());
			} catch (InterruptedException e) {
				this.interrupt();//e.printStackTrace();
			}
		}
	}

}
