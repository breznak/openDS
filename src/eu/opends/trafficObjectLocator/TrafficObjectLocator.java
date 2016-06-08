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

package eu.opends.trafficObjectLocator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import eu.opends.camera.SimulatorCam;
import eu.opends.car.Car;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class TrafficObjectLocator 
{
	private Simulator sim;
	private Car car;
	private List<TrafficObject> trafficObjectList;
    private Spatial trafficObjectSpatial;
    
	private boolean enabled = false;
	private int currentIndex = -1;
	private String currentObjectPath;
	
	
	public TrafficObjectLocator(Simulator sim, Car car)
	{
		
		Boolean enable = Simulator.getSettingsLoader().getSetting(Setting.ObjectLocator_enable, false);
		if(enable != null)
			enabled = enable;
		
		if(enabled)
		{
			this.sim = sim;
			this.car = car;
			
			String fileName = "trafficObjects.txt";
			String tempFileName = Simulator.getSettingsLoader().getSetting(Setting.ObjectLocator_fileName, "");
			if(tempFileName != null && !tempFileName.isEmpty())
				fileName = tempFileName;
			
			
			String drivingtaskFilename = Simulator.getDrivingTask().getFileName();
			String drivingtaskPath = Simulator.getDrivingTask().getPath();
			String trafficObjectListPath = drivingtaskPath.replace(drivingtaskFilename, fileName);
			
			trafficObjectList = loadTrafficObjects(trafficObjectListPath);

			if(!trafficObjectList.isEmpty())
			{
				currentIndex = 0;
				attachObject(trafficObjectList.get(currentIndex));
			}
			else
			{
				System.err.println("No traffic objects available");
				enabled = false;
			}
		}
	}
	

	private List<TrafficObject> loadTrafficObjects(String trafficObjectListPath) 
	{
		List<TrafficObject> trafficObjectList = new ArrayList<TrafficObject>();

		File inFile = new File(trafficObjectListPath);
		if (!inFile.isFile()) {
			System.err.println("File " + inFile.toString() + " could not be found.");
			return trafficObjectList;
		}
		try {
			BufferedReader inputReader = new BufferedReader(new FileReader(inFile));

			String inputLine = inputReader.readLine();
			
			while(inputLine != null)
			{
				String[] splittedLineArray = inputLine.split(";");
				
				String name = splittedLineArray[0].trim();
				String path = splittedLineArray[1].trim();
				
				String[] splittedTranslationArray = splittedLineArray[2].split(",");
				Vector3f translation = new Vector3f(
						Float.parseFloat(splittedTranslationArray[0]),
						Float.parseFloat(splittedTranslationArray[1]), 
						Float.parseFloat(splittedTranslationArray[2]));
				
				String[] splittedRotationArray = splittedLineArray[3].split(",");
				Quaternion rotation = new Quaternion().fromAngles(
						Float.parseFloat(splittedRotationArray[0]) * FastMath.DEG_TO_RAD,
						Float.parseFloat(splittedRotationArray[1]) * FastMath.DEG_TO_RAD, 
						Float.parseFloat(splittedRotationArray[2]) * FastMath.DEG_TO_RAD);
				
				String[] splittedScaleArray = splittedLineArray[4].split(",");
				Vector3f scale = new Vector3f(
						Float.parseFloat(splittedScaleArray[0]),
						Float.parseFloat(splittedScaleArray[1]), 
						Float.parseFloat(splittedScaleArray[2]));
				
				TrafficObject trafficObject = new TrafficObject(name, path, translation, rotation, scale);
				trafficObjectList.add(trafficObject);
				
				inputLine = inputReader.readLine();
			}
			
			inputReader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return trafficObjectList;
	}


	public void rotateThingNode(float degree) 
	{
		if(enabled)
		{
			float[] angles = trafficObjectSpatial.getLocalRotation().toAngles(null);
			float angle = (angles[1] + degree*FastMath.DEG_TO_RAD) % FastMath.TWO_PI;
			trafficObjectSpatial.setLocalRotation(new Quaternion().fromAngles(0, angle, 0));		
		}
	}
	
	
	public void toggleThingNode() 
	{
		if(enabled)
		{
			// detach current traffic object
			if(trafficObjectSpatial != null)
				car.getCarNode().detachChild(trafficObjectSpatial);
			
			// attach next traffic object
			TrafficObject nextTrafficObject = getNextTrafficObject();
			attachObject(nextTrafficObject);
		}
	}
	
	
	private TrafficObject getNextTrafficObject() 
	{
		currentIndex++;
		
		if(currentIndex >= trafficObjectList.size())
			currentIndex = 0;
		
		return trafficObjectList.get(currentIndex);
	}


	public void attachObject(TrafficObject trafficObject) 
	{
		currentObjectPath = trafficObject.getPath();
		
		if(currentObjectPath.equalsIgnoreCase("null") || currentObjectPath.equalsIgnoreCase("trigger"))
			trafficObjectSpatial = makeBox();
		else
			trafficObjectSpatial = sim.getAssetManager().loadModel(currentObjectPath);
		
	    car.getCarNode().attachChild(trafficObjectSpatial);
	    trafficObjectSpatial.setName(trafficObject.getName());
	    trafficObjectSpatial.setLocalTranslation(trafficObject.getTranslation());
	    trafficObjectSpatial.setLocalRotation(trafficObject.getRotation());
	    trafficObjectSpatial.setLocalScale(trafficObject.getScale());
	}


	private Geometry makeBox() 
	{
		Box box = new Box(1f, 1f, 1f);
		Geometry boxGeometry = new Geometry("boxGeometry", box);
		Material boxMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		boxMaterial.setColor("Color", ColorRGBA.Green);
		boxGeometry.setMaterial(boxMaterial);
		return boxGeometry;
	}


	public void placeThingNode() 
	{
		if(enabled)
		{
			String name = trafficObjectSpatial.getName();
			Vector3f position = trafficObjectSpatial.getWorldTranslation();
			float[] angles = trafficObjectSpatial.getWorldRotation().toAngles(null);
			Vector3f scale = trafficObjectSpatial.getWorldScale();
			
			int counter = trafficObjectList.get(currentIndex).incCounter();
			
			
			if(currentObjectPath.equalsIgnoreCase("null"))
			{
				Spatial newThingSpatial = makeBox();
				sim.getSceneNode().attachChild(newThingSpatial);
				newThingSpatial.setName(name + counter);
				newThingSpatial.setLocalTranslation(position);
				newThingSpatial.setLocalRotation(new Quaternion().fromAngles(angles));
				newThingSpatial.setLocalScale(scale);
				
				Simulator.getDrivingTaskLogger().reportText(
					"<wayPoint id=\"" +  name + counter + "\">"
						+"<translation>"
							+"<vector jtype=\"java_lang_Float\" size=\"3\">"
								+"<entry>" + position.getX() + "</entry>"
								+"<entry>" + position.getY() + "</entry>"
								+"<entry>" + position.getZ() + "</entry>"
							+"</vector>"
						+"</translation>"
						+"<speed>50</speed>" 
					+"</wayPoint>");
			}
			else if(currentObjectPath.equalsIgnoreCase("trigger"))
			{
				Spatial newThingSpatial = makeBox();
				sim.getSceneNode().attachChild(newThingSpatial);
				newThingSpatial.setName(name + counter);
				newThingSpatial.setLocalTranslation(position);
				newThingSpatial.setLocalRotation(new Quaternion().fromAngles(angles));
				newThingSpatial.setLocalScale(scale);
				
				Simulator.getDrivingTaskLogger().reportText(
					"<model id=\"" +  name + counter + "\" key=\"\" ref=\"box\">"
						+"<mass>0</mass>"
							
						+"<material>"
							+"<color>"
								+"<vector jtype=\"java_lang_Float\" size=\"4\">"
									+"<entry>0</entry>"
									+"<entry>1</entry>"
									+"<entry>0</entry>"
									+"<entry>1</entry>"
								+"</vector>"
							+"</color>"
						+"</material>"
					
						+"<visible>true</visible>"
						
						+"<collisionShape>none</collisionShape>"
						
						+"<scale>"
							+"<vector jtype=\"java_lang_Float\" size=\"3\">"
								+"<entry>" + scale.getX() + "</entry>"
								+"<entry>" + scale.getY() + "</entry>"
								+"<entry>" + scale.getZ() + "</entry>"
							+"</vector>"
						+"</scale>"
		
						+"<rotation quaternion=\"false\">"
							+"<vector jtype=\"java_lang_Float\" size=\"3\">"
								+"<entry>0</entry>"
								+"<entry>" + angles[1] * FastMath.RAD_TO_DEG + "</entry>"
								+"<entry>0</entry>"
							+"</vector>"
						+"</rotation>"
		
						+"<translation>"
							+"<vector jtype=\"java_lang_Float\" size=\"3\">"
								+"<entry>" + position.getX() + "</entry>"
								+"<entry>" + position.getY() + "</entry>"
								+"<entry>" + position.getZ() + "</entry>"
							+"</vector>"
						+"</translation>"
					+"</model>");
			}
			else
			{
				Spatial newThingSpatial = sim.getAssetManager().loadModel(currentObjectPath);
				sim.getSceneNode().attachChild(newThingSpatial);
				newThingSpatial.setName(name + counter);
				newThingSpatial.setLocalTranslation(position);
				newThingSpatial.setLocalRotation(new Quaternion().fromAngles(angles));
				newThingSpatial.setLocalScale(scale);
				
				Simulator.getDrivingTaskLogger().reportText(
					"<model id=\"" +  name + counter + "\" key=\"" + currentObjectPath + "\" ref=\"\">"
						+"<mass>0</mass>"
						+"<visible>true</visible>"
						+"<collisionShape>meshShape</collisionShape>"
						+"<scale>"
							+"<vector jtype=\"java_lang_Float\" size=\"3\">"
								+"<entry>" + scale.getX() + "</entry>"
								+"<entry>" + scale.getY() + "</entry>"
								+"<entry>" + scale.getZ() + "</entry>"
							+"</vector>"
						+"</scale>"
		
						+"<rotation quaternion=\"false\">"
							+"<vector jtype=\"java_lang_Float\" size=\"3\">"
								+"<entry>0</entry>"
								+"<entry>" + angles[1] * FastMath.RAD_TO_DEG + "</entry>"
								+"<entry>0</entry>"
							+"</vector>"
						+"</rotation>"
		
						+"<translation>"
							+"<vector jtype=\"java_lang_Float\" size=\"3\">"
								+"<entry>" + position.getX() + "</entry>"
								+"<entry>" + position.getY() + "</entry>"
								+"<entry>" + position.getZ() + "</entry>"
							+"</vector>"
						+"</translation>"
					+"</model>");
			}
		}
	}


	public void update() 
	{
		if(enabled)
			((SimulatorCam) sim.getCameraFactory()).setCarVisible(true);
	}
}
