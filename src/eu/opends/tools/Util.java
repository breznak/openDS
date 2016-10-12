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

package eu.opends.tools;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;

/**
 * 
 * @author Rafael Math
 */
public class Util 
{
    public static Geometry findGeom(Spatial spatial, String name) 
    {
        if (spatial instanceof Node) 
        {
            Node node = (Node) spatial;
            for (int i = 0; i < node.getQuantity(); i++) 
            {
                Spatial child = node.getChild(i);
                Geometry result = findGeom(child, name);
                if (result != null)
                    return result;
            }
        } else if (spatial instanceof Geometry) 
        {
            if (spatial.getName().startsWith(name))
                return (Geometry) spatial;
        }
        return null;
    }
    
    
    public static Node findNode(Spatial spatial, String name) 
    {
        if (spatial != null && spatial instanceof Node) 
        {
            Node node = (Node) spatial;
            
            if (node.getName() != null && node.getName().startsWith(name))
            	return (Node) spatial;
            
            for (int i = 0; i < node.getQuantity(); i++) 
            {
                Spatial child = node.getChild(i);
                Node result = findNode(child, name);
                if (result != null)
                    return result;
            }
        }
        return null;
    }
    
    
    public static String printTree(Spatial spatial) 
    {
        if (spatial instanceof Node) 
        {
            Node node = (Node) spatial;
            String resultList = "";
            for (int i = 0; i < node.getQuantity(); i++)
            {
                Spatial child = node.getChild(i);
                String result = printTree(child);
                resultList = resultList + result + "  ";
            }
            return resultList;
            
        } else if (spatial instanceof Geometry) 
        {
        	return spatial.getName();
        }
        return null;
    }
    
    
    public static String getPath(Spatial spatial) 
    {
        if (spatial != null) 
        	return getPath(spatial.getParent()) + "/" + spatial.getName();                
        else
        	return "";
    }


    private static List<Geometry> resultListGeometries = new ArrayList<Geometry>();
    
	private static void collectGeometries(Spatial spatial) 
	{
        if (spatial instanceof Node) 
        {
            for (Spatial child: ((Node) spatial).getChildren()) 
                collectGeometries(child);
        } 
        else if (spatial instanceof Geometry) 
        {
        	resultListGeometries.add((Geometry) spatial);
        }
	}
	
	public static List<Geometry> getAllGeometries(Spatial spatial)
	{
		resultListGeometries.clear();
		collectGeometries(spatial);
		return resultListGeometries;
	}
	
	
	/*
	//---------------------------------------------
    private static List<Spatial> resultListSpatials = new ArrayList<Spatial>();
    
	private static void collectSpatials(Spatial spatial) 
	{
		resultListSpatials.add(spatial);
		
        if (spatial instanceof Node) 
        {
            for (Spatial child: ((Node) spatial).getChildren()) 
                collectSpatials(child);
        }
	}
	
	public static List<Spatial> getAllSpatials(Spatial spatial) 
	{
		resultListSpatials.clear();
		collectSpatials(spatial);
		return resultListSpatials;
	}
	//---------------------------------------------
	*/
	
	
	public static void setFaceCullMode(Spatial spatial, FaceCullMode mode)
	{
		List<Geometry> geometryList = getAllGeometries(spatial);
    	for(Geometry geometry : geometryList)
    		geometry.getMaterial().getAdditionalRenderState().setFaceCullMode(mode);
	}
	
	public static void setWireFrame(Spatial spatial, boolean enable)
	{
		List<Geometry> geometryList = getAllGeometries(spatial);
    	for(Geometry geometry : geometryList)
    		geometry.getMaterial().getAdditionalRenderState().setWireframe(enable);
	}
	
	public static void setCullHint(Spatial spatial, CullHint cullHint)
	{
		List<Geometry> geometryList = getAllGeometries(spatial);
    	for(Geometry geometry : geometryList)
    		geometry.setCullHint(cullHint);
	}
    
	public static void open(String fileName)
	{  	  
	  	  try {
	   
	  		File file = new File(fileName);
	  		if (file.exists())
	  		{
	  			if (Desktop.isDesktopSupported()) 
	  			{
	  				Desktop.getDesktop().open(file);
	  			} else 
	  			{
	  				System.err.println("Awt Desktop is not supported!");
	  			}
	   
	  		} else {
	  			System.err.println("File does not exist!");
	  		}
	   
	  	  } catch (Exception ex) {
	  		  
	  		ex.printStackTrace();
	  	  }
	}


	public static String getDateTimeString() 
	{
		Calendar cal = Calendar.getInstance();
		DecimalFormat form = new DecimalFormat("00");
		String str = Integer.toString(cal.get(Calendar.YEAR)) + "_"
				+ form.format(cal.get(Calendar.MONTH) + 1) + "_"
				+ form.format(cal.get(Calendar.DAY_OF_MONTH)) + "-"
				+ form.format(cal.get(Calendar.HOUR_OF_DAY)) + "_"
				+ form.format(cal.get(Calendar.MINUTE)) + "_"
				+ form.format(cal.get(Calendar.SECOND));
		return str;
	}


	/**
	 * Makes the given directory if it not yet exists.
	 *
	 * @param directory
	 * 			Directory to make.
	 */
	public static void makeDirectory(String directory) 
	{
		File dir = new File(directory);
		
		if (!dir.exists())
			dir.mkdir();
		else if (!dir.isDirectory())
			System.err.println("'" + directory + "' exists but is not a directory");		
	}
	
	
	public static boolean isValidFilename(String filename) 
	{
		File f = new File(filename);
		
		try {
			f.getCanonicalPath();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	
	/**
	 * Computes the angle between three given points A, B, and C at point B. 
	 * 
	 * @param pointA
	 * 		3-dimensional point A
	 * 
	 * @param pointB
	 * 		3-dimensional point B where angle is measured
	 * 
	 * @param pointC
	 * 		3-dimensional point C
	 * 
	 * @param is2DSpace
	 * 		Compute angle in 2D-space (y=0 in points A, B, C)
	 * 
	 * @return
	 * 		Angle between vector BA and vector BC
	 */
	public static float getAngleBetweenPoints(Vector3f pointA, Vector3f pointB, Vector3f pointC, boolean is2DSpace) 
	{
		// vector pointing from vehicle's center towards vehicle's front
		Vector3f vectorBA = pointA.subtract(pointB);
		if(is2DSpace)
			vectorBA.setY(0);
		vectorBA.normalizeLocal();
		
		// vector pointing from vehicle's center towards obstacle
		Vector3f vectorBC = pointC.subtract(pointB);
		if(is2DSpace)
			vectorBC.setY(0);
		vectorBC.normalizeLocal();
		
		// angle between both vectors
		return vectorBA.angleBetween(vectorBC);
	}

    
}
