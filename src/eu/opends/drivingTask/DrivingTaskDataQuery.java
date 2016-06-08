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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Jama.Matrix;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import eu.opends.traffic.Waypoint;

/**
 *
 * @author Biasutti, Rafael Math
 */
public class DrivingTaskDataQuery 
{
	private static final String sceneSchema = "assets/DrivingTasks/Schema/scene.xsd";
	private static final String scenarioSchema = "assets/DrivingTasks/Schema/scenario.xsd";
	private static final String interactionSchema = "assets/DrivingTasks/Schema/interaction.xsd";
	private static final String settingsSchema = "assets/DrivingTasks/Schema/settings.xsd";
	private static final String taskSchema = "assets/DrivingTasks/Schema/task.xsd";
    private String scenePath;
    private String scenarioPath;
    private String interactionPath;
    private String settingsPath;
    private String taskPath;
    private Document scene;
    private Document scenario;
    private Document interaction;
    private Document settings;
    private Document task;
    private boolean verbose = true;
	private boolean isValid = false;
    
    
    public static enum Layer 
    {
        SETTINGS ("settings"),
        SCENE ("scene"),
        SCENARIO ("scenario"), 
        INTERACTION ("interaction"), 
        TASK ("task");
        
        private String stringRepresentation;
        
        private Layer(String stringRepresentation)
        {
        	this.stringRepresentation = stringRepresentation;
        }
        
        public String toString()
        {
        	return stringRepresentation;
        }
    }
    
    
	public DrivingTaskDataQuery(String pathToPropertiesFile)
    {
		String errorMsg = "File is not a valid driving task: " + pathToPropertiesFile;
		
		try {
			
			// look up path names for scene, scenario, interaction and settings files
			lookupPathNames(pathToPropertiesFile);
	        
	        // validate scene file
			errorMsg = "File '" + pathToPropertiesFile + "'\npoints to an invalid scene file: " + scenePath;
			scene = validateFile(scenePath, sceneSchema);
			
			// validate scenario file
			errorMsg = "File '" + pathToPropertiesFile + "'\npoints to an invalid scenario file: " + scenarioPath;
	        scenario = validateFile(scenarioPath, scenarioSchema);
	        
	        // validate interaction file
	        errorMsg = "File '" + pathToPropertiesFile + "'\npoints to an invalid interaction file: " + interactionPath;
	        interaction = validateFile(interactionPath, interactionSchema);
	        
	        // validate settings file
	        errorMsg = "File '" + pathToPropertiesFile + "'\npoints to an invalid settings file: " + settingsPath;
	        settings = validateFile(settingsPath, settingsSchema);
	        
	        // validate task file
	        if((new File(taskPath)).exists())
	        {
	        	errorMsg = "File '" + pathToPropertiesFile + "'\npoints to an invalid task file: " + taskPath;
	        	task = validateFile(taskPath, taskSchema);
	        }
	        else
	        	task = newEmptyDocument();
	        
	        if((scene != null) && (scenario != null) && (interaction != null) && (settings != null) && (task != null))
	        	isValid = true;
	        
	    } catch (Exception ex) {
	    	System.err.println(errorMsg);
	    	isValid = false;
	    }
    }
	
	
	private Document newEmptyDocument()
	{
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.newDocument();

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
    
	public boolean isValidDrivingTask() 
	{
		return isValid ;
	}
    
	
    /**
	 * @return the scenario
	 */
	public Document getScenario() 
	{
		return scenario;
	}


	/**
	 * @return the scene
	 */
	public Document getScene() 
	{
		return scene;
	}


	/**
	 * @return the settings
	 */
	public Document getSettings() 
	{
		return settings;
	}


	/**
	 * @return the interaction
	 */
	public Document getInteraction() 
	{
		return interaction;
	}
	
	
	/**
	 * @return the task
	 */
	public Document getTask() 
	{
		return task;
	}


	public String getScenePath()
	{
		return scenePath;
	}
	
	
	public String getScenarioPath()
	{
		return scenarioPath;
	}
	
	
	public String getInteractionPath()
	{
		return interactionPath;
	}
	
	
	public String getSettingsPath()
	{
		return settingsPath;
	}
    
	
	public String getTaskPath()
	{
		return taskPath;
	}
	
	
	public Waypoint getWayPoint(Layer layer, String path) 
	{
		String id = getValue(layer, path + "/@id", String.class);
		Vector3f translation = getVector3f(layer, path + "/" + layer + ":translation");
		Float speed = getValue(layer, path + "/" + layer + ":speed", Float.class);
		String trafficLightID = getValue(layer, path + "/" + layer + ":trafficLight", String.class);
		Float headLightIntensity = getValue(layer, path + "/" + layer + ":headLightIntensity", Float.class);
		String turnSignal = getValue(layer, path + "/" + layer + ":turnSignal", String.class);
		Integer waitingTime = getValue(layer, path + "/" + layer + ":waitingTime", Integer.class);

		if((id != null) && (translation != null) && (speed != null))
			return new Waypoint(id, translation, speed, trafficLightID, headLightIntensity, turnSignal, waitingTime);
		
		return null;
	}
	
	
	public <T> List<T> getArray(Layer layer, String path, Class<T> cast) 
	{
		try {
			
			NodeList arrayNodes = (NodeList) xPathQuery(layer, 
					path + "/" + layer + ":vector/" + layer + ":entry", XPathConstants.NODESET);

			List<T> array = new ArrayList<T>(arrayNodes.getLength());
			
			for (int k = 1; k <= arrayNodes.getLength(); k++) 
				array.add(getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry["+k+"]", cast));

			return array;
			
		} catch (Exception e) {
			
			//reportError("array");
			return null;
		}
	}
	
	
	public Vector3f getVector3f(Layer layer, String path) 
	{		
		try {
				
			float x = getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry[1]", Float.class);
			float y = getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry[2]", Float.class);
			float z = getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry[3]", Float.class);
			return new Vector3f(x,y,z);
			
		} catch (Exception e) {
			
			//reportError("vector array");
			return null;
		}
	}
	
	
	public Matrix getMatrix(Layer layer, String path)
	{
		Matrix matrix = new Matrix(4,4);
		
		try {

			for (int i=0; i<=3 ; i++)
			{
				for (int k=0; k<=3; k++)
				{
					Double value = getValue(layer, path + "/" + layer + ":matrix/" 
							+ layer + ":m"+i+""+k, Double.class);
					matrix.set(i, k, value);
				}
			}
			
		} catch (Exception e) {
			
			//reportError("matrix array");
			return null;
		}
		
		return matrix;
	}


	public ColorRGBA getColorRGBA(Layer layer, String path) 
	{
		try {
			
			float r = getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry[1]", Float.class);
			float g = getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry[2]", Float.class);
			float b = getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry[3]", Float.class);
			float a = getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry[4]", Float.class);
			return new ColorRGBA(r,g,b,a);
			
		} catch (Exception e) {
			
			//reportError("color array");
			return null;
		}
	}
	
	
	public Quaternion getQuaternion(Layer layer, String path) 
	{
		Boolean isQuaternion = getValue(layer, path + "/@quaternion", Boolean.class);
		if(isQuaternion == null)
			isQuaternion = false;
				
		if(isQuaternion)
		{
			try {
				
				float x = getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry[1]", Float.class);
				float y = getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry[2]", Float.class);
				float z = getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry[3]", Float.class);
				float w = getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry[4]", Float.class);
				return new Quaternion(x,y,z,w);
				
			} catch (Exception e) {
				
				//reportError("rotation quaternion array");
				return null;
			}
		}
		else
		{
			try {
					
				float yaw = degToRad(getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry[1]", Float.class));
				float roll = degToRad(getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry[2]", Float.class));
				float pitch = degToRad(getValue(layer, path + "/" + layer + ":vector/" + layer + ":entry[3]", Float.class));
				return new Quaternion().fromAngles(yaw, roll, pitch);
				
			} catch (Exception e) {
				
				//reportError("rotation vector array");
				return null;
			}
		}
	}
	
	
	/**
	 * Transforms degree to radian angles
	 * 
	 * @param degree
	 * 			Angle in degree to transform.
	 * 
	 * @return
	 * 			Angle in radians.
	 */
	public float degToRad(float degree) 
	{
		return degree * (FastMath.PI/180);
	}
	
	
	
	public boolean hasChild(Layer layer, String path, String childNode) 
	{
		Node node = (Node) xPathQuery(layer, path + "/" + layer + ":" + childNode, XPathConstants.NODE);
		if((node != null) && (node.getNodeName() != null) && (node.getNodeName().equalsIgnoreCase(childNode)))
			return true;
		
		return false;
	}
	
	
    /**
     * Executes a XPath query to the specified layer file.
     * @param <T> return type
     * @param query the XPath query
     * @param layer the driving task layer DrivingTaskDataQuery.Layer
     * @param cast result will be casted to the class
     * @return the casted XPath query result
     */
    @SuppressWarnings("unchecked")
	public <T> T getValue(Layer layer, String query, Class<T> cast)
    {
        try {

            String stringValue = (String) xPathQuery(layer, query, XPathConstants.STRING);
            
            if (verbose) 
            {
                System.out.println("Query: " + query);
                System.out.println("Result: " + stringValue);
            }
            
            Constructor<T> constructor = cast.getConstructor(String.class);
            Object obj = constructor.newInstance(stringValue);
            return (T) obj;

        } catch (Exception ex) {
            //Logger.getLogger(DrivingTaskDataQuery.class.getName()).log(Level.SEVERE, null, ex);
        	return null;
        }
    }
    
	
	public Object xPathQuery(Layer layer, String query, QName xPathConst) 
    {
        try {

            final XPath xpath = XPathFactory.newInstance().newXPath();
            
            NamespaceContext nsContext = new DrivingTaskNamespaceContext();
            xpath.setNamespaceContext(nsContext);

            if (!query.startsWith("/")) {
                query = "/" + layer.toString() + ":" + layer.toString() + "/" + query;
            }

            final XPathExpression expr = xpath.compile(query);
            Document document = lookUpSource(layer);
            return expr.evaluate(document, xPathConst);

        } catch (XPathExpressionException ex) {
            Logger.getLogger(DrivingTaskDataQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
	
	
	private Document lookUpSource(Layer layer) 
	{
		Document source;
		switch(layer)
		{
			case SCENE : source = scene; break;
			case SCENARIO : source = scenario; break;
			case INTERACTION : source = interaction; break;
			case TASK : source = task; break;
			default : source = settings; break;
		}
		return source;
	}

	
    /**
     * Dynamically reconfigures the query module to access data from a different Carmina Driving Task Project.
     * To reconfigure the module the simulator must supply the path until the folder where all the projects are stored
     * and the drivingtask.properties.xml must be directly inside this folder.
     *
     * @param pathToPropertiesFile this can be a relative path from the simulator's runtime classpath, or it can
     * be also an absolute path
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws InvalidPropertiesFormatException 
     */
    private void lookupPathNames(String pathToPropertiesFile) 
    		throws InvalidPropertiesFormatException, FileNotFoundException, IOException 
    {
        File propertiesFile = new File(pathToPropertiesFile);
        
        if (!propertiesFile.exists()) 
        {
        	System.err.println("File '" + pathToPropertiesFile + "' does not exist");
            return;
        }
        
        Properties properties = new Properties();
        properties.loadFromXML(new FileInputStream(propertiesFile));
        if (!properties.getProperty("verbose").equalsIgnoreCase("true"))
            verbose = false;

        if (verbose)
            System.out.println("Loading settings for Driving Task Data Query");

        String currentDirectory = propertiesFile.getParent();
        scenePath = currentDirectory + File.separator + properties.getProperty("scene");
        scenarioPath = currentDirectory + File.separator + properties.getProperty("scenario");
        interactionPath = currentDirectory + File.separator + properties.getProperty("interaction");
        settingsPath = currentDirectory + File.separator + properties.getProperty("settings");
        taskPath = currentDirectory + File.separator + properties.getProperty("task");

        if (verbose)
            System.out.println("Settings successfully loaded.");
    }
    
    
	private Document validateFile(String filePath, String schemaSource) 
			throws SAXException, ParserConfigurationException, IOException
	{
		File file = new File(filePath);
		
		// create new DocumentBuilderFactory and set validation properties
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		if(!schemaSource.equalsIgnoreCase(""))
		{
			factory.setNamespaceAware(true);
			
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			factory.setSchema(schemaFactory.newSchema(new Source[] {new StreamSource(schemaSource)}));				
		}

		// create new DocumentBuilder and set error handler
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setErrorHandler(new DrivingTaskErrorHandler(file.getName())); 
		
		// parse and validate driving task file
		return builder.parse(file);
	}
	
	
	/**
	 * Prints a warning message to the console, stating: the value of the 
	 * given element is invalid. If possible, the current value will be
	 * looked up.
	 * 
	 * @param element
	 * 			Element which contains an invalid value.
	 * 
	 * @param fileName
	 * 			File where the error occurred
	 */
	public void reportInvalidValueError(String element, String fileName) 
	{
		System.err.println("WARNING: invalid value in " +
				"\"<" + element + ">\" (File: " + fileName + ")");
	}


}
