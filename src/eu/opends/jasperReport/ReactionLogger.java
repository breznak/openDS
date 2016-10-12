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

package eu.opends.jasperReport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;
import eu.opends.tools.Util;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JaxenXmlDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 * 
 * @author Rafael Math
 */
public class ReactionLogger 
{
	private boolean isRunning = false;
	private String dataFileName = "reactionData.xml";
	private String reportFileName = "reactionReport.pdf";
	private String outputFolder;
	BufferedWriter bw;


	private void start()
	{
		try
		{		
			outputFolder = Simulator.getOutputFolder();
			Util.makeDirectory(outputFolder);
			
			bw = new BufferedWriter(new FileWriter(outputFolder + "/" + dataFileName));
			bw.write("<?xml version=\"1.0\"?>\n");    
			bw.write("<report>\n");
			
			isRunning = true;
			
		} catch (IOException e) {
	
			e.printStackTrace();
		}		
	}
	
	
	public void add(String reactionGroup, int reactionResult, long reactionTime, 
			long absoluteTime, long experimentTime, String comment)
	{
		if(!isRunning)
			start();
		
		if(isRunning)
		{
			try {
				
	            bw.write("\t<reactionMeasurement>\n");
	            
	            bw.write("\t\t<reactionGroup>" + reactionGroup + "</reactionGroup>\n");
				
				bw.write("\t\t<reactionResult>" + reactionResult + "</reactionResult>\n");
				
				bw.write("\t\t<reactionTime>" + reactionTime + "</reactionTime>\n");
				
				bw.write("\t\t<absoluteTime>" + absoluteTime + "</absoluteTime>\n");
				
				bw.write("\t\t<experimentTime>" + experimentTime + "</experimentTime>\n");
				
				bw.write("\t\t<comment>" + comment + "</comment>\n");

	            bw.write("\t</reactionMeasurement>\n");
	            
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	
	public void close()
	{
		if(isRunning)
		{
			isRunning = false;
			
			try {
				
				bw.write("</report>\n");        
				bw.close();
				generateReport();
				
			} catch (IOException e) {
	
				e.printStackTrace();
			}
		}
	}
	
	
	private void generateReport()
	{
		try
		{
			// open XML data source
			JRDataSource dataSource = new JaxenXmlDataSource(new File(outputFolder + "/" + dataFileName),
					"report/reactionMeasurement");

			//get report template for reaction measurement
			//InputStream reportStream = new FileInputStream("assets/JasperReports/templates/reactionMeasurement.jasper");
			InputStream inputStream = new FileInputStream("assets/JasperReports/templates/reactionMeasurement.jrxml");
			JasperDesign design = JRXmlLoader.load(inputStream);
			JasperReport report = JasperCompileManager.compileReport(design);

			// fill report with parameters and data
			Map<String, Object> parameters = getParameters();
			JasperPrint print = JasperFillManager.fillReport(report, parameters, dataSource);
			
			// create PDF file
			long start = System.currentTimeMillis();
			JasperExportManager.exportReportToPdfFile(print, outputFolder + "/" + reportFileName);
			System.out.println("PDF creation time : " + (System.currentTimeMillis() - start) + " ms");
			
			// open PDF file
			boolean suppressPDF = Simulator.getSettingsLoader().getSetting(Setting.Analyzer_suppressPDFPopup, 
					SimulationDefaults.Analyzer_suppressPDFPopup);
			
			if(!suppressPDF)
				Util.open(outputFolder + "/" + reportFileName);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}


	private Map<String, Object> getParameters() 
	{
		SettingsLoader settingsLoader = Simulator.getDrivingTask().getSettingsLoader();
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		String groupRed = settingsLoader.getSetting(Setting.ReactionMeasurement_groupRed, " ");
		if(!groupRed.isEmpty())
			parameters.put("groupRed", groupRed);

		String groupYellow = settingsLoader.getSetting(Setting.ReactionMeasurement_groupYellow, "  ");
		if(!groupYellow.isEmpty())
			parameters.put("groupYellow", groupYellow);

		String groupGreen = settingsLoader.getSetting(Setting.ReactionMeasurement_groupGreen, "   ");
		if(!groupGreen.isEmpty())
			parameters.put("groupGreen", groupGreen);
		
		String groupCyan = settingsLoader.getSetting(Setting.ReactionMeasurement_groupCyan, "    ");
		if(!groupCyan.isEmpty())
			parameters.put("groupCyan", groupCyan);
		
		String groupBlue = settingsLoader.getSetting(Setting.ReactionMeasurement_groupBlue, "     ");
		if(!groupBlue.isEmpty())
			parameters.put("groupBlue", groupBlue);
		
		String groupMagenta = settingsLoader.getSetting(Setting.ReactionMeasurement_groupMagenta, "      ");
		if(!groupMagenta.isEmpty())
			parameters.put("groupMagenta", groupMagenta);

		return parameters;
	}
}
