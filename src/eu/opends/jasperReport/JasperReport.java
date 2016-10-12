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

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;
import eu.opends.tools.Util;

/**
 * Class for handling database connection and logging to database
 * 
 * @author Rafael Math
 */
public class JasperReport 
{
	protected String reportTemplate;
    protected Connection connection;
    protected PreparedStatement statement;
    protected PreparedStatement additionalStatement;
    protected boolean useAdditionalTable;

    protected String outputFolder;
    protected String fileName = "report.pdf";
    protected boolean createReport = true;
    protected boolean openReport = true;
    protected Map<String, Object> parameters;

    
    /**
     * Constructor, that creates database connection and prepared statement for fast query execution.
     * 
     * @param reportTemplate
     * 			Path of report template file.
     * 
     * @param url
     * 			URL of database.
     * 
     * @param user
     * 			User name for database access.
     * 
     * @param pass
     * 			Password for database access.
     * 
     * @param table
     * 			Database table.
     * 
     * @param useAdditionalTable
     * 			If true, additional table will be created.
     */
    public JasperReport(String reportTemplate, String url, String user, String pass, String table,
    		boolean useAdditionalTable) 
    {
    	this.reportTemplate = reportTemplate;
    	this.useAdditionalTable = useAdditionalTable;
    	this.outputFolder = Simulator.getOutputFolder();
    	
    	String tempFileName = Simulator.getSettingsLoader().getSetting(Setting.Analyzer_fileName, "");
    	if(tempFileName != null && !tempFileName.isEmpty())
    		fileName = tempFileName;
    	
    	boolean suppressOpen = Simulator.getSettingsLoader().getSetting(Setting.Analyzer_suppressPDFPopup, 
				SimulationDefaults.Analyzer_suppressPDFPopup);
    	
    	openReport = !suppressOpen;
    	
		this.parameters = new HashMap<String, Object>();
    	
        try {
        	
            // Loading database connection driver for MySQL server connection
            Class.forName("com.mysql.jdbc.Driver").newInstance();
    		
            // Creating connection to local database
            connection = DriverManager.getConnection(url, user, pass);

            if(!connection.isClosed())
            	System.out.println("Successfully connected to MySQL server using TCP/IP...");
      	
        } catch(Exception e) {

        	e.getStackTrace();
        }
    }

   
    /**
     * Closes database connection after logging ended
     */
    public void createPDF()
    {
        try {
        	
        	boolean reportCreated = false;
        	
        	if(createReport)
        		reportCreated = createReport();
        	
        	if(reportCreated && openReport)
				Util.open(outputFolder + "/" + fileName);
			
			if(statement != null)
				statement.close();
			
			if(additionalStatement != null)
				additionalStatement.close();
			
			if(connection != null)
				connection.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(JasperReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
	private boolean createReport()
	{
		boolean success = false;
		
		try{
			
			/*
			// used for source file
			InputStream inputStream = new FileInputStream("assets/JasperReports/templates/driver.jrxml");
			JasperDesign reportDesign = JRXmlLoader.load(inputStream);
			JasperReport report = JasperCompileManager.compileReport(reportDesign);
			*/
			
			// maybe try XML data source instead of database connection
			//JRDataSource dataSource = new JaxenXmlDataSource(new File("input.xml"));
			
			//get report template for driver or passenger task
			InputStream reportStream = new FileInputStream(reportTemplate);
			
			// fill report with parameters and data from database
			JasperPrint print = JasperFillManager.fillReport(reportStream, parameters, connection);
			
			// create PDF file
			long start = System.currentTimeMillis();
			JasperExportManager.exportReportToPdfFile(print, outputFolder + "/" + fileName);
			System.out.println("PDF creation time : " + (System.currentTimeMillis() - start) + " ms");
			
			success = true;
			
		} catch (Exception e) {

			//System.err.println("Could not create report. Maybe PDF is still open?");
			e.printStackTrace();
		}
		
		return success;
	}

}
