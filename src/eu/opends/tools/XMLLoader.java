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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;


/**
 * 
 * @author Rafael Math
 */
public class XMLLoader implements AssetLoader
{
	@Override
	public Document load(AssetInfo arg0) throws IOException 
	{
		InputStream inputStream = arg0.openStream();
		BufferedInputStream stream = new BufferedInputStream(inputStream);
		
		// create new DocumentBuilderFactory and set validation properties
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = null;
		
		try {
			// create new DocumentBuilder
			builder = factory.newDocumentBuilder();
			
		} catch (ParserConfigurationException e) {

		}
		
		Document document = null;
		
		try{
			
			// set error handler
			builder.setErrorHandler(new XMLLoaderErrorHandler(arg0.getKey().getName())); 
		
			// parse and validate driving task file
			document = builder.parse(stream);
			
				
		} catch (Exception ex){
			//ex.printStackTrace();
			System.err.println("Error in XMLLoader while parsing: " + arg0.getKey().getName());
			document = builder.newDocument();
		}	

		stream.close();
		inputStream.close();
		
		return document;
	}
}
