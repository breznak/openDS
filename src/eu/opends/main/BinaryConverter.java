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

package eu.opends.main;

import java.io.File;
import java.io.IOException;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;


public class BinaryConverter extends SimpleApplication 
{

    public static void main(String[] args) 
    {
    	java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE);
        BinaryConverter app = new BinaryConverter();
        app.start();
        //app.start(Type.Headless);
    }

    @Override
    public void simpleUpdate(float tpf) 
    {
    }

    public void simpleInitApp() 
    {
    	String path = "Scenes/DrivingSchoolTask/drivingSchoolTask.scene";
    	
    	assetManager.registerLocator("assets", FileLocator.class);
        
        //the actual model would be attached to this node
        Spatial model = (Spatial) assetManager.loadModel(path);        
        rootNode.attachChild(model);
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.7f));
        rootNode.addLight(al);
        
        Spatial sky = SkyFactory.createSky(assetManager, SimulationDefaults.skyTexture, EnvMapType.CubeMap);
        rootNode.attachChild(sky);
        
        cam.setLocation(new Vector3f(0,50,0));        
        flyCam.setMoveSpeed(100);
        
	    BinaryExporter exporter = BinaryExporter.getInstance();
	    File file = new File("assets/" + path.replace("scene", "j3o"));
	    
	    System.err.println("finished");
	    
	    try {
	      exporter.save(model, file);
	    } catch (IOException ex) {
	      ex.printStackTrace();
	    }    
	    
    }
}
