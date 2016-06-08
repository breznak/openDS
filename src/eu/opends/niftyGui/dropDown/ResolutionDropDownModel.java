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
public class ResolutionDropDownModel implements Comparable<ResolutionDropDownModel> 
{
	private int width;
	private int height;

	
	public ResolutionDropDownModel(final int width, final int height) 
	{
		this.width = width;
		this.height = height;
	}

	
	public String getLabel() 
	{
		return width + " x " + height;
	}

	
	public int getWidth()
	{
		return width;
	}
	

	public int getHeight()
	{
		return height;
	}
  
	
	@Override
	public boolean equals(Object object)
	{
		if (object instanceof ResolutionDropDownModel) 
		{
			ResolutionDropDownModel that = ((ResolutionDropDownModel) object);
			return ((this.getWidth() == that.getWidth()) && 
					(this.getHeight() == that.getHeight()));
		} else
			return false;
	}


	@Override
	public int compareTo(ResolutionDropDownModel that) 
	{
		if (this.getWidth() < that.getWidth())
	        return -1;
		else if (this.getWidth() > that.getWidth())
	        return 1;
	    else 
	    {
			if (this.getHeight() < that.getHeight())
		        return -1;
			else if (this.getHeight() > that.getHeight())
		        return 1;
		    else 
		    	return 0;
	    }
	}
}
