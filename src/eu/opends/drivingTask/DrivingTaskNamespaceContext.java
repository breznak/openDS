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

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * 
 * @author Rafael Math
 */
public class DrivingTaskNamespaceContext implements NamespaceContext 
{
    public String getNamespaceURI(String prefix)
    {
        if (prefix.equals("scene"))
            return "http://opends.eu/drivingtask/scene";
        else if (prefix.equals("scenario"))
            return "http://opends.eu/drivingtask/scenario";
        else if (prefix.equals("interaction"))
            return "http://opends.eu/drivingtask/interaction";
        else if (prefix.equals("settings"))
            return "http://opends.eu/drivingtask/settings";
        else if (prefix.equals("task"))
            return "http://opends.eu/drivingtask/task";
        else
            return XMLConstants.NULL_NS_URI;
    }
    
    public String getPrefix(String namespace)
    {
        if (namespace.equals("http://opends.eu/drivingtask/scene"))
            return "scene";
        else if (namespace.equals("http://opends.eu/drivingtask/scenario"))
            return "scenario";
        else if (namespace.equals("http://opends.eu/drivingtask/interaction"))
            return "interaction";
        else if (namespace.equals("http://opends.eu/drivingtask/settings"))
            return "settings";
        else if (namespace.equals("http://opends.eu/drivingtask/task"))
            return "task";
        else
            return null;
    }

	@Override
	public Iterator<?> getPrefixes(String arg0) 
	{
		return null;
	}


}  