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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import eu.opends.main.Simulator;

/**
 * 
 * @author Daniel Braun
 */
public class ConnectionHandler extends Thread 
{	
	private Simulator sim;
	private OutputStream out;
	private DataInputStream in;
	private UpdateSender updateSender;	
	private APIData data;
	
	private int updateInterval = 1000; //in ms
	
	private Lock intervalLock = new ReentrantLock();
	
	
	public static int byteArrToInt(byte[] b){
		int value = 0;
		
		for (int i = 0; i < b.length; i++)
   	 	{
			value += ((long) b[i] & 0xffL) << (8 * i);
   	 	}
		
		return value;
	}
	
	public static String byteArrToStr(byte[] b){
		Charset charset = Charset.forName("UTF-8");
		int i;		
		for (i = 0; i < b.length && b[i] != 0; i++) { }		
		String str = new String(b, 0, i, charset);
		return str;
	}
	
	private static Document loadXMLFromString(String xml) throws Exception
    {		
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
	
	public ConnectionHandler(Simulator s, OutputStream o, DataInputStream i){
		sim = s;
		out = o;
		in = i;	
		
		data = new APIData(sim.getCar());		
		updateSender = new UpdateSender(data, this);
	}
	
	public void run(){		
		while(!isInterrupted()){
			try{					
	        	 BufferedReader r = new BufferedReader(new InputStreamReader(in));
	        	 	        	 
	        	 String messageValue = "";
	        	 
	        	 try{
	        		 while(!isInterrupted()){
	        			 
	        			 try
	        			 {
	        				 String line = r.readLine();
		        		 
	        				 if(line == null){
	        					 interrupt();
	        					 System.out.println("Connection closed by client.");
	        					 break;
	        				 }
	        				 else {
	        					 messageValue += line;
		        			 
	        					 if(line.contains("</Message>"))
	        						 break;
	        				 }
	        		
	        			 } catch (SocketTimeoutException e) {
	        			 }
		        	 }        	
	        	 }catch(SocketException e){
	        		 interrupt();
	        		 System.out.println("Connection closed by client.");
	        		 break;
	        	 }	        	 
	        	 	        		        	 
	        	 if(!messageValue.equals("")){
	        		 parseXML(messageValue);
	        	 }
	        }catch(Exception e){
				e.printStackTrace();
			}			
		}	
		
		try {
			out.close();
			updateSender.interrupt();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public int getUpdateInterval(){
		int value = 0;
		
		intervalLock.lock();
		try{
			value = updateInterval;
		}
		finally{			
			intervalLock.unlock();			
		}		
		
		return value;
	}
	
	public void setUpdateInterval(int ui){
		intervalLock.lock();
		try{
			updateInterval = ui;
		}
		finally{
			intervalLock.unlock();
		}			
	}
	
	private void parseXML(String xml) {
		try {						
			Document doc = loadXMLFromString(xml);			
			doc.getDocumentElement().normalize();			
			String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			
			NodeList nodes = doc.getElementsByTagName("Event");		
			
			response += "<Message>";
			
			for (int i = 0; i < nodes.getLength(); i++) {
				String eventName = (((Element) nodes.item(i)).getAttribute("Name"));				
				
				if(eventName.equals("EstablishConnection")){	
					String val =nodes.item(i).getTextContent();
					
					if(val.length() > 0){
						try{
							updateInterval = Integer.valueOf(val);
						} catch(Exception e){}
					}
					
					if(!updateSender.isAlive())
						updateSender.start();
					
					response += "<Event Name=\"ConnectionEstablished\"/>\n";
				}
				else if(eventName.equals("AbolishConnection")){				
					response += "<Event Name=\"ConnectionAbolished\"/>\n";
					this.interrupt();
				}
				else if(eventName.equals("GetDataSchema")){				
					response += "<Event Name=\"DataSchema\">\n" + data.getSchema() + "\n</Event>";
				}
				else if(eventName.equals("GetSubscriptions")){				
					response += "<Event Name=\"Subscriptions\">\n" + data.getAllSubscribedValues(true) + "\n</Event>";
				}				
				else if(eventName.equals("GetSubscribedValues")){
					response += "<Event Name=\"SubscribedValues\">\n" + data.getAllSubscribedValues(false) + "\n</Event>";
				}
				else if(eventName.equals("GetValue")){				
					String[] val = new String[]{nodes.item(i).getTextContent()};
					response += "<Event Name=\""+val[0]+"\">\n" + data.getValues(val, false) + "\n</Event>";
				}
				else if(eventName.equals("GetUpdateInterval")){
					response += "<Event Name=\"UpdateInterval\">\n" + String.valueOf(getUpdateInterval()) + "\n</Event>";
				}
				else if(eventName.equals("SetUpdateInterval")){
					String val =nodes.item(i).getTextContent();
					setUpdateInterval(Integer.valueOf(val));
					response += "<Event Name=\"UpdateInterval\">\n" + String.valueOf(getUpdateInterval()) + "\n</Event>";
				}
				else if(eventName.equals("Subscribe")){		
					data.subscribe(nodes.item(i).getTextContent());
					response += "<Event Name=\"Subscriptions\">\n" + data.getAllSubscribedValues(true) + "\n</Event>";
				}
				else if(eventName.equals("Unsubscribe")){	
					data.unsubscribe(nodes.item(i).getTextContent());
					response += "<Event Name=\"Subscriptions\">\n" + data.getAllSubscribedValues(true) + "\n</Event>";
				}
				else{
					System.err.println("Unknow event received!");
					return;
				}			
				
						
			}
			
			response += "</Message>\n";
			
			
			
			sendResponse(response);		
			
			
		} catch (Exception e) {;
			System.err.println("No valid XML data received!");
			e.printStackTrace();
		}		
	}
	
	public synchronized void sendResponse(String response){		
		try {
			byte[] msg = (response).getBytes("UTF-8");			
			out.write(msg);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
