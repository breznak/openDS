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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class MultiDriverServer extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -8791488542719810796L;
	private JButton startButton;
	private JButton resetButton;
	private JTextField maxUpdateField;
	private JTextField portField;
	private JTextArea textArea;	
    private ServerThread server;

	
	public MultiDriverServer()
	{
		this.setLayout(new BorderLayout());
		this.setTitle("Multi-driver Server");
		this.setSize(500, 350);
		//this.setResizable(false);
		this.getContentPane().setLayout(new BorderLayout());	
		
		//CONNECTION
		JPanel topPanel = new JPanel(new FlowLayout());
		
		JLabel maxUpdateLabel = new JLabel("max. update rate:");
		maxUpdateField = new JTextField(5);
		
		JLabel portLabel = new JLabel("port:");
		portField = new JTextField(5);
		
		startButton = new JButton("start server");
		startButton.addActionListener(this);
		
		resetButton = new JButton("stop server");
		resetButton.setEnabled(false);
		resetButton.addActionListener(this);
		
		
		topPanel.add(maxUpdateLabel);
		topPanel.add(maxUpdateField);
		
		topPanel.add(portLabel);
		topPanel.add(portField);
		
		topPanel.add(startButton);
		
		topPanel.add(resetButton);
		
		
		//LOG
		JPanel bottomPanel = new JPanel(new BorderLayout());	     
	   
	    JLabel serverLog = new JLabel("Current drivers:");
	    textArea = new JTextArea(15, 30);
	    textArea.setEditable(false);
	    JScrollPane scroll = new JScrollPane(textArea);

	    bottomPanel.add(serverLog, BorderLayout.NORTH);
	    bottomPanel.add(scroll);
	    
	    this.getContentPane().add(BorderLayout.NORTH, topPanel);
	    this.getContentPane().add(BorderLayout.SOUTH, bottomPanel);
	}
	
	
	public static void main(String[] args) 
	{		
		MultiDriverServer server = new MultiDriverServer();
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		server.setVisible(true);		
	}

	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getSource() == startButton)
		{		
			int maxUpdateRate;
			try {
				
				maxUpdateRate = Integer.parseInt(maxUpdateField.getText());
				maxUpdateRate = Math.min(Math.max(maxUpdateRate, 1),1000);
			
			} catch (Exception e1) {
				maxUpdateRate = 24;
			}
			maxUpdateField.setText(maxUpdateRate + "");
			
			
			int port;
			try {
				
				port = Integer.parseInt(portField.getText());
				port = Math.min(Math.max(port, 1),65535);
			
			} catch (Exception e1) {
				port = 4510;
			}
			portField.setText(port + "");
			
			
			server = new ServerThread(maxUpdateRate, port, textArea);
			
			//System.out.println("server started");	
			
			maxUpdateField.setEnabled(false);
			portField.setEnabled(false);
	        startButton.setEnabled(false);
	        resetButton.setEnabled(true);
		}
		else if(arg0.getSource() == resetButton)
		{ 
			server.stopServer();

			//System.out.println("server stopped");
			
			maxUpdateField.setEnabled(true);
			portField.setEnabled(true);
			startButton.setEnabled(true);
	        resetButton.setEnabled(false);
	        textArea.replaceRange("", 0, textArea.getText().length());
	     }
	}
}
