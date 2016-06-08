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

package eu.opends.niftyGui.listBox;

import de.lessvoid.nifty.tools.Color;

/**
 * 
 * @author Rafael Math
 */
public class TextListBoxModel 
{
	  private String label;
	  private String path;
	  private Color color;

	  
	  public TextListBoxModel(final String label, final String path, Color color) 
	  {
	    this.label = label;
	    this.path = path;
	    this.color = color;
	  }

	  
	  public String getLabel() 
	  {
	    return label;
	  }
	  
	  
	  public String getPath() 
	  {
	    return path;
	  }
	  
	  
	  public Color getColor() 
	  {
	    return color;
	  }
}
