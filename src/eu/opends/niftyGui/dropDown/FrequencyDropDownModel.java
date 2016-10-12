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
public class FrequencyDropDownModel implements Comparable<FrequencyDropDownModel> 
{
	private int frequency;
	private String label;

	
	public FrequencyDropDownModel(final int frequency) 
	{
		this.frequency = frequency;
		this.label = null;
	}

	
	public FrequencyDropDownModel(final int frequency, final String label) 
	{
		this.frequency = frequency;
		this.label = label;
	}


	public String getLabel() 
	{
		if(label == null)
			return frequency + " Hz";
		else
			return label;
	}

	
	public int getFrequency()
	{
		return frequency;
	}
  
	
	@Override
	public boolean equals(Object object)
	{
		if (object instanceof FrequencyDropDownModel) 
		{
			FrequencyDropDownModel that = ((FrequencyDropDownModel) object);
			return (this.getFrequency() == that.getFrequency());
		} else
			return false;
	}


	@Override
	public int compareTo(FrequencyDropDownModel that) 
	{
		if (this.getFrequency() < that.getFrequency())
	        return -1;
		else if (this.getFrequency() > that.getFrequency())
	        return 1;
	    else
		    return 0;
	}
}
