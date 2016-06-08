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


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;


public class SettingsControllerClient extends JFrame implements ActionListener{

	private JButton connectButton;
	private JButton openButton;
	private JTextField ipField;
	private JTextField portField;
	private JTextArea ta;
	
	private OutputStream out;
	private DataInputStream in;
	
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
	
	
	public SettingsControllerClient(){
	
		
	
		this.setLayout(new BorderLayout());
		this.setTitle("openDS test-client");
		this.setSize(800, 350);
		this.getContentPane().setLayout(new BorderLayout());	
		
		//CONNECTION
		JPanel topPanel = new JPanel(new FlowLayout());
		
		JLabel ipLabel = new JLabel("Server IP:");
		ipField = new JTextField(9);
		
		JLabel portLabel = new JLabel("Server port:");
		portField = new JTextField(3);
		
		connectButton = new JButton("connect");
		connectButton.addActionListener(this);
		
		openButton = new JButton("send command");
		openButton.setEnabled(false);
		openButton.addActionListener(this);
		
		
		topPanel.add(ipLabel);
		topPanel.add(ipField);
		
		topPanel.add(portLabel);
		topPanel.add(portField);
		
		topPanel.add(connectButton);
		
		topPanel.add(openButton);
		
		
		//LOG
		JPanel bottomPanel = new JPanel(new BorderLayout());	     
	   
	    JLabel serverLog = new JLabel("Message log:");
	    ta = new JTextArea(15, 30);
	    ta.setEditable(false);
	    JScrollPane scroll = new JScrollPane(ta);

	    bottomPanel.add(serverLog, BorderLayout.NORTH);
	    bottomPanel.add(scroll);
	    
	    this.getContentPane().add(BorderLayout.NORTH, topPanel);
	    this.getContentPane().add(BorderLayout.SOUTH, bottomPanel);
	}
	
	public static void main(String[] args) {		
		SettingsControllerClient client = new SettingsControllerClient();
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		client.setVisible(true);		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getSource() == connectButton){
			//establish connection
			System.out.println("try to connect");
			
			String filename,xml,port,ip;
	    	
	        Socket socket = null;
	        out = null;
	        in = null;
	        
	        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));	        
	       
	        ip = ipField.getText();
	        port = portField.getText();
	 
	        try {
	            socket = new Socket(ip, Integer.valueOf(port));
	            out = socket.getOutputStream();
	            in = new DataInputStream(socket.getInputStream());
	        } catch (UnknownHostException e) {
	            System.err.println("Don't know about host");
	            return;
	        } catch (IOException e) {
	            System.err.println("Couldn't get I/O for the connection");
	            return;
	        } catch (NumberFormatException e){
	        	System.err.println("Port is not a valid number");
	        	return;
	        }		
			
	        connectButton.setEnabled(false);
	        openButton.setEnabled(true);
	        
	        ta.append("----CONNECTED----\n\r");       	
       		ta.setCaretPosition(ta.getText().length());
	        
	        Thread t = new Thread(new Receiver());
			 t.start();
		}
		else if(arg0.getSource() == openButton){ 
			 Thread t = new Thread(new PTask());
			 t.start();
	     }
		
		
		
	}
	
	private class PTask implements Runnable{

		@Override
		public void run() {
			sendCommand();			
		}		
	}
	
	private class Receiver implements Runnable{

		@Override
		public void run() {		
			while(true){
				try{	        	
					BufferedReader r = new BufferedReader(new InputStreamReader(in));
	 	        	 
		        	 String messageValue = "";
		        	 
		        	 try{
			        	 while(true){
			        		 String line = r.readLine();
			        		 
			        		 if(line == null){
			        			 System.out.println("Connection closed by server.");
			        			 break;
			        		 }
			        		 else {
			        			 messageValue += line;
			        			 
			        			 if(line.contains("</Message>"))
			        				 break;
			        		 }
			        	 }        	
		        	 }catch(SocketException e){		        		 
		        		 System.out.println("Connection closed by server.");
		        		 break;
		        	 }	        	 
		        	 	        		        	 
		        	 if(!messageValue.equals("")){
		        		 ta.append("----RECEIVE----\n\r");
			        	 ta.append(messageValue+"\n\r");
			        	 ta.setCaretPosition(ta.getText().length());
		        	 }		        	 
		        	 
				}catch(Exception e){
					e.printStackTrace();
				}
			}		
		}
	}
	
	private void sendCommand(){
			JFileChooser j = new JFileChooser();
			 j.setFileSelectionMode(JFileChooser.FILES_ONLY );
			 Integer opt = j.showOpenDialog(this);		
			 //boolean showSaveDialog = true;
			 
			 if (opt == JFileChooser.APPROVE_OPTION) {
		            File file = j.getSelectedFile();
		            System.out.println(file.getAbsolutePath());
		            try{
		            FileReader input = new FileReader(file.getAbsolutePath());
					BufferedReader bufRead = new BufferedReader(input);			
		            String line;
		            String xml ="";
		            
		            line = bufRead.readLine();
		            while (line != null){
		                xml = xml+line+"\n";  
		                line = bufRead.readLine();
		            }
		            
		            bufRead.close();
		        			        			        	
		        	byte[] msg = (xml).getBytes("UTF-8");
		        	
		        	
		        	ta.append("----SEND----\n\r");
		        	ta.append(xml);
		        	ta.setCaretPosition(ta.getText().length());
		        			
		        	out.write(msg);
		            }
		            catch(Exception e){
		            	e.printStackTrace();		            	
		            }
			 }
		}
	

}
