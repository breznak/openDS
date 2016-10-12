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

package eu.opends.niftyGui.dropDown;

/**
 * 
 * @author Rafael Math
 */
public class ColorDepthDropDownModel implements Comparable<ColorDepthDropDownModel> 
{
	private int colorDepth;
	private String label;

	
	public ColorDepthDropDownModel(final int colorDepth) 
	{
		this.colorDepth = colorDepth;
		this.label = null;
	}

	
	public ColorDepthDropDownModel(final int colorDepth, final String label) 
	{
		this.colorDepth = colorDepth;
		this.label = label;
	}


	public String getLabel() 
	{
		if(label == null)
			return colorDepth + " bpp";
		else
			return label;
	}

	
	public int getColorDepth()
	{
		return colorDepth;
	}
  
	
	@Override
	public boolean equals(Object object)
	{
		if (object instanceof ColorDepthDropDownModel) 
		{
			ColorDepthDropDownModel that = ((ColorDepthDropDownModel) object);
			return (this.getColorDepth() == that.getColorDepth());
		} else
			return false;
	}


	@Override
	public int compareTo(ColorDepthDropDownModel that) 
	{
		if (this.getColorDepth() < that.getColorDepth())
	        return -1;
		else if (this.getColorDepth() > that.getColorDepth())
	        return 1;
	    else
		    return 0;
	}
}
