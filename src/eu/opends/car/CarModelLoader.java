/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2014 Rafael Math
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

package eu.opends.car;

import java.util.Properties;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;

import eu.opends.main.Simulator;
import eu.opends.tools.Util;

/**
 * 
 * @author Rafael Math
 */
public class CarModelLoader
{
	private CullHint showHeadLightDebugBoxes = CullHint.Always;
		
	private Node carNode;
	public Node getCarNode() {
		return carNode;
	}

	private Vector3f egoCamPos;
	public Vector3f getEgoCamPos() {
		return egoCamPos;
	}

	private Vector3f staticBackCamPos;
	public Vector3f getStaticBackCamPos() {
		return staticBackCamPos;
	}

	private VehicleControl carControl;
	public VehicleControl getCarControl() {
		return carControl;
	}

	private Geometry leftLightSource;
	public Vector3f getLeftLightPosition() {
		return leftLightSource.getWorldTranslation();
	}

	private Geometry leftLightTarget;
	public Vector3f getLeftLightDirection() {
		return leftLightTarget.getWorldTranslation().subtract(getLeftLightPosition());
	}

	private Geometry rightLightSource;
	public Vector3f getRightLightPosition() {
		return rightLightSource.getWorldTranslation();
	}

	private Geometry rightLightTarget;
	public Vector3f getRightLightDirection() {
		return rightLightTarget.getWorldTranslation().subtract(getRightLightPosition());
	}

	
	public CarModelLoader(Simulator sim, String modelPath, float mass)
	{	
        carNode = (Node)sim.getAssetManager().loadModel(modelPath);
        
        // set car's shadow mode
        carNode.setShadowMode(ShadowMode.Cast);        
	    
		// load settings from car properties file
		String propertiesPath = modelPath.replace(".j3o", ".properties");
		propertiesPath = propertiesPath.replace(".scene", ".properties");
		Properties properties = (Properties) sim.getAssetManager().loadAsset(propertiesPath);
		
		// ego camera properties
		egoCamPos = new Vector3f(getVector3f(properties, "egoCamPos"));
		
		// static back camera properties
		staticBackCamPos = new Vector3f(getVector3f(properties, "staticBackCamPos"));
		
		// chassis properties
		Vector3f chassisScale = new Vector3f(getVector3f(properties, "chassisScale"));
		
		// wheel properties
		float wheelRadius = Float.parseFloat(properties.getProperty("wheelRadius"));
		float frictionSlip = Float.parseFloat(properties.getProperty("wheelFrictionSlip"));
		
		// suspension properties
		float stiffness = Float.parseFloat(properties.getProperty("suspensionStiffness"));
		float compValue = Float.parseFloat(properties.getProperty("suspensionCompression"));
		float dampValue = Float.parseFloat(properties.getProperty("suspensionDamping"));
		float suspensionLenght = Float.parseFloat(properties.getProperty("suspensionLenght"));
		
		// wheel position
		float frontAxlePos = Float.parseFloat(properties.getProperty("frontAxlePos"));
		float backAxlePos = Float.parseFloat(properties.getProperty("backAxlePos"));
		float leftWheelsPos = Float.parseFloat(properties.getProperty("leftWheelsPos"));
		float rightWheelsPos = Float.parseFloat(properties.getProperty("rightWheelsPos"));
		float frontAxleHeight = Float.parseFloat(properties.getProperty("frontAxleHeight"));
		float backAxleHeight = Float.parseFloat(properties.getProperty("backAxleHeight"));

        // setup position and direction of head lights
        setupHeadLight(sim, properties);
        
        // setup reference points
        setupReferencePoints();
        
        // get chassis geometry and corresponding node
        Geometry chassis = Util.findGeom(carNode, "Chassis");
        //chassis.getMaterial().setColor("GlowColor", ColorRGBA.Orange);
        Node chassisNode = chassis.getParent();
        
        // scale chassis
        for(Geometry geo : Util.getAllGeometries(chassisNode))
        	geo.setLocalScale(chassisScale);

        // create a collision shape for the largest spatial (= hull) of the chassis
        Spatial largestSpatial = findLargestSpatial(chassisNode);
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(largestSpatial);
        
        // add collision shape to compound collision shape in order to 
        // apply chassis's translation and rotation to collision shape
        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
        Vector3f location = chassis.getWorldTranslation();
        Matrix3f rotation = (new Matrix3f()).set(chassis.getWorldRotation());
        compoundShape.addChildShape(carHull, location , rotation);
        
        // create a vehicle control
        carControl = new VehicleControl(compoundShape, mass);
        carNode.addControl(carControl);

        // set values for suspension
        carControl.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        carControl.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        carControl.setSuspensionStiffness(stiffness);
        carControl.setMaxSuspensionForce(10000);

        /*
        System.out.println("Compression: "+ carControl.getSuspensionCompression());
        System.out.println("Damping: "+ carControl.getSuspensionDamping());
        System.out.println("Stiffness: "+ carControl.getSuspensionStiffness());
        System.out.println("MaxSuspensionForce: "+ carControl.getMaxSuspensionForce());
        */
        
        // create four wheels and add them at their locations
        // note that the car actually goes backwards
        Vector3f wheelDirection = new Vector3f(0, -1, 0);
        Vector3f wheelAxle = new Vector3f(-1, 0, 0);
        
        // add front right wheel
        Geometry geom_wheel_fr = Util.findGeom(carNode, "WheelFrontRight");
        geom_wheel_fr.setLocalScale(wheelRadius*2);
        geom_wheel_fr.center();
        BoundingBox box = (BoundingBox) geom_wheel_fr.getModelBound();
        carControl.addWheel(geom_wheel_fr.getParent(), 
        		box.getCenter().add(rightWheelsPos, frontAxleHeight, frontAxlePos),
                wheelDirection, wheelAxle, suspensionLenght, wheelRadius, true);        

        // add front left wheel
        Geometry geom_wheel_fl = Util.findGeom(carNode, "WheelFrontLeft");
        geom_wheel_fl.setLocalScale(wheelRadius*2);
        geom_wheel_fl.center();
        box = (BoundingBox) geom_wheel_fl.getModelBound();
        carControl.addWheel(geom_wheel_fl.getParent(), 
        		box.getCenter().add(leftWheelsPos, frontAxleHeight, frontAxlePos),
                wheelDirection, wheelAxle, suspensionLenght, wheelRadius, true);

        // add back right wheel
        Geometry geom_wheel_br = Util.findGeom(carNode, "WheelBackRight");
        geom_wheel_br.setLocalScale(wheelRadius*2);
        geom_wheel_br.center();
        box = (BoundingBox) geom_wheel_br.getModelBound();
        VehicleWheel wheel_br = carControl.addWheel(geom_wheel_br.getParent(), 
        		box.getCenter().add(rightWheelsPos, backAxleHeight, backAxlePos),
                wheelDirection, wheelAxle, suspensionLenght, wheelRadius, false);
        wheel_br.setFrictionSlip(frictionSlip); // apply friction slip (likelihood of breakaway)

        // add back left wheel
        Geometry geom_wheel_bl = Util.findGeom(carNode, "WheelBackLeft");
        geom_wheel_bl.setLocalScale(wheelRadius*2);
        geom_wheel_bl.center();
        box = (BoundingBox) geom_wheel_bl.getModelBound();
        VehicleWheel wheel_bl = carControl.addWheel(geom_wheel_bl.getParent(), 
        		box.getCenter().add(leftWheelsPos, backAxleHeight, backAxlePos),
                wheelDirection, wheelAxle, suspensionLenght, wheelRadius, false);
        wheel_bl.setFrictionSlip(frictionSlip); // apply friction slip (likelihood of breakaway)
		
        if(properties.getProperty("thirdAxlePos") != null && properties.getProperty("thirdAxleHeight") != null)
        {
        	float thirdAxlePos = Float.parseFloat(properties.getProperty("thirdAxlePos"));
    		float thirdAxleHeight = Float.parseFloat(properties.getProperty("thirdAxleHeight"));
    		
	        // add back right wheel 2
	        Geometry geom_wheel_br2 = Util.findGeom(carNode, "WheelBackRight2");
	        geom_wheel_br2.setLocalScale(wheelRadius*2);
	        geom_wheel_br2.center();
	        box = (BoundingBox) geom_wheel_br2.getModelBound();
	        VehicleWheel wheel_br2 = carControl.addWheel(geom_wheel_br2.getParent(), 
	        		box.getCenter().add(rightWheelsPos, thirdAxleHeight, thirdAxlePos),
	                wheelDirection, wheelAxle, suspensionLenght, wheelRadius, false);
	        wheel_br2.setFrictionSlip(frictionSlip); // apply friction slip (likelihood of breakaway)
	
	        // add back left wheel 2
	        Geometry geom_wheel_bl2 = Util.findGeom(carNode, "WheelBackLeft2");
	        geom_wheel_bl2.setLocalScale(wheelRadius*2);
	        geom_wheel_bl2.center();
	        box = (BoundingBox) geom_wheel_bl2.getModelBound();
	        VehicleWheel wheel_bl2 = carControl.addWheel(geom_wheel_bl2.getParent(), 
	        		box.getCenter().add(leftWheelsPos, thirdAxleHeight, thirdAxlePos),
	                wheelDirection, wheelAxle, suspensionLenght, wheelRadius, false);
	        wheel_bl2.setFrictionSlip(frictionSlip); // apply friction slip (likelihood of breakaway)
        }
        
        // no longer needed, as FaceCullMode.Off is default setting
        //Util.setFaceCullMode(carNode, FaceCullMode.Off);
	}    


	private Spatial findLargestSpatial(Node chassisNode) 
	{
		// if no child larger than chassisNode available, return chassisNode
		Spatial largestSpatial = chassisNode;
        int vertexCount = 0;
        
        for(Spatial n : chassisNode.getChildren())
        {
        	if(n.getVertexCount() > vertexCount)
        	{
        		largestSpatial = n;
        		vertexCount = n.getVertexCount();
        	}
        }
        
		return largestSpatial;
	}

	
	private void setupHeadLight(Simulator sim, Properties properties) 
	{
		// add node representing position of left head light
		Box leftLightBox = new Box(0.01f, 0.01f, 0.01f);
        leftLightSource = new Geometry("leftLightBox", leftLightBox);
        leftLightSource.setLocalTranslation(getVector3f(properties, "leftHeadlightPos"));
		Material leftMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		leftMaterial.setColor("Color", ColorRGBA.Red);
		leftLightSource.setMaterial(leftMaterial);
		Node leftNode = new Node();
		leftNode.attachChild(leftLightSource);
		leftNode.setCullHint(showHeadLightDebugBoxes);
		carNode.attachChild(leftNode);
		
		// add node representing target position of left head light
        Box leftLightTargetBox = new Box(0.01f, 0.01f, 0.01f);
        leftLightTarget = new Geometry("leftLightTargetBox", leftLightTargetBox);
        leftLightTarget.setLocalTranslation(getVector3f(properties, "leftHeadlightTarget"));
		Material leftTargetMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		leftTargetMaterial.setColor("Color", ColorRGBA.Red);
		leftLightTarget.setMaterial(leftTargetMaterial);
		Node leftTargetNode = new Node();
		leftTargetNode.attachChild(leftLightTarget);
		leftTargetNode.setCullHint(showHeadLightDebugBoxes);
		carNode.attachChild(leftTargetNode);        
        
		// add node representing position of right head light
        Box rightLightBox = new Box(0.01f, 0.01f, 0.01f);
        rightLightSource = new Geometry("rightLightBox", rightLightBox);
        rightLightSource.setLocalTranslation(getVector3f(properties, "rightHeadlightPos"));
		Material rightMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		rightMaterial.setColor("Color", ColorRGBA.Green);
		rightLightSource.setMaterial(rightMaterial);
		Node rightNode = new Node();
		rightNode.attachChild(rightLightSource);
		rightNode.setCullHint(showHeadLightDebugBoxes);
		carNode.attachChild(rightNode);
		
		// add node representing target position of right head light
        Box rightLightTargetBox = new Box(0.01f, 0.01f, 0.01f);
        rightLightTarget = new Geometry("rightLightTargetBox", rightLightTargetBox);
        rightLightTarget.setLocalTranslation(getVector3f(properties, "rightHeadlightTarget"));
		Material rightTargetMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		rightTargetMaterial.setColor("Color", ColorRGBA.Green);
		rightLightTarget.setMaterial(rightTargetMaterial);
		Node rightTargetNode = new Node();
		rightTargetNode.attachChild(rightLightTarget);
		rightTargetNode.setCullHint(showHeadLightDebugBoxes);
		carNode.attachChild(rightTargetNode);
	}
	
	
	private void setupReferencePoints()
	{
		Node leftPoint = new Node("leftPoint");
		leftPoint.setLocalTranslation(-1, 1, 0);
		carNode.attachChild(leftPoint);
		
		Node rightPoint = new Node("rightPoint");
		rightPoint.setLocalTranslation(1, 1, 0);
		carNode.attachChild(rightPoint);
		
		Node frontPoint = new Node("frontPoint");
		frontPoint.setLocalTranslation(0, 1, -2);
		carNode.attachChild(frontPoint);
		
		Node backPoint = new Node("backPoint");
		backPoint.setLocalTranslation(0, 1, 2);
		carNode.attachChild(backPoint);
	}
	
	
	private Vector3f getVector3f(Properties properties, String key)
	{
        float x = Float.parseFloat(properties.getProperty(key + ".x"));
        float y = Float.parseFloat(properties.getProperty(key + ".y"));
        float z = Float.parseFloat(properties.getProperty(key + ".z"));
        return new Vector3f(x,y,z);
	}
	
}
