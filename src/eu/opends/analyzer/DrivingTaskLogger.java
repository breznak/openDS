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

package eu.opends.analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import eu.opends.tools.Util;

/**
 * This class is used for logging driving task data to a text file. 
 * 
 * @author Rafael Math
 */
public class DrivingTaskLogger 
{
	private String outputFolder;
	private File outFile;
	private BufferedWriter output;
	private String newLine = System.getProperty("line.separator");
	private String driverName;
	private String drivingTask;


	/**
	 * Creates a new driving task logger and initializes it. A file with the given
	 * parameters driver name, driving task file name and type of progress bar will
	 * be written.
	 * 
	 * @param outputFolder
	 * 			Indicates the folder the log file will be written to. 
	 * 
	 * @param driverName
	 * 			Name of the current driver.
	 * 
	 * @param drivingTask
	 * 			Name of the driving task file.
	 */
	public DrivingTaskLogger(String outputFolder, String driverName, String drivingTask) 
	{
		this.outputFolder = outputFolder;
		this.driverName = driverName;
		this.drivingTask = drivingTask;

		Util.makeDirectory(outputFolder);
		initWriter();
	}
	
	
	/**
	 * Adds a string with time stamp to the output file.
	 * 
	 * @param string
	 * 			String to add.
	 * 
	 * @param timestamp
	 * 			Time stamp for output line.
	 */
	public void reportText(String string, Date timestamp) 
	{
		// format current time stamp
		String timestampString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(timestamp);

		// write data to file
		try {
			output.write(timestampString + " --> " + string + newLine);
			output.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Adds a string to the output file.
	 * 
	 * @param string
	 * 			String to add.
	 */
	public void reportText(String string) 
	{
		// write data to file
		try {
			output.write(string + newLine);
			output.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Adds a reaction time measurement to the output file.
	 * 
	 * @param triggerName
	 * 			Name of the trigger causing the reaction measurement.
	 * 
	 * @param startTime
	 * 			Start time of the measurement.
	 */
	public void reportReactionTime(String triggerName, Calendar startTime) 
	{
		// get time stamp of start time
		long startTimeInMilliseconds = startTime.getTimeInMillis();
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(startTimeInMilliseconds);

		// get passed milliseconds since start time (= reaction time)
		Calendar now = new GregorianCalendar();
		long passedMilliseconds = now.getTimeInMillis() - startTimeInMilliseconds;
		String reactionTime = new SimpleDateFormat("mm:ss.SSS").format(passedMilliseconds);
		
		// write data to file
		try {
			output.write(timestamp + " --> reaction time at '" + triggerName + "': " + reactionTime + newLine);
			output.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Adds changes of the HMI screen to the output file.
	 * 
	 * @param currentPresentationType
	 * 			Presentation type of the current presentation task to be added 
	 * 			to the log file.
	 */
	public void reportPresentationType(int currentPresentationType) 
	{
		String ptString = "";
		
		// lookup string representation of presentation type
		switch (currentPresentationType)
		{
			case 0 : ptString = "before construction site"; break;
			case 1 : ptString = "in construction site"; break;
			case 2 : ptString = "behind construction site"; break;
			case 3 : ptString = "empty screen"; break;
		}
		
		// write data to file
		try {	
			output.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + 
					" --> PresentationTask: " + ptString + newLine);
			output.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Adds speed limit exceeding to the log file.
	 */
	public void reportSpeedLimitExceeded() 
	{
		// write data to file
		try {	
			output.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + 
					" --> exceeded speed limit" + newLine);
			output.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Adds a note to the log file, if the car is getting too slow.
	 */
	public void reportSpeedLimitUnderExceeded() 
	{
		// write data to file
		try {	
			output.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + 
					" --> fall below speed limit" + newLine);
			output.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Adds a note to the log file, if the car drives at normal speed again.
	 */
	public void reportSpeedNormal() 
	{
		// write data to file
		try {	
			output.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + 
					" --> complying with speed limit again" + newLine);
			output.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Adds values for mean (arithmetic average) and standard deviation of 
	 * speed to the log file.
	 * 
	 * @param averageDifference
	 * 			Average speed deviation (positive, if car faster than allowed; 
	 * 			negative, if car slower than allowed).
	 * 
	 * @param standardDeviation
	 * 			Standard deviation of speed.
	 */
	public void reportSpeedDifference(float averageDifference, float standardDeviation) 
	{
		// write data to file
		try {	
			output.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + 
					" --> speed deviation: " + averageDifference + " km/h (average), " +
					standardDeviation + " km/h (standard deviation)" + newLine);
			output.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Closes the file when the simulator has been halted.
	 */
	public void quit() 
	{
		try {
			
			if (output != null)
				output.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Initializes the log file by setting up its file name and some further 
	 * information like: driver's name, name of driving task file, type of 
	 * progress bar and start time.
	 */
	private void initWriter() 
	{
		// create a valid, nonexistent file name
		//String fileName = createFileName();
		File analyzerDataFile = new File(outputFolder + "/drivingTaskLog.txt");
		
		if (analyzerDataFile.getAbsolutePath() == null) 
		{
			System.err.println("Parameter not accepted at method initWriter.");
			return;
		}
		
		outFile = new File(analyzerDataFile.getAbsolutePath());
		
		// write date to file
		try {
			
			String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
			
			output = new BufferedWriter(new FileWriter(outFile));
			output.write("Driver: " + driverName + newLine);
			output.write("Driving Task: " + drivingTask + newLine);
			output.write("Start Time: "	+ timestamp + newLine + newLine);
			output.flush();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
