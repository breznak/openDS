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

package eu.opends.drivingTask;

import java.io.File;

import eu.opends.basics.SimulationBasics;
import eu.opends.drivingTask.interaction.InteractionLoader;
import eu.opends.drivingTask.scenario.ScenarioLoader;
import eu.opends.drivingTask.scene.SceneLoader;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.task.TaskLoader;


/**
 * This class contains a parser for parsing and validating driving 
 * tasks in XML-format. When the constructor is called all settings
 * (general, car and environment settings) from the driving task file 
 * will be written to the global fields which can be accessed with
 * the corresponding getter and setter methods. The dynamic and static
 * map objects will be read from the file when method "getMapObjects()"
 * is called.
 * 
 * @author Rafael Math
 */
public class DrivingTask 
{
	private DrivingTaskDataQuery dtData;
	private String drivingTaskFileName;
	private String drivingTaskPath;
	
	private SceneLoader sceneLoader;	
	public SceneLoader getSceneLoader()
	{
		return sceneLoader;
	}
	
	private ScenarioLoader scenarioLoader;	
	public ScenarioLoader getScenarioLoader()
	{
		return scenarioLoader;
	}
	
	private InteractionLoader interactionLoader;
	public InteractionLoader getInteractionLoader() 
	{
		return interactionLoader;
	}
	
	private SettingsLoader settingsLoader;
	public SettingsLoader getSettingsLoader()
	{
		return settingsLoader;
	}
	
	private TaskLoader taskLoader;
	public TaskLoader getTaskLoader()
	{
		return taskLoader;
	}
	
	
	/**
	 * Creates a DOM-object from the given input file. If the input file 
	 * does not contain a valid XML string, a warning message will be 
	 * returned and the default settings will be applied.
	 * 
	 * @param sim
	 * 			Simulator or Analyzer
	 * 
	 * @param xmlfile
	 * 			XML driving task file to parse
	 */
	public DrivingTask(SimulationBasics sim, File xmlfile) 
	{
		this.drivingTaskFileName = xmlfile.getName();
		this.drivingTaskPath = xmlfile.getPath();
			
		// init Driving Task Data Query
		dtData = new DrivingTaskDataQuery(drivingTaskPath);
		if(!dtData.isValidDrivingTask())
		{
			System.err.println("File is not a valid driving task: " + 
					drivingTaskFileName + "\nExit simulator");
			sim.stop();
		}			
		
		sceneLoader = new SceneLoader(dtData, sim);
		scenarioLoader = new ScenarioLoader(dtData, sim, this);
		settingsLoader = new SettingsLoader(dtData);
		interactionLoader = new InteractionLoader(dtData, sim, settingsLoader);
		taskLoader = new TaskLoader(dtData, this);
	}
	
	
	public static boolean isValidDrivingTask(File xmlfile)
	{
		DrivingTaskDataQuery dtData = new DrivingTaskDataQuery(xmlfile.getPath());
		return dtData.isValidDrivingTask();
	}
	
	
	/**
	 * Return the file name of the driving task.
	 * 
	 * @return
	 * 			File name of the driving task
	 */
	public String getFileName()
	{
		return drivingTaskFileName;
	}
	
	
	public String getPath() 
	{
		return drivingTaskPath;
	}

}