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
public class SamplesDropDownModel implements Comparable<SamplesDropDownModel> 
{
	private int samples;
	private String label;

	
	public SamplesDropDownModel(final int samples) 
	{
		this.samples = samples;
		this.label = null;
	}

	
	public SamplesDropDownModel(final int samples, final String label) 
	{
		this.samples = samples;
		this.label = label;
	}


	public String getLabel() 
	{
		if(label == null)
			return samples + "x";
		else
			return label;
	}

	
	public int getSamples()
	{
		return samples;
	}
  
	
	@Override
	public boolean equals(Object object)
	{
		if (object instanceof SamplesDropDownModel) 
		{
			SamplesDropDownModel that = ((SamplesDropDownModel) object);
			return (this.getSamples() == that.getSamples());
		} else
			return false;
	}


	@Override
	public int compareTo(SamplesDropDownModel that) 
	{
		if (this.getSamples() < that.getSamples())
	        return -1;
		else if (this.getSamples() > that.getSamples())
	        return 1;
	    else
		    return 0;
	}
}
