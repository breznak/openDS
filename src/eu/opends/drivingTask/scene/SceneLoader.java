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

package eu.opends.drivingTask.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.audio.AudioNode;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Image;
import com.jme3.ui.Picture;

import eu.opends.basics.MapObject;
import eu.opends.basics.SimulationBasics;
import eu.opends.car.ResetPosition;
import eu.opends.drivingTask.DrivingTaskDataQuery;
import eu.opends.drivingTask.DrivingTaskDataQuery.Layer;
import eu.opends.tools.Util;
import eu.opends.visualization.MovieData;

/**
 * 
 * @author Rafael Math
 */
public class SceneLoader 
{
	private DrivingTaskDataQuery dtData;
	private SimulationBasics sim;
	private AssetManager assetManager;
	private Map<String, Spatial> geometryMap = new HashMap<String, Spatial>();
	private Map<String, Vector3f> pointMap = new HashMap<String, Vector3f>();
	private Map<String, ResetPosition> resetPositionMap = new HashMap<String, ResetPosition>();
	private List<MapObject> mapObjectsList = new ArrayList<MapObject>();
	
	
	public SceneLoader(DrivingTaskDataQuery dtData, SimulationBasics sim) 
	{
		this.dtData = dtData;
		this.sim = sim;
		this.assetManager = sim.getAssetManager();
		assetManager.registerLocator("assets", FileLocator.class);
		getGeometries(new String[]{"box", "sphere", "cylinder", "terrain"});
		getPoints();
		getResetPoints();
		createMapObjects();
	}
	
	
	public Map<String,AudioNode> getAudioNodes()
	{
		Map<String,AudioNode> audioNodeList = new HashMap<String,AudioNode>();
		
		try {
			
			NodeList audioNodes = (NodeList) dtData.xPathQuery(Layer.SCENE, 
					"/scene:scene/scene:sounds/scene:sound", XPathConstants.NODESET);

			for (int k = 1; k <= audioNodes.getLength(); k++) 
			{
				Node currentNode = audioNodes.item(k-1);
				
				// get ID of audio node
				//String audioNodeID = dtData.getValue(Layer.SCENE,
				//		"/scene:scene/scene:sounds/scene:sound["+k+"]/@id", String.class);
				String audioNodeID = currentNode.getAttributes().getNamedItem("id").getNodeValue();
				
				// get URL of audio node
				//String audioNodeURL = dtData.getValue(Layer.SCENE,
				//		"/scene:scene/scene:sounds/scene:sound["+k+"]/@key", String.class);
				String audioNodeURL = currentNode.getAttributes().getNamedItem("key").getNodeValue();
			
				if((audioNodeURL != null) && (!audioNodeURL.equals("")))
				{
					AudioNode audioNode = new AudioNode(assetManager, audioNodeURL);
					
					
					// set positional
					Boolean isPositional = dtData.getValue(Layer.SCENE,
							"/scene:scene/scene:sounds/scene:sound["+k+"]/scene:positional/@value", Boolean.class);
					if(isPositional == null)
						isPositional = false;
					audioNode.setPositional(isPositional);
					if(isPositional)
					{
						Vector3f translation = dtData.getVector3f(Layer.SCENE,
								"/scene:scene/scene:sounds/scene:sound["+k+"]/scene:positional/scene:translation");
						if(translation != null)
							audioNode.setLocalTranslation(new Vector3f(0,0,0));
					}
					
					
					// set directional
					Boolean isDirectional = dtData.getValue(Layer.SCENE,
							"/scene:scene/scene:sounds/scene:sound["+k+"]/scene:directional/@value", Boolean.class);
					if(isDirectional == null)
						isDirectional = false;
					audioNode.setDirectional(isDirectional);
					if(isDirectional)
					{
						Vector3f direction = dtData.getVector3f(Layer.SCENE,
								"/scene:scene/scene:sounds/scene:sound["+k+"]/scene:directional/scene:direction");
						if(direction != null)
							audioNode.setDirection(direction);
						
						Float innerAngle = dtData.getValue(Layer.SCENE,
								"/scene:scene/scene:sounds/scene:sound["+k+"]/scene:directional/scene:innerAngle", 
								Float.class);
						if(innerAngle != null)
							audioNode.setInnerAngle(dtData.degToRad(innerAngle));
						
						Float outerAngle = dtData.getValue(Layer.SCENE,
								"/scene:scene/scene:sounds/scene:sound["+k+"]/scene:directional/scene:outerAngle", 
								Float.class);
						if(outerAngle != null)
							audioNode.setOuterAngle(dtData.degToRad(outerAngle));
					}
					
					
					// set looping
					Boolean isLooping = dtData.getValue(Layer.SCENE,
							"/scene:scene/scene:sounds/scene:sound["+k+"]/scene:loop", Boolean.class);
					if(isLooping == null)
						isLooping = false;
					audioNode.setLooping(isLooping);
					
					
					// set volume
					Float volume = dtData.getValue(Layer.SCENE,
							"/scene:scene/scene:sounds/scene:sound["+k+"]/scene:volume", Float.class);
					if(volume == null)
						volume = 0.5f;
					audioNode.setVolume(volume);
					
					
					// set pitch
					Float pitch = dtData.getValue(Layer.SCENE,
							"/scene:scene/scene:sounds/scene:sound["+k+"]/scene:pitch", Float.class);
					if(pitch == null)
						pitch = 1.0f;
					audioNode.setVolume(pitch);
					
					audioNodeList.put(audioNodeID, audioNode);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return audioNodeList;		
	}
	
	
	public TreeMap<String, MovieData> getMoviesMap() 
	{
		TreeMap<String,MovieData> movieMap = new TreeMap<String,MovieData>();
		
		try {
			
			NodeList movieNodes = (NodeList) dtData.xPathQuery(Layer.SCENE, 
					"/scene:scene/scene:movies/scene:movie", XPathConstants.NODESET);

			for (int k = 1; k <= movieNodes.getLength(); k++) 
			{
				Node currentNode = movieNodes.item(k-1);
				
				// get ID of movie node
				String movieNodeID = currentNode.getAttributes().getNamedItem("id").getNodeValue();
				
				// get URL of movie node
				String movieNodeURL = currentNode.getAttributes().getNamedItem("key").getNodeValue();
			
				if((movieNodeURL != null) && (!movieNodeURL.equals("")))
				{
					// width
					Integer width = dtData.getValue(Layer.SCENE,
							"/scene:scene/scene:movies/scene:movie["+k+"]/scene:width", Integer.class);
					if(width == null)
						width = sim.getSettings().getWidth();
					
					// height
					Integer height = dtData.getValue(Layer.SCENE,
							"/scene:scene/scene:movies/scene:movie["+k+"]/scene:height", Integer.class);
					if(height == null)
						height = sim.getSettings().getHeight();
					
					// zoomingFactor
					Float zoomingFactor = dtData.getValue(Layer.SCENE,
							"/scene:scene/scene:movies/scene:movie["+k+"]/scene:zoomingFactor", Float.class);
					if(zoomingFactor == null)
						zoomingFactor = 2.0f;
					
					movieMap.put(movieNodeID, new MovieData(movieNodeURL, width, height, zoomingFactor));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return movieMap;
	}
	
	
	public TreeMap<String, Picture> getPictures()
	{
		TreeMap<String,Picture> pictureList = new TreeMap<String,Picture>();
		
		try {
			
			NodeList pictureNodes = (NodeList) dtData.xPathQuery(Layer.SCENE, 
					"/scene:scene/scene:pictures/scene:picture", XPathConstants.NODESET);

			for (int k = 1; k <= pictureNodes.getLength(); k++) 
			{
				Node currentNode = pictureNodes.item(k-1);
				
				// get ID of picture node
				//String pictureID = dtData.getValue(Layer.SCENE,
				//		"/scene:scene/scene:pictures/scene:picture["+k+"]/@id", String.class);
				String pictureID = currentNode.getAttributes().getNamedItem("id").getNodeValue();
				
				// get URL of picture node
				//String pictureURL = dtData.getValue(Layer.SCENE,
				//		"/scene:scene/scene:pictures/scene:picture["+k+"]/@key", String.class);
				String pictureURL = currentNode.getAttributes().getNamedItem("key").getNodeValue();
				
				String pictureLevel = currentNode.getAttributes().getNamedItem("level").getNodeValue();
							
				if((pictureURL != null) && (!pictureURL.equals("")))
				{
					Picture picture = new Picture(pictureID);
					
					// set useAlpha
					Boolean useAlpha = dtData.getValue(Layer.SCENE,
							"/scene:scene/scene:pictures/scene:picture["+k+"]/scene:useAlpha", Boolean.class);
					if(useAlpha == null)
						useAlpha = false;
					picture.setImage(sim.getAssetManager(), pictureURL, useAlpha);
					

					// set width
					Integer width = dtData.getValue(Layer.SCENE,
							"/scene:scene/scene:pictures/scene:picture["+k+"]/scene:width", Integer.class);
					if(width == null)
						width = 100;
					picture.setWidth(width);
					
					
					// set height
					Integer height = dtData.getValue(Layer.SCENE,
							"/scene:scene/scene:pictures/scene:picture["+k+"]/scene:height", Integer.class);
					if(height == null)
						height = 100;
					picture.setHeight(height);

					
					// set isVisible
					Boolean isVisible = dtData.getValue(Layer.SCENE,
							"/scene:scene/scene:pictures/scene:picture["+k+"]/scene:visible", Boolean.class);
					if(isVisible == null)
						isVisible = false;
					picture.setCullHint(isVisible?CullHint.Dynamic:CullHint.Always);
					
					
					// set position
					NodeList pictureChildren = currentNode.getChildNodes();
					int vPosition = 10;
					int hPosition = 10;
					
					for (int l = 1; l <= pictureChildren.getLength(); l++) 
					{
						Node currentPictureChild = pictureChildren.item(l-1);
						
						if(currentPictureChild.getNodeName().equals("vPosition"))
						{
							NodeList vPositionChildren = currentPictureChild.getChildNodes();
							for (int m = 1; m <= vPositionChildren.getLength(); m++) 
							{
								Node currentVPositionChild = vPositionChildren.item(m-1);
								int maxHeight = sim.getSettings().getHeight();
								
								if(currentVPositionChild.getNodeName().equals("center"))
								{
									vPosition = (int) ((maxHeight-height)/2.0f);
									//System.out.println("center: " + vPosition);
								}
								else if(currentVPositionChild.getNodeName().equals("fromTop") || 
										currentVPositionChild.getNodeName().equals("fromBottom"))
								{
									float valueFloat = 10;
									
									String value = currentVPositionChild.getAttributes().getNamedItem("value").getNodeValue();
									if(value != null && !value.isEmpty())
										valueFloat = Float.parseFloat(value);
									
									String unit = currentVPositionChild.getAttributes().getNamedItem("unit").getNodeValue();
									
									if((value != null && !value.isEmpty()) && (unit.equals("%") || unit.equals("percent")))
										vPosition = (int)((maxHeight*valueFloat)/100.0f);
									else
										vPosition = ((int) valueFloat);
									
									if(currentVPositionChild.getNodeName().equals("fromTop"))
										vPosition = maxHeight - (vPosition + height);
									
									vPosition = Math.max(Math.min(vPosition,maxHeight),0);
									
									//System.out.println("vpos: " + vPosition);
								}
							}
						}
						else if(currentPictureChild.getNodeName().equals("hPosition"))
						{
							NodeList hPositionChildren = currentPictureChild.getChildNodes();
							for (int m = 1; m <= hPositionChildren.getLength(); m++) 
							{
								Node currentHPositionChild = hPositionChildren.item(m-1);
								int maxWidth = sim.getSettings().getWidth();
																
								if(currentHPositionChild.getNodeName().equals("center"))
								{
									hPosition = (int) ((maxWidth-width)/2.0f);
									//System.out.println("hpos center: " + hPosition);
								}
								else if(currentHPositionChild.getNodeName().equals("fromLeft") || 
										currentHPositionChild.getNodeName().equals("fromRight"))
								{
									float valueFloat = 10;
									
									String value = currentHPositionChild.getAttributes().getNamedItem("value").getNodeValue();
									if(value != null && !value.isEmpty())
										valueFloat = Float.parseFloat(value);
									
									String unit = currentHPositionChild.getAttributes().getNamedItem("unit").getNodeValue();
									
									if((value != null && !value.isEmpty()) && (unit.equals("%") || unit.equals("percent")))
										hPosition = (int)((maxWidth*valueFloat)/100.0f);
									else
										hPosition = ((int) valueFloat);
									
									if(currentHPositionChild.getNodeName().equals("fromRight"))
										hPosition = maxWidth - (hPosition + width);
									
									hPosition = Math.max(Math.min(hPosition,maxWidth),0);
									
									//System.out.println("hpos: " + hPosition);
								}
							}
						}
					}
					
					picture.setPosition(hPosition, vPosition);				
					
					if(pictureList.containsKey(pictureLevel))
					{
						Picture previousPicture = pictureList.get(pictureLevel);
						System.err.println("Caution: Picture '" + previousPicture.getName() + 
								"' will be overwritten by picture '" + picture.getName() + "'. Same level conflict." );
					}
						
					pictureList.put(pictureLevel, picture);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pictureList;		
	}
	

	public List<MapObject> getMapObjects()
	{
		return mapObjectsList;
	}
	
	
	/**
	 * Returns a list of all map objects (dynamic, static spatial objects) which
	 * were defined in the driving task. Those objects will be added to the
	 * objects loaded within the map model. 
	 */
	public void createMapObjects() 
	{	
		// Structure:
		// <scene>
		//     <models>
		//          <model>
		//				<mass>0</mass>
		//				<visible>true</visible>
		//				<collidable>true</collidable>
		//				<scale>
		//					<vector jtype="java_lang_Float" size="3">
		//						<entry>1</entry>
		//						<entry>1</entry>
		//						<entry>1</entry>
		//					</vector>
		//				</scale>
		//
		//				<rotation quaternion="false">
		//					<vector jtype="java_lang_Float" size="3">
		//						<entry>0</entry>
		//						<entry>0</entry>
		//						<entry>0</entry>
		//					</vector>
		//				</rotation>
		//
		//				<translation>
		//					<vector jtype="java_lang_Float" size="3">
		//						<entry>0</entry>
		//						<entry>0</entry>
		//						<entry>0</entry>
		//					</vector>
		//				</translation>

		
		
		try {
			
			NodeList modelNodes = (NodeList) dtData.xPathQuery(Layer.SCENE, 
					"/scene:scene/scene:models/scene:model", XPathConstants.NODESET);

			for (int k = 1; k <= modelNodes.getLength(); k++) 
			{
				Node currentNode = modelNodes.item(k-1);
				
				// get name
				//String name = dtData.getValue(Layer.SCENE, 
				//		"/scene:scene/scene:models/scene:model["+k+"]/@id", String.class);
				String name = currentNode.getAttributes().getNamedItem("id").getNodeValue();
				
				// get spatial model
				Spatial spatial = null;
				//String spatialURL = dtData.getValue(Layer.SCENE, 
				//		"/scene:scene/scene:models/scene:model["+k+"]/@key", String.class);
				String spatialURL = currentNode.getAttributes().getNamedItem("key").getNodeValue();

				if((spatialURL != null) && (!spatialURL.equals("")))
				{
					spatial = assetManager.loadModel(spatialURL);
				}
				else
				{
					//String geometryRef = dtData.getValue(Layer.SCENE, 
					//		"/scene:scene/scene:models/scene:model["+k+"]/@ref", String.class);
					String geometryRef = currentNode.getAttributes().getNamedItem("ref").getNodeValue();

					if((geometryRef != null) && (geometryMap.containsKey(geometryRef)))
					{
						// get pre-defined shape (!!! clone() causes errors with multiple terrains !!!)
						spatial = geometryMap.get(geometryRef).deepClone();
					}
					else
						throw new Exception("No spatial available for model '" + name + "'");
				}
				


				NodeList childnodes = currentNode.getChildNodes();
				
				Float mass = null;
				Boolean visible = null;
				String collisionShape = null;
				Vector3f scale = null;
				Quaternion rotation = null;
				Vector3f translation = null;
				String collisionSound = null;
				
				for (int j = 1; j <= childnodes.getLength(); j++) 
				{
					Node currentChild = childnodes.item(j-1);
					
					// get material + color
					if(currentChild.getNodeName().equals("material"))
					{
						// get material
						//String materialFile = dtData.getValue(Layer.SCENE,
						//		"/scene:scene/scene:models/scene:model["+k+"]/scene:material/@key", String.class);
						
						Node materialNode = currentChild.getAttributes().getNamedItem("key");
						String matInstance = null;
						if(materialNode != null)
							matInstance = materialNode.getNodeValue();

						if(matInstance != null && !matInstance.equalsIgnoreCase(""))
						{
							Material material = assetManager.loadMaterial(matInstance);
							spatial.setMaterial(material);
						}
						else
						{
							NodeList materialChildren = currentChild.getChildNodes();
							for (int l = 1; l <= materialChildren.getLength(); l++) 
							{
								Node currentMaterialChild = materialChildren.item(l-1);
								
								if(currentMaterialChild.getNodeName().equals("color"))
								{
									// get color
									ColorRGBA color = getColorRGBA(currentMaterialChild);
	
									if(color != null && spatial != null)
									{
										String matDefinition = "Common/MatDefs/Misc/Unshaded.j3md";
										Material material = new Material(sim.getAssetManager(), matDefinition);
										material.setColor("Color", color);
										spatial.setMaterial(material);
									}
								}
							}
						}
					}
					
					else if(currentChild.getNodeName().equals("mass"))
					{
						mass = Float.parseFloat(currentChild.getTextContent());
					}
					
					else if(currentChild.getNodeName().equals("visible"))
					{
						visible = Boolean.parseBoolean(currentChild.getTextContent());
					}
					
					else if(currentChild.getNodeName().equals("collisionShape"))
					{
						collisionShape = currentChild.getTextContent();
					}
					
					else if(currentChild.getNodeName().equals("textureScale"))
					{
						Vector2f textureScale = getVector2f(currentChild);
						
						for(Geometry g : Util.getAllGeometries(spatial))
						{
							g.getMesh().scaleTextureCoordinates(textureScale);
							break;
						}
					}
					
					else if(currentChild.getNodeName().equals("scale"))
					{
						scale = getVector3f(currentChild);
					}
					
					else if(currentChild.getNodeName().equals("rotation"))
					{
						rotation = getQuaternion(currentChild);
					}
					
					else if(currentChild.getNodeName().equals("translation"))
					{
						translation = getVector3f(currentChild);
					}
					
					else if(currentChild.getNodeName().equals("collisionSound"))
					{
						collisionSound = currentChild.getAttributes().getNamedItem("ref").getNodeValue();
					}
					
					else if(currentChild.getNodeName().equals("ambientLight"))
					{
						// add ambient light to current spatial
						if(spatial != null)
						{
							NodeList lightnodes = currentChild.getChildNodes();
							for (int z = 1; z <= lightnodes.getLength(); z++) 
							{
								Node lightChild = lightnodes.item(z-1);
								if(lightChild.getNodeName().equals("color"))
								{
									AmbientLight ambientLight = new AmbientLight();
							        ColorRGBA color = getColorRGBA(lightChild);
							        ambientLight.setColor(color);
							        spatial.addLight(ambientLight);
								}
							}
						}
					}
					
					else if(currentChild.getNodeName().equals("shadowMode"))
					{
						// add shadow mode to current spatial
						if(spatial != null)
						{
							ShadowMode shadowMode = getShadowMode(currentChild);
							spatial.setShadowMode(shadowMode);							
						}
					}
				}

				if(mass == null)
					mass = 0f;
				
				if(visible == null)
					visible = true;
				
				if(scale == null)
					scale = new Vector3f(1,1,1);
				
				// build map object
				if((name != null) && (spatial != null) && (translation != null) && (rotation != null) && 
						(collisionShape != null))
				{
					MapObject mapObject = new MapObject(name, spatial, translation, rotation, scale,
							visible, collisionShape, mass, spatialURL, collisionSound);
					mapObjectsList.add(mapObject);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private ShadowMode getShadowMode(Node currentChild) 
	{
		String shadowModeString = currentChild.getTextContent();
		ShadowMode shadowMode = ShadowMode.Off;
		
		if(shadowModeString.equalsIgnoreCase("CastAndReceive"))
			shadowMode = ShadowMode.CastAndReceive;
		else if(shadowModeString.equalsIgnoreCase("Cast"))
			shadowMode = ShadowMode.Cast;
		else if(shadowModeString.equalsIgnoreCase("Receive"))
			shadowMode = ShadowMode.Receive;
		return shadowMode;
	}

	
	/**
	 * Returns a list of all lights. 
	 * 
	 * @return
	 * 			List of lights
	 */
	public List<Light> getLightList() 
	{
		List<Light> lightList = new ArrayList<Light>();

		try {
			
			NodeList lightNodes = (NodeList) dtData.xPathQuery(Layer.SCENE, 
					"/scene:scene/scene:lights", XPathConstants.NODESET);

			for (int i = 1; i <= lightNodes.getLength(); i++) 
			{
				Node lightNode = lightNodes.item(i-1);
				NodeList lightTypeNodes = lightNode.getChildNodes();
				
				for (int k = 1; k <= lightTypeNodes.getLength(); k++) 
				{
					Node lightTypeNode = lightTypeNodes.item(k-1);
					NodeList childnodes = lightTypeNode.getChildNodes();
					
					if(lightTypeNode.getNodeName().equals("directionalLight"))
					{						
						Vector3f direction = null;
						ColorRGBA color = null;
						for (int j = 1; j <= childnodes.getLength(); j++) 
						{
							Node currentChild = childnodes.item(j-1);
								
							if(currentChild.getNodeName().equals("direction"))
								direction = getVector3f(currentChild);
							else if(currentChild.getNodeName().equals("color"))
								color = getColorRGBA(currentChild);
						}
						
						if(direction != null)
						{
							DirectionalLight directionalLight = new DirectionalLight();
					        directionalLight.setDirection(direction.normalizeLocal());
					        
					        if(color != null)
					        	directionalLight.setColor(color);
					        
					        lightList.add(directionalLight);
						}
					}
					
					if(lightTypeNode.getNodeName().equals("pointLight"))
					{
						Vector3f position = null;
						Float radius = null;
						ColorRGBA color = null;
						for (int j = 1; j <= childnodes.getLength(); j++) 
						{
							Node currentChild = childnodes.item(j-1);
								
							if(currentChild.getNodeName().equals("position"))
								position = getVector3f(currentChild);
							else if(currentChild.getNodeName().equals("radius"))
								radius = Float.parseFloat(currentChild.getChildNodes().item(0).getTextContent());
							else if(currentChild.getNodeName().equals("color"))
								color = getColorRGBA(currentChild);
						}
						
						if(position != null)
						{
							PointLight pointLight = new PointLight();
							pointLight.setPosition(position);
								
							if(radius != null)
								pointLight.setRadius(radius);
					        
							if(color!= null)
								pointLight.setColor(color);
							
					        lightList.add(pointLight);
						}
					}
					
					if(lightTypeNode.getNodeName().equals("ambientLight"))
					{						
						ColorRGBA color = null;
						for (int j = 1; j <= childnodes.getLength(); j++) 
						{
							Node currentChild = childnodes.item(j-1);

							if(currentChild.getNodeName().equals("color"))
								color = getColorRGBA(currentChild);
						}
						
						if(color != null)
						{
							AmbientLight ambientLight = new AmbientLight();
					        ambientLight.setColor(color);
					        
					        lightList.add(ambientLight);
						}
					}					
				}
			}

		} catch (Exception e) {
			System.err.println("SceneLoader: Error parsing light information");
			e.printStackTrace();
		}
		
		return lightList;
	}
	
	
	public String getSkyTexture(String defaultValue)
	{
		String skyTexture = dtData.getValue(Layer.SCENE, "/scene:scene/scene:skyTexture", String.class);
		if(skyTexture != null && !skyTexture.isEmpty())
			return skyTexture;
		else
			return defaultValue;
	}

	private Vector3f getVector3f(Node node) 
	{
		ArrayList<Float> array = new ArrayList<Float>();
		
		NodeList childnodes = node.getChildNodes();

		for(int a = 1; a<= childnodes.getLength(); a++)
		{
			Node childNode = childnodes.item(a-1);
			if(childNode.getNodeName().equals("vector"))
			{
				NodeList childChildNodes = childNode.getChildNodes();
				for(int b = 1; b<= childChildNodes.getLength(); b++)
				{
					Node childChildNode = childChildNodes.item(b-1);
					if(childChildNode.getNodeName().equals("entry"))
					{
						array.add(Float.parseFloat(childChildNode.getChildNodes().item(0).getTextContent()));
					}
				}
			}
		}
		
		if(array.size() == 3)
			return new Vector3f(array.get(0),array.get(1),array.get(2));
		else
			return null;
	}
	
	private Vector2f getVector2f(Node node) 
	{
		ArrayList<Float> array = new ArrayList<Float>();
		
		NodeList childnodes = node.getChildNodes();

		for(int a = 1; a<= childnodes.getLength(); a++)
		{
			Node childNode = childnodes.item(a-1);
			if(childNode.getNodeName().equals("vector"))
			{
				NodeList childChildNodes = childNode.getChildNodes();
				for(int b = 1; b<= childChildNodes.getLength(); b++)
				{
					Node childChildNode = childChildNodes.item(b-1);
					if(childChildNode.getNodeName().equals("entry"))
					{
						array.add(Float.parseFloat(childChildNode.getChildNodes().item(0).getTextContent()));
					}
				}
			}
		}
		
		if(array.size() == 2)
			return new Vector2f(array.get(0),array.get(1));
		else
			return null;
	}

	private Quaternion getQuaternion(Node node) 
	{
		ArrayList<Float> array = new ArrayList<Float>();
		
		NodeList childnodes = node.getChildNodes();

		for(int a = 1; a<= childnodes.getLength(); a++)
		{
			Node childNode = childnodes.item(a-1);
			if(childNode.getNodeName().equals("vector"))
			{
				NodeList childChildNodes = childNode.getChildNodes();
				for(int b = 1; b<= childChildNodes.getLength(); b++)
				{
					Node childChildNode = childChildNodes.item(b-1);
					if(childChildNode.getNodeName().equals("entry"))
					{
						array.add(Float.parseFloat(childChildNode.getChildNodes().item(0).getTextContent()));
					}
				}
			}
		}
		
		if(array.size() == 3)
		{
			float yaw = degToRad(array.get(0));
			float roll = degToRad(array.get(1));
			float pitch = degToRad(array.get(2));
			return new Quaternion().fromAngles(yaw, roll, pitch);
		}
		else if(array.size() == 4)
			return new Quaternion(array.get(0),array.get(1),array.get(2),array.get(3));
		else
			return null;
	}
	
	private ColorRGBA getColorRGBA(Node node) 
	{
		ArrayList<Float> array = new ArrayList<Float>();
		
		NodeList childnodes = node.getChildNodes();

		for(int a = 1; a<= childnodes.getLength(); a++)
		{
			Node childNode = childnodes.item(a-1);
			if(childNode.getNodeName().equals("vector"))
			{
				NodeList childChildNodes = childNode.getChildNodes();
				for(int b = 1; b<= childChildNodes.getLength(); b++)
				{
					Node childChildNode = childChildNodes.item(b-1);
					if(childChildNode.getNodeName().equals("entry"))
					{
						array.add(Float.parseFloat(childChildNode.getChildNodes().item(0).getTextContent()));
					}
				}
			}
		}
		
		if(array.size() == 4)
			return new ColorRGBA(array.get(0),array.get(1),array.get(2),array.get(3));
		else
			return null;
	}
	
	public float degToRad(float degree) 
	{
		return degree * (FastMath.PI/180);
	}

	
	public void getGeometries(String[] typeList)
	{
		for(String type : typeList)
		{
			NodeList geometryNodes = (NodeList) dtData.xPathQuery(Layer.SCENE, 
					"/scene:scene/scene:geometries/scene:" + type, XPathConstants.NODESET);
	
			for (int k = 1; k <= geometryNodes.getLength(); k++) 
			{
				Spatial geometry = null;
				String geometryID = dtData.getValue(Layer.SCENE, 
						"/scene:scene/scene:geometries/scene:" + type + "["+k+"]/@id", String.class);
		
				if(geometryID != null)
				{
					String path = "/scene:scene/scene:geometries/scene:" + type + "["+k+"]";
					
					if(type.equals("box"))
						geometry = createBox(path, geometryID);
					else if(type.equals("sphere"))
						geometry = createSphere(path, geometryID);
					else if(type.equals("cylinder"))
						geometry = createCylinder(path, geometryID);
					else if(type.equals("terrain"))
						geometry = createTerrain(path, geometryID);
				}
				
				if(geometry != null)
					geometryMap.put(geometryID,geometry);
			}
		}
	}


	private Geometry createBox(String path, String name) 
	{
		Geometry geometry = null;
		
		Float width = dtData.getValue(Layer.SCENE, path + "/scene:width", Float.class);		
		Float depth = dtData.getValue(Layer.SCENE, path + "/scene:depth", Float.class);		
		Float height = dtData.getValue(Layer.SCENE, path + "/scene:height", Float.class);

		if((width != null) && (depth != null) && (height != null))
		{
			// create new box
			Box box = new Box(width, depth, height);
			geometry = new Geometry(name + "_box", box);
		}
		
		return geometry;
	}
	
	
	private Geometry createSphere(String path, String name) 
	{
		Geometry geometry = null;
		
		Integer axisSamples = dtData.getValue(Layer.SCENE, path + "/scene:samples/@axis", Integer.class);		
		Integer radialSamples = dtData.getValue(Layer.SCENE, path + "/scene:samples/@radial", Integer.class);		
		Float radius = dtData.getValue(Layer.SCENE, path + "/scene:radius", Float.class);

		if((axisSamples != null) && (radialSamples != null) && (radius != null))
		{
			// create new sphere
			Sphere sphere = new Sphere(axisSamples, radialSamples, radius);
			geometry = new Geometry(name + "_sphere", sphere);
		}
		
		return geometry;
	}
	
	
	private Geometry createCylinder(String path, String name) 
	{
		Geometry geometry = null;
	
		Integer axisSamples = dtData.getValue(Layer.SCENE, path + "/scene:samples/@axis", Integer.class);		
		Integer radialSamples = dtData.getValue(Layer.SCENE, path + "/scene:samples/@radial", Integer.class);		
		Float radius = dtData.getValue(Layer.SCENE, path + "/scene:radius", Float.class);
		Float height = dtData.getValue(Layer.SCENE, path + "/scene:height", Float.class);
		Boolean closed = dtData.getValue(Layer.SCENE, path + "/scene:closed", Boolean.class);

		if((axisSamples != null) && (radialSamples != null) && (radius != null) && 
				(height != null) && (closed != null))
		{
			// create new cylinder
			Cylinder cylinder = new Cylinder(axisSamples, radialSamples, radius, height, closed);
			geometry = new Geometry(name + "_cylinder", cylinder);
		}
		
		return geometry;
	}

	
	private Spatial createTerrain(String path, String name) 
	{
		/*
			<terrain id="myTerrain">
	            <imageBasedHeightMap key="Textures/Terrain/splat/mountains512.png" heightScale="0.2" />
	            <smoothing percentage="0.9" radius="1" />
	            <lod patchSize="65" totalSize="513" lodFactor="2.7" />
	        </terrain>
		 */
		
		TerrainQuad terrain = null;
		
		String heightMapImagePath = dtData.getValue(Layer.SCENE, path + "/scene:imageBasedHeightMap/@key", String.class);
		Float heightScale = dtData.getValue(Layer.SCENE, path + "/scene:imageBasedHeightMap/@heightScale", Float.class);
		
		Float smoothPercentage = dtData.getValue(Layer.SCENE, path + "/scene:smoothing/@percentage", Float.class);
		Integer smoothRadius = dtData.getValue(Layer.SCENE, path + "/scene:smoothing/@radius", Integer.class);		
		
		Integer patchSize = dtData.getValue(Layer.SCENE, path + "/scene:lod/@patchSize", Integer.class);		
		Integer totalSize = dtData.getValue(Layer.SCENE, path + "/scene:lod/@totalSize", Integer.class);	
		Float lodFactor = dtData.getValue(Layer.SCENE, path + "/scene:lod/@distanceFactor", Float.class);

		if(heightMapImagePath != null)
		{
			TextureKey textureKey = new TextureKey(heightMapImagePath, false);
			Image heightMapImage = assetManager.loadTexture(textureKey).getImage();
			
			if(heightScale == null)
				heightScale = 0.5f;
			
			if(smoothPercentage == null)
				smoothPercentage = 0.9f;
			
			if(smoothRadius == null)
				smoothRadius = 1;
			
			if(patchSize == null)
				patchSize = 65;
			
			if(totalSize == null)
				totalSize = heightMapImage.getWidth()+1;
			
			if(lodFactor == null)
				lodFactor = 2.7f;
			
			// create heightmap
			AbstractHeightMap heightmap = new ImageBasedHeightMap(heightMapImage, heightScale);
			heightmap.load();
			heightmap.smooth(smoothPercentage, smoothRadius);
			
			// create terrain
			// The tiles will be 65x65, and the total size of the terrain will be 513x513.
			// Optimal terrain patch size is 65 (64x64). The total size is up to you. At 1025 
			// it ran fine for me (200+FPS), however at size=2049, it got really slow. But that 
			// is a jump from 2 million to 8 million triangles...
			terrain = new TerrainQuad(name + "_terrainQuad", patchSize, totalSize, heightmap.getHeightMap());
			TerrainLodControl control = new TerrainLodControl(terrain, sim.getCamera());
			control.setLodCalculator(new DistanceLodCalculator(patchSize, lodFactor) );
			terrain.addControl(control);
		}
		
		return terrain;
	}
	

	public void getPoints()
	{	
		NodeList pointNodes = (NodeList) dtData.xPathQuery(Layer.SCENE, 
				"/scene:scene/scene:geometries/scene:point", XPathConstants.NODESET);

		for (int k = 1; k <= pointNodes.getLength(); k++) 
		{
			String pointID = dtData.getValue(Layer.SCENE, 
					"/scene:scene/scene:geometries/scene:point" + "["+k+"]/@id", String.class);

			if(pointID != null)
				addPoint("/scene:scene/scene:geometries/scene:point" + "["+k+"]");
		}
	}
	
	
	private void addPoint(String path) 
	{
		String id = dtData.getValue(Layer.SCENE, path + "/@id", String.class);
		Vector3f translation = dtData.getVector3f(Layer.SCENE, path + "/scene:translation");

		if((id != null) && (translation != null))
			pointMap.put(id, translation);
	}
	
	
	public void getResetPoints()
	{	
		NodeList pointNodes = (NodeList) dtData.xPathQuery(Layer.SCENE, 
				"/scene:scene/scene:resetPoints/scene:resetPoint", XPathConstants.NODESET);

		for (int k = 1; k <= pointNodes.getLength(); k++) 
		{
			String pointID = dtData.getValue(Layer.SCENE, 
					"/scene:scene/scene:resetPoints/scene:resetPoint" + "["+k+"]/@id", String.class);

			if(pointID != null)
				addResetPoint("/scene:scene/scene:resetPoints/scene:resetPoint" + "["+k+"]");
		}
	}


	private void addResetPoint(String path) 
	{
		String id = dtData.getValue(Layer.SCENE, path + "/@id", String.class);
		Vector3f translation = dtData.getVector3f(Layer.SCENE, path + "/scene:translation");
		Quaternion rotation = dtData.getQuaternion(Layer.SCENE, path + "/scene:rotation");

		if((id != null) && (translation != null) && (rotation != null))
			//Simulator.getResetPositionList().add(new ResetPosition(id, translation, rotation));
			resetPositionMap.put(id, new ResetPosition(id, translation, rotation));
	}


	public float getGravity(float defaultValue)
	{
		Float gravity = dtData.getValue(Layer.SCENE, "/scene:scene/scene:gravity", Float.class);
		if(gravity != null)
			return gravity;
		else
			return defaultValue;
	}


	public Map<String, ResetPosition> getResetPositionMap() 
	{
		return resetPositionMap;
	}


	public Map<String, Vector3f> getPointMap() 
	{
		return pointMap;
	}

}
